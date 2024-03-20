package net.minecraft.network.packet.s2c.play;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record MapUpdateS2CPacket(
	MapIdComponent mapId, byte scale, boolean locked, Optional<List<MapDecoration>> decorations, Optional<MapState.UpdateData> updateData
) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, MapUpdateS2CPacket> CODEC = PacketCodec.tuple(
		MapIdComponent.PACKET_CODEC,
		MapUpdateS2CPacket::mapId,
		PacketCodecs.BYTE,
		MapUpdateS2CPacket::scale,
		PacketCodecs.BOOL,
		MapUpdateS2CPacket::locked,
		MapDecoration.CODEC.collect(PacketCodecs.toList()).collect(PacketCodecs::optional),
		MapUpdateS2CPacket::decorations,
		MapState.UpdateData.CODEC,
		MapUpdateS2CPacket::updateData,
		MapUpdateS2CPacket::new
	);

	public MapUpdateS2CPacket(
		MapIdComponent mapId, byte scale, boolean locked, @Nullable Collection<MapDecoration> decorations, @Nullable MapState.UpdateData updateData
	) {
		this(mapId, scale, locked, decorations != null ? Optional.of(List.copyOf(decorations)) : Optional.empty(), Optional.ofNullable(updateData));
	}

	@Override
	public PacketType<MapUpdateS2CPacket> getPacketId() {
		return PlayPackets.MAP_ITEM_DATA;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onMapUpdate(this);
	}

	public void apply(MapState mapState) {
		this.decorations.ifPresent(mapState::replaceDecorations);
		this.updateData.ifPresent(updateData -> updateData.setColorsTo(mapState));
	}
}
