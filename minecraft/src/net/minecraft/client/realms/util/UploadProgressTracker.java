package net.minecraft.client.realms.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface UploadProgressTracker {
	UploadProgress getUploadProgress();

	void updateProgressDisplay();

	static UploadProgressTracker create() {
		return new UploadProgressTracker() {
			private final UploadProgress progress = new UploadProgress();

			@Override
			public UploadProgress getUploadProgress() {
				return this.progress;
			}

			@Override
			public void updateProgressDisplay() {
			}
		};
	}
}
