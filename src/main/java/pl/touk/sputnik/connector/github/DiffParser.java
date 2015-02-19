package pl.touk.sputnik.connector.github;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

public class DiffParser {

    Map<String, Map<Integer, Integer>> parse(String content) {
        Map<String, Map<Integer, Integer>> result = Maps.newHashMap();

        int inFilePosition = 1;
        int relativePosition = 1;
        String fromFile = null;

        for (String line : content.split("\n")) {
            switch (lineType(line)) {
                case DiffStart:
                    fromFile = parseDiffStart(line);
                    result.put(fromFile, new HashMap<Integer, Integer>());
                    inFilePosition = 1;
                    relativePosition = 1;
                    break;
                case LineMarker:
                    inFilePosition = parseLineMarker(line);
                    break;
                case RemovedLine:
                    relativePosition++;
                    inFilePosition++;
                    break;
                case AddedLine:
                    result.get(fromFile).put(inFilePosition, relativePosition);
                    inFilePosition++;
                    relativePosition++;
                    break;
                case Noop:
                    inFilePosition++;
                    relativePosition++;
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    // @@ -44,2 +47,2 @@
    private int parseLineMarker(String line) {
        return Integer.parseInt(line.replace("@@", "").trim().split(" ")[1].split(",")[0]);
    }

    private String parseDiffStart(String line) {
        String fileNameWithPrefix = line.split(" ")[3];
        return fileNameWithPrefix.substring(fileNameWithPrefix.indexOf("/") + 1);
    }

    private LineType lineType(String line) {
        if (line.startsWith("diff")) {
            return LineType.DiffStart;
        } else if (line.startsWith("@@ ")) {
            return LineType.LineMarker;
        } else if (line.startsWith("--- ")) {
            return LineType.FileFrom;
        } else if (line.startsWith("+++ ")) {
            return LineType.FileTo;
        } else if (line.startsWith("-")) {
            return LineType.RemovedLine;
        } else if (line.startsWith("+")) {
            return LineType.AddedLine;
        } else if (line.startsWith("index ")) {
            return LineType.Index;
        }

        return LineType.Noop;
    }
}
