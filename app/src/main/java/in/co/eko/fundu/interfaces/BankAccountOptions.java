package in.co.eko.fundu.interfaces;


import in.co.eko.fundu.models.BankAccountItem;

public interface BankAccountOptions {
    void onCheckBalance(BankAccountItem item);
    void onSetPin(BankAccountItem item);
}