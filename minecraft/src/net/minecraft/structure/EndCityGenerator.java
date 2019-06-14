package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.loot.LootTables;

public class EndCityGenerator {
	private static final StructurePlacementData field_14383 = new StructurePlacementData()
		.setIgnoreEntities(true)
		.method_16184(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
	private static final StructurePlacementData field_14389 = new StructurePlacementData()
		.setIgnoreEntities(true)
		.method_16184(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
	private static final EndCityGenerator.Part BUILDING = new EndCityGenerator.Part() {
		@Override
		public void init() {
		}

		@Override
		public boolean method_14687(
			StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random
		) {
			if (i > 8) {
				return false;
			} else {
				BlockRotation blockRotation = piece.field_15434.getRotation();
				EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
					list, EndCityGenerator.method_14684(structureManager, piece, blockPos, "base_floor", blockRotation, true)
				);
				int j = random.nextInt(3);
				if (j == 0) {
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(-1, 4, -1), "base_roof", blockRotation, true)
					);
				} else if (j == 1) {
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false)
					);
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(-1, 8, -1), "second_roof", blockRotation, false)
					);
					EndCityGenerator.method_14673(structureManager, EndCityGenerator.SMALL_TOWER, i + 1, piece2, null, list, random);
				} else if (j == 2) {
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false)
					);
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(-1, 4, -1), "third_floor_2", blockRotation, false)
					);
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(-1, 8, -1), "third_roof", blockRotation, true)
					);
					EndCityGenerator.method_14673(structureManager, EndCityGenerator.SMALL_TOWER, i + 1, piece2, null, list, random);
				}

				return true;
			}
		}
	};
	private static final List<Pair<BlockRotation, BlockPos>> SMALL_TOWER_BRIDGE_ATTACHMENTS = Lists.<Pair<BlockRotation, BlockPos>>newArrayList(
		new Pair<>(BlockRotation.field_11467, new BlockPos(1, -1, 0)),
		new Pair<>(BlockRotation.field_11463, new BlockPos(6, -1, 1)),
		new Pair<>(BlockRotation.field_11465, new BlockPos(0, -1, 5)),
		new Pair<>(BlockRotation.field_11464, new BlockPos(5, -1, 6))
	);
	private static final EndCityGenerator.Part SMALL_TOWER = new EndCityGenerator.Part() {
		@Override
		public void init() {
		}

		@Override
		public boolean method_14687(
			StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random
		) {
			BlockRotation blockRotation = piece.field_15434.getRotation();
			EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
				list,
				EndCityGenerator.method_14684(structureManager, piece, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", blockRotation, true)
			);
			piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(0, 7, 0), "tower_piece", blockRotation, true));
			EndCityGenerator.Piece piece3 = random.nextInt(3) == 0 ? piece2 : null;
			int j = 1 + random.nextInt(3);

			for (int k = 0; k < j; k++) {
				piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(0, 4, 0), "tower_piece", blockRotation, true));
				if (k < j - 1 && random.nextBoolean()) {
					piece3 = piece2;
				}
			}

			if (piece3 != null) {
				for (Pair<BlockRotation, BlockPos> pair : EndCityGenerator.SMALL_TOWER_BRIDGE_ATTACHMENTS) {
					if (random.nextBoolean()) {
						EndCityGenerator.Piece piece4 = EndCityGenerator.addPiece(
							list, EndCityGenerator.method_14684(structureManager, piece3, pair.getRight(), "bridge_end", blockRotation.rotate(pair.getLeft()), true)
						);
						EndCityGenerator.method_14673(structureManager, EndCityGenerator.BRIDGE_PIECE, i + 1, piece4, null, list, random);
					}
				}

				piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
			} else {
				if (i != 7) {
					return EndCityGenerator.method_14673(structureManager, EndCityGenerator.FAT_TOWER, i + 1, piece2, null, list, random);
				}

				piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
			}

			return true;
		}
	};
	private static final EndCityGenerator.Part BRIDGE_PIECE = new EndCityGenerator.Part() {
		public boolean shipGenerated;

		@Override
		public void init() {
			this.shipGenerated = false;
		}

		@Override
		public boolean method_14687(
			StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random
		) {
			BlockRotation blockRotation = piece.field_15434.getRotation();
			int j = random.nextInt(4) + 1;
			EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.method_14684(structureManager, piece, new BlockPos(0, 0, -4), "bridge_piece", blockRotation, true)
			);
			piece2.field_15316 = -1;
			int k = 0;

			for (int l = 0; l < j; l++) {
				if (random.nextBoolean()) {
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(0, k, -4), "bridge_piece", blockRotation, true)
					);
					k = 0;
				} else {
					if (random.nextBoolean()) {
						piece2 = EndCityGenerator.addPiece(
							list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(0, k, -4), "bridge_steep_stairs", blockRotation, true)
						);
					} else {
						piece2 = EndCityGenerator.addPiece(
							list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(0, k, -8), "bridge_gentle_stairs", blockRotation, true)
						);
					}

					k = 4;
				}
			}

			if (!this.shipGenerated && random.nextInt(10 - i) == 0) {
				EndCityGenerator.addPiece(
					list,
					EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(-8 + random.nextInt(8), k, -70 + random.nextInt(10)), "ship", blockRotation, true)
				);
				this.shipGenerated = true;
			} else if (!EndCityGenerator.method_14673(structureManager, EndCityGenerator.BUILDING, i + 1, piece2, new BlockPos(-3, k + 1, -11), list, random)) {
				return false;
			}

			piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(4, k, 0), "bridge_end", blockRotation.rotate(BlockRotation.field_11464), true)
			);
			piece2.field_15316 = -1;
			return true;
		}
	};
	private static final List<Pair<BlockRotation, BlockPos>> FAT_TOWER_BRIDGE_ATTACHMENTS = Lists.<Pair<BlockRotation, BlockPos>>newArrayList(
		new Pair<>(BlockRotation.field_11467, new BlockPos(4, -1, 0)),
		new Pair<>(BlockRotation.field_11463, new BlockPos(12, -1, 4)),
		new Pair<>(BlockRotation.field_11465, new BlockPos(0, -1, 8)),
		new Pair<>(BlockRotation.field_11464, new BlockPos(8, -1, 12))
	);
	private static final EndCityGenerator.Part FAT_TOWER = new EndCityGenerator.Part() {
		@Override
		public void init() {
		}

		@Override
		public boolean method_14687(
			StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random
		) {
			BlockRotation blockRotation = piece.field_15434.getRotation();
			EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.method_14684(structureManager, piece, new BlockPos(-3, 4, -3), "fat_tower_base", blockRotation, true)
			);
			piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(0, 4, 0), "fat_tower_middle", blockRotation, true)
			);

			for (int j = 0; j < 2 && random.nextInt(3) != 0; j++) {
				piece2 = EndCityGenerator.addPiece(
					list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(0, 8, 0), "fat_tower_middle", blockRotation, true)
				);

				for (Pair<BlockRotation, BlockPos> pair : EndCityGenerator.FAT_TOWER_BRIDGE_ATTACHMENTS) {
					if (random.nextBoolean()) {
						EndCityGenerator.Piece piece3 = EndCityGenerator.addPiece(
							list, EndCityGenerator.method_14684(structureManager, piece2, pair.getRight(), "bridge_end", blockRotation.rotate(pair.getLeft()), true)
						);
						EndCityGenerator.method_14673(structureManager, EndCityGenerator.BRIDGE_PIECE, i + 1, piece3, null, list, random);
					}
				}
			}

			piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.method_14684(structureManager, piece2, new BlockPos(-2, 8, -2), "fat_tower_top", blockRotation, true)
			);
			return true;
		}
	};

	private static EndCityGenerator.Piece method_14684(
		StructureManager structureManager, EndCityGenerator.Piece piece, BlockPos blockPos, String string, BlockRotation blockRotation, boolean bl
	) {
		EndCityGenerator.Piece piece2 = new EndCityGenerator.Piece(structureManager, string, piece.pos, blockRotation, bl);
		BlockPos blockPos2 = piece.field_15433.method_15180(piece.field_15434, blockPos, piece2.field_15434, BlockPos.ORIGIN);
		piece2.translate(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
		return piece2;
	}

	public static void method_14679(StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation, List<StructurePiece> list, Random random) {
		FAT_TOWER.init();
		BUILDING.init();
		BRIDGE_PIECE.init();
		SMALL_TOWER.init();
		EndCityGenerator.Piece piece = addPiece(list, new EndCityGenerator.Piece(structureManager, "base_floor", blockPos, blockRotation, true));
		piece = addPiece(list, method_14684(structureManager, piece, new BlockPos(-1, 0, -1), "second_floor_1", blockRotation, false));
		piece = addPiece(list, method_14684(structureManager, piece, new BlockPos(-1, 4, -1), "third_floor_1", blockRotation, false));
		piece = addPiece(list, method_14684(structureManager, piece, new BlockPos(-1, 8, -1), "third_roof", blockRotation, true));
		method_14673(structureManager, SMALL_TOWER, 1, piece, null, list, random);
	}

	private static EndCityGenerator.Piece addPiece(List<StructurePiece> list, EndCityGenerator.Piece piece) {
		list.add(piece);
		return piece;
	}

	private static boolean method_14673(
		StructureManager structureManager,
		EndCityGenerator.Part part,
		int i,
		EndCityGenerator.Piece piece,
		BlockPos blockPos,
		List<StructurePiece> list,
		Random random
	) {
		if (i > 8) {
			return false;
		} else {
			List<StructurePiece> list2 = Lists.<StructurePiece>newArrayList();
			if (part.method_14687(structureManager, i, piece, blockPos, list2, random)) {
				boolean bl = false;
				int j = random.nextInt();

				for (StructurePiece structurePiece : list2) {
					structurePiece.field_15316 = j;
					StructurePiece structurePiece2 = StructurePiece.method_14932(list, structurePiece.getBoundingBox());
					if (structurePiece2 != null && structurePiece2.field_15316 != piece.field_15316) {
						bl = true;
						break;
					}
				}

				if (!bl) {
					list.addAll(list2);
					return true;
				}
			}

			return false;
		}
	}

	interface Part {
		void init();

		boolean method_14687(StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random);
	}

	public static class Piece extends SimpleStructurePiece {
		private final String template;
		private final BlockRotation rotation;
		private final boolean ignoreAir;

		public Piece(StructureManager structureManager, String string, BlockPos blockPos, BlockRotation blockRotation, boolean bl) {
			super(StructurePieceType.END_CITY, 0);
			this.template = string;
			this.pos = blockPos;
			this.rotation = blockRotation;
			this.ignoreAir = bl;
			this.method_14686(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.END_CITY, compoundTag);
			this.template = compoundTag.getString("Template");
			this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
			this.ignoreAir = compoundTag.getBoolean("OW");
			this.method_14686(structureManager);
		}

		private void method_14686(StructureManager structureManager) {
			Structure structure = structureManager.method_15091(new Identifier("end_city/" + this.template));
			StructurePlacementData structurePlacementData = (this.ignoreAir ? EndCityGenerator.field_14383 : EndCityGenerator.field_14389)
				.copy()
				.setRotation(this.rotation);
			this.method_15027(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.template);
			compoundTag.putString("Rot", this.rotation.name());
			compoundTag.putBoolean("OW", this.ignoreAir);
		}

		@Override
		protected void handleMetadata(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if (string.startsWith("Chest")) {
				BlockPos blockPos2 = blockPos.down();
				if (mutableIntBoundingBox.contains(blockPos2)) {
					LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos2, LootTables.field_274);
				}
			} else if (string.startsWith("Sentry")) {
				ShulkerEntity shulkerEntity = EntityType.field_6109.method_5883(iWorld.getWorld());
				shulkerEntity.setPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
				shulkerEntity.setAttachedBlock(blockPos);
				iWorld.spawnEntity(shulkerEntity);
			} else if (string.startsWith("Elytra")) {
				ItemFrameEntity itemFrameEntity = new ItemFrameEntity(iWorld.getWorld(), blockPos, this.rotation.rotate(Direction.field_11035));
				itemFrameEntity.setHeldItemStack(new ItemStack(Items.field_8833), false);
				iWorld.spawnEntity(itemFrameEntity);
			}
		}
	}
}
