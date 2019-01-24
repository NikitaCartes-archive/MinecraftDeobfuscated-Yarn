package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class TitleClientPacket implements Packet<ClientPlayPacketListener> {
	private TitleClientPacket.Action action;
	private TextComponent text;
	private int ticksFadeIn;
	private int ticksDisplay;
	private int ticksFadeOut;

	public TitleClientPacket() {
	}

	public TitleClientPacket(TitleClientPacket.Action action, TextComponent textComponent) {
		this(action, textComponent, -1, -1, -1);
	}

	public TitleClientPacket(int i, int j, int k) {
		this(TitleClientPacket.Action.DISPLAY, null, i, j, k);
	}

	public TitleClientPacket(TitleClientPacket.Action action, @Nullable TextComponent textComponent, int i, int j, int k) {
		this.action = action;
		this.text = textComponent;
		this.ticksFadeIn = i;
		this.ticksDisplay = j;
		this.ticksFadeOut = k;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.action = packetByteBuf.readEnumConstant(TitleClientPacket.Action.class);
		if (this.action == TitleClientPacket.Action.field_12630
			|| this.action == TitleClientPacket.Action.field_12632
			|| this.action == TitleClientPacket.Action.field_12627) {
			this.text = packetByteBuf.readTextComponent();
		}

		if (this.action == TitleClientPacket.Action.DISPLAY) {
			this.ticksFadeIn = packetByteBuf.readInt();
			this.ticksDisplay = packetByteBuf.readInt();
			this.ticksFadeOut = packetByteBuf.readInt();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.action);
		if (this.action == TitleClientPacket.Action.field_12630
			|| this.action == TitleClientPacket.Action.field_12632
			|| this.action == TitleClientPacket.Action.field_12627) {
			packetByteBuf.writeTextComponent(this.text);
		}

		if (this.action == TitleClientPacket.Action.DISPLAY) {
			packetByteBuf.writeInt(this.ticksFadeIn);
			packetByteBuf.writeInt(this.ticksDisplay);
			packetByteBuf.writeInt(this.ticksFadeOut);
		}
	}

	public void method_11879(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTitle(this);
	}

	@Environment(EnvType.CLIENT)
	public TitleClientPacket.Action getAction() {
		return this.action;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getText() {
		return this.text;
	}

	@Environment(EnvType.CLIENT)
	public int getTicksFadeIn() {
		return this.ticksFadeIn;
	}

	@Environment(EnvType.CLIENT)
	public int getTicksDisplay() {
		return this.ticksDisplay;
	}

	@Environment(EnvType.CLIENT)
	public int getTicksFadeOut() {
		return this.ticksFadeOut;
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
