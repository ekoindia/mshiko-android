package in.co.eko.fundu.models;

public class SideMenuItem {

    private String title;
    private String onClick;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOnClick() {
        return onClick;
    }

    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    @Override
    public boolean equals(Object obj) {
        SideMenuItem newItem = (SideMenuItem) obj;
        return this.title.equalsIgnoreCase(newItem.getTitle());
    }
}
