package view;

import subsystems.operatingsystem.OperatingSystem;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import common.UncheckedException;

public class PdfHeaderFooter extends PdfPageEventHelper {

	protected static final OperatingSystem os = new OperatingSystem();

	public void onEndPage(PdfWriter writer, Document document) {

		try {
			PdfPTable header = new PdfPTable(2);
			header.setWidths(new int[] { 24, 24 });
			header.setTotalWidth(450);
			header.setLockedWidth(true);
			header.getDefaultCell().setFixedHeight(20);
			header.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			header.addCell("");
			header.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
			header.addCell(new Paragraph("Company Logo Here", BaseView.headerFooterFont));
			header.writeSelectedRows(0, -1, 70, 803, writer.getDirectContent());

			PdfPTable footer = new PdfPTable(2);
			footer.setWidths(new int[]{24, 24});
			footer.setTotalWidth(450);
			footer.setLockedWidth(true);
			footer.getDefaultCell().setFixedHeight(40);
			footer.getDefaultCell().setBorder(Rectangle.NO_BORDER);

			PdfPCell footerCell = new PdfPCell();
			footerCell.setBorder(Rectangle.NO_BORDER);
			footerCell.addElement(new Paragraph(
					"Meeting run with Meeteorite\u2122", BaseView.headerFooterFont));
			footerCell.addElement(new Paragraph(
					"The world's No.1 meeting productivity software",
					BaseView.headerFooterFont));
			String domainUrl = common.Config.getInstance().getValue("app.url")
					+ "/reportLink.jsp";
			Anchor anchor = new Anchor(
					"All Rights Reserved. \u00A9 Meeteorite http://www.meeteorite.com",
					BaseView.headerFooterFont);
			anchor.setReference(domainUrl);
			footerCell.addElement(new Paragraph(anchor));
	
			footer.addCell(footerCell);
			footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);

			Image logo = Image.getInstance(os.getWebInfClassesDirectory()
					+ "/../../static/img/logo_meeteorite_black.jpg");
			logo.scalePercent(70f);

			logo.setAnnotation(new Annotation(0, 0, 0, 0, domainUrl));

			footer.addCell(logo);
			footer.writeSelectedRows(0, -1, 70, 75, writer.getDirectContent());

		} catch (Throwable de) {
			throw new UncheckedException("Failed to write header report", de);
		}
	}

}
