package pdk.seq.io;

import com.google.common.collect.ArrayListMultimap;
import pdk.seq.DatabaseType;
import pdk.seq.Seq;
import pdk.util.StringUtils;
import pdk.util.exception.PDKRuntimeException;
import pdk.util.io.IterateReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * UniProt Txt format file reader.
 * <p>
 * https://web.expasy.org/docs/userman.html
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 17 Oct 2024, 1:51 PM
 */
public class UniProtTxtReader implements IterateReader<Seq>, AutoCloseable {

    private final LineNumberReader reader_;
    /**
     * line map of current entry.
     */
    private ArrayListMultimap<String, String> lineMap_ = ArrayListMultimap.create();

    /**
     * Create {@link UniProtTxtReader} from given file
     *
     * @param file {@link Path} instance
     * @throws IOException for reading error
     */
    public UniProtTxtReader(final Path file) throws IOException {
        this.reader_ = new LineNumberReader(Files.newBufferedReader(file));
    }

    /**
     * Create a {@link UniProtTxtReader} from an {@link InputStream}
     *
     * @param inputStream {@link InputStream} instance
     */
    public UniProtTxtReader(final InputStream inputStream) {
        this.reader_ = new LineNumberReader(new InputStreamReader(inputStream));
    }

    /**
     * Create a {@link UniProtTxtReader} from a {@link Reader}
     *
     * @param reader {@link Reader} instance
     */
    public UniProtTxtReader(final Reader reader) {
        if (reader instanceof LineNumberReader) {
            this.reader_ = (LineNumberReader) reader;
        } else {
            this.reader_ = new LineNumberReader(reader);
        }
    }

    @Override
    public void close() throws IOException {
        lineMap_.clear();
        lineMap_ = null;
        reader_.close();
    }

    @Override
    public boolean hasNext() {
        lineMap_.clear();
        try {
            String line;
            while ((line = reader_.readLine()) != null) {
                if (line.startsWith("//")) // termination line
                    break;
                String lineCode = line.substring(0, 2);
                String value = line.substring(5);
                lineMap_.put(lineCode, value);
            }
        } catch (IOException e) {
            throw new PDKRuntimeException(e);
        }
        return lineMap_.containsKey("ID");
    }

    @Override
    public Seq next() {
        UniProtTxtHeader header = new UniProtTxtHeader();

        List<String> lines = lineMap_.get("ID"); // once
        readID(header, lines);
        readAC(header, lineMap_.get("AC")); // once or more
        readDT(header, lineMap_.get("DT")); // three
        readDE(header, lineMap_.get("DE")); // once or more

        lines = lineMap_.get("GN"); // optional
        readGN(header, lines);

        lines = lineMap_.get("OS");// Once or more
        readOS(header, lines);
//        index += lines.size();

        lines = lineMap_.get("OG"); // Optional
        readOG(header, lines);
//        index += lines.size();

        lines = lineMap_.get("OC");// once or more
        readOC(header, lines);
//        index += lines.size();

        lines = lineMap_.get("OX"); // once
        readOX(header, lines);
//        index += lines.size();

        lines = lineMap_.get("OH"); // optional
        readOH(header, lines);
//        index += lines.size();

        // skip all references
//        lines = new ArrayList<>(); // once or more reference
//        for (int i = index; i < lineList.size(); i++) {
//            String s = lineList.get(i);
//            if (s.startsWith("RN") || s.startsWith("RP") || s.startsWith("RC")
//                    || s.startsWith("RX") || s.startsWith("RG") || s.startsWith("RA")
//                    || s.startsWith("RT") || s.startsWith("RL")) {
//                lines.add(s);
//            } else {
//                break;
//            }
//        }

        lines = lineMap_.get("CC"); // optional
        if (!lines.isEmpty()) {
            // readCC
        }

        lines = lineMap_.get("DR"); // optional
        if (!lines.isEmpty()) {
            // readDR
        }

        lines = lineMap_.get("PE"); // once
        readPE(header, lines);

        lines = lineMap_.get("KW");// optional
        readKW(header, lines);

        lines = lineMap_.get("FT");// once or more in Swiss-Prot, optional in TrEMBL
        readFT(header, lines);

        lines = lineMap_.get("SQ");
        assert lines.size() == 1;

        lines = lineMap_.get("  ");
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append(line);
        }
        String seq = builder.toString();
        seq = StringUtils.deleteWhitespace(seq);

