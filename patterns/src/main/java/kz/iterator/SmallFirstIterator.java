package kz.iterator;

import kz.entity.Matryoshka;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class SmallFirstIterator implements Iterator<String> {

    private int currentIndex;
    private int currentMatryoshkaIndex;
    private final int itemsCount;
    private final List<Matryoshka> matryoshkas;

    public SmallFirstIterator(List<Matryoshka> matryoshkas) {
        this.matryoshkas = matryoshkas;
        itemsCount = matryoshkas.getFirst().getItems().size();
    }

    @Override
    public boolean hasNext() {
        if (currentMatryoshkaIndex > matryoshkas.size() - 1) {
            currentMatryoshkaIndex = 0;
            currentIndex++;
        }
        return currentMatryoshkaIndex < matryoshkas.size() && currentIndex < itemsCount;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Matryoshka matryoshka = matryoshkas.get(currentMatryoshkaIndex++);
        return matryoshka.getItems().get(currentIndex);
    }
}
