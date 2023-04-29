package fi.metropolia.expensetracker.module;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class StylingManager {


    public void styleListView(ListView listView) {
        listView.setCellFactory(list -> new ListCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item.toString());
                }if (getIndex() >= 0 && getIndex() <= getListView().getItems().size()-1) {
                    setStyle(null);
                }
                else  {
                    setStyle("-fx-background-color:#FFFFFF;");
                }
            }
        });
    }
}
//Vielä varmuuden vuoksi säilössä
/*
                if (getIndex() == 0) {
                    setStyle("-fx-background-radius: 10 10 0 0;");
                } else if (getIndex() == listView.getItems().size() - 1) {
                    setStyle("-fx-background-radius: 0 0 10 10;");
                }*/