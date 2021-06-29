package net.minecraft.network.packet.c2s.play;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class BookUpdateC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final int field_34038 = 4;
	private static final int MAX_TITLE_LENGTH = 128;
	private static final int MAX_PAGE_LENGTH = 8192;
	private static final int field_34046 = 200;
	private final int slot;
	private final List<String> pages;
	private final Optional<String> title;

	public BookUpdateC2SPacket(int slot, List<String> pages, Optional<String> title) {
		this.slot = slot;
		this.pages = ImmutableList.copyOf(pages);
		this.title = title;
	}

	public BookUpdateC2SPacket(PacketByteBuf buf) {
		this.slot = buf.readVarInt();
		this.pages = buf.readCollection(PacketByteBuf.method_37453(Lists::newArrayListWithCapacity, 200), bufx -> bufx.readString(8192));
		this.title = buf.readOptional(bufx -> bufx.readString(128));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.slot);
		buf.writeCollection(this.pages, (bufx, page) -> bufx.writeString(page, 8192));
		buf.writeOptional(this.title, (bufx, title) -> bufx.writeString(title, 128));
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onBookUpdate(this);
	}

	public List<String> getPages() {
		return this.pages;
	}

	public Optional<String> getTitle() {
		return this.title;
	}

	public int getSlot() {
		return this.slot;
	}
}
