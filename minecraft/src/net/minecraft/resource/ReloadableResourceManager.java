package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceLoadProgressProvider;

public interface ReloadableResourceManager extends ResourceManager {
	void reload(List<ResourcePack> list);

	@Environment(EnvType.CLIENT)
	default ResourceLoadProgressProvider method_18231() {
		return this.method_18230(null);
	}

	@Environment(EnvType.CLIENT)
	ResourceLoadProgressProvider method_18230(@Nullable CompletableFuture<Void> completableFuture);

	default ResourceLoadProgressProvider method_18233() {
		return this.method_18232(null);
	}

	ResourceLoadProgressProvider method_18232(@Nullable CompletableFuture<Void> completableFuture);

	void addListener(ResourceReloadListener<?> resourceReloadListener);
}
