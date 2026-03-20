package pdk.seq.io;

import pdk.seq.Header;
import pdk.seq.Seq;
import pdk.util.exception.PDKRuntimeException;
import pdk.util.io.FileUtils;
import pdk.util.io.IterateReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

/**
 * Fasta file reader
 *
 * <ul>
 *     <li>2026-03-19: reuse {@link StringBuilder} to improve performance</li>
 * </ul>
 *
 * @author Jiawei Mao
 * @version 1.1.0⭐
 * @since 16 Oct 2024
 */
public final class FastaReader implements IterateReader<Seq> {

    /**
     * Return the number of sequence in the fasta file.
     *
     * @param file a fasta file
     * @return number of sequences in the file
     * @since 2024-11-25 ⭐
     */
    public static int getSeqCount(Path file) throws IOException {
        int n = 0;
        try (FastaReader reader = new FastaReader(file)) {
            while (reader.hasNext()) {
                reader.next();
                n++;
            }
        }
        return n;
    }



    private final boolean detailMode_;
    private final LineNumberReader reader_;

    /**
     * current header line
     */
    private String headerLine_ = null;
    /**
     * Reuse for improved performance.
     */
    private StringBuilder sb_ = new StringBuilder();

    /**
     * Create a {@link FastaReader}
     *
     * @param file file path
     */
    public FastaReader(final Path file) throws IOException {
        this(file, false);
    }

    /**
     * Create a FastaReader
     *
     * @param file       {@link java.nio.file.Path} instance, support .fasta or .fasta.gz format.
     * @param detailMode whether to extract all field values from the protein header line.
     */
    public FastaReader(final Path file, boolean detailMode) throws IOException {
        this.detailMode_ = detailMode;
        if (FileUtils.endWith(file, ".gz")) {
            this.reader_ = new LineNumberReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(file))));
        } else {
            this.reader_ = new LineNumberReader(Files.newBufferedReader(file));
        }
        init();
    }

    /**
     * Create a {@link FastaReader} from {@link Reader} instance
     *
     * @param reader     {@link Reader}
     * @param detailMode true if extract all possible information in protein header
     */
    public FastaReader(Reader reader, boolean detailMode) throws IOException {
        this.reader_ = new LineNumberReader(reader);
        this.detailMode_ = detailMode;
        init();
    }

    public FastaReader(InputStream inputStream, boolean detailMode) throws IOException {
        this.detailMode_ = detailMode;
        this.reader_ = new LineNumberReader(new InputStreamReader(inputStream));
        init();
    }

    /**
     * Skip until the first protein header line.
     */
    private void init() throws IOException {
        while ((headerLine_ = reader_.readLine()) != null) {
            if (headerLine_.startsWith(">"))
                break;
        }
    }

    @Override
    public void close() throws IOException {
        sb_ = null;
        headerLine_ = null;
        reader_.close();
    }

    @Override
    public boolean hasNext() {
        return headerLine_ != null;
    }

    @Override
    public Seq next() {
        String header = headerLine_;
        headerLine_ = null;
        sb_.setLength(0);
        try {
            String line;
            while ((line = reader_.readLine()) != null) {
                if (line.startsWith(">")) { // find next header line, stop
                    headerLine_ = line;
                    break;
                }
                sb_.append(line.trim());
            }
            return new Seq(Header.of(header, detailMode_), sb_.toString());
        } catch (IOException e) {
            throw new PDKRuntimeException("Error in reading line " + reader_.getLineNumber());
        }
    }
}
