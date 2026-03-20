package pdk.seq.io;

import com.google.common.collect.Multiset;
import org.junit.jupiter.api.Test;
import pdk.seq.DatabaseType;
import pdk.seq.Header;
import pdk.seq.Seq;
import pdk.util.io.FileUtils;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 19 Mar 2026, 3:10 PM
 */
class FastaReaderTest {

    @Test
    void next() throws IOException {
        URL url = FastaReaderTest.class.getResource("test.fasta");
        FastaReader reader = new FastaReader(FileUtils.toPath(url), true);
        assertTrue(reader.hasNext());

        Seq seq1 = reader.next();
        Header header = seq1.getHeader();
        assertEquals(DatabaseType.TrEMBL, header.getDatabaseType());
        assertEquals("A0A067YZ78", header.getAccession());
        assertEquals("A0A067YZ78_9INFA", header.getEntryName());
        assertEquals("Matrix protein 2", header.getProteinName());
        assertEquals("Influenza A virus", header.getOrganismName());
        assertEquals("1454272", header.getOrganismIdentifier());
        assertEquals("M2", header.getGeneName());
        assertEquals(3, header.getProteinExistence());
        assertEquals(1, header.getSequenceVersion());
        assertEquals("A0A067YZ78_9INFA Matrix protein 2 OS=Influenza A virus OX=1454272 GN=M2 PE=3 SV=1", header.getDescription());
        assertEquals("MSLLTEVETLTRTGWVCNCSVSSDPLVVAANIIGILHLILWILDRLFFKYIYRRFKYGLK" +
                "RGPSTEGVPESMREEYRQEQQNAVDVDDGHFVNIELE", seq1.getSequence());

        assertTrue(reader.hasNext());

        Seq seq2 = reader.next();
        Header header2 = seq2.getHeader();
        assertEquals(DatabaseType.TrEMBL, header2.getDatabaseType());
        assertEquals("A0A067YZV9", header2.getAccession());
        assertEquals("A0A067YZV9_9INFA", header2.getEntryName());
        assertEquals("Hemagglutinin", header2.getProteinName());
        assertEquals("Influenza A virus", header2.getOrganismName());
        assertEquals("1454272", header2.getOrganismIdentifier());
        assertEquals("HA", header2.getGeneName());
        assertEquals(1, header2.getProteinExistence());
        assertEquals(1, header2.getSequenceVersion());
        assertEquals("MIAFIVIATLVATGKSDKICIGYHANNSTTKVDTILEKNVTVTHSVELLENQKEERFCKI" +
                "SNKAPLDLRDCTLEGWILGNPRCGILLADQSWSYIVERPNARNGICYPGTLNEAEELKAL" +
                "IGSGERVERFEMFPKSTWTGVNTESGVSSACPLGNGPSFYRNLLWIIKLKSSEYPVIRGT" +
                "FNNTGDKSILYFWGVHHPPVTTEQNALYGSGDRYVRMGTESMNFARSPEIAARPAVNGQR" +
                "GRIDYFWSILKPGETLNVESNGNLIAPWYAYRFVNKDSKGAIFRSNLPIENCDATCQTTE" +
                "GVIRTNKTFQNVSPLWIGECPKYVKSKSLRLATGLRNVPQIETRGLFGAIAGFIEGGWTG" +
                "MIDGWYGYHHENSQGSGYAADKESTQKAIDGITNKVNSIIDKMNTQFEAVGHEFSNLERR" +
                "IDNLNKRMEDGFLDVWTYNAELLVLLENERTLDLHDANVKNLHEKVRSQLRDNANDLGNG" +
                "CFEFWHKCNNECMESVKNGTYDYPKYQKESRLNRQKIESVKLENFDVYQILAIYSTVSSS" +
                "LVLVGLILAMGLWMCSNGSMQCRICI", seq2.getSequence());

        assertTrue(reader.hasNext());
        Seq seq3 = reader.next();
        Header header3 = seq3.getHeader();
        assertEquals(DatabaseType.SwissProt, header3.getDatabaseType());
        assertEquals("Q4R572", header3.getAccession());
        assertEquals("1433B_MACFA", header3.getEntryName());
        assertEquals("14-3-3 protein beta/alpha", header3.getProteinName());
        assertEquals("Macaca fascicularis", header3.getOrganismName());
        assertEquals("9541", header3.getOrganismIdentifier());
        assertEquals("YWHAB", header3.getGeneName());
        assertEquals(2, header3.getProteinExistence());
        assertEquals(3, header3.getSequenceVersion());

        assertTrue(reader.hasNext());
        Seq seq4 = reader.next();
        Header header4 = seq4.getHeader();
        assertEquals(DatabaseType.SwissProt, header4.getDatabaseType());
        assertEquals("Q4R572-2", header4.getAccession());
        assertEquals("1433B_MACFA", header4.getEntryName());
        assertEquals("Isoform Short of 14-3-3 protein beta/alpha", header4.getProteinName());
        assertEquals("Macaca fascicularis", header4.getOrganismName());
        assertEquals("9541", header4.getOrganismIdentifier());
        assertEquals("YWHAB", header4.getGeneName());
        assertNull(header4.getProteinExistence());
        assertNull(header4.getSequenceVersion());

        assertFalse(reader.hasNext());
        reader.close();
    }

