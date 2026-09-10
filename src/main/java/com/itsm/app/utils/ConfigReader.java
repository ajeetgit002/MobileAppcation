package com.itsm.app.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

	private Properties properties;

	private static final String CONFIG_PATH = "src/test/resources/config.properties";

	public ConfigReader() {

		properties = new Properties();

		loadProperties();
	}

	// ===========================================
	// LOAD PROPERTIES
	// ===========================================

	private void loadProperties() {

		try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH)) {

			properties.load(fileInputStream);

		} catch (IOException e) {

			throw new RuntimeException("Unable to load config.properties from: " + CONFIG_PATH, e);
		}
	}

	// ===========================================
	// GET PROPERTY
	// ===========================================

	public String getProperty(String key) {

		String value = properties.getProperty(key);

		if (value == null) {

			throw new IllegalArgumentException("Property key not found: " + key);
		}

		return value;
	}

	public String getProperty(String key, String defaultValue) {

		return properties.getProperty(key, defaultValue);
	}

	// ===========================================
	// PLATFORM PROPERTIES
	// ===========================================

	public String getPlatformName() {

		return getProperty("platformName");
	}

	public String getAutomationName() {

		return getProperty("automationName");
	}

	public String getDeviceName() {

		return getProperty("deviceName");
	}

	public String getPlatformVersion() {

		return getProperty("platformVersion");
	}

	public String getUdid() {

		return getProperty("udid");
	}

	// ===========================================
	// APP PROPERTIES
	// ===========================================

	public String getAppPath() {

		return getProperty("appPath");
	}

	public String getAppPackage() {

		return getProperty("appPackage");
	}

	public String getAppActivity() {

		return getProperty("appActivity");
	}

	// ===========================================
	// RESET SETTINGS
	// ===========================================

	public boolean isNoReset() {

		return Boolean.parseBoolean(getProperty("noReset", "true"));
	}

	public boolean isFullReset() {

		return Boolean.parseBoolean(getProperty("fullReset", "false"));
	}

	// ===========================================
	// TIMEOUT SETTINGS
	// ===========================================

	public int getNewCommandTimeout() {

		return Integer.parseInt(getProperty("newCommandTimeout", "300"));
	}

	public int getExplicitWaitTimeout() {

		return Integer.parseInt(getProperty("explicitWait", "15"));
	}

	public int getAlertWaitTimeout() {

		return Integer.parseInt(getProperty("alertWait", "10"));
	}

	public int getImplicitWaitTimeout() {

		return Integer.parseInt(getProperty("implicitWait", "0"));
	}

	// ===========================================
	// APPIUM SERVER
	// ===========================================

	public String getAppiumServerUrl() {

		return getProperty("appiumServerUrl");
	}
}