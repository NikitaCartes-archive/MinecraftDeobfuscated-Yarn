package net.minecraft.sortme.structures;

import java.util.List;
import java.util.Random;
import net.minecraft.class_3443;
import net.minecraft.class_3470;
import net.minecraft.class_3485;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.sortme.structures.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.config.feature.ShipwreckFeatureConfig;
import net.minecraft.world.loot.LootTables;

public class ShipwreckGenerator {
	private static final BlockPos field_14536 = new BlockPos(4, 0, 15);
	private static final Identifier[] field_14534 = new Identifier[]{
		new Identifier("shipwreck/with_mast"),
		new Identifier("shipwreck/sideways_full"),
		new Identifier("shipwreck/sideways_fronthalf"),
		new Identifier("shipwreck/sideways_backhalf"),
		new Identifier("shipwreck/rightsideup_full"),
		new Identifier("shipwreck/rightsideup_fronthalf"),
		new Identifier("shipwreck/rightsideup_backhalf"),
		new Identifier("shipwreck/with_mast_degraded"),
		new Identifier("shipwreck/rightsideup_full_degraded"),
		new Identifier("shipwreck/rightsideup_fronthalf_degraded"),
		new Identifier("shipwreck/rightsideup_backhalf_degraded")
	};
	private static final Identifier[] field_14535 = new Identifier[]{
		new Identifier("shipwreck/with_mast"),
		new Identifier("shipwreck/upsidedown_full"),
		new Identifier("shipwreck/upsidedown_fronthalf"),
		new Identifier("shipwreck/upsidedown_backhalf"),
		new Identifier("shipwreck/sideways_full"),
		new Identifier("shipwreck/sideways_fronthalf"),
		new Identifier("shipwreck/sideways_backhalf"),
		new Identifier("shipwreck/rightsideup_full"),
		new Identifier("shipwreck/rightsideup_fronthalf"),
		new Identifier("shipwreck/rightsideup_backhalf"),
		new Identifier("shipwreck/with_mast_degraded"),
		new Identifier("shipwreck/upsidedown_full_degraded"),
		new Identifier("shipwreck/upsidedown_fronthalf_degraded"),
		new Identifier("shipwreck/upsidedown_backhalf_degraded"),
		new Identifier("shipwreck/sideways_full_degraded"),
		new Identifier("shipwreck/sideways_fronthalf_degraded"),
		new Identifier("shipwreck/sideways_backhalf_degraded"),
		new Identifier("shipwreck/rightsideup_full_degraded"),
		new Identifier("shipwreck/rightsideup_fronthalf_degraded"),
		new Identifier("shipwreck/rightsideup_backhalf_degraded")
	};

	public static void method_14834(
		class_3485 arg, BlockPos blockPos, Rotation rotation, List<class_3443> list, Random random, ShipwreckFeatureConfig shipwreckFeatureConfig
	) {
		Identifier identifier = shipwreckFeatureConfig.isBeached ? field_14534[random.nextInt(field_14534.length)] : field_14535[random.nextInt(field_14535.length)];
		list.add(new ShipwreckGenerator.class_3416(arg, identifier, blockPos, rotation, shipwreckFeatureConfig.isBeached));
	}

	public static class class_3416 extends class_3470 {
		private final Rotation field_14539;
		private final Identifier field_14537;
		private final boolean field_14538;

		public class_3416(class_3485 arg, Identifier identifier, BlockPos blockPos, Rotation rotation, boolean bl) {
			super(StructurePiece.field_16935, 0);
			this.field_15432 = blockPos;
			this.field_14539 = rotation;
			this.field_14537 = identifier;
			this.field_14538 = bl;
			this.method_14837(arg);
		}

		public class_3416(class_3485 arg, CompoundTag compoundTag) {
			super(StructurePiece.field_16935, compoundTag);
			this.field_14537 = new Identifier(compoundTag.getString("Template"));
			this.field_14538 = compoundTag.getBoolean("isBeached");
			this.field_14539 = Rotation.valueOf(compoundTag.getString("Rot"));
			this.method_14837(arg);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.field_14537.toString());
			compoundTag.putBoolean("isBeached", this.field_14538);
			compoundTag.putString("Rot", this.field_14539.name());
		}

		private void method_14837(class_3485 arg) {
			class_3499 lv = arg.method_15091(this.field_14537);
			class_3492 lv2 = new class_3492()
				.method_15123(this.field_14539)
				.method_15125(Mirror.NONE)
				.method_15119(ShipwreckGenerator.field_14536)
				.method_16184(BlockIgnoreStructureProcessor.field_16721);
			this.method_15027(lv, this.field_15432, lv2);
		}

		@Override
		protected void method_15026(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if ("map_chest".equals(string)) {
				LootableContainerBlockEntity.method_11287(iWorld, random, blockPos.down(), LootTables.field_841);
			} else if ("treasure_chest".equals(string)) {
				LootableContainerBlockEntity.method_11287(iWorld, random, blockPos.down(), LootTables.field_665);
			} else if ("supply_chest".equals(string)) {
				LootableContainerBlockEntity.method_11287(iWorld, random, blockPos.down(), LootTables.field_880);
			}
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			int i = 256;
			int j = 0;
			BlockPos blockPos = this.field_15432.add(this.field_15433.method_15160().getX() - 1, 0, this.field_15433.method_15160().getZ() - 1);

			for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(this.field_15432, blockPos)) {
				int k = iWorld.getTop(this.field_14538 ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG, blockPos2.getX(), blockPos2.getZ());
				j += k;
				i = Math.min(i, k);
			}

			j /= this.field_15433.method_15160().getX() * this.field_15433.method_15160().getZ();
			int l = this.field_14538 ? i - this.field_15433.method_15160().getY() / 2 - random.nextInt(3) : j;
			this.field_15432 = new BlockPos(this.field_15432.getX(), l, this.field_15432.getZ());
			return super.method_14931(iWorld, random, mutableIntBoundingBox, chunkPos);
		}
	}
}
