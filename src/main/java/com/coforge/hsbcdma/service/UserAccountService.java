package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.dto.AuthResponseDTO;
import com.coforge.hsbcdma.dto.LoginRequestDTO;
import com.coforge.hsbcdma.dto.LoginResponseDTO;
import com.coforge.hsbcdma.dto.RegisterRequestDTO;
import com.coforge.hsbcdma.dto.RolemgmtDTO.GetRoleResponse;
import com.coforge.hsbcdma.dto.UserMananagementDTO.GetAllUsersDTO.CountryRefDTO;
import com.coforge.hsbcdma.dto.UserMananagementDTO.GetAllUsersDTO.NamedRefDTO;
import com.coforge.hsbcdma.dto.UserMananagementDTO.GetAllUsersDTO.RoleRefDTO;
import com.coforge.hsbcdma.dto.UserMananagementDTO.GetUserResponse;
import com.coforge.hsbcdma.dto.UserMananagementDTO.PutUserRequestDTO;
import com.coforge.hsbcdma.dto.UserMananagementDTO.UserListItemDTO;
import com.coforge.hsbcdma.entity.*;
import com.coforge.hsbcdma.entity.UserManagementEntities.Action;
import com.coforge.hsbcdma.entity.UserManagementEntities.ChildModule;
import com.coforge.hsbcdma.entity.UserManagementEntities.Module;
import com.coforge.hsbcdma.exception.InvalidCredentialsException;
import com.coforge.hsbcdma.exception.UserAlreadyExistsException;
import com.coforge.hsbcdma.exception.UserInactiveException;
import com.coforge.hsbcdma.mapper.RoleMapper;
import com.coforge.hsbcdma.mapper.UserMapper;
import com.coforge.hsbcdma.repository.*;
import com.coforge.hsbcdma.repository.UserManagementRepositories.ActionRepository;
import com.coforge.hsbcdma.repository.UserManagementRepositories.ChildModuleRepository;
import com.coforge.hsbcdma.repository.UserManagementRepositories.ModuleRepository;
import com.coforge.hsbcdma.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service layer responsible for managing user account operations.
 * <p>
 * This service handles core user-related business logic including
 * user registration and persistence,
 * password encryption using PasswordEncoder,
 * user authentication via AuthenticationManager,
 * JWT token generation and validation using JwtUtil,
 * email notifications through EmailService.
 * change and forgot password functionality
 * login functionality
 *
 * @author Vandana Pal
 */


@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CountryCodeRepository countryCodeRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ChildModuleRepository childModuleRepository;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private ActionRepository actionRepository;

    // Added by Chetan

    @Autowired private LocationRepository locationRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private SubDepartmentRepository subDepartmentRepository;

    private static final int TOKEN_EXP_MINUTES =5;

    private static final Logger logger = LoggerFactory.getLogger(UserAccountService.class);

    public LoginResponseDTO register(RegisterRequestDTO userRequest) {
        User user = new User();
        user.setUserId(userRequest.getUserId());
        Optional<User> existingUser = repo.findByUserId(userRequest.getUserId());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User already registered with given username: " + userRequest.getUserId());
        }
        // Check if email already exists
        Optional<User> existingUserByEmail = repo.findByEmailId(userRequest.getEmailId());
        if (existingUserByEmail.isPresent()) {
            throw new UserAlreadyExistsException(
                    "Email already registered: " + userRequest.getEmailId());
        }

//        String encode = passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(null);

        user.setEmailId(userRequest.getEmailId());
        user.setPhoneNumber(userRequest.getPhoneNumber());
//        user.setPermissions(userRequest.getPermissions());

        user.setName(userRequest.getName());
        CountryCode country = countryCodeRepository.findById(userRequest.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + userRequest.getCountryId()));
        user.setCountry(country);

        Roles role = rolesRepository.findById(userRequest.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + userRequest.getRoleId()));
        user.setRole(role);
        user.setActive(true);

        String adminUserId = getCurrentAdminUserId();
        user.setCreatedBy(adminUserId);
        user.setUpdatedBy(adminUserId);
