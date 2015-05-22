package pl.touk.sputnik.processor.sonar;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * Parses a json file produced by a run of Sonar.
 */
@Slf4j
@AllArgsConstructor
class SonarResultParser {

    private final File resultFile;

    /**
     * A component in the result file is identified by a key.
     * It may reference another component by its key (called moduleKey).
     */
    @AllArgsConstructor
    private static class Component {
        public String path;
        public String moduleKey;
    }

    /**
     * Parses the file and returns all the issues as a ReviewResult.
     */
    public ReviewResult parseResults() throws IOException {
        ReviewResult result = new ReviewResult();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new FileReader(resultFile));
        JsonNode issues = rootNode.path("issues");
        Iterator<JsonNode> issuesIterator = issues.iterator();
        Map<String, Component> components = getComponents(rootNode.path("components"));

        while (issuesIterator.hasNext()) {
            JsonNode issue = issuesIterator.next();
            boolean isNew = issue.path("isNew").asBoolean();
            if (!isNew) {
                log.debug("Skipping already indexed issue: {}", issue.toString());
                continue;
            }
            JsonNode lineNode = issue.path("line");
            if (lineNode.isMissingNode()) {
                log.debug("Skipping an issue with no line information: {}", issue.toString());
                continue;
            }
            int line = lineNode.asInt();
            String message = issue.path("message").asText();
            String file = getIssueFilePath(issue.path("component").asText(), components);
            String severity = issue.path("severity").asText();
            String rule = issue.path("rule").asText();
            result.add(new Violation(file, line, String.format("%s (Rule: %s)", message, rule), getSeverity(severity)));
        }
        return result;
    }

    /**
     * Converts a Sonar severity to a Sputnik severity.
     * @param severityName severity to convert.
     */
    static Severity getSeverity(String severityName) {
        switch (severityName) {
        case "BLOCKER":
        case "CRITICAL":
        case "MAJOR":
            return Severity.ERROR;
        case "MINOR":
            return Severity.WARNING;
        case "INFO":
            return Severity.INFO;
        default:
          log.warn("Unknown severity: " + severityName);
        }
        return Severity.WARNING;
    }

    /**
     * Extracts all the components from the json data.
     */
    private Map<String, Component> getComponents(JsonNode componentsNode) {
        Iterator<JsonNode> it = componentsNode.iterator();
        Map<String, Component> components = Maps.newHashMap();

        while(it.hasNext()){
            JsonNode componentNode = it.next();
            JsonNode pathNode = componentNode.path("path");
            JsonNode moduleNode = componentNode.path("moduleKey");
            components.put(componentNode.path("key").asText(),
                    new Component(pathNode.asText(), moduleNode.isMissingNode()?null:moduleNode.asText()));
        }

        return components;
    }

    /**
     * Returns the path of the file linked to an issue created by Sonar.
     * The path is relative to the folder where Sonar has been run.
     *
     * @param issueComponent "component" field in an issue.
     * @param components information about all components.
     */
    private String getIssueFilePath(String issueComponent, Map<String, Component> components) {
        Component comp = components.get(issueComponent);
        String file = comp.path;
        if (!Strings.isNullOrEmpty(comp.moduleKey)) {
            Component moduleComp = components.get(comp.moduleKey);
            file = moduleComp.path + '/' + file;
        }
        return file;
    }
}
