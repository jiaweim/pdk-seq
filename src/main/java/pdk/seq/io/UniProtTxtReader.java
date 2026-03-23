package pdk.seq.io;

import com.google.common.base.Splitter;
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
 * @version 1.0.0⭐
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
        readGN(header, lineMap_.get("GN")); // optional
        readOS(header, lineMap_.get("OS")); // Once or more
        readOG(header, lineMap_.get("OG")); // Optional
        readOC(header, lineMap_.get("OC")); // once or more
        readOX(header, lineMap_.get("OX")); // once
        readOH(header, lineMap_.get("OH")); // optional
        // skip references:RN, RP, RC, RX, RG, RA, RT, RL
        // skip CC lines
        // skip DR lines
        readPE(header, lineMap_.get("PE")); // once
        readKW(header, lineMap_.get("KW")); // optional
        readFT(header, lineMap_.get("FT")); // once or more in Swiss-Prot, optional in TrEMBL

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
     * The DE (DEscription) lines contain general descriptive information about the sequence stored.⭐
     */
    protected static void readDE(UniProtTxtHeader header, List<String> lines) {
        int idx = 0;
        int startIdx = 0;
        for (; idx < lines.size(); idx++) {
            String line = lines.get(idx);
            if (line.startsWith(TAG_DE_Includes) || line.startsWith(TAG_DE_Contains) || line.startsWith(TAG_DE_Flags)) {
                idx--;
                break;
            }
        }
        int endIdx = (idx == lines.size()) ? idx - 1 : idx;

//        System.out.println(startIdx + " " + endIdx);
        DEProteinName name = readDEProteinName(lines, startIdx, endIdx);
        header.setDEProteinName(name);

        if (endIdx < (lines.size() - 1)) {
            idx = endIdx + 1;
            ArrayList<DEProteinName> contains = new ArrayList<>();
            ArrayList<DEProteinName> includes = new ArrayList<>();
            for (; idx < lines.size(); idx++) {
                String line = lines.get(idx);
                if (line.startsWith(TAG_DE_Contains)) {
                    startIdx = idx + 1;
                    for (int i = startIdx; i < lines.size(); i++) {
                        line = lines.get(i);
                        if (!line.startsWith(TAG_DE_TAB2)) {
                            break;
                        }
                        idx = i;
                    }
                    endIdx = idx;
                    DEProteinName proteinName = readDEProteinNameAlt(lines, startIdx, endIdx);
                    contains.add(proteinName);
                } else if (line.startsWith(TAG_DE_Includes)) {
                    startIdx = idx + 1;
                    for (int i = startIdx; i < lines.size(); i++) {
                        line = lines.get(i);
                        if (!line.startsWith(TAG_DE_TAB2)) {
                            break;
                        }
                        idx = i;
                    }
                    endIdx = idx;

                    DEProteinName proteinName = readDEProteinNameAlt(lines, startIdx, endIdx);
                    includes.add(proteinName);
                } else if (line.startsWith(TAG_DE_Flags)) {
                    String flag = line.substring(TAG_DE_Flags.length(), line.length() - 1);
                    header.setFlags(flag);
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
    }

    /**
     * Read lines in a given range
     *
     * @param lines    lines of DE lines except 'includes' and 'contains' sections
     * @param startIdx start index, inclusive
     * @param endIdx   end index, inclusive
     * @return {@link DEProteinName} instance
     */
    protected static DEProteinName readDEProteinName(List<String> lines, int startIdx, int endIdx) {
        DEProteinName proteinName = new DEProteinName();
        for (int idx1 = startIdx; idx1 <= endIdx; idx1++) {
            String line = lines.get(idx1);
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
                if (idx1 < endIdx) {
                    for (int idx2 = idx1 + 1; idx2 <= endIdx; idx2++) {
                        line = lines.get(idx2);
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
                            idx1 = idx2;
                        } else {
                            idx1 = idx2 - 1;
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
                if (idx1 < endIdx) {
                    for (int idx2 = idx1 + 1; idx2 < lines.size(); idx2++) {
                        line = lines.get(idx2);
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
                            idx1 = idx2;
                        } else {
                            idx1 = idx2 - 1;
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
                if (idx1 < endIdx) {
                    for (int idx2 = idx1 + 1; idx2 < lines.size(); idx2++) {
                        line = lines.get(idx2);
                        if (line.startsWith(TAG_DE_TAB)) {
                            int ecTagIdx = line.indexOf(TAG_DE_EC);
                            if (ecTagIdx >= 0) {
                                String ecName = line.substring(ecTagIdx + TAG_DE_EC.length(), line.length() - 1);
                                subName.addECName(ecName);
                            } else {
                                throw new IllegalStateException("Unknown DE line under AltName: " + line);
                            }
                            idx1 = idx2;
                        } else {
                            idx1 = idx2 - 1;
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

        proteinName.trimToSize();
        return proteinName;
    }

    /**
     * Read lines in a given range
     *
     * @param lines    lines of 'includes', 'contains' sections
     * @param startIdx start index, inclusive
     * @param endIdx   end index, inclusive
     * @return {@link DEProteinName} instance
     */
    protected static DEProteinName readDEProteinNameAlt(List<String> lines, int startIdx, int endIdx) {
        DEProteinName proteinName = new DEProteinName();
        for (int idx1 = startIdx; idx1 <= endIdx; idx1++) {
            String line = lines.get(idx1).substring(2);
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
                if (idx1 < endIdx) {
                    for (int idx2 = idx1 + 1; idx2 <= endIdx; idx2++) {
                        line = lines.get(idx2);
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
                            idx1 = idx2;
                        } else {
                            idx1 = idx2 - 1;
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
                if (idx1 < endIdx) {
                    for (int idx2 = idx1 + 1; idx2 < lines.size(); idx2++) {
                        line = lines.get(idx2);
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
                            idx1 = idx2;
                        } else {
                            idx1 = idx2 - 1;
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
                if (idx1 < endIdx) {
                    for (int idx2 = idx1 + 1; idx2 < lines.size(); idx2++) {
                        line = lines.get(idx2);
                        if (line.startsWith(TAG_DE_TAB)) {
                            int ecTagIdx = line.indexOf(TAG_DE_EC);
                            if (ecTagIdx >= 0) {
                                String ecName = line.substring(ecTagIdx + TAG_DE_EC.length(), line.length() - 1);
                                subName.addECName(ecName);
                            } else {
                                throw new IllegalStateException("Unknown DE line under AltName: " + line);
                            }
                            idx1 = idx2;
                        } else {
                            idx1 = idx2 - 1;
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
        proteinName.trimToSize();

        return proteinName;
    }

    /**
     * Read GN lines.⭐
     *
     * @param header {@link UniProtTxtHeader} to store the parse result
     * @param lines  GN lines
     */
    protected static void readGN(UniProtTxtHeader header, List<String> lines) {
        if (lines.isEmpty())
            return;

        ArrayList<GeneName> geneNames = new ArrayList<>();
        int startIdx = 0;
        StringJoiner joiner = new StringJoiner(" ");
        String line;
        for (int i = startIdx; i < lines.size(); i++) {
            line = lines.get(i);
            if (line.equals("and")) {
                String fullGeneLine = joiner.toString();
                GeneName geneName = readGN(fullGeneLine);
                geneNames.add(geneName);

                joiner = new StringJoiner(" ");
            } else {
                joiner.add(line);
            }
        }

        GeneName geneName = readGN(joiner.toString());
        geneNames.add(geneName);

        geneNames.trimToSize();
        header.setGeneNames(geneNames);
    }

    protected static final Splitter SEMICOLON_Splitter = Splitter.on(';').trimResults().omitEmptyStrings();
    protected static final Splitter COMMA_Splitter = Splitter.on(',').trimResults().omitEmptyStrings();

    protected static GeneName readGN(String line) {
        GeneName geneName = new GeneName();

        for (String nameValue : SEMICOLON_Splitter.split(line)) {
            if (nameValue.startsWith("Name=")) {
                String name = nameValue.substring(5);
                geneName.setName(name);
            } else if (nameValue.startsWith("Synonyms=")) {
                String synonyms = nameValue.substring(9);
                List<String> synonymList = COMMA_Splitter.splitToList(synonyms);
                ArrayList<String> synonymNames = new ArrayList<>(synonymList);
                geneName.setSynonyms(synonymNames);
            } else if (nameValue.startsWith("OrderedLocusNames=")) {
                String orderedLocusNames = nameValue.substring("OrderedLocusNames=".length());
                geneName.setOrderedLocusNames(new ArrayList<>(COMMA_Splitter.splitToList(orderedLocusNames)));
            } else if (nameValue.startsWith("ORFNames=")) {
                String orfNames = nameValue.substring("ORFNames=".length());
                geneName.setORFNames(new ArrayList<>(COMMA_Splitter.splitToList(orfNames)));
            }
        }

        return geneName;
    }

    /**
     * Read Organism Species⭐
     *
     * @param header {@link UniProtTxtHeader} to store the result
     * @param lines  OS lines
     */
    protected static void readOS(UniProtTxtHeader header, List<String> lines) {
        StringJoiner joiner = new StringJoiner(" ");
        for (String line : lines) {
            joiner.add(line);
        }

        String name = joiner.toString();
        header.setOrganismName(name.substring(0, name.length() - 1));
    }

    /**
     * Read OG (OrGanelle) line⭐
     */
    private void readOG(UniProtTxtHeader header, List<String> lines) {
        if (lines.isEmpty())
            return;
        String first = lines.getFirst();
        header.setOrganelle(first.substring(0, first.length() - 1));
    }

    /**
     * read The OC (Organism Classification) lines.⭐
     */
    protected static void readOC(UniProtTxtHeader header, List<String> lines) {
        StringJoiner joiner = new StringJoiner(" ");
        for (String line : lines) {
            joiner.add(line);
        }
        String name = joiner.toString();

        header.setOrganismClassification(new ArrayList<>(SEMICOLON_Splitter.splitToList(name.substring(0, name.length() - 1))));
    }

    /**
     * Read The OX (Organism taxonomy cross-reference) line⭐
     */
    protected static void readOX(UniProtTxtHeader header, List<String> lines) {
        assert lines.size() == 1;
        String line = lines.getFirst();
        int idx1 = line.indexOf("=") + 1;
        String code = line.substring(idx1, line.length() - 1);
        header.setOrganismIdentifier(code);
    }

    /**
     * The OH (Organism Host) line⭐
     */
    protected static void readOH(UniProtTxtHeader header, List<String> lines) {
        if (lines.isEmpty())
            return;
        ArrayList<OrganismHost> hosts = new ArrayList<>(lines.size());
        for (String line : lines) {
            int idx1 = line.indexOf("=") + 1;
            int idx2 = line.indexOf("; ");

            String taxId = line.substring(idx1, idx2);
            String hostName = line.substring(idx2 + 2, line.length() - 1);
            OrganismHost host = new OrganismHost(taxId, hostName);
            hosts.add(host);
        }
        header.setOrganismHostTaxIDs(hosts);
    }

    /**
     * Read protein existence line.⭐
     */
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
