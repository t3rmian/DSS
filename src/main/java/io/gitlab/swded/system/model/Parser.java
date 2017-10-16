package io.gitlab.swded.system.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static final String SPLIT_CHARACTER = " ";
    private String[] header;
    private List<DataRow> rows = new ArrayList<>();
    private boolean fileWithHeader;

    public Parser(boolean fileWithHeader) {
        this.fileWithHeader = fileWithHeader;
    }

    public String[] getHeader() {
        return header;
    }

    public DataRow[] getData() {
        return rows.toArray(new DataRow[0]);
    }

    public void parse(BufferedReader reader) throws IOException {
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
            if (fileWithHeader && header == null) {
                header = line.split(SPLIT_CHARACTER);
            } else {
                String[] values = line.split(SPLIT_CHARACTER);
                this.rows.add(new DataRow(values));
            }
        }
    }

    private String replaceMultipleSpacesWith(String text, String replacement) {
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replacement);
    }

}
