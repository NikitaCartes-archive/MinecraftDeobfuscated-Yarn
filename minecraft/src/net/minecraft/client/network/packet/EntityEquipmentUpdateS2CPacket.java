package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityEquipmentUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private EquipmentSlot slot;
	private ItemStack stack = ItemStack.EMPTY;

	public EntityEquipmentUpdateS2CPacket() {
	}

	public EntityEquipmentUpdateS2CPacket(int i, EquipmentSlot equipmentSlot, ItemStack itemStack) {
		this.id = i;
		this.slot = equipmentSlot;
		this.stack = itemStack.copy();
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.slot = packetByteBuf.readEnumConstant(EquipmentSlot.class);
		this.stack = packetByteBuf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeEnumConstant(this.slot);
		packetByteBuf.writeItemStack(this.stack);
	}

	public void method_11823(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11151(this);
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getStack() {
		return this.stack;
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public EquipmentSlot getSlot() {
		return this.slot;
	}
}
