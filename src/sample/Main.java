package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 920, 690));
        primaryStage.show();
    }


    public static void main(String[] args) {
//        Board board = new Board(4,4);
//        board.setRandomGrains(2);
//        Grain[][] matrix;
//        while(board.generateNextStep()){
//            matrix = board.getMatrix();
//            System.out.println(board.getStep());
//
//            for(int i = 0; i<matrix.length; i++){
//                System.out.println(Arrays.toString(matrix[i]));
//            }
//            System.out.println("\n");
//        }
//        System.out.println(Arrays.stream( NeighbourhoodEnum.values()).map(NeighbourhoodEnum::getName).collect(Collectors.toList()));
        launch(args);
    }
}
