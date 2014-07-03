package pl.touk.sputnik.connector.gerrit.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gerrit request for review input.
 * Used with JSON marshaller only.
 *
 * Example JSON:
 *
 * {
 * "message": "Some nits need to be fixed.",
 * "labels": {
 * "Code-Review": -1
 * },
 * "comments": {
 * "gerrit-server/src/main/java/com/google/gerrit/server/project/RefControl.java": [
 * {
 * "line": 23,
 * "message": "[nit] trailing whitespace"
 * },
 * {
 * "line": 49,
 * "message": "[nit] s/conrtol/control"
 * }
 * ]
 * }
 * }
 */
public class ReviewInput {

    public static final String CODE_REVIEW = "Code-Review";
    public String message = "Looks good to me.";
    public Map<String, Integer> labels = new HashMap<>();
    public Map<String, List<ReviewFileComment>> comments = new HashMap<>();

    public void setLabelToPlusOne() {
        labels.put(CODE_REVIEW, 1);
    }

    public int getReviewCount() {
        int count = 0;
        for (Map.Entry<String, List<ReviewFileComment>> reviewFile : comments.entrySet()) {
            count += reviewFile.getValue().size();
        }
        return count;
    }
}
