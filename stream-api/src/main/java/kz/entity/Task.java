package kz.entity;

import kz.enumeration.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Task {
    private Integer id;
    private String name;
    private TaskStatus status;
}
