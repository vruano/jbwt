package jbwt.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;


public class Main extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {

        Thread.setDefaultUncaughtExceptionHandler(Main::showError);

        final Parent root = FXMLLoader.load(getClass().getResource("/jbwt/ui/sample.fxml"));
        primaryStage.setTitle("CallViz");
        primaryStage.setScene(new Scene(root, 1024, 800));
        primaryStage.show();
    }

    private static void showError(final Thread thread, final Throwable throwable) {
        if (Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showErrorDialog(throwable));
        } else {
            System.err.println("An unexpected error occurred in "+ thread);
        }
    }

    /**
     *
     * Some of the code in this class was copied for an internet example found at <a ref="http://code.makery.ch/blog/javafx-dialogs-official/">code.makery.ch</a>
     * @param e the causing throwable exception
     */
    private static void showErrorDialog(final Throwable e) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        if (e.getMessage() != null) {
            alert.setContentText("Exception thrown with message: " + e.getMessage());
        } else {
            alert.setContentText("Exception thrown of type: " + e.getClass().getSimpleName());
        }
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        final String stackTraceString = stringWriter.toString();
        printWriter.close();
        final Label label = new Label("The exception stacktrace was:");

        final TextArea textArea = new TextArea(stackTraceString);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        final GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
