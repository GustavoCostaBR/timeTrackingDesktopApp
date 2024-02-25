package allogica.trackingTimeDesktoppApp.exceptions;

/**
 * Custom exception class for when an activity does not have subactivities or an incompatible count of ends.
 */
public class ActivityEndingTimeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActivityEndingTimeException(String message) {
		super(message);
	}
}
