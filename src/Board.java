import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class Board {
    final static short BORDER = -100;
    private short[][] board;
    private ArrayList<Step> steps = new ArrayList<>();
    int h,l;
    int ev = 666666;
    public Board(int h,int l){
        //Initialize the board
        this.h = h;
        this.l = l;
        board = new short[h + 2][l + 2];
        for(int i = 0;i <= h + 1;i++){
            for(int j = 0;j <= l + 1;j++){
                board[i][j] = BORDER;
            }
        }
    }
    public Board(short[][] board){
        this.board = board;
        this.h = board.length - 2;
        this.l = board[0].length - 2;
        evaluate();
    }
    public void set(int x,int y,short v){
        board[x][y] = v;
    }
    public short getVal(int x,int y){
        return board[x][y];
    }
    public short[][] getBoard(){
        short[][] tmpBoard = new short[h + 2][l + 2];
        for(int i = 0;i < h + 2;i++){
            for(int j = 0;j < l + 2;j++){
                tmpBoard[i][j] = board[i][j];
            }
        }
        return tmpBoard;
    }
    public void setSteps(ArrayList<Step> a){
        steps = (ArrayList<Step>) a.clone();
    }
    public void addStep(Step s){
        steps.add(s);
    }
    public ArrayList<Step> getSteps(){
        return steps;
    }
    public void deleteLatestStep(){
        steps.remove(steps.size() - 1);
    }
    public int getStepsSize(){
        return steps.size();
    }
    public int firstCheck(int t){
        //Check the legality of board before bounding
        if(t >= h * l)
            return 0;
        int cnt = 0;
        int[] check = new int[t];
        for(int i = 1;i <= h;i++){
            for(int j = 1;j <= h;j++){
                if(board[i][j] < 0 || board[i][j] > t)
                    return 0;
                if(board[i][j] != 0){
                    if(check[board[i][j]] == 0){
                        check[board[i][j]] = 1;
                        cnt++;
                    }else{
                        return 0;
                    }
                }
            }
        }
        if(cnt != t)
            return 0;
        return 1;
    }

    public int evaluate(){
        //A rough evaluation of the "Distance" to restoration
        int v = 0;
        for(int i = 1;i <= h;i++){
            for(int j = 1;j <= l;j++){
                if(board[i][j] != 0) {
                    v += (Math.abs((board[i][j] - 1) / l + 1 - i) + Math.abs((board[i][j] - 1) % l + 1 - j));
                }
                //Manhattan Distance
                //distance = |i - x_goal| + |j - y_goal|
            }
        }
        ev = v;
        return v;
    }

    public int move(int x,int y,int d){
        //Move the number at point (x,y) with direction d
        short tmp = board[x][y];
        board[x][y] = board[x + Core.dx[d]][y + Core.dy[d]];
        board[x + Core.dx[d]][y + Core.dy[d]] = tmp;
        return 0;
    }



    public String toString(){
        //Return the unique identifier of current board
        StringBuilder s = new StringBuilder();
        for(int i = 1;i <= h;i++){
            for(int j = 1;j <= l;j++){
                s.append((char)board[i][j]);
            }
        }
        return s.toString();
    }
}
