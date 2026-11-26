package il.ac.tau.cs.software1.bigram;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class BigramModel {
    public static final int MAX_VOCABULARY_SIZE = 14500;
    public static final String VOC_FILE_SUFFIX = ".voc";
    public static final String COUNTS_FILE_SUFFIX = ".counts";
    public static final String SOME_NUM = "some_num";
    public static final int ELEMENT_NOT_FOUND = -1;

    String[] mVocabulary;
    int[][] mBigramCounts;

    // DO NOT CHANGE THIS !!!
    public void initModel(String fileName) throws IOException {
        mVocabulary = buildVocabularyIndex(fileName);
        mBigramCounts = buildCountsArray(fileName, mVocabulary);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods - already implemented for you. Use them!
    ///////////////////////////////////////////////////////////////////////////

    /*
     * Converts a single token (a whitespace-free piece of text from the file)
     * into the form in which it is stored in the vocabulary:
     *   - a token with at least one English letter  -> the token in lowercase
     *   - a token made only of digits (an integer)  -> SOME_NUM
     *   - anything else is not a legal word         -> null
     *
     * @pre: token != null
     */
    public static String normalizeWord(String token) {
        boolean hasLetter = false;
        boolean allDigits = !token.isEmpty();
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                hasLetter = true;
            }
            if (c < '0' || c > '9') {
                allDigits = false;
            }
        }
        if (hasLetter) {
            return token.toLowerCase(Locale.ROOT);
        }
        if (allDigits) {
            return SOME_NUM;
        }
        return null;
    }

    /*
     * @pre: vocabulary != null
     * @post: $ret = the index of word in vocabulary, or ELEMENT_NOT_FOUND if
     * word is null or does not appear in it
     */
    public static int findIndex(String[] vocabulary, String word) {
        if (word == null) {
            return ELEMENT_NOT_FOUND;
        }
        for (int i = 0; i < vocabulary.length; i++) {
            if (word.equals(vocabulary[i])) {
                return i;
            }
        }
        return ELEMENT_NOT_FOUND;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Questions 1 - 5: implement these
    ///////////////////////////////////////////////////////////////////////////

    /*
     * @post: mVocabulary = prev(mVocabulary)
     * @post: mBigramCounts = prev(mBigramCounts)
     */
    public String[] buildVocabularyIndex(String fileName) throws IOException { // Question 1
        String text=convertFileToString(fileName);
        String[] tempTokensArray=new String[14500];
        String[] tokens = text.split("\\s+");
        int i=0,j=0;
        while ((i<14500)&&(j<tokens.length)){
            String token=normalizeWord(tokens[j]);
            if ((findIndex(tempTokensArray,token)==ELEMENT_NOT_FOUND)&&(token!=null)){
                tempTokensArray[i]=token;
                i++;
            }
            j++;
        }
        String[] vocabularyIndex = Arrays.copyOfRange(tempTokensArray, 0, i);
        return vocabularyIndex;
    }
    

        // helper function to convert a fileName to string
    public static String convertFileToString(String fileName) throws IOException { 
        StringBuilder resultBuilder=new StringBuilder();
        File fromFile = new File(fileName);
        FileReader fReader = new FileReader(fromFile);
        char[] charRead = new char[1000];
        int numRead;
        while ((numRead = fReader.read(charRead)) != -1) {
            String string = new String(charRead, 0, numRead);
            resultBuilder.append(string);
        }
        fReader.close();
        return resultBuilder.toString();
    }

    /*
     * @post: mVocabulary = prev(mVocabulary)
     * @post: mBigramCounts = prev(mBigramCounts)
     */
    public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException { // Question 2
        int len=vocabulary.length;
        int[][] counts = new int[len][len];
        File fromFile = new File(fileName);
        BufferedReader bReader = new BufferedReader(new FileReader(fromFile)); 
        String line;
        while ((line=bReader.readLine())!= null){
            String[] lineStrings=line.split("\\s+");
            for (int i=0;i<lineStrings.length-1;i++){
                String token1=normalizeWord(lineStrings[i]),token2=normalizeWord(lineStrings[i+1]);
                if ((token1!=null)&&(token2!=null)){
                    counts[findIndex(vocabulary, token1)][findIndex(vocabulary, token2)]++;
                }
            }
        }
        bReader.close();
        return counts;  
    }



    /*
     * @pre: the method initModel was called (the language model is initialized)
     * @pre: fileName is a legal file path
     */
    public void saveModel(String fileName) throws IOException { // Question 3
        String vocName = fileName+VOC_FILE_SUFFIX;
        String cntName = fileName+COUNTS_FILE_SUFFIX;
        BufferedWriter vocWriter = new BufferedWriter(new FileWriter(vocName));
        BufferedWriter cntWriter = new BufferedWriter(new FileWriter(cntName));
        int len=mVocabulary.length;
        vocWriter.write(len+ " words"+System.lineSeparator());
        for (int i=0;i<len;i++){
            vocWriter.write(i+","+mVocabulary[i]+System.lineSeparator());//this should work
            for (int j=0;j<len;j++){
                int pairCount=mBigramCounts[i][j];
                if (pairCount!=0)
                    cntWriter.write(i+","+j+":"+pairCount+System.lineSeparator());
            }
        }
        vocWriter.close();
        cntWriter.close();
    }

    /*
     * @pre: fileName is a legal file path
     */
    public void loadModel(String fileName) throws IOException { // Question 4
        String vocName = fileName+VOC_FILE_SUFFIX;
        String cntName = fileName+COUNTS_FILE_SUFFIX;
        BufferedReader vocReader = new BufferedReader(new FileReader(vocName));
        BufferedReader cntReader = new BufferedReader(new FileReader(cntName));
        String line = vocReader.readLine();
        int length=Integer.parseInt(line.split(" ")[0]);
        String[] newmVocabulary = new String[length];
        int[][] newmBigramCounts = new int[length][length];
        while ((line=vocReader.readLine())!=null)
        {
            String[] splitLine=line.split(",");
            int index=Integer.parseInt(splitLine[0]);
            newmVocabulary[index]=splitLine[1];
        }
        vocReader.close();
        while ((line=cntReader.readLine())!=null)
        {
            String[] splitLine=line.split("[,:]");
            newmBigramCounts[Integer.parseInt(splitLine[0])][Integer.parseInt(splitLine[1])]=Integer.parseInt(splitLine[2]);
        }
        cntReader.close();
        mVocabulary=newmVocabulary;
        mBigramCounts=newmBigramCounts;
    }

    /*
     * @pre: sentence is in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @pre: each two words in the sentence are separated with a single space
     * @post: if sentence is probable, according to the model, $ret = true, else, $ret = false
     */
    public boolean isLegalSentence(String sentence) { // Question 5
        String[] splitSentence=sentence.split(" ");
        if (sentence=="")
            return true;
        for (int i=0;i<splitSentence.length-1;i++){
            int index1=findIndex(mVocabulary,splitSentence[i]),index2=findIndex(mVocabulary, splitSentence[i+1]);
            if ((index1==ELEMENT_NOT_FOUND)||(index2==ELEMENT_NOT_FOUND)||(mBigramCounts[index1][index2]==0))
                return false;
        }
        if ((splitSentence.length==1)&&(findIndex(mVocabulary,splitSentence[0])==-1))
            return false;
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Queries on the model - already implemented for you.
    // They start working once Questions 1 - 2 are done.
    ///////////////////////////////////////////////////////////////////////////

    /*
     * @pre: word is in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
     */
    public int getWordIndex(String word) {
        return findIndex(mVocabulary, word);
    }

    /*
     * @pre: word1, word2 are in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
     * exist in the vocabulary, $ret = 0
     */
    public int getBigramCount(String word1, String word2) {
        int index1 = getWordIndex(word1);
        int index2 = getWordIndex(word2);
        if (index1 == ELEMENT_NOT_FOUND || index2 == ELEMENT_NOT_FOUND) {
            return 0;
        }
        return mBigramCounts[index1][index2];
    }

    /*
     * @pre: word is in lowercase, and is in mVocabulary
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = the word with the lowest vocabulary index that appears most frequently after word
     * (if a bigram starting with word was never seen, $ret will be null)
     */
    public String getMostFrequentProceeding(String word) {
        int[] row = mBigramCounts[getWordIndex(word)];
        int best = ELEMENT_NOT_FOUND;
        for (int i = 0; i < row.length; i++) {
            if (row[i] > 0 && (best == ELEMENT_NOT_FOUND || row[i] > row[best])) {
                best = i;
            }
        }
        return best == ELEMENT_NOT_FOUND ? null : mVocabulary[best];
    }

    /*
     * @pre: arr1.length = arr2.length
     * @post: if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calculates CosineSim
     */
    public static double calcCosineSim(int[] arr1, int[] arr2) {
        double dot = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < arr1.length; i++) {
            dot += (double) arr1[i] * arr2[i];
            norm1 += (double) arr1[i] * arr1[i];
            norm2 += (double) arr2[i] * arr2[i];
        }
        if (norm1 == 0 || norm2 == 0) {
            return -1;
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /*
     * @pre: word is in vocabulary
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w)
     * among all the other words in vocabulary (the lowest index wins a tie)
     */
    public String getClosestWord(String word) {
        int wordIndex = getWordIndex(word);
        int best = ELEMENT_NOT_FOUND;
        double bestSim = 0;
        for (int i = 0; i < mVocabulary.length; i++) {
            if (i == wordIndex) {
                continue;
            }
            double sim = calcCosineSim(mBigramCounts[wordIndex], mBigramCounts[i]);
            if (best == ELEMENT_NOT_FOUND || sim > bestSim) {
                best = i;
                bestSim = sim;
            }
        }
        // A vocabulary with a single word: the closest word is the word itself
        return best == ELEMENT_NOT_FOUND ? word : mVocabulary[best];
    }
}
