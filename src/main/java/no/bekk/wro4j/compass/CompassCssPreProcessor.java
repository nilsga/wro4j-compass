package no.bekk.wro4j.compass;

import org.apache.commons.io.IOUtils;
import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.manager.factory.standalone.StandaloneContext;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;
import ro.isdc.wro.model.resource.SupportedResourceType;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

@SupportedResourceType(ResourceType.CSS)
public class CompassCssPreProcessor implements ResourcePreProcessor {

	public static final String ALIAS = "compassCss";
    private StandaloneContext standaloneContext;
    private String compassBaseDir;

    private File projectBaseDir;

    @Override
	public void process(Resource resource, Reader reader, Writer writer) throws IOException {

		final String content = IOUtils.toString(reader);
		
		try {
			String result =  new CompassEngine(computeCompassBaseDir()).process(content, computePath(resource));
			writer.write(result);
		} catch (final WroRuntimeException e) {
			onException(e);
//		  final String resourceUri = resource == null ? StringUtils.EMPTY : "[" + resource.getUri() + "]";
//		  LOG.warn("Exception while applying " + getClass().getSimpleName() + " processor on the " + resourceUri
//			  + " resource, no processing applied...", e);
		} finally {
			reader.close();
			writer.close();
		}

	}

	private void onException(WroRuntimeException e) {
		e.printStackTrace();
	}


    public void setStandaloneContext(StandaloneContext standaloneContext) {
        this.standaloneContext = standaloneContext;
    }

    public void setCompassBaseDir(String compassBaseDir) {
        this.compassBaseDir = compassBaseDir;
    }

    public void setProjectBaseDir(File file) {
        this.projectBaseDir = file;
    }

    private String computePath(Resource resource) {
        return new File(standaloneContext != null ? standaloneContext.getContextFolder() : projectBaseDir, resource.getUri()).getAbsolutePath();
    }

    private String computeCompassBaseDir() {
        if(compassBaseDir == null) {
            return projectBaseDir.getAbsolutePath();
        }
        else {
            return new File(projectBaseDir, compassBaseDir).getAbsolutePath();
        }
    }
}
