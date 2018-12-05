package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class class_2871 implements Packet<ServerPlayPacketListener> {
	private int field_13067;
	private String field_13068;
	private boolean field_13066;

	public class_2871() {
	}

	@Environment(EnvType.CLIENT)
	public class_2871(int i, String string, boolean bl) {
		this.field_13067 = i;
		this.field_13068 = string;
		this.field_13066 = bl;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13067 = packetByteBuf.readVarInt();
		this.field_13068 = packetByteBuf.readString(32767);
		this.field_13066 = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_13067);
		packetByteBuf.writeString(this.field_13068);
		packetByteBuf.writeBoolean(this.field_13066);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12049(this);
	}

	@Nullable
	public CommandBlockExecutor method_12476(World world) {
		Entity entity = world.getEntityById(this.field_13067);
		return entity instanceof CommandBlockMinecartEntity ? ((CommandBlockMinecartEntity)entity).getCommandExecutor() : null;
	}

	public String method_12475() {
		return this.field_13068;
	}

	public boolean method_12478() {
		return this.field_13066;
	}
}
