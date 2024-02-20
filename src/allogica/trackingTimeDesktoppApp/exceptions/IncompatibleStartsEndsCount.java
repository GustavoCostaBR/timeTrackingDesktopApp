package allogica.trackingTimeDesktoppApp.exceptions;


/**
 * Custom exception class for when an activity does not have a compatible number of starts and ends.
 */
public class IncompatibleStartsEndsCount extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IncompatibleStartsEndsCount(String message) {
		super(message);
	}
}
