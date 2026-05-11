package com.coforge.hsbcdma.service.ProfileServices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class ProfileStorageService {

    private final Path baseDir;

    public ProfileStorageService(@Value("${app.jd.storage-path}") String baseDir) {
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
        createDirIfNotExists(this.baseDir);
    }

    // baseDir/profiles/profile-<id>/
    public String storeProfileFile(Long profileId, MultipartFile file) {

        validateUpload(file);

        Path dir = baseDir.resolve("profiles")
                .resolve("profile-" + profileId)
                .normalize();

        ensureInsideBaseDir(dir);
        createDirIfNotExists(dir);

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("profile.txt");
        String base = sanitizeBaseName(stripExtension(original));
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String ext = getSafeDotExtension(original);

        // profile-12_resume_20260209_123045.pdf
        String finalName = "profile-" + profileId + "_" + base + "_" + ts + ext;

        Path target = dir.resolve(finalName).normalize();
        ensureInsideBaseDir(target);

        try {
            Path tmp = dir.resolve(finalName + ".tmp");
            Files.copy(file.getInputStream(), tmp, StandardCopyOption.REPLACE_EXISTING);
            Files.move(tmp, target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
            return finalName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store profile file", e);
        }
    }

    // ----------------------------
    // Same helper methods as demands
    // ----------------------------
    private String stripExtension(String filename) {
        String name = filename.trim();
        int dot = name.lastIndexOf('.');
        return (dot > 0) ? name.substring(0, dot) : name;
    }

    private String sanitizeBaseName(String base) {
        String cleaned = base.replaceAll("[^a-zA-Z0-9_-]", "_");
        return cleaned.isBlank() ? "profile" : cleaned;
    }

    private void createDirIfNotExists(Path dir) {
        try { Files.createDirectories(dir); }
        catch (IOException e) { throw new RuntimeException("Failed to create directory: " + dir, e); }
    }

    private void ensureInsideBaseDir(Path path) {
        Path normalized = path.toAbsolutePath().normalize();
        if (!normalized.startsWith(baseDir)) {
            throw new IllegalArgumentException("Invalid path outside storage base dir: " + normalized);
        }
    }

    private void validateUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File too large. Max allowed: " + MAX_FILE_SIZE_BYTES + " bytes");
        }

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("");
        String ext = getExtension(original);

        if (ext.isBlank() || !ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Unsupported file extension: " + ext +
                    ". Allowed: " + ALLOWED_EXT);
        }

        String mime = Optional.ofNullable(file.getContentType()).orElse("").toLowerCase(Locale.ROOT);

        if (!mime.isBlank() && !ALLOWED_MIME.contains(mime)) {
            // tolerate octet-stream if ext is allowed
            if (!"application/octet-stream".equals(mime)) {
                throw new IllegalArgumentException("Unsupported content type: " + mime +
                        ". Allowed: " + ALLOWED_MIME);
            }
        }

        if (!isSignatureValidFor(ext, file)) {
            throw new IllegalArgumentException("File signature does not match extension: " + ext);
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        String name = filename.trim();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return "";
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private String getSafeDotExtension(String filename) {
        String ext = getExtension(filename);
        if (ext.isBlank() || !ALLOWED_EXT.contains(ext)) return ".txt";
        return "." + ext;
    }

    private boolean isSignatureValidFor(String ext, MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] header = in.readNBytes(12);

            if (ext.equals("txt")) return true;

            if (ext.equals("rtf")) {
                String s = new String(header, StandardCharsets.US_ASCII);
                return s.startsWith("{\\rtf");
            }

            if (ext.equals("pdf")) {
                String s = new String(header, StandardCharsets.US_ASCII);
                return s.startsWith("%PDF");
            }

            if (ext.equals("jpg") || ext.equals("jpeg")) {
                return header.length >= 3 &&
                        (header[0] & 0xFF) == 0xFF &&
                        (header[1] & 0xFF) == 0xD8 &&
                        (header[2] & 0xFF) == 0xFF;
            }

            if (ext.equals("png")) {
                byte[] sig = new byte[]{(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
                if (header.length < sig.length) return false;
                for (int i = 0; i < sig.length; i++) {
                    if (header[i] != sig[i]) return false;
                }
                return true;
            }

            // OLE2: doc/ppt
            if (ext.equals("doc") || ext.equals("ppt")) {
                byte[] ole = new byte[]{
                        (byte)0xD0,(byte)0xCF,0x11,(byte)0xE0,(byte)0xA1,(byte)0xB1,0x1A,(byte)0xE1
                };
                if (header.length < ole.length) return false;
                for (int i = 0; i < ole.length; i++) {
                    if (header[i] != ole[i]) return false;
                }
                return true;
            }

            // OOXML: docx/xlsx/pptx => zip => 'PK'
            if (ext.equals("docx") || ext.equals("xlsx") || ext.equals("pptx")) {
                return header.length >= 2 && header[0] == 'P' && header[1] == 'K';
            }

            return true;

        } catch (IOException e) {
            throw new RuntimeException("Could not validate file signature", e);
        }
    }

    private static final Set<String> ALLOWED_EXT = Set.of(
            "txt", "pdf", "doc", "docx", "rtf",
            "jpg", "jpeg", "png",
            "xlsx", "ppt", "pptx"
    );

    private static final Set<String> ALLOWED_MIME = Set.of(
            "text/plain",
            "application/pdf",
            "application/msword",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/rtf",
            "image/jpeg",
            "image/png"
    );

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024;
}
