package kz.ex05_repositories;

import kz.entity.Account;
import kz.entity.User;
import kz.ex05_repositories.for_remove.UsersDao;
import kz.ex05_repositories.for_remove.UsersDaoImpl;

public class Application {
    // Домашнее задание:
    // - Реализовать класс DbMigrator - он должен при старте создавать все необходимые таблицы из файла init.sql
    // Доработать AbstractRepository
    // - Доделать findById(id), findAll(), update(), deleteById(id), deleteAll()
    // - Сделать возможность указывать имя столбца таблицы для конкретного поля (например, поле accountType маппить на столбец с именем account_type)
    // - Добавить проверки, если по какой-то причине невозможно проинициализировать репозиторий, необходимо бросать исключение, чтобы
    // программа завершила свою работу (в исключении надо объяснить что сломалось)
    // - Работу с полями объектов выполнять только через геттеры/сеттеры

    public static void main(String[] args) {
        DataSource dataSource = null;
        try {
            dataSource = new DataSource("jdbc:h2:file:./db;MODE=PostgreSQL");
            dataSource.connect();
            DbMigrator migrator = new DbMigrator(dataSource);
            migrator.migrate();

            UsersDao usersDao = new UsersDaoImpl(dataSource);
            System.out.println(usersDao.findAll());
            AbstractRepository<User> repository = new AbstractRepository<>(dataSource, User.class);
            User user = new User("bob", "123", "bob");
            repository.create(user);
            System.out.println(usersDao.findAll());

            AbstractRepository<Account> accountAbstractRepository = new AbstractRepository<>(dataSource, Account.class);
            Account account = new Account(100L, "credit", "blocked");
            accountAbstractRepository.create(account);
            System.out.println("findAll " +accountAbstractRepository.findAll());
            System.out.println("findById " + accountAbstractRepository.findById(1L));

            accountAbstractRepository.deleteById(1L);
            System.out.println("findAll " + accountAbstractRepository.findAll());

            account = new Account(200L, "credit2", "active");
            accountAbstractRepository.create(account);
            account = new Account(300L, "credit3", "active");
            accountAbstractRepository.create(account);
            System.out.println("findAll " + accountAbstractRepository.findAll());
            account = accountAbstractRepository.findById(2L);
            account.setAmount(499L);
            accountAbstractRepository.update(account);
            System.out.println("findAll " + accountAbstractRepository.findAll());

            accountAbstractRepository.deleteAll();
            System.out.println("findAll " + accountAbstractRepository.findAll());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataSource.disconnect();
        }
    }
}
