package name.hersen.livesplits;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.List;

public class Competitor {
    private String name;
    private Period time;
    private String status;
    private List splits;
    private String id;

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

    public void setName(String name) {
        this.name = name;
    }

    public Period getTime() {
        return time;
    }

    public Competitor() {
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
    }

    public void setTime(Period period) {
        this.time = period;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public List getSplits() {
        return splits;
    }

    public void setSplits(List splits) {
        this.splits = splits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings({"unchecked"})
    public FormattedCompetitor format() {
        List<FormattedSplit> formattedSplits = new ArrayList<FormattedSplit>();
        List<Split> splitList = getSplits();
        for (Split split : splitList) {
            if (split != null) {
                formattedSplits.add(split.format());
            }
        }
        return new FormattedCompetitor(getId(), getName(), getTime(), status, formattedSplits);
    }
}
