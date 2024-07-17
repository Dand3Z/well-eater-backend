package pl.well_eater.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.well_eater.security.dto.LogInRequest;
import pl.well_eater.security.dto.SingUpRequest;
import pl.well_eater.security.model.UserEntity;
import pl.well_eater.security.service.JwtService;
import pl.well_eater.security.service.UserService;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@Valid @RequestBody SingUpRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            log.error("Username {} already exists. Cannot to make new user with that username", request.getUsername());
            return ResponseEntity.badRequest().body("Username is already in use");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        UserEntity result = userService.signUpUser(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LogInRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            String token = jwtService.generateToken(request.getUsername());
            return ResponseEntity.ok(token);

        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user {}", request.getUsername());
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }

}
