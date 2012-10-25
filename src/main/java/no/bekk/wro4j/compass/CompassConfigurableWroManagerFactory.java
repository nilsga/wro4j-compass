package no.bekk.wro4j.compass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.isdc.wro.config.metadata.MetaDataFactory;
import ro.isdc.wro.manager.factory.standalone.StandaloneContext;
import ro.isdc.wro.maven.plugin.manager.factory.ConfigurableWroManagerFactory;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CompassConfigurableWroManagerFactory extends ConfigurableWroManagerFactory  {

    public static final Logger LOG = LoggerFactory.getLogger(CompassConfigurableWroManagerFactory.class);

    private StandaloneContext standaloneContext;

    @Override
    public void initialize(StandaloneContext standaloneContext) {
        this.standaloneContext = standaloneContext;
        super.initialize(standaloneContext); 
    }

    @Override
    protected MetaDataFactory newMetaDataFactory() {

        final MetaDataFactory parent = super.newMetaDataFactory();
        final Map<String, Object> parentProps = parent.create();
        Properties props = createProperties();
        String compassBaseDir = props.getProperty("compassBaseDir");
        final CompassSettings compassSettings = new CompassSettings();
        compassSettings.setProjectBaseDir(computeProjectDir());
        compassSettings.setGemHome(props.getProperty("gemHome", (compassBaseDir != null ? compassBaseDir + "./gems" : null)));
        compassSettings.setCompassBaseDir(compassBaseDir);
        compassSettings.setStandaloneContext(standaloneContext);
        return new MetaDataFactory() {

            private Map<String, Object> map = new HashMap<String, Object>();

            {
                map.putAll(parentProps);
                map.put("compassSettings", compassSettings);
            }

            @Override
            public Map<String, Object> create() {
                return Collections.unmodifiableMap(map);
            }
        };
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
