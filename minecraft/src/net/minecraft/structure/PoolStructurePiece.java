package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;

public abstract class PoolStructurePiece extends StructurePiece {
	protected final StructurePoolElement poolElement;
	protected BlockPos field_16695;
	private final int groundLevelDelta;
	protected final Rotation rotation;
	private final List<JigsawJunction> junctions = Lists.<JigsawJunction>newArrayList();
	private final StructureManager field_17660;

	public PoolStructurePiece(
		StructurePieceType structurePieceType,
		StructureManager structureManager,
		StructurePoolElement structurePoolElement,
		BlockPos blockPos,
		int i,
		Rotation rotation,
		MutableIntBoundingBox mutableIntBoundingBox
	) {
		super(structurePieceType, 0);
		this.field_17660 = structureManager;
		this.poolElement = structurePoolElement;
		this.field_16695 = blockPos;
		this.groundLevelDelta = i;
		this.rotation = rotation;
		this.boundingBox = mutableIntBoundingBox;
	}

	public PoolStructurePiece(StructureManager structureManager, CompoundTag compoundTag, StructurePieceType structurePieceType) {
		super(structurePieceType, compoundTag);
		this.field_17660 = structureManager;
		this.field_16695 = new BlockPos(compoundTag.getInt("PosX"), compoundTag.getInt("PosY"), compoundTag.getInt("PosZ"));
		this.groundLevelDelta = compoundTag.getInt("ground_level_delta");
		this.poolElement = DynamicDeserializer.deserialize(
			new Dynamic<>(NbtOps.INSTANCE, compoundTag.getCompound("pool_element")), Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE
		);
		this.rotation = Rotation.valueOf(compoundTag.getString("rotation"));
		this.boundingBox = this.poolElement.method_16628(structureManager, this.field_16695, this.rotation);
		ListTag listTag = compoundTag.method_10554("junctions", 10);
		this.junctions.clear();
		listTag.forEach(tag -> this.junctions.add(JigsawJunction.deserialize(new Dynamic<>(NbtOps.INSTANCE, tag))));
	}

	@Override
	protected void method_14943(CompoundTag compoundTag) {
		compoundTag.putInt("PosX", this.field_16695.getX());
		compoundTag.putInt("PosY", this.field_16695.getY());
		compoundTag.putInt("PosZ", this.field_16695.getZ());
		compoundTag.putInt("ground_level_delta", this.groundLevelDelta);
		compoundTag.method_10566("pool_element", this.poolElement.method_16755(NbtOps.INSTANCE).getValue());
		compoundTag.putString("rotation", this.rotation.name());
		ListTag listTag = new ListTag();

		for (JigsawJunction jigsawJunction : this.junctions) {
			listTag.add(jigsawJunction.serialize(NbtOps.INSTANCE).getValue());
		}

		compoundTag.method_10566("junctions", listTag);
	}

	@Override
	public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		return this.poolElement.method_16626(this.field_17660, iWorld, this.field_16695, this.rotation, mutableIntBoundingBox, random);
	}

	@Override
	public void translate(int i, int j, int k) {
		super.translate(i, j, k);
		this.field_16695 = this.field_16695.add(i, j, k);
	}

	@Override
	public Rotation getRotation() {
		return this.rotation;
	}

	public String toString() {
		return String.format("<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.field_16695, this.rotation, this.poolElement);
	}

	public StructurePoolElement getPoolElement() {
		return this.poolElement;
	}

	public BlockPos method_16648() {
		return this.field_16695;
	}

	public int getGroundLevelDelta() {
		return this.groundLevelDelta;
	}

	public void addJunction(JigsawJunction jigsawJunction) {
		this.junctions.add(jigsawJunction);
	}

	public List<JigsawJunction> getJunctions() {
		return this.junctions;
	}
}
