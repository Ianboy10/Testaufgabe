package de.ianboy10.firmenregister.managers;

import lombok.Getter;

public enum RegisterTypes {
    BANKINGID("Kontonummer"),
    OWNER("Besitzer"),
    DESCRIPTION("Firmenbeschreibung"),
    BIZ("BIZ"),
    MEMBERS("Mitglieder");

    @Getter
    private final String displayName;

    RegisterTypes(String displayName) {
        this.displayName = displayName;
    }
}