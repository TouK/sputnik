package pl.touk.sputnik.processor.shellcheck;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import pl.touk.sputnik.processor.shellcheck.json.ShellcheckMessage;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

class ShellcheckResultParser implements ExternalProcessResultParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SHELLCHECK_ERROR = "error";
    private static final String SHELLCHECK_WARNING = "warning";
    private static final String SHELLCHECK_INFO = "info";

    @Override
    public List<Violation> parse(String shellcheckOutput) {
        if (StringUtils.isEmpty(shellcheckOutput)) {
            return Collections.emptyList();
        }
        try {
            List<ShellcheckMessage> violationMessages = objectMapper.readValue(shellcheckOutput,
                    new TypeReference<List<ShellcheckMessage>>() {
                    });

            return violationMessages
                    .stream()
                    .map(violationMessage -> mapToViolation(violationMessage))
                    .collect(toList());
        } catch (IOException e) {
            throw new ShellcheckException("Error when converting from json format", e);
        }
    }

    private Violation mapToViolation(ShellcheckMessage violationMessage) {
        return new Violation(violationMessage.getFile(),
                violationMessage.getLine(),
                formatViolationMessage(violationMessage),
                shellcheckMessageTypeToSeverity(violationMessage.getLevel()));
    }

    private String formatViolationMessage(ShellcheckMessage violationMessage) {
        return "[Code:" + violationMessage.getCode() + "] Message: " + violationMessage.getMessage();
    }

    private Severity shellcheckMessageTypeToSeverity(String messageType) {
        if (SHELLCHECK_ERROR.equals(messageType)) {
            return Severity.ERROR;
        } else if (SHELLCHECK_WARNING.equals(messageType)) {
            return Severity.WARNING;
        } else if (SHELLCHECK_INFO.equals(messageType)) {
            return Severity.INFO;
        } else {
            throw new ShellcheckException("Unknown message type returned by shellcheck (type = " + messageType + ")");
        }
    }
}
