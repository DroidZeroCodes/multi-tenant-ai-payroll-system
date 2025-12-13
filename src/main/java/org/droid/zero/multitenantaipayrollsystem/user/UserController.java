package org.droid.zero.multitenantaipayrollsystem.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseFactory<UserResponse> findUserById(@PathVariable UUID userId) {
        return ResponseFactory.success(
                "Find One Success",
                userService.findById(userId)
        );
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    private ResponseFactory<UserResponse> addUser(@Valid @RequestBody UserRegistrationRequest newUser) {
        UserResponse savedUser = this.userService.save(newUser);
        return ResponseFactory.created(
                "Create Success",
                savedUser
        );
    }

}