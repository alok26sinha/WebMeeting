package view;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import itext.SymDocument;
import itext.SymTable;
import model.BaseDescriptiveModel;
import model.Guest;
import model.ShiftMeeting;
import model.StartingIdea;
import model.Traction;

import org.springframework.context.ApplicationContext;

import type.StringUtils;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.hyphenation.TernaryTree.Iterator;

import dao.ForesightTractionDao;
import dao.StartingIdeaTractionDao;

public class ShiftFollowUpReport extends BaseView {

	private ShiftMeeting shiftMeeting;
	private ApplicationContext context;

	public ShiftFollowUpReport(SymDocument document, ShiftMeeting shiftMeeting,
			ApplicationContext context) {
		super(document);
		this.shiftMeeting = shiftMeeting;
		this.context = context;
	}

	public void render() throws DocumentException, IOException {

		writeTitle("logo-type-shift.png", " SHIFT Follow-up meeting report");

		writeMeetingDetails(shiftMeeting);

		writeAttendees(shiftMeeting);

		writeActionItems();

		writeShiftSection("Starting Ideas", shiftMeeting.startingIdeas,
				shiftMeeting.staringIdeaParkedThoughts);

		writeShiftSection("Hindsight", shiftMeeting.hindsights,
				shiftMeeting.hindsightParkedThoughts);

		writeShiftSection("Insight", shiftMeeting.insights,
				shiftMeeting.insightParkedThoughts);

		writeShiftSection("Foresight", shiftMeeting.foresights,
				shiftMeeting.foresightParkedThoughts);

		writeFreeStandingParkedThoughts("Traction Parked Thoughts",
				shiftMeeting.tractionParkedThoughts);

		writeFreeStandingParkedThoughts("Key Outputs Parked Thoughts",
				shiftMeeting.keyOutputParkedThoughts);

	}

	private void writeFreeStandingParkedThoughts(String headingText,
			String parkedThoughts) throws DocumentException {
		if (!StringUtils.isEmpty(parkedThoughts)) {
			document.add(new Paragraph(new Chunk(headingText, subHeading)));
			document.add(new Paragraph(parkedThoughts, STANDARD));
			document.add(new Paragraph(" "));
		}

	}

	protected void writeActionItems() throws DocumentException,
			BadElementException {
		document.add(new Paragraph(new Chunk("Action Items", heading)));
		SymTable itemsTable = document.createTable(5);
		itemsTable.setWidthPercentage(100);
		itemsTable.setWidths(new float[] { 0.36f, 0.15f, 0.15f, 0.15f, 0.15f });

		ForesightTractionDao foresightTractionDao = context
				.getBean(ForesightTractionDao.class);
		for (Traction traction : foresightTractionDao.getAll(shiftMeeting)) {
			itemsTable.addPhrase(new Phrase(traction.description, STANDARD));
			itemsTable.addPhrase(new Phrase(traction.getDueDateString(),
					STANDARD));
			if (traction.personResponsible != null
					&& traction.personResponsible.name != null) {
				itemsTable.addPhrase(new Phrase(
						traction.personResponsible.name, STANDARD));
			} else {
				itemsTable.addPhrase(new Phrase(" ", STANDARD));
			}
			itemsTable.addPhrase(new Phrase(traction.getStatusString(),
					STANDARD));
			itemsTable.addPhrase(new Phrase(traction.comments, STANDARD));
		}
		StartingIdeaTractionDao startingIdeaTractionDao = context
				.getBean(StartingIdeaTractionDao.class);
		for (Traction traction : startingIdeaTractionDao.getAll(shiftMeeting)) {
			itemsTable.addPhrase(new Phrase(traction.description, STANDARD));
			itemsTable.addPhrase(new Phrase(traction.getDueDateString(),
					STANDARD));
			if (traction.personResponsible != null
					&& traction.personResponsible.name != null) {
				itemsTable.addPhrase(new Phrase(
						traction.personResponsible.name, STANDARD));
			} else {
				itemsTable.addPhrase(new Phrase(" ", STANDARD));
			}
			itemsTable.addPhrase(new Phrase(traction.getStatusString(),
					STANDARD));
			itemsTable.addPhrase(new Phrase(traction.comments, STANDARD));
		}
		document.add(itemsTable);
		document.add(new Paragraph(" "));
	}

	protected void writeShiftSection(String headingText,
			List<?> descriptiveModels, String parkedThoughts)
			throws DocumentException, BadElementException {
		if (!StringUtils.isEmpty(parkedThoughts)
				|| descriptiveModels.size() > 0) {
			document.add(new Paragraph(new Chunk(headingText, heading)));
			if (descriptiveModels.size() > 0) {
				SymTable descriptionTable = document.createTable(1);
				descriptionTable.setWidthPercentage(100);
				descriptionTable.setWidths(new float[] { 1.0f });
				java.util.Iterator<?> iterator = descriptiveModels.iterator();
				while (iterator.hasNext()) {
					Object o = iterator.next();
					System.out.println("####"
							+ ((BaseDescriptiveModel) o).description);
					if (o instanceof BaseDescriptiveModel)
						descriptionTable.addPhrase(new Phrase(
								((BaseDescriptiveModel) o).description,
								STANDARD));
				}
				document.add(descriptionTable);
			}

			if (!StringUtils.isEmpty(parkedThoughts)) {
				document.add(new Paragraph(new Chunk("Parked Thoughts",
						subHeading)));
				document.add(new Paragraph(parkedThoughts, STANDARD));
			}
			document.add(new Paragraph(" "));
		}
	}

	@Override
	protected Color getBrandColour() {
		return new Color(0x33CCFF);
	}

}
