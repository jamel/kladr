package org.jamel.kladr;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

/**
 * @author Sergey Polovko
 */
public class KladrConfig extends Configuration {

    @NotNull
    @JsonProperty
    private String archivePath;

    @NotNull
    @JsonProperty
    private String extractPath;

    @NotNull
    @JsonProperty
    private String indexPath;


    public String getArchivePath() {
        return archivePath;
    }

    public String getExtractPath() {
        return extractPath;
    }

    public String getIndexPath() {
        return indexPath;
    }
}
