package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.*;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import com.coforge.hsbcdma.service.RolePermissionService;
import com.coforge.hsbcdma.service.UserAccountService;
import com.coforge.hsbcdma.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;

/**
 * This is a User controller which handles all requests for registering new user and has all end-points required for login.
 * @author Vandana Pal
 */

@RestController
@RequestMapping(path = "/auth_user/")
public class UserAccountController extends BaseController {
    @Autowired
    UserAccountService userService;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserAccountRepository repo;

//    private static final int TOKEN_EXP_MINUTES = 5;


    @Autowired
    RolePermissionService rolePermissionService;

    @GetMapping("/register")
    public ResponseEntity<?> getRolesAndPermissions(){
        return success(rolePermissionService.getRolesAndPermissions());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO userRequest){
        LoginResponseDTO user = userService.register(userRequest);
//        return ResponseEntity.status(HttpStatus.CREATED).body(user);
        return create(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest,HttpServletRequest request){
        AuthResponseDTO responseDTO = userService.login(loginRequest,request);
//        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        if(responseDTO.getStatus().equals("SUCCESS"))
            return success("Login successful", responseDTO);
        else
            return success("Password Change Required", responseDTO);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody LoginRequestDTO request) {
        String pwdChange = userService.changePassword(request);
//        return ResponseEntity.ok(pwdChange);
        return success(pwdChange);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request){

        userService.forgotPassword(
                request.getUserId(),
                request.getEmailId()
        );

        return ResponseEntity.ok(
                Map.of("message", "Temporary password has been sent to your email")
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        String header = request.getHeader("Authorization");
        String token = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        String msg = userService.logout(token,request);
        return success(msg);
    }


    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(
                userService.refreshToken(body.get("refreshToken"))
        );
    }
}
