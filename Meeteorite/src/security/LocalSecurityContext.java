package security;

import common.UncheckedException;

public class LocalSecurityContext {
	private static ThreadLocal<SecurityContext> allContexts = new ThreadLocal<SecurityContext>();

	public static void set(SecurityContext securityContext) {
		allContexts.set(securityContext);
	}

	public static SecurityContext get() {
		SecurityContext context = allContexts.get();
		{
			if (context != null) {
				return context;
			} else {
				throw new UncheckedException("No local security context set");
			}
		}
	}

	public static boolean hasConextSet() {
		SecurityContext context = allContexts.get();
		if (context != null) {
			return true;
		} else
			return false;
	}

	public static void clear() {
		allContexts.set(null);
	}
}
