import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class GraphApp extends JFrame {
    public GraphApp() {
        setTitle("Grafo Interactivo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        Graph graph = new Graph(true);
        tabbedPane.addTab("Grafo Interactivo", graph);

        Node nodeA = new Node("A", 100, 100);
        Node nodeB = new Node("B", 300, 150);
        Node nodeC = new Node("C", 200, 300);
        Node nodeD = new Node("D", 400, 200);

        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addNode(nodeD);

        Edge edgeAB = new Edge(nodeA, nodeB, 5);
        Edge edgeBC = new Edge(nodeB, nodeC, 3);
        Edge edgeCD = new Edge(nodeC, nodeD, 2);
        Edge edgeDA = new Edge(nodeD, nodeA, 7);
        graph.addEdge(edgeAB);
        graph.addEdge(edgeBC);
        graph.addEdge(edgeCD);
        graph.addEdge(edgeDA);

        // Botón para resaltar aristas y nodos
        JButton highlightButton = new JButton("Resaltar Aristas");
        highlightButton.addActionListener(e -> {
            List<Edge> edgesToHighlight = new ArrayList<>();
            edgesToHighlight.add(edgeAB);
            edgesToHighlight.add(edgeBC);
            graph.highlightEdges(edgesToHighlight);
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(highlightButton);

        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        new GraphApp();
    }
}
