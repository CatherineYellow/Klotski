public class Main{
    public static void main(String[] args){
        try {
            if (args[0].equals("terminal") || args[0].equals("gui")) {
                Core core = new Core();
                core.initialize(args[0]);
            } else {
                System.out.println("argument error");
            }
        }catch (Exception e){
            System.out.println("no argument");
        }
    }
}