package org.droid.zero.multitenantaipayrollsystem.user;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserRequest;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserResponse;
import org.droid.zero.multitenantaipayrollsystem.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.USER;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse findById(UUID userId) {
        //Find the User by its ID then map it to the response object if exists, if not, throw an exception
        return this.userRepository.findById(userId)
                .map(userMapper::toResponse)
                .orElseThrow(()-> new ObjectNotFoundException(USER, userId));
    }

    @Override
    public UserResponse save(UserRequest request) {
        //Validate if the provided arguments does not violate unique constraints
        new FieldDuplicateValidator()
                .addField(userRepository.existsByEmailIgnoreCaseAndTenantId(request.email(), request.tenantId()), "email")
                .validate(USER);

        //Convert request to an entity to be persisted
        User user = userMapper.toEntity(request);

        //Create the new record in the database
        User savedUser = this.userRepository.save(user);

        //Map the saved model to a response object, then return
        return userMapper.toResponse(savedUser);
    }
}
