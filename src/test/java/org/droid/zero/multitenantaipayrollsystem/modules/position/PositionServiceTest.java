package org.droid.zero.multitenantaipayrollsystem.modules.position;

import org.droid.zero.multitenantaipayrollsystem.modules.position.dto.PositionRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.position.dto.PositionResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.position.mapper.PositionMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.position.model.Position;
import org.droid.zero.multitenantaipayrollsystem.modules.position.repository.PositionRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.position.service.PositionServiceImpl;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.DuplicateResourceException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.test.config.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.POSITION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest extends BaseUnitTest {

    @Mock
    private PositionRepository positionRepository;

    private final PositionMapper positionMapper = Mappers.getMapper(PositionMapper.class);;
    private final UUID positionId = UUID.randomUUID();

    private UUID tenantId;
    private Position position;
    private PositionRequest positionRequest;

    private PositionServiceImpl positionService;

    @BeforeEach
    void setUp() {
        positionService = new PositionServiceImpl(positionRepository, positionMapper);

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenantId = tenant.getId();
        TenantContext.setTenantId(tenant.getId());

        position = new Position(
                "Position Title",
                "Position Description",
                "Position Level",
                tenantId
        );
        position.setId(positionId);

        positionRequest = new PositionRequest(
                "Position Title",
                "Position Description",
                "Position Level"
        );
    }

    @Test
    void findById_shouldReturnPosition_whenPositionExists() {
        // Arrange
        when(positionRepository
                .findById(positionId))
                .thenReturn(Optional.of(position));

        // Act
        PositionResponse foundPosition = positionService.findById(positionId);

        // Assert
        assertThat(foundPosition).isNotNull();
        assertThat(foundPosition.id()).isEqualTo(positionId);
        assertThat(foundPosition.description()).isEqualTo(position.getDescription());
        verify(positionRepository, times(1)).findById(positionId);
    }

    @Test
    void findById_shouldThrowException_whenPositionDoesNotExist() {
        // Arrange
        when(positionRepository
                .findById(positionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> positionService.findById(positionId))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find POSITION with ID '" + positionId + "'.");

        verify(positionRepository, times(1)).findById(positionId);
    }

    @Test
    void save_shouldSaveAndReturnPosition_whenRequestIsValid() {
        // Arrange
//        when(tenantRepository.existsById(tenantId)).thenReturn(true);
        when(positionRepository.existsByTitleIgnoreCase(anyString())).thenReturn(false);
        when(positionRepository.save(any(Position.class))).thenReturn(position);

        // Act
        PositionResponse savedPosition = positionService.save(positionRequest);

        // Assert
        assertNotNull(savedPosition);
        assertEquals(positionId, savedPosition.id());
        assertEquals("Position Title", savedPosition.title());
        assertEquals("Position Description", savedPosition.description());
        assertFalse(savedPosition.active());

        verify(positionRepository, times(1)).save(any(Position.class));
    }

    @Test
    void save_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
//        when(tenantRepository.existsById(tenantId)).thenReturn(true);
        when(positionRepository.existsByTitleIgnoreCase(anyString())).thenReturn(true);

        // Act
        DuplicateResourceException thrown = catchThrowableOfType(
                DuplicateResourceException.class,
                ()-> positionService.save(positionRequest)
        );

        // Assert
        assertThat(thrown)
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("An existing POSITION already exists with the provided arguments.");

        verify(positionRepository, times(1)).existsByTitleIgnoreCase(anyString());
        verifyNoMoreInteractions(positionRepository);

        assertThat(thrown.getFields())
                .containsExactlyInAnyOrder("title");

        assertThat(thrown.getResourceType())
                .isEqualTo(POSITION);
    }
}