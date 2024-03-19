package allogica.trackingTimeDesktopApp.DTOs;

import allogica.trackingTimeDesktopApp.utilities.RoleName;

public record CreateUserDto (
		String username,
        String password,
        RoleName role)
{
	
}
