package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;

public interface ProgressListener {
	void method_15412(TextComponent textComponent);

	@Environment(EnvType.CLIENT)
	void method_15413(TextComponent textComponent);

	void method_15414(TextComponent textComponent);

	void progressStagePercentage(int i);

	@Environment(EnvType.CLIENT)
	void method_15411();
}
