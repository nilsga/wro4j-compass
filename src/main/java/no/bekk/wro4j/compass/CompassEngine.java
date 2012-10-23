package no.bekk.wro4j.compass;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.util.StopWatch;

import javax.script.*;

public class CompassEngine {

    public static Invocable engine = null;
    private String compassBaseDir;
    private final static Logger LOG = LoggerFactory.getLogger(CompassEngine.class);


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
            LOG.debug("Finished in: " + stopWatch.getLastTaskTimeMillis());
        }
	}
}