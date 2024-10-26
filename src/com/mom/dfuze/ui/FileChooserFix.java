package com.mom.dfuze.ui;

import java.awt.Component;
import java.awt.Container;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;

public class FileChooserFix extends JFileChooser {

    private static final long serialVersionUID = 1L;
    private JTable            detailsTable;

    public FileChooserFix(String currentDirectoryPath) {
        super(currentDirectoryPath);
        Action details = getActionMap().get("viewTypeDetails");
        details.actionPerformed(null);
        detailsTable = findChildComponent(this, JTable.class);
    }

    private <T> T findChildComponent(Container container, Class<T> cls) {
        int n = container.getComponentCount();
        for (int i = 0; i < n; i++) {
            Component comp = container.getComponent(i);
            if (cls.isInstance(comp)) {
                return cls.cast(comp);
            }
            else if (comp instanceof Container) {
                T c = findChildComponent((Container) comp, cls);
                if (c != null) {
                    return c;
                }
            }
        }
        return null;
    }

    @Override
    public void setCurrentDirectory(File dir) {
        super.setCurrentDirectory(dir);
        fixNameColumnWidth();
    }

    private void fixNameColumnWidth() {
        if (detailsTable != null) {
            JViewport viewport = (JViewport) detailsTable.getParent();
            int viewWidth = viewport.getSize().width;
            TableColumn nameCol = detailsTable.getColumnModel().getColumn(0);
            int tableWidth = detailsTable.getPreferredSize().width;
            if (tableWidth < viewWidth) {
                nameCol.setPreferredWidth(nameCol.getPreferredWidth() + 
                                    viewWidth - tableWidth);
            }
        }
    }
}