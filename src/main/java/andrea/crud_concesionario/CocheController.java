/**
 * Controlador para gestionar la interfaz gráfica de la aplicación CRUD de concesionarios.
 * Permite visualizar, agregar, actualizar, eliminar y buscar coches mediante peticiones HTTP a un servidor REST.
 */
package andrea.crud_concesionario;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.*;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class CocheController implements Initializable {

    @FXML
    private TableView<Coche> tablaCoches;
    @FXML
    private TableColumn<Coche, Long> colId;
    @FXML
    private TableColumn<Coche, String> colMatricula;
    @FXML
    private TableColumn<Coche, String> colMarca;
    @FXML
    private TableColumn<Coche, String> colModelo;
    @FXML
    private TableColumn<Coche, String> colFecha;
    @FXML
    private TextField txtMatricula;
    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtid;
    @FXML
    private TextField txtModelo;
    @FXML
    private TextField txtFecha;



    private List<Coche> listaCoches;
    //para el cliente
    private OkHttpClient cliente;

    /**
     * Inicializa el controlador, configurando las columnas de la tabla y cargando los datos iniciales
     * @param location URL de localización
     * @param resources Recursos para internacionalización
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cliente = new OkHttpClient();

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colFecha.setCellValueFactory(cell -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return new SimpleStringProperty(dateFormat.format(cell.getValue().getFechaMatriculacion()));
        });



        tablaCoches.getSelectionModel().selectedItemProperty().addListener((ac, viejo, nuevo) -> {
            if (nuevo != null) {
                txtid.setText(nuevo.getId().toString());
                txtModelo.setText(nuevo.getModelo());
                txtMarca.setText(nuevo.getMarca());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                txtFecha.setText(dateFormat.format(nuevo.getFechaMatriculacion()));
                //txtMatricula.setText(nuevo.getMatricula());
                txtMatricula.setText(nuevo.getMatricula() != null ? nuevo.getMatricula() : "");

            }
        });
        cargarCoches();
        txtid.setDisable(true);
    }

    /**
     * Carga la lista de coches desde la API y los muestra en la tabla.
     */
    @FXML
    private void cargarCoches() {
        Request request = new Request.Builder()
                .url("http://localhost:9001/api/coches")
                .get()
                .build();

        Call call = cliente.newCall(request);

        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                TypeToken<List<Coche>> typeToken = new TypeToken<>() {};
                listaCoches = gson.fromJson(json, typeToken.getType());
                tablaCoches.getItems().setAll(listaCoches);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda un nuevo coche en la base de datos a través de la API
     * @throws ParseException si hay un error en el formato de la fecha
     */
    @FXML
    private void save() throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Coche coche= Coche.builder()
                    .marca(txtMarca.getText())
                    .matricula(txtMatricula.getText())
                    .modelo(txtModelo.getText())
                    .fechaMatriculacion(format .parse(txtFecha.getText()))
                    .build();

            Gson gson= new GsonBuilder().create();

            String j= gson.toJson(coche);
            Request request = new Request.Builder()
                    .url("http://localhost:9001/api/coche")
                    //.post(okhttp3.RequestBody.create(j, okhttp3.MediaType.parse("application/json")))
                    .post(RequestBody.create(MediaType.get("application/json"), j))
                    .build();

            Call call = cliente.newCall(request);

            try (Response response = call.execute()) {
                if (response.isSuccessful()) {
                    cargarCoches();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        txtMarca.clear();
        txtFecha.clear();
        txtModelo.clear();
        txtMatricula.clear();

    }

    /**
     * Elimina un coche de la base de datos mediante su ID
     */
    @FXML
    private void delete() {
        long id = Long.parseLong(txtid.getText());

        Request request = new Request.Builder()
                .url("http://localhost:9001/api/coche/" + id)
                .delete()
                .build();

        Call call = cliente.newCall(request);
        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                cargarCoches();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        txtid.clear();
        txtMarca.clear();
        txtFecha.clear();
        txtModelo.clear();
        txtMatricula.clear();
    }

    /**
     * Actualiza la información de un coche en la base de datos
     * @throws ParseException si hay un error en el formato de la fecha
     */
    @FXML
    private void update() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Coche coche = Coche.builder()
                .id(Long.parseLong(txtid.getText()))
                .marca(txtMarca.getText())
                .matricula(txtMatricula.getText())
                .modelo(txtModelo.getText())
                .fechaMatriculacion(format.parse(txtFecha.getText()))
                .build();

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(coche);

        Request request = new Request.Builder()
                .url("http://localhost:9001/api/coche/" + coche.getId())
                //.put(RequestBody.create(json, MediaType.parse("application/json")))
                .put(RequestBody.create(MediaType.get("application/json"), json))
                .build();

        Call call = cliente.newCall(request);
        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                cargarCoches();
                tablaCoches.refresh();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtid.clear();
        txtMarca.clear();
        txtFecha.clear();
        txtModelo.clear();
        txtMatricula.clear();
    }

    /**
     * Busca un coche en la base de datos por su matrícula y lo muestra en la tabla
     * Si se encuentra un coche con la matrícula ingresada, la tabla se actualizará para mostrar solo ese coche
     * Si la matrícula no es encontrada o hay un error en la petición, no se actualizarán los datos
     */

    @FXML
    private void findByMatricula(){
        String matricula =txtMatricula.getText();

        Request request = new Request.Builder()
                .url("http://localhost:9001/api/coches/coche/" + matricula)
                .get()
                .build();

        Call call = cliente.newCall(request);
        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                TypeToken<Coche> typeToken = new TypeToken<>() {};
                Coche coche = gson.fromJson(json, typeToken.getType());
                tablaCoches.getItems().clear();
                tablaCoches.getItems().add(coche);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        txtid.clear();
        txtMarca.clear();
        txtFecha.clear();
        txtModelo.clear();
        txtMatricula.clear();
    }
}


