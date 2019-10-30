package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class MapUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private byte scale;
	private boolean showIcons;
	private boolean locked;
	private MapIcon[] icons;
	private int startX;
	private int startZ;
	private int width;
	private int height;
	private byte[] colors;

	public MapUpdateS2CPacket() {
	}

	public MapUpdateS2CPacket(
		int id, byte scale, boolean showIcons, boolean locked, Collection<MapIcon> icons, byte[] mapColors, int startX, int startZ, int width, int height
	) {
		this.id = id;
		this.scale = scale;
		this.showIcons = showIcons;
		this.locked = locked;
		this.icons = (MapIcon[])icons.toArray(new MapIcon[icons.size()]);
		this.startX = startX;
		this.startZ = startZ;
		this.width = width;
		this.height = height;
		this.colors = new byte[width * height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				this.colors[i + j * width] = mapColors[startX + i + (startZ + j) * 128];
			}
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.scale = buf.readByte();
		this.showIcons = buf.readBoolean();
		this.locked = buf.readBoolean();
		this.icons = new MapIcon[buf.readVarInt()];

		for (int i = 0; i < this.icons.length; i++) {
			MapIcon.Type type = buf.readEnumConstant(MapIcon.Type.class);
			this.icons[i] = new MapIcon(type, buf.readByte(), buf.readByte(), (byte)(buf.readByte() & 15), buf.readBoolean() ? buf.readText() : null);
		}

		this.width = buf.readUnsignedByte();
		if (this.width > 0) {
			this.height = buf.readUnsignedByte();
			this.startX = buf.readUnsignedByte();
			this.startZ = buf.readUnsignedByte();
			this.colors = buf.readByteArray();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeByte(this.scale);
		buf.writeBoolean(this.showIcons);
		buf.writeBoolean(this.locked);
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

		buf.writeByte(this.width);
		if (this.width > 0) {
			buf.writeByte(this.height);
			buf.writeByte(this.startX);
			buf.writeByte(this.startZ);
			buf.writeByteArray(this.colors);
		}
	}

	public void method_11643(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onMapUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public void apply(MapState mapState) {
		mapState.scale = this.scale;
		mapState.showIcons = this.showIcons;
		mapState.locked = this.locked;
		mapState.icons.clear();

		for (int i = 0; i < this.icons.length; i++) {
			MapIcon mapIcon = this.icons[i];
			mapState.icons.put("icon-" + i, mapIcon);
		}

		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				mapState.colors[this.startX + i + (this.startZ + j) * 128] = this.colors[i + j * this.width];
			}
		}
	}
}
