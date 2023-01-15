package com.usi.ch.syn.core.model.view.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class ColorPalette {

    public static final ColorPalette DEFAULT = new ColorPalette(
            Color.decode("#58A55C"),
            Color.decode("#D85040"),
            Color.decode("#134BA2"),
            Color.decode("#4285F4"),
            Color.decode("#F1BD42"),
            Color.decode("#808080")
    );

    private Color addColor;
    private Color deleteColor;
    private Color renameColor;
    private Color moveColor;
    private Color modifyColor;
    private Color baseColor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColorPalette)) return false;
        ColorPalette that = (ColorPalette) o;
        return Objects.equals(getAddColor(), that.getAddColor()) && Objects.equals(getDeleteColor(), that.getDeleteColor()) && Objects.equals(getRenameColor(), that.getRenameColor()) && Objects.equals(getMoveColor(), that.getMoveColor()) && Objects.equals(getModifyColor(), that.getModifyColor()) && Objects.equals(getBaseColor(), that.getBaseColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddColor(), getDeleteColor(), getRenameColor(), getMoveColor(), getModifyColor(), getBaseColor());
    }
}
