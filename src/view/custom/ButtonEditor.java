package view.custom;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;

public class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private boolean isPushed;
    private int row;
    private java.util.function.Consumer<Integer> action;

    public ButtonEditor(JCheckBox checkBox, java.util.function.Consumer<Integer> action) {
        super(checkBox);
        this.action = action;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        button.setText((value == null) ? "" : value.toString());
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            action.accept(row);
        }
        isPushed = false;
        return button.getText();
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }
}