//        user.setCreatedAt(LocalDateTime.now());

        String tempPassword = generateTempPassword();

        // Saving encrypted temporary password
        user.setTempPassword(Objects.requireNonNull(passwordEncoder.encode(tempPassword)).substring(0,10));
        user.setFirstLogin(true);

//      Added By chetan
        if (userRequest.getLocationId() != null) {
            Location location = locationRepository.findById(userRequest.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found: " + userRequest.getLocationId()));
            user.setLocation(location);
        }

        if (userRequest.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(userRequest.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found: " + userRequest.getDepartmentId()));
            user.setDepartment(dept);
        }

        if (userRequest.getSubdepartmentId() != null) {
            SubDepartment sub = subDepartmentRepository.findById(userRequest.getSubdepartmentId())
                    .orElseThrow(() -> new RuntimeException("SubDepartment not found: " + userRequest.getSubdepartmentId()));
            user.setSubdepartment(sub);
        }
        User savedUser = repo.save(user);
        logger.info("User created with ID : {}", savedUser.getId());

//        emailService.sendTempPassword(savedUser.getEmailId(), tempPassword);

        logger.info("TEMP PASSWORD: {}", tempPassword);

        LoginResponseDTO dto = new LoginResponseDTO(savedUser.getUserId(),user.getTempPassword());
        return dto;
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequest, HttpServletRequest request) {

        logger.info("Login attempt for userId: {}", loginRequest.getUserId());

        User user = repo.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", loginRequest.getUserId());
                    return new RuntimeException("User not found");
                });

        if (!user.isActive()) {
            throw new DisabledException("Your account is inactive. Please contact the admin.");
        }



        // 🔒 BLOCK PARALLEL LOGIN
//        if (user.getSessionId() != null) {
//            throw new InvalidCredentialsException(
//                    "User is already logged in from another device");
//        }
        LocalDateTime now = LocalDateTime.now();
        // 🔒 BLOCK PARALLEL LOGIN (CORE REQUIREMENT)
//        if (user.getActiveToken() != null &&
//                user.getTokenExpiresAt() != null &&
//                user.getTokenExpiresAt().isAfter(now)) {
//
//            throw new InvalidCredentialsException(
//                    "User is already logged in from another device");
//        }

        // ✅ Handle stale session cleanup
        if (user.getActiveToken() != null && user.getTokenExpiresAt() != null) {

            if (user.getTokenExpiresAt().isBefore(now)) {
                // 🔥 expired session → clean it
                user.setActiveToken(null);
                user.setTokenExpiresAt(null);
                repo.save(user);
            } else {
                //  real parallel login
                throw new InvalidCredentialsException(
                        "User is already logged in from another device");
            }
        }




        //First time login
        if (user.isFirstLogin()) {
            logger.info("First time login detected for userId: {}", user.getUserId());
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getTempPassword())) {
                logger.warn("Invalid temp password attempt for userId: {}", user.getUserId());
                throw new InvalidCredentialsException("Invalid temp password");
            }
            logger.info("First time login successful, password change required for userId: {}", user.getUserId());
            return new AuthResponseDTO("PASSWORD_CHANGE_REQUIRED");
        }


        //1. normal login
        try {
            logger.info("Attempting authentication for userId and password : {} / {}", user.getUserId(),user.getPassword());
            logger.info("***** Matching incoming and stored passwords:***{}::::{}***", loginRequest.getPassword(),user.getPassword());
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserId(),
                            loginRequest.getPassword()
                    )
            );

            // 2. Get username safely from principal
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            logger.info("Authentication successful for userId: {}", user.getUserId());


            // ✅ Create session
//            HttpSession session = request.getSession(true);
//            user.setSessionId(session.getId());
//            repo.save(user);





            // 3. Generate JWT token

