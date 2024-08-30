package model;

import javafx.scene.control.CheckBox;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
public class Tasks {
    private String id;
    private  String title;
    private String description;
    private LocalDate date;

    public Tasks(String id, String title, String description, LocalDate date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

}