    @Test
    void nextNoDetail() throws IOException {
        FastaReader reader = new FastaReader(FileUtils.toPath(FastaReaderTest.class.getResource("test.fasta")));
        assertTrue(reader.hasNext());

        Seq seq1 = reader.next();
        Header header = seq1.getHeader();
        assertEquals("tr|A0A067YZ78|A0A067YZ78_9INFA", header.getAccession());
        assertEquals("Matrix protein 2 OS=Influenza A virus OX=1454272 GN=M2 PE=3 SV=1", header.getDescription());
        assertEquals("MSLLTEVETLTRTGWVCNCSVSSDPLVVAANIIGILHLILWILDRLFFKYIYRRFKYGLK" +
                "RGPSTEGVPESMREEYRQEQQNAVDVDDGHFVNIELE", seq1.getSequence());

        assertTrue(reader.hasNext());

        Seq seq2 = reader.next();
        Header header2 = seq2.getHeader();
        assertEquals("tr|A0A067YZV9|A0A067YZV9_9INFA", header2.getAccession());
        assertEquals("Hemagglutinin OS=Influenza A virus OX=1454272 GN=HA PE=1 SV=1", header2.getDescription());
        assertEquals("MIAFIVIATLVATGKSDKICIGYHANNSTTKVDTILEKNVTVTHSVELLENQKEERFCKI" +
                "SNKAPLDLRDCTLEGWILGNPRCGILLADQSWSYIVERPNARNGICYPGTLNEAEELKAL" +
                "IGSGERVERFEMFPKSTWTGVNTESGVSSACPLGNGPSFYRNLLWIIKLKSSEYPVIRGT" +
                "FNNTGDKSILYFWGVHHPPVTTEQNALYGSGDRYVRMGTESMNFARSPEIAARPAVNGQR" +
                "GRIDYFWSILKPGETLNVESNGNLIAPWYAYRFVNKDSKGAIFRSNLPIENCDATCQTTE" +
                "GVIRTNKTFQNVSPLWIGECPKYVKSKSLRLATGLRNVPQIETRGLFGAIAGFIEGGWTG" +
                "MIDGWYGYHHENSQGSGYAADKESTQKAIDGITNKVNSIIDKMNTQFEAVGHEFSNLERR" +
                "IDNLNKRMEDGFLDVWTYNAELLVLLENERTLDLHDANVKNLHEKVRSQLRDNANDLGNG" +
                "CFEFWHKCNNECMESVKNGTYDYPKYQKESRLNRQKIESVKLENFDVYQILAIYSTVSSS" +
                "LVLVGLILAMGLWMCSNGSMQCRICI", seq2.getSequence());

        assertTrue(reader.hasNext());
        Seq seq3 = reader.next();
        Header header3 = seq3.getHeader();
        assertEquals("sp|Q4R572|1433B_MACFA", header3.getAccession());
        assertEquals("14-3-3 protein beta/alpha OS=Macaca fascicularis OX=9541 GN=YWHAB PE=2 SV=3", header3.getDescription());
        assertNull(header3.getEntryName());
        assertNull(header3.getProteinName());
        assertNull(header3.getOrganismName());
        assertNull(header3.getGeneName());
        assertNull(header3.getSequenceVersion());
        assertNull(header3.getProteinExistence());
        assertEquals("MTMDKSELVQKAKLAEQAERYDDMAAAMKAVTEQGHELSNEERNLLSVAYKNVVGARRSS" +
                "WRVISSIEQKTERNEKKQQMGKEYREKIEAELQDICNDVLELLDKYLIPNATQPESKVFY" +
                "LKMKGDYFRYLSEVASGDNKQTTVSNSQQAYQEAFEISKKEMQPTHPIRLGLALNFSVFY" +
                "YEILNSPEKACSLAKTAFDEAIAELDTLNEESYKDSTLIMQLLRDNLTLWTSENQGDEGDAGEGEN", seq3.getSequence());

        assertTrue(reader.hasNext());
        Seq seq4 = reader.next();
        Header header4 = seq4.getHeader();
        assertEquals("sp|Q4R572-2|1433B_MACFA", header4.getAccession());
        assertEquals("Isoform Short of 14-3-3 protein beta/alpha OS=Macaca fascicularis OX=9541 GN=YWHAB", header4.getDescription());

        assertFalse(reader.hasNext());
        reader.close();
    }

