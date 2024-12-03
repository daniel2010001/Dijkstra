public class City extends Node {
    private int grains;

    public City(String name, int grains) {
        super(name, 0, 0);
        this.grains = grains;
    }

    public City(String name, int grains, int x, int y) {
        super(name, x, y);
        this.grains = grains;
    }

    public int getGrains() {
        return grains;
    }
}