// 3. Generate ACCESS token (JWT)
            String accessToken = jwtUtil.generateToken(
                    userDetails.getUsername(),
                    user.getRole().getRole(),
                    user.getRole().getModuleChildModule()
            );


// 4. Generate REFRESH token
            String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());


            logger.info(
                    "Successful Login : Access + Refresh token generated for userId: {}",
                    user.getUserId()
            );


// SINGLE-LOGIN ENFORCEMENT
            user.setActiveToken(accessToken);
            user.setTokenExpiresAt(LocalDateTime.now().plusMinutes(5)); // SAME AS JWT EXPIRY
            repo.save(user);

            // -------- Build role response with moduleName + childName --------
            Roles role = user.getRole();

            // 1) Collect unique moduleIds
            List<Long> moduleIds = role.getModuleChildModule()
                    .stream()
                    .map(mg -> mg.getModuleId())              // moduleId
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            logger.info("*****moduleid********{}",moduleIds.stream().toList());

            // 2) Collect unique childIds
            List<Long> childIds = role.getModuleChildModule()
                    .stream()
                    .flatMap(mg -> mg.getChildModules().stream())
                    .map(cm -> cm.getChildModuleId())                  // childId
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            logger.info("*****childmoduleid********{}",childIds.stream().toList());
            // 3) Fetch module names
            Map<Long, String> moduleIdToName = moduleRepository.findAllById(moduleIds)
                    .stream()
                    .collect(Collectors.toMap(
                            Module::getId,
                            Module::getModule,
                            (a, b) -> a
                    ));

            // 4) Fetch child namee
            Map<Long, String> childIdToName = childModuleRepository.findAllById(childIds)
                    .stream()
                    .collect(Collectors.toMap(
                            ChildModule::getId,
                            ChildModule::getChildModule,
                            (a, b) -> a
                    ));


            List<Long> actionIds = role.getModuleChildModule().stream()
                    .filter(Objects::nonNull)
                    .flatMap(mg -> mg.getChildModules() == null
                            ? Stream.empty()
                            : mg.getChildModules().stream())
                    .filter(Objects::nonNull)
                    .flatMap(cm -> cm.getActionIds() == null
                            ? Stream.empty()
                            : cm.getActionIds().stream())
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            logger.info("*****actionIds******** {}", actionIds);


            Map<Long, String> actionIdToName = actionRepository.findAllById(actionIds)
                    .stream()
                    .collect(Collectors.toMap(
                            Action::getId,
                            Action::getAction,
                            (a, b) -> a
                    ));

            // 5) mapping
            GetRoleResponse roleInfo = RoleMapper.toGetRoleResponse(role, moduleIdToName, childIdToName,actionIdToName);

            logger.info("*********gerroleresponse***{}",roleInfo.toString());

