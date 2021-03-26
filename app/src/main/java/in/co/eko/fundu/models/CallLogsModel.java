package in.co.eko.fundu.models;

/**
 * Created by divyanshu.jain on 9/20/2016.
 */
public class CallLogsModel extends ContactItem {
    int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public boolean equals(Object o) {
        return this.getContactNumber().equals(((CallLogsModel) o).getContactNumber());

    }


}
