package no.bekk.wro4j.compass;

import org.apache.commons.io.IOUtils;
import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.util.StopWatch;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CompassEngine {

    public static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByName("jruby");
    private String compassBaseDir;

    public CompassEngine(String compassBaseDir) {
        this.compassBaseDir = compassBaseDir;
    }

    public String process(String content, String realFileName) {
		final StopWatch stopWatch = new StopWatch();
		try {

			stopWatch.start("process compass");

			return ENGINE.eval(buildUpdateScript(content, realFileName)).toString();

		} catch (Exception e) {

			throw new WroRuntimeException(e.getMessage(), e);

        } finally {

			stopWatch.stop();
		}
	}

	private String buildUpdateScript(String content, String realFileName) throws IOException {
		final StringWriter raw = new StringWriter();
		final PrintWriter script = new PrintWriter(raw);

        script.println("ENV['GEM_HOME'] = './.gems'");
        String compassScript = IOUtils.toString(getClass().getResource("/wro4j_compass.rb"));

        script.println(compassScript);
        script.println("cmd = Compass::Commands::UpdateProject.new('" + compassBaseDir + "', {:sass_files => '" + realFileName + "'})");
        script.println("compiler = cmd.new_compiler_instance");
		script.println("compiler.compile_string('" + content.replace("'", "\"") + "', '" + realFileName + "')");

		return raw.toString();
	}
}
