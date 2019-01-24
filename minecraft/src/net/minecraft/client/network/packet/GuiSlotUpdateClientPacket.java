package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GuiSlotUpdateClientPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int slot;
	private ItemStack itemStack = ItemStack.EMPTY;

	public GuiSlotUpdateClientPacket() {
	}

	public GuiSlotUpdateClientPacket(int i, int j, ItemStack itemStack) {
		this.id = i;
		this.slot = j;
		this.itemStack = itemStack.copy();
	}

	public void method_11451(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGuiSlotUpdate(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readByte();
		this.slot = packetByteBuf.readShort();
		this.itemStack = packetByteBuf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.id);
		packetByteBuf.writeShort(this.slot);
		packetByteBuf.writeItemStack(this.itemStack);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public int getSlot() {
		return this.slot;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getItemStack() {
		return this.itemStack;
	}
}
