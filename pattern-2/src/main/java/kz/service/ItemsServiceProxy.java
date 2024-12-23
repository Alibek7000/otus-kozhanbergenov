package kz.service;


import kz.dao.DataSource;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

@Slf4j
public class ItemsServiceProxy {

    public static ItemsService create(ItemsService itemsService) {
        return (ItemsService) Proxy.newProxyInstance(
                ItemsService.class.getClassLoader(),
                new Class[]{ItemsService.class},
                new TransactionHandler(itemsService)
        );
    }

    private static class TransactionHandler implements InvocationHandler {
        private final ItemsService target;

        public TransactionHandler(ItemsService target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Connection connection = DataSource.getInstance().getConnection();
            try {
                connection.setAutoCommit(false);
                Object result = method.invoke(target, args);
                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                log.error(e.getMessage(), e);
                throw e;
            } finally {
                DataSource.getInstance().closeConnection();
            }
        }
    }
}
