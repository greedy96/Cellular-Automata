package BoardController.board.neighbour;

import BoardController.board.cells.Grain;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {

    private Grain grainResult;
    private ResultEnum result;

    public Response() {
        this.result = ResultEnum.FINISHED;
    }

    public enum ResultEnum {
        CONTINUE, FINISHED
    }

    public boolean isContinue() {
        return this.result == ResultEnum.CONTINUE;
    }

    public void setGrainResult(Grain grain) {
        if (grain != null) {
            this.result = ResultEnum.CONTINUE;
            this.grainResult = grain;
        }
    }
}
