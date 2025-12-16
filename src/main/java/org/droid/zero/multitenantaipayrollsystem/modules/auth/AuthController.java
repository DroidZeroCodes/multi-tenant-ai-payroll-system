package org.droid.zero.multitenantaipayrollsystem.modules.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.AuthTokenDto;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.security.core.Authentication;
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
    public ResponseFactory<AuthTokenDto> login(Authentication authentication) {
        return ResponseFactory.success(
                "User Info and JSON Web Token",
                this.authService.createToken(authentication)
        );
    }

    @DeleteMapping("/logout")
    @ResponseStatus(OK)
    public ResponseFactory<AuthTokenDto> logout(HttpServletRequest request, Authentication authentication) {
        this.authService.invalidateToken(request, authentication);
        return ResponseFactory.success(
                "User has been logged out",
                null
        );
    }
}