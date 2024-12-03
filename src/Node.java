public class Node {
    private final String label;
    private int x, y;

    Node(String label, int x, int y) {
        this.label = label;
        this.x = x;
        this.y = y;
    }

    /**
     * Devuelve el nombre del nodo
     *
     * @return el nombre del nodo
     */
    public String getLabel() {
        return this.label;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    @Override
    public Node clone() {
        return new Node(this.label, this.x, this.y);
    }

    @Override
    public String toString() {
        return this.label;
    }
}
