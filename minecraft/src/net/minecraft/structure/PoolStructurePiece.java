package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class PoolStructurePiece extends StructurePiece {
	protected final StructurePoolElement poolElement;
	protected BlockPos pos;
	private final int groundLevelDelta;
	protected final BlockRotation rotation;
	private final List<JigsawJunction> junctions = Lists.<JigsawJunction>newArrayList();
	private final StructureManager structureManager;

	public PoolStructurePiece(
		StructurePieceType type,
		StructureManager manager,
		StructurePoolElement poolElement,
		BlockPos pos,
		int groundLevelDelta,
		BlockRotation rotation,
		BlockBox boundingBox
	) {
		super(type, 0);
		this.structureManager = manager;
		this.poolElement = poolElement;
		this.pos = pos;
		this.groundLevelDelta = groundLevelDelta;
		this.rotation = rotation;
		this.boundingBox = boundingBox;
	}

	public PoolStructurePiece(StructureManager manager, CompoundTag tag, StructurePieceType type) {
		super(type, tag);
		this.structureManager = manager;
		this.pos = new BlockPos(tag.getInt("PosX"), tag.getInt("PosY"), tag.getInt("PosZ"));
		this.groundLevelDelta = tag.getInt("ground_level_delta");
		this.poolElement = DynamicDeserializer.deserialize(
			new Dynamic<>(NbtOps.INSTANCE, tag.getCompound("pool_element")), Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE
		);
		this.rotation = BlockRotation.valueOf(tag.getString("rotation"));
		this.boundingBox = this.poolElement.getBoundingBox(manager, this.pos, this.rotation);
		ListTag listTag = tag.getList("junctions", 10);
		this.junctions.clear();
		listTag.forEach(tagx -> this.junctions.add(JigsawJunction.deserialize(new Dynamic<>(NbtOps.INSTANCE, tagx))));
	}

	@Override
	protected void toNbt(CompoundTag tag) {
		tag.putInt("PosX", this.pos.getX());
		tag.putInt("PosY", this.pos.getY());
		tag.putInt("PosZ", this.pos.getZ());
		tag.putInt("ground_level_delta", this.groundLevelDelta);
		tag.put("pool_element", this.poolElement.toDynamic(NbtOps.INSTANCE).getValue());
		tag.putString("rotation", this.rotation.name());
		ListTag listTag = new ListTag();

		for (JigsawJunction jigsawJunction : this.junctions) {
			listTag.add(jigsawJunction.serialize(NbtOps.INSTANCE).getValue());
		}

		tag.put("junctions", listTag);
	}

	@Override
	public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
		return this.poolElement.generate(this.structureManager, world, generator, this.pos, this.rotation, box, random);
	}

	@Override
	public void translate(int x, int y, int z) {
		super.translate(x, y, z);
		this.pos = this.pos.add(x, y, z);
	}

	@Override
	public BlockRotation getRotation() {
		return this.rotation;
	}

	public String toString() {
		return String.format("<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.pos, this.rotation, this.poolElement);
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

	public void addJunction(JigsawJunction junction) {
		this.junctions.add(junction);
	}

	public List<JigsawJunction> getJunctions() {
		return this.junctions;
	}
}
