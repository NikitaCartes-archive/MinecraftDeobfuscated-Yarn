package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4463 implements Packet<ClientPlayPacketListener> {
	private static final Logger field_20320 = LogManager.getLogger();
	private BlockPos field_20321;
	private BlockState field_20322;
	PlayerActionC2SPacket.Action field_20319;
	private boolean field_20323;

	public class_4463() {
	}

	public class_4463(BlockPos blockPos, BlockState blockState, PlayerActionC2SPacket.Action action, boolean bl) {
		this.field_20321 = blockPos.toImmutable();
		this.field_20322 = blockState;
		this.field_20319 = action;
		this.field_20323 = bl;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_20321 = packetByteBuf.readBlockPos();
		this.field_20322 = Block.STATE_IDS.get(packetByteBuf.readVarInt());
		this.field_20319 = packetByteBuf.readEnumConstant(PlayerActionC2SPacket.Action.class);
		this.field_20323 = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.field_20321);
		packetByteBuf.writeVarInt(Block.getRawIdFromState(this.field_20322));
		packetByteBuf.writeEnumConstant(this.field_20319);
		packetByteBuf.writeBoolean(this.field_20323);
	}

	public void method_21708(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_21707(this);
	}

	@Environment(EnvType.CLIENT)
	public BlockState method_21709() {
		return this.field_20322;
	}

	@Environment(EnvType.CLIENT)
	public BlockPos method_21710() {
		return this.field_20321;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_21711() {
		return this.field_20323;
	}

	@Environment(EnvType.CLIENT)
	public PlayerActionC2SPacket.Action method_21712() {
		return this.field_20319;
	}
}
