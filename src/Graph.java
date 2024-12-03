import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Graph extends JPanel {
    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private Node selectedNode = null;
    private Point lastMousePosition = null;
    private List<Edge> highlightedEdges = new ArrayList<>();
    private List<Node> highlightedNodes = new ArrayList<>();

    public Graph(boolean interactive) {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        if (interactive) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedNode = getNodeAtPosition(e.getPoint());
                    lastMousePosition = e.getPoint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    selectedNode = null;
                    lastMousePosition = null;
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (selectedNode != null && lastMousePosition != null) {
                        Point currentPosition = e.getPoint();
                        int dx = currentPosition.x - lastMousePosition.x;
                        int dy = currentPosition.y - lastMousePosition.y;

                        selectedNode.move(dx, dy);
                        lastMousePosition = currentPosition;
                        repaint();
                    }
                }
            });
        }
    }

    public void addNode(Node node) {
        nodes.add(node);
        repaint();
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
        repaint();
    }

    public void highlightEdges(List<Edge> edgesToHighlight) {
        highlightedEdges = edgesToHighlight;
        highlightedNodes = new ArrayList<>();
        for (Edge edge : edgesToHighlight) {
            if (!highlightedNodes.contains(edge.getOrigin())) {
                highlightedNodes.add(edge.getOrigin());
            }
            if (!highlightedNodes.contains(edge.getDestiny())) {
                highlightedNodes.add(edge.getDestiny());
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar aristas
        for (Edge edge : edges) {
            Node origin = edge.getOrigin();
            Node destiny = edge.getDestiny();

            if (highlightedEdges.contains(edge)) {
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(3)); // Línea más gruesa
            } else {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1)); // Línea normal
            }

            g2d.drawLine(origin.getX(), origin.getY(), destiny.getX(), destiny.getY());

            // Dibujar peso de la arista
            int midX = (origin.getX() + destiny.getX()) / 2;
            int midY = (origin.getY() + destiny.getY()) / 2;
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.valueOf(edge.getWeight()), midX, midY);
        }

        // Dibujar nodos
        for (Node node : nodes) {
            g2d.setColor(highlightedNodes.contains(node) ? Color.RED : Color.BLUE);
            g2d.fillOval(node.getX() - 15, node.getY() - 15, 30, 30);

            g2d.setColor(Color.BLACK);
            g2d.drawString(node.getLabel(), node.getX() - g2d.getFontMetrics().stringWidth(node.getLabel()) / 2,
                    node.getY() - 20);
        }
    }

    private Node getNodeAtPosition(Point point) {
        for (Node node : nodes) {
            int dx = point.x - node.getX();
            int dy = point.y - node.getY();
            if (Math.sqrt(dx * dx + dy * dy) <= 15) {
                return node;
            }
        }
        return null;
    }
}
