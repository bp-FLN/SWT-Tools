package swt;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SWTWorker that shows a Dialog while the thread is running.<br>
 * setProgress(int) sets the progress bar value and<br>
 * publish(String) sets the label text.<br>
 * supported flags:<br>
 * SWT.INDETERMINATE to make the progress bar indeterminate<br>
 * SWT.CANCEL disables cancel button and dialog close button (useful when the task is not interruptible)<br>
 * @author FLN
 */
public abstract class ProgressDialogWorker<T>
		extends SWTWorker<T, String>
		implements ProgressListener {

	/** the display */
	private Display display;
	/** the progress dialog */
	private ProgressDialog dialog;
	/** the logger */
	private static final Logger log = LoggerFactory.getLogger(ProgressDialogWorker.class);

//	public ProgressDialogWorker(Shell shell) {
//		this(shell, SWT.INDETERMINATE | SWT.CANCEL);
//	}

	public ProgressDialogWorker(Shell shell, int flags) {
		this(shell, flags, "Progress Dialog");
	}

	public ProgressDialogWorker(Shell shell, int flags, String title) {
		dialog = new ProgressDialog(shell, flags, title);
		display = shell.getDisplay();
		addListener();
	}

	private void addListener() {
		addProgressListener(this);
	}

	@Override
	public final void process(List<String> s) {
		dialog.setStatusLabel(s.get(s.size() - 1));
	}

	@Override
	public void progressChanged(ProgressChangedEvent e) {
		dialog.setProgress(e.getValue());
	}

	public boolean isDialogDisposed() {
		return dialog.isDisposed();
	}

	protected void disposeDialog() {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				dialog.dispose();
			}
		});
	}

	protected void showDialog() {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				dialog.open();
			}
		});
//
//		try {
//			Thread.sleep(250);
//		} catch (InterruptedException ex) {
//			log.error(null, ex);
//		}
	}
}
