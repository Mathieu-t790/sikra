package api.sikra.app.file.hash;

import api.sikra.app.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
