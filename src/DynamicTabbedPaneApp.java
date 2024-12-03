import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class DynamicTabbedPaneApp extends JFrame {
    private JTabbedPane superTabbedPane;
    private DynamicTabComponent dynamicTabComponent;

    public DynamicTabbedPaneApp() {
        setTitle("SuperTab con Subtabs Dinámicos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        superTabbedPane = new JTabbedPane();

        // Tab de Input
        JPanel inputTab = new JPanel(new FlowLayout());
        JTextField inputField = new JTextField(5);
        JButton updateButton = new JButton("Actualizar Subtabs");
        inputTab.add(new JLabel("Número de subtabs:"));
        inputTab.add(inputField);
        inputTab.add(updateButton);
        superTabbedPane.addTab("Tab Input", inputTab);

        // Tab estático
        JPanel staticTab = new JPanel(new FlowLayout());
        staticTab.add(new JLabel("Este es un tab estático."));
        // Tab dinámico
        dynamicTabComponent = new DynamicTabComponent("Tab Estático", staticTab);
        superTabbedPane.addTab("Tabs Dinámicos", dynamicTabComponent);

        // Acción para actualizar los subtabs
        updateButton.addActionListener(e -> {
            try {
                int input = Integer.parseInt(inputField.getText());
                if (input < 0)
                    throw new NumberFormatException();
                dynamicTabComponent.updateSubTabs(input);
                superTabbedPane.setSelectedIndex(1); // Cambiar al tab dinámico
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Por favor ingrese un número entero positivo.",
                        "Error de Entrada",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        add(superTabbedPane);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DynamicTabbedPaneApp::new);
    }
}
