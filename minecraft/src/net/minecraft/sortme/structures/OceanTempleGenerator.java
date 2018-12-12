package net.minecraft.sortme.structures;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.class_3443;
import net.minecraft.class_3470;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.sortme.structures.processor.BlockIgnoreStructureProcessor;
import net.minecraft.sortme.structures.processor.BlockRotStructureProcessor;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.loot.LootTables;

public class OceanTempleGenerator {
	private static final Identifier[] field_14521 = new Identifier[]{
		new Identifier("underwater_ruin/warm_1"),
		new Identifier("underwater_ruin/warm_2"),
		new Identifier("underwater_ruin/warm_3"),
		new Identifier("underwater_ruin/warm_4"),
		new Identifier("underwater_ruin/warm_5"),
		new Identifier("underwater_ruin/warm_6"),
		new Identifier("underwater_ruin/warm_7"),
		new Identifier("underwater_ruin/warm_8")
	};
	private static final Identifier[] field_14518 = new Identifier[]{
		new Identifier("underwater_ruin/brick_1"),
		new Identifier("underwater_ruin/brick_2"),
		new Identifier("underwater_ruin/brick_3"),
		new Identifier("underwater_ruin/brick_4"),
		new Identifier("underwater_ruin/brick_5"),
		new Identifier("underwater_ruin/brick_6"),
		new Identifier("underwater_ruin/brick_7"),
		new Identifier("underwater_ruin/brick_8")
	};
	private static final Identifier[] field_14519 = new Identifier[]{
		new Identifier("underwater_ruin/cracked_1"),
		new Identifier("underwater_ruin/cracked_2"),
		new Identifier("underwater_ruin/cracked_3"),
		new Identifier("underwater_ruin/cracked_4"),
		new Identifier("underwater_ruin/cracked_5"),
		new Identifier("underwater_ruin/cracked_6"),
		new Identifier("underwater_ruin/cracked_7"),
		new Identifier("underwater_ruin/cracked_8")
	};
	private static final Identifier[] field_14522 = new Identifier[]{
		new Identifier("underwater_ruin/mossy_1"),
		new Identifier("underwater_ruin/mossy_2"),
		new Identifier("underwater_ruin/mossy_3"),
		new Identifier("underwater_ruin/mossy_4"),
		new Identifier("underwater_ruin/mossy_5"),
		new Identifier("underwater_ruin/mossy_6"),
		new Identifier("underwater_ruin/mossy_7"),
		new Identifier("underwater_ruin/mossy_8")
	};
	private static final Identifier[] field_14516 = new Identifier[]{
		new Identifier("underwater_ruin/big_brick_1"),
		new Identifier("underwater_ruin/big_brick_2"),
		new Identifier("underwater_ruin/big_brick_3"),
		new Identifier("underwater_ruin/big_brick_8")
	};
	private static final Identifier[] field_14517 = new Identifier[]{
		new Identifier("underwater_ruin/big_mossy_1"),
		new Identifier("underwater_ruin/big_mossy_2"),
		new Identifier("underwater_ruin/big_mossy_3"),
		new Identifier("underwater_ruin/big_mossy_8")
	};
	private static final Identifier[] field_14520 = new Identifier[]{
		new Identifier("underwater_ruin/big_cracked_1"),
		new Identifier("underwater_ruin/big_cracked_2"),
		new Identifier("underwater_ruin/big_cracked_3"),
		new Identifier("underwater_ruin/big_cracked_8")
	};
	private static final Identifier[] field_14515 = new Identifier[]{
		new Identifier("underwater_ruin/big_warm_4"),
		new Identifier("underwater_ruin/big_warm_5"),
		new Identifier("underwater_ruin/big_warm_6"),
		new Identifier("underwater_ruin/big_warm_7")
	};

	private static Identifier method_14824(Random random) {
		return field_14521[random.nextInt(field_14521.length)];
	}

	private static Identifier method_14826(Random random) {
		return field_14515[random.nextInt(field_14515.length)];
	}

	public static void method_14827(
		StructureManager structureManager, BlockPos blockPos, Rotation rotation, List<class_3443> list, Random random, OceanRuinFeatureConfig oceanRuinFeatureConfig
	) {
		boolean bl = random.nextFloat() <= oceanRuinFeatureConfig.largeProbability;
		float f = bl ? 0.9F : 0.8F;
		method_14822(structureManager, blockPos, rotation, list, random, oceanRuinFeatureConfig, bl, f);
		if (bl && random.nextFloat() <= oceanRuinFeatureConfig.clusterProbability) {
			method_14825(structureManager, random, rotation, blockPos, oceanRuinFeatureConfig, list);
		}
	}

