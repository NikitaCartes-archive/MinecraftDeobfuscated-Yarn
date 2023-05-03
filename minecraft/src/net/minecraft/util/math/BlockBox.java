package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import org.slf4j.Logger;

/**
 * A mutable box with integer coordinates. The box is axis-aligned and the
 * coordinates are inclusive.
 * 
 * <p>This box, though mutable, has proper {@code hashCode} and {@code
 * equals} implementations and can be used as map keys if user can ensure
 * they are not modified.
 * 
 * @see Box
 */
public class BlockBox {
	private static final Logger LOGGER = LogUtils.getLogger();
	/**
	 * A codec that stores a block box as an int array. In the serialized array,
	 * the ordered elements are {@link #minX}, {@link #minY}, {@link #minZ},
	 * {@link #maxX}, {@link #maxY}, {@link #maxZ}.
	 */
	public static final Codec<BlockBox> CODEC = Codec.INT_STREAM
		.<BlockBox>comapFlatMap(
			values -> Util.decodeFixedLengthArray(values, 6).map(array -> new BlockBox(array[0], array[1], array[2], array[3], array[4], array[5])),
			box -> IntStream.of(new int[]{box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ})
		)
		.stable();
	private int minX;
	private int minY;
	private int minZ;
	private int maxX;
	private int maxY;
	private int maxZ;

	/**
	 * Creates a box enclosing only {@code pos}.
	 */
	public BlockBox(BlockPos pos) {
		this(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
	}

	public BlockBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		if (maxX < minX || maxY < minY || maxZ < minZ) {
			String string = "Invalid bounding box data, inverted bounds for: " + this;
			if (SharedConstants.isDevelopment) {
				throw new IllegalStateException(string);
			}

			LOGGER.error(string);
			this.minX = Math.min(minX, maxX);
			this.minY = Math.min(minY, maxY);
			this.minZ = Math.min(minZ, maxZ);
			this.maxX = Math.max(minX, maxX);
			this.maxY = Math.max(minY, maxY);
			this.maxZ = Math.max(minZ, maxZ);
		}
	}

	/**
	 * {@return a new box from two corners, {@code first} and {@code second}}
	 */
	public static BlockBox create(Vec3i first, Vec3i second) {
		return new BlockBox(
			Math.min(first.getX(), second.getX()),
			Math.min(first.getY(), second.getY()),
			Math.min(first.getZ(), second.getZ()),
			Math.max(first.getX(), second.getX()),
			Math.max(first.getY(), second.getY()),
			Math.max(first.getZ(), second.getZ())
		);
	}

