package allogica.trackingTimeDesktoppApp.exceptions;

/**
 * Custom exception class for when an activity is not found.
 */
public class ActivityNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActivityNotFoundException(Long Id) {
		super("Activity Id = " + Id + " was not found.");
	}
}
