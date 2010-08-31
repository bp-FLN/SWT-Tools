package swt;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 *
 * @author FLN
 */
public class GuiTools {

	public static final FormAttachment TOP_LEFT = new FormAttachment(0, 10);
	public static final FormAttachment BOTTOM_RIGHT = new FormAttachment(100, -10);
	public static final int OK_CANCEL = SWT.OK | SWT.CANCEL;

	private GuiTools() {
	}

	public static void centerShellOnParent(Shell parent, Shell shell) {
		Rectangle shellBounds = parent.getBounds();
		Point dialogSize = shell.getSize();
		int x = shellBounds.x + (shellBounds.width - dialogSize.x) / 2;
		int y = shellBounds.y + (shellBounds.height - dialogSize.y) / 2;
		shell.setLocation(x, y);
	}

	public static void centerShellOnScreen(Shell shell) {
		Monitor primary = shell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}

	public static Image loadImage(Display display, Class<?> clazz, String path) {
		InputStream stream = clazz.getResourceAsStream(path);
		if (stream == null) {
			return null;
		}
		Image image = null;
		try {
			image = new Image(display, stream);
		} catch (SWTException ex) {
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
			}
		}
		return image;
	}

	public static int showMessage(Shell s, String title, String msg, int style) {
		MessageBox mb = new MessageBox(s, style);
		mb.setText(title);
		mb.setMessage(msg);
		return mb.open();
	}

	public static int showConfirmMessage(Shell shell, String title, String msg) {
		return showMessage(shell, title, msg, SWT.ICON_QUESTION | OK_CANCEL);
	}

	public static int showConfirmMessage(Shell shell, String msg) {
		return showMessage(shell, "Question", msg, SWT.ICON_QUESTION | OK_CANCEL);
	}

	public static void showErrorMessage(Shell s, String title, String msg) {
		showMessage(s, title, msg, SWT.ICON_ERROR | SWT.OK);
	}

	public static void showErrorMessage(Shell shell, String msg) {
		showMessage(shell, "Error", msg, SWT.ICON_ERROR | SWT.OK);
	}

	public static void showWarningMessage(Shell shell, String title, String msg) {
		showMessage(shell, title, msg, SWT.ICON_WARNING | SWT.OK);
	}

	public static void showWarningMessage(Shell shell, String msg) {
		showMessage(shell, "Warning", msg, SWT.ICON_WARNING | SWT.OK);
	}

	public static void showInformationMessage(Shell shell, String title, String msg) {
		showMessage(shell, title, msg, SWT.ICON_INFORMATION | SWT.OK);
	}

	public static void showInformationMessage(Shell shell, String msg) {
		showMessage(shell, "Information", msg, SWT.ICON_INFORMATION | SWT.OK);
	}

	public static void showExceptionDialog(final Shell shell, final Exception ex) {
		shell.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				ExceptionDialog dlg = new ExceptionDialog(shell);
				dlg.setException(ex);
				dlg.open();
			}
		});
	}
}
