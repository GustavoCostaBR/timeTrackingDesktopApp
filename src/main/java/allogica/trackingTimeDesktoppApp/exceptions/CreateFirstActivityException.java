package allogica.trackingTimeDesktoppApp.exceptions;

/**
 * Custom exception class for when an createFirstActivity method is called and there is already one activity for the user
 */
public class CreateFirstActivityException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CreateFirstActivityException(String message) {
		super(message);
	}
}
