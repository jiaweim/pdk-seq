package pdk.seq;


/**
 * Type of Protein Database.
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 02 Jul 2024, 1:43 PM
 */
public enum DatabaseType {

    /**
     * The <a href="https://www.uniprot.org/">UniProt</a> Knowledgebase is a central hub for the collection of functional information on
     * proteins with accurate, consistent and rich annotation.
     * <p>
     * It consists of UniProtKB/Swiss-Prot (expert-curated records) and UniProtKB/TrEMBL (computationally annotated records).
     */
    UniProtKB,
    /**
     * expert-curated records in UniProtKB
     */
    SwissProt,
    /**
     * computationally annotated records in UnkProtKB
     */
    TrEMBL,
    /**
     * <a href="https://www.ncbi.nlm.nih.gov/protein">The NCBI Protein database</a> is a collection of
     * sequences from several sources, including translations from annotated coding regions in GenBank,
     * RefSeq and TPA, as well as records from SwissProt, PIR, PRF, and PDB.
     */
    NCBI,
    Unknown
}
