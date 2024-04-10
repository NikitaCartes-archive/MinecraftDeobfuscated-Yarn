package net.minecraft.util.math;

import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ImmutableBlockBox(BlockPos min, BlockPos max) implements Iterable<BlockPos> {
	public static final PacketCodec<ByteBuf, ImmutableBlockBox> PACKET_CODEC = new PacketCodec<ByteBuf, ImmutableBlockBox>() {
		public ImmutableBlockBox decode(ByteBuf byteBuf) {
			return new ImmutableBlockBox(PacketByteBuf.readBlockPos(byteBuf), PacketByteBuf.readBlockPos(byteBuf));
		}

		public void encode(ByteBuf byteBuf, ImmutableBlockBox immutableBlockBox) {
			PacketByteBuf.writeBlockPos(byteBuf, immutableBlockBox.min());
			PacketByteBuf.writeBlockPos(byteBuf, immutableBlockBox.max());
		}
	};

	public ImmutableBlockBox(final BlockPos min, final BlockPos max) {
		this.min = BlockPos.min(min, max);
		this.max = BlockPos.max(min, max);
	}

	public static ImmutableBlockBox of(BlockPos pos) {
		return new ImmutableBlockBox(pos, pos);
	}

	public static ImmutableBlockBox of(BlockPos first, BlockPos second) {
		return new ImmutableBlockBox(first, second);
	}

	public ImmutableBlockBox encompass(BlockPos pos) {
		return new ImmutableBlockBox(BlockPos.min(this.min, pos), BlockPos.max(this.max, pos));
	}

	public boolean isSingleBlock() {
		return this.min.equals(this.max);
	}

	public boolean includes(BlockPos pos) {
		return pos.getX() >= this.min.getX()
			&& pos.getY() >= this.min.getY()
			&& pos.getZ() >= this.min.getZ()
			&& pos.getX() <= this.max.getX()
			&& pos.getY() <= this.max.getY()
			&& pos.getZ() <= this.max.getZ();
	}

	public Box enclosingBox() {
		return Box.enclosing(this.min, this.max);
	}

	public Iterator<BlockPos> iterator() {
		return BlockPos.iterate(this.min, this.max).iterator();
	}

	public int getBlockCountX() {
		return this.max.getX() - this.min.getX() + 1;
	}

	public int getBlockCountY() {
		return this.max.getY() - this.min.getY() + 1;
	}

	public int getBlockCountZ() {
		return this.max.getZ() - this.min.getZ() + 1;
	}

	public ImmutableBlockBox expand(Direction direction, int offset) {
		if (offset == 0) {
			return this;
		} else {
			return direction.getDirection() == Direction.AxisDirection.POSITIVE
				? of(this.min, BlockPos.max(this.min, this.max.offset(direction, offset)))
				: of(BlockPos.min(this.min.offset(direction, offset), this.max), this.max);
		}
	}

	public ImmutableBlockBox move(Direction direction, int offset) {
		return offset == 0 ? this : new ImmutableBlockBox(this.min.offset(direction, offset), this.max.offset(direction, offset));
	}

	public ImmutableBlockBox move(Vec3i offset) {
		return new ImmutableBlockBox(this.min.add(offset), this.max.add(offset));
	}
}
