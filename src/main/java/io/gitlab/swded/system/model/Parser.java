package io.gitlab.swded.system.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private final BufferedReader reader;
    private String[] header;
    private List<Float[]> values = new ArrayList<>();
    private List<String> classes = new ArrayList<>();
    private int classesColumnIndex;

    public int getClassesColumnIndex() {
        return classesColumnIndex;
    }

    public String[] getHeader() {
        return header;
    }

    public Float[][] getValues() {
        return values.toArray(new Float[0][0]);
    }

    public String[] getClasses() {
        return classes.toArray(new String[0]);
    }

    public Data[] getData() {
        Data[] data = new Data[values.size()];
        for (int i = 0; i < values.size(); i++) {
            data[i] = new Data(values.get(i), classes.get(i));
        }
        return data;
    }

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
                        classesColumnIndex = dataRowIndex;
                        classes.add(value);
                    }
                }
                this.values.add(dataRow);
            }
        }
    }

    private String replaceMultipleSpacesWith(String text, String replacement) {
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replacement);
    }

}
