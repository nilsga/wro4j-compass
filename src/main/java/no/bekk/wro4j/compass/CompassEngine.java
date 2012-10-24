package no.bekk.wro4j.compass;

import org.apache.commons.io.IOUtils;
import org.jruby.CompatVersion;
import org.jruby.RubyInstanceConfig;
import org.jruby.embed.ScriptingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.util.StopWatch;

public class CompassEngine {

    public static ScriptingContainer container;
    private String compassBaseDir;
    private static CompassCompiler compiler;
    private final static Logger LOG = LoggerFactory.getLogger(CompassEngine.class);

    public CompassEngine(String compassBaseDir) {
        this.compassBaseDir = compassBaseDir;
    }

    public String process(String content, String realFileName) {
		final StopWatch stopWatch = new StopWatch();
		try {

			stopWatch.start("process compass");
            if(container == null) {
                container = new ScriptingContainer();
                container.setCompileMode(RubyInstanceConfig.CompileMode.JIT);
                container.setCompatVersion(CompatVersion.RUBY1_8);
                LOG.debug("JRuby Scripting Compile mode: " + container.getCompileMode());
                LOG.debug("JRuby supported version: " + container.getSupportedRubyVersion());
                container.put("$compass_dir", compassBaseDir);
                Object reciver = container.runScriptlet(IOUtils.toString(getClass().getResource("/wro4j_compass.rb")));
                compiler = container.getInstance(reciver, CompassCompiler.class);
            }

            return compiler.compile(compassBaseDir, content.replace("'", "\""), realFileName);

		} catch (Exception e) {

			throw new WroRuntimeException(e.getMessage(), e);

        } finally {

			stopWatch.stop();
            LOG.debug("Finished in: {}", stopWatch.getLastTaskTimeMillis());
		}
	}
}