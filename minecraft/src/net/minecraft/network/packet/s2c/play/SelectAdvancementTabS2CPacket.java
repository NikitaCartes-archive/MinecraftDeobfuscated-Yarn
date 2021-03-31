package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;

public class SelectAdvancementTabS2CPacket implements Packet<ClientPlayPacketListener> {
	@Nullable
	private final Identifier tabId;

	public SelectAdvancementTabS2CPacket(@Nullable Identifier tabId) {
		this.tabId = tabId;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSelectAdvancementTab(this);
	}

	public SelectAdvancementTabS2CPacket(PacketByteBuf buf) {
		if (buf.readBoolean()) {
			this.tabId = buf.readIdentifier();
		} else {
			this.tabId = null;
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.tabId != null);
		if (this.tabId != null) {
			buf.writeIdentifier(this.tabId);
		}
	}

	@Nullable
	public Identifier getTabId() {
		return this.tabId;
	}
}
