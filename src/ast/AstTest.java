package ast;

import org.junit.jupiter.api.Test;
import util.Layout;

public class AstTest {

    @Test
    public void test() {
        // to test the pretty printer
        Layout.T layout = Ast.Program.layout(SamplePrograms.progSumRec);
        Layout.print(layout, System.out::print, Layout.Style.C);
        Layout.print(Ast.Program.layout(SamplePrograms.progFac), System.out::print, Layout.Style.C);
    }
}
