package swt;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jonn
 */
public class SWTWorkerTest {

	private Shell shell;
	private Display display;
	private Button b;
	private Text t1;
	private Text t2;
	private Text t3;
	private ProgressBar pb1;
	private ProgressBar pb2;
	private ProgressBar pb3;
	private static Logger log = LoggerFactory.getLogger(SWTWorkerTest.class);

	public static void main(String[] args) {
		Thread.currentThread().setName("Event-Dispatch-Thread");
		new SWTWorkerTest().init();
	}

	private void init() {
		display = Display.getDefault();
		shell = new Shell(display);
		shell.setText("SWTWorkerTest");
//		shell.setLayout(new FillLayout());
//		RowLayout rowLayout = new RowLayout(1);
//		rowLayout.fill = true;
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
//                data.FILL_HORIZONTAL;
		data.horizontalSpan = 3;
		b = new Button(shell, SWT.NONE);
		b.setLayoutData(data);
		b.setText("start");


		data = new GridData(GridData.FILL_HORIZONTAL);
//                data.horizontalSpan = 1;
		shell.setLayout(layout);

		t1 = new Text(shell, SWT.NONE);
		t1.setLayoutData(data);
		t2 = new Text(shell, SWT.NONE);
		t2.setLayoutData(data);
		t3 = new Text(shell, SWT.NONE);
		t3.setLayoutData(data);
		pb1 = new ProgressBar(shell, SWT.SMOOTH);
		pb2 = new ProgressBar(shell, SWT.SMOOTH);
		pb3 = new ProgressBar(shell, SWT.SMOOTH);

		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				new TestWorker(t3, pb3, 1000, 10).execute();
				new TestWorker(t2, pb2, 100, 100).execute();
				new TestWorker(t1, pb1, 10, 1000).execute();
				new ProgressDialogWorkerTest(1000, 10).execute();
//                new TestWorker(1000, 10).execute();
			}
		});

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	class ProgressDialogWorkerTest extends ProgressDialogWorker<String> {

		private int loops;
		private int sleep;

		public ProgressDialogWorkerTest(int loops, int sleep) {
			super(shell, SWT.NONE, "title");
			this.loops = loops;
			this.sleep = sleep;
		}

		@Override
		protected String doInBackground() throws Exception {
			log.debug("starting execution ...");
			showDialog();

			for (int i = 1; i <= loops; i++) {
				if (isDialogDisposed()) {
					cancel(true);
				}

//                Thread.sleep(sleep);
				Thread.sleep((int) (Math.random() * 10));
				setProgress(i * 100 / loops);
				publish(String.valueOf(i));

			}
			return "ende task";
		}

		@Override
		public void done() {
			try {
				if (isCancelled()) {
					log.debug("we got killed!!");
					return;
				}
				String s = get();
				log.debug(s);
			} catch (Exception ex) {
				log.error(null, ex);
			} finally {
				disposeDialog();
			}
		}
	}

	class TestWorker extends SWTWorker<String, String> implements ProgressListener {

		private Text t;
		private ProgressBar pb;
		private int loops;
		private int sleep;

		public TestWorker(Text t, ProgressBar pb, int loops, int sleep) {
			this.t = t;
			this.pb = pb;
			this.loops = loops;
			this.sleep = sleep;
			addProgressListener(this);
		}

		@Override
		protected String doInBackground() throws Exception {
			log.debug("starting execution ...");
			for (int i = 0; i <= loops; i++) {
				setProgress(i * 100 / loops);
				publish(String.valueOf(i));
				Thread.sleep(sleep);
			}
			return "ende task";
		}

		@Override
		public void process(List<String> s) {
			for (String ss : s) {
				t.setText(ss);
			}
		}

		@Override
		public void done() {
			try {
				String s = get();
				log.debug(s);
			} catch (Exception ex) {
				log.error(null, ex);
			}
		}

		@Override
		public void progressChanged(ProgressChangedEvent e) {
			pb.setSelection(e.getValue());
		}
	}
}
