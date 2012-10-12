package org.isatools.isacreator.launch;

import org.isatools.isacreator.gui.modeselection.Mode;
import sun.jvmstat.perfdata.monitor.MonitorDataException;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 01/10/2012
 * Time: 16:19
 *
 * Class to maintain ISAcreator Command Line (CL) arguments
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAcreatorCLArgs {

    private static boolean noArguments = true;

    private static Mode mode =  null;
    private static String configDir = null;
    private static String username = null;
    private static String password = null;
    private static String isatabDir = null;
    private static String[] isatabFiles = null;


    public static Mode mode(){
        return mode;
    }

    public static String configDir(){
        return configDir;
    }

    public static String username(){
        return username;
    }

    public static String password(){
        return password;
    }

    public static String isatabDir(){
        return isatabDir;
    }

    public static void isatabDir(String dir){
        isatabDir = dir;
    }

    public static String[] isatabFiles(){
        return isatabFiles;
    }

    public static boolean noArguments(){
        return noArguments;
    }

    public static void parseArgs(String[] args){
        noArguments = false;

        int i = 0;
        String arg = null, option = null;
        while (i < args.length && args[i].startsWith("--")) {
            option = args[i++];

            if (i<args.length)
                arg = args[i++];

            option.toLowerCase();

            if (option.equals("--help")){
                System.out.println("usage: ISAcreator.jar [--mode] [--configDir <path>] [--username <username>] [--password <password>] [--isatabDir <path>]\n" +
                        "\t[--isatabFiles <files>] [--help]\n");
                System.exit(0);

            }else if (option.equals("--mode")){
                // mode = arg.equals("NORMAL_MODE")? Mode.NORMAL_MODE: Mode.LIGHT_MODE;
                if (arg.equals("NORMAL_MODE"))
                    ISAcreatorCLArgs.mode = Mode.NORMAL_MODE;
                else if (arg.equals("LIGHT_MODE"))
                    ISAcreatorCLArgs.mode = Mode.LIGHT_MODE;
                else if (arg.equals("GS"))
                    ISAcreatorCLArgs.mode = Mode.GS;

            }else if (option.equals("--configDir"))
                ISAcreatorCLArgs.configDir = arg;
            else if (option.equals("--username"))
                ISAcreatorCLArgs.username = arg;
            else if (option.equals("--password"))
                ISAcreatorCLArgs.password = arg;
            else if (option.equals("--isatabDir"))
                ISAcreatorCLArgs.isatabDir = arg;
            else if (option.equals("--isatabFiles")) {
                ISAcreatorCLArgs.isatabFiles = parseFilenames(arg);
            }

        } //while
    }

    private static String[] parseFilenames(String arg){
        return arg.split(",");
    }


}
