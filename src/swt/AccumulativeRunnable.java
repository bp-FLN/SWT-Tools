/*
 * $Id: AccumulativeRunnable.java,v 1.1 2005/06/18 21:27:15 idk Exp $
 *
 * Copyright  2005 Sun Microsystems, Inc. All rights
 * reserved. Use is subject to license terms.
 */

package swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class to be used in the cases where we need {@code Runnable}
 * to perform  some actions on an appendable set of data.
 * The set of data might be appended after the {@code Runnable} is
 * sent for the execution. Usually such {@code Runnables} are sent to
 * the EDT.
 *
 * <p>
 * Usage example:
 *
 * <p>
 * Say we want to implement JLabel.setText(String text) which sends
 * {@code text} string to the JLabel.setTextImpl(String text) on the EDT.
 * In the event JLabel.setText is called rapidly many times off the EDT
 * we will get many updates on the EDT but only the last one is important.
 * (Every next updates overrides the previous one.)
 * We might want to implement this {@code setText} in a way that only
 * the last update is delivered.
 * <p>
 * Here is how one can do this using {@code AccumulativeRunnable}:
 * <pre>
 * AccumulativeRunnable<String> doSetTextImpl =
 * new  AccumulativeRunnable<String>() {
 *     @Override
 *     protected void run(String... args) {
 *         //set to the last string being passed
 *         setTextImpl(args[args.size - 1]);
 *     }
 * }
 * void setText(String text) {
 *     //add text and send for the execution if needed.
 *     doSetTextImpl.add(text);
 * }
 * </pre>
 *
 * <p>
 * Say we want want to implement addDirtyRegion(Rectangle rect)
 * which sends this region to the
 * handleDirtyRegions(List<Rect> regiouns) on the EDT.
 * addDirtyRegions better be accumulated before handling on the EDT.
 *
 * <p>
 * Here is how it can be implemented using AccumulativeRunnable:
 * <pre>
 * AccumulativeRunnable<Rectangle> doHandleDirtyRegions =
 *     new AccumulativeRunnable<Rectangle>() {
 *         @Override
 *         protected void run(Rectangle... args) {
 *             handleDirtyRegions(Arrays.asList(args));
 *         }
 *     };
 *  void addDirtyRegion(Rectangle rect) {
 *      doHandleDirtyRegions.add(rect);
 *  }
 * </pre>
 *
 * @author Igor Kushnirskiy
 * @version $Revision: 1.1 $ $Date: 2005/06/18 21:27:15 $
 *
 * @param <T> the type this {@code Runnable} accumulates
 *
 */
abstract class AccumulativeRunnable<T> implements Runnable {
    private List<T> arguments = null;
    private Class<?> componentType = null;
    private static Logger log = LoggerFactory.getLogger(AccumulativeRunnable.class);

    /**
     * Equivalent to {@code Runnable.run} method with the
     * accumulated arguments to process.
     *
     * @param args accumulated argumets to process.
     */
    protected abstract void run(List<T> args);

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation calls {@code run(T... args)} mehtod
     * with the list of accumulated arguments.
     */
	@Override
    public final void run() {
//        log.debug("run");
        run(flush());
//        log.debug("run done");
    }

    /**
     * appends arguments and sends this {@cod Runnable} for the
     * execution if needed.
     * <p>
     * This implementation uses {@see #submit} to send this
     * {@code Runnable} for execution.
     * @param args the arguments to accumulate
     */
    public final synchronized void add(T... args) {
//        log.debug("add: {}", args);
        if (componentType == null) {
            componentType = (Class<T>) args.getClass().getComponentType();
        }
        boolean isSubmitted = true;
        if (arguments == null) {
            isSubmitted = false;
            arguments = new ArrayList<T>();
        }
        Collections.addAll(arguments, args);
//        arguments.addAll(args);
        if (!isSubmitted) {
            submit();
        }
//        log.debug("add done");
    }

    /**
     * Sends this {@code Runnable} for the execution
     *
     * <p>
     * This method is to be executed only from {@code add} method.
     *
     * <p>
     * This implementation uses {@code SwingWorker.invokeLater}.
     */
    protected void submit() {
//        SwingUtilities.invokeLater(this);
//        log.debug("submit");
        Display.getCurrent().asyncExec(this);
//        log.debug("submit done");
    }

    /**
     * Returns accumulated arguments and flashes the arguments storage.
     *
     * @return accumulated artuments
     */
    private synchronized List<T> flush() {
//        log.debug("flush");
        List<T> list = arguments;
        arguments = null;
        if (componentType == null) {
            componentType = Object.class;
        }
//        List<T> args = (List<T>) Array.newInstance(componentType,
//                                           list.size());
//        list.toArray(args);
//        log.debug("flush done");
        return list;
    }
}