    @Test
    void teatReader() {
        String txt = """
                >sp|Q29836|1B67_HUMAN HLA class I histocompatibility antigen, B-67 alpha chain OS=Homo sapiens GN=HLA-B PE=1 SV=1
                MLVMAPRTVLLLLSAALALTETWAGSHSMRYFYTSVSRPGRGEPRFISVGYVDDTQFVRF
                DSDAASPREEPRAPWIEQEGPEYWDRNTQIYKAQAQTDRESLRNLRGYYNQSEAGSHTLQ
                RMYGCDVGPDGRLLRGHNQFAYDGKDYIALNEDLSSWTAADTAAQITQRKWEAARVAEQL
                RTYLEGTCVEWLRRYLENGKETLQRADPPKTHVTHHPISDHEATLRCWALGFYPAEITLT
                WQRDGEDQTQDTELVETRPAGDRTFQKWAAVVVPSGEEQRYTCHVQHEGLPKPLTLRWEP
                SSQSTVPIVGIVAGLAVLAVVVIGAVVAAVMCRRKSSGGKGGSYSQAASSDSAQGSDVSL
                TA
                """;
        try (FastaReader reader = new FastaReader(new StringReader(txt), true)) {
            assertTrue(reader.hasNext());

            Seq seq = reader.next();
            assertEquals(DatabaseType.SwissProt, seq.getDatabaseType());
            assertEquals("Q29836", seq.getAccession());
            assertEquals("1B67_HUMAN", seq.getEntryName());
            assertEquals("HLA class I histocompatibility antigen, B-67 alpha chain", seq.getProteinName());
            assertEquals("Homo sapiens", seq.getOrganismName());
            assertEquals("HLA-B", seq.getGeneName());
            assertEquals(1, seq.getProteinExistence());
            assertEquals(1, seq.getSequenceVersion());

            assertEquals("MLVMAPRTVLLLLSAALALTETWAGSHSMRYFYTSVSRPGRGEPRFISVGYVDDTQFVRF" +
                    "DSDAASPREEPRAPWIEQEGPEYWDRNTQIYKAQAQTDRESLRNLRGYYNQSEAGSHTLQ" +
                    "RMYGCDVGPDGRLLRGHNQFAYDGKDYIALNEDLSSWTAADTAAQITQRKWEAARVAEQL" +
                    "RTYLEGTCVEWLRRYLENGKETLQRADPPKTHVTHHPISDHEATLRCWALGFYPAEITLT" +
                    "WQRDGEDQTQDTELVETRPAGDRTFQKWAAVVVPSGEEQRYTCHVQHEGLPKPLTLRWEP" +
                    "SSQSTVPIVGIVAGLAVLAVVVIGAVVAAVMCRRKSSGGKGGSYSQAASSDSAQGSDVSL" +
                    "TA", seq.getSequence());
            assertFalse(reader.hasNext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void teatReaderNoDetail() {
        String txt = """
                >sp|Q29836|1B67_HUMAN HLA class I histocompatibility antigen, B-67 alpha chain OS=Homo sapiens GN=HLA-B PE=1 SV=1
                MLVMAPRTVLLLLSAALALTETWAGSHSMRYFYTSVSRPGRGEPRFISVGYVDDTQFVRF
                DSDAASPREEPRAPWIEQEGPEYWDRNTQIYKAQAQTDRESLRNLRGYYNQSEAGSHTLQ
                RMYGCDVGPDGRLLRGHNQFAYDGKDYIALNEDLSSWTAADTAAQITQRKWEAARVAEQL
                RTYLEGTCVEWLRRYLENGKETLQRADPPKTHVTHHPISDHEATLRCWALGFYPAEITLT
                WQRDGEDQTQDTELVETRPAGDRTFQKWAAVVVPSGEEQRYTCHVQHEGLPKPLTLRWEP
                SSQSTVPIVGIVAGLAVLAVVVIGAVVAAVMCRRKSSGGKGGSYSQAASSDSAQGSDVSL
                TA
                """;
        try (FastaReader reader = new FastaReader(new StringReader(txt), false)) {
            assertTrue(reader.hasNext());

            Seq seq = reader.next();
            assertEquals("sp|Q29836|1B67_HUMAN", seq.getAccession());
            assertEquals("HLA class I histocompatibility antigen, B-67 alpha chain OS=Homo sapiens GN=HLA-B PE=1 SV=1", seq.getDescription());
            assertEquals("MLVMAPRTVLLLLSAALALTETWAGSHSMRYFYTSVSRPGRGEPRFISVGYVDDTQFVRF" +
                    "DSDAASPREEPRAPWIEQEGPEYWDRNTQIYKAQAQTDRESLRNLRGYYNQSEAGSHTLQ" +
                    "RMYGCDVGPDGRLLRGHNQFAYDGKDYIALNEDLSSWTAADTAAQITQRKWEAARVAEQL" +
                    "RTYLEGTCVEWLRRYLENGKETLQRADPPKTHVTHHPISDHEATLRCWALGFYPAEITLT" +
                    "WQRDGEDQTQDTELVETRPAGDRTFQKWAAVVVPSGEEQRYTCHVQHEGLPKPLTLRWEP" +
                    "SSQSTVPIVGIVAGLAVLAVVVIGAVVAAVMCRRKSSGGKGGSYSQAASSDSAQGSDVSL" +
                    "TA", seq.getSequence());
            assertFalse(reader.hasNext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void readGenomeFasta() throws IOException {
        FastaReader reader = new FastaReader(FileUtils.toPath(FastaReaderTest.class.getResource("lambda_virus.fa")));
        assertTrue(reader.hasNext());
        Seq seq = reader.next();
        String f100 = seq.subSeq(0, 100);
        assertEquals("GGGCGGCGACCTCGCGGGTTTTCGCTATTTATGAAAATTTTCCGGTTTAAGGCGTTTCCGTTCTTCTTCGTCATAACTTAATGTTTTTATTTAAAATACC", f100);
        assertEquals(48502, seq.length());

        Multiset<Character> aaCount = seq.getCharacterCount();
        assertEquals(12334, aaCount.count('A'));
        assertEquals(11362, aaCount.count('C'));
        assertEquals(12820, aaCount.count('G'));
        assertEquals(11986, aaCount.count('T'));
        assertEquals(aaCount.size(), seq.length());
        reader.close();
    }

    @Test
    void testNonUniprot() throws IOException {
        String seq = """
                >mgo
                MTLSAEERAALERSKAIEKNLKEDGISAAKDVKLLLLGADNSGKSTIVKQMKIIHGGSGG
                SGGTTGIVETHFTFKNLHFRLFDVGGQRSERKKWIHCFEDVTAIIFCVDLSDYNRMHESL
                MLFDSICNNKFFIDTSIILFLNKKDLFGEKIKKSPLTICFPEYTGPNTYEDAAAYIQAQF
                ESKNRSPNKEIYCHMTCATDTNNAQVIFDAVTDIIIANNLRGCGLY
                """;

        FastaReader reader = new FastaReader(new StringReader(seq), false);
        assertTrue(reader.hasNext());
        Seq entry = reader.next();
        assertEquals("mgo", entry.getAccession());
        reader.close();
    }

    @Test
    void stringBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello");
        sb.append(" World");
        assertEquals("Hello World", sb.toString());

        sb.setLength(0);
        sb.append("New Content");
        assertEquals("New Content", sb.toString());
    }

}