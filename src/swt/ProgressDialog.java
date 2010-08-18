package swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Simple progress dialog containing a progress bar,
 * a status label and a cancel button.
 * @author FLN
 */
class ProgressDialog extends Dialog {

	private Display display;
	private Shell shell;
	private TextProgressBar progressBar;
	private Label statusLabel;
	private int flags;
	private String title;

	ProgressDialog(Shell shell, int flags, String title) {
		super(shell, SWT.NONE);
		display = shell.getDisplay();
		this.flags = flags;
		this.title = title;
		initWidgets();
	}

	public void setStatusLabel(String s) {
		if (!shell.isDisposed()) {
			statusLabel.setText(s);
		}
	}

	public void setProgress(int val) {
		if (!shell.isDisposed()) {
			progressBar.setText("%% %");
			progressBar.setSelection(val);
		}
	}

	public boolean isDisposed() {
		return shell.isDisposed();
	}

	public Shell getShell() {
		return shell;
	}

	private void initWidgets() {
//		shell = new Shell(getParent(), SWT.TITLE | SWT.TOOL | SWT.APPLICATION_MODAL);
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(title);
		shell.setLayout(new GridLayout());

		GridData gridData;

		statusLabel = new Label(shell, SWT.NONE);
		statusLabel.setText("statusLabel");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.widthHint = 200;
		gridData.verticalIndent = 5;
		statusLabel.setLayoutData(gridData);

		int style = (flags & SWT.INDETERMINATE) != 0 ? SWT.INDETERMINATE : SWT.NONE;
		progressBar = new TextProgressBar(shell, style);
		progressBar.setShowText(true);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.widthHint = 200;
		gridData.verticalIndent = 5;
		progressBar.setLayoutData(gridData);

		Button cancelButton = new Button(shell, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalIndent = 5;
		cancelButton.setLayoutData(gridData);
		cancelButton.setText("Cancel");
		boolean canCancel = (flags & SWT.CANCEL) != 0 ? true : false;
		if (canCancel) {
			cancelButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					shell.dispose();
				}
			});
		} else {
			cancelButton.setEnabled(false);
			shell.addShellListener(new ShellAdapter() {

				@Override
				public void shellClosed(ShellEvent e) {
					e.doit = false;
				}
			});
		}

	}

	void open() {
		shell.pack();
		shell.open();
		GuiTools.centerShellOnParent(getParent(), shell);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	void dispose() {
		shell.dispose();
	}
}
