package org.droid.zero.multitenantaipayrollsystem.security.auth;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.AuthTokenDto;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("${api.endpoint.base-url}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(CREATED)
    public ResponseFactory<AuthTokenDto> getLoginInfo(Authentication authentication) {
        return ResponseFactory.success(
                "User Info and JSON Web Token",
                this.authService.createToken(authentication)
        );
    }
}