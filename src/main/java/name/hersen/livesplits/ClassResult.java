package name.hersen.livesplits;

import java.util.List;

public class ClassResult {
    private String name;

    private List<FormattedCompetitor> list;

    public ClassResult(String name, List<FormattedCompetitor> list) {
        this.name = name;
        this.list = list;
    }

    @Override
    public String toString() {
        return "ClassResult{" +
                "name='" + name + '\'' +
                ", list=" + list +
                '}';
    }

    public String getName() {
        return name;
    }

    public List<FormattedCompetitor> getList() {
        return list;
    }
}
