package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePoolElement;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
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
		this(ConfiguredFeature.deserialize((Dynamic<T>)dynamic.get("feature").orElseGet(dynamic::emptyMap)));
	}

	public BlockPos method_16601(class_3485 arg, Rotation rotation) {
		return new BlockPos(0, 0, 0);
	}

	@Override
	public List<class_3499.class_3501> method_16627(class_3485 arg, BlockPos blockPos, Rotation rotation, Random random) {
		ArrayList<class_3499.class_3501> arrayList = Lists.newArrayList();
		arrayList.add(new class_3499.class_3501(blockPos, Blocks.field_16540.getDefaultState().with(JigsawBlock.field_10927, Direction.DOWN), this.field_16662));
		return arrayList;
	}

	@Override
	public MutableIntBoundingBox method_16628(class_3485 arg, BlockPos blockPos, Rotation rotation) {
		BlockPos blockPos2 = this.method_16601(arg, rotation);
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
		return this.field_16661.generate(iWorld, (ChunkGenerator<? extends ChunkGeneratorSettings>)chunkGenerator, random, blockPos);
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
