package org.isatools.isacreator.orcid.gui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.orcid.model.OrcidAuthor;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 23/05/2013
 * Time: 15:32
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class OrcidSearchResultsPanel extends JPanel {

    private JEditorPane resultInfo;

    private JScrollPane resultScroller;

    @InjectedResource
    private ImageIcon connectionError;

    public OrcidSearchResultsPanel() {
        ResourceInjector.get("orcidlookup-package.style").inject(this);
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        resultInfo = new JEditorPane();
        resultInfo.setContentType("text/html");
        resultInfo.setEditable(false);
        resultInfo.setBackground(UIHelper.BG_COLOR);
        resultInfo.setAutoscrolls(true);
        resultInfo.setEditorKit(new HTMLEditorKit());

        resultScroller = new JScrollPane(resultInfo, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultScroller.setBorder(new EmptyBorder(2, 2, 2, 2));
        resultScroller.setPreferredSize(new Dimension(480, 270));

        IAppWidgetFactory.makeIAppScrollPane(resultScroller);
    }

    public void showOrcidContact(OrcidAuthor currentOrcidContact) {
        reformResultData(currentOrcidContact, resultInfo);
        removeAll();
        add(resultScroller);
        revalidate();
        repaint();
    }

    private void reformResultData(OrcidAuthor contact, JEditorPane htmlPane) {

        String header = "<html>" + "<head>" +
                "<style type=\"text/css\">" + "<!--" +
                ".titleFont {" +
                "   font-family: Verdana;" + "   font-size: 9px;" +
                "   color: #006838;" + "}" +
                ".authorFont {" +
                "   font-family: Verdana;" + "   font-size: 8px;" +
                "   color: #009444;" + "}" +
                ".abstractFont {" +
                "   font-family: Verdana;" + "   font-size: 8px;" +
                "   color: #39B54A;" + "}" +
                ".otherInfoSectionFont {" +
                "   font-family: Verdana;" + "   font-size: 8px;" +
                "   color: #8DC63F;" + "}" +
                ".otherInfoValueFont {" +
                "   font-family: Verdana;" + "   font-size: 8px;" +
                "   color: #006838;" + "}" + "-->" +
                "</style>" + "</head>" +
                "<body class=\"bodyFont\">";

        StringBuffer result = new StringBuffer();
        result.append(header);
        if (contact != null) {
            result.append("<div align=\"left\">");
            result.append("<span class=\"titleFont\">").append(contact.getGivenNames()+" "+contact.getFamilyName()).append("</span><p/>");

            if (contact.getEmail()!=null){
                result.append("<span class=\"authorFont\">").append(contact.getEmail()).append("</span><p/>");
            }
//            if (!p.getAbstractText().trim().equals("")) {
//                result.append("<span class=\"abstractFont\">").append(p.getAbstractText().trim()).append("</span><p/>");
//            }
//            result.append("<span class=\"otherInfoSectionFont\">PUBMED ID:</span><span class=\"otherInfoValueFont\">").append(p.getPubmedId()).append("</span><p/>");
//result.append("<span class=\"otherInfoSectionFont\">DOI:</span><span class=\"otherInfoValueFont\">").append(p.getPublicationDOI().toUpperCase()).append("</span><p/>");
            result.append("<p/>");
            result.append("</div>");
        }

        result.append("</body></html>");

        htmlPane.setText(result.toString());
        htmlPane.setCaretPosition(0);
        htmlPane.revalidate();

    }

    public void showError() {
        removeAll();
        add(new JLabel(connectionError));
        revalidate();
        repaint();
    }
}
