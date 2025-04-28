import frontend.Frontend;

public class Dragon {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: Dragon {file path}");
            System.exit(1);
        }
        var cfg = new Frontend().buildCfg(args[0]);
    }
}
