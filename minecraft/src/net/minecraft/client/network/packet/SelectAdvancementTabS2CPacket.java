package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class SelectAdvancementTabS2CPacket implements Packet<ClientPlayPacketListener> {
	@Nullable
	private Identifier tabId;

	public SelectAdvancementTabS2CPacket() {
	}

	public SelectAdvancementTabS2CPacket(@Nullable Identifier identifier) {
		this.tabId = identifier;
	}

	public void method_11794(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11161(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		if (packetByteBuf.readBoolean()) {
			this.tabId = packetByteBuf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBoolean(this.tabId != null);
		if (this.tabId != null) {
			packetByteBuf.writeIdentifier(this.tabId);
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Identifier getTabId() {
		return this.tabId;
	}
}
