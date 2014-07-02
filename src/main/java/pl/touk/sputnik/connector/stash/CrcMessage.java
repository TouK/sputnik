package pl.touk.sputnik.connector.stash;

import org.apache.commons.codec.binary.Base64;
import pl.touk.sputnik.connector.gerrit.json.ReviewLineComment;

public class CrcMessage extends ReviewLineComment {
    public CrcMessage(ReviewLineComment comment) {
        super(comment.line, comment.message);
    }

    @Override
    public String getMessage() {
        return String.format("%s\ncrc:%s", message, getCrc());
    }

    public String getCrc() {
        return base64();
    }

    private String base64() {
        byte[] encodedBytes = Base64.encodeBase64(String.format("%s %s", line, message).getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte encodedByte : encodedBytes) {
            sb.append(Integer.toHexString(encodedByte));
        }
        return sb.toString();
    }
}
