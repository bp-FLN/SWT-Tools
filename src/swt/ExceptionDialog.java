package swt;

import swt.GuiTools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * SWT Exception Dialog.
 * @author FLN
 */
public final class ExceptionDialog extends Dialog
		implements PaintListener {

	private static Display display;
	private static Shell shell;
	private static Label label;
	private static Label imageLabel;
	private static Button detailButton;
	private static Button shutdownButton;
	private static Button continueButton;
	private static Text text;
	private static Image image;

	public ExceptionDialog(Shell parent, int style) {
		super(parent, style);
		display = parent.getDisplay();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("Exception occured");
		shell.setLayout(new FormLayout());
		initWidgets();
		initListeners();
	}

	public ExceptionDialog(Shell parent) {
		this(parent, 0);
	}

	private void initListeners() {
		detailButton.addSelectionListener(new DetailsSelectionListener());
		shutdownButton.addSelectionListener(new ShutdownSelectionListener());
		continueButton.addSelectionListener(new ContinueSelectionListener());
	}

	private void initWidgets() {
		//image = GuiTools.loadImage(display, this.getClass(), "images/exception.ico");
		image = display.getSystemImage(SWT.ICON_ERROR);
		FormData data = new FormData();
		imageLabel = new Label(shell, SWT.NONE);
		imageLabel.setImage(image);
		data.top = new FormAttachment(0, 20);
		data.left = new FormAttachment(0, 20);
		imageLabel.setLayoutData(data);

		label = new Label(shell, SWT.NONE);
		label.setText("An exception occured!\n"
				+ "Click continue to keep working or\n"
				+ "details to see more information.");
		data = new FormData();
		data.top = GuiTools.TOP_LEFT;
		data.left = new FormAttachment(imageLabel, 20);
		data.right = GuiTools.BOTTOM_RIGHT;
		label.setLayoutData(data);

		detailButton = new Button(shell, SWT.TOGGLE);
		detailButton.setText("Details");
		data = new FormData();
		data.top = new FormAttachment(label, 10);
		data.left = GuiTools.TOP_LEFT;
		detailButton.setLayoutData(data);

		continueButton = new Button(shell, SWT.NONE);
		continueButton.setText("Continue");
		data = new FormData();
		data.top = new FormAttachment(label, 10);
		data.right = GuiTools.BOTTOM_RIGHT;
		continueButton.setLayoutData(data);

		shutdownButton = new Button(shell, SWT.NONE);
		shutdownButton.setText("Shutdown");
		data = new FormData();
		data.top = new FormAttachment(label, 10);
		data.right = new FormAttachment(continueButton, -5);
		shutdownButton.setLayoutData(data);

		text = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setEditable(false);
		data = new FormData();
		data.top = new FormAttachment(detailButton, 10);
		data.bottom = new FormAttachment(100, 0);
		data.left = GuiTools.TOP_LEFT;
		data.right = GuiTools.BOTTOM_RIGHT;
		data.width = 430;
		text.setLayoutData(data);

		text.setVisible(false);
		continueButton.setFocus();
	}

	public void open() {
		shell.pack();
		shell.setSize(shell.getSize().x, shell.getSize().y - text.getSize().y);
		shell.open();

//		Rectangle shellBounds = getParent().getBounds();
//		Point dialogSize = shell.getSize();
//		shell.setLocation(
//				shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
//				shellBounds.y + (shellBounds.height - dialogSize.y) / 2);

		GuiTools.centerShellOnParent(getParent(), shell);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void setException(Exception ex) {
		text.append(ex.toString() + "\n");
		for (StackTraceElement e : ex.getStackTrace()) {
			text.append("\tat " + e.toString() + "\n");
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.drawImage(image, 0, 0);
	}

	private class ContinueSelectionListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			shell.dispose();
		}
	}

	private class ShutdownSelectionListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			for (Shell shell : Display.getDefault().getShells()) {
				shell.dispose();
			}
		}
	}

	private class DetailsSelectionListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (text.isVisible()) {
				text.setVisible(false);
				shell.setSize(shell.getSize().x, shell.getSize().y - 200);
				((FormData) text.getLayoutData()).bottom = new FormAttachment(100, 0);
				shell.layout();
			} else {
				text.setVisible(true);
				shell.setSize(shell.getSize().x, shell.getSize().y + 200);
				((FormData) text.getLayoutData()).bottom = GuiTools.BOTTOM_RIGHT;
				shell.layout();
			}
		}
	}
}
