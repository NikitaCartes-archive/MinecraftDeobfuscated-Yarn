package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;

public record ConnectedClientData(GameProfile gameProfile, int latency, SyncedClientOptions clientInformation) {
	public static ConnectedClientData createDefault(GameProfile profile) {
		return new ConnectedClientData(profile, 0, SyncedClientOptions.createDefault());
	}

	public SyncedClientOptions syncedOptions() {
		return this.clientInformation;
	}
}
