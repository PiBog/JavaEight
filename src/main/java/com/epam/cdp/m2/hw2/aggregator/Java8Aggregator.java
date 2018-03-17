package com.epam.cdp.m2.hw2.aggregator;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Java8Aggregator implements Aggregator {

    /**
     * {@inheritDoc}
     */
    @Override
    public int sum(List<Integer> numbers) {
        int sum = numbers.stream().reduce(0, Integer::sum);
        return sum;
    }

    //-----------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, Long>> getMostFrequentWords(List<String> words, long limit) {
//        throw new UnsupportedOperationException();
//        Create new Map from input List, sorted by key, where value is frequensy.
        Map<String, Long> map = words.stream().sorted().collect(
                Collectors.toMap(Function.identity(), (w1) -> 1L,
                        (m1, m2) -> m1 + 1, LinkedHashMap::new));

        return map.entrySet().stream()
                .sorted((a1, a2) -> a2.getValue().compareTo(a1.getValue()))
                .limit(limit).map(e -> new Pair(e.getKey(), e.getValue()))
                .collect(ArrayList::new, (e1, e2) -> e1.add(e2), ArrayList::addAll);  //ArrayList::add ???
    }
//-----------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDuplicates(List<String> words, long limit) {
        return words.stream().filter(x -> x.length() > 1).map(String::toUpperCase)
                .filter(x -> {
                    char s1 = x.charAt(0);
                    for (int i = 1; i < x.length(); i++) {
                        if (s1 != x.charAt(i))
                            return true;
                    }
                    return false;
                })
                .collect(Collectors.toSet())
                .stream().sorted().sorted((o1, o2) -> Integer.compare(o1.length(), o2.length()))
                .limit(limit).collect(Collectors.toList());
    }


}