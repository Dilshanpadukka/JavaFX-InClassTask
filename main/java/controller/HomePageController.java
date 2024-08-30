package controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Tasks;

import java.io.InvalidClassException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
    public JFXTextField txtId;
    public JFXTextField txtTitle;
    public DatePicker date;
    public JFXTextArea txtDescription;
    public TableView<Tasks> tblTaskView;
    public TableColumn<Tasks, String> colTodo;
    public TableView<Tasks> tblDoneTasks;
    public TableColumn<Tasks, String> colDoneTask;
    public TableColumn colCheckBox;
    public JFXTextField txtIdStatus;
    public JFXTextField txtTitleStatus;
    public TableView tblDoneTask;
    public TableColumn colDoneID;
    public TableColumn colDoneTitle;
    public TableColumn colDoneDes;
    public TableColumn colDoneDate;

    private ObservableList<Tasks> taskObservableList = FXCollections.observableArrayList();

    ObservableList<Tasks> observableTasks;

    public void onClickAddtask(ActionEvent event) {
        String id = txtId.getText();
        String title = txtTitle.getText();
        String description = txtDescription.getText();
        LocalDate taskDate = date.getValue();
        //CheckBox checkBox = new CheckBox();

        Tasks tasks = new Tasks(id, title, description, taskDate);
        List<Tasks> tasksList = DBConnection.getInstance().getConnection();

        if (tasksList != null) {
            tasksList.add(tasks);
            updateTable();
            System.out.println(tasks);
        } else {
            System.err.println("Failed to retrieve tasks list from DBConnection.");
        }
    }

    private void updateTable() {
        List<Tasks> taskList = DBConnection.getInstance().getConnection();
        taskObservableList.setAll(taskList);
        tblTaskView.setItems(taskObservableList);
    }

    private void setTextToValues(Tasks newValue) {
        txtIdStatus.setText(newValue.getId());
        txtTitleStatus.setText(newValue.getTitle());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reload();
        colDoneID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDoneTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDoneDes.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDoneDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        colTodo.setCellValueFactory(new PropertyValueFactory<>("title"));
        updateTable();
        tblTaskView.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            setTextToValues(newValue);
        }));
    }

    public void onClickAddDone(ActionEvent event) {
        String searchId = txtIdStatus.getText();
        List<Tasks> taskList = DBConnection.getInstance().getConnection();
        for (Tasks tasks : taskList) {
            if (tasks.getId().equals(searchId)) {
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaFXTask", "root", "12345");
                    String SQL = "INSERT INTO DoneTasks VALUES(?,?,?,?)";
                    PreparedStatement psTm = connection.prepareStatement(SQL);
                    psTm.setObject(1, tasks.getId());
                    psTm.setObject(2, tasks.getTitle());
                    psTm.setObject(3, tasks.getDescription());
                    psTm.setObject(4, tasks.getDate());
                    int execute = psTm.executeUpdate();
                    if (execute > 0) {
                        txtIdStatus.setText(null);
                        txtTitleStatus.setText(null);
                        Tasks taskToDelete = taskList.stream().filter(task -> task.getId().equals(searchId)).findFirst().orElse(null);
                        taskList.remove(taskToDelete);
                        updateTable();

                    }

                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, "Error : " + e.getMessage()).show();
                }
                break;
            }
        }
        System.out.println("Success");
    }

    public void onClickReload(ActionEvent event) {
reload();


    }
    public void reload(){
        ObservableList<Tasks> doneTaskList = FXCollections.observableArrayList();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/JavaFXTask", "root", "12345");
            String SQL = "SELECT * FROM DoneTasks";
            PreparedStatement psTm = connection.prepareStatement(SQL);
            ResultSet resultSet = psTm.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("task_id");
                String title = resultSet.getString("task_title");
                String description = resultSet.getString("task_description");
                LocalDate date = resultSet.getDate("completion_date").toLocalDate();

                Tasks task = new Tasks(id, title, description, date);
                doneTaskList.add(task);
            }

            tblDoneTask.setItems(doneTaskList);

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error : " + e.getMessage()).show();
        }
    }
}