        return new Seq(header, seq);
    }

    /**
     * read `ID` line.⭐
     * format: ID   EntryName Status; SequenceLength.
     */
    private void readID(UniProtTxtHeader header, List<String> lines) {
        assert lines.size() == 1;

        String line = lines.getFirst();
        String[] items = line.split("\\s+");

        header.setEntryName(items[0]);
        // Reviewed;
        header.setDatabaseType(items[1].equals("Unreviewed;") ? DatabaseType.TrEMBL : DatabaseType.SwissProt);

        // the length is omitted.
    }

    /**
     * AC line: once or more⭐
     * <pre>
     *     AC   AC_number_1;[ AC_number_2;]...[ AC_number_N;]
     * </pre>
     */
    private void readAC(UniProtTxtHeader header, List<String> lines) {
        ArrayList<String> accList = new ArrayList<>();
        for (String line : lines) {
            String[] items = line.split(";");
            for (String item : items) {
                accList.add(item.trim());
            }
        }
        accList.trimToSize();

        header.setAccession(accList.getFirst());
        header.setAccessions(accList);
    }

    /**
     * DT line: three times⭐
     * <pre>
     *  DT   DD-MMM-YYYY, integrated into UniProtKB/database_name.
     *  DT   DD-MMM-YYYY, sequence version x.
     *  DT   DD-MMM-YYYY, entry version x.
     * </pre>
     */
    private void readDT(UniProtTxtHeader header, List<String> lines) {
        assert lines.size() == 3;

        String line1 = lines.get(0);
        String line2 = lines.get(1);
        String line3 = lines.get(2);

        String[] values1 = line1.split(",");
        header.setIntegrateDate(values1[0]);
        // next comment is same as Reviewed or Unreviewed

        String[] values2 = line2.split(",");
        header.setSequenceDate(values2[0]);
        header.setSequenceVersion(Integer.parseInt(values2[1].substring(18, values2[1].length() - 1)));

        String[] values3 = line3.split(",");
        header.setEntryDate(values3[0]);
        header.setEntryVersion(Integer.parseInt(values3[1].substring(15, values3[1].length() - 1)));
    }

    private static final String TAG_DE_RecName = "RecName:";
    private static final String TAG_DE_AltName = "AltName:";
    private static final String TAG_DE_SubName = "SubName:";

    private static final String TAG_DE_Contains = "Contains:";
    private static final String TAG_DE_Includes = "Includes:";
    private static final String TAG_DE_Flags = "Flags: ";

    private static final String TAG_DE_Allergen = "AltName: Allergen=";
    private static final String TAG_DE_Biotech = "AltName: Biotech=";
    private static final String TAG_DE_CD_antigen = "AltName: CD_antigen=";
    private static final String TAG_DE_INN = "AltName: INN=";

    private static final String TAG_DE_Full = "Full=";
    private static final String TAG_DE_Short = "Short=";
    private static final String TAG_DE_EC = "EC=";
    private static final String TAG_DE_TAB = "         ";
    private static final String TAG_DE_TAB2 = "  ";

    /**
     * The DE (DEscription) lines contain general descriptive information about the sequence stored.
     */
    private void readDE(UniProtTxtHeader header, List<String> lines) {
        List<String> blockLines = new ArrayList<>(lines.size());
        int idx = 0;
        for (; idx < lines.size(); idx++) {
            String line = lines.get(idx);
            if (line.startsWith(TAG_DE_Flags)) { // 0-1
                String flag = line.substring(TAG_DE_Flags.length(), line.length() - 1);
                header.setFlags(flag);
                idx++;
                break;
            } else if (line.startsWith(TAG_DE_Includes) || line.startsWith(TAG_DE_Contains)) {
                break;
            }
            blockLines.add(line);
        }
        DEProteinName name = readDEProteinName(blockLines);
        header.setDEProteinName(name);

        ArrayList<DEProteinName> contains = new ArrayList<>();
        ArrayList<DEProteinName> includes = new ArrayList<>();
        for (; idx < lines.size(); idx++) {
            String line = lines.get(idx);
            if (line.startsWith(TAG_DE_Contains)) {
                blockLines.clear();
                System.out.println(header.getAccession());
                for (int i = idx + 1; i < lines.size(); i++) {
                    line = lines.get(i);
                    if (line.startsWith(TAG_DE_TAB2)) {
                        System.out.println(line);
                        blockLines.add(line.substring(2));
                        idx = i;
                    } else {
                        DEProteinName proteinName = readDEProteinName(blockLines);
                        contains.add(proteinName);
                        idx = i - 1;
                        break;
                    }
                }
            } else if (line.startsWith(TAG_DE_Includes)) {
                blockLines.clear();
                for (int i = idx + 1; i < lines.size(); i++) {
                    line = lines.get(i);
                    if (line.startsWith(TAG_DE_TAB2)) {
                        blockLines.add(line.substring(2));
                        idx = i;
                    } else {
                        DEProteinName proteinName = readDEProteinName(blockLines);
                        includes.add(proteinName);
                        idx = i - 1;
                        break;
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid line: " + line);
            }
        }
        if (!contains.isEmpty()) {
            contains.trimToSize();
            header.setContains(contains);
        }
        if (!includes.isEmpty()) {
            includes.trimToSize();
            header.setIncludes(includes);
        }
    }

    private DEProteinName readDEProteinName(List<String> lines) {
//        System.out.println(lines.size());
//        for (String line : lines) {
//            System.out.println(line);
//        }

        DEProteinName proteinName = new DEProteinName();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith(TAG_DE_Allergen)) {
                String allergen = line.substring(TAG_DE_Allergen.length(), line.length() - 1);
                proteinName.setAllergen(allergen);
            } else if (line.startsWith(TAG_DE_Biotech)) {
                String biotech = line.substring(TAG_DE_Biotech.length(), line.length() - 1);
                proteinName.setBiotech(biotech);
            } else if (line.startsWith(TAG_DE_CD_antigen)) {
                String cdAntigen = line.substring(TAG_DE_CD_antigen.length(), line.length() - 1);
                proteinName.addCDAntigen(cdAntigen);
            } else if (line.startsWith(TAG_DE_INN)) {
                String inn = line.substring(TAG_DE_INN.length(), line.length() - 1);
                proteinName.addINN(inn);
            } else if (line.startsWith(TAG_DE_RecName)) {
                DEName recName = new DEName();
                int fullTagIdx = line.indexOf(TAG_DE_Full);
                assert fullTagIdx >= 0;
                String fullName = line.substring(fullTagIdx + TAG_DE_Full.length(), line.length() - 1);
                recName.setFullName(fullName);
                if (i < lines.size() - 1) {
                    for (int idx = i + 1; idx < lines.size(); idx++) {
                        line = lines.get(idx);
                        if (line.startsWith(TAG_DE_TAB)) {
                            int shortTagIdx = line.indexOf(TAG_DE_Short);
                            if (shortTagIdx >= 0) {
                                String shortName = line.substring(shortTagIdx + TAG_DE_Short.length(), line.length() - 1);
                                recName.addShortName(shortName);
                            } else {
                                int ecTagIdx = line.indexOf(TAG_DE_EC);
                                if (ecTagIdx >= 0) {
                                    String ecName = line.substring(ecTagIdx + TAG_DE_EC.length(), line.length() - 1);
                                    recName.addECName(ecName);
                                } else {
                                    throw new IllegalStateException("Unknown DE line under RecName: " + line);
                                }
                            }
                            i = idx;
                        } else {
                            i = idx - 1;
                            break;
                        }
                    }
                }
                recName.trimToSize();
                proteinName.setRecName(recName);
            } else if (line.startsWith(TAG_DE_AltName)) {
                DEName altName = new DEName();
                int fullTagIdx = line.indexOf(TAG_DE_Full);
                if (fullTagIdx < 0) { // 其实不确定 AltName 是不是必然以Full= 开始，只是在人库中没有看到例外
                    throw new IllegalStateException("Unknown DE line under AltName: " + line);
                }
                String fullName = line.substring(fullTagIdx + TAG_DE_Full.length(), line.length() - 1);
                altName.setFullName(fullName);
                if (i < lines.size() - 1) {
                    for (int idx = i + 1; idx < lines.size(); idx++) {
                        line = lines.get(idx);
                        if (line.startsWith(TAG_DE_TAB)) {
                            int shortTagIdx = line.indexOf(TAG_DE_Short);
                            if (shortTagIdx >= 0) {
                                String shortName = line.substring(shortTagIdx + TAG_DE_Short.length(), line.length() - 1);
                                altName.addShortName(shortName);
                            } else {
                                int ecTagIdx = line.indexOf(TAG_DE_EC);
                                if (ecTagIdx >= 0) {
                                    String ecName = line.substring(ecTagIdx + TAG_DE_EC.length(), line.length() - 1);
                                    altName.addECName(ecName);
                                } else {
                                    throw new IllegalStateException("Unknown DE line under AltName: " + line);
                                }
                            }
                            i = idx;
                        } else {
                            i = idx - 1;
                            break;
                        }
                    }
                }
                altName.trimToSize();
                proteinName.addAltName(altName);
            } else if (line.startsWith(TAG_DE_SubName)) {
                DEName subName = new DEName();
                int fullTagIdx = line.indexOf(TAG_DE_Full);
                if (fullTagIdx < 0) {
                    throw new IllegalStateException("Unknown DE line under AltName: " + line);
                }
                String fullName = line.substring(fullTagIdx + TAG_DE_Full.length(), line.length() - 1);
                subName.setFullName(fullName);
                if (i < lines.size() - 1) {
                    for (int idx = i + 1; idx < lines.size(); idx++) {
                        line = lines.get(idx);
                        if (line.startsWith(TAG_DE_TAB)) {
                            int ecTagIdx = line.indexOf(TAG_DE_EC);
                            if (ecTagIdx >= 0) {
                                String ecName = line.substring(ecTagIdx + TAG_DE_EC.length(), line.length() - 1);
                                subName.addECName(ecName);
                            } else {
                                throw new IllegalStateException("Unknown DE line under AltName: " + line);
                            }
                            i = idx;
                        } else {
                            i = idx - 1;
                            break;
                        }
                    }
                }
                subName.trimToSize();
                proteinName.addSubName(subName);
            } else {
                throw new IllegalStateException("Unknown DE line: " + line);
            }
        }

        return proteinName;
    }

    private void readGN(UniProtTxtHeader header, List<String> lines) {
        if (lines.isEmpty())
            return;

        ArrayList<String> genes = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("Name=")) {
                int endIdx2 = line.indexOf(";");
                int endIdx1 = line.indexOf(" {");

                int endIdx;
                if (endIdx1 > 0 && endIdx2 > 0) {
                    endIdx = Math.min(endIdx1, endIdx2);
                } else {
                    endIdx = Math.max(endIdx1, endIdx2);
                }

                int startIdx = line.indexOf("=") + 1;

                String geneName = line.substring(startIdx, endIdx);
                genes.add(geneName);
            }
        }

        if (!genes.isEmpty()) {
            genes.trimToSize();
            header.setGeneNames(genes);
            header.setGeneName(genes.getLast());
        }

