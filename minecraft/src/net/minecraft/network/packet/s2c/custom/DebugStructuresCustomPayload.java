package net.minecraft.network.packet.s2c.custom;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.World;

public record DebugStructuresCustomPayload(RegistryKey<World> dimension, BlockBox mainBB, List<DebugStructuresCustomPayload.Piece> pieces)
	implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugStructuresCustomPayload> CODEC = CustomPayload.codecOf(
		DebugStructuresCustomPayload::write, DebugStructuresCustomPayload::new
	);
	public static final CustomPayload.Id<DebugStructuresCustomPayload> ID = CustomPayload.id("debug/structures");

	private DebugStructuresCustomPayload(PacketByteBuf buf) {
		this(buf.readRegistryKey(RegistryKeys.WORLD), readBox(buf), buf.readList(DebugStructuresCustomPayload.Piece::new));
	}

	private void write(PacketByteBuf buf) {
		buf.writeRegistryKey(this.dimension);
		writeBox(buf, this.mainBB);
		buf.writeCollection(this.pieces, (buf2, piece) -> piece.write(buf));
	}

	@Override
	public CustomPayload.Id<DebugStructuresCustomPayload> getId() {
		return ID;
	}

	static BlockBox readBox(PacketByteBuf buf) {
		return new BlockBox(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
	}

	static void writeBox(PacketByteBuf buf, BlockBox box) {
		buf.writeInt(box.getMinX());
		buf.writeInt(box.getMinY());
		buf.writeInt(box.getMinZ());
		buf.writeInt(box.getMaxX());
		buf.writeInt(box.getMaxY());
		buf.writeInt(box.getMaxZ());
	}

	public static record Piece(BlockBox boundingBox, boolean isStart) {
		public Piece(PacketByteBuf buf) {
			this(DebugStructuresCustomPayload.readBox(buf), buf.readBoolean());
		}

		public void write(PacketByteBuf buf) {
			DebugStructuresCustomPayload.writeBox(buf, this.boundingBox);
			buf.writeBoolean(this.isStart);
		}
	}
}
