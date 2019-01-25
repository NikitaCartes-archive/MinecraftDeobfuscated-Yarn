package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import net.minecraft.class_3817;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;

public abstract class PoolStructurePiece extends StructurePiece {
	protected final StructurePoolElement poolElement;
	protected BlockPos pos;
	private final int groundLevelDelta;
	protected final Rotation rotation;
	private final List<JigsawJunction> junctions = Lists.<JigsawJunction>newArrayList();
	private final StructureManager structureManager;

	public PoolStructurePiece(
		StructurePieceType structurePieceType,
		StructureManager structureManager,
		StructurePoolElement structurePoolElement,
		BlockPos blockPos,
		int i,
		Rotation rotation
	) {
		super(structurePieceType, 0);
		this.structureManager = structureManager;
		this.poolElement = structurePoolElement;
		this.pos = blockPos;
		this.groundLevelDelta = i;
		this.rotation = rotation;
		this.boundingBox = structurePoolElement.getBoundingBox(structureManager, blockPos, rotation);
	}

	public PoolStructurePiece(StructureManager structureManager, CompoundTag compoundTag, StructurePieceType structurePieceType) {
		super(structurePieceType, compoundTag);
		this.structureManager = structureManager;
		this.pos = new BlockPos(compoundTag.getInt("PosX"), compoundTag.getInt("PosY"), compoundTag.getInt("PosZ"));
		this.groundLevelDelta = compoundTag.getInt("ground_level_delta");
		this.poolElement = class_3817.deserialize(
			new Dynamic<>(NbtOps.INSTANCE, compoundTag.getCompound("pool_element")), Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE
		);
		this.rotation = Rotation.valueOf(compoundTag.getString("rotation"));
		this.boundingBox = this.poolElement.getBoundingBox(structureManager, this.pos, this.rotation);
		ListTag listTag = compoundTag.getList("junctions", 10);
		this.junctions.clear();
		listTag.forEach(tag -> this.junctions.add(JigsawJunction.deserialize(new Dynamic<>(NbtOps.INSTANCE, tag))));
	}

	@Override
	protected void toNbt(CompoundTag compoundTag) {
		compoundTag.putInt("PosX", this.pos.getX());
		compoundTag.putInt("PosY", this.pos.getY());
		compoundTag.putInt("PosZ", this.pos.getZ());
		compoundTag.putInt("ground_level_delta", this.groundLevelDelta);
		compoundTag.put("pool_element", this.poolElement.method_16755(NbtOps.INSTANCE).getValue());
		compoundTag.putString("rotation", this.rotation.name());
		ListTag listTag = new ListTag();

		for (JigsawJunction jigsawJunction : this.junctions) {
			listTag.add(jigsawJunction.serialize(NbtOps.INSTANCE).getValue());
		}

		compoundTag.put("junctions", listTag);
	}

	@Override
	public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		return this.poolElement.generate(this.structureManager, iWorld, this.pos, this.rotation, mutableIntBoundingBox, random);
	}

	@Override
	public void translate(int i, int j, int k) {
		super.translate(i, j, k);
		this.pos = this.pos.add(i, j, k);
	}

	@Override
	public Rotation getRotation() {
		return this.rotation;
	}

	public String toString() {
		return String.format("<%s | %s | %s>", this.getClass().getSimpleName(), this.pos, this.rotation);
	}

	public StructurePoolElement getPoolElement() {
		return this.poolElement;
	}

	public BlockPos getPos() {
		return this.pos;
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