// ✅ RETURN BOTH TOKENS
            return new AuthResponseDTO(
                    user.getUserId(),
                    accessToken,
                    refreshToken,
                    roleInfo,
                    user.getName()
            );
        }
        catch (DisabledException e) { // Added by Chetan
            throw new UserInactiveException("User is inactive. Please contact admin.");
        }
        catch (BadCredentialsException e) {
            logger.warn("Invalid login attempt for userId: {}", loginRequest.getUserId());
            throw new InvalidCredentialsException("Invalid username or password");
        } catch (Exception e) {
            logger.error("Unexpected error during login for userId: {}", loginRequest.getUserId(), e);
            throw e;
        }
    }

    public void forgotPassword(String userId, String emailId) {
        logger.info("Forgot password request received for userId: {}, emailId: {}", userId, emailId);

        User user = repo
                .findByUserIdAndEmailId(userId, emailId)
                .orElseThrow(() -> {
                    logger.warn("Invalid forgot password attempt for userId: {}, emailId: {}", userId, emailId);
                    return new InvalidCredentialsException("Invalid userId or emailId");
                });

        // Generate temporary password
        String tempPassword = generateTempPassword();

        logger.debug("Generated temporary password for userId {}: {}", user.getUserId(), tempPassword);

        user.setTempPassword(passwordEncoder.encode(tempPassword));
        user.setFirstLogin(true); //force password change
        repo.save(user);

        logger.info("Temporary password set and user marked for first login password change for userId: {}", user.getUserId());

        // Send email
//        emailService.sendTempPassword(emailId, tempPassword);
        logger.info("Temporary password email would be sent to: {}", emailId);
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8); // 8-char temp password
    }

    public String changePassword(LoginRequestDTO request) {
        User user = repo.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!request.getTempPassword().equals(user.getTempPassword())) {
            throw new RuntimeException("Passwords don't match");
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setTempPassword(null);
        user.setFirstLogin(false);

        repo.save(user);

        return "Password changed successfully";
    }

    /**
     * LOGOUT: Blacklist the token, so it cannot be reused.
     */
    public String logout(String token, HttpServletRequest request) {
        if (token == null || token.isBlank()) {
            logger.warn("Logout called without token");
            return "Token not provided";
        }

        try {
//            long expiryEpochSeconds = jwtUtil.getExpirationEpochSeconds(token);
//            tokenBlacklistService.blacklistToken(token, expiryEpochSeconds);

            // Extract userId from token
            String userId = jwtUtil.extractUsername(token);

            // Clear activetoken from DB
            repo.findByUserId(userId).ifPresent(user -> {
                user.setActiveToken(null);
                user.setTokenExpiresAt(null);
                repo.save(user);
            });

            long expiry = jwtUtil.getExpirationEpochSeconds(token);
            tokenBlacklistService.blacklistToken(token, expiry);


//            logger.info("Logout successful, token blacklisted until {}", expiryEpochSeconds);
            return "Logout successful";
        } catch (Exception e) {
            logger.warn("Logout failed: invalid token. {}", e.getMessage());
            return "Invalid token";
        }
    }

