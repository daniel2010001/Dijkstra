public class Edge {
    private final Node origin, destiny;
    private int weight;

    Edge(Node origin, Node destiny, int weight) {
        this.origin = origin;
        this.destiny = destiny;
        this.weight = weight;
    }

    /**
     * Devuelve el origen de la arista
     *
     * @return el origen de la arista
     */
    public Node getOrigin() {
        return this.origin;
    }

    /**
     * Devuelve el destino de la arista
     *
     * @return el destino de la arista
     */
    public Node getDestiny() {
        return this.destiny;
    }

    /**
     * Devuelve el peso de la arista
     *
     * @return el peso de la arista
     */
    public int getWeight() {
        return this.weight;
    }

    /**
     * Establece el peso de la arista
     *
     * @param weight el peso de la arista
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public Edge clone() {
        return new Edge(this.origin, this.destiny, this.weight);
    }

    @Override
    public String toString() {
        return "{ " + getWeight() + ": " + getOrigin() + " -> " + getDestiny() + " }";
    }
}