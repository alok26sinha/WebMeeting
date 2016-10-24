package webapp;

import itext.SymDocument;
import itext.SymHtmlWriter;
import itext.SymPdfWriter;
import itext.SymRtfWriter2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DartMeeting;
import model.Meeting;
import model.ShiftMeeting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.springframework.web.context.support.WebApplicationContextUtils;

import type.StringUtils;
import view.DartFollowUpReport;
import view.ShiftFollowUpReport;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import common.UncheckedException;

@SuppressWarnings("serial")
public class ItextResultType extends StrutsResultSupport {
	private final transient Log log = LogFactory.getLog(ItextResultType.class);

	public static final String DEFAULT_PARAM = "property";
	private String property;

	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation)
			throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String presentationType = request.getParameter("presentationType");
		
		if(StringUtils.isEmpty(presentationType))
			presentationType = "pdf";

		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ property + "." + presentationType + "\"");

		SymDocument document;
		if ("pdf".equals(presentationType)) {
			document = new SymDocument(true);
			response.setContentType("application/pdf");
			SymPdfWriter.getInstance(document, response.getOutputStream());
		} else if ("html".equals(presentationType)) {
			document = new SymDocument(false);
			response.setContentType("text/html");
			SymHtmlWriter.getInstance(document, response.getOutputStream());
		} else if ("rtf".equals(presentationType)) {
			document = new SymDocument(false);
			response.setContentType("text/rtf");
			SymRtfWriter2.getInstance(document, response.getOutputStream());
		} else {
			throw new UncheckedException("Unknown presentation type:" + presentationType);
		}

		document.open();

		if ("FollowUpReport".equals(property)) {
			ValueStack stack = invocation.getStack();
			Meeting meeting = (Meeting) stack.findValue("meeting");
			if (meeting instanceof DartMeeting) {
    			DartFollowUpReport report = new DartFollowUpReport(document, (DartMeeting) meeting);
    			report.render();
			} else if (meeting instanceof ShiftMeeting) {
			    ShiftFollowUpReport report = new ShiftFollowUpReport(document, (ShiftMeeting) meeting,
			            WebApplicationContextUtils.getWebApplicationContext(ServletActionContext.getServletContext()));
			    report.render();
			}
		} /*else if ("Estimate".equals(property)
				|| "EstimateDetails".equals(property)
				|| "EstimateDetailsAjax".equals(property)) {
			ValueStack stack = invocation.getStack();
			Job job = (Job) stack.findValue("job");
			EstimateView estimateView = new EstimateView(document, job);
			estimateView.render();
		}*/ else {
			throw new UncheckedException(
					"Cannot build output. Not a known output:" + property);
		}

		document.close();

	}

	public void setProperty(String property) {
		this.property = property;
	}

}
