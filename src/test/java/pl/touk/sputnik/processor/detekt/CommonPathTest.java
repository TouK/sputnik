package pl.touk.sputnik.processor.detekt;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonPathTest {

    @Test
    void testFindWithCommonPath() {
        List<String> files = Arrays.asList(
                "path/to/file1.txt",
                "path/to/file2.txt",
                "path/to/file3.txt"
        );
        CommonPath commonPath = new CommonPath(files);
        String result = commonPath.find();
        assertEquals("path/to", result);
    }

    @Test
    void testFindWithNoCommonPath() {
        List<String> files = Arrays.asList(
                "path/to/file1.txt",
                "another/path/to/file2.txt",
                "yet/another/path/to/file3.txt"
        );
        CommonPath commonPath = new CommonPath(files);
        String result = commonPath.find();
        assertEquals("", result);
    }

    @Test
    void testFindWithEmptyFilesList() {
        List<String> files = Arrays.asList();
        CommonPath commonPath = new CommonPath(files);
        String result = commonPath.find();
        assertEquals("", result);
    }
}


