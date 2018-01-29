package pl.touk.sputnik.processor.tslint;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.processor.tslint.json.ListViolationsResponse;
import pl.touk.sputnik.processor.tslint.json.TSLintFileInfo;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class TSLintResultParser implements ExternalProcessResultParser {

	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public List<Violation> parse(String jsonViolations) {
        if (StringUtils.isEmpty(jsonViolations)) {
            return Collections.emptyList();
        }
        try {
            List<Violation> result = new ArrayList<>();
            ListViolationsResponse violations = objectMapper
                    .readValue(jsonViolations, ListViolationsResponse.class);
            log.debug(String.format("Converted from json format to %d violations.", violations.size()));
            for (TSLintFileInfo fileInfo : violations) {
                Violation violation = new Violation(fileInfo.getName(), fileInfo.getStartPosition().getLine(),
                        fileInfo.getFailure(), Severity.ERROR);
                result.add(violation);
            }
            return result;
        } catch (IOException e) {
            throw new TSLintException("Error when converting from json format", e);
        }
    }
}
