package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

	public SelectAdvancementTabS2CPacket(PacketByteBuf packetByteBuf) {
		if (packetByteBuf.readBoolean()) {
			this.tabId = packetByteBuf.readIdentifier();
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
	@Environment(EnvType.CLIENT)
	public Identifier getTabId() {
		return this.tabId;
	}
}
