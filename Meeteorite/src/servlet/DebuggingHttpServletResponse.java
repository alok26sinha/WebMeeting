package servlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class DebuggingHttpServletResponse extends HttpServletResponseWrapper {

	private StringBuilder headers = new StringBuilder();

	String getHeaders() {
		return headers.toString();
	}

	public DebuggingHttpServletResponse(HttpServletResponse response) {
		super(response);
		headers.append("Response\n");
	}

	@Override
	public void sendError(int sc, java.lang.String msg)
			throws java.io.IOException {
		headers.append("\n  sendError statusCode:" + sc + " msg:" + msg);

		super.sendError(sc, msg);
	}

	@Override
	public void sendError(int sc) throws java.io.IOException {
		headers.append("\n  sendError statusCode:" + sc);

		super.sendError(sc);
	}

	@Override
	public void setDateHeader(java.lang.String name, long date) {
		headers.append("\n  setDateHeader name:" + name + " date:" + dateAsUtc(date));
		super.setDateHeader(name, date);
	}

	@Override
	public void addDateHeader(java.lang.String name, long date) {
		headers.append("\n  addDateHeader name:" + name + " date:" + dateAsUtc(date));
		super.setDateHeader(name, date);
	}

	private String dateAsUtc(long millis) {
		Date date = new Date(millis);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss z");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	@Override
	public void setHeader(java.lang.String name, java.lang.String value) {
		headers.append("\n  setHeader name:" + name + " value:" + value);
		super.setHeader(name, value);
	}

	@Override
	public void addHeader(java.lang.String name, java.lang.String value) {
		headers.append("\n  addHeader name:" + name + " value:" + value);
		super.setHeader(name, value);
	}

	@Override
	public void setIntHeader(java.lang.String name, int value) {
		headers.append("\n  setIntHeader name:" + name + " value:" + value);
		super.setIntHeader(name, value);
	}

	@Override
	public void addIntHeader(java.lang.String name, int value) {
		headers.append("\n  setIntHeader name:" + name + " value:" + value);
		super.setIntHeader(name, value);
	}

	@Override
	public void setStatus(int sc) {
		headers.append("\n  setStatus :" + sc);
		super.setStatus(sc);
	}

	@Override
	@Deprecated
	public void setStatus(int sc, String msg) {
		headers.append("\n  setStatus :" + sc);
		super.setStatus(sc, msg);
	}
}
