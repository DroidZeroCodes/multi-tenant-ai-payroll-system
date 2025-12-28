package org.droid.zero.multitenantaipayrollsystem.modules.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.user.service.UserService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN') or #userId == @userContext.currentUser.id")
    public ResponseFactory<UserResponse> findUserById(@PathVariable UUID userId) {
        return ResponseFactory.success(
                "Find One Success",
                userService.findById(userId)
        );
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN')")
    public ResponseFactory<UserResponse> addUser(@Valid @RequestBody UserRegistrationRequest newUser) {
        UserResponse savedUser = this.userService.save(newUser);
        return ResponseFactory.created(
                "Create Success",
                savedUser
        );
    }

}