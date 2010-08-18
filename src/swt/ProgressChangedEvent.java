package swt;

/**
 * This event indicates that the progress of a XThread has changed.
 * @author PEH & FLN
 */
public class ProgressChangedEvent {

	/**
	 * the progress value
	 */
	private int progress;

	/**
	 * Creates a new ProgressChangedEvent.
	 * @param progress
	 */
	public ProgressChangedEvent(int progress) {
		this.progress = progress;
	}

	/**
	 * Returns the progress value.
	 * @return value
	 */
	public int getValue() {
		return progress;
	}
}
