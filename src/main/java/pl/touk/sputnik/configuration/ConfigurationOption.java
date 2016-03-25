package pl.touk.sputnik.configuration;

public interface  ConfigurationOption {
    String getKey();
    String getDescription();
    String getDefaultValue();
}
