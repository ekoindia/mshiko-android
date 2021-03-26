package in.co.eko.fundu.event;

/**
 * Created by zartha on 7/25/17.
 */

public class ReplaceFragment {
    private Class from,to;
    public ReplaceFragment(Class to,Class from){
        this.from = from;
        this.to = to;
    }

    public Class getFrom() {
        return from;
    }

    public Class getTo() {
        return to;
    }
}
