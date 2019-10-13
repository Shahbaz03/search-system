package com.search;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

public class SearchStarterTest {

    private final String lineSeparator = System.getProperty("line.separator");

    @Test(expected = IllegalArgumentException.class)
    public void testWithIllegalArgument() {
        String[] args = {};
        SearchStarter.main(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithFileInArgument() {
        String[] args = {System.getProperty("user.dir") + "/directory/File1.txt"};
        SearchStarter.main(args);
    }

    @Test
    public void testIndexFiles() {
        try{
            SearchStarter.indexFiles(new File(System.getProperty("user.dir") + "/directory"));
            Assert.assertEquals(32, SearchStarter.indexInMemory.size());
            Assert.assertEquals(2, SearchStarter.indexInMemory.get("technical").size());
            Assert.assertEquals(1, SearchStarter.indexInMemory.get("real").size());
        } catch (Exception ex) {
            ex.getCause();
        }
    }

    @Test
    public void testSearch() {
        try{
            final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            SearchStarter.indexFiles(new File(System.getProperty("user.dir") + "/directory"));
            SearchStarter.search("technical skill");

            String expectedOutput = "File1.txt:50.0%" + lineSeparator + "File2.txt:50.0%" + lineSeparator;

            Assert.assertEquals(expectedOutput, outContent.toString());
        } catch (Exception ex) {
            ex.getCause();
        }
    }
}
