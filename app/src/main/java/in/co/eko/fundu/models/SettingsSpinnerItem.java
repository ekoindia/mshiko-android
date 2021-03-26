package in.co.eko.fundu.models;/*
 * Created by Bhuvnesh
 */

public class SettingsSpinnerItem {
    private String itemName;
    private int index;

    public SettingsSpinnerItem(String itemName, int index) {
        this.itemName = itemName;
        this.index = index;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
