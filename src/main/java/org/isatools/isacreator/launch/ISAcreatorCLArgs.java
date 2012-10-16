package org.isatools.isacreator.launch;

import org.isatools.isacreator.gui.modeselection.Mode;

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
    private static char[] password = null;
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

    public static char[] password(){
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
        String arg = null, option;
        while (i < args.length && args[i].startsWith("--")) {
            option = args[i++];

            if (i<args.length)
                arg = args[i++];

            option.toLowerCase();

            if (option.equals("--help")){
                System.out.println("usage: ISAcreator.jar [--mode] [--configDir <path>] [--username <username>] [--password <password>] [--isatabDir <path>]\n" +
                        "\t[--isatabFiles <files>] [--help]\n");
                System.out.println("\t--mode\tIndicates ISAcreator mode, the options are NORMAL, LIGHT or GS");
                System.out.println("\t--configDir\tIt sets the path of the directory containing the configuration files");
                System.out.println("\t--username\tIt sets the username for ISAcreator");
                System.out.println("\t--password\tThe password for the username set by --username can be passed to ISAcreator");
                System.out.println("\t--isatabDir\tIt sets the directory containing the ISAtab files");
                System.out.println("\t--isatabFiles <files>\t<files> must be a comma separated list of ISAtab files; this option is only valid for mode GS ");
                System.exit(0);

            }else if (option.equals("--mode")){

                if (arg.equals("NORMAL"))
                    ISAcreatorCLArgs.mode = Mode.NORMAL_MODE;
                else if (arg.equals("LIGHT"))
                    ISAcreatorCLArgs.mode = Mode.LIGHT_MODE;
                else if (arg.equals("GS"))
                    ISAcreatorCLArgs.mode = Mode.GS;
                else{
                    System.out.println("Invalid mode argument, the possible modes are: NORMAL, LIGHT or GS");
                    System.exit(-1);
                }


            }else if (option.equals("--configDir"))
                ISAcreatorCLArgs.configDir = arg;
            else if (option.equals("--username"))
                ISAcreatorCLArgs.username = arg;
            else if (option.equals("--password"))
                ISAcreatorCLArgs.password = arg.toCharArray();
            else if (option.equals("--isatabDir"))
                ISAcreatorCLArgs.isatabDir = arg;
            else if (option.equals("--isatabFiles")) {
                ISAcreatorCLArgs.isatabFiles = parseFilenames(arg);
            }

        } //while

        validate();
    }

    private static String[] parseFilenames(String arg){
        return arg.split(",");
    }

    private static void validate(){
        if (isatabFiles()!=null && mode()!=Mode.GS){
            System.out.println("The isatabFiles parameter is only valid for Genome Space mode.");
            System.exit(-1);
        }
    }


}
