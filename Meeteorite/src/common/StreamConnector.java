package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StreamConnector {
	private static Log log = LogFactory.getLog(StreamConnector.class);
	private static int BUFFER_SIZE = 1024;

	public void pipe(File inFile, File outFile) {
		try {
			InputStream input = new FileInputStream(inFile);
			OutputStream output = new FileOutputStream(outFile);
			pipe(input, output);
			
		} catch (IOException e) {
			throw new UncheckedException("IO exception piping content", e);
		}

	}

	public void pipe(InputStream input, File outFile) {
		try {
			OutputStream output = new FileOutputStream(outFile);
			pipe(input, output);
			
		} catch (IOException e) {
			throw new UncheckedException("IO exception piping content", e);
		}

	}
	
	public void pipe(InputStream input, OutputStream output) {
		try {
			// Read from input and write to output
			byte[] oBuff = new byte[BUFFER_SIZE];
			int iSize;
			while (-1 != (iSize = input.read(oBuff))) {
				output.write(oBuff, 0, iSize);
			}

		} catch (IOException e) {
			throw new UncheckedException("IO exception piping content", e);
		} finally {
			try {
				if (input != null)
					input.close();
				if (output != null)
					output.close();
			} catch (IOException e) {
				log.warn("Failed to close streams. " + e.getMessage());
			}

		}
	}
}
