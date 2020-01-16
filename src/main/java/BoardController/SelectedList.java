package BoardController;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

public class SelectedList extends CheckBoxListCell<SelectedItem> {

    @Override
    public void updateItem(SelectedItem item, boolean empty) {
        setSelectedStateCallback(new Callback<SelectedItem, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(SelectedItem param) {
                return param.onProperty();
            }
        });
        super.updateItem(item, empty);

        if (item != null) {
            Node graphic = getGraphic();
            GridPane gridPane = new GridPane();
            Rectangle rect = new Rectangle(20, 20);
            rect.setFill(item.getColor());

            gridPane.add(graphic, 0, 0, 1, 1);
            gridPane.add(rect, 1, 0, 1, 1);
            setGraphic(gridPane);
        }
    }
}
