package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Utils {
    
    /**
     * Reads multiline String from buffer reader
     *
     * @param buffIn - Buffer that is connected to a sockets inputstream
     */
    public static void readParagraph(BufferedReader buffIn) throws IOException {
        String line;
        while(true) {
            //If there is a line, read it
            if((line = buffIn.readLine()) == null || line.equals("final_string")){
                break;
            }
            System.out.println(line);
        }
    }

    /**
     * Writes multiline String to sockets outputstream
     *
     * @param pw - PrintWriter connected to sockets output stream
     * @param message - Message wished to be sent to sockets output stream
     */
    public static void writeParagraph(PrintWriter pw, String message) throws IOException {
        pw.write(message + "\n" + "final_string" + "\n");
        pw.flush();
    }



}
