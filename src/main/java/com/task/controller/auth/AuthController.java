package com.task.controller.auth;

import com.task.dto.*;
import com.task.payload.APIResponse;
import com.task.repository.UserRepository;
import com.task.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final UserRepository userRepository;






//    @PostMapping("/signup")
//    public ResponseEntity<APIResponse<UserDto>> signUpUser(@RequestBody SignUpRequest signUpRequest) {
//        if (authService.hasUserWithEmail(signUpRequest.getEmail())) {
//            APIResponse<UserDto> response = APIResponse.<UserDto>builder()
//                    .data(null)
//                    .status(HttpStatus.NOT_ACCEPTABLE)
//                    .message("User already exists with this email")
//                    .build();
//            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
//        }
//
//        UserDto createdUserDto = authService.signUpUser(signUpRequest);
//        if (createdUserDto == null) {
//            APIResponse<UserDto> response =
//                    APIResponse.<UserDto>builder()
//                    .data(null)
//                    .status(HttpStatus.BAD_REQUEST)
//                    .message("User not created")
//                    .build();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//
//        APIResponse<UserDto> response = APIResponse.<UserDto>builder()
//                .data(createdUserDto)
//                .message(null)
//                .status(HttpStatus.CREATED)
//                .build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<UserDto>> signUpUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (authService.hasUserWithEmail(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new APIResponse<>(null,"User already exists with this email",HttpStatus.NOT_ACCEPTABLE));
        }
        UserDto createdUserDto = authService.signUpUser(signUpRequest);
        if (createdUserDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse<>(null,"User not created",HttpStatus.BAD_REQUEST));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(createdUserDto,null,HttpStatus.CREATED));
    }


    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRequest) {
        return authService.login(authenticationRequest);
    }
}
