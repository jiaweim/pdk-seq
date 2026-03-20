package pdk.seq;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pdk.util.ArgUtils.checkArgument;

/**
 * A sequence in the database.
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 16 Oct 2024, 5:06 PM
 */
public class Seq {

    private final Header header;
    private final String sequence;

    /**
     * Create a {@link Seq} with empty header {@link Header#EMPTY}
     *
     * @param sequence sequence
     */
    public Seq(String sequence) {
        this(Header.EMPTY, sequence);
    }

    /**
     * Create a {@link Seq}
     *
     * @param header   entry {@link Header}
     * @param sequence entry sequence
     */
    public Seq(Header header, String sequence) {
        this.header = header;
        this.sequence = sequence;
    }

    /**
     * Return the protein header
     *
     * @return {@link Header}
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Return protein sequence
     *
     * @return protein sequence
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * @return the primary accession number of the UniProtKB entry.
     */
    public String getAccession() {
        return header.getAccession();
    }

    public DatabaseType getDatabaseType() {
        return header.getDatabaseType();
    }

    /**
     * @return true if this seq comes from SwissProt
     */
    public boolean isReviewed() {
        return header.getDatabaseType() == DatabaseType.SwissProt;
    }

    /**
     * @return protein description
     */
    public String getDescription() {
        return header.getDescription();
    }

    public String getEntryName() {
        return header.getEntryName();
    }


    public String getProteinName() {
        return header.getProteinName();
    }

    /**
     * @return the scientific name of the organism of the UniProtKB entry.
     */
    public String getOrganismName() {
        return header.getOrganismName();
    }

    /**
     * @return the unique identifier of the source organism, assigned by the NCBI.
     */
    public String getOrganismIdentifier() {
        return header.getOrganismIdentifier();
    }

    /**
     * for TrEMBL, the gene name could be null
     *
     * @return the first gene name of the UniProtKB entry. If there is no gene name,
     * OrderedLocusName or ORFname, the GN field is not listed.
     */
    @Nullable
    public String getGeneName() {
        return header.getGeneName();
    }

    /**
     * For isoform, protein existence is null.
     *
     * @return the numerical value describing the evidence for the existence of the protein.
     */
    public @Nullable Integer getProteinExistence() {
        return header.getProteinExistence();
    }

    /**
     * For isoform, sequence version is null.
     *
     * @return the version number of the sequence.
     */
    public @Nullable Integer getSequenceVersion() {
        return header.getSequenceVersion();
    }

    /**
     * Write this sequence to the FASTA file the PrintWrite points to.
     *
     * @param writer   PrintWriter to write the file to.
     * @param maxWidth maximum line width used to get pretty formatted output
     */
    public void writeToFasta(PrintWriter writer, int maxWidth) {
        header.writeToFasta(writer);

        StringBuilder builder = new StringBuilder(sequence);
        if (builder.length() > maxWidth) {
            int offset = maxWidth;
            do {
                builder.insert(offset, "\n");
                offset += (maxWidth + 1);
            } while (offset <= builder.length());
        }
        writer.println(builder);
    }

    /**
     * This method can be used to append this Seq to the FASTA file the PrintWrite points to, with default line width 60.
     *
     * @param writer PrintWriter to write the file to.
     */
    public void writeToFasta(PrintWriter writer) {
        writeToFasta(writer, 60);
    }

    /**
     * Find a pattern in the sequence
     *
     * @param pattern {@link Pattern}
     * @return list of pattern positions
     */
    public List<Integer> findMotif(Pattern pattern) {
        ArrayList<Integer> indexes = new ArrayList<>();
        Matcher matcher = pattern.matcher(sequence);
        while (matcher.find()) {
            indexes.add(matcher.start());
        }
        return indexes;
    }

    /**
     * Return a sub sequence
     *
     * @param start start index
     * @param end   end index
     * @return sub-seq
     * @since 2025-04-09 ⭐
     */
    public String subSeq(int start, int end) {
        return sequence.substring(start, end);
    }

    /**
     * Return sub seq of the protein sequence
     *
     * @param index       index in protein
     * @param width       window width
     * @param replaceChar char used if there is no char og given length.
     * @return sub string
     */
    public String subSeq(int index, int width, char replaceChar) {
        return subSeq(index, width, width, replaceChar);
    }

