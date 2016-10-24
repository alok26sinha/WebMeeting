package security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that no authentication is required to run an action
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NoAuthenticationRequired {

}
