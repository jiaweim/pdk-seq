package pdk.seq.io;

import org.jetbrains.annotations.Nullable;
import pdk.seq.DatabaseType;
import pdk.seq.Header;
import pdk.util.exception.PDKRuntimeException;

import java.io.PrintWriter;

/**
 * Uniprot FASTA header format:
 * <pre>
 * >db|UniqueIdentifier|EntryName ProteinName OS=OrganismName OX=OrganismIdentifier [GN=GeneName ]PE=ProteinExistence SV=SequenceVersion
 * </pre>
 * <p>
 * and the format of isoform:
 *
 * <pre>
 *     >sp|IsoID|EntryName Isoform IsoformName of ProteinName OS=OrganismName OX=OrganismIdentifier[ GN=GeneName]
 * </pre>
 *
 * <p>
 * For format information about UniProt FASTA files, please refer to: <a href="https://www.uniprot.org/help/fasta-headers">UniProt</a>.
 *
 * <ul>
 *     <li>2026-03-19: fix bug in {@link #writeToFasta(PrintWriter)}, as the 'PE' and 'SV' fields are not present in isoforms</li>
 * </ul>
 *
 * @author Jiawei Mao
 * @version 1.1.0⭐
 * @since 02 Jul 2024, 1:46 PM
 */
public class UniProtFastaHeader extends Header {

    private DatabaseType databaseType_;
    private String entryName_;
    private String proteinName_;
    private String organismName_;
    private String organismIdentifier_;
    private String geneName_;
    private Integer proteinExistence_;
    private Integer sequenceVersion_;

    /**
     * Create {@link UniProtFastaHeader} with fasta header line
     *
     * @param line fasta protein header line including leading `>`
     */
    public UniProtFastaHeader(String line) {
        // remove ">"
        String txt = line.trim().substring(1);

        if (txt.startsWith("sp")) {
            this.databaseType_ = DatabaseType.SwissProt;
        } else if (txt.startsWith("tr")) {
            this.databaseType_ = DatabaseType.TrEMBL;
        } else {
            throw new PDKRuntimeException("Not UnkProtKB header: " + txt);
        }

        int idx1 = txt.indexOf("|");
        int idx2 = txt.indexOf("|", idx1 + 1);

        this.accession_ = txt.substring(idx1 + 1, idx2);
        this.description_ = txt.substring(idx2 + 1);

        txt = txt.substring(idx2 + 1);

        int osIdx = txt.indexOf("OS=");  //
        int oxIdx = txt.indexOf("OX=");
        int gnIdx = txt.indexOf("GN=");  // optional
        int peIdx = txt.indexOf("PE=");  // optional, isoform
        int svIdx = txt.indexOf("SV=");  // optional, isoform

        int end = txt.length();
        if (svIdx > 0) { // not present in isoform
            this.sequenceVersion_ = Integer.parseInt(txt.substring(svIdx + 3, end));
            end = svIdx - 1; // space
        }

        if (peIdx > 0) { // not present in isoform
            this.proteinExistence_ = Integer.parseInt(txt.substring(peIdx + 3, end));
            end = peIdx - 1;
        }

        if (gnIdx > 0) { // optional
            this.geneName_ = txt.substring(gnIdx + 3, end);
            end = gnIdx - 1;
        }

        if (oxIdx > 0) {
            this.organismIdentifier_ = txt.substring(oxIdx + 3, end);
            end = oxIdx - 1;
        }

        if (osIdx > 0) {
            this.organismName_ = txt.substring(osIdx + 3, end);
            end = osIdx - 1;
        }

        int spaceIndex = txt.indexOf(" ");
        this.entryName_ = txt.substring(0, spaceIndex);
        this.proteinName_ = txt.substring(spaceIndex + 1, end);
    }

    public UniProtFastaHeader() {}

