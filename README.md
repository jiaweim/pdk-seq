# ProtSeqDB

Analysis toolkit for protein sequence databases.

## Read Fasta

```java
FastaReader reader = new FastaReader(file_path, true);

while (reader.hasNext()) {
    Seq seq = reader.next();
    n++;

    Header header = seq.getHeader();
    String sequence = seq.getSequence();

    DatabaseType databaseType = header.getDatabaseType();

    System.out.println(databaseType);
    System.out.println(header.getAccession());
    System.out.println(header.getEntryName());
    System.out.println(header.getProteinName());
    System.out.println(header.getOrganismName());
    System.out.println(header.getOrganismIdentifier());
    System.out.println(header.getGeneName());
    System.out.println(header.getProteinExistence());
    System.out.println(header.getSequenceVersion());
}

reader.close();
```