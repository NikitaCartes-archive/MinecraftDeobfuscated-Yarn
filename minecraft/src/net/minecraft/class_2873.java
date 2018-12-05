package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2873 implements Packet<ServerPlayPacketListener> {
	private int field_13071;
	private ItemStack field_13070 = ItemStack.EMPTY;

	public class_2873() {
	}

	@Environment(EnvType.CLIENT)
	public class_2873(int i, ItemStack itemStack) {
		this.field_13071 = i;
		this.field_13070 = itemStack.copy();
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12070(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13071 = packetByteBuf.readShort();
		this.field_13070 = packetByteBuf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeShort(this.field_13071);
		packetByteBuf.writeItemStack(this.field_13070);
	}

	public int method_12481() {
		return this.field_13071;
	}

	public ItemStack method_12479() {
		return this.field_13070;
	}
}
