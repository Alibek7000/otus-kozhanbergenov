package kz.entity;

import kz.iterator.ColorFirstIterator;
import kz.iterator.SmallFirstIterator;
import lombok.Data;

import java.util.Iterator;
import java.util.List;

@Data
public class Box {
    private final Matryoshka red;
    private final Matryoshka green;
    private final Matryoshka blue;
    private final Matryoshka magenta;


    public Iterator<String> getSmallFirstIterator() {
        return new SmallFirstIterator(List.of(red, green, blue, magenta));
    }

    public Iterator<String> getColorFirstIterator() {
        return new ColorFirstIterator(List.of(red, green, blue, magenta));
    }

}
