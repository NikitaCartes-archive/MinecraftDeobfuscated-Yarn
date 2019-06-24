package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.PacketByteBuf;

public class InventoryS2CPacket implements Packet<ClientPlayPacketListener> {
	private int guiId;
	private List<ItemStack> slotStackList;

	public InventoryS2CPacket() {
	}

	public InventoryS2CPacket(int i, DefaultedList<ItemStack> defaultedList) {
		this.guiId = i;
		this.slotStackList = DefaultedList.<ItemStack>ofSize(defaultedList.size(), ItemStack.EMPTY);

		for (int j = 0; j < this.slotStackList.size(); j++) {
			this.slotStackList.set(j, defaultedList.get(j).copy());
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.guiId = packetByteBuf.readUnsignedByte();
		int i = packetByteBuf.readShort();
		this.slotStackList = DefaultedList.<ItemStack>ofSize(i, ItemStack.EMPTY);

		for (int j = 0; j < i; j++) {
			this.slotStackList.set(j, packetByteBuf.readItemStack());
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.guiId);
		packetByteBuf.writeShort(this.slotStackList.size());

		for (ItemStack itemStack : this.slotStackList) {
			packetByteBuf.writeItemStack(itemStack);
		}
	}

	public void method_11439(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onInventory(this);
	}

	@Environment(EnvType.CLIENT)
	public int getGuiId() {
		return this.guiId;
	}

	@Environment(EnvType.CLIENT)
	public List<ItemStack> getSlotStacks() {
		return this.slotStackList;
	}
}
