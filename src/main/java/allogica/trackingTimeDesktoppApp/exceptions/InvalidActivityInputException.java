package allogica.trackingTimeDesktoppApp.exceptions;

/**
 * Custom exception for when the data input for activity is invalid.
 */
public class InvalidActivityInputException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidActivityInputException(String message) {
		super(message);
	}
}