//    public List<GetUserResponse>getAllUsers(){
//        List<User> allUsers = repo.findAll();
//        return allUsers.stream()
//                .map(UserMapper::toGetUserResponse)
//                .toList();
//    }
//
//    public List<UserListItemDTO> getAllUsersList() {
//        return repo.findAllWithRefs().stream()
//                .map(this::toUserListItemDTO)
//                .toList();
//    }
//
//    @Transactional
//    public void updateUserActiveStatus(String targetUserId, boolean active) {
//        User target = repo.findByUserId(targetUserId)
//                .orElseThrow(() -> new RuntimeException("User not found: " + targetUserId));
//
//        String adminUserId = getCurrentAdminUserId();
//
//        target.setActive(active);
//        target.setUpdatedBy(adminUserId);
//
//        repo.save(target);
//    }
//
//    private String getCurrentAdminUserId() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
//            throw new RuntimeException("Unauthorized: Admin context not found");
//        }
//
//        Object principal = auth.getPrincipal();
//
//        if (principal instanceof UserDetails userDetails) {
//            return userDetails.getUsername(); // should be userId
//        }
//
//        return auth.getName(); // fallback
//    }
//
//    private UserListItemDTO toUserListItemDTO(User u) {
//        return new UserListItemDTO(
//                u.getId(),
//                u.getUserId(),
//                u.getName(),
//                u.getEmailId(),
//                u.getPhoneNumber(),
//                u.isActive(),
//                u.getCreatedAt(),
//                u.getUpdatedAt(),
//                u.getCreatedBy(),
//                u.getUpdatedBy(),
//
//                u.getRole() != null
//                        ? new RoleRefDTO(u.getRole().getId(), u.getRole().getRole())
//                        : null,
//
//                u.getCountry() != null
//                        ? new CountryRefDTO(u.getCountry().getId(),u.getCountry().getName(), u.getCountry().getCallingCode())
//                        : null,
//
//                u.getLocation() != null
//                        ? new NamedRefDTO(u.getLocation().getId(), u.getLocation().getName())
//                        : null,
//
//                u.getDepartment() != null
//                        ? new NamedRefDTO(u.getDepartment().getId(), u.getDepartment().getName())
//                        : null,
//
//                u.getSubdepartment() != null
//                        ? new NamedRefDTO(u.getSubdepartment().getId(), u.getSubdepartment().getName())
//                        : null
//        );
//    }
//
//    @Transactional
//    public UserListItemDTO putUserById(Long id, PutUserRequestDTO dto) {
//
//        User user = repo.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
//
//        // --- userId (editable + unique) ---
//        if (StringUtils.hasText(dto.getUserId())) {
//            String newUserId = dto.getUserId().trim();
//
//            // uniqueness check if changed
//            if (!newUserId.equals(user.getUserId())) {
//                repo.findByUserId(newUserId).ifPresent(existing -> {
//                    if (!existing.getId().equals(user.getId())) {
//                        throw new RuntimeException("userId already exists: " + newUserId);
//                    }
//                });
//            }
//            user.setUserId(newUserId);
//        }
//
//        // --- name ---
//        if (StringUtils.hasText(dto.getName())) {
//            user.setName(dto.getName().trim());
//        }
//
//        // --- email (unique) ---
//        if (StringUtils.hasText(dto.getEmailId())) {
//            String newEmail = dto.getEmailId().trim();
//
//            if (!newEmail.equalsIgnoreCase(user.getEmailId())) {
//                repo.findByEmailId(newEmail).ifPresent(existing -> {
//                    if (!existing.getId().equals(user.getId())) {
//                        throw new RuntimeException("emailId already exists: " + newEmail);
//                    }
//                });
//            }
//            user.setEmailId(newEmail);
//        }
//
//        // --- phone number (validate if provided) ---
//        if (dto.getPhoneNumber() != null) {
//            Long phone = dto.getPhoneNumber();
//            if (phone <= 0) throw new RuntimeException("Phone number must be positive");
//            if (phone < 1000000000L || phone > 9999999999L)
//                throw new RuntimeException("Phone number must be 10 digits");
//            user.setPhoneNumber(phone);
//        }
//
//        // --- role ---
//        if (dto.getRoleId() != null) {
//            Roles role = rolesRepository.findById(dto.getRoleId())
//                    .orElseThrow(() -> new RuntimeException("Invalid roleId: " + dto.getRoleId()));
//            user.setRole(role);
//        }
//
//        // --- country ---
//        if (dto.getCountryId() != null) {
//            CountryCode country = countryCodeRepository.findById(dto.getCountryId())
//                    .orElseThrow(() -> new RuntimeException("Invalid countryId: " + dto.getCountryId()));
//            user.setCountry(country);
//        }
//
//        // --- location (IMPORTANT: null means "don’t update", so only update if not null) ---
//        if (dto.getLocationId() != null) {
//            Location loc = locationRepository.findById(dto.getLocationId())
//                    .orElseThrow(() -> new RuntimeException("Invalid locationId: " + dto.getLocationId()));
//            user.setLocation(loc);
//        }
//
//        // --- department ---
//        Department dept = null;
//        if (dto.getDepartmentId() != null) {
//            dept = departmentRepository.findById(dto.getDepartmentId())
//                    .orElseThrow(() -> new RuntimeException("Invalid departmentId: " + dto.getDepartmentId()));
//            user.setDepartment(dept);
//        } else {
//            // not updating department, keep existing
//            dept = user.getDepartment();
//        }
//
//        // --- subdepartment ---
//        if (dto.getSubdepartmentId() != null) {
//            SubDepartment sub = subDepartmentRepository.findById(dto.getSubdepartmentId())
//                    .orElseThrow(() -> new RuntimeException("Invalid subdepartmentId: " + dto.getSubdepartmentId()));
//
//            // Optional check: if dept exists (either updated or existing), ensure relation matches
//            if (dept != null && sub.getDepartment() != null && !sub.getDepartment().getId().equals(dept.getId())) {
//                throw new RuntimeException("subdepartmentId does not belong to departmentId");
//            }
//
//            user.setSubdepartment(sub);
//        }
//
//        // --- active ---
//        if (dto.getActive() != null) {
//            user.setActive(dto.getActive());
//        }
//
//        // --- audit ---
//        String adminUserId = getCurrentAdminUserId();
//        user.setUpdatedBy(adminUserId);
//
//        repo.save(user);
//
//        // re-fetch with refs so response matches GET (nested objects)
//        User full = repo.findById(user.getId())
//                .orElseThrow(() -> new RuntimeException("User not found after update"));
//
//        return toUserListItemDTO(full);
//    }


    public List<GetUserResponse>getAllUsers(){
        List<User> allUsers = repo.findAll();
        return allUsers.stream()
                .map(UserMapper::toGetUserResponse)
                .toList();
    }