	private static void method_14825(
		StructureManager structureManager, Random random, Rotation rotation, BlockPos blockPos, OceanRuinFeatureConfig oceanRuinFeatureConfig, List<class_3443> list
	) {
		int i = blockPos.getX();
		int j = blockPos.getZ();
		BlockPos blockPos2 = class_3499.method_15168(new BlockPos(15, 0, 15), Mirror.NONE, rotation, new BlockPos(0, 0, 0)).add(i, 0, j);
		MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.create(i, 0, j, blockPos2.getX(), 0, blockPos2.getZ());
		BlockPos blockPos3 = new BlockPos(Math.min(i, blockPos2.getX()), 0, Math.min(j, blockPos2.getZ()));
		List<BlockPos> list2 = method_14821(random, blockPos3.getX(), blockPos3.getZ());
		int k = MathHelper.nextInt(random, 4, 8);

		for (int l = 0; l < k; l++) {
			if (!list2.isEmpty()) {
				int m = random.nextInt(list2.size());
				BlockPos blockPos4 = (BlockPos)list2.remove(m);
				int n = blockPos4.getX();
				int o = blockPos4.getZ();
				Rotation rotation2 = Rotation.values()[random.nextInt(Rotation.values().length)];
				BlockPos blockPos5 = class_3499.method_15168(new BlockPos(5, 0, 6), Mirror.NONE, rotation2, new BlockPos(0, 0, 0)).add(n, 0, o);
				MutableIntBoundingBox mutableIntBoundingBox2 = MutableIntBoundingBox.create(n, 0, o, blockPos5.getX(), 0, blockPos5.getZ());
				if (!mutableIntBoundingBox2.intersects(mutableIntBoundingBox)) {
					method_14822(structureManager, blockPos4, rotation2, list, random, oceanRuinFeatureConfig, false, 0.8F);
				}
			}
		}
	}

