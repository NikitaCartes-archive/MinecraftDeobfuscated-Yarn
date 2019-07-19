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
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class FeaturePoolElement extends StructurePoolElement {
	private final ConfiguredFeature<?> feature;
	private final CompoundTag tag;

	@Deprecated
	public FeaturePoolElement(ConfiguredFeature<?> feature) {
		this(feature, StructurePool.Projection.RIGID);
	}

	public FeaturePoolElement(ConfiguredFeature<?> configuredFeature, StructurePool.Projection projection) {
		super(projection);
		this.feature = configuredFeature;
		this.tag = this.method_19299();
	}

	public <T> FeaturePoolElement(Dynamic<T> dynamic) {
		super(dynamic);
		this.feature = ConfiguredFeature.deserialize(dynamic.get("feature").orElseEmptyMap());
		this.tag = this.method_19299();
	}

	public CompoundTag method_19299() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("target_pool", "minecraft:empty");
		compoundTag.putString("attachement_type", "minecraft:bottom");
		compoundTag.putString("final_state", "minecraft:air");
		return compoundTag;
	}

	public BlockPos method_16601(StructureManager structureManager, BlockRotation blockRotation) {
		return BlockPos.ORIGIN;
	}

	@Override
	public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
		List<Structure.StructureBlockInfo> list = Lists.<Structure.StructureBlockInfo>newArrayList();
		list.add(new Structure.StructureBlockInfo(pos, Blocks.JIGSAW.getDefaultState().with(JigsawBlock.FACING, Direction.DOWN), this.tag));
		return list;
	}

	@Override
	public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
		BlockPos blockPos = this.method_16601(structureManager, rotation);
		return new BlockBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + blockPos.getX(), pos.getY() + blockPos.getY(), pos.getZ() + blockPos.getZ());
	}

	@Override
	public boolean generate(StructureManager structureManager, IWorld world, BlockPos pos, BlockRotation rotation, BlockBox boundingBox, Random random) {
		ChunkGenerator<?> chunkGenerator = world.getChunkManager().getChunkGenerator();
		return this.feature.generate(world, (ChunkGenerator<? extends ChunkGeneratorConfig>)chunkGenerator, random, pos);
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("feature"), this.feature.serialize(dynamicOps).getValue())));
	}

	@Override
	public StructurePoolElementType getType() {
		return StructurePoolElementType.FEATURE_POOL_ELEMENT;
	}

	public String toString() {
		return "Feature[" + Registry.FEATURE.getId(this.feature.feature) + "]";
	}
}
