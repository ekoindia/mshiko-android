package in.co.eko.fundu.models;

/**
 * Created by divyanshu.jain on 8/12/2016.
 */
public class BanksNameItem {
    String bankname;
    String bankcode;
    int imps;
    int ifsc_req_ipms;


    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }
}
