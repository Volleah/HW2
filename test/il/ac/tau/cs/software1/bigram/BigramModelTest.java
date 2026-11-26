package il.ac.tau.cs.software1.bigram;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

/*
 * Sanity checks for Part A (Questions 1 - 5).
 * These are the examples from the assignment document, and nothing more:
 * passing them does not guarantee a full grade. DO NOT CHANGE THIS FILE.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class BigramModelTest {

    private static final String ALL_YOU_NEED = "resources/bigram/all_you_need.txt";

    private static final String[] VOCABULARY = {"love", "all", "you", "need", "is"};
    private static final int[][] COUNTS = {
            {3, 1, 0, 0, 1},
            {0, 0, 3, 0, 0},
            {0, 0, 0, 3, 0},
            {0, 0, 0, 0, 2},
            {2, 1, 0, 0, 0},
    };
    private static final List<String> VOC_FILE_LINES =
            List.of("5 words", "0,love", "1,all", "2,you", "3,need", "4,is");
    private static final List<String> COUNTS_FILE_LINES =
            List.of("0,0:3", "0,1:1", "0,4:1", "1,2:3", "2,3:3", "3,4:2", "4,0:2", "4,1:1");

    // The directory is created before each test, and deleted after it
    @TempDir
    Path tempDir;

    // ------------------------------------------------------------ Question 1

    @Test
    void q1_vocabularyOfAllYouNeed() throws IOException {
        BigramModel model = new BigramModel();
        assertArrayEquals(VOCABULARY, model.buildVocabularyIndex(ALL_YOU_NEED));
    }

    @Test
    void q1_numbersAndIllegalWords() throws IOException {
        Path file = tempDir.resolve("numbers.txt");
        Files.write(file, List.of("In 1984 , Java's 2 cats ate 13 fish !!"));

        BigramModel model = new BigramModel();
        assertArrayEquals(
                new String[]{"in", "some_num", "java's", "cats", "ate", "fish"},
                model.buildVocabularyIndex(file.toString()));
    }

    // ------------------------------------------------------------ Question 2

    @Test
    void q2_countsOfAllYouNeed() throws IOException {
        BigramModel model = new BigramModel();
        assertArrayEquals(COUNTS, model.buildCountsArray(ALL_YOU_NEED, VOCABULARY));
    }

    // ------------------------------------------------------------ Question 3

    @Test
    void q3_saveModelWritesBothFiles() throws IOException {
        BigramModel model = new BigramModel();
        model.mVocabulary = VOCABULARY;
        model.mBigramCounts = COUNTS;

        String modelPath = tempDir.resolve("saved_model").toString();
        model.saveModel(modelPath);

        assertEquals(VOC_FILE_LINES, Files.readAllLines(Path.of(modelPath + ".voc")));
        assertEquals(COUNTS_FILE_LINES, Files.readAllLines(Path.of(modelPath + ".counts")));
    }

    // ------------------------------------------------------------ Question 4

    @Test
    void q4_loadModelReadsBothFiles() throws IOException {
        String modelPath = tempDir.resolve("model_to_load").toString();
        Files.write(Path.of(modelPath + ".voc"), VOC_FILE_LINES);
        Files.write(Path.of(modelPath + ".counts"), COUNTS_FILE_LINES);

        BigramModel model = new BigramModel();
        model.loadModel(modelPath);

        assertArrayEquals(VOCABULARY, model.mVocabulary);
        assertArrayEquals(COUNTS, model.mBigramCounts);
    }

    // ------------------------------------------------------------ Question 5

    @Test
    void q5_isLegalSentence() {
        BigramModel model = new BigramModel();
        model.mVocabulary = VOCABULARY;
        model.mBigramCounts = COUNTS;

        assertTrue(model.isLegalSentence("love all you"));
        assertTrue(model.isLegalSentence(""));
        assertTrue(model.isLegalSentence("love"));

        assertFalse(model.isLegalSentence("love is is"));
        // assertFalse(model.isLegalSentence("love the beatles"));
        // assertFalse(model.isLegalSentence("beatles"));
    }

    // ------------------------------------------- Everything together (Q1 + Q2)

    @Test
    void q1q2_initModelThenQuery() throws IOException {
        BigramModel model = new BigramModel();
        model.initModel(ALL_YOU_NEED);

        assertEquals(2, model.getBigramCount("is", "love"));
        assertEquals(0, model.getBigramCount("strawberry", "fields"));
        assertEquals("love", model.getMostFrequentProceeding("is"));
    }
}