//    public List<UserListItemDTO> getAllUsersList() {
//        return repo.findAllWithRefs().stream()
//                .map(this::toUserListItemDTO)
//                .toList();
//    }

    public List<UserListItemDTO> getAllUsersList() {

        List<User> users = repo.findAllWithRefs();

        // Collect all creator/updater userIds
        Set<String> auditUserIds = users.stream()
                .flatMap(u -> Stream.of(u.getCreatedBy(), u.getUpdatedBy()))
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        // Fetch creator/updater users in one shot
        Map<String, String> userIdToName = repo.findByUserIdIn(auditUserIds).stream()
                .collect(Collectors.toMap(User::getUserId, User::getName, (a,b) -> a));

        // Map to DTO including createdByName/updatedByName
        return users.stream()
                .map(u -> toUserListItemDTO(u, userIdToName))
                .toList();
    }

    @Transactional
    public void updateUserActiveStatus(String targetUserId, boolean active) {
        User target = repo.findByUserId(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found: " + targetUserId));

        String adminUserId = getCurrentAdminUserId();

        target.setActive(active);
        target.setUpdatedBy(adminUserId);

        repo.save(target);
    }

    private String getCurrentAdminUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new RuntimeException("Unauthorized: Admin context not found");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername(); // should be userId
        }

        return auth.getName(); // fallback
    }

    private UserListItemDTO toUserListItemDTO(User u) {
        return toUserListItemDTO(u, Collections.emptyMap());
    }

    private UserListItemDTO toUserListItemDTO(User u, Map<String, String> userIdToName) {

        String createdByName = u.getCreatedBy() != null ? userIdToName.get(u.getCreatedBy()) : null;
        String updatedByName = u.getUpdatedBy() != null ? userIdToName.get(u.getUpdatedBy()) : null;

        return new UserListItemDTO(
                u.getId(),
                u.getUserId(),
                u.getName(),
                u.getEmailId(),
                u.getPhoneNumber(),
                u.isActive(),
                u.getCreatedAt(),
                u.getUpdatedAt(),
                u.getCreatedBy(),
                createdByName,         // ✅
                u.getUpdatedBy(),
                updatedByName,         // ✅

                u.getRole() != null
                        ? new RoleRefDTO(u.getRole().getId(), u.getRole().getRole())
                        : null,

                u.getCountry() != null
                        ? new CountryRefDTO(u.getCountry().getId(), u.getCountry().getName(), u.getCountry().getCallingCode())
                        : null,

                u.getLocation() != null
                        ? new NamedRefDTO(u.getLocation().getId(), u.getLocation().getName())
                        : null,

                u.getDepartment() != null
                        ? new NamedRefDTO(u.getDepartment().getId(), u.getDepartment().getName())
                        : null,

                u.getSubdepartment() != null
                        ? new NamedRefDTO(u.getSubdepartment().getId(), u.getSubdepartment().getName())
                        : null
        );
    }

    @Transactional
    public UserListItemDTO putUserById(Long id, PutUserRequestDTO dto) {

        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // --- userId (editable + unique) ---
        if (StringUtils.hasText(dto.getUserId())) {
            String newUserId = dto.getUserId().trim();

            // uniqueness check if changed
            if (!newUserId.equals(user.getUserId())) {
                repo.findByUserId(newUserId).ifPresent(existing -> {
                    if (!existing.getId().equals(user.getId())) {
                        throw new RuntimeException("userId already exists: " + newUserId);
                    }
                });
            }
            user.setUserId(newUserId);
        }

        // --- name ---
        if (StringUtils.hasText(dto.getName())) {
            user.setName(dto.getName().trim());
        }

        // --- email (unique) ---
        if (StringUtils.hasText(dto.getEmailId())) {
            String newEmail = dto.getEmailId().trim();

            if (!newEmail.equalsIgnoreCase(user.getEmailId())) {
                repo.findByEmailId(newEmail).ifPresent(existing -> {
                    if (!existing.getId().equals(user.getId())) {
                        throw new RuntimeException("emailId already exists: " + newEmail);
                    }
                });
            }
            user.setEmailId(newEmail);
        }

        // --- phone number (validate if provided) ---
        if (dto.getPhoneNumber() != null) {
            Long phone = dto.getPhoneNumber();
            if (phone <= 0) throw new RuntimeException("Phone number must be positive");
            if (phone < 1000000000L || phone > 9999999999L)
                throw new RuntimeException("Phone number must be 10 digits");
            user.setPhoneNumber(phone);
        }

        // --- role ---
        if (dto.getRoleId() != null) {
            Roles role = rolesRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Invalid roleId: " + dto.getRoleId()));
            user.setRole(role);
        }

        // --- country ---
        if (dto.getCountryId() != null) {
            CountryCode country = countryCodeRepository.findById(dto.getCountryId())
                    .orElseThrow(() -> new RuntimeException("Invalid countryId: " + dto.getCountryId()));
            user.setCountry(country);
        }

        // --- location (IMPORTANT: null means "don’t update", so only update if not null) ---
        if (dto.getLocationId() != null) {
            Location loc = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Invalid locationId: " + dto.getLocationId()));
            user.setLocation(loc);
        }

        // --- department ---
        Department dept = null;
        if (dto.getDepartmentId() != null) {
            dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Invalid departmentId: " + dto.getDepartmentId()));
            user.setDepartment(dept);
        } else {
            // not updating department, keep existing
            dept = user.getDepartment();
        }

        // --- subdepartment ---
        if (dto.getSubdepartmentId() != null) {
            SubDepartment sub = subDepartmentRepository.findById(dto.getSubdepartmentId())
                    .orElseThrow(() -> new RuntimeException("Invalid subdepartmentId: " + dto.getSubdepartmentId()));

            // Optional check: if dept exists (either updated or existing), ensure relation matches
            if (dept != null && sub.getDepartment() != null && !sub.getDepartment().getId().equals(dept.getId())) {
                throw new RuntimeException("subdepartmentId does not belong to departmentId");
            }

            user.setSubdepartment(sub);
        }

        // --- active ---
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }

        // --- audit ---
        String adminUserId = getCurrentAdminUserId();
        user.setUpdatedBy(adminUserId);

        repo.save(user);

        // re-fetch with refs so response matches GET (nested objects)
        User full = repo.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found after update"));

        return toUserListItemDTO(full);
    }


    public Map<String, String> refreshToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException("Refresh token missing");
        }

        String userId = jwtUtil.extractUsername(refreshToken);

        User user = repo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ session must still exist
        if (user.getActiveToken() == null) {
            throw new RuntimeException("Session expired");
        }

        String newAccessToken = jwtUtil.generateToken(
                user.getUserId(),
                user.getRole().getRole(),
                user.getRole().getModuleChildModule()
        );

        user.setActiveToken(newAccessToken);
        user.setTokenExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXP_MINUTES));
        repo.save(user);

        return Map.of("token", newAccessToken);
    }


}
 