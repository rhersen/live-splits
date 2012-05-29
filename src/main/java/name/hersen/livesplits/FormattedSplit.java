package name.hersen.livesplits;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class FormattedSplit {
    private final String time;
    private final Control control;

    public FormattedSplit(Period time, Control control) {
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
    }

    public FormattedSplit(Split split) {
        this(split.getTime(), split.getControl());
    }

    public String getTime() {
        return time;
    }

    public Control getControl() {
        return control;
    }

}
