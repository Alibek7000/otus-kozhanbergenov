package kz.iterator;

import kz.entity.Matryoshka;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ColorFirstIterator implements Iterator<String> {

    private int currentIndex;
    private int currentMatryoshkaIndex;
    private int itemsCount;
    private List<Matryoshka> matryoshkas;

    public ColorFirstIterator(List<Matryoshka> matryoshkas) {
        this.matryoshkas = matryoshkas;
        itemsCount = matryoshkas.getFirst().getItems().size();
    }

    @Override
    public boolean hasNext() {
        if (currentIndex == itemsCount - 1) {
            currentMatryoshkaIndex++;
            currentIndex = 0;
        }
        return currentMatryoshkaIndex < matryoshkas.size() && currentIndex < itemsCount;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Matryoshka matryoshka = matryoshkas.get(currentMatryoshkaIndex);
        return matryoshka.getItems().get(currentIndex++);
    }
}
