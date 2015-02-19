package pl.touk.sputnik.connector.github;

enum LineType {
    AddedLine,
    RemovedLine,
    DiffStart,
    FileFrom,
    FileTo,
    LineMarker,
    Index,
    Noop
}
