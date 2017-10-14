package io.gitlab.swded.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private final BufferedReader reader;
    private String[] header;
    private List<Float[]> data = new ArrayList<>();
    private List<String> classes = new ArrayList<>();

    public Parser(BufferedReader reader) {
        this.reader = reader;
    }

    public void parse() throws IOException {
        String line = reader.readLine();
        while (line != null) {
            parseLine(line);
            line = reader.readLine();
        }
    }

    private void parseLine(String line) {
        if (line.startsWith("#")) {
            System.out.println(line);
        } else if (!line.isEmpty()) {
            line = line.replace(',', '.');
            line = replaceMultipleSpacesWith(line, " ");
            if (header == null) {
                header = line.split(" ");
            } else {
                String[] values = line.split(" ");
                Float[] dataRow = new Float[values.length - 1];
                int dataRowIndex = 0;
                for (String value : values) {
                    try {
                        dataRow[dataRowIndex] = Float.parseFloat(value);
                        dataRowIndex++;
                    } catch (NumberFormatException nfe) {
                        classes.add(value);
                    }
                }
                data.add(dataRow);
            }
        }
    }

    private String replaceMultipleSpacesWith(String text, String replacement) {
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replacement);
    }

}
