package reflectx.mapping;

import javax.annotation.Nullable;

import reflectx.annotations.Source;
import reflectx.annotations.SourceName;

/**
 * @author zpp0196
 */
public class SourceMapping {

    public static final long DEFAULT_VERSION = Source.DEFAULT_VERSION;

    public long version;
    public String value;
    public long identifies;

    public SourceMapping(long version, String value, @Nullable Long identifies) {
        this.version = version;
        this.value = value;
        this.identifies = identifies == null ? Source.DEFAULT_IDENTIFIES : identifies;
    }

    public SourceMapping(Source source) {
        this(source.version(), source.value(), source.identifies());
    }

    public SourceMapping(SourceName sourceName) {
        this(sourceName.version(), sourceName.value(), sourceName.identifies());
    }

    public boolean isDefaultIdentifies() {
        return identifies == Source.DEFAULT_IDENTIFIES;
    }
}
