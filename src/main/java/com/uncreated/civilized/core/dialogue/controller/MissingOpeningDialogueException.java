package com.uncreated.civilized.core.dialogue.controller;

public class MissingOpeningDialogueException extends RuntimeException {
    public MissingOpeningDialogueException() {
        super("Could not find a suitable opening dialogue for a specific villager. This indicates a problem with dialogue questline logic that the mod developer should fix.");
    }
}
