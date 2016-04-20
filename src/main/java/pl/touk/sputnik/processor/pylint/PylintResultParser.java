package pl.touk.sputnik.processor.pylint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.processor.pylint.json.PylintMessage;
import pl.touk.sputnik.processor.pylint.json.PylintMessages;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PylintResultParser implements ExternalProcessResultParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Violation> parse(String pylintOutput) {
        if (StringUtils.isEmpty(pylintOutput)) {
            return Collections.emptyList();
        }
        try {
            List<Violation> violations = new ArrayList<>();
            PylintMessages messages = objectMapper.readValue(removeHeaderFromPylintOutput(pylintOutput), PylintMessages.class);
            for (PylintMessage message : messages) {
                Violation violation = new Violation(message.getPath(),
                        message.getLine(),
                        formatViolationMessageFromPylint(message),
                        pylintMessageTypeToSeverity(message.getType()));
                violations.add(violation);
            }
            return violations;
        } catch (IOException e) {
            throw new PylintException("Error when converting from json format", e);
        }
    }

    private String removeHeaderFromPylintOutput(String pylintOutput) {
        return pylintOutput.replace("No config file found, using default configuration", "");
    }

    private String formatViolationMessageFromPylint(PylintMessage message) {
        return message.getMessage() + " [" + message.getSymbol() + "]";
    }

    private Severity pylintMessageTypeToSeverity(String messageType) {
        if (messageType.equals("error") || messageType.equals("fatal")) {
            return Severity.ERROR;
        } else if (messageType.equals("warning")) {
            return Severity.WARNING;
        } else if (messageType.equals("convention") || messageType.equals("refactor")) {
            return Severity.INFO;
        } else {
            throw new PylintException("Unknown message type returned by pylint in message (type = " + messageType + ")");
        }
    }
}
