package net.minecraft.network.packet.c2s.play;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.screen.slot.SlotActionType;

public class ClickSlotC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, ClickSlotC2SPacket> CODEC = Packet.createCodec(ClickSlotC2SPacket::write, ClickSlotC2SPacket::new);
	private static final int MAX_MODIFIED_STACKS = 128;
	private static final PacketCodec<RegistryByteBuf, Int2ObjectMap<ItemStack>> STACK_MAP_CODEC = PacketCodecs.map(
		Int2ObjectOpenHashMap::new, PacketCodecs.SHORT.xmap(Short::intValue, Integer::shortValue), ItemStack.OPTIONAL_PACKET_CODEC, 128
	);
	private final int syncId;
	private final int revision;
	private final int slot;
	private final int button;
	private final SlotActionType actionType;
	private final ItemStack stack;
	private final Int2ObjectMap<ItemStack> modifiedStacks;

	public ClickSlotC2SPacket(int syncId, int revision, int slot, int button, SlotActionType actionType, ItemStack stack, Int2ObjectMap<ItemStack> modifiedStacks) {
		this.syncId = syncId;
		this.revision = revision;
		this.slot = slot;
		this.button = button;
		this.actionType = actionType;
		this.stack = stack;
		this.modifiedStacks = Int2ObjectMaps.unmodifiable(modifiedStacks);
	}

	private ClickSlotC2SPacket(RegistryByteBuf buf) {
		this.syncId = buf.readByte();
		this.revision = buf.readVarInt();
		this.slot = buf.readShort();
		this.button = buf.readByte();
		this.actionType = buf.readEnumConstant(SlotActionType.class);
		this.modifiedStacks = Int2ObjectMaps.unmodifiable(STACK_MAP_CODEC.decode(buf));
		this.stack = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
	}

	private void write(RegistryByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeVarInt(this.revision);
		buf.writeShort(this.slot);
		buf.writeByte(this.button);
		buf.writeEnumConstant(this.actionType);
		STACK_MAP_CODEC.encode(buf, this.modifiedStacks);
		ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, this.stack);
	}

	@Override
	public PacketType<ClickSlotC2SPacket> getPacketId() {
		return PlayPackets.CONTAINER_CLICK;
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

	public int getRevision() {
		return this.revision;
	}
}
