package pdk.seq.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A Protein header in UniprotKB database.
 *
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 27 Jun 2024, 2:15 PM
 */
public class UniProtTxtHeader extends UniProtFastaHeader {

    private List<String> accessions_;
    private String integrateDate_;
    private String sequenceDate_;
    private String entryDate_;
    private Integer entryVersion_;
    private DEProteinName DEProteinName_;
    /**
     * If a protein is known to include multiple functional domains each of which is described by a different name,
     * the description starts with the name of the overall protein, followed by 'Includes:' section(s).
     * All the domains are listed in a separate 'Includes:' section.
     * Alternative names (AltName) are allowed for each individual domain.
     */
    private List<DEProteinName> includes_;
    /**
     * If a protein is known to be cleaved into multiple functional components, the description starts with the
     * name of the precursor protein, followed by 'Contains:' section(s).
     * Each individual component is described in a separate 'Contains:' section Alternative names (AltName)
     * are allowed for each individual component. 0-n
     */
    private List<DEProteinName> contains_;
    /**
     * Precursor and/or Fragment or Fragments
     */
    private String flags_;

    private ArrayList<String> geneNames;
    private String organelle;
    private List<String> organismClassification;
    private List<String> organismHostTaxIds;
    private Set<String> keywords;

    /**
     * Return all accession-numbers, including primary accession-numbers
     *
     * @return list of accession numbers, the first one is the primary accession number
     */
    public List<String> getAccessions() {
        return accessions_;
    }

    /**
     * set accession numbers, including the primary one.
     *
     * @param accessionList list of accession number
     */
    public void setAccessions(List<String> accessionList) {
        this.accessions_ = accessionList;
    }

    /**
     * Date when the entry first appeared in the database, in the format "DD-MMM-YYYY".
     *
     * @return integrate date in format "DD-MMM-YYYY"
     */
    public String getIntegrateDate() {
        return integrateDate_;
    }

    /**
     * Set the integrate date of this entry.
     *
     * @param integrateDate date in format "DD-MMM-YYYY"
     */
    public void setIntegrateDate(String integrateDate) {
        this.integrateDate_ = integrateDate;
    }

    /**
     * The data when the sequence data was last modified.
     *
     * @return when the sequence data was last modified in format "DD-MMM-YYYY"
     */
    public String getSequenceDate() {
        return sequenceDate_;
    }

    /**
     * Set the sequence date of this entry.
     *
     * @param sequenceDate sequence data in format "DD-MMM-YYYY"
     */
    public void setSequenceDate(String sequenceDate) {
        this.sequenceDate_ = sequenceDate;
    }

    /**
     * @return when data other than the sequence was last modified
     */
    public String getEntryDate() {
        return entryDate_;
    }

    /**
     * Set the entry date, when data other than the sequence was last modified.
     *
     * @param entryDate date in format "DD-MMM-YYYY"
     */
    public void setEntryDate(String entryDate) {
        this.entryDate_ = entryDate;
    }

    /**
     * The entry version number is incremented by one whenever any data in the flat file representation of
     * the entry is modified.
     *
     * @return entry version
     */
    public Integer getEntryVersion() {
        return entryVersion_;
    }

    /**
     * Set the entry version. The entry version number is incremented by one
     * whenever any data in the flat file representation of the entry is modified.
     *
     * @param entryVersion entry version number
     */
    public void setEntryVersion(Integer entryVersion) {
        this.entryVersion_ = entryVersion;
    }

    /**
     * Return protein names in the 'DE' section
     *
     * @return {@link DEProteinName}
     */
    public @NotNull DEProteinName getDEProteinName() {
        return DEProteinName_;
    }

    /**
     * Set the protein names
     *
     * @param proteinName {@link DEProteinName} instance
     */
    public void setDEProteinName(DEProteinName proteinName) {
        this.DEProteinName_ = proteinName;
    }

    /**
     * set the Flag value
     *
     * @param flags flags in 'DE' section
     */
    public void setFlags(String flags) {
        this.flags_ = flags;
    }

    /**
     * When the mature form of a protein is derived by processing of a precursor, we indicate this fact using
     * the Flag 'Precursor'; in such cases the sequence displayed does not correspond to the mature form of the protein.
     * <p>
     * If the complete sequence is not determined, we indicate it in the 'Flags' section with 'Fragment' or 'Fragments'.
     *
     * @return flag in 'DE' section
     */
    public @Nullable String getFlags() {
        return flags_;
    }

    /**
     * If a protein is known to include multiple functional domains each of which is described by a different name,
     * the description starts with the name of the overall protein, followed by 'Includes:' section(s).
     * All the domains are listed in a separate 'Includes:' section.
     * Alternative names (AltName) are allowed for each individual domain.
     *
     * @return name of all functional domains
     */
    public @Nullable List<DEProteinName> getIncludes() {
        return includes_;
    }

    public void setIncludes(List<DEProteinName> includes) {
        this.includes_ = includes;
    }

    /**
     * If a protein is known to be cleaved into multiple functional components, the description starts with
     * the name of the precursor protein, followed by 'Contains:' section(s).
     * Each individual component is described in a separate 'Contains:' section Alternative names (AltName)
     * are allowed for each individual component.
     *
     * @return names of functional components
     */
    public @Nullable List<DEProteinName> getContains() {
        return contains_;
    }

    public void setContains(List<DEProteinName> contains) {
        this.contains_ = contains;
    }

    @Nullable
    public ArrayList<String> getGeneNames() {
        return geneNames;
    }

    public void setGeneNames(ArrayList<String> geneNameList) {
        this.geneNames = geneNameList;
    }

    /**
     * @return The OG (OrGanelle) line indicates if the gene coding for a protein originates from mitochondria,
     * a plastid, a nucleomorph or a plasmid.
     */
    @Nullable
    public String getOrganelle() {
        return organelle;
    }

    public void setOrganelle(String organelle) {
        this.organelle = organelle;
    }

    /**
     * @return The classification is listed top-down as nodes in a taxonomic tree in which
     * the most general grouping is given first.
     */
    public List<String> getOrganismClassification() {
        return organismClassification;
    }

    public void setOrganismClassification(List<String> organismClassification) {
        this.organismClassification = organismClassification;
    }

    /**
     * @return the host organism(s) NCBI TaxId that are susceptible to be infected by a virus.
     */
    public List<String> getOrganismHostTaxIDs() {
        return organismHostTaxIds;
    }

    public void setOrganismHostTaxIDs(List<String> organismHostTaxIds) {
        this.organismHostTaxIds = organismHostTaxIds;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }
}
