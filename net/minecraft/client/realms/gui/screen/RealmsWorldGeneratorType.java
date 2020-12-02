/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public enum RealmsWorldGeneratorType {
    DEFAULT(0, new TranslatableText("generator.default")),
    FLAT(1, new TranslatableText("generator.flat")),
    LARGE_BIOMES(2, new TranslatableText("generator.large_biomes")),
    AMPLIFIED(3, new TranslatableText("generator.amplified"));

    private final int id;
    private final Text text;

    private RealmsWorldGeneratorType(int id, Text text) {
        this.id = id;
        this.text = text;
    }

    public Text getText() {
        return this.text;
    }

    public int getId() {
        return this.id;
    }
}

