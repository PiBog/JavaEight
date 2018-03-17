package com.epam.cdp.m2.hw2.aggregator;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MainAggregator {

    static long numOfOper = 10_000_000_000L;
    static int numbOfThreads = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
//        listSort();
//        treeMapSort();
//        forkJ();
        List<Integer> list = new ArrayList<>();
        list.add(2);
//        list.add(3);
//        list.add(4);
//        list.add(5);
//        list.add(6);
        List<Integer> l1 = list.subList(0,list.size()/2);
        List<Integer> l2 = list.subList(list.size()/2, list.size());
        System.out.println("List:");
        for (int c : list){
            System.out.print(c + ", ");
        }
        System.out.println();
        System.out.println("List 1:");
        for (int a : l1){
            System.out.print(a + ", ");
        }
        System.out.println();
        System.out.println("List 2:");
        for (int b : l2){
            System.out.print(b + ", ");
        }


    }

    private static void treeMapSort() {
        Map<String, Integer> map = new HashMap<>();
        map.put("мама", 3);
        map.put("тата", 5);
        map.put("тьотя", 5);
        map.put("Буба", 1);
        map.put("ковб", 7);
        map.put("ков", 7);
        map.put("коба", 5);

        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o2,
                               Map.Entry<String, Integer> o1) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        for (Map.Entry<String, Integer> entry : list) {
            System.out.println(entry.getKey() + "-" + entry.getValue());
        }
    }

    private static void listSort() {
        List<String> list = new ArrayList<>();
        list.add("б");
        list.add("Д");
        list.add("к");
        list.add("Б");
        list.add("кОм");
        list.add("ков");
        list.add("коба");

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o2, String o1) {
                return o1.toUpperCase().compareTo(o2.toUpperCase());
            }
        });

        for (String str : list) {
            System.out.println(str);
        }
    }

    private static void forkJ() {

        System.out.println(new Date());


        ForkJoinPool pool = new ForkJoinPool(numbOfThreads);
        System.out.println(pool.invoke(new MyFork(0, numOfOper)));
        System.out.println(new Date());
    }

    static class MyFork extends RecursiveTask<Long> {

        long from, to;

        public MyFork(long from, long to) {
            this.from = from;
            this.to = to;
        }

        @Override
        protected Long compute() {
            if ((to - from) <= numOfOper / numbOfThreads) {
                long j = 0;
                for (long i = from; i <= to; i++) {
                    j += i;
                }
                return j;

            } else {
                long mid = (to + from) / 2;
                MyFork partOne = new MyFork(from, mid);
                partOne.fork();
                MyFork partTwo = new MyFork(mid + 1, to);
                long secVal = partTwo.compute();
                return partOne.join() + secVal;

            }
        }
    }


}
