package api.sikra.app.endpoint.event.consumer.model;

import api.sikra.app.PojaGenerated;
import api.sikra.app.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
