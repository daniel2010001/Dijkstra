import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Dijkstra {
    private Map<String, Node> nodes;
    private List<Edge> edges;
    private Map<String, Edge> response;

    public Dijkstra(List<Edge> edges) {
        this.nodes = new HashMap<String, Node>();
        for (Edge edge : edges) {
            this.nodes.put(edge.getOrigin().getLabel(), edge.getOrigin());
            this.nodes.put(edge.getDestiny().getLabel(), edge.getDestiny());
        }
        this.edges = edges;
    }

    public List<Node> getNodes() {
        return nodes.values().stream().collect(Collectors.toList());
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Map<String, Edge> getResponse() {
        return response;
    }

    private void verifyNode(String name) throws IllegalArgumentException {
        if (name == null)
            throw new IllegalArgumentException("El nombre del nodo no puede ser nulo");
        if (!this.nodes.containsKey(name))
            throw new IllegalArgumentException("El nodo " + name + " no existe");
    }

    public List<Edge> getEdgesByOrigin(String origin) {
        verifyNode(origin);
        return edges.stream()
                .filter(edge -> edge.getOrigin().getLabel().equals(origin))
                .collect(Collectors.toList());
    }

    public List<Edge> getEdgesByDestiny(String destiny) {
        verifyNode(destiny);
        return edges.stream()
                .filter(edge -> edge.getDestiny().getLabel().equals(destiny))
                .collect(Collectors.toList());
    }

    public List<Node> getNodesTo(String destiny) {
        verifyNode(destiny);
        List<Node> tracedEdge = new ArrayList<Node>();
        tracedEdge.add(this.nodes.get(destiny));
        Edge edge = this.response.get(destiny);
        while (edge != null && edge.getOrigin() != null) {
            tracedEdge.add(edge.getOrigin());
            edge = this.response.get(edge.getOrigin().getLabel());
        }
        Collections.reverse(tracedEdge);
        return tracedEdge;
    }

    public List<Edge> getEdgesTo(String destiny) {
        return getNodesTo(destiny).stream().map(node -> this.response.get(node.getLabel()))
                .collect(Collectors.toList());
    }

    public void minimize(String origin) {
        verifyNode(origin);
        this.response = new LinkedHashMap<String, Edge>();
        Map<String, Edge> exploredEdges = new HashMap<String, Edge>();
        exploredEdges.put(origin, new Edge(null, this.nodes.get(origin), 0));

        for (int i = 0; i < this.nodes.size() && !exploredEdges.isEmpty(); i++) {
            Map.Entry<String, Edge> currentNode = null;
            for (Map.Entry<String, Edge> entry : exploredEdges.entrySet())
                if (currentNode == null || currentNode.getValue().getWeight() > entry.getValue().getWeight())
                    currentNode = entry;

            for (Edge edge : getEdgesByOrigin(currentNode.getKey()))
                if (!this.response.containsKey(edge.getDestiny().getLabel())) {
                    int weight = currentNode.getValue().getWeight() + edge.getWeight();
                    if (!exploredEdges.containsKey(edge.getDestiny().getLabel())
                            || weight < exploredEdges.get(edge.getDestiny().getLabel()).getWeight())
                        exploredEdges.put(edge.getDestiny().getLabel(),
                                new Edge(edge.getOrigin(), edge.getDestiny(), weight));
                }

            this.response.put(currentNode.getKey(), currentNode.getValue());
            exploredEdges.remove(currentNode.getKey());
        }
    }

    public void maximize(String origin) {
        verifyNode(origin);
        int maxWeight = this.edges.stream()
                .mapToInt(edge -> edge.getWeight())
                .max().orElse(0);
        List<Edge> tempEdges = new ArrayList<Edge>(), thisEdges = this.edges;
        for (Edge edge : this.edges)
            tempEdges.add(new Edge(edge.getOrigin(), edge.getDestiny(), maxWeight - edge.getWeight()));
        this.edges = tempEdges;
        minimize(origin);
        this.edges = thisEdges;
        for (Edge edge : this.response.values())
            edge.setWeight(maxWeight * (getNodesTo(edge.getDestiny().getLabel()).size() - 1) - edge.getWeight());
    }

    public static void main(String[] args) {
        // crear ciudades
        City sanAntonio = new City("San Antonio", 100);
        City tolata = new City("Tolata", 100);
        City arbieto = new City("Arbieto", 100);
        City tarata = new City("Tarata", 100);
        City cliza = new City("Cliza", 100);
        City sanBenito = new City("San Benito", 100);
        City ucurenia = new City("Ucurenia", 100);
        City arani = new City("Arani", 100);
        City punata = new City("Punata", 100);
        City ansaldo = new City("Ansaldo", 100);
        // crear rutas
        List<Edge> routes = new ArrayList<Edge>();
        routes.add(new Route(sanAntonio, tolata, 18));
        routes.add(new Route(sanAntonio, arbieto, 22));
        routes.add(new Route(sanAntonio, tarata, 30));
        routes.add(new Route(tolata, cliza, 6));
        routes.add(new Route(tolata, sanBenito, 12));
        routes.add(new Route(tolata, ucurenia, 5));
        routes.add(new Route(arbieto, cliza, 20));
        routes.add(new Route(arbieto, ucurenia, 27));
        routes.add(new Route(tarata, sanBenito, 23));
        routes.add(new Route(tarata, ucurenia, 10));
        routes.add(new Route(cliza, arani, 12));
        routes.add(new Route(sanBenito, arani, 18));
        routes.add(new Route(sanBenito, punata, 14));
        routes.add(new Route(ucurenia, arani, 40));
        routes.add(new Route(ucurenia, punata, 13));
        routes.add(new Route(arani, ansaldo, 20));
        routes.add(new Route(punata, ansaldo, 16));

        Dijkstra dijkstra = new Dijkstra(List.copyOf(routes));
        dijkstra.maximize("Ucurenia");
        // dijkstra.minimize("San Antonio");
        System.out.println("Rutas:");
        for (Edge edge : dijkstra.getResponse().values())
            System.out.println(edge.getWeight() + ": " + dijkstra.getNodesTo(edge.getDestiny().getLabel()));
    }
}