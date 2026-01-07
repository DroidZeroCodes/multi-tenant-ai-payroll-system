package org.droid.zero.multitenantaipayrollsystem.modules.position.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.position.dto.PositionRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.position.dto.PositionResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.position.service.PositionService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.endpoint.base-url}/positions")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService positionService;

    @GetMapping("/{positionId}")
    @ResponseStatus(OK)
    public ResponseFactory<PositionResponse> findPosition(
            @PathVariable UUID positionId
    ) {
        return ResponseFactory.success(
                "Find One Success",
                positionService.findById(positionId));
    }

    @PostMapping()
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER')")
    public ResponseFactory<PositionResponse> createPosition(
            @Valid @RequestBody PositionRequest positionRequest
    ) {
        return ResponseFactory.created(
                "Create Success",
                positionService.save(positionRequest)
        );
    }

    @PutMapping("/{positionId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER')")
    public ResponseFactory<PositionResponse> updatePosition(
            @PathVariable UUID positionId,
            @Valid @RequestBody PositionRequest positionRequest
    ) {
        return ResponseFactory.success(
                "Update Success",
                positionService.update(positionRequest, positionId)
        );
    }

    @PatchMapping("/{positionId}/status")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER')")
    public ResponseFactory<?> updatePositionStatus(@PathVariable UUID positionId) {
        positionService.togglePositionStatus(positionId);
        return ResponseFactory.success(
                "Update Success",
                null
        );
    }

}