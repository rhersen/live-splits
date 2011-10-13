package se.cygni.ruhe.sl;

public class Competitor {
    private String name;
    private String time;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
