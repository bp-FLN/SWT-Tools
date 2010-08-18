package swt;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * The standart SWT ProgressBar with setText abilities.
 * This ProgressBar only shows the given text if showText is set true <b>AND</b> a text is set.
 * If not this ProgressBar shows it's current progress as text.
 * @author PEH
 * @see ProgressBar
 */
public class TextProgressBar extends Composite {
	public TextProgressBar(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		text = "";
		showText = false;
		bar = new ProgressBar(this, style);
		bar.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				Point point = bar.getSize();
				
				e.gc.setFont(getFont());
				e.gc.setForeground(getForeground());
				FontMetrics fontMetrics = e.gc.getFontMetrics();
				int stringHeight = fontMetrics.getHeight();
				if(!showText){
					text = String.valueOf(bar.getSelection());
				}else {
					text = text.replace("%%", String.valueOf(bar.getSelection()));
				}
				e.gc.drawString(text, (point.x - getStringWidth(text, fontMetrics)) / 2, (point.y - stringHeight) / 2, true);
			}
		});
	}
	private ProgressBar bar;
	private String text;
	private boolean showText;

	/**
	 * Returns the widgets underlying ProgressBar.
	 * @return the ProgressBar
	 */
	public ProgressBar getProgressBar() {
		return this.bar;
	}

	/**
	 * Set the shown Text
	 * This text is only shown if showText is true.
	 * Else the text in the bar will only show the current progress.
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Returns the currently shown text.<br>
	 * If showText is set false this method returns an empty String.
	 * @return - the currently shown text
	 */
	public String getText() {
		if (showText) {
			return this.text;
		}
		return "";
	}

	/**
	 * Sets weather to show text or just progress instead.
	 * @param showText
	 */
	public void setShowText(boolean showText) {
		this.showText = showText;
	}

	/**
	 * Returns weather text is shown or only the progress.
	 * @return - weather text is shown or progress
	 */
	public boolean isTextShown() {
		return this.showText;
	}

	/**
	 * Calls ProgressBar.setSelection(selection)
	 * @see ProgressBar#setSelection(int)
	 */
	public void setSelection(int selection) {
		bar.setSelection(selection);
	}

	/**
	 * calss ProgressBar.setMaximun(maximum)
	 * @see ProgressBar#setMaximum(int)
	 */
	public void setMaximum(int maximum) {
		bar.setMaximum(maximum);
	}

	/**
	 * Calls ProgressBar.setMinimum(minimum)
	 * @see ProgressBar#setMinimum(int)
	 */
	public void setMinimun(int minimum) {
		bar.setMinimum(minimum);
	}

	/**
	 * @return ProgressBar.getSelection()
	 * @see ProgressBar#getSelection()
	 */
	public int getSelection() {
		return bar.getSelection();
	}

	/**
	 * Returns the givens Strings width for given FontMetrics
	 * @param text
	 * @param fontMetrics
	 * @return the strings width in pixels
	 */
	private int getStringWidth(String text, FontMetrics fontMetrics) {
		return fontMetrics.getAverageCharWidth() * text.length();
	}
}
