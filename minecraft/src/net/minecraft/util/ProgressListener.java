package net.minecraft.util;

import net.minecraft.text.Text;

public interface ProgressListener {
	void setTitle(Text title);

	void setTitleAndTask(Text title);

	void setTask(Text task);

	void progressStagePercentage(int percentage);

	void setDone();
}
