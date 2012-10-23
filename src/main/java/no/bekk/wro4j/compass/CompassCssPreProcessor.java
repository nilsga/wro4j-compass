package no.bekk.wro4j.compass;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.manager.factory.standalone.StandaloneContext;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;
import ro.isdc.wro.model.resource.SupportedResourceType;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;
import ro.isdc.wro.util.Base64;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@SupportedResourceType(ResourceType.CSS)
public class CompassCssPreProcessor implements ResourcePreProcessor {

	public static final String ALIAS = "compassCss";
    private StandaloneContext standaloneContext;
    private String compassBaseDir;
    private File projectBaseDir;
    private Map<String, CacheElem> resourceCache = new HashMap<String, CacheElem>();

    private static final Logger LOG = LoggerFactory.getLogger(CompassCssPreProcessor.class);

    @Override
	public void process(Resource resource, Reader reader, Writer writer) throws IOException {

        String realFileName = computePath(resource);
        final String content = IOUtils.toString(reader);
		try {
            String hash = computeHash(content);
            String result;
            if(!resourceCache.containsKey(realFileName)) {
                resourceCache.put(realFileName, new CacheElem(hash));
            }
            CacheElem cacheElem = resourceCache.get(realFileName);
            if(cacheElem.getResult() == null || !cacheElem.getContentHash().equals(hash)) {
                LOG.debug(realFileName + " needs refresh");
                result =  new CompassEngine(computeCompassBaseDir()).process(content, computePath(resource));
                cacheElem.setContentHash(hash);
                cacheElem.setResult(result);
            }
            else {
                LOG.debug(realFileName + " has not been modified");
                result = cacheElem.getResult();
            }
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


    public static String computeHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return Base64.encodeBytes(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private class CacheElem {
        private String contentHash;
        private String result;

        public CacheElem(String contentHash) {
            this.contentHash = contentHash;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }

        public String getContentHash() {
            return contentHash;
        }

        public void setContentHash(String contentHash) {
            this.contentHash = contentHash;
        }
    }
}
