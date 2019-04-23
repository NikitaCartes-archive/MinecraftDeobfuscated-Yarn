package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Environment(EnvType.CLIENT)
public class IntegratedPlayerManager extends PlayerManager {
	private CompoundTag userData;

	public IntegratedPlayerManager(IntegratedServer integratedServer) {
		super(integratedServer, 8);
		this.setViewDistance(10, 8);
	}

	@Override
	protected void savePlayerData(ServerPlayerEntity serverPlayerEntity) {
		if (serverPlayerEntity.getName().getString().equals(this.method_4811().getUserName())) {
			this.userData = serverPlayerEntity.toTag(new CompoundTag());
		}

		super.savePlayerData(serverPlayerEntity);
	}

	@Override
	public Component checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
		return (Component)(gameProfile.getName().equalsIgnoreCase(this.method_4811().getUserName()) && this.getPlayer(gameProfile.getName()) != null
			? new TranslatableComponent("multiplayer.disconnect.name_taken")
			: super.checkCanJoin(socketAddress, gameProfile));
	}

	public IntegratedServer method_4811() {
		return (IntegratedServer)super.getServer();
	}

	@Override
	public CompoundTag getUserData() {
		return this.userData;
	}
}
