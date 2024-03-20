package net.minecraft.item.map;

import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

public record MapDecoration(RegistryEntry<MapDecorationType> type, byte x, byte z, byte rotation, Optional<Text> name) {
	public static final PacketCodec<RegistryByteBuf, MapDecoration> CODEC = PacketCodec.tuple(
		MapDecorationType.PACKET_CODEC,
		MapDecoration::type,
		PacketCodecs.BYTE,
		MapDecoration::x,
		PacketCodecs.BYTE,
		MapDecoration::z,
		PacketCodecs.BYTE,
		MapDecoration::rotation,
		TextCodecs.OPTIONAL_PACKET_CODEC,
		MapDecoration::name,
		MapDecoration::new
	);

	public MapDecoration(RegistryEntry<MapDecorationType> type, byte x, byte z, byte rotation, Optional<Text> name) {
		rotation = (byte)(rotation & 15);
		this.type = type;
		this.x = x;
		this.z = z;
		this.rotation = rotation;
		this.name = name;
	}

	public Identifier getAssetId() {
		return this.type.value().assetId();
	}

	public boolean isAlwaysRendered() {
		return this.type.value().showOnItemFrame();
	}
}
