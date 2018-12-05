package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class class_2870 implements Packet<ServerPlayPacketListener> {
	private BlockPos field_13065;
	private String field_13064;
	private boolean field_13063;
	private boolean field_13062;
	private boolean field_13061;
	private CommandBlockBlockEntity.Type field_13060;

	public class_2870() {
	}

	@Environment(EnvType.CLIENT)
	public class_2870(BlockPos blockPos, String string, CommandBlockBlockEntity.Type type, boolean bl, boolean bl2, boolean bl3) {
		this.field_13065 = blockPos;
		this.field_13064 = string;
		this.field_13063 = bl;
		this.field_13062 = bl2;
		this.field_13061 = bl3;
		this.field_13060 = type;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13065 = packetByteBuf.readBlockPos();
		this.field_13064 = packetByteBuf.readString(32767);
		this.field_13060 = packetByteBuf.readEnumConstant(CommandBlockBlockEntity.Type.class);
		int i = packetByteBuf.readByte();
		this.field_13063 = (i & 1) != 0;
		this.field_13062 = (i & 2) != 0;
		this.field_13061 = (i & 4) != 0;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.field_13065);
		packetByteBuf.writeString(this.field_13064);
		packetByteBuf.writeEnumConstant(this.field_13060);
		int i = 0;
		if (this.field_13063) {
			i |= 1;
		}

		if (this.field_13062) {
			i |= 2;
		}

		if (this.field_13061) {
			i |= 4;
		}

		packetByteBuf.writeByte(i);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12077(this);
	}

	public BlockPos getBlockPos() {
		return this.field_13065;
	}

	public String getCommand() {
		return this.field_13064;
	}

	public boolean shouldTrackOutput() {
		return this.field_13063;
	}

	public boolean method_12471() {
		return this.field_13062;
	}

	public boolean method_12474() {
		return this.field_13061;
	}

	public CommandBlockBlockEntity.Type method_12468() {
		return this.field_13060;
	}
}
