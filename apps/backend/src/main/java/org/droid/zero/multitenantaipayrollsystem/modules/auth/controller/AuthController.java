package org.droid.zero.multitenantaipayrollsystem.modules.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Login", description = "Login to the system")
    @ApiResponse(responseCode = "201", description = "User Info and JSON Web Token")
    @Parameter(in = ParameterIn.HEADER, name = "Basic", description = "Basic authentication")
    @Parameter(in = ParameterIn.HEADER, name = "X-Tenant-Id", description = "Tenant ID")
    @PostMapping("/login")
    @ResponseStatus(CREATED)
    public ResponseFactory<AuthTokenDto> login(HttpServletRequest request) {
        return ResponseFactory.success(
                "User Info and JSON Web Token",
                this.authService.createToken(request)
        );
    }


    @Operation(summary = "Validate Token", description = "Validate the token")
    @ApiResponse(responseCode = "200", description = "Token is valid")
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token")
    @GetMapping("/validate")
    @ResponseStatus(OK)
    public ResponseFactory<AuthTokenDto> validateToken(HttpServletRequest request) {
        return ResponseFactory.success(
                "Token is valid",
                this.authService.validateToken(request)
        );
    }

    @Operation(summary = "Logout", description = "Logout from the system")
    @ApiResponse(responseCode = "200", description = "User has been logged out")
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token")
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