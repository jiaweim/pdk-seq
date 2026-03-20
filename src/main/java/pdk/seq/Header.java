package pdk.seq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pdk.seq.io.UniProtFastaHeader;
import pdk.util.ICopy;

import java.io.PrintWriter;

/**
 * Protein sequence header in the database
 *
 * @author Jiawei Mao
 * @version 1.1.0
 * @since 02 Jul 2024, 1:37 PM
 */
public class Header implements ICopy<Header> {

    /**
     * empty header instance
     */
    public static final Header EMPTY = new Header("", "");

    /**
     * Create {@link Header} of given text
     *
     * @param txt        full header text (including `>`)
     * @param detailMode true if parse header text in detail mode
     * @return {@link Header} instance
     */
    public static Header of(String txt, boolean detailMode) {
        if (detailMode) {
            UniProtFastaHeader fastaHeader;
            try {
                fastaHeader = new UniProtFastaHeader(txt);
            } catch (Exception e) {
                return new Header(txt);
            }

            return fastaHeader;
        } else {
            return new Header(txt);
        }
    }

    protected String accession_;
    protected String description_;

    /**
     * Create a {@link Header}
     *
     * @param accession   protein accession number
     * @param description protein description
     */
    public Header(String accession, String description) {
        this.accession_ = accession;
        this.description_ = description;
    }

    /**
     * Take the part before the space as the accession, and the part after the space as the description.
     *
     * @param line header line with the leading '>'
     */
    public Header(String line) {
        int idx = line.indexOf(" ");
        if (idx > 0) {
            accession_ = line.substring(1, idx);
            description_ = line.substring(idx + 1);
        } else {
            accession_ = line.substring(1);
            description_ = "";
        }
    }

    public Header() {}

    /**
     * write this header to a fasta file.
     */
    public void writeToFasta(PrintWriter writer) {
        if (description_.isBlank()) {
            writer.println(">" + accession_);
        } else {
            writer.println(">" + accession_ + " " + description_);
        }
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.Unknown;
    }

    /**
     * @return the primary accession number of the UniProtKB entry.
     */
    public String getAccession() {
        return accession_;
    }

    /**
     * Set the primary accession-number
     *
     * @param accession primary accession number
     */
    public void setAccession(String accession) {
        this.accession_ = accession;
    }

    /**
     * @return protein description
     */
    public @NotNull String getDescription() {
        return description_;
    }

    public void setDescription(String description) {
        this.description_ = description;
    }

    /**
     * Return the protein entry name. If detail mode is not enabled, return null.
     *
     * @return protein entry name
     */
    public @Nullable String getEntryName() {
        return null;
    }

    /**
     * Return the protein name. If detail mode is not enabled, return null.
     *
     * @return protein name
     */
    public @Nullable String getProteinName() {
        return null;
    }

    /**
     * If detail mode is not enabled, return null.
     *
     * @return the scientific name of the organism of the UniProtKB entry.
     */
    public String getOrganismName() {
        return null;
    }

    /**
     * If detail mode is not enabled, return null.
     *
     * @return the unique identifier of the source organism, assigned by the NCBI.
     */
    public String getOrganismIdentifier() {
        return null;
    }

    /**
     * for TrEMBL, the gene name could be null
     *
     * @return the first gene name of the UniProtKB entry. If there is no gene name,
     * OrderedLocusName or ORFname, the GN field is not listed.
     */
    public @Nullable String getGeneName() {
        return null;
    }

    /**
     * For isoform, protein existence is null.
     *
     * @return the numerical value describing the evidence for the existence of the protein.
     */
    public @Nullable Integer getProteinExistence() {
        return null;
    }

    /**
     * The sequence version number of an entry is incremented by one when the amino acid sequence shown in
     * the sequence record is modified; For isoform, sequence version is null.
     *
     * @return the sequence version number
     */
    @Nullable
    public Integer getSequenceVersion() {
        return null;
    }

    @Override
    public Header copy() {
        return new Header(accession_, description_);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Header header)) return false;

        return accession_.equals(header.accession_);
    }

    @Override
    public int hashCode() {
        return accession_.hashCode();
    }

    @Override
    public String toString() {
        if (description_.isBlank()) {
            return ">" + accession_;
        } else {
            return ">" + accession_ + " " + description_;
        }
    }
}
