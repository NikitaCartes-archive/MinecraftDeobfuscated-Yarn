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
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class EndCityGenerator {
	private static final StructurePlacementData PLACEMENT_DATA = new StructurePlacementData()
		.setIgnoreEntities(true)
		.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
	private static final StructurePlacementData IGNORE_AIR_PLACEMENT_DATA = new StructurePlacementData()
		.setIgnoreEntities(true)
		.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
	private static final EndCityGenerator.Part BUILDING = new EndCityGenerator.Part() {
		@Override
		public void init() {
		}

		@Override
		public boolean create(StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random) {
			if (i > 8) {
				return false;
			} else {
				BlockRotation blockRotation = piece.placementData.getRotation();
				EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
					list, EndCityGenerator.createPiece(structureManager, piece, blockPos, "base_floor", blockRotation, true)
				);
				int j = random.nextInt(3);
				if (j == 0) {
					piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 4, -1), "base_roof", blockRotation, true));
				} else if (j == 1) {
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false)
					);
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 8, -1), "second_roof", blockRotation, false)
					);
					EndCityGenerator.createPart(structureManager, EndCityGenerator.SMALL_TOWER, i + 1, piece2, null, list, random);
				} else if (j == 2) {
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false)
					);
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 4, -1), "third_floor_2", blockRotation, false)
					);
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 8, -1), "third_roof", blockRotation, true)
					);
					EndCityGenerator.createPart(structureManager, EndCityGenerator.SMALL_TOWER, i + 1, piece2, null, list, random);
				}

				return true;
			}
		}
	};
	private static final List<Pair<BlockRotation, BlockPos>> SMALL_TOWER_BRIDGE_ATTACHMENTS = Lists.<Pair<BlockRotation, BlockPos>>newArrayList(
		new Pair<>(BlockRotation.NONE, new BlockPos(1, -1, 0)),
		new Pair<>(BlockRotation.CLOCKWISE_90, new BlockPos(6, -1, 1)),
		new Pair<>(BlockRotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)),
		new Pair<>(BlockRotation.CLOCKWISE_180, new BlockPos(5, -1, 6))
	);
	private static final EndCityGenerator.Part SMALL_TOWER = new EndCityGenerator.Part() {
		@Override
		public void init() {
		}

		@Override
		public boolean create(StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random) {
			BlockRotation blockRotation = piece.placementData.getRotation();
			EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
				list,
				EndCityGenerator.createPiece(structureManager, piece, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", blockRotation, true)
			);
			piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, 7, 0), "tower_piece", blockRotation, true));
			EndCityGenerator.Piece piece3 = random.nextInt(3) == 0 ? piece2 : null;
			int j = 1 + random.nextInt(3);

			for (int k = 0; k < j; k++) {
				piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, 4, 0), "tower_piece", blockRotation, true));
				if (k < j - 1 && random.nextBoolean()) {
					piece3 = piece2;
				}
			}

			if (piece3 != null) {
				for (Pair<BlockRotation, BlockPos> pair : EndCityGenerator.SMALL_TOWER_BRIDGE_ATTACHMENTS) {
					if (random.nextBoolean()) {
						EndCityGenerator.Piece piece4 = EndCityGenerator.addPiece(
							list, EndCityGenerator.createPiece(structureManager, piece3, pair.getRight(), "bridge_end", blockRotation.rotate(pair.getLeft()), true)
						);
						EndCityGenerator.createPart(structureManager, EndCityGenerator.BRIDGE_PIECE, i + 1, piece4, null, list, random);
					}
				}

				piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
			} else {
				if (i != 7) {
					return EndCityGenerator.createPart(structureManager, EndCityGenerator.FAT_TOWER, i + 1, piece2, null, list, random);
				}

				piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
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
		public boolean create(StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random) {
			BlockRotation blockRotation = piece.placementData.getRotation();
			int j = random.nextInt(4) + 1;
			EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.createPiece(structureManager, piece, new BlockPos(0, 0, -4), "bridge_piece", blockRotation, true)
			);
			piece2.field_15316 = -1;
			int k = 0;

			for (int l = 0; l < j; l++) {
				if (random.nextBoolean()) {
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, k, -4), "bridge_piece", blockRotation, true)
					);
					k = 0;
				} else {
					if (random.nextBoolean()) {
						piece2 = EndCityGenerator.addPiece(
							list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, k, -4), "bridge_steep_stairs", blockRotation, true)
						);
					} else {
						piece2 = EndCityGenerator.addPiece(
							list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, k, -8), "bridge_gentle_stairs", blockRotation, true)
						);
					}

					k = 4;
				}
			}

			if (!this.shipGenerated && random.nextInt(10 - i) == 0) {
				EndCityGenerator.addPiece(
					list,
					EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-8 + random.nextInt(8), k, -70 + random.nextInt(10)), "ship", blockRotation, true)
				);
				this.shipGenerated = true;
			} else if (!EndCityGenerator.createPart(structureManager, EndCityGenerator.BUILDING, i + 1, piece2, new BlockPos(-3, k + 1, -11), list, random)) {
				return false;
			}

			piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(4, k, 0), "bridge_end", blockRotation.rotate(BlockRotation.CLOCKWISE_180), true)
			);
			piece2.field_15316 = -1;
			return true;
		}
	};
	private static final List<Pair<BlockRotation, BlockPos>> FAT_TOWER_BRIDGE_ATTACHMENTS = Lists.<Pair<BlockRotation, BlockPos>>newArrayList(
		new Pair<>(BlockRotation.NONE, new BlockPos(4, -1, 0)),
		new Pair<>(BlockRotation.CLOCKWISE_90, new BlockPos(12, -1, 4)),
		new Pair<>(BlockRotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)),
		new Pair<>(BlockRotation.CLOCKWISE_180, new BlockPos(8, -1, 12))
	);
	private static final EndCityGenerator.Part FAT_TOWER = new EndCityGenerator.Part() {
		@Override
		public void init() {
		}

		@Override
		public boolean create(StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random) {
			BlockRotation blockRotation = piece.placementData.getRotation();
			EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.createPiece(structureManager, piece, new BlockPos(-3, 4, -3), "fat_tower_base", blockRotation, true)
			);
			piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, 4, 0), "fat_tower_middle", blockRotation, true)
			);

			for (int j = 0; j < 2 && random.nextInt(3) != 0; j++) {
				piece2 = EndCityGenerator.addPiece(
					list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, 8, 0), "fat_tower_middle", blockRotation, true)
				);

				for (Pair<BlockRotation, BlockPos> pair : EndCityGenerator.FAT_TOWER_BRIDGE_ATTACHMENTS) {
					if (random.nextBoolean()) {
						EndCityGenerator.Piece piece3 = EndCityGenerator.addPiece(
							list, EndCityGenerator.createPiece(structureManager, piece2, pair.getRight(), "bridge_end", blockRotation.rotate(pair.getLeft()), true)
						);
						EndCityGenerator.createPart(structureManager, EndCityGenerator.BRIDGE_PIECE, i + 1, piece3, null, list, random);
					}
				}
			}

			piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-2, 8, -2), "fat_tower_top", blockRotation, true)
			);
			return true;
		}
	};

	private static EndCityGenerator.Piece createPiece(
		StructureManager structureManager, EndCityGenerator.Piece piece, BlockPos blockPos, String string, BlockRotation blockRotation, boolean bl
	) {
		EndCityGenerator.Piece piece2 = new EndCityGenerator.Piece(structureManager, string, piece.pos, blockRotation, bl);
		BlockPos blockPos2 = piece.structure.method_15180(piece.placementData, blockPos, piece2.placementData, BlockPos.ORIGIN);
		piece2.translate(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
		return piece2;
	}

	public static void addPieces(StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation, List<StructurePiece> list, Random random) {
		FAT_TOWER.init();
		BUILDING.init();
		BRIDGE_PIECE.init();
		SMALL_TOWER.init();
		EndCityGenerator.Piece piece = addPiece(list, new EndCityGenerator.Piece(structureManager, "base_floor", blockPos, blockRotation, true));
		piece = addPiece(list, createPiece(structureManager, piece, new BlockPos(-1, 0, -1), "second_floor_1", blockRotation, false));
		piece = addPiece(list, createPiece(structureManager, piece, new BlockPos(-1, 4, -1), "third_floor_1", blockRotation, false));
		piece = addPiece(list, createPiece(structureManager, piece, new BlockPos(-1, 8, -1), "third_roof", blockRotation, true));
		createPart(structureManager, SMALL_TOWER, 1, piece, null, list, random);
	}

	private static EndCityGenerator.Piece addPiece(List<StructurePiece> list, EndCityGenerator.Piece piece) {
		list.add(piece);
		return piece;
	}

	private static boolean createPart(
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
			if (part.create(structureManager, i, piece, blockPos, list2, random)) {
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

		boolean create(StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random);
	}

	public static class Piece extends SimpleStructurePiece {
		private final String template;
		private final BlockRotation rotation;
		private final boolean ignoreAir;

		public Piece(StructureManager structureManager, String string, BlockPos blockPos, BlockRotation blockRotation, boolean bl) {
			super(StructurePieceType.ECP, 0);
			this.template = string;
			this.pos = blockPos;
			this.rotation = blockRotation;
			this.ignoreAir = bl;
			this.initializeStructureData(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.ECP, compoundTag);
			this.template = compoundTag.getString("Template");
			this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
			this.ignoreAir = compoundTag.getBoolean("OW");
			this.initializeStructureData(structureManager);
		}

		private void initializeStructureData(StructureManager structureManager) {
			Structure structure = structureManager.getStructureOrBlank(new Identifier("end_city/" + this.template));
			StructurePlacementData structurePlacementData = (this.ignoreAir ? EndCityGenerator.PLACEMENT_DATA : EndCityGenerator.IGNORE_AIR_PLACEMENT_DATA)
				.copy()
				.setRotation(this.rotation);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.template);
			compoundTag.putString("Rot", this.rotation.name());
			compoundTag.putBoolean("OW", this.ignoreAir);
		}

		@Override
		protected void handleMetadata(String string, BlockPos blockPos, IWorld iWorld, Random random, BlockBox blockBox) {
			if (string.startsWith("Chest")) {
				BlockPos blockPos2 = blockPos.method_10074();
				if (blockBox.contains(blockPos2)) {
					LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos2, LootTables.END_CITY_TREASURE_CHEST);
				}
			} else if (string.startsWith("Sentry")) {
				ShulkerEntity shulkerEntity = EntityType.SHULKER.create(iWorld.getWorld());
				shulkerEntity.setPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
				shulkerEntity.setAttachedBlock(blockPos);
				iWorld.spawnEntity(shulkerEntity);
			} else if (string.startsWith("Elytra")) {
				ItemFrameEntity itemFrameEntity = new ItemFrameEntity(iWorld.getWorld(), blockPos, this.rotation.rotate(Direction.SOUTH));
				itemFrameEntity.setHeldItemStack(new ItemStack(Items.ELYTRA), false);
				iWorld.spawnEntity(itemFrameEntity);
			}
		}
	}
}
