import java.awt.*;
import javax.swing.*;

public class Tile extends JButton {
    public final int r;
    public final int c;
    private boolean revealed = false;
    private boolean flagged = false;

    public Tile(int r, int c, int fontSize){
        super("");
        this.r = r;
        this.c = c;
        setFocusable(false);
        setMargin(new Insets(0,0,0,0));
        setFont(getFont().deriveFont(Font.BOLD, fontSize));
    }
    public boolean isRevealed(){ return revealed; }
    public void setRevealed(boolean v){ revealed = v; }

    public boolean isFlagged(){ return flagged; }
    public void setFlagged(boolean v){ flagged = v; }
}