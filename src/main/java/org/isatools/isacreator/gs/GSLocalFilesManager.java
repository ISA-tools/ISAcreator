package org.isatools.isacreator.gs;

import org.isatools.isacreator.api.Authentication;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.launch.ISAcreatorCLArgs;
import org.isatools.isacreator.utils.GeneralUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 12/10/2012
 * Time: 16:41
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSLocalFilesManager {


    public static void downloadFiles(Authentication gsAuthentication) {

            if (ISAcreatorCLArgs.isatabDir()!=null || ISAcreatorCLArgs.isatabFiles()!=null){
                //isatabDir not null or isatabFiles not null
                String localTmpDirectory = GeneralUtils.createISATmpDirectory();

                GSDataManager gsDataManager = ((GSIdentityManager)gsAuthentication).getGsDataManager();

                if (ISAcreatorCLArgs.isatabDir()!=null){

                    if (ISAcreatorCLArgs.isatabFiles()!=null){
                        System.err.println("Either a directory containing the ISA-Tab dataset or the set of ISA-Tab files should be passed as parameters, but not both.");
                        System.exit(-1);
                    }

                    gsDataManager.downloadAllFilesFromDirectory(ISAcreatorCLArgs.isatabDir(), localTmpDirectory);

                }//isatabDir not null

                if (ISAcreatorCLArgs.isatabFiles()!=null){

                    for(String filePath: ISAcreatorCLArgs.isatabFiles()){
                        gsDataManager.downloadFile(filePath, localTmpDirectory);
                    }//for
                }//if

                ISAcreatorCLArgs.isatabDir(localTmpDirectory);
            }//

    }


    /*
    public static void main(String[] args) {
        System.out.println(GSLocalFilesManager.transformURLtoFilePath("https://dm.genomespace.org/datamanager/v1.0/file/Home/Public/agbeltran/ISAtab-Datasets/BII-S-3"));
        System.out.println(GSLocalFilesManager.transformURLtoFilePath("https://dm.genomespace.org/datamanager/v1.0/file/Home/agbeltran/ISAtab-Datasets/BII-S-3"));
        System.out.println(GSLocalFilesManager.transformURLtoFilePath(null));
    }
    */
}
