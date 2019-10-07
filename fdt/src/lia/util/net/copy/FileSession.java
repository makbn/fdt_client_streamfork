/*
 * $Id$
 */
package lia.util.net.copy;

import lia.util.net.common.FileChannelProvider;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the FDT wrapper over the FileChannel which performs the I/O operations
 *
 * @author ramiro
 */
public abstract class FileSession extends IOSession {

    public static final String DEV_NULL_FILENAME = "/dev/null";
    public static final String DEV_ZERO_FILENAME = "/dev/zero";
    private static final Logger logger = Logger.getLogger(FileSession.class.getName());
    public final AtomicLong cProcessedBytes = new AtomicLong(0);
    protected final boolean isLoop;
    protected final FDTSession fdtSession;
    protected final boolean isNull;
    protected final boolean isZero;
    protected final FileChannelProvider fileChannelProvider;
    protected volatile String fileName;
    protected volatile FileChannel fileChannel;
    protected volatile File file;
    protected volatile int partitionID;
    protected volatile long lastModified;

    public FileSession(UUID uid, FDTSession fdtSession, String fileName, boolean isLoop,
                       FileChannelProvider fileChannelProvider) {
        super(uid, -1);
        this.fdtSession = fdtSession;

        boolean bNull = false;
        boolean bZero = false;
        File iFile = null;

        this.fileChannelProvider = fileChannelProvider;

        try {
            this.isLoop = isLoop;

            //some checks
            if (fileName == null) {
                throw new NullPointerException("The fileName cannot be null");
            }

            iFile = new File(fileName);
            this.fileName = fileName;

            this.lastModified = iFile.lastModified();

            if (fileName.startsWith(DEV_NULL_FILENAME)) {
                iFile = new File(DEV_NULL_FILENAME);
                bNull = true;
                return;
            }

            if (fileName.startsWith(DEV_ZERO_FILENAME)) {
                iFile = new File(DEV_ZERO_FILENAME);
                bZero = true;
                return;
            }
        } finally {
            file = iFile;
            isNull = bNull;
            isZero = bZero;
        }
    }

    public abstract FileChannel getChannel() throws Exception;

    public int partitionID() {
        return partitionID;
    }

    public long lastModified() {
        return lastModified;
    }

    public final boolean isNull() {
        return isNull;
    }

    public final boolean isZero() {
        return isZero;
    }

    public File getFile() {
        return file;
    }

    public final boolean isLoop() {
        return isLoop;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    protected void internalClose() {
        final FileChannel fileChannel = this.fileChannel;
        if (fileChannel != null) {
            try {
                if (!isLoop) {
                    fileChannel.close();
                }
            } catch (Throwable t) {
                logger.log(Level.WARNING, " Got exception closing file " + file, t);
            }
        }
    }

    public String fileName() {
        return fileName;
    }

    /**
     * @param fileName
     * @throws IOException
     */
    public void setFileName(String fileName) throws IOException {
        this.fileName = fileName;
        this.file = new File(fileName);
    }
}
