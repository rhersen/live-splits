package name.hersen.livesplits;

import org.joda.time.Period;

public class Split {
    private final Period time;
    private final Control control;

    public Split(Control control, Period time) {
        this.time = time;
        this.control = control;
    }

    public Control getControl() {
        return control;
    }

    public Period getTime() {
        return time;
    }
}
