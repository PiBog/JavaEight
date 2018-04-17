package com.epam.cdp.m2.hw2.aggregator;

import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.*;

public class Java7Aggregator implements Aggregator {
    private final static Logger logger = Logger.getLogger(Java7Aggregator.class.getName());

    /**
     * {@inheritDoc}
     */
    @Override
    public int sum(List<Integer> numbers) {
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, Long>> getMostFrequentWords(List<String> words, long limit) {

        long counter = 0;
        logger.debug("Get new TreeMap");
        Map<String, Long> freqMap = new TreeMap<>();
        logger.debug("Get sorted by Key treemap with frequency");
        for (String word : words) {
            freqMap.put(word, freqMap.containsKey(word) ? freqMap.get(word) + 1 : 1);
        }
        logger.debug("Convert Map to List of Map");
        List<Map.Entry<String, Long>> entryList = new LinkedList<>(freqMap.entrySet());
        logger.debug("Sort list with Collections.sort(), provide a custom Comparator");
        Collections.sort(entryList, new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o2,
                               Map.Entry<String, Long> o1) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        logger.debug("Create sortedList");
        List<Pair<String, Long>> sortedList = new ArrayList<>();
        logger.debug("Loop the sorted list and put it into a new list with Pairs");
        for (Map.Entry<String, Long> entry : entryList) {
            if (counter >= limit) break;
            counter++;
            sortedList.add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        return sortedList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDuplicates(List<String> words, long limit) {

        long counter = 0;
        Map<String, Integer> lenghtMap = new TreeMap<>();
        logger.debug("Get sorted by Key treemap with frequency");
        for (String word : words) {
            if (!lenghtMap.containsKey(word.toUpperCase()) && !isReject(word))
                lenghtMap.put(word.toUpperCase(), word.length());
        }
        logger.debug("Convert Map to List of Map");
        List<Map.Entry<String, Integer>> entryList = new LinkedList<>(lenghtMap.entrySet());
        logger.debug("Sort list with Collections.sort(), provide a custom Comparator");
        Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        logger.debug("Create sortedList");
        List<String> sortedList = new ArrayList<>();

        logger.debug("Loop the sorted list and put it into a new list with Pairs");
        for (Map.Entry<String, Integer> entry : entryList) {
            if (counter >= limit) break;
            counter++;
            sortedList.add(entry.getKey());
        }

        return sortedList;
    }

    /**
     * Check the word for compliance condition
     *
     * @param word - checked word
     * @return <b>true</b> if word fail pass test
     */
    private boolean isReject(String word) {
        boolean test = false;
        if (word.length() < 2) test = true;
        return test;
    }
}

