package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class EntityEquipmentUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private static final byte field_33342 = -128;
	private final int id;
	private final List<Pair<EquipmentSlot, ItemStack>> equipmentList;

	public EntityEquipmentUpdateS2CPacket(int id, List<Pair<EquipmentSlot, ItemStack>> equipmentList) {
		this.id = id;
		this.equipmentList = equipmentList;
	}

	public EntityEquipmentUpdateS2CPacket(PacketByteBuf buf) {
		this.id = buf.readVarInt();
		EquipmentSlot[] equipmentSlots = EquipmentSlot.values();
		this.equipmentList = Lists.<Pair<EquipmentSlot, ItemStack>>newArrayList();

		int i;
		do {
			i = buf.readByte();
			EquipmentSlot equipmentSlot = equipmentSlots[i & 127];
			ItemStack itemStack = buf.readItemStack();
			this.equipmentList.add(Pair.of(equipmentSlot, itemStack));
		} while ((i & -128) != 0);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		int i = this.equipmentList.size();

		for (int j = 0; j < i; j++) {
			Pair<EquipmentSlot, ItemStack> pair = (Pair<EquipmentSlot, ItemStack>)this.equipmentList.get(j);
			EquipmentSlot equipmentSlot = pair.getFirst();
			boolean bl = j != i - 1;
			int k = equipmentSlot.ordinal();
			buf.writeByte(bl ? k | -128 : k);
			buf.writeItemStack(pair.getSecond());
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityEquipmentUpdate(this);
	}

	public int getId() {
		return this.id;
	}

	public List<Pair<EquipmentSlot, ItemStack>> getEquipmentList() {
		return this.equipmentList;
	}
}
