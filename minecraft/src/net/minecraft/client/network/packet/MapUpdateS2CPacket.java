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

	public MapUpdateS2CPacket(int i, byte b, boolean bl, boolean bl2, Collection<MapIcon> collection, byte[] bs, int j, int k, int l, int m) {
		this.id = i;
		this.scale = b;
		this.showIcons = bl;
		this.locked = bl2;
		this.icons = (MapIcon[])collection.toArray(new MapIcon[collection.size()]);
		this.startX = j;
		this.startZ = k;
		this.width = l;
		this.height = m;
		this.colors = new byte[l * m];

		for (int n = 0; n < l; n++) {
			for (int o = 0; o < m; o++) {
				this.colors[n + o * l] = bs[j + n + (k + o) * 128];
			}
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.scale = packetByteBuf.readByte();
		this.showIcons = packetByteBuf.readBoolean();
		this.locked = packetByteBuf.readBoolean();
		this.icons = new MapIcon[packetByteBuf.readVarInt()];

		for (int i = 0; i < this.icons.length; i++) {
			MapIcon.Type type = packetByteBuf.readEnumConstant(MapIcon.Type.class);
			this.icons[i] = new MapIcon(
				type,
				packetByteBuf.readByte(),
				packetByteBuf.readByte(),
				(byte)(packetByteBuf.readByte() & 15),
				packetByteBuf.readBoolean() ? packetByteBuf.method_10808() : null
			);
		}

		this.width = packetByteBuf.readUnsignedByte();
		if (this.width > 0) {
			this.height = packetByteBuf.readUnsignedByte();
			this.startX = packetByteBuf.readUnsignedByte();
			this.startZ = packetByteBuf.readUnsignedByte();
			this.colors = packetByteBuf.readByteArray();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeByte(this.scale);
		packetByteBuf.writeBoolean(this.showIcons);
		packetByteBuf.writeBoolean(this.locked);
		packetByteBuf.writeVarInt(this.icons.length);

		for (MapIcon mapIcon : this.icons) {
			packetByteBuf.writeEnumConstant(mapIcon.getType());
			packetByteBuf.writeByte(mapIcon.getX());
			packetByteBuf.writeByte(mapIcon.getZ());
			packetByteBuf.writeByte(mapIcon.getRotation() & 15);
			if (mapIcon.method_88() != null) {
				packetByteBuf.writeBoolean(true);
				packetByteBuf.method_10805(mapIcon.method_88());
			} else {
				packetByteBuf.writeBoolean(false);
			}
		}

		packetByteBuf.writeByte(this.width);
		if (this.width > 0) {
			packetByteBuf.writeByte(this.height);
			packetByteBuf.writeByte(this.startX);
			packetByteBuf.writeByte(this.startZ);
			packetByteBuf.writeByteArray(this.colors);
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
