package itext;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class SymPdfTable extends SymTable {

	PdfPTable table;

	SymPdfTable(int columns) throws BadElementException {
		super(columns);
		table = new PdfPTable(columns);
	}

	public void setWidthPercentage(int width) {
		table.setWidthPercentage(width);
	}

	public void setWidths(float[] widths) throws DocumentException {
		table.setWidths(widths);
	}

	public Element getTable() {
		return table;
	}

	private void addCell(PdfPCell cell) {
		table.addCell(cell);
	}

	public void addPhrase(Phrase phrase, Integer alignment) {
		PdfPCell cell = new PdfPCell(phrase);
		cell.setLeading(0, 1.2f);
		cell.setPaddingTop(1);
		cell.setPaddingBottom(3);
		cell.setPaddingLeft(0);
		cell.setPaddingRight(5);

		if (alignment != null)
			cell.setHorizontalAlignment(alignment);

		int border = Rectangle.NO_BORDER;
		cell.setBorder(border);

		addCell(cell);
	}

	public void addEmptyRow(int height) {
		PdfPCell cell = new PdfPCell();
		cell.setColspan(columns);
		cell.setMinimumHeight(height);
		cell.setBorder(Rectangle.NO_BORDER);
		addCell(cell);
	}

	@Override
	public void writeLine() {
		PdfPCell cell = new PdfPCell();
		cell.setColspan(columns);
		cell.setFixedHeight((float) 0.01);
		cell.setBorder(Rectangle.TOP);
		addCell(cell);
	}

	@Override
	public void setHorizontalAlignment(int alignLeft) {
		table.setHorizontalAlignment(alignLeft);
	}


}
