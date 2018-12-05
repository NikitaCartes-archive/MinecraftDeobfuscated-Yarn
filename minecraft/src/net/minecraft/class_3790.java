package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.feature.structure.JigsawJunction;

public abstract class class_3790 extends class_3443 {
	protected class_3784 field_16693;
	protected BlockPos field_16695;
	private int field_16692;
	protected Rotation field_16694;
	private final List<JigsawJunction> field_16696 = Lists.<JigsawJunction>newArrayList();

	public class_3790(StructurePiece structurePiece, class_3485 arg, class_3784 arg2, BlockPos blockPos, int i, Rotation rotation) {
		super(structurePiece, 0);
		this.field_16693 = arg2;
		this.field_16695 = blockPos;
		this.field_16692 = i;
		this.field_16694 = rotation;
		this.structureBounds = arg2.method_16628(arg, blockPos, rotation);
	}

	public class_3790(class_3485 arg, CompoundTag compoundTag, StructurePiece structurePiece) {
		super(structurePiece, compoundTag);
		this.field_16695 = new BlockPos(compoundTag.getInt("PosX"), compoundTag.getInt("PosY"), compoundTag.getInt("PosZ"));
		this.field_16692 = compoundTag.getInt("ground_level_delta");
		this.field_16693 = class_3817.deserialize(
			new Dynamic<>(NbtOps.INSTANCE, compoundTag.getCompound("pool_element")), Registry.STRUCTURE_POOL_ELEMENT, "element_type", class_3777.field_16663
		);
		this.field_16694 = Rotation.valueOf(compoundTag.getString("rotation"));
		this.structureBounds = this.field_16693.method_16628(arg, this.field_16695, this.field_16694);
		ListTag listTag = compoundTag.getList("junctions", 10);
		this.field_16696.clear();
		listTag.forEach(tag -> this.field_16696.add(JigsawJunction.deserialize(new Dynamic<>(NbtOps.INSTANCE, tag))));
	}

	@Override
	protected void toNbt(CompoundTag compoundTag) {
		compoundTag.putInt("PosX", this.field_16695.getX());
		compoundTag.putInt("PosY", this.field_16695.getY());
		compoundTag.putInt("PosZ", this.field_16695.getZ());
		compoundTag.putInt("ground_level_delta", this.field_16692);
		compoundTag.put("pool_element", this.field_16693.method_16755(NbtOps.INSTANCE).getValue());
		compoundTag.putString("rotation", this.field_16694.name());
		ListTag listTag = new ListTag();

		for (JigsawJunction jigsawJunction : this.field_16696) {
			listTag.add(jigsawJunction.serialize(NbtOps.INSTANCE).getValue());
		}

		compoundTag.put("junctions", listTag);
	}

	@Override
	public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		return this.field_16693.method_16626(iWorld, this.field_16695, this.field_16694, mutableIntBoundingBox, random);
	}

	@Override
	public void translate(int i, int j, int k) {
		super.translate(i, j, k);
		this.field_16695 = this.field_16695.add(i, j, k);
	}

	@Override
	public Rotation method_16888() {
		return this.field_16694;
	}

	public String toString() {
		return String.format("<%s | %s | %s>", this.getClass().getSimpleName(), this.field_16695, this.field_16694);
	}

	public class_3784 method_16644() {
		return this.field_16693;
	}

	public BlockPos method_16648() {
		return this.field_16695;
	}

	public int method_16646() {
		return this.field_16692;
	}

	public void method_16647(JigsawJunction jigsawJunction) {
		this.field_16696.add(jigsawJunction);
	}

	public List<JigsawJunction> method_16645() {
		return this.field_16696;
	}
}
