package io.gitlab.swded.system.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gitlab.swded.system.model.DataRow;
import javafx.concurrent.Worker;
import javafx.scene.Scene;

import java.util.List;

public class Chart2D extends Chart {
    private final Browser browser = new Browser("chart2D.html");
    private final List<DataRow> data;
    private final int classColumnIndex;
    private final int[] valueColumnIndexes;
    private final List<String> header;

    public Chart2D(List<DataRow> data, int classColumnIndex, int[] valueColumnIndexes, List<String> header) {
        this.data = data;
        this.classColumnIndex = classColumnIndex;
        this.valueColumnIndexes = valueColumnIndexes;
        this.header = header;
        Scene scene = new Scene(browser, width, height);
        stage.setTitle("2D Chart");
        stage.setScene(scene);
    }

    public void show() {
        browser.webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                doShowChart(data, classColumnIndex, valueColumnIndexes, header);
            }
        });
        super.show();
    }

    @Override
    public void close() {
        stage.close();
    }

    private void doShowChart(List<DataRow> data, int classColumn, int[] valueColumns, List<String> header) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String dataJSON = mapper.writeValueAsString(data);
            String valueColumnsJSON = mapper.writeValueAsString(valueColumns);
            String headerJSON = mapper.writeValueAsString(header);
            browser.webEngine.executeScript("showChart(" + dataJSON + ", " + classColumn + ", " + valueColumnsJSON + ", " + headerJSON + ")");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
