package pdk.seq.io;

import java.util.ArrayList;

/**
 * GeneName in the 'GN' section in UniProt entry.
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 23 Mar 2026, 2:56 PM
 */
public class GeneName {

    private String name_;
    private ArrayList<String> synonyms_;
    private ArrayList<String> orderedLocusNames_;
    private ArrayList<String> orfNames_;

    public GeneName() {}

    public String getName() {
        return name_;
    }

    public void setName(String name_) {
        this.name_ = name_;
    }

    public ArrayList<String> getSynonyms() {
        return synonyms_;
    }

    public void setSynonyms(ArrayList<String> synonyms) {
        this.synonyms_ = synonyms;
    }

    public ArrayList<String> getOrderedLocusNames() {
        return orderedLocusNames_;
    }

    public void setOrderedLocusNames(ArrayList<String> orderedLocusNames) {
        this.orderedLocusNames_ = orderedLocusNames;
    }

    public ArrayList<String> getORFNames() {
        return orfNames_;
    }

    public void setORFNames(ArrayList<String> orfNames) {
        this.orfNames_ = orfNames;
    }
}