    /**
     * return substring of the protein sequence.
     *
     * @param index       index in the protein.
     * @param left        length before index
     * @param right       length after index
     * @param replaceChar char to use if there is no char of given length.
     * @return the sub string.
     */
    public String subSeq(int index, int left, int right, char replaceChar) {
        int len = left + right + 1;
        char[] result = new char[len];

        int lId = index - left;
        int lCount = 0;
        if (lId < 0) {
            lCount -= lId;
            lId = 0;
        }

        int rId = index + right + 1;
        int rCount = 0;
        if (rId > sequence.length()) {
            rCount = rId - sequence.length();
            rId = sequence.length();
        }

        if (lCount > 0)
            Arrays.fill(result, 0, lCount, replaceChar);

        int num = lCount;
        for (int id = lId; id < rId; id++) {
            result[num] = sequence.charAt(id);
            num++;
        }

        if (rCount > 0)
            Arrays.fill(result, num, len, replaceChar);

        return new String(result);
    }

    /**
     * @return length of the sequence
     */
    public int length() {
        return sequence.length();
    }

    /**
     * Return residue at given index
     *
     * @param index index in the protein
     * @return residue
     * @since 2025-04-09 ⭐
     */
    public char getChar(int index) {
        return sequence.charAt(index);
    }

    /**
     * Return true if this Seq pass the filter
     *
     * @param filter {@link SeqFilter} instance
     * @return true if this {@link Seq} pass the filter
     */
    public boolean test(SeqFilter filter) {
        return filter.test(this);
    }

    /**
     * Return true if this {@link Seq} pass all filters
     *
     * @param filters {@link SeqFilter}s
     * @return true if pass filters
     */
    public boolean test(Collection<SeqFilter> filters) {
        boolean value = true;
        for (SeqFilter filter : filters) {
            if (!filter.test(this)) {
                value = false;
                break;
            }
        }
        return value;
    }

    /**
     * Return true if this seq contain given sub sequence
     *
     * @param subSeq sub-sequence
     * @return true if contains the sub-sequence
     */
    public boolean contains(String subSeq) {
        return sequence.contains(subSeq);
    }

    /**
     * Get the indexes of given sub sequence
     *
     * @param subSeq  sub sequence to search
     * @param ignoreX true if 'X' in the sequence can match any residues
     * @return indexes of sub sequence, an empty array if cannot find the sub sequence
     */
    public int[] indexOf(String subSeq, boolean ignoreX) {
        IntArrayList list = new IntArrayList();
        int index;
        int start = 0;
        while ((index = sequence.indexOf(subSeq, start)) != -1) {
            list.add(index);
            start = index + subSeq.length();
        }

        if (ignoreX && (sequence.charAt(0) == 'X')) {
            if (sequence.substring(1).startsWith(subSeq.substring(1))) {
                list.add(0);
            }
        }

        return list.toIntArray();
    }

    /**
     * Count the number of occurrences of difference amino acid residues
     *
     * @return count of all characters in this sequence ⭐⭐
     */
    public Multiset<Character> getCharacterCount() {
        Multiset<Character> aaCount = HashMultiset.create(30);

        for (int i = 0; i < sequence.length(); i++) {
            char aa = sequence.charAt(i);
            aaCount.add(aa);
        }
        return aaCount;
    }

    /**
     * Random sample substring from this sequence
     *
     * @param length sample length
     * @param count  sample count
     * @return list of substring
     */
    public List<String> sample(int length, int count) {
        checkArgument(length <= length());

        Random random = new Random();
        int N = length() - length;
        ArrayList<String> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int start = random.nextInt(N);
            list.add(sequence.substring(start, start + length));
        }
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seq seq)) return false;

        return header.equals(seq.header) && sequence.equals(seq.sequence);
    }

    @Override
    public int hashCode() {
        int result = header.hashCode();
        result = 31 * result + sequence.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(sequence);
        if (builder.length() > 60) {
            int offset = 60;
            do {
                builder.insert(offset, "\n");
                offset += (60 + 1);
            } while (offset <= builder.length());
        }

        return header.toString() + "\n" + builder;
    }
}