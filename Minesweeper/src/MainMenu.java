import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class MainMenu {

    private JFrame frame;
    private JTextField[] nameFields;
    private int playerCount;

    public interface MenuListener {
        void onSingleplayerStart();
        void onMultiplayerStart(List<String> names);
    }

    private MenuListener listener;

    public MainMenu(MenuListener listener){
        this.listener = listener;
        SwingUtilities.invokeLater(this::build);
    }

    private void build(){
        frame = new JFrame("Minesweeper Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Multiplayer Minesweeper", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1,4,10,10));

        JButton one = new JButton("1 Player");
        JButton two = new JButton("2 Players");
        JButton three = new JButton("3 Players");
        JButton four = new JButton("4 Players");

        one.addActionListener(e -> startNameInput(1));
        two.addActionListener(e -> startNameInput(2));
        three.addActionListener(e -> startNameInput(3));
        four.addActionListener(e -> startNameInput(4));

        buttons.add(one);
        buttons.add(two);
        buttons.add(three);
        buttons.add(four);

        frame.add(buttons, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startNameInput(int count){
        this.playerCount = count;

        if(count == 1){
            listener.onSingleplayerStart();
            frame.dispose();
            return;
        }

        JDialog dialog = new JDialog(frame, "Enter Player Names", true);
        dialog.setLayout(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new GridLayout(count,1,5,5));
        nameFields = new JTextField[count];

        for(int i=0;i<count;i++){
            JTextField f = new JTextField("Player " + (i+1));
            nameFields[i] = f;
            fieldsPanel.add(f);
        }

        JButton start = new JButton("Start Multiplayer");
        start.addActionListener(e -> {
            List<String> names = new ArrayList<>();
            for(JTextField f : nameFields){
                names.add(f.getText().trim());
            }
            listener.onMultiplayerStart(names);
            dialog.dispose();
            frame.dispose();
        });

        dialog.add(fieldsPanel, BorderLayout.CENTER);
        dialog.add(start, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
}
