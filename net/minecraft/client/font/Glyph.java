/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface Glyph {
    public float getAdvance();

    default public float getAdvance(boolean bold) {
        return this.getAdvance() + (bold ? this.getBoldOffset() : 0.0f);
    }

    default public float getBearingX() {
        return 0.0f;
    }

    default public float getAscent() {
        return 0.0f;
    }

    default public float getBoldOffset() {
        return 1.0f;
    }

    default public float getShadowOffset() {
        return 1.0f;
    }
}

