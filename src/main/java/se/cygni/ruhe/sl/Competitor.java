package se.cygni.ruhe.sl;

import org.apache.commons.collections.Transformer;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.collect;

public class Competitor {
    private String name;
    private Period time;
    private String status;
    private List splits;
    private PeriodFormatter formatter;

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

    public String getTimeString() {
        if (time != null) {
            return time.toString(formatter);
        } else {
            return status;
        }
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


    public List getSplitStrings() {
        return (List) collect(splits, periodToString());
    }

    private Transformer periodToString() {
        return new Transformer() {
            public Object transform(Object o) {
                Period split = (Period) o;
                return split.toString(formatter);
            }
        };
    }
}
