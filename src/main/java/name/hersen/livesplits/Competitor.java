package name.hersen.livesplits;

import org.joda.time.Duration;
import org.joda.time.Period;

import java.util.Collection;
import java.util.Deque;
import java.util.List;

public class Competitor {
    private final String name;
    private final Duration time;
    private final String status;
    private final Collection<Split> splits;
    private final String id;
    private final Deque<String> laps;

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
        return time == null ? null : time.toPeriod();
    }

    public Collection<Split> getSplits() {
        return splits;
    }

    public Competitor(String name, Duration time, String status, List<Split> splits, String id, Deque<String> laps) {
        this.name = name;
        this.time = time;
        this.status = status;
        this.splits = splits;
        this.id = id;
        this.laps = laps;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Deque<String> getLaps() {
        return laps;
    }
}
