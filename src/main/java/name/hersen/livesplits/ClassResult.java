package name.hersen.livesplits;

import java.util.List;

public class ClassResult {
    private final String name;

    private final List<FormattedCompetitor> list;

    public ClassResult(String name, List<FormattedCompetitor> list) {
        this.name = name;
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public List<FormattedCompetitor> getList() {
        return list;
    }
}
