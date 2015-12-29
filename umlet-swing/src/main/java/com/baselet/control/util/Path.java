package com.baselet.control.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.baselet.control.constants.SystemInfo;
import com.baselet.control.enums.Os;
import com.baselet.control.enums.Program;
import com.baselet.element.interfaces.GridElement;

public class Path {

	private static String tempDir;
	private static String homeProgramDir;

	public static boolean hasOsConformConfig() {
		File file = new File(osConformConfig());
		return file.exists();
	}

	public static String osConformConfig() {
		String programConfigDir = combine(osConformConfigDirectory(), Program.getInstance().getProgramName());
		ensureDirectoryIsExisting(programConfigDir);

		return combine(programConfigDir, Program.getInstance().getConfigName());
	}

	@Deprecated // #273: legacy cfg is read for some versions; should be removed in v15 or sooner (only use osConformConfig() instead)
	public static boolean hasLegacyConfig() {
		File file = new File(legacyConfig());
		return file.exists();
	}

	@Deprecated // #273: legacy cfg is read for some versions; should be removed in v15 or sooner (only use osConformConfig() instead)
	public static String legacyConfig() {
		String programConfigDirectory = combine(userHomeDirectory(), Program.getInstance().getProgramName());

		return combine(programConfigDirectory, Program.getInstance().getConfigName());
	}

	private static void ensureDirectoryIsExisting(String path) {
		File file = new File(path);
		if (!file.exists()) {
			Utils.safeMkDir(file, true);
		}
	}

	private static String osConformConfigDirectory() {
		String configDir = userHomeDirectory();

		if (SystemInfo.OS == Os.WINDOWS) {
			configDir = windowsConfigDirectory();
		}
		else if (SystemInfo.OS == Os.MAC) {
			configDir = macOSXConfigDirectory();
		}
		else if (SystemInfo.OS == Os.LINUX || SystemInfo.OS == Os.UNIX) {
			configDir = xgdConfigDirectory();
		}

		return configDir;
	}

	private static String windowsConfigDirectory() {
		String configPath = System.getenv("LOCALAPPDATA");
		if (configPath == null) {
			configPath = userHomeDirectory();
		}

		return configPath;
	}

	private static String macOSXConfigDirectory() {
		return combine(userHomeDirectory(), "Library/Preferences");
	}

	private static String xgdConfigDirectory() {
		String configPath = System.getenv("XDG_CONFIG_HOME");
		if (configPath == null) {
			configPath = combine(userHomeDirectory(), ".config");
		}

		return configPath;
	}

	private static String userHomeDirectory() {
		return System.getProperty("user.home");
	}

	private static String combine(String path, String childPath) {
		return new File(path, childPath).getPath();
	}

	public static String customElements() {
		return homeProgram() + "custom_elements/";
	}

	public static String temp() {
		if (tempDir == null) {
			String tmp = System.getProperty("java.io.tmpdir");
			if (!tmp.endsWith(File.separator)) {
				tmp = tmp + File.separator;
			}
			tempDir = tmp;
		}
		return tempDir;
	}

	/**
	 * <ul>
	 * <li>{@literal STANDALONE NOJAR: <programpath>}</li>
	 * <li>{@literal STANDALONE JAR: <programpath>}</li>
	 * <li>{@literal ECLIPSE NOJAR: <programpath>}</li>
	 * <li>{@literal ECLIPSE JAR: <eclipse-path>/<configuration>/<dirToStoreCustomStuff>}</li>
	 * </ul>
	 */
	public static String homeProgram() {
		return homeProgramDir;
	}

	public static void setHomeProgram(String homeProgramDir) {
		Path.homeProgramDir = homeProgramDir;
	}

	/**
	 * <ul>
	 * <li>{@literal STANDALONE NOJAR: <umlet-swing-path>/target/classes/}</li>
	 * <li>{@literal STANDALONE JAR: <program-path>/<progname>.jar}</li>
	 * <li>{@literal ECLIPSE NOJAR: <umlet-eclipse-plugin-path>}</li>
	 * <li>{@literal ECLIPSE JAR: <eclipse-path>/<pluginname>.jar}</li>
	 * </ul>
	 */
	public static String executable() {
		return executableHelper(Path.class);
	}

	public static String executableShared() {
		return executableHelper(GridElement.class);
	}

	private static String executableHelper(Class<?> c) {
		String path = null;
		URL codeSourceUrl = c.getProtectionDomain().getCodeSource().getLocation();
		try { // Convert URL to URI to avoid HTML problems with special characters like space,ä,ö,ü,...
			path = codeSourceUrl.toURI().getPath();
		} catch (URISyntaxException e) {/* path stays null */}

		if (path == null) { // URI2URL Conversion failed, because URI.getPath() returned null OR because of an URISyntaxException
			// In this case use the URL and replace special characters manually (for now only space)
			path = codeSourceUrl.getPath().replace("%20", " ");
		}

		return path;
	}
}
