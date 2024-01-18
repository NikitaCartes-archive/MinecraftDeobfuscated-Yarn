package net.minecraft.network.packet.c2s.play;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class UpdateCommandBlockMinecartC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, UpdateCommandBlockMinecartC2SPacket> CODEC = Packet.createCodec(
		UpdateCommandBlockMinecartC2SPacket::write, UpdateCommandBlockMinecartC2SPacket::new
	);
	private final int entityId;
	private final String command;
	private final boolean trackOutput;

	public UpdateCommandBlockMinecartC2SPacket(int entityId, String command, boolean trackOutput) {
		this.entityId = entityId;
		this.command = command;
		this.trackOutput = trackOutput;
	}

	private UpdateCommandBlockMinecartC2SPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.command = buf.readString();
		this.trackOutput = buf.readBoolean();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeString(this.command);
		buf.writeBoolean(this.trackOutput);
	}

	@Override
	public PacketType<UpdateCommandBlockMinecartC2SPacket> getPacketId() {
		return PlayPackets.SET_COMMAND_MINECART;
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
