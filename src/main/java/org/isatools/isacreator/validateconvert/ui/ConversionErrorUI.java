package org.isatools.isacreator.validateconvert.ui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.errorreporter.html.ErrorMessageWriter;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 09/09/2011
 *         Time: 09:56
 */
public class ConversionErrorUI extends Container {


    public ConversionErrorUI() {
        setLayout(new BorderLayout());
    }

    public void constructErrorPane(List<ErrorMessage> errorMessages) {

        JLabel info = UIHelper.createLabel("Conversion failed with "
                + errorMessages.size()
                + (errorMessages.size() > 1 ? " errors" : " error")
                + ". Here is why:", UIHelper.VER_11_BOLD, UIHelper.RED_COLOR);

        info.setHorizontalAlignment(SwingConstants.LEFT);

        add(UIHelper.padComponentVerticalBox(30, info), BorderLayout.NORTH);

        ErrorMessageWriter writer = new ErrorMessageWriter();
        String errorReport = writer.createHTMLRepresentationOfErrors(errorMessages);

        JEditorPane messagePane = new JEditorPane();
        messagePane.setContentType("text/html");
        messagePane.setBackground(org.isatools.errorreporter.ui.utils.UIHelper.BG_COLOR);
        messagePane.setSelectionColor(org.isatools.errorreporter.ui.utils.UIHelper.LIGHT_GREEN_COLOR);
        messagePane.setSelectedTextColor(org.isatools.errorreporter.ui.utils.UIHelper.BG_COLOR);
        messagePane.setEditable(false);
        messagePane.setBorder(null);

        JScrollPane scroller = new JScrollPane(messagePane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBorder(null);
        scroller.getViewport().setOpaque(false);
        scroller.setOpaque(false);

        IAppWidgetFactory.makeIAppScrollPane(scroller);

        messagePane.setText(errorReport);

        add(scroller, BorderLayout.CENTER);
    }
}
