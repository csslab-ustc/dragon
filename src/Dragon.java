import cfg.Cfg;
import frontend.Driver;

public class Dragon {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: Dragon {file path}");
            System.exit(1);
        }
        Driver driver = new Driver(args[0]);
        Cfg.Program.pp(driver.getControlFlowGraph());
    }
}