//        ArrayList<GeneName> geneNameList = new ArrayList<>(1);
//        GeneName geneName = new GeneName();
//        for (int i = 0; i < lines.size(); i++) {
//            String line = lines.get(i);
//            if (line.startsWith("and")) { // GN line blocks for the different genes are separated by "and"
//                geneNameList.add(geneName);
//                geneName = new GeneName();
//                continue;
//            }
//            if (!line.endsWith(";")) {
//                StringJoiner joiner = new StringJoiner(" ");
//                joiner.add(line);
//
//                for (int j = i + 1; j < lines.size(); j++) {
//                    String l = lines.get(j);
//                    joiner.add(l);
//                    if (l.endsWith(";")) {
//                        line = joiner.toString();
//                        i = j;
//                        break;
//                    }
//                }
//            }
//
//            String[] items = line.split(";");
//            for (String item : items) {
//                item = item.stripLeading();
//
//                if (item.startsWith("Name=")) {
//                    geneName.setName(item.substring(5));
//                } else if (item.startsWith("Synonyms=")) {
//                    String synnoyms = item.substring(9);
//                    String[] values = synnoyms.split(",");
//                    ArrayList<String> synList = new ArrayList<>(values.length);
//                    for (String value : values) {
//                        synList.add(value.stripLeading());
//                    }
//                    geneName.setSynonyms(synList);
//                } else if (item.startsWith("OrderedLocusNames=")) {
//                    item = item.substring(18);
//                    for (String locus : item.split(",")) {
//                        geneName.addOrderedLocusName(locus.stripLeading());
//                    }
//                } else if (item.startsWith("ORFNames=")) {
//                    item = item.substring(9);
//                    for (String orf : item.split(",")) {
//                        geneName.addORFName(orf.stripLeading());
//                    }
//                } else {
//                    LOG.warn("Unknown line {} in protein {}", item, header.getAccession());
//                }
//            }
//        }
//        geneNameList.add(geneName);
//        header.setGeneNames(geneNameList);
    }

    // ⭐
    private void readOS(UniProtTxtHeader header, List<String> lines) {
        StringJoiner joiner = new StringJoiner(" ");
        for (String line : lines) {
            joiner.add(line);
        }

        String name = joiner.toString();
        int idx = name.indexOf("(");
        if (idx < 0) {
            idx = name.length();
        }
        idx = idx - 1;

        header.setOrganismName(name.substring(0, idx));
    }

    // ⭐
    private void readOG(UniProtTxtHeader header, List<String> lines) {
        if (lines.isEmpty())
            return;
        String first = lines.getFirst();
        header.setOrganelle(first.substring(0, first.length() - 1));
    }

