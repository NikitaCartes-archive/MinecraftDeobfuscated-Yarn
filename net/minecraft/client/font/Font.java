/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Closeable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.RenderableGlyph;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface Font
extends Closeable {
    @Override
    default public void close() {
    }

    @Nullable
    default public RenderableGlyph getGlyph(int i) {
        return null;
    }

    public IntSet method_27442();
}

