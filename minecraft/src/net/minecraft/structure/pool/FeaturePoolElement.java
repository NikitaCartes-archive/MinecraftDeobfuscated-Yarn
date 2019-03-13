package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class FeaturePoolElement extends StructurePoolElement {
	private final ConfiguredFeature<?> feature;
	private final CompoundTag field_16662;

	@Deprecated
	public FeaturePoolElement(ConfiguredFeature<?> configuredFeature) {
		this(configuredFeature, StructurePool.Projection.RIGID);
	}

	public FeaturePoolElement(ConfiguredFeature<?> configuredFeature, StructurePool.Projection projection) {
		super(projection);
		this.feature = configuredFeature;
		this.field_16662 = this.method_19299();
	}

	public <T> FeaturePoolElement(Dynamic<T> dynamic) {
		super(dynamic);
		this.feature = ConfiguredFeature.deserialize(dynamic.get("feature").orElseEmptyMap());
		this.field_16662 = this.method_19299();
	}

	public CompoundTag method_19299() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("target_pool", "minecraft:empty");
		compoundTag.putString("attachement_type", "minecraft:bottom");
		compoundTag.putString("final_state", "minecraft:air");
		return compoundTag;
	}

	public BlockPos method_16601(StructureManager structureManager, Rotation rotation) {
		return BlockPos.ORIGIN;
	}

	@Override
	public List<Structure.StructureBlockInfo> method_16627(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random) {
		List<Structure.StructureBlockInfo> list = Lists.<Structure.StructureBlockInfo>newArrayList();
		list.add(new Structure.StructureBlockInfo(blockPos, Blocks.field_16540.method_9564().method_11657(JigsawBlock.field_10927, Direction.DOWN), this.field_16662));
		return list;
	}

	@Override
	public MutableIntBoundingBox method_16628(StructureManager structureManager, BlockPos blockPos, Rotation rotation) {
		BlockPos blockPos2 = this.method_16601(structureManager, rotation);
		return new MutableIntBoundingBox(
			blockPos.getX(),
			blockPos.getY(),
			blockPos.getZ(),
			blockPos.getX() + blockPos2.getX(),
			blockPos.getY() + blockPos2.getY(),
			blockPos.getZ() + blockPos2.getZ()
		);
	}

	@Override
	public boolean method_16626(
		StructureManager structureManager, IWorld iWorld, BlockPos blockPos, Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox, Random random
	) {
		ChunkGenerator<?> chunkGenerator = iWorld.method_8398().getChunkGenerator();
		return this.feature.method_12862(iWorld, (ChunkGenerator<? extends ChunkGeneratorConfig>)chunkGenerator, random, blockPos);
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("feature"), this.feature.serialize(dynamicOps).getValue())));
	}

	@Override
	public StructurePoolElementType method_16757() {
		return StructurePoolElementType.FEATURE_POOL_ELEMENT;
	}

	public String toString() {
		return "Feature[" + Registry.FEATURE.method_10221(this.feature.field_13376) + "]";
	}
}
