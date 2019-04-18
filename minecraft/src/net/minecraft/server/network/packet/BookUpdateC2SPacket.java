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
	private ItemStack book;
	private boolean signed;
	private Hand hand;

	public BookUpdateC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public BookUpdateC2SPacket(ItemStack itemStack, boolean bl, Hand hand) {
		this.book = itemStack.copy();
		this.signed = bl;
		this.hand = hand;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.book = packetByteBuf.readItemStack();
		this.signed = packetByteBuf.readBoolean();
		this.hand = packetByteBuf.readEnumConstant(Hand.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeItemStack(this.book);
		packetByteBuf.writeBoolean(this.signed);
		packetByteBuf.writeEnumConstant(this.hand);
	}

	public void method_12236(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onBookUpdate(this);
	}

	public ItemStack getBook() {
		return this.book;
	}

	public boolean wasSigned() {
		return this.signed;
	}

	public Hand getHand() {
		return this.hand;
	}
}
