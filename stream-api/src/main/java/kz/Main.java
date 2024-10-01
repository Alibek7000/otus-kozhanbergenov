package kz;

import kz.entity.Task;
import kz.enumeration.TaskStatus;

import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Task> tasks = List.of(new Task(1, "task1", TaskStatus.OPEN),
                new Task(2, "task2", TaskStatus.OPEN),
                new Task(3, "task3", TaskStatus.IN_PROGRESS),
                new Task(5, "task5", TaskStatus.CLOSED),
                new Task(4, "task4", TaskStatus.IN_PROGRESS));


        List<Task> tasksByStatus = tasks.stream().filter(task -> TaskStatus.IN_PROGRESS.equals(task.getStatus())).toList();
        System.out.println("tasksByStatus = " + tasksByStatus);

        boolean taskExistsById = tasks.stream().anyMatch(t -> t.getId().equals(1));
        System.out.println("taskExistsById = " + taskExistsById);

        List<Task> sortedByStatus = tasks.stream().sorted(Comparator.comparing(Task::getStatus)).toList();
        System.out.println("sortedByStatus = " + sortedByStatus);

        long countByType = tasks.stream().filter(task -> TaskStatus.OPEN.equals(task.getStatus())).count();
        System.out.println("countByType = " + countByType);
    }
}