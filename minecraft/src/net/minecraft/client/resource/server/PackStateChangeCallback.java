package net.minecraft.client.resource.server;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface PackStateChangeCallback {
	void onStateChanged(UUID id, PackStateChangeCallback.State state);

	void onFinish(UUID id, PackStateChangeCallback.FinishState state);

	@Environment(EnvType.CLIENT)
	public static enum FinishState {
		DECLINED,
		APPLIED,
		DISCARDED,
		DOWNLOAD_FAILED,
		ACTIVATION_FAILED;
	}

	@Environment(EnvType.CLIENT)
	public static enum State {
		ACCEPTED,
		DOWNLOADED;
	}
}
