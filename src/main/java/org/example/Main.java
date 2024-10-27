package org.example;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 */
public class Main {
    private static final int QUEUE_CAPACITY = 100;
    public static AtomicInteger countSymbolA = new AtomicInteger();
    public static AtomicInteger countSymbolB = new AtomicInteger();
    public static AtomicInteger countSymbolC = new AtomicInteger();

    private static final BlockingQueue<String> symbolACounterQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> symbolBCounterQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> symbolCCounterQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) {
        Thread textGeneratorThread = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    String text = generateText("abc", 100000);

                    symbolACounterQueue.put(text);
                    symbolBCounterQueue.put(text);
                    symbolCCounterQueue.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread letterACountingThread = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    countSymbolA.addAndGet(countLetter(symbolACounterQueue.take(), 'a'));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("Letters 'a': " + countSymbolA);
        });

        Thread letterBCountingThread = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    countSymbolB.addAndGet(countLetter(symbolBCounterQueue.take(), 'b'));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Letters 'b': " + countSymbolB);
        });

        Thread letterCCountingThread = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    countSymbolC.addAndGet(countLetter(symbolCCounterQueue.take(), 'c'));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Letters 'c': " + countSymbolC);
        });

        textGeneratorThread.start();
        letterACountingThread.start();
        letterBCountingThread.start();
        letterCCountingThread.start();

        try {
            textGeneratorThread.join();
            letterACountingThread.join();
            letterBCountingThread.join();
            letterCCountingThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countLetter(String text, char letter) {
        int count = 0;

        if (Objects.isNull(text)) {
            return count;
        }

        for (char c : text.toCharArray()) {
            if (c == letter || c == Character.toUpperCase(letter)) { // Учитываем как строчные, так и заглавные буквы
                count++;
            }
        }
        return count;
    }
}