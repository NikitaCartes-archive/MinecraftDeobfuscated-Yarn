package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class IntegratedPlayerManager extends PlayerManager {
	private CompoundTag field_5514;

	public IntegratedPlayerManager(IntegratedServer integratedServer) {
		super(integratedServer, 8);
		this.setViewDistance(10, 8);
	}

	@Override
	protected void savePlayerData(ServerPlayerEntity serverPlayerEntity) {
		if (serverPlayerEntity.method_5477().getString().equals(this.method_4811().getUserName())) {
			this.field_5514 = serverPlayerEntity.method_5647(new CompoundTag());
		}

		super.savePlayerData(serverPlayerEntity);
	}

	@Override
	public TextComponent checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
		return (TextComponent)(gameProfile.getName().equalsIgnoreCase(this.method_4811().getUserName()) && this.getPlayer(gameProfile.getName()) != null
			? new TranslatableTextComponent("multiplayer.disconnect.name_taken")
			: super.checkCanJoin(socketAddress, gameProfile));
	}

	public IntegratedServer method_4811() {
		return (IntegratedServer)super.getServer();
	}

	@Override
	public CompoundTag getUserData() {
		return this.field_5514;
	}
}
