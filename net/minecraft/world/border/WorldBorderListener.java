/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.border;

import net.minecraft.world.border.WorldBorder;

public interface WorldBorderListener {
    public void onSizeChange(WorldBorder var1, double var2);

    public void onInterpolateSize(WorldBorder var1, double var2, double var4, long var6);

    public void onCenterChanged(WorldBorder var1, double var2, double var4);

    public void onWarningTimeChanged(WorldBorder var1, int var2);

    public void onWarningBlocksChanged(WorldBorder var1, int var2);

    public void onDamagePerBlockChanged(WorldBorder var1, double var2);

    public void onSafeZoneChanged(WorldBorder var1, double var2);

    public static class WorldBorderSyncer
    implements WorldBorderListener {
        private final WorldBorder border;

        public WorldBorderSyncer(WorldBorder border) {
            this.border = border;
        }

        @Override
        public void onSizeChange(WorldBorder border, double size) {
            this.border.setSize(size);
        }

        @Override
        public void onInterpolateSize(WorldBorder border, double fromSize, double toSize, long time) {
            this.border.interpolateSize(fromSize, toSize, time);
        }

        @Override
        public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
            this.border.setCenter(centerX, centerZ);
        }

        @Override
        public void onWarningTimeChanged(WorldBorder border, int warningTime) {
            this.border.setWarningTime(warningTime);
        }

        @Override
        public void onWarningBlocksChanged(WorldBorder border, int warningBlockDistance) {
            this.border.setWarningBlocks(warningBlockDistance);
        }

        @Override
        public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
            this.border.setDamagePerBlock(damagePerBlock);
        }

        @Override
        public void onSafeZoneChanged(WorldBorder border, double safeZoneRadius) {
            this.border.setSafeZone(safeZoneRadius);
        }
    }
}

