import java.awt.*;
import javax.swing.*;

public class TileUI extends JButton {
    public final int r;
    public final int c;

    public TileUI(int r, int c, int fontSize){
        super("");
        this.r = r;
        this.c = c;
        setFocusable(false);
        setMargin(new Insets(0,0,0,0));
        setFont(getFont().deriveFont(Font.BOLD, fontSize));
    }
}