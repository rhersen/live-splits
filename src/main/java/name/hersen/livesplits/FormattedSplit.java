package name.hersen.livesplits;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class FormattedSplit {
    private String time;
    private Control control;
    private String lap;

    public FormattedSplit(Period time, Control control, Period lap) {
        this.control = control;
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSeparatorIfFieldsBefore(".")
                .appendMinutes()
                .appendSeparator(".")
                .minimumPrintedDigits(2)
                .printZeroAlways()
                .appendSeconds()
                .toFormatter();
        this.time = time.toString(formatter);
        this.lap = lap.toString(formatter);
    }

    public String getTime() {
        return time;
    }

    public Control getControl() {
        return control;
    }

    public String getLap() {
        return lap;
    }
}
