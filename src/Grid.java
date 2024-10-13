import javax.swing.*;
import java.awt.*;

public class Grid extends JComponent{
    public static final int gridSize = 45;
    public static Color gridColor = new Color(33, 150, 150);
    public static Color borderColor = new Color(44, 180, 194);
    public static Color currentColor = new Color(90, 140, 185);
    public static Color lastColor = new Color(15,100,105);
    public static Color textColor = new Color(154,230,185);
    public static Color boundedColor = new Color(33,134,200);



    private Color thisColor = gridColor;
    private short val = 0;
    private int row;
    private int col;
    private float opacity = 0.7f;

    public Grid(int row,int col){
        this.row = row;
        this.col = col;
        this.setSize(gridSize, gridSize);
        this.setFont(new Font("Century",0,16));
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void drawPiece(Graphics g) {
        g.setColor(thisColor);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(borderColor);
        g.drawRect(0,0,this.getWidth(),this.getHeight());
        if(val != 0){
            g.setColor(textColor);
            g.drawString(String.valueOf(val),this.getWidth() / 2 - 2,this.getHeight() / 2);
        }
        repaint();
    }

    public void setColor(Color color){
        thisColor = color;
        repaint();
    }

    public void setVal(short val){
        this.val = val;
        repaint();
    }

    public void setOp(float opacity){
        this.opacity = opacity;
    }
    public short getVal(){
        return val;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.printComponents(g);
        drawPiece(g);
    }
}
