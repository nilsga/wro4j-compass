package no.bekk.wro4j.compass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.isdc.wro.manager.WroManager;
import ro.isdc.wro.manager.factory.standalone.StandaloneContext;
import ro.isdc.wro.maven.plugin.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

public class CompassConfigurableWroManagerFactory extends ConfigurableWroManagerFactory  {

    public static final Logger LOG = LoggerFactory.getLogger(CompassConfigurableWroManagerFactory.class);

    private StandaloneContext standaloneContext;

    @Override
    public void initialize(StandaloneContext standaloneContext) {
        this.standaloneContext = standaloneContext;
        super.initialize(standaloneContext);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onAfterInitializeManager(WroManager manager) {
        Collection<ResourcePreProcessor> resourcePreProcessors = manager.getProcessorsFactory().getPreProcessors();
        if(resourcePreProcessors != null) {
            for(ResourcePreProcessor pp : resourcePreProcessors) {
                if(pp instanceof CompassCssPreProcessor) {
                    CompassCssPreProcessor processor = (CompassCssPreProcessor) pp;
                    Properties props = createProperties();
                    String compassBaseDir = props.getProperty("compassBaseDir");
                    String gemHome = props.getProperty("gemHome", (compassBaseDir != null ? compassBaseDir + "./gems" : null));
                    processor.setStandaloneContext(standaloneContext);
                    processor.setProjectBaseDir(computeProjectDir());
                    processor.setCompassBaseDir(compassBaseDir);
                    processor.setGemHome(gemHome);
                }
            }
        }
        super.onAfterInitializeManager(manager);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private File computeProjectDir() {
        File candidate = standaloneContext.getContextFolder();
        while(candidate != null && candidate.isDirectory()) {
            File[] files = candidate.listFiles();
            for(File f : files) {
                if(f.getName().equals("pom.xml")) {
                    LOG.info("Resolved project directory to " + candidate);
                    return candidate;
                }
            }
            candidate = candidate.getParentFile();
        }
        throw new RuntimeException("Cannot location maven base directory");
    }
}
