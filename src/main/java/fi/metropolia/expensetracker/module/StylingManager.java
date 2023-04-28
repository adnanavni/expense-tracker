package fi.metropolia.expensetracker.module;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.VirtualContainerBase;
import javafx.scene.control.skin.VirtualFlow;

public class StylingManager {


    public void styleListView(ListView listView) {
        listView.setCellFactory(list -> new ListCell<Salary>() {
            @Override
            protected void updateItem(Salary item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                    if (getIndex() == 0) {
                        setStyle("-fx-background-radius: 10 10 0 0;");
                    } else if (getIndex() == listView.getItems().size() - 1) {
                        setStyle("-fx-background-radius: 0 0 10 10;");
                    } else {
                        setStyle("-fx-background-color:  #E0E0E0;");
                    }
                } else {
                    setText(item.toString());
                    if (getIndex() == 0) {
                        setStyle("-fx-background-radius: 10 10 0 0;");
                    } else if (getIndex() == listView.getItems().size() - 1) {
                        setStyle("-fx-background-radius: 0 0 10 10;");
                    } else {
                        setStyle("-fx-background-color:  #E0E0E0;");
                    }
                }

            }
        });
    }
}
