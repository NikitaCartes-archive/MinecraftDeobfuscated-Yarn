package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.JigsawOrientation;
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
	private final ConfiguredFeature<?, ?> feature;
	private final CompoundTag tag;

	@Deprecated
	public FeaturePoolElement(ConfiguredFeature<?, ?> feature) {
		this(feature, StructurePool.Projection.RIGID);
	}

	private FeaturePoolElement(ConfiguredFeature<?, ?> configuredFeature, StructurePool.Projection projection) {
		super(projection);
		this.feature = configuredFeature;
		this.tag = this.createDefaultJigsawTag();
	}

	public <T> FeaturePoolElement(Dynamic<T> dynamic) {
		super(dynamic);
		this.feature = ConfiguredFeature.deserialize(dynamic.get("feature").orElseEmptyMap());
		this.tag = this.createDefaultJigsawTag();
	}

	private CompoundTag createDefaultJigsawTag() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("name", "minecraft:bottom");
		compoundTag.putString("final_state", "minecraft:air");
		compoundTag.putString("pool", "minecraft:empty");
		compoundTag.putString("target", "minecraft:empty");
		compoundTag.putString("joint", JigsawBlockEntity.Joint.ROLLABLE.asString());
		return compoundTag;
	}

	public BlockPos getStart(StructureManager structureManager, BlockRotation blockRotation) {
		return BlockPos.ORIGIN;
	}

	@Override
	public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
		List<Structure.StructureBlockInfo> list = Lists.<Structure.StructureBlockInfo>newArrayList();
		list.add(
			new Structure.StructureBlockInfo(
				pos, Blocks.JIGSAW.getDefaultState().with(JigsawBlock.ORIENTATION, JigsawOrientation.byDirections(Direction.DOWN, Direction.SOUTH)), this.tag
			)
		);
		return list;
	}

	@Override
	public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
		BlockPos blockPos = this.getStart(structureManager, rotation);
		return new BlockBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + blockPos.getX(), pos.getY() + blockPos.getY(), pos.getZ() + blockPos.getZ());
	}

	@Override
	public boolean generate(
		StructureManager structureManager,
		IWorld world,
		ChunkGenerator<?> chunkGenerator,
		BlockPos blockPos,
		BlockPos blockPos2,
		BlockRotation blockRotation,
		BlockBox blockBox,
		Random random
	) {
		return this.feature.generate(world, (ChunkGenerator<? extends ChunkGeneratorConfig>)chunkGenerator, random, blockPos);
	}

	@Override
	public <T> Dynamic<T> rawToDynamic(DynamicOps<T> dynamicOps) {
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
