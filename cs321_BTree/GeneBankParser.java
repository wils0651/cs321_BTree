import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneBankParser {
    private static final String START_TAG = "ORIGIN";
    private static final String END_TAG = "//";
    private static final List<Character> VALID_CHARS = Arrays.asList('a', 'A', 't', 'T', 'c', 'C', 'g', 'G', 'n', 'N');
    private static final List<Character> DELIMITERS = Arrays.asList('n', 'N');

    public List<String> parseSequencesFromFileContents(String fileContents) {
        List<String> sequences = new ArrayList<>();

        int startIndex = fileContents.indexOf(START_TAG);
        while (startIndex > -1) {
            int endIndex = fileContents.indexOf(END_TAG, startIndex);
            if (endIndex == -1) {
                throw new IllegalStateException("Could not find closing tag for starting tag index of: " + startIndex);
            }

            String content = fileContents.substring(startIndex + START_TAG.length(), endIndex);
            StringBuilder fullSequenceBuilder = new StringBuilder();
            for (char c : content.toCharArray()) {
                if (VALID_CHARS.contains(c)) {
                    fullSequenceBuilder.append(Character.toLowerCase(c));
                }
            }

            String fullSequence = fullSequenceBuilder.toString();
            for(String subSequence : fullSequence.split("[nN]")){
                if(!subSequence.isEmpty()){
                    sequences.add(subSequence);
                }
            }

            startIndex = fileContents.indexOf(START_TAG, endIndex);
        }

        return sequences;
    }

    public List<String> generateSubSequencesFromSequence(String sequence, int subSequenceLength){
        List<String> subSequences = new ArrayList<>();
        if((sequence == null) || sequence.isEmpty()){
            return subSequences;
        }

        for(int i = 0; i <= sequence.length() - subSequenceLength; i++){
            subSequences.add(sequence.substring(i, i + subSequenceLength));
        }

        return subSequences;
    }

    public long convertSequenceToLong(String sequence){
        long value = 0L;

        for(int i = 0; i < sequence.length(); i++){
            char c = sequence.charAt(i);
            switch(c){
                case 'a':
                case 'A':
                    value *= 2;
                    value *= 2;
                    break;
                case 'c':
                case 'C':
                    value *= 2;
                    value *= 2;
                    value++;
                    break;
                case 'g':
                case 'G':
                    value *= 2;
                    value++;
                    value *= 2;
                    break;
                case 't':
                case 'T':
                    value *= 2;
                    value++;
                    value *= 2;
                    value++;
                    break;
            }
        }

        return value;
    }

    public String convertLongToSequence(long value, int sequenceLength){
        String sequence = "";

        while(value > 0){
            boolean leftMostBit = ((value % 2) == 1);
            value /= 2;

            boolean rightMostBit = ((value % 2) == 1);
            value /= 2;

            if(leftMostBit && rightMostBit){
                sequence += "t";
            } else if (leftMostBit){
                sequence += "c";
            } else if (rightMostBit){
                sequence += "g";
            } else {
                sequence += "a";
            }
        }

        for(int i = sequence.length(); i < sequenceLength; i++){
            sequence += 'a';
        }

        return new StringBuilder(sequence).reverse().toString();
    }
}