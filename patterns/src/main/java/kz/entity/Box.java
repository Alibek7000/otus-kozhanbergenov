package kz.entity;

import kz.iterator.ColorFirstIterator;
import kz.iterator.SmallFirstIterator;
import lombok.Data;

import java.util.Iterator;
import java.util.List;

@Data
public class Box {
    private final Matryoshka red;    // "red0", "red1", ..., "red9"
    private final Matryoshka green;
    private final Matryoshka blue;
    private final Matryoshka magenta;


    // expected: "red0", "green0", "blue0", "magenta0", "red1", "green1", "blue1", "magenta1",...
    public Iterator<String> getSmallFirstIterator() {
        return new SmallFirstIterator(List.of(red, green, blue, magenta));
    }

//    // expected: "red0", "red1", ..., "red9", "green0", "green1", ..., "green9", ...
    public Iterator<String> getColorFirstIterator() {
        return new ColorFirstIterator(List.of(red, green, blue, magenta));
    }

}
