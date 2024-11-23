package kz;

import kz.entity.Item;
import kz.service.ItemsService;
import kz.service.ItemsServiceImpl;
import kz.service.ItemsServiceProxy;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ItemsService itemsService = ItemsServiceProxy.create(new ItemsServiceImpl());

        itemsService.saveHundredItems();
        System.out.println("All Items:");
        List<Item> items = itemsService.findAll();
        items.forEach(item ->
                System.out.println("id " + item.getId() + ": " + item.getName() + " - $" + item.getPrice()));

        itemsService.doublePrices();
        System.out.println("-----------------------------------------");
        System.out.println("All Items after double-prices:");
        items = itemsService.findAll();
        items.forEach(item ->
                System.out.println("id " + item.getId() + ": " + item.getName() + " - $" + item.getPrice()));

    }
}
