package name.hersen.livesplits;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.List;

public class FormattedCompetitor {
    private String id;
    private String name;
    private String time;
    private List<FormattedSplit> splits;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Competitor{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getTime() {
        return time;
    }

    public FormattedCompetitor(String id, String name, Period time, String status, List<FormattedSplit> splits) {
        this.id = id;
        this.name = name;
        this.splits = splits;

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
        this.time = time == null ? status : time.toString(formatter);
    }

    public List<FormattedSplit> getSplits() {
        return splits;
    }

    public String getId() {
        return id;
    }

}
