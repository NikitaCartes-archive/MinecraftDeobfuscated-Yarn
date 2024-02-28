package net.minecraft.network.packet.s2c.play;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record MapUpdateS2CPacket(MapIdComponent mapId, byte scale, boolean locked, Optional<List<MapIcon>> icons, Optional<MapState.UpdateData> updateData)
	implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, MapUpdateS2CPacket> CODEC = PacketCodec.tuple(
		MapIdComponent.PACKET_CODEC,
		MapUpdateS2CPacket::mapId,
		PacketCodecs.BYTE,
		MapUpdateS2CPacket::scale,
		PacketCodecs.BOOL,
		MapUpdateS2CPacket::locked,
		MapIcon.CODEC.collect(PacketCodecs.toList()).collect(PacketCodecs::optional),
		MapUpdateS2CPacket::icons,
		MapState.UpdateData.CODEC,
		MapUpdateS2CPacket::updateData,
		MapUpdateS2CPacket::new
	);

	public MapUpdateS2CPacket(MapIdComponent mapId, byte scale, boolean locked, @Nullable Collection<MapIcon> icons, @Nullable MapState.UpdateData updateData) {
		this(mapId, scale, locked, icons != null ? Optional.of(List.copyOf(icons)) : Optional.empty(), Optional.ofNullable(updateData));
	}

	@Override
	public PacketType<MapUpdateS2CPacket> getPacketId() {
		return PlayPackets.MAP_ITEM_DATA;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onMapUpdate(this);
	}

	public void apply(MapState mapState) {
		this.icons.ifPresent(mapState::replaceIcons);
		this.updateData.ifPresent(updateData -> updateData.setColorsTo(mapState));
	}
}
