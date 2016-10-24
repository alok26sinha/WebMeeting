package common;

@SuppressWarnings("serial")
public class UncheckedException extends RuntimeException {
    Throwable source;

    public UncheckedException(String s) {
        super(s);
    }

    public UncheckedException(String s, Throwable e) {
        super(s + " Caused by: " + e.getMessage());
        source = e;
    }

    public UncheckedException(Throwable e) {
        super(e.getMessage());
        source = e;
    }

    public void printStackTrace() {
        super.printStackTrace();

        if (source != null) {
            System.err.println("Caused by: ");
            source.printStackTrace();
        }
    }

    public void printStackTrace(java.io.PrintStream ps) {
        super.printStackTrace(ps);

        if (source != null) {
            ps.println("Caused by: ");
            source.printStackTrace(ps);
        }
    }

    public void printStackTrace(java.io.PrintWriter pw) {
        super.printStackTrace(pw);

        if (source != null) {
            pw.println("Caused by: ");
            source.printStackTrace(pw);
        }
    }
}
