package kz.ex05_repositories;


import kz.annotation.RepositoryField;
import kz.annotation.RepositoryIdField;
import kz.annotation.RepositoryTable;
import kz.exceptions.ApplicationInitializationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AbstractRepository<T> {
    private DataSource dataSource;

    private PreparedStatement psCreate;
    private PreparedStatement psUpdate;
    private PreparedStatement psGetId;
    private PreparedStatement psFindById;
    private PreparedStatement psDeleteById;
    private PreparedStatement psFindAll;
    private PreparedStatement psDeleteAll;

    private List<Field> cachedFields;
    private Field idField;
    private Class<T> clazz;
    private String tableName;

    public AbstractRepository(DataSource dataSource, Class<T> cls) {
        this.dataSource = dataSource;
        clazz = cls;
        prepareCommon();
        validateGettersAndSetters();
        prepareInsert();
        prepareUpdate();
        prepareGetId();
        prepareFindById();
        prepareDeleteById();
        prepareDeleteAll();
        prepareFindAll();
    }

    private void prepareCommon() {
        tableName = clazz.getAnnotation(RepositoryTable.class).title();
        cachedFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(RepositoryField.class))
                .filter(f -> !f.isAnnotationPresent(RepositoryIdField.class))
                .collect(Collectors.toList());
        Optional<Field> idFieldOptional = Arrays.stream(clazz.getDeclaredFields()).filter(f -> f.isAnnotationPresent(RepositoryIdField.class)).findFirst();
        if (idFieldOptional.isEmpty()) {
            throw new ApplicationInitializationException("id field not found  for class " + clazz.getName());
        }
        idField = idFieldOptional.get();
    }

    private void validateGettersAndSetters() {
        for (Field field : cachedFields) {
            try {
                getGetter(field);
                getSetter(field);
            } catch (NoSuchMethodException e) {
                throw new ApplicationInitializationException(
                        "Missing getter or setter for field: " + field.getName() + " in class " + clazz.getName());
            }
        }
    }

    public void create(T entity) {
        try {
            Long id = getId();
            psCreate.setObject(1, id);
            for (int i = 0; i < cachedFields.size(); i++) {
                Field field = cachedFields.get(i);
                Method getter = getGetter(field);
                Object value = getter.invoke(entity);
                psCreate.setObject(i + 2, value);
            }
            psCreate.executeUpdate();
        } catch (Exception e) {
            throw new ApplicationInitializationException("Exception on create object to class " + clazz.getName());
        }
    }

    public void update(T entity) {
        try {
            Object id = getGetter(idField).invoke(entity);
            if (id == null) {
                throw new ApplicationInitializationException("Can't update for non persited entity!");
            }
            for (int i = 0; i < cachedFields.size(); i++) {
                Field field = cachedFields.get(i);
                Method getter = getGetter(field);
                Object value = getter.invoke(entity);
                psUpdate.setObject(i + 1, value);
            }
            psUpdate.setObject(cachedFields.size() + 1, id);
            psUpdate.executeUpdate();
        } catch (Exception e) {
            throw new ApplicationInitializationException("Exception on update object to class " + clazz.getName());
        }
    }

    public T findById(Long id) {
        T result = null;
        try {
            psFindById.setObject(1, id);
            ResultSet rs = psFindById.executeQuery();
            while (rs.next()) {
                result = fillEntity(rs);
            }
        } catch (Exception e) {
            throw new ApplicationInitializationException("Exception on findById " + id + " for class " + clazz.getName());
        }
        return result;
    }

    public void deleteById(Long id) {
        try {
            psDeleteById.setObject(1, id);
            psDeleteById.executeUpdate();
        } catch (Exception e) {
            throw new ApplicationInitializationException("Exception on deleteById " + id + " for class " + clazz.getName());
        }
    }

    public void deleteAll() {
        try {
            psDeleteAll.executeUpdate();
        } catch (Exception e) {
            throw new ApplicationInitializationException("Exception on deleteAll for class " + clazz.getName());
        }
    }

    public List<T> findAll() {
        List<T> out = new ArrayList<>();
        try {
            ResultSet rs = psFindAll.executeQuery();
            while (rs.next()) {
                out.add(fillEntity(rs));
            }
        } catch (Exception e) {
            throw new ApplicationInitializationException("Exception on findAll for class " + clazz.getName());
        }
        return out;
    }

    private T fillEntity(ResultSet rs) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
        T instance = clazz.getDeclaredConstructor().newInstance();
        String idFieldName = getTableFieldName(idField);
        Object idValue = rs.getObject(idFieldName);
        Method idSetter = getSetter(idField);
        idSetter.invoke(instance, idValue);
        for (Field field : cachedFields) {
            String columnName = getTableFieldName(field);
            Object value = rs.getObject(columnName);
            Method setter = getSetter(field);
            setter.invoke(instance, value);
        }
        return instance;
    }

    private Method getGetter(Field field) throws NoSuchMethodException {
        String getterName = "get" + capitalize(field.getName());
        return clazz.getMethod(getterName);
    }

    private Method getSetter(Field field) throws NoSuchMethodException {
        String setterName = "set" + capitalize(field.getName());
        return clazz.getMethod(setterName, field.getType());
    }

    private void prepareInsert() {
        StringBuilder query = new StringBuilder("insert into ");
        String tableName = clazz.getAnnotation(RepositoryTable.class).title();
        query.append(tableName).append(" (");
        // 'insert into users ('
        query.append(getTableFieldName(idField)).append(", ");
        for (Field f : cachedFields) {
            String fieldName = getTableFieldName(f);
            query.append(fieldName).append(", ");
        }
        // 'insert into users (id, login, password, nickname, '
        query.setLength(query.length() - 2);
        // 'insert into users (id, login, password, nickname'
        query.append(") values (");
        query.append("?, ");
        for (Field f : cachedFields) {
            query.append("?, ");
        }
        // 'insert into users (id, login, password, nickname) values (?, ?, ?, ?, '
        query.setLength(query.length() - 2);
        // 'insert into users (id, login, password, nickname) values (?, ?, ?, ?'
        query.append(");");
        try {
            psCreate = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ApplicationInitializationException("Exception on prepareInsert for class " + clazz.getName());
        }
    }

    private void prepareUpdate() {
        StringBuilder query = new StringBuilder("update ");
        String tableName = clazz.getAnnotation(RepositoryTable.class).title();
        query.append(tableName).append(" set ");
        for (Field f : cachedFields) {
            String fieldName = getTableFieldName(f);
            query.append(fieldName).append(" = ?, ");
        }
        query.setLength(query.length() - 2);
        query.append(" where id = ?");
        try {
            psUpdate = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ApplicationInitializationException("Exception on prepareUpdate for class " + clazz.getName());
        }
    }


    private static String getTableFieldName(Field f) {
        String fieldName = f.getName();
        if (f.isAnnotationPresent(RepositoryField.class)) {
            RepositoryField fAnnotation = f.getAnnotation(RepositoryField.class);
            String name = fAnnotation.name();
            fieldName = name.isBlank() ? fieldName : name;
        }
        return fieldName;
    }


    private void prepareGetId() {
        StringBuilder query = new StringBuilder("select max( ");
        query.append(idField.getName());
        query.append(") from ");
        query.append(clazz.getAnnotation(RepositoryTable.class).title());
        try {
            psGetId = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ApplicationInitializationException("Exception on prepareGetId for class " + clazz.getName());
        }
    }

    private void prepareFindById() {
        StringBuilder query = new StringBuilder("select * from ");
        String tableName = clazz.getAnnotation(RepositoryTable.class).title();
        query.append(tableName);
        query.append(" where id = ?");
        query.append(";");
        try {
            psFindById = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ApplicationInitializationException("Exception on prepareFindById for class " + clazz.getName());
        }
    }

    private void prepareDeleteAll() {
        StringBuilder query = new StringBuilder("delete from ");
        String tableName = clazz.getAnnotation(RepositoryTable.class).title();
        query.append(tableName);
        query.append(" where 1 = 1");
        query.append(";");
        try {
            psDeleteAll = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ApplicationInitializationException("Exception on prepareDeleteAll for class " + clazz.getName());
        }
    }

    private void prepareDeleteById() {
        StringBuilder query = new StringBuilder("delete from ");
        String tableName = clazz.getAnnotation(RepositoryTable.class).title();
        query.append(tableName);
        query.append(" where id = ?");
        query.append(";");
        try {
            psDeleteById = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ApplicationInitializationException("Exception on prepareDeleteById for class " + clazz.getName());
        }
    }

    private void prepareFindAll() {
        StringBuilder query = new StringBuilder("select * from ");
        String tableName = clazz.getAnnotation(RepositoryTable.class).title();
        query.append(tableName);
        query.append(";");
        try {
            psFindAll = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ApplicationInitializationException("Exception on prepareFindAll for class " + clazz.getName());
        }
    }

    private Long getId() {
        Long result = 0L;
        try {
            ResultSet rs = psGetId.executeQuery();
            while (rs.next()) {
                result = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result + 1;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


}
