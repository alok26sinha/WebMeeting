package common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Singleton class (managed by spring) that provides the interface to the
 * software configuration.
 * 
 * All configuration is set in two files: enviroment.properties and
 * setting.properties.
 * 
 * When the code runs in a servlet container the servlet context is defined.
 * This allows us to put settings somewhere within the context directory
 * structure. But when running the junit test straight from the IDE the context
 * directory is /netbeans/bin for netbeans. So to keep things simple the class
 * loader is used to load the config files from the root of the class path. This
 * is consistent both in the IDE and the servlet container.
 * 
 */
public class Config {
	private static Log log = LogFactory.getLog(Config.class);
	private static Config instance;
	private PropertiesFile environment;
	private PropertiesFile local;
	private PropertiesFile global;

	protected Config() {
		environment = new PropertiesFile("environment.properties", 10000);
		local = new PropertiesFile("local.properties", 20);
		global = new PropertiesFile("global.properties", 20);
	}

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}

		return instance;
	}

	/**
	 * Get the value of this key from settings.
	 * 
	 * Look first for environment specific values and then default.
	 */
	public String getValue(String key) {
		// First find the environment we are working in
		String env = getEnvironment();

		return getValue(key, env);
	}

	public String getValue(String key, String env) {
		if (log.isDebugEnabled())
			log.debug("Looking up : " + key + " in environment : " + env);

		// Look in local
		String value = getValue(local, key, env);

		if (value == null || value.equals(""))
			value = getValue(global, key, env);

		if (value == null) {
			throw new ConfigException("Could not find the value for "
					+ " key : " + key
					+ " in either local.properties or global.properties");
		}

		return value;
	}

	private String getValue(PropertiesFile propertiesFile, String key,
			String env) {
		// Lookup environment specific values
		String value = propertiesFile.get(env + "." + key);

		// If not set use default
		if (value == null) {

			if (log.isDebugEnabled())
				log.debug("Did not find environment specific value.");

			value = propertiesFile.get("default." + key);
		}

		if (log.isDebugEnabled())
			log.debug("Found : " + value);

		return value;

	}

	public int getValueInt(String key) {
		String stringValue = getValue(key);

		try {
			return Integer.parseInt(stringValue);
		} catch (NumberFormatException e) {
			throw new ConfigException("Setting for " + key + " needs to be "
					+ "an integer. Value : " + stringValue);
		}
	}

	public long getValueLong(String key) {
		String stringValue = getValue(key);

		try {
			return Long.parseLong(stringValue);
		} catch (NumberFormatException e) {
			throw new ConfigException("Setting for " + key + " needs to be "
					+ "an integer. Value : " + stringValue);
		}
	}

	public String getEnvironment() {
		String env = environment.getManditory("environment");
		env = env.trim();

		return env;
	}

	public boolean isProductionEnvironment() {
		String environment = getEnvironment();
		return "pro".equals(environment);
	}

	public boolean isDevelopmentEnvironment() {
		String environment = getEnvironment();
		return "dev".equals(environment);
	}

	public boolean isTestEnvironment() {
		String environment = getEnvironment();
		return "tst".equals(environment);
	}
}
