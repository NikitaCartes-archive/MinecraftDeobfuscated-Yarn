/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.item;

public interface TooltipContext {
    public boolean isAdvanced();

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

