package view;

import com.lowagie.text.Font;
import com.lowagie.text.Phrase;

@SuppressWarnings("serial")
public class CleanPhrase extends Phrase {

	public CleanPhrase(String string) {
		super(string != null ? string.replaceAll("\r", "") : null);
	}

	public CleanPhrase(String string, Font font) {
		super(string != null ? string.replaceAll("\r", "") : null, font);
	}
}
