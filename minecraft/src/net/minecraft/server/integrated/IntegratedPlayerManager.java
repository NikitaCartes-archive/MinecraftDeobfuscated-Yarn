package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class IntegratedPlayerManager extends PlayerManager {
	private CompoundTag userData;

	public IntegratedPlayerManager(IntegratedServer integratedServer) {
		super(integratedServer, 8);
		this.setViewDistance(10);
	}

	@Override
	protected void savePlayerData(ServerPlayerEntity player) {
		if (player.getName().getString().equals(this.method_4811().getUserName())) {
			this.userData = player.toTag(new CompoundTag());
		}

		super.savePlayerData(player);
	}

	@Override
	public Text checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
		return (Text)(gameProfile.getName().equalsIgnoreCase(this.method_4811().getUserName()) && this.getPlayer(gameProfile.getName()) != null
			? new TranslatableText("multiplayer.disconnect.name_taken")
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
