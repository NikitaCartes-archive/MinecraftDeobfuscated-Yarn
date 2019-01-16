package net.minecraft.server.network.packet;

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

public class UpdateCommandBlockMinecartServerPacket implements Packet<ServerPlayPacketListener> {
	private int entityId;
	private String command;
	private boolean trackOutput;

	public UpdateCommandBlockMinecartServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateCommandBlockMinecartServerPacket(int i, String string, boolean bl) {
		this.entityId = i;
		this.command = string;
		this.trackOutput = bl;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityId = packetByteBuf.readVarInt();
		this.command = packetByteBuf.readString(32767);
		this.trackOutput = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeString(this.command);
		packetByteBuf.writeBoolean(this.trackOutput);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateCommandBlockMinecart(this);
	}

	@Nullable
	public CommandBlockExecutor getMinecartCommandExecutor(World world) {
		Entity entity = world.getEntityById(this.entityId);
		return entity instanceof CommandBlockMinecartEntity ? ((CommandBlockMinecartEntity)entity).getCommandExecutor() : null;
	}

	public String getCommand() {
		return this.command;
	}

	public boolean shouldTrackOutput() {
		return this.trackOutput;
	}
}
