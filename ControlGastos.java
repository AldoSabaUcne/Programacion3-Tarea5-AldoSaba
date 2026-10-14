import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControlGastos extends Application {

    private double presupuesto = 0;
    private double totalGastado = 0;

    private TextField txtPresupuesto;
    private TextField txtDescripcion;
    private TextField txtMonto;
    private ComboBox<String> cmbCategoria;
    private Label lblTotal;
    private Label lblMensaje;
    private VBox listaGastos;

    @Override
    public void start(Stage stage) {
        txtPresupuesto = new TextField();
        txtPresupuesto.setPromptText("Presupuesto mensual");
        Button btnFijar = new Button("Fijar");
        btnFijar.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold;");
        btnFijar.setOnAction(e -> fijarPresupuesto());
        HBox filaPresupuesto = new HBox(10, txtPresupuesto, btnFijar);
        HBox.setHgrow(txtPresupuesto, Priority.ALWAYS);

        cmbCategoria = new ComboBox<>();
        cmbCategoria.getItems().addAll("Comida", "Transporte", "Ocio", "Salud", "Educacion", "Otros");
        cmbCategoria.setValue("Comida");

        txtDescripcion = new TextField();
        txtDescripcion.setPromptText("Descripcion");

        txtMonto = new TextField();
        txtMonto.setPromptText("Monto");
        txtMonto.setPrefWidth(110);

        Button btnAgregar = new Button("Agregar gasto");
        btnAgregar.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAgregar.setOnAction(e -> agregarGasto());

        HBox filaGasto = new HBox(10, cmbCategoria, txtDescripcion, txtMonto, btnAgregar);
        HBox.setHgrow(txtDescripcion, Priority.ALWAYS);

        lblMensaje = new Label("");
        lblMensaje.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12px;");

        listaGastos = new VBox(6);
        listaGastos.setPadding(new Insets(5));
        ScrollPane scroll = new ScrollPane(listaGastos);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(220);

        lblTotal = new Label();
        lblTotal.setPadding(new Insets(8));

        VBox root = new VBox(12, filaPresupuesto, filaGasto, lblMensaje, scroll, lblTotal);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        actualizarTotal();

        stage.setTitle("Control de gastos del mes");
        stage.setScene(new Scene(root, 620, 460));
        stage.show();
    }

    private void fijarPresupuesto() {
        try {
            double valor = Double.parseDouble(txtPresupuesto.getText().trim());
            if (valor <= 0) {
                throw new NumberFormatException();
            }
            presupuesto = valor;
            lblMensaje.setText("");
            actualizarTotal();
        } catch (NumberFormatException ex) {
            lblMensaje.setText("Error: el presupuesto debe ser un numero positivo.");
        }
    }

    private void agregarGasto() {
        String descripcion = txtDescripcion.getText().trim();
        if (descripcion.isEmpty()) {
            lblMensaje.setText("Error: escribe una descripcion.");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText().trim());
            if (monto <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            lblMensaje.setText("Error: el monto debe ser un numero positivo.");
            return;
        }

        lblMensaje.setText("");
        String categoria = cmbCategoria.getValue();

        Label lblGasto = new Label(String.format("%s - %s - RD$ %,.2f", categoria, descripcion, monto));
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Button btnEliminar = new Button("X");
        btnEliminar.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox fila = new HBox(10, lblGasto, espacio, btnEliminar);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(6));
        fila.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 4;");

        btnEliminar.setOnAction(e -> {
            listaGastos.getChildren().remove(fila);
            totalGastado -= monto;
            actualizarTotal();
        });

        listaGastos.getChildren().add(fila);
        totalGastado += monto;

        txtDescripcion.clear();
        txtMonto.clear();
        actualizarTotal();
    }

    private void actualizarTotal() {
        if (Math.abs(totalGastado) < 0.005) {
            totalGastado = 0;
        }

        double disponible = presupuesto - totalGastado;
        String texto = String.format("Total gastado: RD$ %,.2f   |   Disponible: RD$ %,.2f",
                totalGastado, disponible);

        String color;
        if (presupuesto <= 0) {
            color = "#616161";
        } else {
            double porcentaje = totalGastado / presupuesto;
            if (porcentaje < 0.70) {
                color = "#2e7d32";
            } else if (porcentaje <= 1.0) {
                color = "#f9a825";
            } else {
                color = "#d32f2f";
                texto += "   ¡Presupuesto excedido!";
            }
        }

        lblTotal.setText(texto);
        lblTotal.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 15px; -fx-font-weight: bold;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}