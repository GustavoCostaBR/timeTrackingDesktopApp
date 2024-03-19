package allogica.trackingTimeDesktopApp.DTOs;

import allogica.trackingTimeDesktopApp.utilities.RoleName;

public record CreateUserDto (
		String email,
        String password,
        RoleName role)
{
	
}
