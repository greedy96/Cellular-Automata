package BoardController;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class SelectedItem {

    private final StringProperty name = new SimpleStringProperty();
    private int id;
    private GridRectangle.CellType type;
    private final BooleanProperty on = new SimpleBooleanProperty();
    private final Color color;

    public SelectedItem(GridRectangle.CellType type, int id, Color color) {
        this.id = id;
        this.type = type;
        if (type == GridRectangle.CellType.GRAIN) {
            setName("Grain", id);
        } else {
            setName("Inclusion", id);
        }
        setOn(true);
        this.color = color;
    }

    public final StringProperty nameProperty() {
        return this.name;
    }

    public final String getName() {
        return this.nameProperty().get();
    }


    public final void setName(final String name, int id) {
        this.nameProperty().set(name + " id: " + id);
    }

    public final BooleanProperty onProperty() {
        return this.on;
    }

    public final boolean isOn() {
        return this.onProperty().get();
    }

    public final void setOn(final boolean on) {
        this.onProperty().set(on);
    }

    @Override
    public String toString() {
        return getName();
    }

    public final void toggle() {
        this.onProperty().set(!this.onProperty().getValue());
    }

    public Color getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    public GridRectangle.CellType getType() {
        return type;
    }
}
