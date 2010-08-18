package swt;

import java.util.EventListener;

/**
 * This interface is implemented by any class which wants to receive
 * notifications about the status change of an XThread.
 * @author PEH & FLN
 */
public interface StatusListener extends EventListener {

	/**
	 * This method is invoked when the status of a XThread has changed.
	 * @param e
	 */
	public void statusChanged(StatusChangedEvent e);
}
