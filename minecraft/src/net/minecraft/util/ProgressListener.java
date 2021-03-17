package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

public interface ProgressListener {
	void setTitle(Text title);

	@Environment(EnvType.CLIENT)
	void setTitleAndTask(Text title);

	void setTask(Text task);

	void progressStagePercentage(int percentage);

	@Environment(EnvType.CLIENT)
	void setDone();
}
