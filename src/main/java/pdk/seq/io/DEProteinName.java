package pdk.seq.io;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This object contain protein names in the DE (DEscription) lines.
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 20 Mar 2026, 3:37 PM
 */
public class DEProteinName {

    private DEName recName_;
    private ArrayList<DEName> altNames_;
    private ArrayList<DEName> subNames_;
    private String allergen_;
    private String biotech_;
    private ArrayList<String> cdAntigens_;
    private ArrayList<String> inn_;

    /**
     * Return the name recommended by the UniProt consortium in the 'DE' section.
     * <p>
     * The RecName is optionally in TrEMBL.
     *
     * @return {@link DEName}
     */
    public @Nullable DEName getRecName() {
        return recName_;
    }

    /**
     * Set the {@link DEName}
     *
     * @param recName {@link DEName} instance
     */
    public void setRecName(DEName recName) {
        this.recName_ = recName;
    }

    /**
     * Return the allergen
     *
     * @return allergen name
     */
    public @Nullable String getAllergen() {
        return allergen_;
    }

    /**
     * Set the allergen name
     *
     * @param allergen allergen name
     */
    public void setAllergen(String allergen) {
        this.allergen_ = allergen;
    }

    /**
     * A name used in a biotechnological context.
     *
     * @return biotech name
     */
    public @Nullable String getBiotech() {
        return biotech_;
    }

    /**
     * Set the name used in a biotechnological context.
     *
     * @param biotech name used in a biotechnological context.
     */
    public void setBiotech(String biotech) {
        this.biotech_ = biotech;
    }

    /**
     * CD antigen alter names
     *
     * @return CD antigen names
     */
    public @Nullable List<String> getCDAntigens() {
        return cdAntigens_;
    }

    /**
     * add a new CD antigen names
     *
     * @param cdAntigen a CD antigen name
     */
    public void addCDAntigen(String cdAntigen) {
        if (this.cdAntigens_ == null) {
            this.cdAntigens_ = new ArrayList<>();
        }
        this.cdAntigens_.add(cdAntigen);
    }

    /**
     * Return all international nonproprietary names.
     *
     * @return international nonproprietary names.
     */
    public List<String> getINNs() {
        return inn_;
    }

    /**
     * add an international nonproprietary name.
     *
     * @param inn an international nonproprietary name.
     */
    public void addINN(String inn) {
        if (this.inn_ == null) {
            this.inn_ = new ArrayList<>();
        }
        this.inn_.add(inn);
    }

    /**
     * Add a new AltName
     *
     * @param altName {@link DEName} instance
     */
    public void addAltName(DEName altName) {
        if (this.altNames_ == null) {
            this.altNames_ = new ArrayList<>();
        }
        this.altNames_.add(altName);
    }

    public @Nullable List<DEName> getAltNames() {
        return altNames_;
    }

    /**
     * Add a new SubName
     *
     * @param subName {@link DEName} instance
     */
    public void addSubName(DEName subName) {
        if (this.subNames_ == null) {
            this.subNames_ = new ArrayList<>();
        }
        this.subNames_.add(subName);
    }

    /**
     * Return sub names
     *
     * @return list of subnames
     */
    public @Nullable List<DEName> getSubNames() {
        return subNames_;
    }

    public void trimToSize() {
        if (cdAntigens_ != null) {
            cdAntigens_.trimToSize();
        }
        if (inn_ != null) {
            inn_.trimToSize();
        }
        if (altNames_ != null) {
            altNames_.trimToSize();
        }
        if (subNames_ != null) {
            subNames_.trimToSize();
        }
    }
}
