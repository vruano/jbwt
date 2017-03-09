package jbwt.ui;

import jbwt.base.Text;
import jbwt.common.dna.Nucleotide;
import jbwt.intervals.UserCoordinate;
import jbwt.sequences.FastaFileReference;
import jbwt.sequences.Reference;
import com.sun.javafx.collections.ImmutableObservableList;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.FileNotFoundException;

public class Controller {

    @FXML
    protected GridPane trackPane;

    @FXML
    protected AutoCompleteComboBox<UserCoordinate> coordinatesBox;

    protected Reference reference = Reference.EMPTY;

    @FXML
    public void initialize() {
        coordinatesBox.setConverter(new StringConverter<UserCoordinate>() {
            @Override
            public String toString(UserCoordinate object) {
                return object ==  null ? "" : object.toString();
            }

            @Override
            public UserCoordinate fromString(String string) {
                return new UserCoordinate(string);
            }
        });
        coordinatesBox.valueProperty().addListener(this::handleCoordinateChange);
    }

    private void handleCoordinateChange(final ObservableValue<? extends UserCoordinate> observable,
                                        final UserCoordinate oldValue, final UserCoordinate newValue) {
        if (reference != null) {
            final Text<Nucleotide> bases = reference.subContig(newValue.contig, newValue.start - 1, newValue.stop - newValue.start + 1);
            final String string = new String();

            trackPane.getChildren().stream()
                    .forEach(c -> {
                        if (c instanceof TextFlow) {
                            ((TextFlow) c).getChildren().clear();
                            ((TextFlow) c).getChildren().add(new javafx.scene.text.Text(Nucleotide.ALPHABET.toString(bases)));
                        }
                    });
        }

    }

    public void handleExit() {
        Platform.exit();
    }

    public void openFile() {

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        final File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            openFile(file);
        }
    }

    private void openFile(final File file) {
        if (file.getName().matches(".*\\.fasta")) {
            final IndexedFastaSequenceFile fasta;
            try {
                fasta = new IndexedFastaSequenceFile(file);
                final Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(String.format("Opened Fasta formatted reference with %d contigs", fasta.getSequenceDictionary().size()));
                alert.showAndWait();
                trackPane.setMaxWidth(Double.POSITIVE_INFINITY);
                final TextFlow trackName = new TextFlow(new javafx.scene.text.Text(file.getName()));
                final FastaFileReference newReference = new FastaFileReference(fasta);
                if (reference.contigs().isEmpty()) {
                    reference = newReference;
                    coordinatesBox.setAllItems(new ImmutableObservableList<>(reference.contigs().keySet().stream().map(UserCoordinate::new).toArray(UserCoordinate[]::new)));
                }
                final TextFlow trackContent = new TextFlow(new javafx.scene.text.Text(Nucleotide.ALPHABET.toString(new FastaFileReference(fasta).contigs().get("1").subtext(1000000, 1000000 + 100))));
                trackContent.setMaxWidth(Double.POSITIVE_INFINITY);
                trackPane.addRow(trackPane.impl_getRowCount(), trackName, trackContent);
            } catch (final FileNotFoundException e) {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } catch (final Exception ex) {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(String.format("Exception raised: %s with message %s", ex.getClass().getSimpleName(), ex.getMessage()));
                alert.showAndWait();
            }


        } else {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(String.format("Unknown file type for '%s'. Perhaps it has the wrong extension name", file));
            alert.showAndWait();
        }
    }
}
