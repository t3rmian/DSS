function debug(obj) {
    document.getElementById("debug").innerHTML += JSON.stringify(obj);
}

function showChart(data, classIndex, valueIndexes, header) {

    var classes = {};
    try {
        for (var i = 0; i < data.length; i++) {
            var klass = data[i].values[classIndex].text;
            if (classes[klass] == null) {
                classes[klass] = {
                    x: [],
                    y: [],
                    mode: 'markers',
                    type: "scatter",
                    name: klass
                };
            }
            classes[klass].x.push(data[i].values[valueIndexes[0]].value);
            classes[klass].y.push(data[i].values[valueIndexes[1]].value);
        }
        var traces = [];
        for (var prop in classes) {
            if (classes.hasOwnProperty(prop)) {
                traces.push(classes[prop]);
            }
        }
        var layout = {
            title: header[valueIndexes[0]] + " \\ " + header[valueIndexes[1]] + " for " + header[classIndex],
            xaxis: {
                title: header[valueIndexes[0]],
                titlefont: {
                    family: 'Courier New, monospace',
                    size: 18,
                    color: '#000000'
                }
            },
            yaxis: {
                title: header[valueIndexes[1]],
                titlefont: {
                    family: 'Courier New, monospace',
                    size: 18,
                    color: '#000000'
                }
            }
        };
        Plotly.newPlot('chart', traces, layout);
        window.addEventListener("resize", function () {
            Plotly.Plots.resize(document.getElementById('chart'));
        });
    } catch (error) {
        debug(error.message);
        debug(error);
    }
}