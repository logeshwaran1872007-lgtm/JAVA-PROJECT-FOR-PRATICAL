import java.awt.*;
import java.awt.event.*;

public class SmartHomeGUI extends Frame implements ActionListener {
    
    // UI Components
    private Label statusLabel;
    private Button lightBtn, fanBtn, acBtn;
    
    // State variables for appliances
    private boolean lightOn = false;
    private boolean fanOn = false;
    private boolean acOn = false;

    public SmartHomeGUI() {
        // Frame setup
        setTitle("Smart Home Automation Dashboard");
        setSize(400, 300);
        setLayout(new BorderLayout());

        // Header Label
        Label headerLabel = new Label("Smart Home Control Panel", Label.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(headerLabel, BorderLayout.NORTH);

        // Control Panel for Buttons
        Panel controlPanel = new Panel();
        controlPanel.setLayout(new GridLayout(3, 1, 10, 10));

        // Initialize Buttons
        lightBtn = new Button("Living Room Light: OFF");
        fanBtn = new Button("Bedroom Fan: OFF");
        acBtn = new Button("Air Conditioner: OFF");

        // Add Action Listeners
        lightBtn.addActionListener(this);
        fanBtn.addActionListener(this);
        acBtn.addActionListener(this);

        // Add buttons to control panel
        controlPanel.add(lightBtn);
        controlPanel.add(fanBtn);
        controlPanel.add(acBtn);
        
        // Add control panel to the center of the Frame
        add(controlPanel, BorderLayout.CENTER);

        // Status Label at bottom
        statusLabel = new Label("System Ready. All appliances are OFF.", Label.CENTER);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        add(statusLabel, BorderLayout.SOUTH);

        // Window listener to close the application
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    // Handle Button Clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == lightBtn) {
            lightOn = !lightOn; // Toggle state
            lightBtn.setLabel("Living Room Light: " + (lightOn ? "ON" : "OFF"));
            statusLabel.setText("Status: Living Room Light turned " + (lightOn ? "ON" : "OFF"));
        } else if (e.getSource() == fanBtn) {
            fanOn = !fanOn; // Toggle state
            fanBtn.setLabel("Bedroom Fan: " + (fanOn ? "ON" : "OFF"));
            statusLabel.setText("Status: Bedroom Fan turned " + (fanOn ? "ON" : "OFF"));
        } else if (e.getSource() == acBtn) {
            acOn = !acOn; // Toggle state
            acBtn.setLabel("Air Conditioner: " + (acOn ? "ON" : "OFF"));
            statusLabel.setText("Status: Air Conditioner turned " + (acOn ? "ON" : "OFF"));
        }
    }

    public static void main(String[] args) {
        // Launch the application
        SmartHomeGUI dashboard = new SmartHomeGUI();
        dashboard.setVisible(true);
    }
}
