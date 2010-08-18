package swt;

import java.util.EventListener;

/**
 * This interface is implemented by any class which wants to receive
 * notifications about the progress change of an XThread.
 * @author PEH & FLN
 */
public interface ProgressListener extends EventListener {

	/**
	 * This method is invoked when the progress message of a XThread has changed.
	 * @param e
	 */
	public void progressChanged(ProgressChangedEvent e);
}
