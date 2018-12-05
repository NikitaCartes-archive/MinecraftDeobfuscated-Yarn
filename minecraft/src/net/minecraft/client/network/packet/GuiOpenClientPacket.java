package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class GuiOpenClientPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private String type;
	private TextComponent title;
	private int slotCount;
	private int entityHorseId;

	public GuiOpenClientPacket() {
	}

	public GuiOpenClientPacket(int i, String string, TextComponent textComponent) {
		this(i, string, textComponent, 0);
	}

	public GuiOpenClientPacket(int i, String string, TextComponent textComponent, int j) {
		this.id = i;
		this.type = string;
		this.title = textComponent;
		this.slotCount = j;
	}

	public GuiOpenClientPacket(int i, String string, TextComponent textComponent, int j, int k) {
		this(i, string, textComponent, j);
		this.entityHorseId = k;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGuiOpen(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readUnsignedByte();
		this.type = packetByteBuf.readString(32);
		this.title = packetByteBuf.readTextComponent();
		this.slotCount = packetByteBuf.readUnsignedByte();
		if (this.type.equals("EntityHorse")) {
			this.entityHorseId = packetByteBuf.readInt();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.id);
		packetByteBuf.writeString(this.type);
		packetByteBuf.writeTextComponent(this.title);
		packetByteBuf.writeByte(this.slotCount);
		if (this.type.equals("EntityHorse")) {
			packetByteBuf.writeInt(this.entityHorseId);
		}
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public String getType() {
		return this.type;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getTitle() {
		return this.title;
	}

	@Environment(EnvType.CLIENT)
	public int getSlotCount() {
		return this.slotCount;
	}

	@Environment(EnvType.CLIENT)
	public int getHorseId() {
		return this.entityHorseId;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasSlots() {
		return this.slotCount > 0;
	}
}
