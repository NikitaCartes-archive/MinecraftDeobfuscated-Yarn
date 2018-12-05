package net.minecraft;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2901 implements Packet<ClientLoginPacketListener> {
	private GameProfile field_13190;

	public class_2901() {
	}

	public class_2901(GameProfile gameProfile) {
		this.field_13190 = gameProfile;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		String string = packetByteBuf.readString(36);
		String string2 = packetByteBuf.readString(16);
		UUID uUID = UUID.fromString(string);
		this.field_13190 = new GameProfile(uUID, string2);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		UUID uUID = this.field_13190.getId();
		packetByteBuf.writeString(uUID == null ? "" : uUID.toString());
		packetByteBuf.writeString(this.field_13190.getName());
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.method_12588(this);
	}

	@Environment(EnvType.CLIENT)
	public GameProfile method_12593() {
		return this.field_13190;
	}
}
