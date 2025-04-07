package util;

import control.Control;

import java.io.*;
import java.util.LinkedList;

public class Dot {
    private record Element(String x,
                           String y,
                           String z) {

        @Override
        public String toString() {
            return "\t\""+x +"\"" + "->" + "\""+ y +"\"" + z + "\n";
        }
    }
    // end of Element

    // fields
    private final String name;
    FileWriter fileWriter;
    BufferedWriter bufferedWriter;

    public Dot(String name) {
        this.name = name;
        String fileName = this.name + ".dot";
        try {
            fileWriter = new FileWriter(fileName);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("""
                    digraph g{
                    \tsize = "10, 10";
                    \tnode [color=lightblue2, style=filled, shape=box, fontname=Arial];\n""");

        } catch (Exception o) {
            throw new Error();
        }
    }

    private void write(String s){
        try{
            bufferedWriter.write(s);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insert(Layout.T from, Layout.T to) {
        this.insert(from, to, "");
    }

    private void insert(Layout.T from, Layout.T to, String info) {
        try {
            String s = "[label=\"" + info + "\"]";
            this.write("\t\"");
            Layout.print(from, this::write, Layout.Style.Dot);
            this.write("\" -> \"");
            Layout.print(to, this::write, Layout.Style.Dot);
            this.write("\"");
            write(s);
            write("\n");
        }catch (Exception o){
            throw new Error();
        }
    }

    public void visualize() {
        try {
            bufferedWriter.write("\n}\n\n");
            bufferedWriter.close();
            fileWriter.close();
        }catch (Exception e){
            throw new Error();
        }

        String format = Control.Dot.format;
        String[] args = {"dot", "-T", format, "-O", this.name + ".dot"};
        try {
            final class StreamDrainer implements Runnable {
                private final InputStream ins;

                public StreamDrainer(InputStream ins) {
                    this.ins = ins;
                }

                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
            }
            Process process = Runtime.getRuntime().exec(args);
            new Thread(new StreamDrainer(process.getInputStream())).start();
            new Thread(new StreamDrainer(process.getErrorStream())).start();
            process.getOutputStream().close();
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                throw new Error(this.name);
            }
            if (!Control.Dot.keep) {
                if (!new File(name + ".dot").delete())
                    throw new Error("Cannot delete dot");
            }
        } catch (Exception o) {
            throw new Error(o);
        }
    }

    private static class Test{
        public static void main(String[] args) {
            Dot dot = new Dot("test");
            dot.insert(Layout.str("a@:"), Layout.str("b->a_;;"));
            dot.visualize();
        }
    }
}