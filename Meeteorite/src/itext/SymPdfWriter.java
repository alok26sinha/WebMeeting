package itext;

import java.io.OutputStream;

import view.PdfHeaderFooter;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;

public class SymPdfWriter {

	public static void getInstance(SymDocument document,
			OutputStream outputStream) throws DocumentException {
		PdfWriter writer = PdfWriter.getInstance(document.document, outputStream);
		PdfHeaderFooter headerFooter = new PdfHeaderFooter();
		writer.setPageEvent(headerFooter);
	}

}
