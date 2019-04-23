package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

public interface ProgressListener {
	void method_15412(Component component);

	@Environment(EnvType.CLIENT)
	void method_15413(Component component);

	void method_15414(Component component);

	void progressStagePercentage(int i);

	@Environment(EnvType.CLIENT)
	void setDone();
}
