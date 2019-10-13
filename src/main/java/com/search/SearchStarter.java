package com.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * CLI Search Application which
 * takes in a directory of files
 * and creates an in memory index.
 *
 * User can search for words and
 * the application returns files
 * which contains the words entered
 * by the user
 *
 *
 * @author Shahbaz.Alam
 */
public class SearchStarter {
    public static final Map<String, Set<File>> indexInMemory = new HashMap<>();
    private static final String regex = "[ ?,.]+";

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("no directory given to index");
        }
        File indexableDirectory = new File(args[0]);
        if (!indexableDirectory.isDirectory() || indexableDirectory.listFiles().length == 0) {
            throw new IllegalArgumentException("path is not for a directory or is an empty directory");
        }
        try {
            indexFiles(indexableDirectory);
        } catch (IOException ex) {
            System.out.println("I/O exception caught while reading the files in the directory:" + ex.getMessage());
            System.exit(0);
        }
        if (indexInMemory.isEmpty()) {
            throw new IllegalArgumentException("directory is having empty files");
        }
        System.out.println("Welcome to the Search Platform. If at anytime you want to leave, type \"quit\"");
        Scanner keyboard = new Scanner(System.in);
        while (true) {
            System.out.print("search> ");
            final String line = keyboard.nextLine();
            if(line.equalsIgnoreCase("quit")){
                System.out.println("Thanks for using the program");
                System.exit(0);
            }
            search(line);
        }
    }

    /**
     * the method is intended to take in a
     * directory and index all the files in it.
     *
     * It does so by creating a global map with words
     * in the file as key and the files it is contained
     * in as value.
     *
     * It is used as a index for search lookups.
     *
     * @param indexableDirectory
     * @throws Exception
     */
    public static void indexFiles(File indexableDirectory) throws IOException {
        for (File file : indexableDirectory.listFiles()) {
            List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            for (String line : lines) {
                String[] tokens = line.split(regex);
                for (String token : tokens) {
                    String tokenKey = token.toLowerCase();
                    if (indexInMemory.containsKey(tokenKey)) {
                        indexInMemory.get(tokenKey).add(file);
                    } else {
                        Set<File> files = new HashSet<>();
                        files.add(file);
                        indexInMemory.put(tokenKey, files);
                    }
                }
            }
        }
    }

    /**
     * the method is intended to split the
     * line into tokens and search the index
     * in memory for files containing the tokens
     * It then creates a map of the token as key and
     * set of files which contains the token.
     *
     * It then outputs the file name with the percentage
     * of matching tokens in it in the console
     *
     * @param line
     */
    public static void search(String line) {
        String[] searchTokens = line.toLowerCase().split(regex);
        Map<String, Integer> weightMap = new HashMap<>();
        for (String searchToken : searchTokens) {
            Set<File> files = indexInMemory.get(searchToken);
            if (files != null) {
                for (File file : files) {
                    if (weightMap.containsKey(file.getName())) {
                        weightMap.put(file.getName(), weightMap.get(file.getName()) + 1);
                    } else {
                        weightMap.put(file.getName(), 1);
                    }
                }
            }
        }
        if (weightMap.isEmpty()) {
            System.out.println("no matches found");
        } else {
            weightMap.keySet().stream()
                    .forEach(fileName -> System.out.println(fileName + ":" + calculatePercentage(weightMap.get(fileName), searchTokens.length) + "%"));
        }
    }

    /**
     * simple function to calculate the percentage
     *
     * @param count
     * @param totalWords
     * @return
     */
    private static double calculatePercentage(int count, int totalWords) {
        return count * 100 / totalWords;
    }
}
