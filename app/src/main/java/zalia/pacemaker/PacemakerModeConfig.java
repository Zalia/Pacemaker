package zalia.pacemaker;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Zalia on 28.03.2017.
 */

public class PacemakerModeConfig implements Serializable {

    private int id;
    private int ival1;
    private int ival2;
    private int ival3;
    private double dval1;
    private String sval1;
    private Map<Integer, Integer> imap;

    public PacemakerModeConfig(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIval1() {
        return ival1;
    }

    public void setIval1(int ival1) {
        this.ival1 = ival1;
    }

    public int getIval2() {
        return ival2;
    }

    public void setIval2(int ival2) {
        this.ival2 = ival2;
    }

    public int getIval3() {
        return ival3;
    }

    public void setIval3(int ival3) {
        this.ival3 = ival3;
    }

    public double getDval1() {
        return dval1;
    }

    public void setDval1(double dval1) {
        this.dval1 = dval1;
    }

    public String getSval1() {
        return sval1;
    }

    public void setSval1(String sval1) {
        this.sval1 = sval1;
    }

    public Map<Integer, Integer> getImap() {
        return imap;
    }

    public void setImap(Map<Integer, Integer> imap) {
        this.imap = imap;
    }

}
