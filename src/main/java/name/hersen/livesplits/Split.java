package name.hersen.livesplits;

import org.joda.time.Period;

public class Split {
    private final Period time;
    private Control control;
    private Period lap;

    public Split(Period time, Control control, Period lap) {
        this.time = time;
        this.control = control;
        this.lap = lap;
    }

    public Period getTime() {
        return time;
    }

    public Control getControl() {
        return control;
    }

    public FormattedSplit format() {
        return new FormattedSplit(time, getControl(), lap);
    }
}
