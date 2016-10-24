package itext;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;

/**
 * Wraps either an PdfPTable or a Table. Depending if output is for PDF.
 */
public abstract class SymTable {

	int columns;

	SymTable(int columns) throws BadElementException {
		this.columns = columns;
	}

	public abstract void setWidthPercentage(int width);

	public abstract void setWidths(float[] widths) throws DocumentException;

	public abstract Element getTable();

	public void addPhrase(Phrase phrase) throws BadElementException {
		addPhrase(phrase, null);
	}

	public abstract void addPhrase(Phrase phrase, Integer alignment)
			throws BadElementException ;

	public abstract void addEmptyRow(int height);
	
	public abstract void writeLine();

	public abstract void setHorizontalAlignment(int alignLeft);

}
