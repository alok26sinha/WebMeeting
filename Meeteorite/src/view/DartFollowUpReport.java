package view;

import itext.SymDocument;
import itext.SymTable;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;

import model.AgendaItem;
import model.DartItem;
import model.DartMeeting;
import type.StringUtils;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

public class DartFollowUpReport extends BaseView {

	DartMeeting dartMeeting;

	public DartFollowUpReport(SymDocument document, DartMeeting dartMeeting) {
		super(document);
		this.dartMeeting = dartMeeting;
	}

	public void render() throws DocumentException, MalformedURLException,
			IOException {

		writeTitle("logo-type-dart.png", " DART Follow-up meeting report");

		writeMeetingDetails(dartMeeting);

		writeAttendees(dartMeeting);

		int itemNo = 0;
		for (AgendaItem agendaItem : dartMeeting.agendaItems) {
			
			SymTable headingsTable = document.createTable(2);
			headingsTable.setWidthPercentage(100);
			headingsTable.setWidths(new float[] { 0.60f, 0.40f });
			
			headingsTable.addPhrase(new Paragraph(new Chunk("Agenda Item " + (++itemNo) + ". "
					+ agendaItem.description, heading)));
			
			if(agendaItem.itemOwner !=  null)
				headingsTable.addPhrase(new Paragraph(new Chunk("Agenda Item Owner: " + agendaItem.itemOwner.name,
						subHeading)));
			else
				headingsTable.addPhrase(new Paragraph(new Chunk("Agenda Item Owner: All",
						subHeading)));
				
			document.add(headingsTable);
			
			if (!StringUtils.isEmpty(agendaItem.subAgendaItem)) {
				document.add(new Paragraph("Sub Agenda Items", BOLD));
				document.add(new Paragraph(agendaItem.subAgendaItem, STANDARD));
			}
			
			if (!StringUtils.isEmpty(agendaItem.discussionPoint)) {
				document.add(new Paragraph(new Chunk("Discussion points",
						subHeading)));
				document.add(new Paragraph(agendaItem.discussionPoint, STANDARD));
			}
			
			if (!agendaItem.dartItems.isEmpty()) {
				document.add(new Paragraph(
						new Chunk("Action items", subHeading)));
				SymTable itemsTable = document.createTable(5);
				itemsTable.setWidthPercentage(100);
				itemsTable.setWidths(new float[] { 0.37f, 0.20f, 0.15f, 0.15f,
						0.23f });
				itemsTable.addPhrase(new Phrase("Action", BOLD));
				itemsTable.addPhrase(new Phrase("Responsibility", BOLD));
				itemsTable.addPhrase(new Phrase("Timing", BOLD));
				itemsTable.addPhrase(new Phrase("Status", BOLD));
				itemsTable.addPhrase(new Phrase("Comments", BOLD));

				for (DartItem dartItem : agendaItem.dartItems) {
					itemsTable.addPhrase(new Phrase(dartItem.action, STANDARD));
					itemsTable.addPhrase(new Phrase(
							dartItem.responsiblePerson.name, STANDARD));
					itemsTable.addPhrase(new Phrase(dartItem.timing
							.getDayMonthYearFormat(), STANDARD));
					itemsTable.addPhrase(new Phrase(dartItem.getStatusString(),
							STANDARD));
					itemsTable
							.addPhrase(new Phrase(dartItem.comment, STANDARD));
				}
				document.add(itemsTable);
			}
			else{
				document.add(new Paragraph(new Chunk("No Action Required",
						ITALIC)));
			}
			if (!StringUtils.isEmpty(agendaItem.parkedThoughts)) {
				document.add(new Paragraph(" "));
				document.add(new Paragraph(new Chunk("Parked Thoughts",
						subHeading)));
				document.add(new Paragraph(agendaItem.parkedThoughts, STANDARD));
			}
			document.add(new Paragraph(" "));
		}

		if (!StringUtils.isEmpty(dartMeeting.agendaReviewParkedThoughts)) {
			document.add(new Paragraph(" "));
			document.add(new Paragraph(new Chunk("Agenda Review Parked Thoughts",
					heading)));
			document.add(new Paragraph(dartMeeting.agendaReviewParkedThoughts, STANDARD));
		}
		
		if (!StringUtils.isEmpty(dartMeeting.agendaReviewParkedThoughts)) {
			document.add(new Paragraph(" "));
			document.add(new Paragraph(new Chunk("Meeting Summary Parked Thoughts",
					heading)));
			document.add(new Paragraph(dartMeeting.agendaReviewParkedThoughts, STANDARD));
		}

	}

	@Override
	protected Color getBrandColour() {
		return new Color(0x037f0c);
	}
}
