import java.util.List;

public class App {
    public static void main(String[] args) {
        new MainMenu(new MainMenu.MenuListener() {
            @Override
            public void onSingleplayerStart() {
                new MinesweeperGUI();
            }

            @Override
            public void onMultiplayerStart(List<String> names) {
                new MinesweeperGUI(names);
            }
        });
    }
}
