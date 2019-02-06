package net.minecraft.client.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceReloadStatus;

public interface ResourceLoadProgressProvider {
	void method_18226();

	@Environment(EnvType.CLIENT)
	ResourceReloadStatus getStatus();

	@Environment(EnvType.CLIENT)
	int getTotal();

	@Environment(EnvType.CLIENT)
	int getProgress();
}
