package name.hersen.livesplits;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class FormattedSplit {
    private String time;
    private Control control;

    public FormattedSplit(Period time, Control control) {
        this.control = control;
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSeparatorIfFieldsBefore(".")
                .minimumPrintedDigits(2)
                .printZeroAlways()
                .appendMinutes()
                .appendSeparator(".")
                .printZeroAlways()
                .appendSeconds()
                .toFormatter();
        this.time = time.toString(formatter);
    }

    public String getTime() {
        return time;
    }

    public Control getControl() {
        return control;
    }

}
