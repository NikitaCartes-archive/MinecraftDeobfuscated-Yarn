package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.WorldSaveHandler;

@Environment(EnvType.CLIENT)
public class IntegratedPlayerManager extends PlayerManager {
	private NbtCompound userData;

	public IntegratedPlayerManager(IntegratedServer server, DynamicRegistryManager.Impl registryManager, WorldSaveHandler saveHandler) {
		super(server, registryManager, saveHandler, 8);
		this.setViewDistance(10);
	}

	@Override
	protected void savePlayerData(ServerPlayerEntity player) {
		if (player.getName().getString().equals(this.getServer().getUserName())) {
			this.userData = player.writeNbt(new NbtCompound());
		}

		super.savePlayerData(player);
	}

	@Override
	public Text checkCanJoin(SocketAddress address, GameProfile profile) {
		return (Text)(profile.getName().equalsIgnoreCase(this.getServer().getUserName()) && this.getPlayer(profile.getName()) != null
			? new TranslatableText("multiplayer.disconnect.name_taken")
			: super.checkCanJoin(address, profile));
	}

	public IntegratedServer getServer() {
		return (IntegratedServer)super.getServer();
	}

	@Override
	public NbtCompound getUserData() {
		return this.userData;
	}
}
