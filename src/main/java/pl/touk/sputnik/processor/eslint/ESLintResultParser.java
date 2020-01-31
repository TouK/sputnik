package pl.touk.sputnik.processor.eslint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.touk.sputnik.processor.eslint.json.FileViolations;
import pl.touk.sputnik.processor.eslint.json.Message;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ESLintResultParser implements ExternalProcessResultParser {

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public List<Violation> parse(String output) {
        List<Violation> result = new ArrayList<>();
        try {
            List<FileViolations> fileViolations =
                    objectMapper.readValue(output, new TypeReference<List<FileViolations>>() { });
            for (FileViolations violation : fileViolations) {
                for (Message message : violation.getMessages()) {
                    result.add(new Violation(violation.getFilePath(), message.getLine(), message.getMessage(),
                            mapSeverity(message.getSeverity())));
                }
            }
        } catch (IOException e) {
            throw new ESLintException("Error when converting from json format", e);
        }
        return result;
    }

    private Severity mapSeverity(int eslintValue) {
        switch (eslintValue) {
            case 1:
                return Severity.WARNING;
            case 2:
                return Severity.ERROR;
            default:
                return Severity.INFO;
        }
    }

}
