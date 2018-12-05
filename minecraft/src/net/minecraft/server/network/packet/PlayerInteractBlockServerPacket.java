package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlayerInteractBlockServerPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private Direction facing;
	private Hand hand;
	private float hitX;
	private float hitY;
	private float hitZ;

	public PlayerInteractBlockServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractBlockServerPacket(BlockPos blockPos, Direction direction, Hand hand, float f, float g, float h) {
		this.pos = blockPos;
		this.facing = direction;
		this.hand = hand;
		this.hitX = f;
		this.hitY = g;
		this.hitZ = h;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.facing = packetByteBuf.readEnumConstant(Direction.class);
		this.hand = packetByteBuf.readEnumConstant(Hand.class);
		this.hitX = packetByteBuf.readFloat();
		this.hitY = packetByteBuf.readFloat();
		this.hitZ = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeEnumConstant(this.facing);
		packetByteBuf.writeEnumConstant(this.hand);
		packetByteBuf.writeFloat(this.hitX);
		packetByteBuf.writeFloat(this.hitY);
		packetByteBuf.writeFloat(this.hitZ);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractBlock(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Direction getFacing() {
		return this.facing;
	}

	public Hand getHand() {
		return this.hand;
	}

	public float getHitX() {
		return this.hitX;
	}

	public float getHitY() {
		return this.hitY;
	}

	public float getHitZ() {
		return this.hitZ;
	}
}