	/**
	 * {@return a new all-encompassing, infinite box}
	 */
	public static BlockBox infinite() {
		return new BlockBox(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public static BlockBox rotated(int x, int y, int z, int offsetX, int offsetY, int offsetZ, int sizeX, int sizeY, int sizeZ, Direction facing) {
		switch (facing) {
			case SOUTH:
			default:
				return new BlockBox(x + offsetX, y + offsetY, z + offsetZ, x + sizeX - 1 + offsetX, y + sizeY - 1 + offsetY, z + sizeZ - 1 + offsetZ);
			case NORTH:
				return new BlockBox(x + offsetX, y + offsetY, z - sizeZ + 1 + offsetZ, x + sizeX - 1 + offsetX, y + sizeY - 1 + offsetY, z + offsetZ);
			case WEST:
				return new BlockBox(x - sizeZ + 1 + offsetZ, y + offsetY, z + offsetX, x + offsetZ, y + sizeY - 1 + offsetY, z + sizeX - 1 + offsetX);
			case EAST:
				return new BlockBox(x + offsetZ, y + offsetY, z + offsetX, x + sizeZ - 1 + offsetZ, y + sizeY - 1 + offsetY, z + sizeX - 1 + offsetX);
		}
	}

	/**
	 * {@return whether {@code other} intersects with this box}
	 */
	public boolean intersects(BlockBox other) {
		return this.maxX >= other.minX
			&& this.minX <= other.maxX
			&& this.maxZ >= other.minZ
			&& this.minZ <= other.maxZ
			&& this.maxY >= other.minY
			&& this.minY <= other.maxY;
	}

	/**
	 * {@return whether the rectangle from the given coordinates intersects with this box's XZ plane}
	 */
	public boolean intersectsXZ(int minX, int minZ, int maxX, int maxZ) {
		return this.maxX >= minX && this.minX <= maxX && this.maxZ >= minZ && this.minZ <= maxZ;
	}

	/**
	 * {@return the minimum box encompassing all of the given {@code positions},
	 * or an empty optional if {@code positions} is empty}
	 */
	public static Optional<BlockBox> encompassPositions(Iterable<BlockPos> positions) {
		Iterator<BlockPos> iterator = positions.iterator();
		if (!iterator.hasNext()) {
			return Optional.empty();
		} else {
			BlockBox blockBox = new BlockBox((BlockPos)iterator.next());
			iterator.forEachRemaining(blockBox::encompass);
			return Optional.of(blockBox);
		}
	}

	/**
	 * {@return the minimum box encompassing all of the given {@code boxes},
	 * or an empty optional if {@code boxes} is empty}
	 */
	public static Optional<BlockBox> encompass(Iterable<BlockBox> boxes) {
		Iterator<BlockBox> iterator = boxes.iterator();
		if (!iterator.hasNext()) {
			return Optional.empty();
		} else {
			BlockBox blockBox = (BlockBox)iterator.next();
			BlockBox blockBox2 = new BlockBox(blockBox.minX, blockBox.minY, blockBox.minZ, blockBox.maxX, blockBox.maxY, blockBox.maxZ);
			iterator.forEachRemaining(blockBox2::encompass);
			return Optional.of(blockBox2);
		}
	}

	@Deprecated
	public BlockBox encompass(BlockBox box) {
		this.minX = Math.min(this.minX, box.minX);
		this.minY = Math.min(this.minY, box.minY);
		this.minZ = Math.min(this.minZ, box.minZ);
		this.maxX = Math.max(this.maxX, box.maxX);
		this.maxY = Math.max(this.maxY, box.maxY);
		this.maxZ = Math.max(this.maxZ, box.maxZ);
		return this;
	}

	/**
	 * Expands this box to encompass the {@code pos}.
	 * 
	 * @return this box, for chaining
	 * 
	 * @param pos the pos to encompass
	 */
	@Deprecated
	public BlockBox encompass(BlockPos pos) {
		this.minX = Math.min(this.minX, pos.getX());
		this.minY = Math.min(this.minY, pos.getY());
		this.minZ = Math.min(this.minZ, pos.getZ());
		this.maxX = Math.max(this.maxX, pos.getX());
		this.maxY = Math.max(this.maxY, pos.getY());
		this.maxZ = Math.max(this.maxZ, pos.getZ());
		return this;
	}

	@Deprecated
	public BlockBox move(int dx, int dy, int dz) {
		this.minX += dx;
		this.minY += dy;
		this.minZ += dz;
		this.maxX += dx;
		this.maxY += dy;
		this.maxZ += dz;
		return this;
	}

	@Deprecated
	public BlockBox move(Vec3i vec) {
		return this.move(vec.getX(), vec.getY(), vec.getZ());
	}

	/**
	 * {@return a new box that is translated by {@code x}, {@code y}, {@code z}
	 * on each axis from this box}
	 * 
	 * @see #move(int, int, int)
	 */
	public BlockBox offset(int x, int y, int z) {
		return new BlockBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}

	/**
	 * {@return a new box that is expanded on each direction by {@code offset}}
	 */
	public BlockBox expand(int offset) {
		return new BlockBox(
			this.getMinX() - offset, this.getMinY() - offset, this.getMinZ() - offset, this.getMaxX() + offset, this.getMaxY() + offset, this.getMaxZ() + offset
		);
	}

	/**
	 * {@return whether this box contains {@code pos}}
	 */
	public boolean contains(Vec3i pos) {
		return this.contains(pos.getX(), pos.getY(), pos.getZ());
	}

	public boolean contains(int x, int y, int z) {
		return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ && y >= this.minY && y <= this.maxY;
	}

	/**
	 * {@return the dimensions (the size) of this box}
	 */
	public Vec3i getDimensions() {
		return new Vec3i(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
	}

	/**
	 * {@return the number of blocks on the X axis}
	 * 
	 * <p>This is equal to {@code maxX - minX + 1}.
	 */
	public int getBlockCountX() {
		return this.maxX - this.minX + 1;
	}

	/**
	 * {@return the number of blocks on the Y axis}
	 * 
	 * <p>This is equal to {@code maxY - minY + 1}.
	 */
	public int getBlockCountY() {
		return this.maxY - this.minY + 1;
	}

	/**
	 * {@return the number of blocks on the Z axis}
	 * 
	 * <p>This is equal to {@code maxZ - minZ + 1}.
	 */
	public int getBlockCountZ() {
		return this.maxZ - this.minZ + 1;
	}

	/**
	 * {@return the center of this box}
	 * 
	 * @apiNote This is biased toward the minimum bound corner of the box.
	 */
	public BlockPos getCenter() {
		return new BlockPos(this.minX + (this.maxX - this.minX + 1) / 2, this.minY + (this.maxY - this.minY + 1) / 2, this.minZ + (this.maxZ - this.minZ + 1) / 2);
	}

	/**
	 * Calls {@code consumer} for each vertex (corner) of this box.
	 */
	public void forEachVertex(Consumer<BlockPos> consumer) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		consumer.accept(mutable.set(this.maxX, this.maxY, this.maxZ));
		consumer.accept(mutable.set(this.minX, this.maxY, this.maxZ));
		consumer.accept(mutable.set(this.maxX, this.minY, this.maxZ));
		consumer.accept(mutable.set(this.minX, this.minY, this.maxZ));
		consumer.accept(mutable.set(this.maxX, this.maxY, this.minZ));
		consumer.accept(mutable.set(this.minX, this.maxY, this.minZ));
		consumer.accept(mutable.set(this.maxX, this.minY, this.minZ));
		consumer.accept(mutable.set(this.minX, this.minY, this.minZ));
	}

	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("minX", this.minX)
			.add("minY", this.minY)
			.add("minZ", this.minZ)
			.add("maxX", this.maxX)
			.add("maxY", this.maxY)
			.add("maxZ", this.maxZ)
			.toString();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof BlockBox blockBox)
				? false
				: this.minX == blockBox.minX
					&& this.minY == blockBox.minY
					&& this.minZ == blockBox.minZ
					&& this.maxX == blockBox.maxX
					&& this.maxY == blockBox.maxY
					&& this.maxZ == blockBox.maxZ;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
	}

	public int getMinX() {
		return this.minX;
	}

	public int getMinY() {
		return this.minY;
	}

	public int getMinZ() {
		return this.minZ;
	}

	public int getMaxX() {
		return this.maxX;
	}

	public int getMaxY() {
		return this.maxY;
	}

	public int getMaxZ() {
		return this.maxZ;
	}
}
