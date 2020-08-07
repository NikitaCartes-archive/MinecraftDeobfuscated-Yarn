package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;

public class SelectAdvancementTabS2CPacket implements Packet<ClientPlayPacketListener> {
	@Nullable
	private Identifier tabId;

	public SelectAdvancementTabS2CPacket() {
	}

	public SelectAdvancementTabS2CPacket(@Nullable Identifier tabId) {
		this.tabId = tabId;
	}

	public void method_11794(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSelectAdvancementTab(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		if (buf.readBoolean()) {
			this.tabId = buf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
