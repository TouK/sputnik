package pl.touk.sputnik.processor.pylint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import pl.touk.sputnik.processor.pylint.json.PylintMessage;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PylintResultParser implements ExternalProcessResultParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PYLINT_ERROR = "error";
    private static final String PYLINT_FATAL = "fatal";
    private static final String PYLINT_WARNING = "warning";
    private static final String PYLINT_CONVENTION = "convention";
    private static final String PYLINT_REFACTOR = "refactor";

    @Override
    public List<Violation> parse(String pylintOutput) {
        if (StringUtils.isEmpty(pylintOutput)) {
            return Collections.emptyList();
        }
        try {
            List<Violation> violations = new ArrayList<>();
            List<PylintMessage> messages = objectMapper.readValue(removeHeaderFromPylintOutput(pylintOutput),
                    new TypeReference<List<PylintMessage>>() { });
            for (PylintMessage message : messages) {
                Violation violation = new Violation(message.getPath(),
                        message.getLine(),
                        formatViolationMessageFromPylint(message),
                        pylintMessageTypeToSeverity(message.getMessage(), message.getType()));
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
        if (message.getMessageId() != null) {
            return message.getMessage() + " [" + message.getMessageId() + ": " + message.getSymbol() + "]";
        } else {
            return message.getMessage() + " [" + message.getSymbol() + "]";
        }
    }

    private Severity pylintMessageTypeToSeverity(String message, String messageType) {
        if (PYLINT_ERROR.equals(messageType)) {
            return Severity.ERROR;
        } else if (PYLINT_WARNING.equals(messageType)) {
            return Severity.WARNING;
        } else if (PYLINT_CONVENTION.equals(messageType) || PYLINT_REFACTOR.equals(messageType)) {
            return Severity.INFO;
        } else if (PYLINT_FATAL.equals(messageType)) {
            throw new PylintException("Fatal error from pylint (" + message + ")");
        } else {
            throw new PylintException("Unknown message type returned by pylint (type = " + messageType + ")");
        }
    }
}
