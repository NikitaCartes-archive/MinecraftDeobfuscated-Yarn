package net.minecraft.client.resource.server;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface PackStateChangeCallback {
	void sendResponse(UUID id, PackStateChangeCallback.State state);

	@Environment(EnvType.CLIENT)
	public static enum State {
		ACCEPTED,
		DECLINED,
		APPLIED,
		DISCARDED,
		DOWNLOAD_FAILED,
		ACTIVATION_FAILED;
	}
}
