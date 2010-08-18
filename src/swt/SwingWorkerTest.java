package swt;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Administrator
 */
public class SwingWorkerTest extends JFrame {

    JButton button = new JButton("Run the Utility");
    JTextField t1 = new JTextField();
    JTextField t2 = new JTextField();
    JTextField t3 = new JTextField();
    JProgressBar pb1 = new JProgressBar();
    JProgressBar pb2 = new JProgressBar();
    JProgressBar pb3 = new JProgressBar();
    JPanel panel = new JPanel();
    private static Logger log = LoggerFactory.getLogger(SwingWorkerTest.class);

    SwingWorkerTest() {
        super("Event Listener Demo");
        setBounds(100, 100, 250, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel.setLayout(new GridLayout(4, 1, 2, 2));
        panel.add(button);
        panel.add(t1);
        panel.add(t2);
        panel.add(t3);
        panel.add(pb1);
        panel.add(pb2);
        panel.add(pb3);
        this.getContentPane().add("Center", panel);
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ThreadFactory threadFactory =
                        new ThreadFactory() {

                            final ThreadFactory defaultFactory =
                                    Executors.defaultThreadFactory();

                            public Thread newThread(final Runnable r) {
                                Thread thread =
                                        defaultFactory.newThread(r);
                                thread.setName("SwingWorker-"
                                        + thread.getName());
                                thread.setDaemon(true);
                                return thread;
                            }
                        };

                ExecutorService executorService =
                        new ThreadPoolExecutor(3, 10,
                        10L, TimeUnit.MINUTES,
                        new LinkedBlockingQueue<Runnable>(),
                        threadFactory);
//                executorService.submit(new TestWorker(t3, pb3, 1000, 10));
//                executorService.submit(new TestWorker(t2, pb2, 100, 100));
//                executorService.submit(new TestWorker(t1, pb1, 10, 1000));

                new TestWorker(t3, pb3, 1000, 10).execute();
                new TestWorker(t2, pb2, 100, 100).execute();
                new TestWorker(t1, pb1, 10, 1000).execute();
            }
        });
        setVisible(true);
    }

    public static void main(String[] args) {
        new SwingWorkerTest().init();
    }

    private void init() {
    }

    class TestWorker extends SwingWorker<String, String> implements PropertyChangeListener {

        private JTextField t;
        private JProgressBar pb;
        private int loops;
        private int sleep;

        public TestWorker(JTextField t, JProgressBar pb, int loops, int sleep) {
            this.t = t;
            this.pb = pb;
            this.loops = loops;
            this.sleep = sleep;
            addPropertyChangeListener(this);
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

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("progress")) {
                pb.setValue((Integer) evt.getNewValue());
            }
        }
    }
}
