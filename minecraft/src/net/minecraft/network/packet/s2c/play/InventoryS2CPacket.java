package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.collection.DefaultedList;

/**
 * Represents the contents of a block or entity inventory being synchronized
 * from the server to the client.
 */
public class InventoryS2CPacket implements Packet<ClientPlayPacketListener> {
	/**
	 * The {@link net.minecraft.screen.ScreenHandler#syncId} of a screen handler.
	 */
	private int syncId;
	private List<ItemStack> contents;

	public InventoryS2CPacket() {
	}

	public InventoryS2CPacket(int syncId, DefaultedList<ItemStack> contents) {
		this.syncId = syncId;
		this.contents = DefaultedList.<ItemStack>ofSize(contents.size(), ItemStack.EMPTY);

		for (int i = 0; i < this.contents.size(); i++) {
			this.contents.set(i, contents.get(i).copy());
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readUnsignedByte();
		int i = buf.readShort();
		this.contents = DefaultedList.<ItemStack>ofSize(i, ItemStack.EMPTY);

		for (int j = 0; j < i; j++) {
			this.contents.set(j, buf.readItemStack());
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeShort(this.contents.size());

		for (ItemStack itemStack : this.contents) {
			buf.writeItemStack(itemStack);
		}
	}

	public void method_11439(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onInventory(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}

	@Environment(EnvType.CLIENT)
	public List<ItemStack> getContents() {
		return this.contents;
	}
}
