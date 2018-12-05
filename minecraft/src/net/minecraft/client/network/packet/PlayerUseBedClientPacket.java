package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerUseBedClientPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private BlockPos bedHeadPos;

	public PlayerUseBedClientPacket() {
	}

	public PlayerUseBedClientPacket(PlayerEntity playerEntity, BlockPos blockPos) {
		this.entityId = playerEntity.getEntityId();
		this.bedHeadPos = blockPos;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityId = packetByteBuf.readVarInt();
		this.bedHeadPos = packetByteBuf.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeBlockPos(this.bedHeadPos);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerUseBed(this);
	}

	@Environment(EnvType.CLIENT)
	public PlayerEntity getPlayer(World world) {
		return (PlayerEntity)world.getEntityById(this.entityId);
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getBedHeadPos() {
		return this.bedHeadPos;
	}
}
