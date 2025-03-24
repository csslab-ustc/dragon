package cfg.lab1;

import cfg.Cfg;
import util.Id;
import util.Label;

import java.util.List;

public class CFGInstrCounter {
    private int doitBlock(Cfg.Block.T bb) {
        // TODO: please add your code:
        throw new util.Todo();

    }

    private int doitFunction(Cfg.Function.T func) {
        // In this function, you should traverse all basic blocks in the function and count
        // their instructions, then print the total number of function's instructions and
        // return it to caller.
        // TODO: please add your code:
        throw new util.Todo();

    }

    public void doitProgram(Cfg.Program.T ast) {
        switch (ast) {
            case Cfg.Program.Singleton(List<Cfg.Function.T> functions) -> {
                int sum = functions.stream().map(this::doitFunction).reduce(0, Integer::sum);
                System.out.printf("Total instructions in program: %d\n", sum);
            }
        }
    }
}
