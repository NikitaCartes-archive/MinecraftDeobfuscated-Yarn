package net.minecraft.server.network;

import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.registry.tag.TagPacketSerializer;

public class SynchronizeRegistriesTask implements ServerPlayerConfigurationTask {
	public static final ServerPlayerConfigurationTask.Key KEY = new ServerPlayerConfigurationTask.Key("synchronize_registries");
	private final List<VersionedIdentifier> knownPacks;
	private final CombinedDynamicRegistries<ServerDynamicRegistryType> registries;

	public SynchronizeRegistriesTask(List<VersionedIdentifier> knownPacks, CombinedDynamicRegistries<ServerDynamicRegistryType> registries) {
		this.knownPacks = knownPacks;
		this.registries = registries;
	}

	@Override
	public void sendPacket(Consumer<Packet<?>> sender) {
		sender.accept(new SelectKnownPacksS2CPacket(this.knownPacks));
	}

	private void syncRegistryAndTags(Consumer<Packet<?>> sender, Set<VersionedIdentifier> commonKnownPacks) {
		DynamicOps<NbtElement> dynamicOps = this.registries.getCombinedRegistryManager().getOps(NbtOps.INSTANCE);
		SerializableRegistries.forEachSyncedRegistry(
			dynamicOps,
			this.registries.getSucceedingRegistryManagers(ServerDynamicRegistryType.WORLDGEN),
			commonKnownPacks,
			(key, entries) -> sender.accept(new DynamicRegistriesS2CPacket(key, entries))
		);
		sender.accept(new SynchronizeTagsS2CPacket(TagPacketSerializer.serializeTags(this.registries)));
	}

	public void onSelectKnownPacks(List<VersionedIdentifier> clientKnownPacks, Consumer<Packet<?>> sender) {
		if (clientKnownPacks.equals(this.knownPacks)) {
			this.syncRegistryAndTags(sender, Set.copyOf(this.knownPacks));
		} else {
			this.syncRegistryAndTags(sender, Set.of());
		}
	}

	@Override
	public ServerPlayerConfigurationTask.Key getKey() {
		return KEY;
	}
}
