package subsystems.operatingsystem;

/**
 * Holds the results of calling an OS routine
 */
public class ExecutionResults {

	private int resultCode;
	private byte[] error;
	private byte[] output;
	
	@Override
	public String toString(){
		return "Result code:" + resultCode + "\nOutput:" + getOutputString() + "\nError:" + error + getErrorString();
	}
	
	public boolean isSuccess(){
		return resultCode == 0;
	}
	
	//Getters and setters
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public String getErrorString() {
		return new String(error);
	}
	public void setError(byte[] error) {
		this.error = error;
	}
	public String getOutputString() {
		return new String(output);
	}
	public byte[] getOutputByteArray(){
		return output;
	}
	public void setOutput(byte[] output) {
		this.output = output;
	}
	
	
}
