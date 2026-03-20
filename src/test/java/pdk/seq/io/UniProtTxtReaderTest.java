package pdk.seq.io;

import org.junit.jupiter.api.Test;
import pdk.seq.DatabaseType;
import pdk.seq.Seq;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 20 Mar 2026, 10:14 AM
 */
class UniProtTxtReaderTest {

    @Test
    void testSwissProtEntry() throws IOException {
        String entry = """
                ID   CP2D7_HUMAN             Reviewed;         515 AA.
                AC   A0A087X1C5; Q6XP50;
                DT   01-APR-2015, integrated into UniProtKB/Swiss-Prot.
                DT   29-OCT-2014, sequence version 1.
                DT   28-JAN-2026, entry version 63.
                DE   RecName: Full=Cytochrome P450 2D7 {ECO:0000305};
                DE            EC=1.14.14.1 {ECO:0000269|PubMed:15051713};
                GN   Name=CYP2D7 {ECO:0000312|HGNC:HGNC:2624};
                OS   Homo sapiens (Human).
                OC   Eukaryota; Metazoa; Chordata; Craniata; Vertebrata; Euteleostomi; Mammalia;
                OC   Eutheria; Euarchontoglires; Primates; Haplorrhini; Catarrhini; Hominidae;
                OC   Homo.
                OX   NCBI_TaxID=9606;
                RN   [1]
                RP   NUCLEOTIDE SEQUENCE [MRNA], VARIANTS ASN-70; LEU-311; SER-337 INS;
                RP   369-ALA--CYS-373 DELINS VAL-HIS-MET-PRO-TYR; ARG-383 AND GLU-428, FUNCTION,
                RP   CATALYTIC ACTIVITY, SUBCELLULAR LOCATION, AND TISSUE SPECIFICITY.
                RC   TISSUE=Brain cortex;
                RX   PubMed=15051713; DOI=10.1074/jbc.m402337200;
                RA   Pai H.V., Kommaddi R.P., Chinta S.J., Mori T., Boyd M.R., Ravindranath V.;
                RT   "A frameshift mutation and alternate splicing in human brain generate a
                RT   functional form of the pseudogene cytochrome P4502D7 that demethylates
                RT   codeine to morphine.";
                RL   J. Biol. Chem. 279:27383-27389(2004).
                RN   [2]
                RP   NUCLEOTIDE SEQUENCE [LARGE SCALE GENOMIC DNA].
                RX   PubMed=10591208; DOI=10.1038/990031;
                RA   Dunham I., Hunt A.R., Collins J.E., Bruskiewich R., Beare D.M., Clamp M.,
                RA   Smink L.J., Ainscough R., Almeida J.P., Babbage A.K., Bagguley C.,
                RA   Bailey J., Barlow K.F., Bates K.N., Beasley O.P., Bird C.P., Blakey S.E.,
                RA   Bridgeman A.M., Buck D., Burgess J., Burrill W.D., Burton J., Carder C.,
                RA   Carter N.P., Chen Y., Clark G., Clegg S.M., Cobley V.E., Cole C.G.,
                RA   Collier R.E., Connor R., Conroy D., Corby N.R., Coville G.J., Cox A.V.,
                RA   Davis J., Dawson E., Dhami P.D., Dockree C., Dodsworth S.J., Durbin R.M.,
                RA   Ellington A.G., Evans K.L., Fey J.M., Fleming K., French L., Garner A.A.,
                RA   Gilbert J.G.R., Goward M.E., Grafham D.V., Griffiths M.N.D., Hall C.,
                RA   Hall R.E., Hall-Tamlyn G., Heathcott R.W., Ho S., Holmes S., Hunt S.E.,
                RA   Jones M.C., Kershaw J., Kimberley A.M., King A., Laird G.K., Langford C.F.,
                RA   Leversha M.A., Lloyd C., Lloyd D.M., Martyn I.D., Mashreghi-Mohammadi M.,
                RA   Matthews L.H., Mccann O.T., Mcclay J., Mclaren S., McMurray A.A.,
                RA   Milne S.A., Mortimore B.J., Odell C.N., Pavitt R., Pearce A.V., Pearson D.,
                RA   Phillimore B.J.C.T., Phillips S.H., Plumb R.W., Ramsay H., Ramsey Y.,
                RA   Rogers L., Ross M.T., Scott C.E., Sehra H.K., Skuce C.D., Smalley S.,
                RA   Smith M.L., Soderlund C., Spragon L., Steward C.A., Sulston J.E.,
                RA   Swann R.M., Vaudin M., Wall M., Wallis J.M., Whiteley M.N., Willey D.L.,
                RA   Williams L., Williams S.A., Williamson H., Wilmer T.E., Wilming L.,
                RA   Wright C.L., Hubbard T., Bentley D.R., Beck S., Rogers J., Shimizu N.,
                RA   Minoshima S., Kawasaki K., Sasaki T., Asakawa S., Kudoh J., Shintani A.,
                RA   Shibuya K., Yoshizaki Y., Aoki N., Mitsuyama S., Roe B.A., Chen F., Chu L.,
                RA   Crabtree J., Deschamps S., Do A., Do T., Dorman A., Fang F., Fu Y., Hu P.,
                RA   Hua A., Kenton S., Lai H., Lao H.I., Lewis J., Lewis S., Lin S.-P., Loh P.,
                RA   Malaj E., Nguyen T., Pan H., Phan S., Qi S., Qian Y., Ray L., Ren Q.,
                RA   Shaull S., Sloan D., Song L., Wang Q., Wang Y., Wang Z., White J.,
                RA   Willingham D., Wu H., Yao Z., Zhan M., Zhang G., Chissoe S., Murray J.,
                RA   Miller N., Minx P., Fulton R., Johnson D., Bemis G., Bentley D.,
                RA   Bradshaw H., Bourne S., Cordes M., Du Z., Fulton L., Goela D., Graves T.,
                RA   Hawkins J., Hinds K., Kemp K., Latreille P., Layman D., Ozersky P.,
                RA   Rohlfing T., Scheet P., Walker C., Wamsley A., Wohldmann P., Pepin K.,
                RA   Nelson J., Korf I., Bedell J.A., Hillier L.W., Mardis E., Waterston R.,
                RA   Wilson R., Emanuel B.S., Shaikh T., Kurahashi H., Saitta S., Budarf M.L.,
                RA   McDermid H.E., Johnson A., Wong A.C.C., Morrow B.E., Edelmann L., Kim U.J.,
                RA   Shizuya H., Simon M.I., Dumanski J.P., Peyrard M., Kedra D., Seroussi E.,
                RA   Fransson I., Tapia I., Bruder C.E., O'Brien K.P., Wilkinson P.,
                RA   Bodenteich A., Hartman K., Hu X., Khan A.S., Lane L., Tilahun Y.,
                RA   Wright H.;
                RT   "The DNA sequence of human chromosome 22.";
                RL   Nature 402:489-495(1999).
                RN   [3]
                RP   POLYMORPHISM.
                RX   PubMed=16169517; DOI=10.1016/j.bbrc.2005.08.255;
                RA   Gaedigk A., Gaedigk R., Leeder J.S.;
                RT   "CYP2D7 splice variants in human liver and brain: does CYP2D7 encode
                RT   functional protein?";
                RL   Biochem. Biophys. Res. Commun. 336:1241-1250(2005).
                RN   [4]
                RP   POLYMORPHISM.
                RX   PubMed=17494644; DOI=10.1124/dmd.107.014993;
                RA   Bhathena A., Mueller T., Grimm D.R., Idler K., Tsurutani A., Spear B.B.,
                RA   Katz D.A.;
                RT   "Frequency of the frame-shifting CYP2D7 138delT polymorphism in a large,
                RT   ethnically diverse sample population.";
                RL   Drug Metab. Dispos. 35:1251-1253(2007).
                RN   [5]
                RP   FUNCTION, AND SUBCELLULAR LOCATION.
                RX   PubMed=18838503; DOI=10.1124/dmd.108.023663;
                RA   Zhang W.Y., Tu Y.B., Haining R.L., Yu A.M.;
                RT   "Expression and functional analysis of CYP2D6.24, CYP2D6.26, CYP2D6.27, and
                RT   CYP2D7 isozymes.";
                RL   Drug Metab. Dispos. 37:1-4(2009).
                CC   -!- FUNCTION: May be responsible for the metabolism of many drugs and
                CC       environmental chemicals that it oxidizes. It may be involved in the
                CC       metabolism of codeine to morphine (PubMed:15051713). However, another
                CC       study could not confirm it (PubMed:18838503).
                CC       {ECO:0000269|PubMed:15051713, ECO:0000269|PubMed:18838503}.
                CC   -!- CATALYTIC ACTIVITY:
                CC       Reaction=an organic molecule + reduced [NADPH--hemoprotein reductase] +
                CC         O2 = an alcohol + oxidized [NADPH--hemoprotein reductase] + H2O +
                CC         H(+); Xref=Rhea:RHEA:17149, Rhea:RHEA-COMP:11964, Rhea:RHEA-
                CC         COMP:11965, ChEBI:CHEBI:15377, ChEBI:CHEBI:15378, ChEBI:CHEBI:15379,
                CC         ChEBI:CHEBI:30879, ChEBI:CHEBI:57618, ChEBI:CHEBI:58210,
                CC         ChEBI:CHEBI:142491; EC=1.14.14.1;
                CC         Evidence={ECO:0000269|PubMed:15051713};
                CC   -!- COFACTOR:
                CC       Name=heme; Xref=ChEBI:CHEBI:30413;
                CC   -!- SUBCELLULAR LOCATION: Membrane {ECO:0000305}; Multi-pass membrane
                CC       protein {ECO:0000255}. Cytoplasm {ECO:0000305|PubMed:15051713}.
                CC       Mitochondrion {ECO:0000269|PubMed:18838503}.
                CC   -!- TISSUE SPECIFICITY: Expressed in brain cortex (at protein level).
                CC       {ECO:0000269|PubMed:15051713}.
                CC   -!- POLYMORPHISM: One study shows that a rare double polymorphism allows
                CC       the expression of a functional protein (PubMed:15051713). Two
                CC       subsequent studies could not confirm the combined existence of both
                CC       polymorphisms in the genomes examined in those studies
                CC       (PubMed:16169517, PubMed:17494644). {ECO:0000269|PubMed:15051713,
                CC       ECO:0000269|PubMed:16169517, ECO:0000269|PubMed:17494644}.
                CC   -!- SIMILARITY: Belongs to the cytochrome P450 family.
                CC       {ECO:0000255|RuleBase:RU000461}.
                CC   -!- CAUTION: Pseudogene in the majority of genomes but is protein-coding in
                CC       others. The functional allele is thought to be rare. {ECO:0000305}.
                CC   ---------------------------------------------------------------------------
                CC   Copyrighted by the UniProt Consortium, see https://www.uniprot.org/terms
                CC   Distributed under the Creative Commons Attribution (CC BY 4.0) License
                CC   ---------------------------------------------------------------------------
                DR   EMBL; AY220845; AAO49806.1; -; mRNA.
                DR   EMBL; AC254562; -; NOT_ANNOTATED_CDS; Genomic_DNA.
                DR   RefSeq; NP_001335315.1; NM_001348386.1.
                DR   AlphaFoldDB; A0A087X1C5; -.
                DR   SMR; A0A087X1C5; -.
                DR   FunCoup; A0A087X1C5; 257.
                DR   ChEMBL; CHEMBL3542437; -.
                DR   GlyCosmos; A0A087X1C5; 1 site, No reported glycans.
                DR   GlyGen; A0A087X1C5; 1 site.
                DR   BioMuta; CYP2D7; -.
                DR   jPOST; A0A087X1C5; -.
                DR   MassIVE; A0A087X1C5; -.
                DR   PeptideAtlas; A0A087X1C5; -.
                DR   DNASU; 1564; -.
                DR   GeneID; 1564; -.
                DR   KEGG; hsa:1564; -.
                DR   UCSC; uc062eux.1; human.
                DR   AGR; HGNC:2624; -.
                DR   CTD; 1564; -.
                DR   DisGeNET; 1564; -.
                DR   GeneCards; CYP2D7; -.
                DR   HGNC; HGNC:2624; CYP2D7.
                DR   VEuPathDB; HostDB:ENSG00000205702; -.
                DR   HOGENOM; CLU_001570_22_0_1; -.
                DR   InParanoid; A0A087X1C5; -.
                DR   OMA; EHDIAFA; -.
                DR   OrthoDB; 3934656at2759; -.
                DR   PAN-GO; A0A087X1C5; 7 GO annotations based on evolutionary models.
                DR   PathwayCommons; A0A087X1C5; -.
                DR   BioGRID-ORCS; 1564; 0 hits in 40 CRISPR screens.
                DR   GenomeRNAi; 1564; -.
                DR   Pharos; A0A087X1C5; Tbio.
                DR   PRO; PR:A0A087X1C5; -.
                DR   Proteomes; UP000005640; Chromosome 22.
                DR   RNAct; A0A087X1C5; protein.
                DR   Bgee; ENSG00000205702; Expressed in right lobe of liver and 91 other cell types or tissues.
                DR   ExpressionAtlas; A0A087X1C5; baseline and differential.
                DR   GO; GO:0005737; C:cytoplasm; IDA:UniProtKB.
                DR   GO; GO:0016020; C:membrane; IEA:UniProtKB-SubCell.
                DR   GO; GO:0005739; C:mitochondrion; IDA:UniProtKB.
                DR   GO; GO:0070330; F:aromatase activity; IDA:UniProtKB.
                DR   GO; GO:0020037; F:heme binding; IBA:GO_Central.
                DR   GO; GO:0005506; F:iron ion binding; IEA:InterPro.
                DR   GO; GO:0016712; F:oxidoreductase activity, acting on paired donors, with incorporation or reduction of molecular oxygen, reduced flavin or flavoprotein as one donor, and incorporation of one atom of oxygen; IBA:GO_Central.
                DR   GO; GO:0019369; P:arachidonate metabolic process; IBA:GO_Central.
                DR   GO; GO:0042178; P:xenobiotic catabolic process; IDA:UniProtKB.
                DR   GO; GO:0006805; P:xenobiotic metabolic process; IDA:UniProtKB.
                DR   CDD; cd20663; CYP2D; 1.
                DR   FunFam; 1.10.630.10:FF:000004; cytochrome P450 2D15 isoform X1; 1.
                DR   Gene3D; 1.10.630.10; Cytochrome P450; 1.
                DR   InterPro; IPR001128; Cyt_P450.
                DR   InterPro; IPR017972; Cyt_P450_CS.
                DR   InterPro; IPR002401; Cyt_P450_E_grp-I.
                DR   InterPro; IPR008069; Cyt_P450_E_grp-I_CYP2D-like.
                DR   InterPro; IPR036396; Cyt_P450_sf.
                DR   InterPro; IPR050182; Cytochrome_P450_fam2.
                DR   PANTHER; PTHR24300:SF1; CYTOCHROME P450 2D6-RELATED; 1.
                DR   PANTHER; PTHR24300; CYTOCHROME P450 508A4-RELATED; 1.
                DR   Pfam; PF00067; p450; 2.
                DR   PRINTS; PR00463; EP450I.
                DR   PRINTS; PR01686; EP450ICYP2D.
                DR   PRINTS; PR00385; P450.
                DR   SUPFAM; SSF48264; Cytochrome P450; 1.
                DR   PROSITE; PS00086; CYTOCHROME_P450; 1.
                PE   1: Evidence at protein level;
                KW   Cytoplasm; Glycoprotein; Heme; Iron; Membrane; Metal-binding;
                KW   Mitochondrion; Monooxygenase; Oxidoreductase; Proteomics identification;
                KW   Reference proteome; Transmembrane; Transmembrane helix.
                FT   CHAIN           1..515
                FT                   /note="Cytochrome P450 2D7"
                FT                   /id="PRO_0000432413"
                FT   TOPO_DOM        1..2
                FT                   /note="Extracellular"
                FT                   /evidence="ECO:0000305"
                FT   TRANSMEM        3..23
                FT                   /note="Helical; Name=1"
                FT                   /evidence="ECO:0000255"
                FT   TOPO_DOM        24..301
                FT                   /note="Cytoplasmic"
                FT                   /evidence="ECO:0000305"
                FT   TRANSMEM        302..322
                FT                   /note="Helical; Name=2"
                FT                   /evidence="ECO:0000255"
                FT   TOPO_DOM        323..515
                FT                   /note="Extracellular"
                FT                   /evidence="ECO:0000305"
                FT   BINDING         461
                FT                   /ligand="heme"
                FT                   /ligand_id="ChEBI:CHEBI:30413"
                FT                   /ligand_part="Fe"
                FT                   /ligand_part_id="ChEBI:CHEBI:18248"
                FT                   /note="axial binding residue"
                FT                   /evidence="ECO:0000250|UniProtKB:P10635"
                FT   CARBOHYD        416
                FT                   /note="N-linked (GlcNAc...) asparagine"
                FT                   /evidence="ECO:0000255"
                FT   VARIANT         70
                FT                   /note="S -> N (in dbSNP:rs11090077)"
                FT                   /evidence="ECO:0000269|PubMed:15051713"
                FT                   /id="VAR_072632"
                FT   VARIANT         311
                FT                   /note="S -> L (in dbSNP:rs1800754)"
                FT                   /evidence="ECO:0000269|PubMed:15051713"
                FT                   /id="VAR_072633"
                FT   VARIANT         337
                FT                   /note="C -> CS"
                FT                   /evidence="ECO:0000269|PubMed:15051713"
                FT                   /id="VAR_072634"
                FT   VARIANT         369..373
                FT                   /note="AHMPC -> VHMPY"
                FT                   /evidence="ECO:0000269|PubMed:15051713"
                FT                   /id="VAR_072635"
                FT   VARIANT         383
                FT                   /note="H -> R (in dbSNP:rs56127449)"
                FT                   /evidence="ECO:0000269|PubMed:15051713"
                FT                   /id="VAR_072636"
                FT   VARIANT         428
                FT                   /note="K -> E (in dbSNP:rs2070907)"
                FT                   /evidence="ECO:0000269|PubMed:15051713"
                FT                   /id="VAR_072637"
                SQ   SEQUENCE   515 AA;  57489 MW;  E9331B1589161E03 CRC64;
                     MGLEALVPLA MIVAIFLLLV DLMHRHQRWA ARYPPGPLPL PGLGNLLHVD FQNTPYCFDQ
                     LRRRFGDVFS LQLAWTPVVV LNGLAAVREA MVTRGEDTAD RPPAPIYQVL GFGPRSQGVI
                     LSRYGPAWRE QRRFSVSTLR NLGLGKKSLE QWVTEEAACL CAAFADQAGR PFRPNGLLDK
                     AVSNVIASLT CGRRFEYDDP RFLRLLDLAQ EGLKEESGFL REVLNAVPVL PHIPALAGKV
                     LRFQKAFLTQ LDELLTEHRM TWDPAQPPRD LTEAFLAKKE KAKGSPESSF NDENLRIVVG
                     NLFLAGMVTT STTLAWGLLL MILHLDVQRG RRVSPGCPIV GTHVCPVRVQ QEIDDVIGQV
                     RRPEMGDQAH MPCTTAVIHE VQHFGDIVPL GVTHMTSRDI EVQGFRIPKG TTLITNLSSV
                     LKDEAVWKKP FRFHPEHFLD AQGHFVKPEA FLPFSAGRRA CLGEPLARME LFLFFTSLLQ
                     HFSFSVAAGQ PRPSHSRVVS FLVTPSPYEL CAVPR
                //
                """;
        UniProtTxtReader reader = new UniProtTxtReader(new StringReader(entry));
        assertTrue(reader.hasNext());
        Seq seq = reader.next();
        UniProtTxtHeader header = (UniProtTxtHeader) seq.getHeader();
        assertEquals("CP2D7_HUMAN", header.getEntryName());
        assertEquals(DatabaseType.SwissProt, header.getDatabaseType());
        List<String> accs = header.getAccessions();
        assertEquals(2, accs.size());
        assertIterableEquals(List.of("A0A087X1C5", "Q6XP50"), accs);
        assertEquals("A0A087X1C5", header.getAccession());

        // DT
        assertEquals("01-APR-2015", header.getIntegrateDate());
        assertEquals("29-OCT-2014", header.getSequenceDate());
        assertEquals(1, header.getSequenceVersion());
        assertEquals("28-JAN-2026", header.getEntryDate());
        assertEquals(63, header.getEntryVersion());

        // DE
        DEProteinName deProteinName = header.getDEProteinName();
        assertEquals("Cytochrome P450 2D7 {ECO:0000305}", deProteinName.getRecName().getFullName());
        assertEquals("1.14.14.1 {ECO:0000269|PubMed:15051713}", deProteinName.getRecName().getECNames().getFirst());
        assertNull(header.getIncludes());
        assertNull(header.getContains());
        assertNull(header.getFlags());

        reader.close();
    }

