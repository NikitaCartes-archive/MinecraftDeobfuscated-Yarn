package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityEquipmentUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private EquipmentSlot slot;
	private ItemStack stack = ItemStack.EMPTY;

	public EntityEquipmentUpdateS2CPacket() {
	}

	public EntityEquipmentUpdateS2CPacket(int id, EquipmentSlot slot, ItemStack stack) {
		this.id = id;
		this.slot = slot;
		this.stack = stack.copy();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.slot = buf.readEnumConstant(EquipmentSlot.class);
		this.stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeEnumConstant(this.slot);
		buf.writeItemStack(this.stack);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEquipmentUpdate(this);
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
