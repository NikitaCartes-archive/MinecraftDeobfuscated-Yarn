package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

public interface ProgressListener {
	void method_15412(Text text);

	@Environment(EnvType.CLIENT)
	void method_15413(Text text);

	void method_15414(Text text);

	void progressStagePercentage(int percentage);

	@Environment(EnvType.CLIENT)
	void setDone();
}
