package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface StatsListener {
	String[] PROGRESS_BAR_STAGES = new String[]{"oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO"};

	void onStatsReady();
}
