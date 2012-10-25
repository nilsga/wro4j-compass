package no.bekk.wro4j.compass;

import ro.isdc.wro.config.Context;
import ro.isdc.wro.config.metadata.MetaDataFactory;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CompassConfigurableRuntimeWroManagerFactory extends ConfigurableWroManagerFactory {

    @Override
    protected MetaDataFactory newMetaDataFactory() {
        final MetaDataFactory parent = super.newMetaDataFactory();
        final Map<String, Object> parentProps = parent.create();
        Properties props = newConfigProperties();
        String compassBaseDir = props.getProperty("compassBaseDir");
        final CompassSettings compassSettings = new CompassSettings();
        compassSettings.setProjectBaseDir(computeProjectDir());
        compassSettings.setGemHome(props.getProperty("gemHome", (compassBaseDir != null ? compassBaseDir + "./gems" : null)));
        compassSettings.setCompassBaseDir(compassBaseDir);
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
        File candidate = new File(Context.get().getServletContext().getRealPath("./"));
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
