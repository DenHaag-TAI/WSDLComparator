package nl.denhaag.tw.util;

/*
 * #%L
 * wsdl-comparator
 * %%
 * Copyright (C) 2012 - 2013 Team Webservices (Gemeente Den Haag)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Utility class for all TWB projects.
 * 
 * 
 */
public class Util {
	private static final String FORBIDDEN_CHARACTERS_REPLACEMENT = "_";
	private static final String FORBIDDEN_CHARACTERS_REGEX = "[\\\\/:*?\"<>\\|]";
	private static final String UNKNOWN_VERSION = "unknown version";
	private static final String UNKNOWN_VENDOR = "unknown vendor";
	private static final String UNKNOWN_TITLE = "unknown title";
	private static Logger LOGGER = LogManager.getLogger(Util.class);
	public static final String SEPARATOR = "/";

	/**
	 * Load a property file and handles the exception
	 * 
	 * @param file
	 *            File location
	 * @return Properties object
	 */
	public static Properties loadProperties(File file) {
		Properties properties = new Properties();
		try {
			FileInputStream fis = new FileInputStream(file);
			properties.load(fis);
			fis.close();
		} catch (IOException ioe) {
			LOGGER.info(ioe.getMessage());
		}
		return properties;
	}

	/**
	 * Store properties to a file
	 * 
	 * @param file
	 *            File to store
	 * @param properties
	 *            Properties object
	 * @param title
	 *            Title of the properties file
	 * @throws IOException
	 */
	public static void storeProperties(File file, Properties properties, String title) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		if (title == null) {
			properties.store(fos, "");
		} else {
			properties.store(fos, title);
		}

		fos.flush();
		fos.close();
	}


	/**
	 * Retrieve the name of the library from the meta-inf file.
	 * 
	 * @param clazz Class of the library
	 * @return Name of the library
	 */
	public static String getImplementationTitle(Class<?> clazz) {
		return getImplementationTitle(clazz, UNKNOWN_TITLE);
	}

	/**
	 * Retrieve the vendor of the library from the meta-inf file.
	 * 
	 * @param clazz Class of the library
	 * @return Vendor of the library
	 */
	public static String getImplementationVendor(Class<?> clazz) {
		return getImplementationVendor(clazz, UNKNOWN_VENDOR);
	}
	
	/**
	 * Retrieve the version of the library from the meta-inf file.
	 * 
	 * @param clazz Class of the library
	 * @return Version of the library
	 */
	public static String getImplementationVersion(Class<?> clazz) {
		return getImplementationVersion(clazz, UNKNOWN_VERSION);
	}

	private static String getImplementationTitle(Class<?> clazz, String defaultString) {
		String title = clazz.getPackage().getImplementationTitle();
		if (StringUtils.isBlank(title)) {
			title = defaultString;
		}
		return title;
	}

	private static String getImplementationVendor(Class<?> clazz, String defaultString) {
		String vendor = clazz.getPackage().getImplementationVendor();
		if (StringUtils.isBlank(vendor)) {
			vendor = defaultString;
		}
		return vendor;
	}

	private static String getImplementationVersion(Class<?> clazz, String defaultString) {
		String version = clazz.getPackage().getImplementationVersion();
		if (StringUtils.isBlank(version)) {
			version = defaultString;
		}
		return version;
	}

}
