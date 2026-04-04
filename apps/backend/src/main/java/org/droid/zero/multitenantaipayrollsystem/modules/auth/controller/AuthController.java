package org.droid.zero.multitenantaipayrollsystem.modules.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.AuthTokenDto;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.service.AuthService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.endpoint.base-url}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(CREATED)
    public ResponseFactory<AuthTokenDto> login(HttpServletRequest request) {
        return ResponseFactory.success(
                "User Info and JSON Web Token",
                this.authService.createToken(request)
        );
    }

    @DeleteMapping("/logout")
    @ResponseStatus(OK)
    public ResponseFactory<AuthTokenDto> logout(HttpServletRequest request) {
        this.authService.invalidateToken(request);
        return ResponseFactory.success(
                "User has been logged out",
                null
        );
    }
}