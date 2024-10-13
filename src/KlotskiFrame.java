import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class KlotskiFrame extends JFrame {
    private Grid [][]grids;
    private int h,l;
    private Board board;
    private ArrayList<Step> steps;
    private int currentStep = -1;
    private HashMap<Short,Integer> bounded;

    static final ImageIcon bg = new ImageIcon("./res/20221105204132.png");
    static final Image bgImage = bg.getImage();
    JPanel background = new JPanel(){
        private static final long serialVersionUID = 1L;
        public void paint(Graphics g){
//            Calendar now = Calendar.getInstance();
            g.drawImage(bgImage,0,0,this.getWidth(),this.getHeight(),null);
        }
    };
    public KlotskiFrame(int h, int l, Board board, ArrayList<Step> steps, HashMap<Short,Integer> bounded){
        grids = new Grid[h][l];
        this.h = h;
        this.l = l;
        this.board = board;
        this.steps = steps;
        this.bounded = bounded;
        this.setTitle("Solution");
        this.setLayout(null);

//        Insets inset = this.getInsets();
//        this.setSize(45 * l + inset.left + inset.right + 220, 45 * h + inset.top + inset.bottom + 150);
        this.setSize(500,350);

        this.setBackground(new Color(16,204,88));
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        initializeGrids();
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        background.setOpaque(false);
        background.setLocation(0,0);
        background.setSize(this.getSize());
//        add(background);

        JLabel state = new JLabel(String.format("%d / %d Steps",currentStep + 1,steps.size()));
        state.setSize(120,30);
        state.setFont(new Font("Century",0,13));
        state.setLocation(45 * l + 100,10);
//        this.add(state);

        JButton next = new JButton("Next");
        next.setSize(90,30);
        next.setLocation(45 * l + 100,50);
        next.setFont(new Font("Century",0,13));
        next.addActionListener(e -> {
            if(currentStep < steps.size() - 1) {
                currentStep++;
                moveGrids(steps.get(currentStep).v, steps.get(currentStep).d);
                state.setText(String.format("%d / %d Steps",currentStep + 1,steps.size()));
                repaint();
            }
        });
//        this.add(next);

        JButton previous = new JButton("Previous");
        previous.setSize(90,30);
        previous.setLocation(45 * l + 100,100);
        previous.setFont(new Font("Century",0,13));
        previous.addActionListener(e -> {
            if(currentStep >= 0) {
                moveGrids(steps.get(currentStep).v, (steps.get(currentStep).d + 2) % 4);
                currentStep--;
                state.setText(String.format("%d / %d Steps",currentStep + 1,steps.size()));
                repaint();
            }
        });
//        this.add(previous);

//        JButton generate = new JButton("Generate");
//        generate.setSize(90,30);
//        generate.setLocation(45 * l + 100,150);
//        generate.setFont(new Font("Century",0,13));
//        this.add(generate);
//        generate.addActionListener(e -> {
//            try {
//                String result = JOptionPane.showInputDialog(null, "Size? Input 'm n',using empty to desperate（eg: 3 4）", "generateRandom", 2);
//                String[] str=result.split(" ",2);
//                int a = Integer.parseInt(str[0]);
//                int b = Integer.parseInt(str[1]);
//                Board ge =GenerateRandom.initialize(a, b);
//                Core core = new Core();
//                core.initialize(a,b,0,ge,GenerateRandom.bounded);
//            }catch (Exception exception){
//                JOptionPane.showMessageDialog (null, "alert", "alert", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//        repaint();
        this.getContentPane().add(background,-1);
        this.getContentPane().add(state,0);
        this.getContentPane().add(next,0);
        this.getContentPane().add(previous,0);
        this.repaint();

    }

    public void initializeGrids(){
        for (int i = 0;i < h;i++) {
            for (int j = 0;j < l;j++) {
                Grid grid = new Grid(i, j);
                grid.setLocation(j * Grid.gridSize + 30, i * Grid.gridSize + 30);
                grid.setVal(board.getVal(i + 1,j + 1));
                grids[i][j] = grid;
                if(bounded.get(grids[i][j].getVal()) !=0) grids[i][j].setColor(Grid.boundedColor);
                this.add(grids[i][j]);
            }
        }
    }

    public void moveGrids(short val,int d){
        int flag = 0;
        for(int i = 0;i < h;i++){
            for(int j = 0;j < l;j++){
                grids[i][j].setColor(Grid.gridColor);
                if(bounded.get(grids[i][j].getVal()) !=0) grids[i][j].setColor(Grid.boundedColor);
            }
        }
        for(int i = 0;i < h;i++){
            for(int j = 0;j < l;j++){
                if(grids[i][j].getVal() == val){
                    int boundType = bounded.get(val);
                    if(d > 1)
                        for(int k = 0;k < Core.boundDirx[boundType].length;k++){
                            moveGrids(i + Core.boundDirx[boundType][k], j + Core.boundDiry[boundType][k], d);
                        }
                    else
                        for(int k = Core.boundDirx[boundType].length - 1;k >= 0;k--){
                            moveGrids(i + Core.boundDirx[boundType][k], j + Core.boundDiry[boundType][k], d);
                        }
                    return;
                }
            }
        }
    }
    public void moveGrids(int x,int y,int d){
        short tmp = grids[x][y].getVal();
        grids[x][y].setVal(grids[x - Core.dx[d]][y - Core.dy[d]].getVal());
        grids[x - Core.dx[d]][y - Core.dy[d]].setVal(tmp);
        grids[x - Core.dx[d]][y - Core.dy[d]].setColor(Grid.currentColor);
        grids[x][y].setColor(Grid.lastColor);
    }
}
