package net.minecraft.network.packet.c2s.play;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.screen.slot.SlotActionType;

public class ClickSlotC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int syncId;
	private final int slot;
	private final int button;
	private final SlotActionType actionType;
	private final ItemStack stack;
	private final Int2ObjectMap<ItemStack> modifiedStacks;

	public ClickSlotC2SPacket(int syncId, int slot, int button, SlotActionType actionType, ItemStack stack, Int2ObjectMap<ItemStack> modifiedStacks) {
		this.syncId = syncId;
		this.slot = slot;
		this.button = button;
		this.actionType = actionType;
		this.stack = stack;
		this.modifiedStacks = Int2ObjectMaps.unmodifiable(modifiedStacks);
	}

	public ClickSlotC2SPacket(PacketByteBuf buf) {
		this.syncId = buf.readByte();
		this.slot = buf.readShort();
		this.button = buf.readByte();
		this.actionType = buf.readEnumConstant(SlotActionType.class);
		this.modifiedStacks = Int2ObjectMaps.unmodifiable(
			buf.readMap(Int2ObjectOpenHashMap::new, packetByteBuf -> Integer.valueOf(packetByteBuf.readShort()), PacketByteBuf::readItemStack)
		);
		this.stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeShort(this.slot);
		buf.writeByte(this.button);
		buf.writeEnumConstant(this.actionType);
		buf.writeMap(this.modifiedStacks, PacketByteBuf::writeShort, PacketByteBuf::writeItemStack);
		buf.writeItemStack(this.stack);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClickSlot(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getSlot() {
		return this.slot;
	}

	public int getButton() {
		return this.button;
	}

	public ItemStack getStack() {
		return this.stack;
	}

	public Int2ObjectMap<ItemStack> getModifiedStacks() {
		return this.modifiedStacks;
	}

	public SlotActionType getActionType() {
		return this.actionType;
	}
}
