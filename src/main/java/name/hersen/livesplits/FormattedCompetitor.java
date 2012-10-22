package name.hersen.livesplits;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Deque;
import java.util.List;

public class FormattedCompetitor {
    private final String id;
    private final String name;
    private final String time;
    private final List<FormattedSplit> splits;
    private final Deque<String> laps;

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public FormattedCompetitor(String id, String name, Period time, String status, List<FormattedSplit> splits, Deque<String> laps) {
        this.id = id;
        this.name = name;
        this.splits = splits;
        this.laps = laps;

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

    public Deque<String> getLaps() {
        return laps;
    }
}
