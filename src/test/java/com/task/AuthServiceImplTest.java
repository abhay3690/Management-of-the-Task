package com.task;

import com.task.dto.AuthenticationRequest;
import com.task.dto.AuthenticationResponse;
import com.task.dto.SignUpRequest;
import com.task.dto.UserDto;
import com.task.entity.User;
import com.task.enums.UserRole;
import com.task.repository.UserRepository;
import com.task.service.auth.AuthServiceImpl;
import com.task.service.jwt.UserService;
import com.task.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAnAdminAccount_AdminDoesNotExist_ShouldCreateAdmin() {
        when(userRepository.findByUserRole(UserRole.ADMIN)).thenReturn(Optional.empty());

        authService.createAnAdminAccount();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createAnAdminAccount_AdminExists_ShouldNotCreateAdmin() {
        when(userRepository.findByUserRole(UserRole.ADMIN)).thenReturn(Optional.of(new User()));

        authService.createAnAdminAccount();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signUpUser_ShouldSaveUser() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@test.com");
        signUpRequest.setName("test");
        signUpRequest.setPassword("password");

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setName(signUpRequest.getName());
        user.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        user.setUserRole(UserRole.EMPLOYEE);

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto userDto = authService.signUpUser(signUpRequest);

        assertNotNull(userDto);
        assertEquals(signUpRequest.getEmail(), userDto.getEmail());
        assertEquals(signUpRequest.getName(), userDto.getName());
    }

    @Test
    void hasUserWithEmail_UserExists_ShouldReturnTrue() {
        when(userRepository.findFirstByEmail("test@test.com")).thenReturn(Optional.of(new User()));

        boolean result = authService.hasUserWithEmail("test@test.com");

        assertTrue(result);
    }

    @Test
    void hasUserWithEmail_UserDoesNotExist_ShouldReturnFalse() {
        when(userRepository.findFirstByEmail("test@test.com")).thenReturn(Optional.empty());

        boolean result = authService.hasUserWithEmail("test@test.com");

        assertFalse(result);
    }

    @Test
    void login_ValidCredentials_ShouldReturnAuthenticationResponse() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@test.com");
        authenticationRequest.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setId(1L);
        user.setUserRole(UserRole.EMPLOYEE);



        // Create a mock UserDetailsService
        UserDetailsService userDetailsService = mock(UserDetailsService.class);

        // Set up the mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername(authenticationRequest.getEmail()))
                .thenReturn(userDetails);
        when(userService.userDetailsService())
                .thenReturn(userDetailsService); // Return the mock UserDetailsService
        when(userRepository.findFirstByEmail(authenticationRequest.getEmail()))
                .thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(userDetails)).thenReturn("token");

        // Call the method to test
        AuthenticationResponse response = authService.login(authenticationRequest);

        // Verify the results
        assertNotNull(response);
        assertEquals("token", response.getJwt());
        assertEquals(1L, response.getUserId());
        assertEquals(UserRole.EMPLOYEE, response.getUserRole());
    }

    @Test
    void login_InvalidCredentials_ShouldThrowException() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@test.com");
        authenticationRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Incorrect username or Password"));

        assertThrows(BadCredentialsException.class, () -> authService.login(authenticationRequest));
    }
}
