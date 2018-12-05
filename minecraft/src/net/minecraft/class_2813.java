package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.ActionTypeSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2813 implements Packet<ServerPlayPacketListener> {
	private int field_12819;
	private int field_12818;
	private int field_12817;
	private short field_12820;
	private ItemStack field_12816 = ItemStack.EMPTY;
	private ActionTypeSlot field_12815;

	public class_2813() {
	}

	@Environment(EnvType.CLIENT)
	public class_2813(int i, int j, int k, ActionTypeSlot actionTypeSlot, ItemStack itemStack, short s) {
		this.field_12819 = i;
		this.field_12818 = j;
		this.field_12817 = k;
		this.field_12816 = itemStack.copy();
		this.field_12820 = s;
		this.field_12815 = actionTypeSlot;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12076(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12819 = packetByteBuf.readByte();
		this.field_12818 = packetByteBuf.readShort();
		this.field_12817 = packetByteBuf.readByte();
		this.field_12820 = packetByteBuf.readShort();
		this.field_12815 = packetByteBuf.readEnumConstant(ActionTypeSlot.class);
		this.field_12816 = packetByteBuf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.field_12819);
		packetByteBuf.writeShort(this.field_12818);
		packetByteBuf.writeByte(this.field_12817);
		packetByteBuf.writeShort(this.field_12820);
		packetByteBuf.writeEnumConstant(this.field_12815);
		packetByteBuf.writeItemStack(this.field_12816);
	}

	public int method_12194() {
		return this.field_12819;
	}

	public int method_12192() {
		return this.field_12818;
	}

	public int method_12193() {
		return this.field_12817;
	}

	public short method_12189() {
		return this.field_12820;
	}

	public ItemStack method_12190() {
		return this.field_12816;
	}

	public ActionTypeSlot method_12195() {
		return this.field_12815;
	}
}
