package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class BookUpdateC2SPacket implements Packet<ServerPlayPacketListener> {
	private ItemStack stack;
	private boolean field_12864;
	private Hand hand;

	public BookUpdateC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public BookUpdateC2SPacket(ItemStack itemStack, boolean bl, Hand hand) {
		this.stack = itemStack.copy();
		this.field_12864 = bl;
		this.hand = hand;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.stack = packetByteBuf.readItemStack();
		this.field_12864 = packetByteBuf.readBoolean();
		this.hand = packetByteBuf.readEnumConstant(Hand.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeItemStack(this.stack);
		packetByteBuf.writeBoolean(this.field_12864);
		packetByteBuf.writeEnumConstant(this.hand);
	}

	public void method_12236(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12053(this);
	}

	public ItemStack stack() {
		return this.stack;
	}

	public boolean method_12238() {
		return this.field_12864;
	}

	public Hand hand() {
		return this.hand;
	}
}
