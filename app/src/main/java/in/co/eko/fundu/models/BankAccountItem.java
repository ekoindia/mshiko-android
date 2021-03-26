package in.co.eko.fundu.models;

/**
 * Created by zartha on 3/8/18.
 */

public class BankAccountItem {
    private String name;
    private String recipientId;
    private String bankCode;
    private String accountNumber;
    private String ifsc;
    private boolean pinAvailable;

    public boolean getPinAvailable(){
        return pinAvailable;
    }

    public void setPinAvailable(boolean pinAvailable) {
        this.pinAvailable = pinAvailable;
    }

    public String getIfsc() {
        return ifsc;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getName() {
        return name;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }


    @Override
    public boolean equals(Object o) {
        return this.getRecipientId().equalsIgnoreCase(((BankAccountItem) o).getRecipientId());

    }

}

