package org.jamel.kladr.health;

import java.io.File;

import com.codahale.metrics.health.HealthCheck;

/**
 * @author Sergey Polovko
 */
public class IndexHealthCheck extends HealthCheck {

    private final File indexDir;


    public IndexHealthCheck(File indexDir) {
        this.indexDir = indexDir;
    }

    @Override
    protected Result check() throws Exception {
        if (indexDir.canRead()) return Result.healthy();
        return Result.unhealthy("Can't read from " + indexDir);
    }
}
