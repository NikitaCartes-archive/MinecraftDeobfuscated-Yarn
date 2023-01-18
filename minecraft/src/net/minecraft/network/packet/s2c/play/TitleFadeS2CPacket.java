package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class TitleFadeS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int fadeInTicks;
	private final int stayTicks;
	private final int fadeOutTicks;

	public TitleFadeS2CPacket(int fadeInTicks, int stayTicks, int fadeOutTicks) {
		this.fadeInTicks = fadeInTicks;
		this.stayTicks = stayTicks;
		this.fadeOutTicks = fadeOutTicks;
	}

	public TitleFadeS2CPacket(PacketByteBuf buf) {
		this.fadeInTicks = buf.readInt();
		this.stayTicks = buf.readInt();
		this.fadeOutTicks = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.fadeInTicks);
		buf.writeInt(this.stayTicks);
		buf.writeInt(this.fadeOutTicks);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTitleFade(this);
	}

	public int getFadeInTicks() {
		return this.fadeInTicks;
	}

	public int getStayTicks() {
		return this.stayTicks;
	}

	public int getFadeOutTicks() {
		return this.fadeOutTicks;
	}
}
