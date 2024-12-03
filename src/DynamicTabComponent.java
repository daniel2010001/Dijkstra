import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class DynamicTabComponent extends JPanel {
    private JTabbedPane subTabbedPane;
    private JPanel staticTab;
    private String titleStaticTab;
    private int numberOfTabs;

    public DynamicTabComponent(String titleStaticTab, JPanel staticTab) {
        this.titleStaticTab = titleStaticTab;
        this.staticTab = staticTab;
        setLayout(new BorderLayout());
        subTabbedPane = new JTabbedPane();
        add(subTabbedPane, BorderLayout.CENTER);
        numberOfTabs = 0;
    }

    /**
     * Actualiza los subtabs dinámicamente con el número especificado.
     * 
     * @param input El número base para crear subtabs.
     */
    public void updateSubTabs(int input) {
        subTabbedPane.removeAll();
        subTabbedPane.add(titleStaticTab, staticTab);
        numberOfTabs = input;
        for (int i = 1; i <= numberOfTabs; i++) {
            JPanel tabPanel = new JPanel(new FlowLayout());
            tabPanel.add(new JLabel("Contenido del subtab " + i));
            subTabbedPane.addTab("Subtab " + i, tabPanel);
        }
    }

    public void updateSubTabs(Map<String, Component> components) {
        subTabbedPane.removeAll();
        subTabbedPane.add(titleStaticTab, staticTab);
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            JPanel tabPanel = new JPanel(new FlowLayout());
            tabPanel.add(entry.getValue());
            subTabbedPane.addTab(entry.getKey(), tabPanel);
        }
    }

    /**
     * Obtiene el contenedor de subtabs para añadir a un tab superior.
     * 
     * @return El componente JTabbedPane con los subtabs dinámicos.
     */
    public JTabbedPane getSubTabbedPane() {
        return subTabbedPane;
    }
}
