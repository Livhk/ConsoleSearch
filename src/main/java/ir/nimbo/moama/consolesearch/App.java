package ir.nimbo.moama.consolesearch;

import asg.cliche.ShellFactory;
import ir.nimbo.moama.consolesearch.console.Console;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        try {
            ShellFactory.createConsoleShell("Console Search", "", new Console()).commandLoop();
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Console failed to open");
        }
    }
}
