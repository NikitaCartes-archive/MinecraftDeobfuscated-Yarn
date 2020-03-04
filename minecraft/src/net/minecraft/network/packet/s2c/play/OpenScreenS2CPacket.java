package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class OpenScreenS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private int screenHandlerId;
	private Text name;

	public OpenScreenS2CPacket() {
	}

	public OpenScreenS2CPacket(int syncId, ScreenHandlerType<?> type, Text name) {
		this.syncId = syncId;
		this.screenHandlerId = Registry.SCREEN_HANDLER.getRawId(type);
		this.name = name;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readVarInt();
		this.screenHandlerId = buf.readVarInt();
		this.name = buf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.syncId);
		buf.writeVarInt(this.screenHandlerId);
		buf.writeText(this.name);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOpenScreen(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public ScreenHandlerType<?> getScreenHandlerType() {
		return Registry.SCREEN_HANDLER.get(this.screenHandlerId);
	}

	@Environment(EnvType.CLIENT)
	public Text getName() {
		return this.name;
	}
}
