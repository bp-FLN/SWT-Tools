package swt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author FLN
 */
public abstract class SWTWorker<T, V> implements RunnableFuture<T> {

	/** the progress (0-100) */
	private volatile int progress;
	/** the workers state */
	private volatile WorkerState state;
	/** the future task */
	private FutureTask<T> future;
	/** the progress listeners */
	private List<ProgressListener> progressListeners;
	/** the status listeners */
	private List<StatusListener> statusListeners;
	private AccumulativeRunnable<V> doProcess;
	private AccumulativeRunnable<Integer> doNotifyProgressChange;
	private static AccumulativeRunnable<Runnable> doSubmit;
	private static ExecutorService executorService;
	/* maximum worker threads in queue */
	private static final int MAX_WORKER_THREADS = 10;
	/* maximum worker threads running concurrently */
	private static final int MAX_CONCURRENT_WORKER_THREADS = 1;
	//	private static Logger log = LoggerFactory.getLogger(SWTWorker.class);

	public SWTWorker() {
		progressListeners = new ArrayList<ProgressListener>();
		statusListeners = new ArrayList<StatusListener>();
		doSubmit = new DoSubmitAccumulativeRunnable();
		executorService = getWorkersExecutorService();

		Callable<T> callable = new Callable<T>() {

			@Override
			public T call() throws Exception {
				setState(WorkerState.RUNNABLE);
				return doInBackground();
			}
		};

		future = new FutureTask<T>(callable) {

			@Override
			protected void done() {
				doneEDT();
				setState(WorkerState.FINISHED);
			}
		};
	}

	/**
	 * Invokes {@code done} on the EDT.
	 */
	private void doneEDT() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				done();
			}
		});
	}

	private void setState(WorkerState workerState) {
		this.state = workerState;
		fireStatusChanged();
	}

	protected void process(List<V> chunks) {
	}

	protected final void publish(V... chunks) {
		synchronized (this) {
			if (doProcess == null) {
				doProcess = new AccumulativeRunnable<V>() {

					@Override
					protected void run(final List<V> args) {
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								process(args);
							}
						});
					}

					@Override
					protected void submit() {
						doSubmit.add(this);
					}
				};
			}
		}
		doProcess.add(chunks);
	}

	protected void done() {
	}

	protected abstract T doInBackground() throws Exception;

	@Override
	public final T get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	public final void execute() {
		executorService.execute(this);
	}

	private static synchronized ExecutorService getWorkersExecutorService() {
		if (executorService == null) {
			//this creates daemon threads.
			ThreadFactory threadFactory =
					new ThreadFactory() {

						final ThreadFactory defaultFactory =
								Executors.defaultThreadFactory();

						@Override
						public Thread newThread(final Runnable r) {
							Thread thread =
									defaultFactory.newThread(r);
							thread.setName("SWTWorker-"
									+ thread.getName());
							thread.setDaemon(true);
							return thread;
						}
					};

			executorService =
					new ThreadPoolExecutor(
					MAX_CONCURRENT_WORKER_THREADS,
					MAX_WORKER_THREADS,
					10L,
					TimeUnit.MINUTES,
					new LinkedBlockingQueue<Runnable>(),
					threadFactory);
		}

		return executorService;
	}

	/**
	 * Adds the given StatusListener to the list of listeners which will
	 * be notified when this SWTWorker's progress has changed.
	 * @param progressListener the progress listener
	 */
	public void addProgressListener(ProgressListener progressListener) {
		if (progressListeners.contains(progressListener)) {
			return;
		}
		this.progressListeners.add(progressListener);
	}

	/**
	 * Adds the given ProgressListener to the list of listeners which will
	 * be notified when this SWTWorker's state has changed.
	 * @param statusListener the status listeners
	 */
	public void addStatusListener(StatusListener statusListener) {
		if (statusListeners.contains(statusListener)) {
			return;
		}
		this.statusListeners.add(statusListener);
	}

	/**
	 * Gets the current progress value.
	 * @return progress
	 */
	public int getProgress() {
		return progress;
	}

	protected final void setProgress(int progress) {
		if (progress < 0 || progress > 100) {
			throw new IllegalArgumentException("the progress value should be from 0 to 100");
		}
		if (this.progress == progress) {
			return;
		}
		int oldProgress = this.progress;
		this.progress = progress;

		synchronized (this) {
			if (doNotifyProgressChange == null) {
				doNotifyProgressChange =
						new AccumulativeRunnable<Integer>() {

							@Override
							public void run(List<Integer> args) {
								fireProgressChanged();
							}

							@Override
							protected void submit() {
								doSubmit.add(this);
							}
						};
			}
		}
		doNotifyProgressChange.add(oldProgress, progress);
	}

	private void fireProgressChanged() {
		ProgressChangedEvent event = new ProgressChangedEvent(progress);
		for (ProgressListener listener : progressListeners) {
			listener.progressChanged(event);
		}
	}

	private void fireStatusChanged() {
		StatusChangedEvent event = new StatusChangedEvent(state);
		for (StatusListener listener : statusListeners) {
			listener.statusChanged(event);
		}
	}

	// Future methods START
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isCancelled() {
		return future.isCancelled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isDone() {
		return future.isDone();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Please refer to {@link #get} for more details.
	 */
	@Override
	public final T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return future.get(timeout, unit);
	}

	/**
	 * Sets this {@code Future} to the result of computation unless
	 * it has been cancelled.
	 */
	@Override
	public final void run() {
		future.run();
	}

	/**
	 * The states a SWTWorker can adopt.
	 */
	public enum WorkerState {

		/** indicates that this thread has been initialized but not yet started */
		RUNNABLE,
		/** indicates that this thread is currently running (working) */
		RUNNING,
		/** indicates that this thread is currently waiting	*/
		WAITING,
		/** indicates that this thread is finished */
		FINISHED;
	}

	private static class DoSubmitAccumulativeRunnable
			extends AccumulativeRunnable<Runnable> {

		private void doRun() {
			run();
		}

		@Override
		protected void run(List<Runnable> args) {
			for (Runnable runnable : args) {
				runnable.run();
			}
		}

		@Override
		protected void submit() {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					doRun();
				}
			});
		}
	}
}
