package pl.touk.sputnik.configuration;

import org.apache.commons.lang3.StringUtils;

public enum Provider {

    GITHUB("github"), GITLAB("gitlab");

    private final String name;

    Provider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Provider from(String name) {
        if (StringUtils.isNoneBlank(name)) {
            return Provider.valueOf(name.toUpperCase());
        } else {
            return null;
        }
    }
}
