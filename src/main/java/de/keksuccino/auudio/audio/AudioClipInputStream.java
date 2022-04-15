package de.keksuccino.auudio.audio;

import de.keksuccino.auudio.util.UrlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class AudioClipInputStream extends InputStream {

    private static final Logger LOGGER = LogManager.getLogger("auudio/AudioClipInputStream");

    protected InputStream parentStream;

    public String source;
    public AudioClip.SoundType type;

    public AudioClipInputStream(InputStream parentStream, String source, AudioClip.SoundType type) {
        this.parentStream = parentStream;
        this.source = source;
        this.type = type;
    }

    @Override
    public int read() throws IOException {
        return this.parentStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        LOGGER.info("############### " + this);
        if ((this.type == AudioClip.SoundType.EXTERNAL_WEB) && !UrlUtils.isValidUrl(this.source)) {
            return -1;
        }
        return this.parentStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.parentStream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return this.parentStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return this.parentStream.available();
    }

    @Override
    public void close() throws IOException {
        this.parentStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.parentStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.parentStream.reset();
    }

    @Override
    public boolean markSupported() {
        return this.parentStream.markSupported();
    }

}
