package kz;

import kz.entity.Box;
import kz.entity.Matryoshka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Box box = prepareBoxWithMatryoshkas();
        box.getSmallFirstIterator().forEachRemaining(System.out::println);
        box.getColorFirstIterator().forEachRemaining(System.out::println);
    }

    private static Box prepareBoxWithMatryoshkas() {
        List<Matryoshka> matryoshkas = new ArrayList<>();
        List<String> names = Arrays.asList("red", "green", "blue", "magenta");
        for (String name : names) {
            List<String> elements = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                elements.add(name + i);
            }
            matryoshkas.add(new Matryoshka(elements));
        }
        return new Box(matryoshkas.get(0), matryoshkas.get(1), matryoshkas.get(2), matryoshkas.get(3));
    }
}