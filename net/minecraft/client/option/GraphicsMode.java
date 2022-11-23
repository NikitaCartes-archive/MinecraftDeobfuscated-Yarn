/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(value=EnvType.CLIENT)
public enum GraphicsMode implements TranslatableOption
{
    FAST(0, "options.graphics.fast"),
    FANCY(1, "options.graphics.fancy"),
    FABULOUS(2, "options.graphics.fabulous");

    private static final IntFunction<GraphicsMode> BY_ID;
    private final int id;
    private final String translationKey;

    private GraphicsMode(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public String toString() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case FAST -> "fast";
            case FANCY -> "fancy";
            case FABULOUS -> "fabulous";
        };
    }

    public static GraphicsMode byId(int id) {
        return BY_ID.apply(id);
    }

    static {
        BY_ID = ValueLists.createIdToValueFunction(GraphicsMode::getId, GraphicsMode.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

