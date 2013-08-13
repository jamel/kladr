package org.jamel.kladr;

import java.io.File;
import java.util.TimeZone;

import com.codahale.dropwizard.Application;
import com.codahale.dropwizard.assets.AssetsBundle;
import com.codahale.dropwizard.setup.Bootstrap;
import com.codahale.dropwizard.setup.Environment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jamel.kladr.commands.ImportCommand;
import org.jamel.kladr.health.IndexHealthCheck;
import org.jamel.kladr.resources.search.SearchResource;
import org.joda.time.DateTimeZone;

/**
 * @author Sergey Polovko
 */
public class KladrApplication extends Application<KladrConfig> {

    public static void main(String[] args) throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateTimeZone.setDefault(DateTimeZone.UTC);
        new KladrApplication().run(args);
    }

    @Override
    public String getName() {
        return "kladr-service";
    }

    @Override
    public void initialize(Bootstrap<KladrConfig> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
        bootstrap.addCommand(new ImportCommand());

        ObjectMapper objectMapper = bootstrap.getObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void run(KladrConfig config, Environment environment) throws Exception {
        environment.jersey().setUrlPattern("/api/*");

        File indexDir = new File(config.getIndexPath());
        environment.jersey().register(new SearchResource(new KladrSearcher(indexDir)));
        environment.healthChecks().register("kladrIndex", new IndexHealthCheck(indexDir));
    }
}
