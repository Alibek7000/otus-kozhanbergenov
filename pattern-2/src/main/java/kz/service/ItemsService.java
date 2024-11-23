package kz.service;

import kz.entity.Item;

import java.util.List;

public interface ItemsService {
    List<Item> findAll();

    void delete(Item item);

    void saveHundredItems();

    void doublePrices();
}