    @Override
    public void writeToFasta(PrintWriter writer) {
        StringBuilder sb = new StringBuilder();
        sb.append(">");
        if (databaseType_ == DatabaseType.SwissProt) {
            sb.append("sp|");
        } else {
            sb.append("tr|");
        }
        sb.append(accession_).append("|");
        sb.append(entryName_).append(" ");
        sb.append(proteinName_).append(" ");
        sb.append("OS=").append(organismName_).append(" ");
        sb.append("OX=").append(organismIdentifier_).append(" ");
        if (geneName_ != null) {
            sb.append("GN=").append(geneName_).append(" ");
        }
        if (proteinExistence_ != null) { // null for isoform
            sb.append("PE=").append(proteinExistence_).append(" ");
        }
        if (sequenceVersion_ != null) { // null for isoform
            sb.append("SV=").append(sequenceVersion_);
        }
        writer.println(sb);
    }

    /**
     * 'sp' for UniProtKB/Swiss-Prot and 'tr' for UniProtKB/TrEMBL.
     *
     * @return {@link DatabaseType}
     */
    public DatabaseType getDatabaseType() {
        return databaseType_;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType_ = databaseType;
    }

    /**
     * @return the entry name of the UniProtKB entry.
     */
    public String getEntryName() {
        return entryName_;
    }

    public void setEntryName(String entryName) {
        this.entryName_ = entryName;
    }

    /**
     * the recommended name of the UniProtKB entry as annotated in the RecName field.
     * For UniProtKB/TrEMBL entries without a RecName field, the SubName field is used.
     * In case of multiple SubNames, the first one is used.
     * The 'precursor' attribute is excluded, 'Fragment' is included with the name if applicable.
     *
     * @return protein name
     */
    public String getProteinName() {
        return proteinName_;
    }

    public void setProteinName(String proteinName) {
        this.proteinName_ = proteinName;
    }

    /**
     * @return the scientific name of the organism of the UniProtKB entry.
     */
    public String getOrganismName() {
        return organismName_;
    }

    public void setOrganismName(String organismName) {
        this.organismName_ = organismName;
    }

    /**
     * @return the unique identifier of the source organism, assigned by the NCBI.
     */
    public String getOrganismIdentifier() {
        return organismIdentifier_;
    }

    public void setOrganismIdentifier(String organismIdentifier) {
        this.organismIdentifier_ = organismIdentifier;
    }

    /**
     * for TrEMBL, the gene name could be null
     *
     * @return the first gene name of the UniProtKB entry. If there is no gene name,
     * OrderedLocusName or ORFname, the GN field is not listed.
     */
    @Nullable
    public String getGeneName() {
        return geneName_;
    }

    public void setGeneName(String geneName) {
        this.geneName_ = geneName;
    }

    /**
     * For isoform, protein existence is null.
     *
     * @return the numerical value describing the evidence for the existence of the protein.
     */
    public @Nullable Integer getProteinExistence() {
        return proteinExistence_;
    }

    public void setProteinExistence(Integer proteinExistence) {
        this.proteinExistence_ = proteinExistence;
    }

    /**
     * For isoform, sequence version is null.
     *
     * @return the version number of the sequence.
     */
    public @Nullable Integer getSequenceVersion() {
        return sequenceVersion_;
    }

    /**
     * Set the sequence version in the UniProt
     *
     * @param sequenceVersion sequence version
     */
    public void setSequenceVersion(Integer sequenceVersion) {
        this.sequenceVersion_ = sequenceVersion;
    }

    @Override
    public Header copy() {
        UniProtFastaHeader header = new UniProtFastaHeader();
        header.setDatabaseType(databaseType_);
        header.setAccession(accession_);
        header.setDescription(description_);
        header.setEntryName(entryName_);
        header.setProteinName(proteinName_);
        header.setOrganismName(organismName_);
        header.setOrganismIdentifier(organismIdentifier_);
        header.setGeneName(geneName_);
        header.setProteinExistence(proteinExistence_);
        header.setSequenceVersion(sequenceVersion_);

        return header;
    }
}
