package org.isatools.isacreator.validateconvert.ui.ENAReceipt;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;
import uk.ac.ebi.utils.xml.XPathReader;

import javax.xml.xpath.XPathConstants;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;


public class ENAReceiptParser {
    private static Logger log = Logger.getLogger(ENAReceiptParser.class.getName());

    public static ENAReceipt parseReceipt(String receipt) {
        try {
            InputStream stream = IOUtils.toInputStream(receipt, "UTF-8");
            XPathReader reader = new XPathReader(stream);

            Set<String> experimentAccessions = parseReceiptSection(reader, "EXPERIMENT");
            Set<String> sampleAccessions = parseReceiptSection(reader, "SAMPLE");
            Set<String> runAccessions = parseReceiptSection(reader, "RUN");
            Set<String> studyAccessions = parseReceiptSection(reader, "STUDY");
            Set<String> infoMessages = parseReceiptInfoMessages(reader);
            Set<String> errors = parseReceiptErrors(reader);
            return new ENAReceipt(experimentAccessions, sampleAccessions, runAccessions, studyAccessions, infoMessages, errors);
        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
            return null;
        }
    }

    public static Set<String> parseReceiptSection(XPathReader reader, String section) {
        NodeList experiments = (NodeList) reader.read("/RECEIPT/" + section, XPathConstants.NODESET);

        Set<String> accessions = new HashSet<String>();
        if (experiments.getLength() > 0) {
            for (int experimentIndex = 0; experimentIndex <= experiments.getLength(); experimentIndex++) {
                String accession = (String) reader.read("/RECEIPT/" + section + "[" + experimentIndex + "]/@accession", XPathConstants.STRING);
                if (!accession.isEmpty()) {
                    accessions.add(accession);
                }
            }
        }
        return accessions;
    }
    public static Set<String> parseReceiptInfoMessages(XPathReader reader) {

        Set<String> infoSet = new HashSet<String>();
        NodeList infoMessages = (NodeList) reader.read("/RECEIPT/MESSAGES/INFO", XPathConstants.NODESET);


        if (infoMessages.getLength() > 0) {
            for (int experimentIndex = 0; experimentIndex <= infoMessages.getLength(); experimentIndex++) {
                String info = (String) reader.read("/RECEIPT/MESSAGES/INFO[" + experimentIndex + "]", XPathConstants.STRING);
                if (!info.isEmpty()) {
                    infoSet.add(info);
                }
            }
        }
        return infoSet;

    }
    public static Set<String> parseReceiptErrors(XPathReader reader) {

        Set<String> errors = new HashSet<String>();
        NodeList errorMessages = (NodeList) reader.read("/RECEIPT/MESSAGES/ERROR", XPathConstants.NODESET);


        if (errorMessages.getLength() > 0) {
            for (int experimentIndex = 0; experimentIndex <= errorMessages.getLength(); experimentIndex++) {
                String error = (String) reader.read("/RECEIPT/MESSAGES/ERROR[" + experimentIndex + "]", XPathConstants.STRING);
                if (!error.isEmpty()) {
                    errors.add(error);
                }
            }
        }
        return errors;

    }
}
