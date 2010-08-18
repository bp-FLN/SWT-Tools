package swt;

import swt.SWTWorker.WorkerState;

/**
 * This event indicates that the status of a XThread has changed.
 * @author PEH & FLN
 */
public class StatusChangedEvent {

	/**
	 * the xstate
	 */
	private WorkerState state;

	/**
	 * Creates a new status changed event.
	 * @param s
	 */
	public StatusChangedEvent(WorkerState xstate) {
		this.state = xstate;
	}

	/**
	 * Returns the xstate.
	 * @return state
	 */
	public WorkerState getState() {
		return state;
	}
}
