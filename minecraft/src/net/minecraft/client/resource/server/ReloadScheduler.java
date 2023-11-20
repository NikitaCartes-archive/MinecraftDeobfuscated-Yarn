package net.minecraft.client.resource.server;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ReloadScheduler {
	void scheduleReload(ReloadScheduler.ReloadContext context);

	@Environment(EnvType.CLIENT)
	public static record PackInfo(UUID id, Path path) {
	}

	@Environment(EnvType.CLIENT)
	public interface ReloadContext {
		void onSuccess();

		void onFailure(boolean force);

		List<ReloadScheduler.PackInfo> getPacks();
	}
}
