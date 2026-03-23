package pdk.seq.io;

import java.util.Objects;

/**
 * It indicates the host organism(s) that are susceptible to be infected by a virus.
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 23 Mar 2026, 5:24 PM
 */
public class OrganismHost {

    private final String taxId_;
    private final String hostName_;

    public OrganismHost(String taxId_, String hostName_) {
        this.taxId_ = taxId_;
        this.hostName_ = hostName_;
    }

    /**
     * tax id of the host.
     *
     * @return tax id of the host
     */
    public String getTaxId() {
        return taxId_;
    }

    /**
     * The official name and, optionally, a common name and/or synonym of the host.
     *
     * @return host name
     */
    public String getHostName() {
        return hostName_;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrganismHost host)) return false;
        return Objects.equals(taxId_, host.taxId_) && Objects.equals(hostName_, host.hostName_);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taxId_, hostName_);
    }

    @Override
    public String toString() {
        return "NCBI_TaxID=" + taxId_ + "; " + hostName_ + ".";
    }
}
