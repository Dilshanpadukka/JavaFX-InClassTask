package db;

import model.Tasks;

import java.util.ArrayList;
import java.util.List;

public class DBConnection {
    private static DBConnection instance;
    private List<Tasks> taskList;
    public List<Tasks> getConnection(){
        return taskList;
    }

    private DBConnection(){
        this.taskList = new ArrayList<>();
    }

    public static DBConnection getInstance(){
        if (instance==null){
            return instance = new DBConnection();
        }
        return instance;
    }
}
