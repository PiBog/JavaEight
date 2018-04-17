package com.epam.cdp.m2.hw2.aggregator;

import com.sun.istack.internal.NotNull;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Java7ParallelAggregator implements Aggregator {

    public static final Logger logger = Logger.getLogger(Java7ParallelAggregator.class.getName());


    static int numOfThreads = Runtime.getRuntime().availableProcessors();

    /**
     * {@inheritDoc}
     */
    @Override
    public int sum(List<Integer> numbers) {
        int treshold = numbers.size() / numOfThreads;
        logger.debug("Hi Jack");

        ForkJoinPool pool = new ForkJoinPool(numOfThreads);
        logger.info("" + numOfThreads + "->" + numbers.size() + "->" + treshold );
        return pool.invoke(new Myforks(numbers, treshold));

    }

    class Myforks extends RecursiveTask<Integer> {
        int length;
        List<Integer> list;
        int treshold;

        public Myforks(List<Integer> list, int treshold) {
            this.list = list;
            this.treshold = treshold;
            this.length = list.size();
        }

        @Override
        protected Integer compute() {
            if (list.isEmpty()) return 0;
            else if (length <= ((length < numOfThreads) ? numOfThreads : treshold)) {
                logger.info("Current threshold ->" + treshold);
                int sum = 0;
                for (int element : list) {
                    sum += element;
                    logger.info(element + "-" + length);
                }
                logger.info("End of cicle");
                return sum;
            } else {
                int mid = list.size() / 2;
                List<Integer> firstList = list.subList(0, mid);
                logger.info("" + firstList.size() + "/" + treshold);

                Myforks f1 = new Myforks(firstList, treshold);
                f1.fork();

                List<Integer> secList = list.subList(mid, length);

                logger.info("" + firstList.size() + "/" + treshold);

                Myforks f2 = new Myforks(secList, treshold);
                int compVal = f2.compute();
                return f1.join() + compVal;

            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, Long>> getMostFrequentWords(List<String> words, long limit) {

        long counter = 0;

        logger.debug("Divide, sort, and merge to entryList");
        ForkJoinPool pool = new ForkJoinPool(numOfThreads);
        Map<String, Long> entryMap = pool.invoke(new PairFork(words, words.size() / numOfThreads));

        List<Map.Entry<String, Long>> entryList = new LinkedList<>(entryMap.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o2,
                               Map.Entry<String, Long> o1) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        logger.debug("  Loop the sorted list and put it into a new list with Pairs");
        List<Pair<String, Long>> sortedList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : entryList) {
            if (counter >= limit) break;
            counter++;
            sortedList.add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        return sortedList;
    }


    class PairFork extends RecursiveTask<Map<String, Long>> {

//        logger.debug(" initialize variebles");
        private List<String> forkList, sortedList;
        private int threshold;
        private int size;


        public PairFork(List<String> forkList, int threshold) {
            this.forkList = forkList;
            this.threshold = threshold;
            this.size = forkList.size();
        }

        @Override
        protected Map<String, Long> compute() {
            if (size <= ((size < numOfThreads) ? numOfThreads : threshold)) {
                Map<String, Long> freqMap = new TreeMap<>();
                logger.debug(" Get sorted by Key treemap with frequency");
                for (String word : forkList) {
                    freqMap.put(word, freqMap.containsKey(word) ? freqMap.get(word) + 1 : 1);
                }
                return freqMap;
            } else {
                int mid = size / 2;
                PairFork pf1 = new PairFork(forkList.subList(0, mid), threshold);
                pf1.fork();
                PairFork pf2 = new PairFork(forkList.subList(mid, size), threshold);
                return mergeTree(pf1.join(), pf2.compute());
            }
        }

        private Map<String, Long> mergeTree(Map<String, Long> mapOne, Map<String, Long> mapTwo) {
            Map<String, Long> mergeMap = (isShorter(mapOne, mapTwo)) ? mapTwo : mapOne;
            Map<String, Long> shorter = (isShorter(mapOne, mapTwo)) ? mapOne : mapTwo;
            for (Map.Entry<String, Long> entry : shorter.entrySet()) {
                String key = entry.getKey();
                long val = entry.getValue();
                mergeMap.put(key, (mergeMap.containsKey(key) ? mergeMap.get(key) + val : val));
            }
            return mergeMap;
        }

        private boolean isShorter(@NotNull Map<String, Long> mA, @NotNull Map<String, Long> mB) {
            return mA.size() < mB.size();
        }


    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDuplicates(List<String> words, long limit) {
        long counter = 0;

        logger.debug("  Divide, sort, and merge to entryList");
        ForkJoinPool poolp = new ForkJoinPool(numOfThreads);
        Map<String, Long> entryMap = poolp.invoke(new DupFork(words, words.size() / numOfThreads));

        List<Map.Entry<String, Long>> entryList = new LinkedList<>(entryMap.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        logger.debug("  Loop the sorted list and put it into a new list with Pairs");
        List<String> sortedList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : entryList) {
            if (counter >= limit) break;
            counter++;
            sortedList.add(entry.getKey());
        }

        return sortedList;


    }

    class DupFork extends RecursiveTask<Map<String, Long>> {

//        logger.debug("initialize variebles");
        private List<String> forkList, sortedList;
        private int threshold;
        private int size;



        public DupFork(List<String> forkList, int threshold) {
            this.forkList = forkList;
            this.threshold = threshold;
            this.size = forkList.size();
        }

        @Override
        protected Map<String, Long> compute() {
            final Map<String, Long> resultMap;
            if (size <= ((size < numOfThreads) ? numOfThreads : threshold)) {
                Map<String, Long> freqMap = new TreeMap<>();
                for (String word : forkList) {
                    if (isGood(word.toUpperCase()) && !freqMap.containsKey(word.toUpperCase())) {
                        freqMap.put(word.toUpperCase(), (long) word.length());
                    }
                }
                resultMap = freqMap;
            } else {
                int mid = size / 2;
                DupFork pf1 = new DupFork(forkList.subList(0, mid), threshold);
                pf1.fork();
                DupFork pf2 = new DupFork(forkList.subList(mid, size), threshold);
                resultMap = mergeTree(pf1.join(), pf2.compute());
            }
            return resultMap;
        }

        /**
         * Adding data to a longer map from a shorter map.
         *
         * @param mapOne
         * @param mapTwo
         * @return one merged result map
         */
        private Map<String, Long> mergeTree(Map<String, Long> mapOne, Map<String, Long> mapTwo) {
            Map<String, Long> mergeMap = (isShorter(mapOne, mapTwo)) ? mapTwo : mapOne;
            Map<String, Long> shorter = (isShorter(mapOne, mapTwo)) ? mapOne : mapTwo;
            for (Map.Entry<String, Long> entry : shorter.entrySet()) {
                mergeMap.put(entry.getKey(), entry.getValue());
            }
            return mergeMap;
        }

        /**Compare the length of two maps.
         *
         * @param firstMap  - first map
         * @param secondMap - second map
         * @return <b>true</b> if first map shorter than second map
         */
        private boolean isShorter(@NotNull Map<String, Long> firstMap, @NotNull Map<String, Long> secondMap) {
            return firstMap.size() < secondMap.size();
        }

        /**Check the word for compliance condition.
         *
         * @param word - checked word
         * @return <b>true</b> if word pass checking
         */
        private boolean isGood(String word) {
            boolean test = false;
            if (word.length() > 2) {
                StringBuffer strBuf = new StringBuffer(word.toUpperCase());
                char zeroChar = strBuf.charAt(0);
                for (int i = 1; i < strBuf.length(); i++) {
                    if (zeroChar != strBuf.charAt(i)) {
                        test = true;
                        break;
                    }
                }
            }
            return test;
        }


    }

}




