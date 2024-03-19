package allogica.trackingTimeDesktopApp.DTOs;

import java.util.Set;

import allogica.trackingTimeDesktopApp.model.entity.Role;

public record RecoveryUserDto(
		Long id,
        String username,
        Set<Role> roles) {

}