	private static List<BlockPos> method_14821(Random random, int i, int j) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
		list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j + 16 + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j - 16 + MathHelper.nextInt(random, 4, 8)));
		list.add(new BlockPos(i + MathHelper.nextInt(random, 1, 7), 90, j + 16 + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i + MathHelper.nextInt(random, 1, 7), 90, j - 16 + MathHelper.nextInt(random, 4, 6)));
		list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j + 16 + MathHelper.nextInt(random, 3, 8)));
		list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j - 16 + MathHelper.nextInt(random, 4, 8)));
		return list;
	}

	private static void method_14822(
		StructureManager structureManager,
		BlockPos blockPos,
		Rotation rotation,
		List<class_3443> list,
		Random random,
		OceanRuinFeatureConfig oceanRuinFeatureConfig,
		boolean bl,
		float f
	) {
		if (oceanRuinFeatureConfig.biomeTemperature == OceanRuinFeature.BiomeTemperature.WARM) {
			Identifier identifier = bl ? method_14826(random) : method_14824(random);
			list.add(new OceanTempleGenerator.class_3410(structureManager, identifier, blockPos, rotation, f, oceanRuinFeatureConfig.biomeTemperature, bl));
		} else if (oceanRuinFeatureConfig.biomeTemperature == OceanRuinFeature.BiomeTemperature.COLD) {
			Identifier[] identifiers = bl ? field_14516 : field_14518;
			Identifier[] identifiers2 = bl ? field_14520 : field_14519;
			Identifier[] identifiers3 = bl ? field_14517 : field_14522;
			int i = random.nextInt(identifiers.length);
			list.add(new OceanTempleGenerator.class_3410(structureManager, identifiers[i], blockPos, rotation, f, oceanRuinFeatureConfig.biomeTemperature, bl));
			list.add(new OceanTempleGenerator.class_3410(structureManager, identifiers2[i], blockPos, rotation, 0.7F, oceanRuinFeatureConfig.biomeTemperature, bl));
			list.add(new OceanTempleGenerator.class_3410(structureManager, identifiers3[i], blockPos, rotation, 0.5F, oceanRuinFeatureConfig.biomeTemperature, bl));
		}
	}

	public static class class_3410 extends class_3470 {
		private final OceanRuinFeature.BiomeTemperature field_14527;
		private final float field_14524;
		private final Identifier field_14523;
		private final Rotation field_14526;
		private final boolean field_14525;

		public class_3410(
			StructureManager structureManager,
			Identifier identifier,
			BlockPos blockPos,
			Rotation rotation,
			float f,
			OceanRuinFeature.BiomeTemperature biomeTemperature,
			boolean bl
		) {
			super(StructurePiece.field_16932, 0);
			this.field_14523 = identifier;
			this.field_15432 = blockPos;
			this.field_14526 = rotation;
			this.field_14524 = f;
			this.field_14527 = biomeTemperature;
			this.field_14525 = bl;
			this.method_14828(structureManager);
		}

		public class_3410(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16932, compoundTag);
			this.field_14523 = new Identifier(compoundTag.getString("Template"));
			this.field_14526 = Rotation.valueOf(compoundTag.getString("Rot"));
			this.field_14524 = compoundTag.getFloat("Integrity");
			this.field_14527 = OceanRuinFeature.BiomeTemperature.valueOf(compoundTag.getString("BiomeType"));
			this.field_14525 = compoundTag.getBoolean("IsLarge");
			this.method_14828(structureManager);
		}

		private void method_14828(StructureManager structureManager) {
			class_3499 lv = structureManager.method_15091(this.field_14523);
			class_3492 lv2 = new class_3492().method_15123(this.field_14526).method_15125(Mirror.NONE).method_16184(BlockIgnoreStructureProcessor.field_16721);
			this.method_15027(lv, this.field_15432, lv2);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.field_14523.toString());
			compoundTag.putString("Rot", this.field_14526.name());
			compoundTag.putFloat("Integrity", this.field_14524);
			compoundTag.putString("BiomeType", this.field_14527.toString());
			compoundTag.putBoolean("IsLarge", this.field_14525);
		}

		@Override
		protected void method_15026(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if ("chest".equals(string)) {
				iWorld.setBlockState(
					blockPos,
					Blocks.field_10034.getDefaultState().with(ChestBlock.field_10772, Boolean.valueOf(iWorld.getFluidState(blockPos).matches(FluidTags.field_15517))),
					2
				);
				BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity).setLootTable(this.field_14525 ? LootTables.field_300 : LootTables.field_397, random.nextLong());
				}
			} else if ("drowned".equals(string)) {
				DrownedEntity drownedEntity = new DrownedEntity(iWorld.getWorld());
				drownedEntity.setPersistent();
				drownedEntity.setPositionAndAngles(blockPos, 0.0F, 0.0F);
				drownedEntity.prepareEntityData(iWorld, iWorld.getLocalDifficulty(blockPos), SpawnType.field_16474, null, null);
				iWorld.spawnEntity(drownedEntity);
				if (blockPos.getY() > iWorld.getSeaLevel()) {
					iWorld.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 2);
				} else {
					iWorld.setBlockState(blockPos, Blocks.field_10382.getDefaultState(), 2);
				}
			}
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.field_15434.method_16183().method_16184(new BlockRotStructureProcessor(this.field_14524)).method_16184(BlockIgnoreStructureProcessor.field_16721);
			int i = iWorld.getTop(Heightmap.Type.OCEAN_FLOOR_WG, this.field_15432.getX(), this.field_15432.getZ());
			this.field_15432 = new BlockPos(this.field_15432.getX(), i, this.field_15432.getZ());
			BlockPos blockPos = class_3499.method_15168(
					new BlockPos(this.field_15433.method_15160().getX() - 1, 0, this.field_15433.method_15160().getZ() - 1),
					Mirror.NONE,
					this.field_14526,
					new BlockPos(0, 0, 0)
				)
				.add(this.field_15432);
			this.field_15432 = new BlockPos(this.field_15432.getX(), this.method_14829(this.field_15432, iWorld, blockPos), this.field_15432.getZ());
			return super.method_14931(iWorld, random, mutableIntBoundingBox, chunkPos);
		}

		private int method_14829(BlockPos blockPos, BlockView blockView, BlockPos blockPos2) {
			int i = blockPos.getY();
			int j = 512;
			int k = i - 1;
			int l = 0;

			for (BlockPos blockPos3 : BlockPos.iterateBoxPositions(blockPos, blockPos2)) {
				int m = blockPos3.getX();
				int n = blockPos3.getZ();
				int o = blockPos.getY() - 1;
				BlockPos.Mutable mutable = new BlockPos.Mutable(m, o, n);
				BlockState blockState = blockView.getBlockState(mutable);

				for (FluidState fluidState = blockView.getFluidState(mutable);
					(blockState.isAir() || fluidState.matches(FluidTags.field_15517) || blockState.getBlock().matches(BlockTags.field_15467)) && o > 1;
					fluidState = blockView.getFluidState(mutable)
				) {
					mutable.set(m, --o, n);
					blockState = blockView.getBlockState(mutable);
				}

				j = Math.min(j, o);
				if (o < k - 2) {
					l++;
				}
			}

			int p = Math.abs(blockPos.getX() - blockPos2.getX());
			if (k - j > 2 && l > p - 2) {
				i = j + 1;
			}

			return i;
		}
	}
}
