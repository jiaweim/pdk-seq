package pdk.seq.io;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is used to represent RecName, AltName, and SubName in 'DE' section.
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 20 Mar 2026, 2:41 PM
 */
public class DEName {
    /**
     * the full name
     */
    private String fullName_;
    /**
     * An abbreviation of the full name or an acronym.
     */
    private ArrayList<String> shortNames_;
    /**
     * An Enzyme Commission number.
     */
    private ArrayList<String> ecNames_;

    /**
     * Create a {@link DEName}
     */
    public DEName() {}

    /**
     * the full name, available in RecName and SubName, optionally in AltName.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName_;
    }

    /**
     * Set the full name
     *
     * @param fullName full name
     */
    public void setFullName(String fullName) {
        this.fullName_ = fullName;
    }

    /**
     * An abbreviation of the full name or an acronym.
     *
     * @return short names
     */
    public @Nullable List<String> getShortNames() {
        return shortNames_;
    }

    /**
     * Add a short name
     *
     * @param shortName short name
     */
    public void addShortName(String shortName) {
        if (shortNames_ == null) {
            shortNames_ = new ArrayList<>();
        }
        shortNames_.add(shortName);
    }

    /**
     * An Enzyme Commission number.
     *
     * @return EC names
     */
    public @Nullable List<String> getECNames() {
        return ecNames_;
    }

    /**
     * Add an EC name
     *
     * @param ecName EC name
     */
    public void addECName(String ecName) {
        if (ecNames_ == null) {
            ecNames_ = new ArrayList<>();
        }
        ecNames_.add(ecName);
    }

    /**
     * Trim to size.
     */
    public void trimToSize() {
        if (shortNames_ != null) {
            shortNames_.trimToSize();
        }
        if (ecNames_ != null) {
            ecNames_.trimToSize();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DEName name)) return false;
        return Objects.equals(fullName_, name.fullName_)
                && Objects.equals(shortNames_, name.shortNames_)
                && Objects.equals(ecNames_, name.ecNames_);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName_, shortNames_, ecNames_);
    }
}
