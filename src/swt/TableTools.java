package swt;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 *
 * @author PEH
 */
public class TableTools {


	public static void packTable(Table table){
		for(TableColumn tc : table.getColumns()){
			tc.pack();
		}
	}
}
