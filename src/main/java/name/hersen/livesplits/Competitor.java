package name.hersen.livesplits;

import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;

public class Competitor {
    private final String name;
    private final Period time;
    private final String status;
    private final List<Split> splits;
    private final String id;

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

    public Period getTime() {
        return time;
    }

    public List<Split> getSplits() {
        return splits;
    }

    public Competitor(String name, Period time, String status, List<Split> splits, String id) {
        this.name = name;
        this.time = time;
        this.status = status;
        this.splits = splits;
        this.id = id;
    }

    public String getId() {
        return id;
    }

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
