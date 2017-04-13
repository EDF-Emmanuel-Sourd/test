package fr.edf.soa.commons.ant;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author es0b390n
 *
 */
public class AntTest {
	private final static Logger log = Logger.getLogger(AntTest.class);

	private String basedir;

	@Before
	public void init() {
		basedir = System.getProperty("user.dir");
	}

	@After
	public void destroy() {
	}

	@Test
	public void test() {
		runAntTarget("test");
	}

	@Test
	public void testError() {
		try {
			runAntTarget("test-error");
		} catch (final BuildException e) {
			log.info("error expected, test passed.");
		}
	}

	@Test
	public void testJavaNoArgs() {
		runAntTarget("test-java-noargs");
	}

	@Test
	public void testJava() {
		runAntTarget("test-java");
	}

	@Test
	public void testJavaFailure() {
		runAntTarget("test-java-failure");
	}

	@Test
	public void testJavaFork() {
		runAntTarget("test-java-fork");
	}

	@Test
	public void testJavaForkFailure() {
		runAntTarget("test-java-fork-failure");
	}

	@Test
	public void testJavaSpawn() {
		runAntTarget("test-java-spawn");
	}

	@Test
	public void testJavaSpawnFailure() {
		runAntTarget("test-java-spawn-failure");
	}

	protected void runAntTarget(String targetName) {
		Properties properties = new Properties();
		properties.setProperty("basedir", basedir);

		AntUtils.callAnt("src/test/resources/ant/run.xml", targetName,
				properties);
	}
}
