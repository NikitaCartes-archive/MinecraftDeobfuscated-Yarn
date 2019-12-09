/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface TooltipContext {
    public boolean isAdvanced();

    @Environment(value=EnvType.CLIENT)
    public static enum Default implements TooltipContext
    {
        NORMAL(false),
        ADVANCED(true);

        private final boolean advanced;

        private Default(boolean advanced) {
            this.advanced = advanced;
        }

        @Override
        public boolean isAdvanced() {
            return this.advanced;
        }
    }
}

