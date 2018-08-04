package knowledge.representation.utils;

import com.google.common.base.MoreObjects;
import com.google.common.io.Resources;

import java.net.URL;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * @author Administrator
 */
public class UrlUtil {
    /**
     * @param resourceName
     * @return
     */
    public static URL getResource(String resourceName) {
        ClassLoader loader = MoreObjects.firstNonNull(
                Thread.currentThread().getContextClassLoader(),
                Resources.class.getClassLoader());
        URL url = loader.getResource(resourceName);
        checkArgument(url != null, "resource %s not found.", resourceName);
        return url;
    }
}
