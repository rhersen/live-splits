package name.hersen.livesplits;

import org.joda.time.Period;

public class Split {
    private final Period time;
    private final Control control;
    private final Period lap;

    public Split(Control control, Period time, Period lap) {
        this.time = time;
        this.control = control;
        this.lap = lap;
    }

    public Control getControl() {
        return control;
    }

    public FormattedSplit format() {
        return new FormattedSplit(time, getControl(), lap);
    }
}
