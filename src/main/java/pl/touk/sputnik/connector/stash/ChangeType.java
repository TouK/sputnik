package pl.touk.sputnik.connector.stash;

public enum ChangeType {
    ADDED, REMOVED, CONTEXT, NONE;
    
    public String getNameForStash() {
        if (this.equals(NONE)) {
            return CONTEXT.name();
        }
        return name();
    }
}
