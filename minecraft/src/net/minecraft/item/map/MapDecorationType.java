package net.minecraft.item.map;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record MapDecorationType(Identifier assetId, boolean showOnItemFrame, int mapColor, boolean explorationMapElement, boolean trackCount) {
	public static final int NO_MAP_COLOR = -1;
	public static final Codec<RegistryEntry<MapDecorationType>> CODEC = Registries.MAP_DECORATION_TYPE.getEntryCodec();
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<MapDecorationType>> PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.MAP_DECORATION_TYPE);

	public boolean hasMapColor() {
		return this.mapColor != -1;
	}
}
