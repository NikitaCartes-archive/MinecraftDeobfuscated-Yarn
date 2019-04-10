package net.minecraft.util.math;

import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.ChunkPos;

public class ChunkSectionPos extends Vec3i {
	private ChunkSectionPos(int i, int j, int k) {
		super(i, j, k);
	}

	public static ChunkSectionPos from(int i, int j, int k) {
		return new ChunkSectionPos(i, j, k);
	}

	public static ChunkSectionPos from(BlockPos blockPos) {
		return new ChunkSectionPos(toChunkCoord(blockPos.getX()), toChunkCoord(blockPos.getY()), toChunkCoord(blockPos.getZ()));
	}

	public static ChunkSectionPos from(ChunkPos chunkPos, int i) {
		return new ChunkSectionPos(chunkPos.x, i, chunkPos.z);
	}

	public static ChunkSectionPos from(Entity entity) {
		return new ChunkSectionPos(toChunkCoord(MathHelper.floor(entity.x)), toChunkCoord(MathHelper.floor(entity.y)), toChunkCoord(MathHelper.floor(entity.z)));
	}

	public static ChunkSectionPos from(long l) {
		return new ChunkSectionPos(unpackLongX(l), unpackLongY(l), unpackLongZ(l));
	}

	public static long offsetPacked(long l, Direction direction) {
		return offsetPacked(l, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
	}

	public static long offsetPacked(long l, int i, int j, int k) {
		return asLong(unpackLongX(l) + i, unpackLongY(l) + j, unpackLongZ(l) + k);
	}

	public static int toChunkCoord(int i) {
		return i >> 4;
	}

	public static int toLocalCoord(int i) {
		return i & 15;
	}

	public static short packToShort(BlockPos blockPos) {
		int i = toLocalCoord(blockPos.getX());
		int j = toLocalCoord(blockPos.getY());
		int k = toLocalCoord(blockPos.getZ());
		return (short)(i << 8 | k << 4 | j);
	}

	public static int fromChunkCoord(int i) {
		return i << 4;
	}

	public static int unpackLongX(long l) {
		return (int)(l << 0 >> 42);
	}

	public static int unpackLongY(long l) {
		return (int)(l << 44 >> 44);
	}

	public static int unpackLongZ(long l) {
		return (int)(l << 22 >> 42);
	}

	public int getChunkX() {
		return this.getX();
	}

	public int getChunkY() {
		return this.getY();
	}

	public int getChunkZ() {
		return this.getZ();
	}

	public int getMinX() {
		return this.getChunkX() << 4;
	}

	public int getMinY() {
		return this.getChunkY() << 4;
	}

	public int getMinZ() {
		return this.getChunkZ() << 4;
	}

	public int getMaxX() {
		return (this.getChunkX() << 4) + 15;
	}

	public int getMaxY() {
		return (this.getChunkY() << 4) + 15;
	}

	public int getMaxZ() {
		return (this.getChunkZ() << 4) + 15;
	}

	public static long toChunkLong(long l) {
		return asLong(toChunkCoord(BlockPos.unpackLongX(l)), toChunkCoord(BlockPos.unpackLongY(l)), toChunkCoord(BlockPos.unpackLongZ(l)));
	}

	public static long method_18693(long l) {
		return l & -1048576L;
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getMinPos() {
		return new BlockPos(fromChunkCoord(this.getChunkX()), fromChunkCoord(this.getChunkY()), fromChunkCoord(this.getChunkZ()));
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getCenterPos() {
		int i = 8;
		return this.getMinPos().add(8, 8, 8);
	}

	public ChunkPos toChunkPos() {
		return new ChunkPos(this.getChunkX(), this.getChunkZ());
	}

	public static long asLong(int i, int j, int k) {
		long l = 0L;
		l |= ((long)i & 4194303L) << 42;
		l |= ((long)j & 1048575L) << 0;
		return l | ((long)k & 4194303L) << 20;
	}

	public long asLong() {
		return asLong(this.getChunkX(), this.getChunkY(), this.getChunkZ());
	}

	public Stream<BlockPos> streamBoxPositions() {
		return BlockPos.streamBoxPositions(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
	}
}
