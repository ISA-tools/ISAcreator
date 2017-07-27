package org.isatools.isacreator.common;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 23/06/2011
 *         Time: 18:31
 */
public class ReOrderableJList extends JList
        implements DragSourceListener, DropTargetListener, DragGestureListener {

    private static Logger log = Logger.getLogger(ReOrderableJList.class.getName());

    static DataFlavor localObjectFlavor;

    static {
        try {
            localObjectFlavor =
                    new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    static DataFlavor[] supportedFlavors = {localObjectFlavor};
    DragSource dragSource;
    DropTarget dropTarget;
    public Object dropTargetCell;
    int draggedIndex = -1;

    public ReOrderableJList(DefaultListModel model) {

        setCellRenderer(new ReOrderableListCellRenderer(this));
        setModel(model);
        dragSource = new DragSource();

        dragSource.createDefaultDragGestureRecognizer(this,
                DnDConstants.ACTION_MOVE,
                this);

        dropTarget = new DropTarget(this, this);
    }

//    public void setCustomCellRenderer(ListCellRenderer renderer) {
//        setCellRenderer(renderer);
//    }

    // DragGestureListener
    public void dragGestureRecognized(DragGestureEvent dge) {
        try {

            // find object at this x,y
            Point clickPoint = dge.getDragOrigin();
            int index = locationToIndex(clickPoint);
            if (index == -1)
                return;

            Transferable trans;

            if (getSelectedValuesList().size() > 1) {
                List<Object> target = new ArrayList<Object>();

                target.addAll(getSelectedValuesList());
                trans = new RJLTransferable(target);


            } else {
                Object target = getModel().getElementAt(index);
                trans = new RJLTransferable(target);
            }


            draggedIndex = index;
            dragSource.startDrag(dge, Cursor.getDefaultCursor(),
                    trans, this);
        } catch (InvalidDnDOperationException e) {
            log.info(e.getMessage());
        }
    }

    // DragSourceListener events
    public void dragDropEnd(DragSourceDropEvent dsde) {

        dropTargetCell = null;
        draggedIndex = -1;
        repaint();
    }

    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragExit(DragSourceEvent dse) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    // DropTargetListener events
    public void dragEnter(DropTargetDragEvent dtde) {
        if (dtde.getSource() != dropTarget)
            dtde.rejectDrag();
        else {
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        }

    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void dragOver(DropTargetDragEvent dtde) {
        // figure out which cell it's over, no drag to self
        if (dtde.getSource() != dropTarget)
            dtde.rejectDrag();
        Point dragPoint = dtde.getLocation();
        int index = locationToIndex(dragPoint);
        if (index == -1)
            dropTargetCell = null;
        else
            dropTargetCell = getModel().getElementAt(index);
        repaint();
    }

    public void drop(DropTargetDropEvent dtde) {

        if (dtde.getSource() != dropTarget) {
            System.out.println("rejecting for bad source (" +
                    dtde.getSource().getClass().getName() + ")");
            dtde.rejectDrop();
            return;
        }
        Point dropPoint = dtde.getLocation();
        int index = locationToIndex(dropPoint);

        boolean dropped = false;
        try {
            if ((index == -1) || (index == draggedIndex)) {
                //dropped onto self
                dtde.rejectDrop();
                return;
            }
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);

            Object dragged =
                    dtde.getTransferable().getTransferData(localObjectFlavor);


            // move items - note that indicies for insert will
            // change if [removed] source was before target
            boolean sourceBeforeTarget = (draggedIndex < index);

            DefaultListModel mod = (DefaultListModel) getModel();

            if (dragged instanceof List) {
                // todo check if dragged is of Instance ArrayList/List. If so, we need to iterate through them, adding the items sequentially to their new position

                List<Object> toInsert = (ArrayList<Object>) dragged;

                int startIndex = draggedIndex;
                int dropIndex = index;

                if (sourceBeforeTarget) {
                    // we need to perform some specialised behaviour

                    // need to remove the elements from the list from the last element selected in the drag to the first.
                    // First to last would cause a conflict in indexes.
                    for (int toRemove = startIndex + toInsert.size() - 1; toRemove >= startIndex; toRemove--) {
                        mod.remove(toRemove);
                    }

                    // we have just removed items from the list, so we need to compensate for this removal by
                    // modifying the dropIndex through subtraction of the items to be entered in their new positions.
                    dropIndex = dropIndex - toInsert.size();

                    // now add all the elements to be added in their rightful place.
                    for (Object draggedObject : toInsert) {
                        mod.add(dropIndex, draggedObject);
                        dropIndex++;
                    }
                } else {

                    for (Object draggedObject : toInsert) {
                        mod.remove(startIndex);
                        mod.add((sourceBeforeTarget ? dropIndex - 1 : dropIndex), draggedObject);
                        startIndex++;
                        dropIndex++;
                    }
                }

            } else {
                mod.remove(draggedIndex);
                mod.add((sourceBeforeTarget ? index - 1 : index), dragged);
            }
            dropped = true;
            firePropertyChange("orderChanged", "", "changedOrder");
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        dtde.dropComplete(dropped);
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    class RJLTransferable implements Transferable {
        Object object;

        public RJLTransferable(Object o) {
            object = o;
        }

        public Object getTransferData(DataFlavor df)
                throws UnsupportedFlavorException, IOException {
            if (isDataFlavorSupported(df))
                return object;
            else
                throw new UnsupportedFlavorException(df);
        }

        public boolean isDataFlavorSupported(DataFlavor df) {
            return (df.equals(localObjectFlavor));
        }

        public DataFlavor[] getTransferDataFlavors() {
            return supportedFlavors;
        }
    }

}