package no.bekk.wro4j.compass;

import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;
import ro.isdc.wro.model.resource.processor.support.ProcessorProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CompassProcessorProvider implements ProcessorProvider {

    @Override
    public Map<String, ResourcePreProcessor> providePreProcessors() {
        Map<String, ResourcePreProcessor> map = new HashMap<String, ResourcePreProcessor>();
        map.put(CompassCssPreProcessor.ALIAS, new CompassCssPreProcessor());
        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, ResourcePostProcessor> providePostProcessors() {
        return Collections.EMPTY_MAP;
    }
}
