package allogica.trackingTimeDesktoppApp.exceptions;

/**
 * Custom exception for when the database checks fails.
 */
public class CompromisedDataBaseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompromisedDataBaseException(String message) {
		super(message);
	}
}
