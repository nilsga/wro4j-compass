package no.bekk.wro4j.compass;

import org.apache.commons.io.IOUtils;
import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.util.StopWatch;

import javax.script.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CompassEngine {

    public static Invocable engine = null;
    private String compassBaseDir;

    public CompassEngine(String compassBaseDir) {
        this.compassBaseDir = compassBaseDir;
    }

    public String process(String content, String realFileName) {
		final StopWatch stopWatch = new StopWatch();
		try {

			stopWatch.start("process compass");
            if(engine == null) {
                ScriptEngine se = new ScriptEngineManager().getEngineByName("jruby");
                Bindings b = se.getBindings(ScriptContext.GLOBAL_SCOPE);
                b.put("compass_dir", compassBaseDir);
                se.eval(IOUtils.toString(getClass().getResource("/wro4j_compass.rb")), b);
                engine = (Invocable) se;
            }

            return engine.invokeFunction("compile_compass", content.replace("'", "\""), realFileName).toString();

		} catch (Exception e) {

			throw new WroRuntimeException(e.getMessage(), e);

        } finally {

			stopWatch.stop();
            System.out.println("Finished in: " + stopWatch.getLastTaskTimeMillis());
		}
	}

	private String buildUpdateScript(String content, String realFileName) throws IOException {
		final StringWriter raw = new StringWriter();
		final PrintWriter script = new PrintWriter(raw);

        script.println("ENV['GEM_HOME'] = '" + compassBaseDir + "/.gem'");
        String compassScript = IOUtils.toString(getClass().getResource("/wro4j_compass.rb"));

        script.println(compassScript);
        script.println("cmd = Compass::Commands::UpdateProject.new('" + compassBaseDir + "', {:sass_files => '" + realFileName + "'})");
        script.println("compiler = cmd.new_compiler_instance");
		script.println("compiler.compile_string('" + content.replace("'", "\"") + "', '" + realFileName + "')");

		return raw.toString();
	}

    
    public static void main(String[] args) throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("jruby");
        CompiledScript sc = ((Compilable)engine).compile("e = $a\ne");
        Bindings bindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
        bindings.put("a", "Test");
        System.out.println(sc.eval(bindings));
    }
}