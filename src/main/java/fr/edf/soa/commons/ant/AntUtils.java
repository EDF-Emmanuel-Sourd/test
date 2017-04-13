package fr.edf.soa.commons.ant;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.DemuxOutputStream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 * convenient tools for Ant
 * 
 * @author ES0B390N
 * 
 */
public class AntUtils {
	private final static Logger log = Logger.getLogger(AntUtils.class);

	/**
	 * calls an Ant target in a build file, with the specified properties. build
	 * file is loaded from the <code>buildFileName</code> parameter as absolute
	 * file path, and if not found, searched in this class classloader.
	 * 
	 * @param buildFileName
	 * @param targetName
	 * @param projectProperties
	 */
	public static void callAnt(String buildFileName, String targetName,
			Properties projectProperties) {
		// define project file
		File buildFile = new File(buildFileName);
		if (!buildFile.exists()) {
			// load from classloader
			log.info("trying to load ant build file [" + buildFileName
					+ "] from classloader.");
			URL resource = AntUtils.class.getResource(buildFileName);
			log.info("found url in classloader [" + resource + "].");
			if (resource != null)
				try {
					buildFile = new File(resource.toURI());
				} catch (URISyntaxException e) {
					throw new IllegalArgumentException("ant build file ["
							+ buildFileName + "] could not be found.", e);
				}
			if (!buildFile.exists())
				throw new IllegalArgumentException("ant build file ["
						+ buildFileName + "] could not be found.");
		}
		AntUtils.callAnt(buildFile, targetName, projectProperties);
	}

	/**
	 * calls an Ant target in a build file, with the specified properties.
	 * 
	 * @param buildFile
	 * @param targetName
	 * @param projectProperties
	 */
	public static void callAnt(File buildFile, String targetName,
			Properties projectProperties) {
		log.info("loading target [" + targetName + "] in ant build file ["
				+ buildFile.getAbsolutePath() + "] with properties ["
				+ projectProperties + "].");

		Project p = new Project();
		// define project file
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		// configure project
		configureProject(p, projectProperties);
		// execute target
		executeTarget(p, buildFile, targetName);
	}

	/**
	 * calls an Ant target with the specified properties, from the input stream
	 * of a build file
	 * 
	 * @param buildFileStream
	 * @param targetName
	 * @param projectProperties
	 */
	public static void callAnt(InputStream buildFileStream, String targetName,
			Properties projectProperties) {
		log.info("loading target [" + targetName
				+ "] from ant file inpustream with properties ["
				+ projectProperties + "].");

		Project p = new Project();
		// configure project
		configureProject(p, projectProperties);
		// execute target
		executeTarget(p, buildFileStream, targetName);
	}

	private static void executeTarget(Project p, Object source,
			String targetName) throws BuildException {
		// backup streams
		PrintStream out = System.out;
		PrintStream err = System.err;
		try {
			configureListener(p);

			p.fireBuildStarted();
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, source);
			p.executeTarget(targetName);
			p.fireBuildFinished(null);
		} catch (BuildException e) {
			p.fireBuildFinished(e);
			throw e;
		} finally {
			// restore streams
			System.setOut(out);
			System.setErr(err);
		}
	}

	private static void configureProject(Project p, Properties projectProperties) {
		// set properties
		if (projectProperties != null) {
			for (String key : projectProperties.stringPropertyNames()) {
				p.setProperty(key, projectProperties.getProperty(key));
			}
		}
	}

	private static void configureListener(Project p) {
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		if (log.isTraceEnabled())
			consoleLogger.setMessageOutputLevel(Project.MSG_DEBUG);
		else if (log.isDebugEnabled())
			consoleLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
		else
			consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		System.setOut(new PrintStream(new DemuxOutputStream(p, false)));
		System.setErr(new PrintStream(new DemuxOutputStream(p, true)));
		p.addBuildListener(consoleLogger);
	}
}
