package name.hersen.livesplits;

import org.joda.time.Period;

public class Split {
    private Period time;
    private Control control;

    public Split(Period time, Control control) {
        this.time = time;
        this.control = control;
    }

    public Period getTime() {
        return time;
    }

    public Control getControl() {
        return control;
    }

    public FormattedSplit format() {
        return new FormattedSplit(getTime(), getControl());
    }
}
