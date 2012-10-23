package name.hersen.livesplits;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FormattedCompetitor {
    private final String id;
    private final String name;
    private final String time;
    private final Collection<FormattedSplit> splits;
    private final Collection<String> laps;

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public FormattedCompetitor(String id, String name, Period time, String status, List<FormattedSplit> splits, Collection<String> laps) {
        this.id = id;
        this.name = name;
        this.splits = new ArrayDeque<FormattedSplit>(splits);
        this.laps = new ArrayDeque<String>(laps);

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

    public Collection<FormattedSplit> getSplits() {
        return Collections.unmodifiableCollection(splits);
    }

    public String getId() {
        return id;
    }

    public Collection<String> getLaps() {
        return Collections.unmodifiableCollection(laps);
    }
}
