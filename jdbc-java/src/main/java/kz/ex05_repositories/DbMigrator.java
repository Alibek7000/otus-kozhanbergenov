package kz.ex05_repositories;

import kz.exceptions.ApplicationInitializationException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class DbMigrator {
    private DataSource dataSource;
    URL resource = DbMigrator.class.getClassLoader().getResource("init.sql");

    public DbMigrator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void migrate() throws URISyntaxException, IOException, SQLException {
        if (resource == null) {
            throw new ApplicationInitializationException("Resource not found");
        }
        Path filePath = Paths.get(resource.toURI());

        dataSource.getStatement().executeUpdate(Files.readString(filePath));

        System.out.println("Migration executed successfully!");
    }
}
