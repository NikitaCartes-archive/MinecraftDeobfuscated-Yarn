package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.collection.DefaultedList;

/**
 * Represents the contents of a block or entity inventory being synchronized
 * from the server to the client.
 */
public class InventoryS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, InventoryS2CPacket> CODEC = Packet.createCodec(InventoryS2CPacket::write, InventoryS2CPacket::new);
	/**
	 * The {@link net.minecraft.screen.ScreenHandler#syncId} of a screen handler.
	 */
	private final int syncId;
	private final int revision;
	private final List<ItemStack> contents;
	private final ItemStack cursorStack;

	public InventoryS2CPacket(int syncId, int revision, DefaultedList<ItemStack> contents, ItemStack cursorStack) {
		this.syncId = syncId;
		this.revision = revision;
		this.contents = DefaultedList.<ItemStack>ofSize(contents.size(), ItemStack.EMPTY);

		for (int i = 0; i < contents.size(); i++) {
			this.contents.set(i, contents.get(i).copy());
		}

		this.cursorStack = cursorStack.copy();
	}

	private InventoryS2CPacket(RegistryByteBuf buf) {
		this.syncId = buf.readUnsignedByte();
		this.revision = buf.readVarInt();
		this.contents = ItemStack.OPTIONAL_LIST_PACKET_CODEC.decode(buf);
		this.cursorStack = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
	}

	private void write(RegistryByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeVarInt(this.revision);
		ItemStack.OPTIONAL_LIST_PACKET_CODEC.encode(buf, this.contents);
		ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, this.cursorStack);
	}

	@Override
	public PacketType<InventoryS2CPacket> getPacketId() {
		return PlayPackets.CONTAINER_SET_CONTENT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onInventory(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public List<ItemStack> getContents() {
		return this.contents;
	}

	public ItemStack getCursorStack() {
		return this.cursorStack;
	}

	public int getRevision() {
		return this.revision;
	}
}
