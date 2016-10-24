package controller.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import common.Config;

import security.NoAuthenticationRequired;
import subsystems.mail.MailMessage;
import subsystems.mail.OutboundService;

@NoAuthenticationRequired
@SuppressWarnings("serial")
public class RegisterAction extends BaseAction {

	public String execute() {

		OutboundService outboundService = new OutboundService();

		MailMessage mailMessage = new MailMessage();
		mailMessage.setFrom("admin@meeteorite.com");
		mailMessage.setTo(Config.getInstance().getValue("send.registration.email"));
		mailMessage.setSubject("Meeteorite Website Registration");

		mailMessage.setContent(getParameters(request));

		outboundService.send(mailMessage);

		return redirect("http://www.meeteorite.com");
	}

	private String getParameters(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append("<table>");

		List<String> keys = new ArrayList<String>();
		Enumeration<?> nameEnumeration = request.getParameterNames();
		while (nameEnumeration.hasMoreElements()) {
			String key = (String) nameEnumeration.nextElement();
			keys.add(key);
		}

		Collections.sort(keys);
		for (String key : keys) {
			if (!("x".equalsIgnoreCase(key) || "y".equalsIgnoreCase(key))) {
				builder.append("<tr><td>" + key + "</td>");
				String[] values = request.getParameterValues(key);

				builder.append("<td>");
				for (int i = 0; i < values.length; i++) {
					builder.append(values[i]);
				}
				builder.append("</td></tr>");
			}
		}
		builder.append("</table>");
		return builder.toString();
	}
}
