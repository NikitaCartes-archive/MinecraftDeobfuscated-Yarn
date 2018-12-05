package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class class_2770 implements Packet<ClientPlayPacketListener> {
	private Identifier field_12676;
	private SoundCategory field_12677;

	public class_2770() {
	}

	public class_2770(@Nullable Identifier identifier, @Nullable SoundCategory soundCategory) {
		this.field_12676 = identifier;
		this.field_12677 = soundCategory;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		int i = packetByteBuf.readByte();
		if ((i & 1) > 0) {
			this.field_12677 = packetByteBuf.readEnumConstant(SoundCategory.class);
		}

		if ((i & 2) > 0) {
			this.field_12676 = packetByteBuf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		if (this.field_12677 != null) {
			if (this.field_12676 != null) {
				packetByteBuf.writeByte(3);
				packetByteBuf.writeEnumConstant(this.field_12677);
				packetByteBuf.writeIdentifier(this.field_12676);
			} else {
				packetByteBuf.writeByte(1);
				packetByteBuf.writeEnumConstant(this.field_12677);
			}
		} else if (this.field_12676 != null) {
			packetByteBuf.writeByte(2);
			packetByteBuf.writeIdentifier(this.field_12676);
		} else {
			packetByteBuf.writeByte(0);
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Identifier method_11904() {
		return this.field_12676;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public SoundCategory method_11903() {
		return this.field_12677;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11082(this);
	}
}
