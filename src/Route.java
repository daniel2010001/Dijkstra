public class Route extends Edge {
    private final City origin, destination;
    private int fuel;

    public Route(City origin, City destination, int fuel) {
        super(origin, destination, 0);
        this.origin = origin;
        this.destination = destination;
        this.fuel = fuel;
    }

    public int getFuel() {
        return this.fuel;
    }

    @Override
    public int getWeight() {
        return 100 * this.destination.getGrains() / this.fuel;
    }

    @Override
    public Route clone() {
        return new Route(this.origin, this.destination, this.fuel);
    }
}
