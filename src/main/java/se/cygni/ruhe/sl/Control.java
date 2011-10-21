package se.cygni.ruhe.sl;

public class Control {
    private String code;
    private double x;
    private double y;

    public Control(String code, double x, double y) {
        this.code = code;
        this.x = x;
        this.y = y;
    }

    public String getCode() {
        return code;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Control{" +
                "code='" + code + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
