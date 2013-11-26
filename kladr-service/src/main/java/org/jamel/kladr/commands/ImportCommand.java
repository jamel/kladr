package org.jamel.kladr.commands;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Stopwatch;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.io.FileUtils;
import org.jamel.j7zip.ArchiveExtractCallback;
import org.jamel.j7zip.RafInputStream;
import org.jamel.j7zip.Result;
import org.jamel.j7zip.archive.IInArchive;
import org.jamel.j7zip.archive.sevenZip.Handler;
import org.jamel.kladr.KladrConfig;
import org.jamel.kladr.KladrIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sergey Polovko
 */
public class ImportCommand extends ConfiguredCommand<KladrConfig> {

    private static final Logger logger = LoggerFactory.getLogger(ImportCommand.class);


    public ImportCommand() {
        super("import", "Importing and indexing KLADR database from .7z archive");
    }

    @Override
    protected void run(Bootstrap<KladrConfig> bootstrap, Namespace namespace, KladrConfig config) throws Exception {
        File kladrDir = null;

        try {
            kladrDir = extractArchive(config);
            new KladrIndexer(kladrDir, new File(config.getIndexPath())).doIndexing();
        } finally {
            if (kladrDir != null) {
                FileUtils.deleteDirectory(kladrDir);
            }
        }
    }

    private File extractArchive(KladrConfig config) {
        Stopwatch stopwatch = new Stopwatch().start();
        try {
            File outDir = getEmptyOutDir(config.getExtractPath());

            IInArchive archive = new Handler();
            if (archive.open(new RafInputStream(config.getArchivePath(), "r")) != Result.OK) {
                logger.error("Can't open KLADR archive file {}", config.getArchivePath());
            }

            archive.extract(null, new ArchiveExtractCallback(outDir));
            return outDir;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            logger.debug("Extracting kladr tooks " + stopwatch.stop());
        }
    }

    private File getEmptyOutDir(String extractPath) throws IOException {
        File outDir = new File(extractPath);
        FileUtils.deleteDirectory(outDir);
        FileUtils.forceMkdir(outDir);
        return outDir;
    }

}
