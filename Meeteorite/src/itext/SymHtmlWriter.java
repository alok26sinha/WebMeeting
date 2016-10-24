package itext;

import javax.servlet.ServletOutputStream;

import com.lowagie.text.html.HtmlWriter;

public class SymHtmlWriter {

	public static void getInstance(SymDocument document,
			ServletOutputStream outputStream) {
		HtmlWriter.getInstance(document.document, outputStream);
	}

}
