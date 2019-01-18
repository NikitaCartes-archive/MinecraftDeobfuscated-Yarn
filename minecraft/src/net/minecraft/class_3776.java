package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.Structure;
import net.minecraft.sortme.StructurePoolElement;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class class_3776 extends class_3784 {
	private final ConfiguredFeature<?> field_16661;
	private final CompoundTag field_16662;

	public class_3776(ConfiguredFeature<?> configuredFeature) {
		this.field_16661 = configuredFeature;
		this.field_16662 = new CompoundTag();
		this.field_16662.putString("target_pool", "minecraft:empty");
		this.field_16662.putString("attachement_type", "minecraft:bottom");
		this.field_16662.putString("final_state", "minecraft:air");
	}

	public <T> class_3776(Dynamic<T> dynamic) {
		this(ConfiguredFeature.deserialize(dynamic.get("feature").orElseEmptyMap()));
	}

	public BlockPos method_16601(StructureManager structureManager, Rotation rotation) {
		return BlockPos.ORIGIN;
	}

	@Override
	public List<Structure.StructureBlockInfo> method_16627(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random) {
		List<Structure.StructureBlockInfo> list = Lists.<Structure.StructureBlockInfo>newArrayList();
		list.add(new Structure.StructureBlockInfo(blockPos, Blocks.field_16540.getDefaultState().with(JigsawBlock.FACING, Direction.DOWN), this.field_16662));
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
	public boolean method_16626(IWorld iWorld, BlockPos blockPos, Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox, Random random) {
		ChunkGenerator<?> chunkGenerator = iWorld.getChunkManager().getChunkGenerator();
		return this.field_16661.generate(iWorld, (ChunkGenerator<? extends ChunkGeneratorConfig>)chunkGenerator, random, blockPos);
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("feature"), this.field_16661.serialize(dynamicOps).getValue())));
	}

	@Override
	public StructurePoolElement method_16757() {
		return StructurePoolElement.field_16971;
	}
}
