package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class MapUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int id;
	private final byte scale;
	private final boolean locked;
	@Nullable
	private final List<MapIcon> icons;
	@Nullable
	private final MapState.UpdateData updateData;

	public MapUpdateS2CPacket(int id, byte scale, boolean showIcons, @Nullable Collection<MapIcon> icons, @Nullable MapState.UpdateData updateData) {
		this.id = id;
		this.scale = scale;
		this.locked = showIcons;
		this.icons = icons != null ? Lists.<MapIcon>newArrayList(icons) : null;
		this.updateData = updateData;
	}

	public MapUpdateS2CPacket(PacketByteBuf packetByteBuf) {
		this.id = packetByteBuf.readVarInt();
		this.scale = packetByteBuf.readByte();
		this.locked = packetByteBuf.readBoolean();
		if (packetByteBuf.readBoolean()) {
			this.icons = packetByteBuf.method_34066(
				packetByteBufx -> {
					MapIcon.Type type = packetByteBufx.readEnumConstant(MapIcon.Type.class);
					return new MapIcon(
						type,
						packetByteBufx.readByte(),
						packetByteBufx.readByte(),
						(byte)(packetByteBufx.readByte() & 15),
						packetByteBufx.readBoolean() ? packetByteBufx.readText() : null
					);
				}
			);
		} else {
			this.icons = null;
		}

		int i = packetByteBuf.readUnsignedByte();
		if (i > 0) {
			int j = packetByteBuf.readUnsignedByte();
			int k = packetByteBuf.readUnsignedByte();
			int l = packetByteBuf.readUnsignedByte();
			byte[] bs = packetByteBuf.readByteArray();
			this.updateData = new MapState.UpdateData(k, l, i, j, bs);
		} else {
			this.updateData = null;
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeByte(this.scale);
		buf.writeBoolean(this.locked);
		if (this.icons != null) {
			buf.writeBoolean(true);
			buf.method_34062(this.icons, (packetByteBuf, mapIcon) -> {
				packetByteBuf.writeEnumConstant(mapIcon.getType());
				packetByteBuf.writeByte(mapIcon.getX());
				packetByteBuf.writeByte(mapIcon.getZ());
				packetByteBuf.writeByte(mapIcon.getRotation() & 15);
				if (mapIcon.getText() != null) {
					packetByteBuf.writeBoolean(true);
					packetByteBuf.writeText(mapIcon.getText());
				} else {
					packetByteBuf.writeBoolean(false);
				}
			});
		} else {
			buf.writeBoolean(false);
		}

		if (this.updateData != null) {
			buf.writeByte(this.updateData.width);
			buf.writeByte(this.updateData.height);
			buf.writeByte(this.updateData.startX);
			buf.writeByte(this.updateData.startZ);
			buf.writeByteArray(this.updateData.colors);
		} else {
			buf.writeByte(0);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onMapUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public void apply(MapState mapState) {
		if (this.icons != null) {
			mapState.replaceIcons(this.icons);
		}

		if (this.updateData != null) {
			this.updateData.setColorsTo(mapState);
		}
	}

	@Environment(EnvType.CLIENT)
	public byte method_32701() {
		return this.scale;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_32702() {
		return this.locked;
	}
}
