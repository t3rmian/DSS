package io.gitlab.swded.system.model;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ParserTest {

    @Test
    public void parse() {
        String dataSetName = "IRISDAT.TXT";
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(dataSetName)))) {
            Parser parser = new Parser(true);
            parser.parse(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
