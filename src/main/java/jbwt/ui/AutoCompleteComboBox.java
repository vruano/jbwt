package jbwt.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Code copied from StackOverflow (<a ref="http://stackoverflow.com/questions/19924852/autocomplete-combobox-in-javafx">here</a>).
 */
public class AutoCompleteComboBox<T> extends ComboBox<T> implements EventHandler<KeyEvent> {

    private ObservableList<T> allItems;
    private boolean moveCaretToPos = false;
    private int caretPos;

    public void setAllItems(final ObservableList<T> items) {
        allItems = items;
        updateItemsBasedOnEditor();
    }

    public AutoCompleteComboBox() {
        allItems = super.getItems();

        setEditable(true);
        setOnKeyPressed(t -> hide());
        setOnKeyReleased(AutoCompleteComboBox.this);
    }

    @Override
    public void handle(final KeyEvent event) {

        if(event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(getEditor().getText().length());
            return;
        } else if(event.getCode() == KeyCode.DOWN) {
            if(!isShowing()) {
                show();
            }
            caretPos = -1;
            moveCaret(getEditor().getText().length());
            return;
        } else if(event.getCode() == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = getEditor().getCaretPosition();
        } else if(event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = getEditor().getCaretPosition();
        }

        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.isControlDown() || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
            return;
        }

        final ObservableList<T> list = updateItemsBasedOnEditor();
        final String t = getEditor().getText();

        setItems(list);
        getEditor().setText(t);
        if(!moveCaretToPos) {
            caretPos = -1;
        }
        moveCaret(t.length());
        if(!list.isEmpty()) {
            show();
        }
    }

    private ObservableList<T> updateItemsBasedOnEditor() {
        final ObservableList<T> list = FXCollections.observableArrayList();
        for (final T aData : allItems) {
            if (aData.toString().toLowerCase().startsWith(
                    getEditor().getText().toLowerCase())) {
                list.add(aData);
            }
        }
        return list;
    }

    private void moveCaret(int textLength) {
        if(caretPos == -1) {
            getEditor().positionCaret(textLength);
        } else {
            getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }

}