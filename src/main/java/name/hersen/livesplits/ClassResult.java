package name.hersen.livesplits;

import java.util.List;

public class ClassResult {
    private String name;

    @Override
    public String toString() {
        return "ClassResult{" +
                "name='" + name + '\'' +
                ", list=" + list +
                '}';
    }

    private List<FormattedCompetitor> list;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<FormattedCompetitor> getList() {
        return list;
    }

    public void setList(List<FormattedCompetitor> list) {
        this.list = list;
    }
}
