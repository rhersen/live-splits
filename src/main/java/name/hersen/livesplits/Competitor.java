package name.hersen.livesplits;

import org.joda.time.Duration;
import org.joda.time.Period;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;

public class Competitor {
    private final String name;
    private final Duration time;
    private final String status;
    private final Collection<Split> splits;
    private final String id;
    private final Collection<String> laps;

    public String getName() {
        return name;
    }

    public Period getTime() {
        return time == null ? null : time.toPeriod();
    }

    public Collection<Split> getSplits() {
        return Collections.unmodifiableCollection(splits);
    }

    public Competitor(String name, Duration time, String status, Collection<Split> splits, String id, Collection<String> laps) {
        this.name = name;
        this.time = time;
        this.status = status;
        this.splits = new ArrayDeque<Split>(splits);
        this.id = id;
        this.laps = new ArrayDeque<String>(laps);
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Collection<String> getLaps() {
        return Collections.unmodifiableCollection(laps);
    }
}
