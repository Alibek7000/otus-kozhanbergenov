package kz.service;

import kz.dao.ItemsDao;
import kz.entity.Item;

import java.math.BigDecimal;
import java.util.List;

public class ItemsServiceImpl implements ItemsService {

    private final ItemsDao itemsDao = new ItemsDao();

    @Override
    public List<Item> findAll() {
        return itemsDao.findAll();
    }

    @Override
    public void delete(Item item) {
        itemsDao.delete(item);
    }

    @Override
    public void saveHundredItems() {
        for (long i = 0; i < 100; i++) {
            Item item = new Item();
            item.setName("Hundred Item " + i);
            item.setPrice(new BigDecimal(i * 100));
            itemsDao.save(item);
        }
    }

    @Override
    public void doublePrices() {
        List<Item> items = itemsDao.findAll();
        for (Item item : items) {
            item.setPrice(item.getPrice().multiply(new BigDecimal(2)));
            itemsDao.update(item);
        }
    }
}
