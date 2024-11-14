package kz.entity;

import lombok.Data;

import java.util.List;

@Data
public class Matryoshka {
    // [0] - the smallest / [9] - the biggest
    private final List<String> items;
}
