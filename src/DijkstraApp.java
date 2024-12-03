import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class DijkstraApp extends JFrame {
    private final Map<String, Node> nodes;
    private final List<Edge> edges;
    private Graph graph;
    private DefaultTableModel cityTableModel, routeTableModel, resultTableModel;
    private DynamicTabComponent dynamicTabComponent;
    private JComboBox<String> originComboBox, destinationComboBox, algorithmComboBox, availabilityComboBox;
    private JTextArea conclusionTextArea;

    public DijkstraApp() {
        nodes = new HashMap<String, Node>();
        edges = new ArrayList<Edge>();

        setTitle("Interfaz de Múltiples Páginas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane mainTabbedPane = new JTabbedPane();

        JTabbedPane dataTabbedPane = new JTabbedPane();
        dataTabbedPane.addTab("Ciudades", cityPage());
        dataTabbedPane.addTab("Rutas", routesPage());
        dataTabbedPane.addTab("Grafo", graphPage());
        mainTabbedPane.addTab("Datos", dataTabbedPane);

        // Agrupar "Resultados Parciales" y "Resultados Finales"
        JTabbedPane resultsTabbedPane = new JTabbedPane();

        // Pestaña "Resultados Parciales" con dos tabs
        dynamicTabComponent = new DynamicTabComponent("Tabla de resultados parciales", resultPage());
        resultsTabbedPane.addTab("Resultados Parciales", dynamicTabComponent);

        // Pestaña "Resultados Finales" con dos tabs
        JTabbedPane finalesTabbedPane = new JTabbedPane();
        finalesTabbedPane.addTab("Conclusión", conclusionPage());
        finalesTabbedPane.addTab("Grafo de resultados", new JLabel("Resultados del Algoritmo 1"));
        resultsTabbedPane.addTab("Resultado Final", finalesTabbedPane);

        // Añadir el super tab "Resultados"
        mainTabbedPane.addTab("Resultados", resultsTabbedPane);

        // Listener para actualizar los resultados cuando se seleccionen ciertas
        // pestañas
        mainTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (mainTabbedPane.getSelectedIndex() == 1) { // "Resultados"
                    updateResultsTable();
                }
            }
        });

        add(mainTabbedPane);

        dataset();
        setVisible(true);
    }

    private JPanel cityPage() {
        JPanel panel = new JPanel(new BorderLayout());
        cityTableModel = new DefaultTableModel(new Object[] { "#", "Ciudad", "Granos", "Disponibilidad" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        JTable cityTable = new JTable(cityTableModel);
        cityTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int lastRow = cityTableModel.getRowCount() - 1;
                String cityName = (String) cityTableModel.getValueAt(lastRow, 1);
                int cityGrains = Integer.parseInt((String) cityTableModel.getValueAt(lastRow, 2));
                if (!cityName.isEmpty() && cityGrains > 0) {
                    nodes.put(cityName, new City(cityName, cityGrains));
                    cityTableModel.addRow(new Object[] { cityTableModel.getRowCount() + 1, "", "", "" });
                }
            }
        });
        availabilityComboBox = new JComboBox<>(new String[] { "Habilitado", "Deshabilitado" });
        TableColumn availabilityColumn = cityTable.getColumnModel().getColumn(3);
        availabilityColumn.setCellEditor(new DefaultCellEditor(availabilityComboBox));
        cityTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        cityTable.getColumnModel().getColumn(0)
                .setCellRenderer((table, value, isSelected, hasFocus, row, column) -> new JLabel(value.toString()));
        panel.add(new JScrollPane(cityTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel routesPage() {
        JPanel panel = new JPanel(new BorderLayout());
        routeTableModel = new DefaultTableModel(
                new Object[] { "#", "Origen", "Destino", "Combustible", "Disponibilidad" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        JTable routeTable = new JTable(routeTableModel);
        originComboBox = new JComboBox<>(getCityNames());
        destinationComboBox = new JComboBox<>(getCityNames());
        JComboBox<String> originComboBoxCellEditor = new JComboBox<>(getCityNames());
        JComboBox<String> destinyComboBoxCellEditor = new JComboBox<>(getCityNames());
        algorithmComboBox = new JComboBox<>(new String[] { "Minimizar", "Maximizar" });
        availabilityComboBox = new JComboBox<>(new String[] { "Habilitado", "Deshabilitado" });
        TableColumn originColumn = routeTable.getColumnModel().getColumn(1);
        originColumn.setCellEditor(new DefaultCellEditor(originComboBoxCellEditor));
        TableColumn destinationColumn = routeTable.getColumnModel().getColumn(2);
        destinationColumn.setCellEditor(new DefaultCellEditor(destinyComboBoxCellEditor));
        TableColumn availabilityColumn = routeTable.getColumnModel().getColumn(4);
        availabilityColumn.setCellEditor(new DefaultCellEditor(availabilityComboBox));
        routeTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int lastRow = routeTableModel.getRowCount() - 1;
                String origin = (String) routeTableModel.getValueAt(lastRow, 1);
                String destination = (String) routeTableModel.getValueAt(lastRow, 2);
                String granos = (String) routeTableModel.getValueAt(lastRow, 3);
                if (origin != null && destination != null && granos != null
                        && !origin.isEmpty() && !destination.isEmpty() && !granos.isEmpty()) {
                    edges.add(new Route((City) nodes.get(origin), (City) nodes.get(destination),
                            Integer.parseInt(granos)));
                    routeTableModel.addRow(new Object[] { routeTableModel.getRowCount() + 1, "", "", "", "" });
                }
            }
        });
        cityTableModel.addTableModelListener(e -> {
            originComboBox.setModel(new DefaultComboBoxModel<>(getCityNames()));
            destinationComboBox.setModel(new DefaultComboBoxModel<>(getCityNames()));
            originComboBoxCellEditor.setModel(new DefaultComboBoxModel<>(getCityNames()));
            destinyComboBoxCellEditor.setModel(new DefaultComboBoxModel<>(getCityNames()));
        });
        routeTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        routeTable.getColumnModel().getColumn(0)
                .setCellRenderer((table, value, isSelected, hasFocus, row, column) -> new JLabel(value.toString()));
        panel.add(new JScrollPane(routeTable), BorderLayout.CENTER);
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.add(new JLabel("Ciudad de origen: "));
        footerPanel.add(originComboBox);
        footerPanel.add(new JLabel("Ciudad de destino: "));
        footerPanel.add(destinationComboBox);
        footerPanel.add(new JLabel("Algoritmo: "));
        footerPanel.add(algorithmComboBox);
        panel.add(footerPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel graphPage() {
        JPanel panel = new JPanel(new BorderLayout());
        graph = new Graph(true);
        panel.add(graph, BorderLayout.CENTER);
        return panel;
    }

    private void dataset() {
        // crear ciudades
        List<City> cities = new ArrayList<City>();
        cities.add(new City("San Antonio", 100, 45, 185));
        cities.add(new City("Tolata", 100, 185, 95));
        cities.add(new City("Arbieto", 100, 180, 190));
        cities.add(new City("Tarata", 100, 180, 300));
        cities.add(new City("Cliza", 100, 390, 65));
        cities.add(new City("San Benito", 100, 380, 200));
        cities.add(new City("Ucurenia", 100, 380, 340));
        cities.add(new City("Arani", 100, 530, 160));
        cities.add(new City("Punata", 100, 520, 250));
        cities.add(new City("Ansaldo", 100, 640, 200));
        // crear rutas
        List<Route> routes = new ArrayList<Route>();
        routes.add(new Route(cities.get(0), cities.get(1), 18));
        routes.add(new Route(cities.get(0), cities.get(2), 22));
        routes.add(new Route(cities.get(0), cities.get(3), 30));
        routes.add(new Route(cities.get(1), cities.get(4), 6));
        routes.add(new Route(cities.get(1), cities.get(5), 12));
        routes.add(new Route(cities.get(1), cities.get(6), 5));
        routes.add(new Route(cities.get(2), cities.get(4), 20));
        routes.add(new Route(cities.get(2), cities.get(6), 27));
        routes.add(new Route(cities.get(3), cities.get(5), 23));
        routes.add(new Route(cities.get(3), cities.get(6), 10));
        routes.add(new Route(cities.get(4), cities.get(7), 12));
        routes.add(new Route(cities.get(5), cities.get(7), 18));
        routes.add(new Route(cities.get(5), cities.get(8), 14));
        routes.add(new Route(cities.get(6), cities.get(7), 40));
        routes.add(new Route(cities.get(6), cities.get(8), 13));
        routes.add(new Route(cities.get(7), cities.get(9), 20));
        routes.add(new Route(cities.get(8), cities.get(9), 16));
        for (City city : cities) {
            cityTableModel.addRow(new Object[] { cityTableModel.getRowCount() + 1,
                    city.getLabel(), String.valueOf(city.getGrains()), "Habilitado" });
            nodes.put(city.getLabel(), city);
            graph.addNode(city);
        }
        cityTableModel.addRow(new Object[] { cityTableModel.getRowCount() + 1, "", "", "" });
        for (Route route : routes) {
            routeTableModel.addRow(new Object[] { routeTableModel.getRowCount() + 1,
                    route.getOrigin().getLabel(), route.getDestiny().getLabel(), String.valueOf(route.getFuel()),
                    "Habilitado" });
            edges.add(route);
            graph.addEdge(route);
        }
        routeTableModel.addRow(new Object[] { routeTableModel.getRowCount() + 1, "", "", "", "" });
    }

    private JPanel resultPage() {
        JPanel panel = new JPanel(new BorderLayout());
        resultTableModel = new DefaultTableModel(new Object[] { "#", "Ruta", "Granos T.", "Coste", "Ponderación" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable resultTable = new JTable(resultTableModel);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        resultTable.getColumnModel().getColumn(0)
                .setCellRenderer((table, value, isSelected, hasFocus, row, column) -> new JLabel(value.toString()));
        panel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        return panel;
    }

    private String[] getCityNames() {
        List<String> cities = new ArrayList<>();
        for (int i = 0; i < cityTableModel.getRowCount(); i++) {
            String cityName = (String) cityTableModel.getValueAt(i, 1);
            if (cityName != null && !cityName.isEmpty())
                cities.add(cityName);
        }
        return cities.toArray(new String[0]);
    }

    private List<Edge> getRoutes() {
        Map<String, City> nodes = new HashMap<String, City>();
        for (int i = 0; i < cityTableModel.getRowCount() - 1; i++) {
            try {
                String cityName = (String) cityTableModel.getValueAt(i, 1);
                int grains = Integer.parseInt((String) cityTableModel.getValueAt(i, 2));
                String availability = (String) cityTableModel.getValueAt(i, 3);
                if (cityName != null && !cityName.isEmpty() && availability.equals("Habilitado"))
                    nodes.put(cityName, new City(cityName, grains));
            } catch (Exception e) {
                System.out.println("Error en la ciudad " + i + 1 + " -> " + e);
            }
        }
        List<Edge> routes = new ArrayList<Edge>();
        for (int i = 0; i < routeTableModel.getRowCount() - 1; i++)
            try {
                String origin = (String) routeTableModel.getValueAt(i, 1);
                String destiny = (String) routeTableModel.getValueAt(i, 2);
                int fuel = Integer.parseInt((String) routeTableModel.getValueAt(i, 3));
                String availability = (String) routeTableModel.getValueAt(i, 4);
                if (nodes.containsKey(origin) && nodes.containsKey(destiny) && availability.equals("Habilitado")) {
                    routes.add(new Route(nodes.get(origin), nodes.get(destiny), fuel));
                    routes.add(new Route(nodes.get(destiny), nodes.get(origin), fuel));
                }
            } catch (Exception e) {
                System.out.println("Error en la ruta " + i + 1 + " -> " + e);
            }
        return routes;
    }

    private void updateResultsTable() {
        resultTableModel.setRowCount(0);
        Map<String, Object[]> routes = new HashMap<String, Object[]>();
        List<Edge> edges = getRoutes();
        Dijkstra dijkstra = new Dijkstra(edges);
        if (algorithmComboBox.getSelectedItem().equals("Maximizar"))
            dijkstra.maximize(originComboBox.getSelectedItem().toString());
        else
            dijkstra.minimize(originComboBox.getSelectedItem().toString());
        int minCities = dijkstra.getNodesTo(destinationComboBox.getSelectedItem().toString()).size();
        for (Edge edge : dijkstra.getResponse().values()) {
            List<Edge> filteredEdge = new ArrayList<Edge>(edges);
            for (Node node : dijkstra.getNodesTo(edge.getDestiny().getLabel()))
                filteredEdge.removeAll(dijkstra.getEdgesByDestiny(node.getLabel()));
            Dijkstra dijkstra2 = new Dijkstra(filteredEdge);
            Edge edge2 = null;
            try {
                if (algorithmComboBox.getSelectedItem().equals("Maximizar"))
                    dijkstra2.maximize(edge.getDestiny().getLabel());
                else
                    dijkstra2.minimize(edge.getDestiny().getLabel());
                edge2 = dijkstra2.getResponse().get(destinationComboBox.getSelectedItem().toString());
            } catch (Exception e) {
            }
            if (edge2 != null) {
                Set<Node> cities = new LinkedHashSet<>(dijkstra.getNodesTo(edge.getDestiny().getLabel()));
                cities.addAll(dijkstra2.getNodesTo(destinationComboBox.getSelectedItem().toString()));
                int grains = cities.stream().mapToInt(node -> ((City) node).getGrains()).sum();
                int fuel = dijkstra.getEdgesTo(edge.getDestiny().getLabel()).stream().map(Edge::getWeight)
                        .reduce(0, Integer::sum);
                System.out.println(dijkstra.getEdgesTo(edge.getDestiny().getLabel()) + " - "
                        + dijkstra2.getEdgesTo(destinationComboBox.getSelectedItem().toString()));
                int weight = edge.getWeight() + edge2.getWeight();
                if (minCities >= cities.size())
                    routes.put(cities.toString(), new Object[] { cities, grains, fuel, weight });
            }
        }
        Map<String, Component> input = new LinkedHashMap<String, Component>();
        for (Object[] row : routes.values()) {
            resultTableModel.addRow(
                    new Object[] { resultTableModel.getRowCount() + 1, row[0].toString(), row[1], row[2], row[3] });
            Graph graph = new Graph(false);
            for (Node node : dijkstra.getNodes()) {
                Node minCity = this.nodes.get(node.getLabel());
                node.setX(minCity.getX());
                node.setY(minCity.getY());
                graph.addNode(node);
            }
            for (Edge edge : dijkstra.getEdges())
                graph.addEdge(edge);
            // graph.highlightEdges(dijkstra.getEdgesTo(row[0].toString()));
            input.put("Resultado " + (resultTableModel.getRowCount()), graph);
        }
        updateConclusionText(conclusionText(dijkstra, routes));
        dynamicTabComponent.updateSubTabs(input);
    }

    private String conclusionText(Dijkstra dijkstra, Map<String, Object[]> routes) {
        StringBuilder textContent = new StringBuilder();
        textContent.append("Conclusión.\n\n");
        textContent.append("Lista de ciudades de la ruta más mejor:\n\n");
        int index = 1;
        List<Node> tracedRoute = dijkstra.getNodesTo(destinationComboBox.getSelectedItem().toString());
        for (Node node : tracedRoute)
            textContent.append(index++ + ". " + node + " y sus granos son: \t" + ((City) node).getGrains() + "\n");
        textContent.append("\nEl total de granos: \t");
        textContent.append(routes.get(tracedRoute.toString())[1]);
        textContent.append("\nEl peso de la ruta: \t");
        textContent.append(routes.get(tracedRoute.toString())[2]);
        textContent.append("\nLa ponderación: \t");
        textContent.append(routes.get(tracedRoute.toString())[3]);
        return textContent.toString();
    }

    private void updateConclusionText(String newText) {
        if (conclusionTextArea != null)
            conclusionTextArea.setText(newText);
    }

    private JPanel conclusionPage() {
        JPanel panel = new JPanel(new BorderLayout());
        conclusionTextArea = new JTextArea();
        conclusionTextArea.setEditable(false);
        conclusionTextArea.setLineWrap(true);
        conclusionTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(conclusionTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DijkstraApp::new);
    }
}
