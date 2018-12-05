package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class UnlockRecipesClientPacket implements Packet<ClientPlayPacketListener> {
	@Nullable
	private Identifier field_12440;

	public UnlockRecipesClientPacket() {
	}

	public UnlockRecipesClientPacket(@Nullable Identifier identifier) {
		this.field_12440 = identifier;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11161(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		if (packetByteBuf.readBoolean()) {
			this.field_12440 = packetByteBuf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBoolean(this.field_12440 != null);
		if (this.field_12440 != null) {
			packetByteBuf.writeIdentifier(this.field_12440);
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Identifier method_11793() {
		return this.field_12440;
	}
}
