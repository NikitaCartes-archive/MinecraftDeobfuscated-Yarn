package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class TitleS2CPacket implements Packet<ClientPlayPacketListener> {
	private TitleS2CPacket.Action action;
	private TextComponent text;
	private int fadeInTicks;
	private int stayTicks;
	private int fadeOutTicks;

	public TitleS2CPacket() {
	}

	public TitleS2CPacket(TitleS2CPacket.Action action, TextComponent textComponent) {
		this(action, textComponent, -1, -1, -1);
	}

	public TitleS2CPacket(int i, int j, int k) {
		this(TitleS2CPacket.Action.DISPLAY, null, i, j, k);
	}

	public TitleS2CPacket(TitleS2CPacket.Action action, @Nullable TextComponent textComponent, int i, int j, int k) {
		this.action = action;
		this.text = textComponent;
		this.fadeInTicks = i;
		this.stayTicks = j;
		this.fadeOutTicks = k;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.action = packetByteBuf.readEnumConstant(TitleS2CPacket.Action.class);
		if (this.action == TitleS2CPacket.Action.field_12630 || this.action == TitleS2CPacket.Action.field_12632 || this.action == TitleS2CPacket.Action.field_12627) {
			this.text = packetByteBuf.readTextComponent();
		}

		if (this.action == TitleS2CPacket.Action.DISPLAY) {
			this.fadeInTicks = packetByteBuf.readInt();
			this.stayTicks = packetByteBuf.readInt();
			this.fadeOutTicks = packetByteBuf.readInt();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.action);
		if (this.action == TitleS2CPacket.Action.field_12630 || this.action == TitleS2CPacket.Action.field_12632 || this.action == TitleS2CPacket.Action.field_12627) {
			packetByteBuf.writeTextComponent(this.text);
		}

		if (this.action == TitleS2CPacket.Action.DISPLAY) {
			packetByteBuf.writeInt(this.fadeInTicks);
			packetByteBuf.writeInt(this.stayTicks);
			packetByteBuf.writeInt(this.fadeOutTicks);
		}
	}

	public void method_11879(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTitle(this);
	}

	@Environment(EnvType.CLIENT)
	public TitleS2CPacket.Action getAction() {
		return this.action;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getText() {
		return this.text;
	}

	@Environment(EnvType.CLIENT)
	public int getFadeInTicks() {
		return this.fadeInTicks;
	}

	@Environment(EnvType.CLIENT)
	public int getStayTicks() {
		return this.stayTicks;
	}

	@Environment(EnvType.CLIENT)
	public int getFadeOutTicks() {
		return this.fadeOutTicks;
	}

	public static enum Action {
		field_12630,
		field_12632,
		field_12627,
		DISPLAY,
		HIDE,
		RESET;
	}
}
