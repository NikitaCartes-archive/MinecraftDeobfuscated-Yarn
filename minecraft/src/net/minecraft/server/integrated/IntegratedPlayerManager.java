package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.PlayerSaveHandler;

@Environment(EnvType.CLIENT)
public class IntegratedPlayerManager extends PlayerManager {
	@Nullable
	private NbtCompound userData;

	public IntegratedPlayerManager(IntegratedServer server, CombinedDynamicRegistries<ServerDynamicRegistryType> registryManager, PlayerSaveHandler saveHandler) {
		super(server, registryManager, saveHandler, 8);
		this.setViewDistance(10);
	}

	@Override
	protected void savePlayerData(ServerPlayerEntity player) {
		if (this.getServer().isHost(player.getGameProfile())) {
			this.userData = player.writeNbt(new NbtCompound());
		}

		super.savePlayerData(player);
	}

	@Override
	public Text checkCanJoin(SocketAddress address, GameProfile profile) {
		return (Text)(this.getServer().isHost(profile) && this.getPlayer(profile.getName()) != null
			? Text.translatable("multiplayer.disconnect.name_taken")
			: super.checkCanJoin(address, profile));
	}

	public IntegratedServer getServer() {
		return (IntegratedServer)super.getServer();
	}

	@Nullable
	@Override
	public NbtCompound getUserData() {
		return this.userData;
	}
}
