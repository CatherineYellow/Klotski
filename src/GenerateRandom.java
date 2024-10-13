import java.util.*;

public class GenerateRandom {
    static Board board;
    static HashMap<Short,Integer> bounded = new HashMap<>();
    static HashSet<String> set = new HashSet<>();
    static final int[] dx = new int[]{-1,0,1,0};
    static final int[] dy = new int[]{0,-1,0,1};
    static final int[][] boundDirx = new int[][]{{0},{0,1}, {0,0}, {0,1,0,1}};
    static final int[][] boundDiry = new int[][]{{0},{0,0}, {0,1}, {0,0,1,1}};
    static final HashMap<Integer,String> reversedBoundMap = new HashMap<>(){{
        put(0,"1*1");
        put(1,"2*1");
        put(2,"1*2");
        put(3,"2*2");
    }};
    static Random r = new Random(System.currentTimeMillis());

    public static void main(String[] args){
        initialize(3 + r.nextInt(4),3 + r.nextInt(4));
        prt();
        KlotskiFrame frame = new KlotskiFrame(board.h,board.l,board,board.getSteps(),bounded);
    }

    public static Board initialize(int h, int l) {
        Scanner in = new Scanner(System.in);
        board = new Board(h, l);
        int numCnt = h * l - 1 - r.nextInt(3);
        short m = 1;  //填入数据
        for(int i = 1;i <= h;i++){
            for(int j = 1;j <= l;j++){
                board.set(i,j,(short)0);
                if(m < numCnt) {
                    board.set(i, j, m);
                    m++;
                }
                bounded.put(board.getVal(i,j),0);
            }
        }
        int boundCnt = r.nextInt(4);
        for(int i = 0;i < boundCnt;i++){
            short v = (short)(r.nextInt(numCnt) + 1);
            int boundType = r.nextInt(3) + 1;
            int flag = 1;
            for(int j = 1;j <= h;j++){
                for(int k = 1;k <= l;k++){
                    if(board.getVal(j,k) == v && bounded.get(v) == 0){
                        for(int b = 0;b < boundDirx[boundType].length;b++){
                            if(board.getVal(j + boundDirx[boundType][b],k + boundDiry[boundType][b])
                                    - board.getVal(j,k) != boundDirx[boundType][b] * l + boundDiry[boundType][b]){
                                flag = 0;
                                break;
                            }
                        }
                        if(flag == 0)
                            break;
                        bounded.replace(v,boundType);
                        for(int b = 1;b < boundDirx[boundType].length;b++){
                            bounded.replace(board.getVal(j + boundDirx[boundType][b],k + boundDiry[boundType][b]),-1);
                        }
                    }
                }
            }
        }
        board.set(h, l, (short)0);
        bounded.put((short) 0,0);
        return Generate();
    }

    public static Board Generate() {
        Random random = new Random();
        int step = random.nextInt(800)+20;
        //交换0和周围东西
        for (int s = 0; s < step; s++) {
            ArrayList<Step> canMove = new ArrayList<>();
            for (int i = 1; i <= board.h; i++) {
                for (int j = 1; j <= board.l; j++) {
                    if (board.getVal(i, j) == 0) {
                        for (int k = 0; k < 4; k++) {
                            int tmpx = i + dx[k];
                            int tmpy = j + dy[k];
                            short tmpv = board.getVal(tmpx, tmpy);
                            if (tmpv != 0 && tmpv != Board.BORDER) {
                                //Bound Check:
                                int boundType = bounded.get(tmpv);
                                if (boundType == -1) {
                                    tmpx += dx[k];
                                    tmpy += dy[k];
                                    tmpv = board.getVal(tmpx, tmpy);
                                    if (tmpv <= 0 || k >= 2)
                                        continue;
                                    boundType = bounded.get(tmpv);
                                    if (boundType <= 0)
                                        continue;
                                    if (k == 0 && boundType == 2)
                                        continue;
                                    if (k == 1 && boundType == 1)
                                        continue;
                                }
                                if (k % 2 == 0 && (boundType == 2 || boundType == 3) && board.getVal(i, j + 1) != 0) {
                                    continue;
                                }//Check up and down
                                if (k % 2 == 1 && (boundType == 1 || boundType == 3) && board.getVal(i + 1, j) != 0) {
                                    continue;
                                }//Check left and right
                                Board tmp = new Board(board.getBoard());
                                int isD = k == 0 ? -1 : 1;
                                int isR = k == 1 ? -1 : 1;
                                for (int b = 0; b < boundDirx[boundType].length; b++) {
                                    tmp.move(i + boundDirx[boundType][b] * isD, j + boundDiry[boundType][b] * isR, k);
                                }
                                tmp.evaluate();
                                String id = tmp.toString();
                                if (set.contains(id))
                                    continue;
                                canMove.add(new Step(tmpv,k));
                            }
                        }
                    }
                }
            }
            if(canMove.size() == 0)
                break;
            int next = r.nextInt(canMove.size());
            move(canMove.get(next).v, canMove.get(next).d);
            board.addStep(canMove.get(next));
            set.add(board.toString());
        }
        ArrayList<Step> st = board.getSteps();
        ArrayList<Step> rvst = new ArrayList<>();
        for(int i = st.size() - 1;i >= 0;i--){
            rvst.add(new Step(st.get(i).v,(st.get(i).d + 2) % 4));
        }
        board.setSteps(rvst);
        return board;
    }

    public static void move(short val,int d){
        for(int i = 1;i <= board.h;i++){
            for(int j = 1;j <= board.l;j++){
                if(board.getVal(i,j) == val){
                    int boundType = bounded.get(val);
                    if(d > 1)
                        for(int k = 0;k < Core.boundDirx[boundType].length;k++){
                            board.move(i + Core.boundDirx[boundType][k], j + Core.boundDiry[boundType][k], (d + 2) % 4);
                        }
                    else
                        for(int k = Core.boundDirx[boundType].length - 1;k >= 0;k--){
                            board.move(i + Core.boundDirx[boundType][k], j + Core.boundDiry[boundType][k], (d + 2) % 4);
                        }
                    return;
                }
            }
        }
    }
    public static void prt(){
        System.out.print(board.h + " "+ board.l);
        System.out.println();
        for (int i = 1; i <= board.h; i++) {
            for (int j = 1; j <= board.l; j++) {
                System.out.print(board.getBoard()[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        int flag = 0;
        for(short i : bounded.keySet()){
            if(bounded.get(i) > 0) {
                flag ++;
            }
        }
        System.out.println(flag);
        for(short i : bounded.keySet()){
            if(bounded.get(i) > 0) {
                System.out.printf("%d %s\n", i, reversedBoundMap.get(bounded.get(i)));
                flag = 1;
            }
        }
    }
}
