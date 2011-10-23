package name.hersen.livesplits;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class Split {
    private Period time;
    private Control control;
    private PeriodFormatter formatter;

    public Split(Period time, Control control) {
        this.time = time;
        this.control = control;
        formatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSeparatorIfFieldsBefore(".")
                .minimumPrintedDigits(2)
                .printZeroAlways()
                .appendMinutes()
                .appendSeparator(".")
                .printZeroAlways()
                .appendSeconds()
                .toFormatter();
    }

    public Period getTime() {
        return time;
    }

    public Control getControl() {
        return control;
    }

    public String getTimeString() {
        return time.toString(formatter);
    }
}
