package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.Collection;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class MapUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private byte scale;
	private boolean locked;
	@Nullable
	private MapIcon[] icons;
	@Nullable
	private MapState.class_5637 field_28016;

	public MapUpdateS2CPacket() {
	}

	public MapUpdateS2CPacket(int id, byte scale, boolean showIcons, @Nullable Collection<MapIcon> collection, @Nullable MapState.class_5637 arg) {
		this.id = id;
		this.scale = scale;
		this.locked = showIcons;
		this.icons = collection != null ? (MapIcon[])collection.toArray(new MapIcon[0]) : null;
		this.field_28016 = arg;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.scale = buf.readByte();
		this.locked = buf.readBoolean();
		if (buf.readBoolean()) {
			this.icons = new MapIcon[buf.readVarInt()];

			for (int i = 0; i < this.icons.length; i++) {
				MapIcon.Type type = buf.readEnumConstant(MapIcon.Type.class);
				this.icons[i] = new MapIcon(type, buf.readByte(), buf.readByte(), (byte)(buf.readByte() & 15), buf.readBoolean() ? buf.readText() : null);
			}
		}

		int i = buf.readUnsignedByte();
		if (i > 0) {
			int j = buf.readUnsignedByte();
			int k = buf.readUnsignedByte();
			int l = buf.readUnsignedByte();
			byte[] bs = buf.readByteArray();
			this.field_28016 = new MapState.class_5637(k, l, i, j, bs);
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeByte(this.scale);
		buf.writeBoolean(this.locked);
		if (this.icons != null) {
			buf.writeBoolean(true);
			buf.writeVarInt(this.icons.length);

			for (MapIcon mapIcon : this.icons) {
				buf.writeEnumConstant(mapIcon.getType());
				buf.writeByte(mapIcon.getX());
				buf.writeByte(mapIcon.getZ());
				buf.writeByte(mapIcon.getRotation() & 15);
				if (mapIcon.getText() != null) {
					buf.writeBoolean(true);
					buf.writeText(mapIcon.getText());
				} else {
					buf.writeBoolean(false);
				}
			}
		} else {
			buf.writeBoolean(false);
		}

		if (this.field_28016 != null) {
			buf.writeByte(this.field_28016.field_27894);
			buf.writeByte(this.field_28016.field_27895);
			buf.writeByte(this.field_28016.field_27892);
			buf.writeByte(this.field_28016.field_27893);
			buf.writeByteArray(this.field_28016.field_27896);
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
			mapState.method_32369(this.icons);
		}

		if (this.field_28016 != null) {
			this.field_28016.method_32380(mapState);
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