//    private List<String> getLines(int index, String tag) {
//        List<String> lines = new ArrayList<>();
//        for (int i = index; i < lineList.size(); i++) {
//            String line = lineList.get(i);
//            if (line.startsWith(tag)) {
//                lines.add(line.substring(5));
//            } else {
//                break;
//            }
//        }
//        return lines;
//    }

    // ⭐
    private void readOC(UniProtTxtHeader header, List<String> lines) {
        StringJoiner joiner = new StringJoiner(" ");
        for (String line : lines) {
            joiner.add(line);
        }
        String name = joiner.toString();
        name = name.substring(0, name.length() - 1);
        String[] values = name.split("; ");
        List<String> ocList = new ArrayList<>();
        Collections.addAll(ocList, values);
        header.setOrganismClassification(ocList);
    }

    private void readOX(UniProtTxtHeader header, List<String> lines) {
        assert lines.size() == 1;
        String line = lines.getFirst();
        int idx1 = line.indexOf("=") + 1;
        int idx2 = line.indexOf("{");
        if (idx2 < 0) {
            idx2 = line.length();
        }
        idx2 = idx2 - 1;

        String code = line.substring(idx1, idx2);
        header.setOrganismIdentifier(code);
    }

    private void readOH(UniProtTxtHeader header, List<String> lines) {
        if (lines.isEmpty())
            return;
        ArrayList<String> taxIds = new ArrayList<>();
        for (String line : lines) {
            String taxId = line.substring(line.indexOf("=") + 1, line.indexOf(";"));
            taxIds.add(taxId);
        }
        taxIds.trimToSize();
        header.setOrganismHostTaxIDs(taxIds);
    }

    private void readPE(UniProtTxtHeader header, List<String> lines) {
        assert lines.size() == 1;
        String pe = lines.getFirst();
        pe = pe.substring(0, 1);
        header.setProteinExistence(Integer.parseInt(pe));
    }

    private void readKW(UniProtTxtHeader header, List<String> lines) {
        if (lines.isEmpty())
            return;
        Set<String> keywordList = new HashSet<>();
        for (String line : lines) {
            line = line.substring(0, line.length() - 1);
            String[] items = line.split(";");
            for (String item : items) {
                keywordList.add(item.trim());
            }
        }
        header.setKeywords(keywordList);
    }

    private void readFT(UniProtTxtHeader header, List<String> lines) {
        if (lines.isEmpty())
            return;

    }

    static void main() throws IOException {
        int n = 0;
        UniProtTxtReader reader = new UniProtTxtReader(Path.of("D:\\database\\uniprot\\uniprot_human_all_20260320.txt"));
        while (reader.hasNext()) {
            Seq seq = reader.next();
//            System.out.println(seq.getAccession());
            n++;
        }
        reader.close();
        System.out.println(n);
    }
}
