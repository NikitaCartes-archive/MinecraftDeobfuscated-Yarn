/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface StatsListener {
    public static final String[] PROGRESS_BAR_STAGES = new String[]{"oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO"};

    public void onStatsReady();
}

