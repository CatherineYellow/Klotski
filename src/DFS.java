import java.util.*;

public class DFS {
    HashSet<String> set = new HashSet<>();//Using a set to avoid repeated nodes.
    HashMap<Short,Integer> bounded = new HashMap<>();
    /*
    Bound type:
     0: Unbounded
     1 / 2 / 3: 2*1 / 1*2 / 2*2 Bounded (Top left element)
     -1: Bounded (Not top left element)
    */
    Board board;
    int sz,minev = 678910;
    Random random = new Random();
    Stack<Board> nodes = new Stack<>();

    int flag = 2;
    static final int[] dx = new int[]{-1,0,1,0};
    static final int[] dy = new int[]{0,-1,0,1};
    static final char[] dir = new char[]{'D','R','U','L'};
    static final int[][] boundDirx = new int[][]{{0},{0,1}, {0,0}, {0,1,0,1}};
    static final int[][] boundDiry = new int[][]{{0},{0,0}, {0,1}, {0,0,1,1}};
    static final HashMap<String,Integer> boundMap = new HashMap<>(){{
        put("1*1",0);
        put("2*1",1);
        put("1*2",2);
        put("2*2",3);
    }};
    Scanner in = new Scanner(System.in);

    public DFS(){}

    public void initialize(){
        int h = in.nextInt();
        int l = in.nextInt();
        board = new Board(h,l);
        bounded = new HashMap<>();
        for(int i = 1;i <= h;i++){
            for(int j = 1;j <= l;j++){
                board.set(i,j,in.nextShort());
                bounded.put(board.getVal(i,j),0);
            }
        }
        int n = in.nextInt();
        initialize(h,l,n,board,bounded);
    }

    public void initialize(int h, int l, int n, Board bo, HashMap<Short,Integer> boed){
        random.setSeed(System.currentTimeMillis());
        this.board = bo;
        this.bounded = boed;
        sz = h * l;
        for(int i = 0;i < n;i++){
            short v = in.nextShort();
            int tmpx,tmpy;
            int boundType = boundMap.get(in.next());
            for(int j = 1;j <= h;j++){
                for(int k = 1;k <= l;k++){
                    if(board.getVal(j,k) == v){
                        bounded.replace(v,boundType);
                        for(int b = 1;b < boundDirx[boundType].length;b++){
                            bounded.replace(board.getVal(j + boundDirx[boundType][b],k + boundDiry[boundType][b]),-1);
                            if(board.getVal(j + boundDirx[boundType][b],k + boundDiry[boundType][b])
                                    - board.getVal(j,k) != boundDirx[boundType][b] * l + boundDiry[boundType][b]){
                                System.out.println("No");
                                return;
                            }
                        }
                    }
                }
            }
        }
        board.evaluate();
        nodes.add(board);
        set.add(board.toString());
        ArrayList<Step> s;
        long start = System.currentTimeMillis();
        do{
            s = Search();
            if(nodes.size() > 700000) {
                if(nodes.peek().ev < h + l + 2) {
                    nodes.removeIf(e -> (e.ev > h + l + 6));
                }
                int tmp = nodes.peek().getStepsSize();//Prune in a magical way
//                int tmpev = nodes.peek().ev;
                nodes.removeIf(e->(e.getStepsSize() >= tmp - 10));//True MAGIC!
                if(System.currentTimeMillis() - start > (1500L * h * l)){
                    break;//Timeout
                }
            }
        }while(nodes.size() <= 700000 && nodes.size() != 0 && s == null);
        nodes.clear();
        if(s != null) {
            System.out.println("Yes");
            System.out.println(s.size());
//            ArrayList<Step> steps = new ArrayList<>();
            for(int i = 0;i < s.size();i++){
//                Step now = s.poll();
                System.out.printf("%d %c\n",s.get(i).v,dir[s.get(i).d]);
//                System.out.printf("%d %c\n",now.v,dir[now.d]);
//                steps.add(now);
            }
            KlotskiFrame frame = new KlotskiFrame(h,l,board,s,bounded);
        }
        else
            System.out.println("No");
//        System.out.printf("%d ms",System.currentTimeMillis() - start);
    }

    public ArrayList<Step> Search(){
        if(nodes.size() == 0) {
            return null;
        }
        Board now = nodes.pop();
        if(now.ev == 0){//Answer found!
            return now.getSteps();
        }

        String id;
        for(int i = 1;i <= now.h;i++){
            for(int j = 1;j <= now.l;j++){
                if(now.getVal(i,j) == 0){
                    for(int k = 0;k < 4;k++){
                        int tmpx = i + dx[k];
                        int tmpy = j + dy[k];
                        short tmpv = now.getVal(tmpx,tmpy);
                        if(tmpv != 0 && tmpv != Board.BORDER){
                            //Bound Check:
                            int boundType = bounded.get(tmpv);
                            if(boundType == -1){
                                tmpx += dx[k];
                                tmpy += dy[k];
                                tmpv = now.getVal(tmpx,tmpy);
                                if(tmpv <= 0 || k >= 2)
                                    continue;
                                boundType = bounded.get(tmpv);
                                if(boundType <= 0)
                                    continue;
                                if(k == 0 && boundType == 2)
                                    continue;
                                if(k == 1 && boundType == 1)
                                    continue;
                            }
                            if(k % 2 == 0 && (boundType == 2 || boundType == 3) && now.getVal(i,j + 1) != 0){
                                continue;
                            }//Check up and down
                            if(k % 2 == 1 && (boundType == 1 || boundType == 3) && now.getVal(i + 1,j) != 0){
                                continue;
                            }//Check left and right
                            Board tmp = new Board(now.getBoard());
                            int isD = k == 0 ? -1 : 1;
                            int isR = k == 1 ? -1 : 1;
                            for (int b = 0; b < boundDirx[boundType].length; b++) {
                                tmp.move(i + boundDirx[boundType][b] * isD, j + boundDiry[boundType][b] * isR, k);
                            }
                            tmp.setSteps(now.getSteps());
                            tmp.addStep(new Step(tmpv,k));
                            tmp.evaluate();
                            if(tmp.ev == 0) {
                                //Answer found!
                                return tmp.getSteps();
                            }
                            if(tmp.getStepsSize() > 800) {
                                tmp = null;
                                continue;
                            }
                            id = tmp.toString();
                            if(set.contains(id))
                                continue;
                            set.add(id);
                            minev = Math.min(minev,tmp.ev);
                            nodes.add(tmp);
                        }
                    }
                }
            }
        }
        return null;
    }
    public void prt(Board b){
        //A function for convenient and intuitive debugging
        //Print out the necessary information of a Board
        for(int i = 1;i <= b.h;i++){
            for(int j = 1;j <= b.l;j++){
                System.out.printf("%d ",b.getBoard()[i][j]);
            }
            System.out.println();
        }
        System.out.println(b.ev);
    }

//    public static void main(String[] args) {
//        DFS dfs = new DFS();
//        dfs.initialize();
//    }
}
