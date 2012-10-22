package name.hersen.livesplits;

public class Control {
    private final String code;
    private final double x;
    private final double y;

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

}
