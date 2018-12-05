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

public class MapUpdateClientPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private byte scale;
	private boolean showIcons;
	private MapIcon[] icons;
	private int updateLeft;
	private int updateTop;
	private int updateHeight;
	private int updateWidth;
	private byte[] updateData;

	public MapUpdateClientPacket() {
	}

	public MapUpdateClientPacket(int i, byte b, boolean bl, Collection<MapIcon> collection, byte[] bs, int j, int k, int l, int m) {
		this.id = i;
		this.scale = b;
		this.showIcons = bl;
		this.icons = (MapIcon[])collection.toArray(new MapIcon[collection.size()]);
		this.updateLeft = j;
		this.updateTop = k;
		this.updateHeight = l;
		this.updateWidth = m;
		this.updateData = new byte[l * m];

		for (int n = 0; n < l; n++) {
			for (int o = 0; o < m; o++) {
				this.updateData[n + o * l] = bs[j + n + (k + o) * 128];
			}
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.scale = packetByteBuf.readByte();
		this.showIcons = packetByteBuf.readBoolean();
		this.icons = new MapIcon[packetByteBuf.readVarInt()];

		for (int i = 0; i < this.icons.length; i++) {
			MapIcon.Direction direction = packetByteBuf.readEnumConstant(MapIcon.Direction.class);
			this.icons[i] = new MapIcon(
				direction,
				packetByteBuf.readByte(),
				packetByteBuf.readByte(),
				(byte)(packetByteBuf.readByte() & 15),
				packetByteBuf.readBoolean() ? packetByteBuf.readTextComponent() : null
			);
		}

		this.updateHeight = packetByteBuf.readUnsignedByte();
		if (this.updateHeight > 0) {
			this.updateWidth = packetByteBuf.readUnsignedByte();
			this.updateLeft = packetByteBuf.readUnsignedByte();
			this.updateTop = packetByteBuf.readUnsignedByte();
			this.updateData = packetByteBuf.readByteArray();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeByte(this.scale);
		packetByteBuf.writeBoolean(this.showIcons);
		packetByteBuf.writeVarInt(this.icons.length);

		for (MapIcon mapIcon : this.icons) {
			packetByteBuf.writeEnumConstant(mapIcon.method_93());
			packetByteBuf.writeByte(mapIcon.getX());
			packetByteBuf.writeByte(mapIcon.getZ());
			packetByteBuf.writeByte(mapIcon.getType() & 15);
			if (mapIcon.method_88() != null) {
				packetByteBuf.writeBoolean(true);
				packetByteBuf.writeTextComponent(mapIcon.method_88());
			} else {
				packetByteBuf.writeBoolean(false);
			}
		}

		packetByteBuf.writeByte(this.updateHeight);
		if (this.updateHeight > 0) {
			packetByteBuf.writeByte(this.updateWidth);
			packetByteBuf.writeByte(this.updateLeft);
			packetByteBuf.writeByte(this.updateTop);
			packetByteBuf.writeByteArray(this.updateData);
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
		mapState.scale = this.scale;
		mapState.showIcons = this.showIcons;
		mapState.icons.clear();

		for (int i = 0; i < this.icons.length; i++) {
			MapIcon mapIcon = this.icons[i];
			mapState.icons.put("icon-" + i, mapIcon);
		}

		for (int i = 0; i < this.updateHeight; i++) {
			for (int j = 0; j < this.updateWidth; j++) {
				mapState.colorArray[this.updateLeft + i + (this.updateTop + j) * 128] = this.updateData[i + j * this.updateHeight];
			}
		}
	}
}
