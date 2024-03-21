package allogica.trackingTimeDesktopApp.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TimeParser {
	public static LocalDateTime parseStringToLocalDateTime (String stringLocalDateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // Create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        LocalDateTime localDateTime = LocalDateTime.parse(stringLocalDateTime, formatter);
	return localDateTime;
	}
}
