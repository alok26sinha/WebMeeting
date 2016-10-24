package itext;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;

/**
 * Thin wrapper around the iText document class
 * 
 * The primary reason for doing this is that itext requires either Table or
 * PdfTable as the table type. This class and the supporting classes abstract
 * this away and choose the correct type internally
 */
public class SymDocument {

	private boolean pdfOutput;

	public SymDocument(boolean pdfOutput) {
		this.pdfOutput = pdfOutput;
	}

	// Default border is 36 points (.5 inches)
	protected Document document = new Document(PageSize.A4, 72, 72, 72, 72);

	public void open() {
		document.open();
	}

	public void close() {
		document.close();
	}

	public void add(Paragraph contactParagraph) throws DocumentException {
		document.add(contactParagraph);
	}

	public void add(Phrase phrase) throws DocumentException {
		document.add(phrase);
	}

	public void add(Chunk chunk) throws DocumentException {
		document.add(chunk);
	}

	public void add(PdfPTable table) throws DocumentException {
		document.add(table);
	}

	public void add(SymTable table) throws DocumentException {
		document.add(table.getTable());
	}

	public SymTable createTable(int columns) throws BadElementException {
		if (pdfOutput)
			return new SymPdfTable(columns);
		else
			return new SymRtfTable(columns);
	}

	public void add(Image image) throws DocumentException {
		document.add(image);
	}

}
