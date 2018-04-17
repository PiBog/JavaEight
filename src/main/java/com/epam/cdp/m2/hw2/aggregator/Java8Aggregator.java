package com.epam.cdp.m2.hw2.aggregator;

import javafx.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Java8Aggregator implements Aggregator {

    private static final Logger logger = Logger.getLogger(Java8Aggregator.class.getName());

    /**
     * {@inheritDoc}
     */
    @Override
    public int sum(List<Integer> numbers) {
        return numbers.stream().reduce(0, Integer::sum);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, Long>> getMostFrequentWords(List<String> words, long limit) {
//        Create new Map from input List, sorted by key, where value is frequensy.
        Map<String, Long> map = words.stream().sorted().collect(
                Collectors.toMap(Function.identity(), (w1) -> 1L,
                        (m1, m2) -> m1 + 1, LinkedHashMap::new));

        return map.entrySet().stream()
                .sorted((a1, a2) -> a2.getValue().compareTo(a1.getValue()))
                .limit(limit).map(e -> new Pair(e.getKey(), e.getValue()))
                .collect(ArrayList::new, (e1, e2) -> e1.add(e2), ArrayList::addAll);  //ArrayList::add ???
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDuplicates(List<String> words, long limit) {

        final Set<String> all = new HashSet<>();

        return words.stream().filter(x -> x.length() > 1).map(String::toUpperCase)
                .filter(x -> !all.add(x))
                .collect(Collectors.toSet())
                .stream().sorted().sorted(Comparator.comparingInt(String::length))
                .limit(limit).collect(Collectors.toList());

    }




}