package struts;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import model.PermanentFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.StreamResult;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import com.opensymphony.xwork2.ActionInvocation;

@SuppressWarnings("serial")
public class PermanentFileResult extends StrutsResultSupport {
	private static final Log log = LogFactory.getLog(StreamResult.class);

	protected int bufferSize = 1024;

	public PermanentFileResult() {
		super();
	}

	protected void doExecute(String finalLocation, ActionInvocation invocation)
			throws Exception {

		OutputStream output = null;
		InputStream input = null;
		try {
			PermanentFile permanentFile = (PermanentFile)invocation.getStack().findValue("permanentFile");
			if( permanentFile == null )
				throw new IllegalArgumentException("Could not find the permanent file");
			
			File tempFile = (File)invocation.getStack().findValue("tempFile");
			if( tempFile == null )
				throw new IllegalArgumentException("Could not find temp file");
			
			// Set headers in response
			HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);
			
			  // Set the content type
            response.setContentType(permanentFile.getSourceContentType());

            // Set the content length. This will be handled by the zip filter
            // oResponse.setContentLength

            // Set the content-disposition
            response.addHeader("Content-disposition", "filename=\"" + permanentFile.getSourceFileName() + "\"");

            // Get the outputstream
            output = response.getOutputStream();
			
			input = new FileInputStream(tempFile);
			
			//Read from input and write to output
			byte[] oBuff = new byte[bufferSize];
			int iSize;
			while (-1 != (iSize = input.read(oBuff))) {
				output.write(oBuff, 0, iSize);
			}
		} finally {
			if (input != null)
				input.close();
			if (output != null)
				output.close();
		}
	}
}
