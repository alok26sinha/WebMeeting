package itext;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;

public class SymRtfTable extends SymTable {

	Table table;

	SymRtfTable(int columns) throws BadElementException {
		super(columns);
		table = new Table(columns);
		table.setBorder(0);
		// Does not seem to have any effect
		// table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	}

	public void setWidthPercentage(int width) {
		table.setWidth(width);
	}

	public void setWidths(float[] widths) throws DocumentException {
		table.setWidths(widths);
	}

	public Element getTable() {
		return table;
	}

	private void addCell(Cell cell) {
		table.addCell(cell);
	}

	public void addPhrase(Phrase phrase, Integer alignment)
			throws BadElementException {
		Cell cell = new Cell(phrase);
		if (alignment != null)
			cell.setHorizontalAlignment(alignment);

		// Line does not seem to have much effect for this table type. i.e.
		// cells are always bordered
		int border = Rectangle.NO_BORDER;
		cell.setBorder(border);

		addCell(cell);
	}

	public void addEmptyRow(int height) {
		Cell cell = new Cell();
		cell.setColspan(columns);
		// cell.setMinimumHeight(height); not exist for html
		cell.setBorder(Rectangle.NO_BORDER);
		addCell(cell);
	}

	@Override
	public void writeLine() {
		// Do nothing
	}

	@Override
	public void setHorizontalAlignment(int alignLeft) {
		// Do nothing
	}

}
