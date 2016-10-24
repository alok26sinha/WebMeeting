package itext;

import javax.servlet.ServletOutputStream;

import com.lowagie.text.rtf.RtfWriter2;

public class SymRtfWriter2 {

	public static void getInstance(SymDocument document,
			ServletOutputStream outputStream) {
		RtfWriter2.getInstance(document.document, outputStream);
	}

}
