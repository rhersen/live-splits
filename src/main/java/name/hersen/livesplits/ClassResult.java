package name.hersen.livesplits;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClassResult {
    private final String name;

    private final Collection<FormattedCompetitor> list;

    public ClassResult(String name, List<FormattedCompetitor> list) {
        this.name = name;
        this.list = new ArrayDeque<FormattedCompetitor>(list);
    }

    public String getName() {
        return name;
    }

    public Collection<FormattedCompetitor> getList() {
        return Collections.unmodifiableCollection(list);
    }
}
