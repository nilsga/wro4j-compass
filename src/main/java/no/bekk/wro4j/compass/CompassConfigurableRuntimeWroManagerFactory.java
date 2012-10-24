package no.bekk.wro4j.compass;

import ro.isdc.wro.config.Context;
import ro.isdc.wro.manager.WroManager;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.model.group.Inject;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Collection;
import java.util.Properties;

public class CompassConfigurableRuntimeWroManagerFactory extends ConfigurableWroManagerFactory {

    public CompassConfigurableRuntimeWroManagerFactory() {
    }

    @Override
    protected void onAfterInitializeManager(WroManager manager) {
        ServletContext servletContext = Context.get().getServletContext();
        Collection<ResourcePreProcessor> resourcePreProcessors = manager.getProcessorsFactory().getPreProcessors();
        if (resourcePreProcessors != null) {
            for (ResourcePreProcessor pp : resourcePreProcessors) {
                if (pp instanceof CompassCssPreProcessor) {
                    CompassCssPreProcessor processor = (CompassCssPreProcessor) pp;
                    Properties props = newConfigProperties();
                    String compassBaseDir = props.getProperty("compassBaseDir");
                    String gemHome = props.getProperty("gemHome", (compassBaseDir != null ? compassBaseDir + "./gems" : null));
                    processor.setCompassBaseDir(compassBaseDir);
                    processor.setProjectBaseDir(computeProjectDir(servletContext));
                    processor.setGemHome(gemHome);
                }
            }
        }
        super.onAfterInitializeManager(manager);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private File computeProjectDir(ServletContext servletContext) {
        File candidate = new File(servletContext.getRealPath("./"));
        while (candidate != null && candidate.isDirectory()) {
            File[] files = candidate.listFiles();
            for (File f : files) {
                if (f.getName().equals("pom.xml")) {
                    return candidate;
                }
            }
            candidate = candidate.getParentFile();
        }
        throw new RuntimeException("Cannot location maven base directory");
    }

}
