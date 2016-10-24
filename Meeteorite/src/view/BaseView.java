package view;

import itext.SymDocument;
import itext.SymTable;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;

import model.Guest;
import model.Meeting;
import model.PermanentFile;
import subsystems.operatingsystem.OperatingSystem;
import type.Money;
import type.StringUtils;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfCell;
import common.Config;

public abstract class BaseView {
	private static final int FONT_SIZE = 8;
	protected static final Font STANDARD = new Font();
	protected static final Font BOLD = new Font();
	protected static final Font DOUBLE_SIZE_BOLD = new Font();
	protected static final Font ITALIC = new Font();
	protected static final Font SMALL_PRINT = new Font();
	protected static final OperatingSystem os = new OperatingSystem();
	protected Font heading;
	protected Font subHeading;
	public static Font headerFooterFont = new Font();
	static {
		STANDARD.setSize(FONT_SIZE);
		BOLD.setStyle(Font.BOLD);
		BOLD.setSize(FONT_SIZE);
		DOUBLE_SIZE_BOLD.setStyle(Font.BOLD);
		DOUBLE_SIZE_BOLD.setSize((int) (FONT_SIZE * 1.5));
		ITALIC.setStyle(Font.ITALIC);
		ITALIC.setSize(FONT_SIZE);
		SMALL_PRINT.setSize(FONT_SIZE);
		headerFooterFont.setSize(FONT_SIZE);
		headerFooterFont.setColor(new Color(0xaaaaaa));
	}

	protected SymDocument document;

	public BaseView(SymDocument document) {
		this.document = document;
		
		heading = new Font(BOLD);
		heading.setColor(getBrandColour());
		heading.setSize(BOLD.getSize() + 4.0f);

		subHeading = new Font(heading);
		subHeading.setSize(BOLD.getSize() + 1.0f);
	}

	protected abstract Color getBrandColour();

	protected void addRightAlignCell(String string, Table table) {
		Cell cell = new Cell(string);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);
	}

	protected void newLine(Paragraph paragraph) {
		if (paragraph != null)
			paragraph.add(Chunk.NEWLINE);
	}

	protected void addCleanPhraseAndNewLine(Paragraph paragraph, String phrase) {
		if (paragraph != null && !"".equals(paragraph) && phrase != null) {
			paragraph.add(new CleanPhrase(phrase));
			newLine(paragraph);
		}
	}

	protected void writeTotalLine(SymTable symTable, String label, Money value,
			Font font) throws BadElementException {

		symTable.addPhrase(new CleanPhrase(label, font), Element.ALIGN_LEFT);
		symTable.addPhrase(new CleanPhrase(value.toString(), font),
				Element.ALIGN_RIGHT);
	}

	protected void addEmptyRow(SymTable table, int height) {
		table.addEmptyRow(height);
	}

	protected void newLine() throws DocumentException {
		document.add(Chunk.NEWLINE);
	}

	protected Paragraph createStandardParagraph() {
		Paragraph clientAddress = new Paragraph();
		clientAddress.setLeading(13);
		clientAddress.setFont(STANDARD);
		return clientAddress;
	}

	protected void writeTitle(String meetingLogo, String titleText)
			throws BadElementException, MalformedURLException, IOException,
			DocumentException {
		Font title = new Font(heading);
		title.setSize(BOLD.getSize() + 10.0f);
		Image dartLogo = Image.getInstance(os.getWebInfClassesDirectory()
				+ "/../../static/img/" + meetingLogo);
		dartLogo.scalePercent(70f);
		Paragraph titleP = new Paragraph();
		titleP.add(new Chunk(dartLogo, 0, 0));
		titleP.add(new Phrase(titleText, title));

		document.add(titleP);

		newLine();
	}

	protected void writeMeetingDetails(Meeting meeting)
			throws DocumentException, BadElementException {
		document.add(new Paragraph(new Chunk("Meeting details", heading)));

		SymTable details = document.createTable(2);
		details.setWidthPercentage(100);
		details.setWidths(new float[] { 0.3f, 0.7f });
		details.addPhrase(new Phrase("Meeting Name", BOLD));
		details.addPhrase(new Paragraph(new Chunk(
				meeting.name == null ? "<Un-named>" : meeting.name, STANDARD)));
		details.addPhrase(new Phrase("Date and Time", BOLD));
		details.addPhrase(new Paragraph(new Chunk(meeting.startDateTime
				.getLongFormat()
				+ " - "
				+ meeting.getEndDateTime().getTimeFormat(), STANDARD)));

		if (!StringUtils.isEmpty(meeting.location)) {
			details.addPhrase(new Phrase("Location", BOLD));
			details.addPhrase(new Paragraph(new Chunk(meeting.location,
					STANDARD)));
		}
		
		if(meeting.permanentFiles.size() > 0){
			boolean firstRow = true;
			for(PermanentFile file: meeting.permanentFiles){
				if(firstRow){
					details.addPhrase(new Phrase("Attachments", BOLD));
					firstRow = false;
				}
				else
					details.addPhrase(new Phrase("", BOLD));
					Anchor anchor = new Anchor(file.getSourceFileName(), STANDARD);
					anchor.setReference(Config.getInstance().getValue("app.url") + "/support/File!download.action?id=" + file.getId());
					details.addPhrase(anchor);
			}
		}

		document.add(details);

		newLine();
	}

	protected void writeAttendees(Meeting meeting) throws DocumentException,
			BadElementException {
		document.add(new Paragraph(new Phrase(new Chunk("Attendees", heading))));
		SymTable attendees = document.createTable(2);
		attendees.setWidthPercentage(100);
		attendees.setWidths(new float[] { 0.3f, 0.7f });
		for (Guest guest : meeting.guests) {
			
			String guestName = guest.person.name;
			if( guest.person.equals(meeting.organiser))
				guestName = guestName + " (Organiser)";
			attendees.addPhrase(new Phrase(guestName, STANDARD));
			attendees.addPhrase(
					new Phrase(guest.getStatusResponse(), STANDARD),
					PdfCell.ALIGN_CENTER);
		}
		document.add(attendees);

		document.add(new Paragraph(" "));
	}

}