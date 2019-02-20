package net.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.ChunkPos;

public class class_4076 extends Vec3i {
	private class_4076(int i, int j, int k) {
		super(i, j, k);
	}

	public static class_4076 method_18676(int i, int j, int k) {
		return new class_4076(i, j, k);
	}

	public static class_4076 method_18682(BlockPos blockPos) {
		return new class_4076(method_18675(blockPos.getX()), method_18675(blockPos.getY()), method_18675(blockPos.getZ()));
	}

	public static class_4076 method_18681(ChunkPos chunkPos, int i) {
		return new class_4076(chunkPos.x, i, chunkPos.z);
	}

	public static class_4076 method_18680(Entity entity) {
		return new class_4076(method_18675(MathHelper.floor(entity.x)), method_18675(MathHelper.floor(entity.y)), method_18675(MathHelper.floor(entity.z)));
	}

	public static class_4076 method_18677(long l) {
		return new class_4076(method_18686(l), method_18689(l), method_18690(l));
	}

	public static long method_18679(long l, Direction direction) {
		return method_18678(l, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
	}

	public static long method_18678(long l, int i, int j, int k) {
		return method_18685(method_18686(l) + i, method_18689(l) + j, method_18690(l) + k);
	}

	public static int method_18675(int i) {
		return i >> 4;
	}

	public static int method_18684(int i) {
		return i & 15;
	}

	public static int method_18688(int i) {
		return i << 4;
	}

	public static int method_18686(long l) {
		return (int)(l << 0 >> 42);
	}

	public static int method_18689(long l) {
		return (int)(l << 44 >> 44);
	}

	public static int method_18690(long l) {
		return (int)(l << 22 >> 42);
	}

	public int method_18674() {
		return this.getX();
	}

	public int method_18683() {
		return this.getY();
	}

	public int method_18687() {
		return this.getZ();
	}

	public static long method_18691(long l) {
		return method_18685(method_18675(BlockPos.unpackLongX(l)), method_18675(BlockPos.unpackLongY(l)), method_18675(BlockPos.unpackLongZ(l)));
	}

	public static long method_18693(long l) {
		return l & -1048576L;
	}

	public ChunkPos method_18692() {
		return new ChunkPos(this.method_18674(), this.method_18687());
	}

	public static long method_18685(int i, int j, int k) {
		long l = 0L;
		l |= ((long)i & 4194303L) << 42;
		l |= ((long)j & 1048575L) << 0;
		return l | ((long)k & 4194303L) << 20;
	}

	public long method_18694() {
		return method_18685(this.method_18674(), this.method_18683(), this.method_18687());
	}
}
