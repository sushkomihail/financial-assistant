package com.paxeevamaria.logic;

import javafx.scene.control.TextFormatter;

public enum TextFieldMask {
    INT("^[0-9]*$"),
    FLOAT("^[0-9]*([.,])?([0-9]+)?$");

    private final String mask;

    public String getMask() {
        return mask;
    }

    public TextFormatter<String> getTextFormatter() {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().matches(mask)) {
                return change;
            } else {
                return null;
            }
        });
    }

    TextFieldMask(String mask) {
        this.mask = mask;
    }
}
