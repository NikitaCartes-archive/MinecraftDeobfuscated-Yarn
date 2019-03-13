package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class CreativeInventoryActionC2SPacket implements Packet<ServerPlayPacketListener> {
	private int slot;
	private ItemStack stack = ItemStack.EMPTY;

	public CreativeInventoryActionC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public CreativeInventoryActionC2SPacket(int i, ItemStack itemStack) {
		this.slot = i;
		this.stack = itemStack.copy();
	}

	public void method_12480(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12070(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.slot = packetByteBuf.readShort();
		this.stack = packetByteBuf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeShort(this.slot);
		packetByteBuf.writeItemStack(this.stack);
	}

	public int getSlot() {
		return this.slot;
	}

	public ItemStack getItemStack() {
		return this.stack;
	}
}
