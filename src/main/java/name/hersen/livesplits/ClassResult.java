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

    private List<Competitor> list;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Competitor> getList() {
        return list;
    }

    public void setList(List<Competitor> list) {
        this.list = list;
    }
}