    @Test
    void testTrEMBLEntry() throws IOException {
        String entry = """
                ID   A0A8S0M502_HUMAN        Unreviewed;       805 AA.
                AC   A0A8S0M502;
                DT   12-OCT-2022, integrated into UniProtKB/TrEMBL.
                DT   12-OCT-2022, sequence version 1.
                DT   28-JAN-2026, entry version 16.
                DE   RecName: Full=Angiotensin-converting enzyme {ECO:0000256|RuleBase:RU361144};
                DE            EC=3.4.-.- {ECO:0000256|RuleBase:RU361144};
                GN   Name=ACE2 {ECO:0000313|EMBL:BDH16358.1};
                OS   Homo sapiens (Human).
                OC   Eukaryota; Metazoa; Chordata; Craniata; Vertebrata; Euteleostomi; Mammalia;
                OC   Eutheria; Euarchontoglires; Primates; Haplorrhini; Catarrhini; Hominidae;
                OC   Homo.
                OX   NCBI_TaxID=9606 {ECO:0000313|EMBL:BDH16358.1};
                RN   [1] {ECO:0000313|EMBL:BDH16358.1}
                RP   NUCLEOTIDE SEQUENCE.
                RC   STRAIN=Human lung cDNA library {ECO:0000313|EMBL:BDH16358.1};
                RX   DOI=10.1128/JVI.01143-07.;
                RA   Fukushi S., Mizutani T., Sakai K., Saijo M., Taguchi F., Yokoyama M.,
                RA   Kurane I., Morikawa S.;
                RT   "Amino acid substitutions in the s2 region enhance severe acute respiratory
                RT   syndrome coronavirus infectivity in rat angiotensin-converting enzyme 2-
                RT   expressing cells.";
                RL   J. Virol. 81:10831-10834(2022).
                CC   -!- CATALYTIC ACTIVITY:
                CC       Reaction=[Pyr1]apelin-13 + H2O = [Pyr1]apelin-12 + L-phenylalanine;
                CC         Xref=Rhea:RHEA:63604, ChEBI:CHEBI:15377, ChEBI:CHEBI:58095,
                CC         ChEBI:CHEBI:147415, ChEBI:CHEBI:147416;
                CC         Evidence={ECO:0000256|ARBA:ARBA00051552};
                CC       PhysiologicalDirection=left-to-right; Xref=Rhea:RHEA:63605;
                CC         Evidence={ECO:0000256|ARBA:ARBA00051552};
                CC   -!- CATALYTIC ACTIVITY:
                CC       Reaction=angiotensin I + H2O = angiotensin-(1-9) + L-leucine;
                CC         Xref=Rhea:RHEA:63532, ChEBI:CHEBI:15377, ChEBI:CHEBI:57427,
                CC         ChEBI:CHEBI:147350, ChEBI:CHEBI:147351;
                CC         Evidence={ECO:0000256|ARBA:ARBA00000796};
                CC       PhysiologicalDirection=left-to-right; Xref=Rhea:RHEA:63533;
                CC         Evidence={ECO:0000256|ARBA:ARBA00000796};
                CC   -!- CATALYTIC ACTIVITY:
                CC       Reaction=angiotensin II + H2O = angiotensin-(1-7) + L-phenylalanine;
                CC         Xref=Rhea:RHEA:26554, ChEBI:CHEBI:15377, ChEBI:CHEBI:58095,
                CC         ChEBI:CHEBI:58506, ChEBI:CHEBI:58922; EC=3.4.17.23;
                CC         Evidence={ECO:0000256|ARBA:ARBA00001502};
                CC       PhysiologicalDirection=left-to-right; Xref=Rhea:RHEA:26555;
                CC         Evidence={ECO:0000256|ARBA:ARBA00001502};
                CC   -!- CATALYTIC ACTIVITY:
                CC       Reaction=apelin-13 + H2O = apelin-12 + L-phenylalanine;
                CC         Xref=Rhea:RHEA:63564, ChEBI:CHEBI:15377, ChEBI:CHEBI:58095,
                CC         ChEBI:CHEBI:147395, ChEBI:CHEBI:147396;
                CC         Evidence={ECO:0000256|ARBA:ARBA00050676};
                CC       PhysiologicalDirection=left-to-right; Xref=Rhea:RHEA:63565;
                CC         Evidence={ECO:0000256|ARBA:ARBA00050676};
                CC   -!- CATALYTIC ACTIVITY:
                CC       Reaction=apelin-17 + H2O = apelin-16 + L-phenylalanine;
                CC         Xref=Rhea:RHEA:63608, ChEBI:CHEBI:15377, ChEBI:CHEBI:58095,
                CC         ChEBI:CHEBI:147421, ChEBI:CHEBI:147422;
                CC         Evidence={ECO:0000256|ARBA:ARBA00050287};
                CC       PhysiologicalDirection=left-to-right; Xref=Rhea:RHEA:63609;
                CC         Evidence={ECO:0000256|ARBA:ARBA00050287};
                CC   -!- CATALYTIC ACTIVITY:
                CC       Reaction=bradykinin(1-8) + H2O = bradykinin(1-7) + L-phenylalanine;
                CC         Xref=Rhea:RHEA:63536, ChEBI:CHEBI:15377, ChEBI:CHEBI:58095,
                CC         ChEBI:CHEBI:133069, ChEBI:CHEBI:147352;
                CC         Evidence={ECO:0000256|ARBA:ARBA00052474};
                CC       PhysiologicalDirection=left-to-right; Xref=Rhea:RHEA:63537;
                CC         Evidence={ECO:0000256|ARBA:ARBA00052474};
                CC   -!- CATALYTIC ACTIVITY:
                CC       Reaction=dynorphin A-(1-13) + H2O = dynorphin A-(1-12) + L-lysine;
                CC         Xref=Rhea:RHEA:63556, ChEBI:CHEBI:15377, ChEBI:CHEBI:32551,
                CC         ChEBI:CHEBI:147381, ChEBI:CHEBI:147383;
                CC         Evidence={ECO:0000256|ARBA:ARBA00050289};
                CC       PhysiologicalDirection=left-to-right; Xref=Rhea:RHEA:63557;
                CC         Evidence={ECO:0000256|ARBA:ARBA00050289};
                CC   -!- CATALYTIC ACTIVITY:
                CC       Reaction=kinetensin + H2O = kinetensin-(1-8) + L-leucine;
                CC         Xref=Rhea:RHEA:63544, ChEBI:CHEBI:15377, ChEBI:CHEBI:57427,
                CC         ChEBI:CHEBI:147364, ChEBI:CHEBI:147365;
                CC         Evidence={ECO:0000256|ARBA:ARBA00050348};
                CC       PhysiologicalDirection=left-to-right; Xref=Rhea:RHEA:63545;
                CC         Evidence={ECO:0000256|ARBA:ARBA00050348};
                CC   -!- CATALYTIC ACTIVITY:
                CC       Reaction=neurotensin + H2O = neurotensin-(1-12) + L-leucine;
                CC         Xref=Rhea:RHEA:63540, ChEBI:CHEBI:15377, ChEBI:CHEBI:57427,
                CC         ChEBI:CHEBI:147362, ChEBI:CHEBI:147363;
                CC         Evidence={ECO:0000256|ARBA:ARBA00052322};
                CC       PhysiologicalDirection=left-to-right; Xref=Rhea:RHEA:63541;
                CC         Evidence={ECO:0000256|ARBA:ARBA00052322};
                CC   -!- COFACTOR:
                CC       Name=Zn(2+); Xref=ChEBI:CHEBI:29105;
                CC         Evidence={ECO:0000256|RuleBase:RU361144};
                CC       Note=Binds 1 zinc ion per subunit. {ECO:0000256|RuleBase:RU361144};
                CC   -!- COFACTOR:
                CC       Name=chloride; Xref=ChEBI:CHEBI:17996;
                CC         Evidence={ECO:0000256|ARBA:ARBA00001923};
                CC   -!- SUBUNIT: Homodimer. Interacts with the catalytically active form of
                CC       TMPRSS2. Interacts with SLC6A19; this interaction is essential for
                CC       expression and function of SLC6A19 in intestine. Interacts with
                CC       ITGA5:ITGB1. Probably interacts (via endocytic sorting signal motif)
                CC       with AP2M1; the interaction is inhibited by phosphorylation of Tyr-781.
                CC       Interacts (via PDZ-binding motif) with NHERF1 (via PDZ domains); the
                CC       interaction may enhance ACE2 membrane residence.
                CC       {ECO:0000256|ARBA:ARBA00063223}.
                CC   -!- SUBCELLULAR LOCATION: Apical cell membrane
                CC       {ECO:0000256|ARBA:ARBA00004221}. Cell membrane
                CC       {ECO:0000256|ARBA:ARBA00004251, ECO:0000256|PROSITE-ProRule:PRU01354};
                CC       Single-pass type I membrane protein {ECO:0000256|ARBA:ARBA00004251,
                CC       ECO:0000256|PROSITE-ProRule:PRU01354}. Cell projection, cilium
                CC       {ECO:0000256|ARBA:ARBA00004138}. Cytoplasm
                CC       {ECO:0000256|ARBA:ARBA00004496}. Secreted
                CC       {ECO:0000256|ARBA:ARBA00004613}.
                CC   -!- SIMILARITY: Belongs to the peptidase M2 family.
                CC       {ECO:0000256|ARBA:ARBA00008139, ECO:0000256|PROSITE-ProRule:PRU01355,
                CC       ECO:0000256|RuleBase:RU361144}.
                CC   -!- CAUTION: Lacks conserved residue(s) required for the propagation of
                CC       feature annotation. {ECO:0000256|PROSITE-ProRule:PRU01355}.
                CC   ---------------------------------------------------------------------------
                CC   Copyrighted by the UniProt Consortium, see https://www.uniprot.org/terms
                CC   Distributed under the Creative Commons Attribution (CC BY 4.0) License
                CC   ---------------------------------------------------------------------------
                DR   EMBL; LC698008; BDH16358.1; -; mRNA.
                DR   AlphaFoldDB; A0A8S0M502; -.
                DR   SMR; A0A8S0M502; -.
                DR   PeptideAtlas; A0A8S0M502; -.
                DR   OrthoDB; 10029630at2759; -.
                DR   GO; GO:0016324; C:apical plasma membrane; IEA:UniProtKB-SubCell.
                DR   GO; GO:0005929; C:cilium; IEA:UniProtKB-SubCell.
                DR   GO; GO:0005737; C:cytoplasm; IEA:UniProtKB-SubCell.
                DR   GO; GO:0005576; C:extracellular region; IEA:UniProtKB-SubCell.
                DR   GO; GO:0004180; F:carboxypeptidase activity; IEA:UniProtKB-KW.
                DR   GO; GO:0046872; F:metal ion binding; IEA:UniProtKB-KW.
                DR   GO; GO:0008237; F:metallopeptidase activity; IEA:UniProtKB-KW.
                DR   GO; GO:0008241; F:peptidyl-dipeptidase activity; IEA:InterPro.
                DR   GO; GO:0006508; P:proteolysis; IEA:UniProtKB-KW.
                DR   CDD; cd06461; M2_ACE; 1.
                DR   FunFam; 1.10.1370.30:FF:000001; Angiotensin-converting enzyme; 1.
                DR   Gene3D; 1.10.1370.30; -; 1.
                DR   InterPro; IPR031588; Collectrin_dom.
                DR   InterPro; IPR001548; Peptidase_M2.
                DR   PANTHER; PTHR10514; ANGIOTENSIN-CONVERTING ENZYME; 1.
                DR   PANTHER; PTHR10514:SF24; ANGIOTENSIN-CONVERTING ENZYME 2; 1.
                DR   Pfam; PF16959; Collectrin; 1.
                DR   Pfam; PF01401; Peptidase_M2; 1.
                DR   PRINTS; PR00791; PEPDIPTASEA.
                DR   SUPFAM; SSF55486; Metalloproteases ('zincins'), catalytic domain; 1.
                DR   PROSITE; PS52010; COLLECTRIN_LIKE; 1.
                DR   PROSITE; PS52011; PEPTIDASE_M2; 1.
                PE   2: Evidence at transcript level;
                KW   Carboxypeptidase {ECO:0000256|ARBA:ARBA00022645,
                KW   ECO:0000256|RuleBase:RU361144};
                KW   Cell membrane {ECO:0000256|ARBA:ARBA00022475, ECO:0000256|PROSITE-
                KW   ProRule:PRU01354}; Cell projection {ECO:0000256|ARBA:ARBA00023273};
                KW   Chloride {ECO:0000256|ARBA:ARBA00023214};
                KW   Cytoplasm {ECO:0000256|ARBA:ARBA00022490};
                KW   Disulfide bond {ECO:0000256|ARBA:ARBA00023157,
                KW   ECO:0000256|PIRSR:PIRSR601548-4};
                KW   Glycoprotein {ECO:0000256|ARBA:ARBA00023180, ECO:0000256|PIRSR:PIRSR601548-
                KW   10};
                KW   Hydrolase {ECO:0000256|ARBA:ARBA00022801, ECO:0000256|RuleBase:RU361144};
                KW   Membrane {ECO:0000256|ARBA:ARBA00023136, ECO:0000256|PROSITE-
                KW   ProRule:PRU01354};
                KW   Metal-binding {ECO:0000256|ARBA:ARBA00022723,
                KW   ECO:0000256|PIRSR:PIRSR601548-3};
                KW   Metalloprotease {ECO:0000256|ARBA:ARBA00023049,
                KW   ECO:0000256|RuleBase:RU361144};
                KW   Phosphoprotein {ECO:0000256|ARBA:ARBA00022553};
                KW   Protease {ECO:0000256|ARBA:ARBA00022670, ECO:0000256|RuleBase:RU361144};
                KW   Secreted {ECO:0000256|ARBA:ARBA00022525};
                KW   Signal {ECO:0000256|ARBA:ARBA00022729, ECO:0000256|SAM:SignalP};
                KW   Transmembrane {ECO:0000256|ARBA:ARBA00022692, ECO:0000256|PROSITE-
                KW   ProRule:PRU01354};
                KW   Transmembrane helix {ECO:0000256|ARBA:ARBA00022989, ECO:0000256|PROSITE-
                KW   ProRule:PRU01354};
                KW   Zinc {ECO:0000256|ARBA:ARBA00022833, ECO:0000256|PIRSR:PIRSR601548-3}.
                FT   SIGNAL          1..17
                FT                   /evidence="ECO:0000256|SAM:SignalP"
                FT   CHAIN           18..805
                FT                   /note="Angiotensin-converting enzyme"
                FT                   /evidence="ECO:0000256|SAM:SignalP"
                FT                   /id="PRO_5035841938"
                FT   TRANSMEM        741..765
                FT                   /note="Helical"
                FT                   /evidence="ECO:0000256|SAM:Phobius"
                FT   DOMAIN          614..805
                FT                   /note="Collectrin-like"
                FT                   /evidence="ECO:0000259|PROSITE:PS52010"
                FT   REGION          772..805
                FT                   /note="Disordered"
                FT                   /evidence="ECO:0000256|SAM:MobiDB-lite"
                FT   COMPBIAS        789..805
                FT                   /note="Polar residues"
                FT                   /evidence="ECO:0000256|SAM:MobiDB-lite"
                FT   ACT_SITE        375
                FT                   /note="Proton acceptor 1"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-1"
                FT   ACT_SITE        375
                FT                   /note="Proton acceptor 2"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-11"
                FT   ACT_SITE        505
                FT                   /note="Proton donor 1"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-1"
                FT   ACT_SITE        505
                FT                   /note="Proton donor 2"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-11"
                FT   BINDING         207
                FT                   /ligand="chloride"
                FT                   /ligand_id="ChEBI:CHEBI:17996"
                FT                   /ligand_label="1"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-2"
                FT   BINDING         374
                FT                   /ligand="Zn(2+)"
                FT                   /ligand_id="ChEBI:CHEBI:29105"
                FT                   /ligand_label="1"
                FT                   /ligand_note="catalytic"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-3"
                FT   BINDING         374
                FT                   /ligand="Zn(2+)"
                FT                   /ligand_id="ChEBI:CHEBI:29105"
                FT                   /ligand_label="2"
                FT                   /ligand_note="catalytic"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-8"
                FT   BINDING         378
                FT                   /ligand="Zn(2+)"
                FT                   /ligand_id="ChEBI:CHEBI:29105"
                FT                   /ligand_label="1"
                FT                   /ligand_note="catalytic"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-3"
                FT   BINDING         378
                FT                   /ligand="Zn(2+)"
                FT                   /ligand_id="ChEBI:CHEBI:29105"
                FT                   /ligand_label="2"
                FT                   /ligand_note="catalytic"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-8"
                FT   BINDING         402
                FT                   /ligand="Zn(2+)"
                FT                   /ligand_id="ChEBI:CHEBI:29105"
                FT                   /ligand_label="2"
                FT                   /ligand_note="catalytic"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-8"
                FT   BINDING         402
                FT                   /ligand="Zn(2+)"
                FT                   /ligand_id="ChEBI:CHEBI:29105"
                FT                   /ligand_label="1"
                FT                   /ligand_note="catalytic"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-3"
                FT   BINDING         514
                FT                   /ligand="chloride"
                FT                   /ligand_id="ChEBI:CHEBI:17996"
                FT                   /ligand_label="1"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-2"
                FT   CARBOHYD        53
                FT                   /note="N-linked (GlcNAc...) asparagine"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-10"
                FT   CARBOHYD        90
                FT                   /note="N-linked (GlcNAc...) (complex) asparagine"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-10"
                FT   CARBOHYD        578
                FT                   /note="N-linked (GlcNAc...) asparagine; partial"
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-10"
                FT   DISULFID        133..141
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-4,
                FT                   ECO:0000256|PROSITE-ProRule:PRU01355"
                FT   DISULFID        344..361
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-4"
                FT   DISULFID        530..542
                FT                   /evidence="ECO:0000256|PIRSR:PIRSR601548-4"
                SQ   SEQUENCE   805 AA;  92491 MW;  4B7EC9864426DE42 CRC64;
                     MSSSSWLLLS LVAVTAAQST IEEQAKTFLD KFNHEAEDLF YQSSLASWNY NTNITEENVQ
                     NMNNAGDKWS AFLKEQSTLV QMYPLQEIQN LTVKLQLQAL QQNGSSVLSE DKSKRLNTIL
                     NTMSTIYSTG KVCNPDNPQE CLLLEPGLNE IMANSLDYNE RLWAWESWRS EVGKQLRPLY
                     EEYVVLKNEM ARANHYEDYG DYWRGDYEVN GVDGYDYSRG QLIEDVEHTF EEIKPLYEHL
                     HAYVRAKLMN AYPSYISPIG CLPAHLLGDM WGRFWTNLYS LTVPFGQKPN IDVTDAMVDQ
                     AWDAQRIFKE AEKFFVSVGL PNMTQGFWEN SMLTDPGNVQ KAVCHPTAWD LGKGDFRILM
                     CTKVTMDDFL TAHHEMGHIQ YDMAYAAQPF LLRNGANEGF HEAVGEIMSL SAATPKHLKS
                     IGLLSPDFQE DNETEINFLL KQALTIVGTL PFTYMLEKWR WMVFKGEIPK DQWMKKWWEM
                     KREIVGVVEP VPHDETYCDP ASLFHVSNDY SFIRYYTRTL YQFQFQEALC QAAKHEGPLH
                     KCDISNSTEA GQKLFNMLRL GKSEPWTLAL ENVVGAKNMN VRPLLNYFEP LFTWLKDQNK
                     NSFVGWSTDW SPYADQSIKV RISLKSALGD KAYEWNDNEM YLFRSSVAYA MRQYFLKVKN
                     QMILFGEEDV RVANLKPRIS FNFFVTAPKN VSDIIPRTEV EKAIRMSRSR INDAFRLNDN
                     SLEFLGIQPT LGPPNQPPVS IWLIVFGVVM GVIVVGIVIL IFTGIRDRKK KNKARSGENP
                     YASIDISKGE NNPGFQNTDD VQTSF
                //
                """;
        UniProtTxtReader reader = new UniProtTxtReader(new StringReader(entry));
        assertTrue(reader.hasNext());
        Seq seq = reader.next();

        UniProtTxtHeader header = (UniProtTxtHeader) seq.getHeader();
        assertEquals("A0A8S0M502_HUMAN", header.getEntryName());
        assertEquals(DatabaseType.TrEMBL, header.getDatabaseType());
        List<String> accs = header.getAccessions();
        assertEquals(1, accs.size());
        assertIterableEquals(List.of("A0A8S0M502"), accs);
        assertEquals("A0A8S0M502", header.getAccession());

        // DT
        assertEquals("12-OCT-2022", header.getIntegrateDate());
        assertEquals("12-OCT-2022", header.getSequenceDate());
        assertEquals(1, header.getSequenceVersion());
        assertEquals("28-JAN-2026", header.getEntryDate());
        assertEquals(16, header.getEntryVersion());

        // DE
        DEProteinName deProteinName = header.getDEProteinName();
        assertEquals("Angiotensin-converting enzyme {ECO:0000256|RuleBase:RU361144}", deProteinName.getRecName().getFullName());
        assertEquals("3.4.-.- {ECO:0000256|RuleBase:RU361144}", deProteinName.getRecName().getECNames().getFirst());

        reader.close();
    }

    @Test
    void split() {
        String accs = "A0A087X1C5; Q6XP50;";
        String[] values = accs.split(";");
        for (String value : values) {
            System.out.println(value);
        }

    }
}