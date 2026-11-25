package com.coforge.hsbcdma.logs;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.zip.GZIPOutputStream;

@Component
public class LogArchiver {

    private static final String LOG_DIR = "logs/";
    private static final String ARCHIVE_DIR = "logs/archive/";

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    public void archiveOldLogs() {

        try {
            Files.createDirectories(Paths.get(ARCHIVE_DIR));

            File folder = new File(LOG_DIR);
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".log"));

            if (files == null) return;

            for (File file : files) {

                LocalDate fileDate = Files.getLastModifiedTime(file.toPath())
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                // ✅ Check if older than 90 days
                if (fileDate.isBefore(LocalDate.now().minusDays(90))) {

                    Path zipPath = Paths.get(ARCHIVE_DIR, file.getName() + ".gz");

                    try (
                            FileInputStream fis = new FileInputStream(file);
                            FileOutputStream fos = new FileOutputStream(zipPath.toFile());
                            GZIPOutputStream gzip = new GZIPOutputStream(fos)
                    ) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            gzip.write(buffer, 0, len);
                        }
                    }

                    // ✅ Delete original after zipping
                    file.delete();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}