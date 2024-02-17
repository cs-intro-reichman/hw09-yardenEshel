import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;

    // The window length used in this model.
    int windowLength;

    // The random number generator used by this model.
    private Random randomGenerator;

    /**
     * Constructs a language model with the given window length and a given
     * seed value. Generating texts from this model multiple times with the
     * same seed value will produce the same random texts. Good for debugging.
     */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /**
     * Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production.
     */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
    public void train(String fileName) {
        In inputFile = new In(fileName);
        String wholeFileString = "";
        wholeFileString = inputFile.readAll();
        for (int i = 0; i + windowLength < wholeFileString.length(); i++) {

            String key = wholeFileString.substring(i, i + windowLength);
            List value = CharDataMap.get(key);
            if (value != null) {
                if (value.indexOf(wholeFileString.charAt(i + windowLength)) != -1) {
                    value.update(wholeFileString.charAt(i + windowLength));

                } else {
                    value.addFirst(wholeFileString.charAt(i + windowLength));
                }
            } else {
                CharDataMap.put(key, new List());
                CharDataMap.get(key).addFirst(wholeFileString.charAt(i + windowLength));
            }
            calculateProbabilities(CharDataMap.get(key));
        }

    }

    // Computes and sets the probabilities (p and cp fields) of all the
    // characters in the given list. */
    public void calculateProbabilities(List probs) {
        double generalNumber = 0;
        double currentIterator = 0;
        for (int i = 0; i < probs.getSize(); i++) {
            generalNumber += probs.get(i).count;
        }
        for (int i = 0; i < probs.getSize(); i++) {
            probs.get(i).p = probs.get(i).count / generalNumber;
            probs.get(i).cp = currentIterator + probs.get(i).p;
            currentIterator = probs.get(i).cp;
        }
    }

    // Returns a random character from the given probabilities list.
    public char getRandomChar(List probs) {
        double around = randomGenerator.nextDouble();
        for (int i = 0; i < probs.getSize(); i++) {
            if (probs.get(i).cp >= around) {
                return probs.get(i).chr;
            }
        }
        return 0;
    }

    /**
     * Generates a random text, based on the probabilities that were learned during
     * training.
     * 
     * @param initialText     - text to start with. If initialText's last substring
     *                        of size numberOfLetters
     *                        doesn't appear as a key in Map, we generate no text
     *                        and return only the initial text.
     * @param numberOfLetters - the size of text to generate
     * @return the generated text
     */
    public String generate(String initialText, int textLength) {
        if (initialText.length() >= windowLength) {
            for (int i = 0; i < textLength; i++) {
                initialText += getRandomChar(
                        CharDataMap.get(initialText.substring(initialText.length() - windowLength)));
            }
        }
        return initialText;
    }

    /** Returns a string representing the map of this language model. */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String key : CharDataMap.keySet()) {
            List keyProbs = CharDataMap.get(key);
            str.append(key + " : " + keyProbs + "\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        // Your code goes here
    }
}
