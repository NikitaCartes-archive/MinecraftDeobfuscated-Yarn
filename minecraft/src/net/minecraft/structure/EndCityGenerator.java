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
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.loot.LootTables;

public class EndCityGenerator {
	private static final StructurePlacementData field_14383 = new StructurePlacementData()
		.setIgnoreEntities(true)
		.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
	private static final StructurePlacementData field_14389 = new StructurePlacementData()
		.setIgnoreEntities(true)
		.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
	private static final EndCityGenerator.class_3344 field_14390 = new EndCityGenerator.class_3344() {
		@Override
		public void method_14688() {
		}

		@Override
		public boolean method_14687(
			StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random
		) {
			if (i > 8) {
				return false;
			} else {
				Rotation rotation = piece.placementData.getRotation();
				EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
					list, EndCityGenerator.createPiece(structureManager, piece, blockPos, "base_floor", rotation, true)
				);
				int j = random.nextInt(3);
				if (j == 0) {
					piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 4, -1), "base_roof", rotation, true));
				} else if (j == 1) {
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false)
					);
					piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 8, -1), "second_roof", rotation, false));
					EndCityGenerator.method_14673(structureManager, EndCityGenerator.field_14386, i + 1, piece2, null, list, random);
				} else if (j == 2) {
					piece2 = EndCityGenerator.addPiece(
						list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false)
					);
					piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 4, -1), "third_floor_2", rotation, false));
					piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
					EndCityGenerator.method_14673(structureManager, EndCityGenerator.field_14386, i + 1, piece2, null, list, random);
				}

				return true;
			}
		}
	};
	private static final List<Pair<Rotation, BlockPos>> field_14385 = Lists.<Pair<Rotation, BlockPos>>newArrayList(
		new Pair<>(Rotation.ROT_0, new BlockPos(1, -1, 0)),
		new Pair<>(Rotation.ROT_90, new BlockPos(6, -1, 1)),
		new Pair<>(Rotation.ROT_270, new BlockPos(0, -1, 5)),
		new Pair<>(Rotation.ROT_180, new BlockPos(5, -1, 6))
	);
	private static final EndCityGenerator.class_3344 field_14386 = new EndCityGenerator.class_3344() {
		@Override
		public void method_14688() {
		}

		@Override
		public boolean method_14687(
			StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random
		) {
			Rotation rotation = piece.placementData.getRotation();
			EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.createPiece(structureManager, piece, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", rotation, true)
			);
			piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, 7, 0), "tower_piece", rotation, true));
			EndCityGenerator.Piece piece3 = random.nextInt(3) == 0 ? piece2 : null;
			int j = 1 + random.nextInt(3);

			for (int k = 0; k < j; k++) {
				piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, 4, 0), "tower_piece", rotation, true));
				if (k < j - 1 && random.nextBoolean()) {
					piece3 = piece2;
				}
			}

			if (piece3 != null) {
				for (Pair<Rotation, BlockPos> pair : EndCityGenerator.field_14385) {
					if (random.nextBoolean()) {
						EndCityGenerator.Piece piece4 = EndCityGenerator.addPiece(
							list, EndCityGenerator.createPiece(structureManager, piece3, pair.getRight(), "bridge_end", rotation.rotate(pair.getLeft()), true)
						);
						EndCityGenerator.method_14673(structureManager, EndCityGenerator.field_14387, i + 1, piece4, null, list, random);
					}
				}

				piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
			} else {
				if (i != 7) {
					return EndCityGenerator.method_14673(structureManager, EndCityGenerator.field_14384, i + 1, piece2, null, list, random);
				}

				piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
			}

			return true;
		}
	};
	private static final EndCityGenerator.class_3344 field_14387 = new EndCityGenerator.class_3344() {
		public boolean field_14394;

		@Override
		public void method_14688() {
			this.field_14394 = false;
		}

		@Override
		public boolean method_14687(
			StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random
		) {
			Rotation rotation = piece.placementData.getRotation();
			int j = random.nextInt(4) + 1;
			EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.createPiece(structureManager, piece, new BlockPos(0, 0, -4), "bridge_piece", rotation, true)
			);
			piece2.field_15316 = -1;
			int k = 0;

			for (int l = 0; l < j; l++) {
				if (random.nextBoolean()) {
					piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, k, -4), "bridge_piece", rotation, true));
					k = 0;
				} else {
					if (random.nextBoolean()) {
						piece2 = EndCityGenerator.addPiece(
							list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, k, -4), "bridge_steep_stairs", rotation, true)
						);
					} else {
						piece2 = EndCityGenerator.addPiece(
							list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, k, -8), "bridge_gentle_stairs", rotation, true)
						);
					}

					k = 4;
				}
			}

			if (!this.field_14394 && random.nextInt(10 - i) == 0) {
				EndCityGenerator.addPiece(
					list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-8 + random.nextInt(8), k, -70 + random.nextInt(10)), "ship", rotation, true)
				);
				this.field_14394 = true;
			} else if (!EndCityGenerator.method_14673(structureManager, EndCityGenerator.field_14390, i + 1, piece2, new BlockPos(-3, k + 1, -11), list, random)) {
				return false;
			}

			piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(4, k, 0), "bridge_end", rotation.rotate(Rotation.ROT_180), true)
			);
			piece2.field_15316 = -1;
			return true;
		}
	};
	private static final List<Pair<Rotation, BlockPos>> field_14388 = Lists.<Pair<Rotation, BlockPos>>newArrayList(
		new Pair<>(Rotation.ROT_0, new BlockPos(4, -1, 0)),
		new Pair<>(Rotation.ROT_90, new BlockPos(12, -1, 4)),
		new Pair<>(Rotation.ROT_270, new BlockPos(0, -1, 8)),
		new Pair<>(Rotation.ROT_180, new BlockPos(8, -1, 12))
	);
	private static final EndCityGenerator.class_3344 field_14384 = new EndCityGenerator.class_3344() {
		@Override
		public void method_14688() {
		}

		@Override
		public boolean method_14687(
			StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random
		) {
			Rotation rotation = piece.placementData.getRotation();
			EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
				list, EndCityGenerator.createPiece(structureManager, piece, new BlockPos(-3, 4, -3), "fat_tower_base", rotation, true)
			);
			piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, 4, 0), "fat_tower_middle", rotation, true));

			for (int j = 0; j < 2 && random.nextInt(3) != 0; j++) {
				piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(0, 8, 0), "fat_tower_middle", rotation, true));

				for (Pair<Rotation, BlockPos> pair : EndCityGenerator.field_14388) {
					if (random.nextBoolean()) {
						EndCityGenerator.Piece piece3 = EndCityGenerator.addPiece(
							list, EndCityGenerator.createPiece(structureManager, piece2, pair.getRight(), "bridge_end", rotation.rotate(pair.getLeft()), true)
						);
						EndCityGenerator.method_14673(structureManager, EndCityGenerator.field_14387, i + 1, piece3, null, list, random);
					}
				}
			}

			piece2 = EndCityGenerator.addPiece(list, EndCityGenerator.createPiece(structureManager, piece2, new BlockPos(-2, 8, -2), "fat_tower_top", rotation, true));
			return true;
		}
	};

	private static EndCityGenerator.Piece createPiece(
		StructureManager structureManager, EndCityGenerator.Piece piece, BlockPos blockPos, String string, Rotation rotation, boolean bl
	) {
		EndCityGenerator.Piece piece2 = new EndCityGenerator.Piece(structureManager, string, piece.pos, rotation, bl);
		BlockPos blockPos2 = piece.structure.method_15180(piece.placementData, blockPos, piece2.placementData, BlockPos.ORIGIN);
		piece2.translate(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
		return piece2;
	}

	public static void addPieces(StructureManager structureManager, BlockPos blockPos, Rotation rotation, List<StructurePiece> list, Random random) {
		field_14384.method_14688();
		field_14390.method_14688();
		field_14387.method_14688();
		field_14386.method_14688();
		EndCityGenerator.Piece piece = addPiece(list, new EndCityGenerator.Piece(structureManager, "base_floor", blockPos, rotation, true));
		piece = addPiece(list, createPiece(structureManager, piece, new BlockPos(-1, 0, -1), "second_floor_1", rotation, false));
		piece = addPiece(list, createPiece(structureManager, piece, new BlockPos(-1, 4, -1), "third_floor_1", rotation, false));
		piece = addPiece(list, createPiece(structureManager, piece, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
		method_14673(structureManager, field_14386, 1, piece, null, list, random);
	}

	private static EndCityGenerator.Piece addPiece(List<StructurePiece> list, EndCityGenerator.Piece piece) {
		list.add(piece);
		return piece;
	}

	private static boolean method_14673(
		StructureManager structureManager,
		EndCityGenerator.class_3344 arg,
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
			if (arg.method_14687(structureManager, i, piece, blockPos, list2, random)) {
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

	public static class Piece extends SimpleStructurePiece {
		private final String template;
		private final Rotation rotation;
		private final boolean field_14392;

		public Piece(StructureManager structureManager, String string, BlockPos blockPos, Rotation rotation, boolean bl) {
			super(StructurePieceType.END_CITY, 0);
			this.template = string;
			this.pos = blockPos;
			this.rotation = rotation;
			this.field_14392 = bl;
			this.method_14686(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.END_CITY, compoundTag);
			this.template = compoundTag.getString("Template");
			this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
			this.field_14392 = compoundTag.getBoolean("OW");
			this.method_14686(structureManager);
		}

		private void method_14686(StructureManager structureManager) {
			Structure structure = structureManager.getStructureOrBlank(new Identifier("end_city/" + this.template));
			StructurePlacementData structurePlacementData = (this.field_14392 ? EndCityGenerator.field_14383 : EndCityGenerator.field_14389)
				.copy()
				.setRotation(this.rotation);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.template);
			compoundTag.putString("Rot", this.rotation.name());
			compoundTag.putBoolean("OW", this.field_14392);
		}

		@Override
		protected void handleMetadata(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if (string.startsWith("Chest")) {
				BlockPos blockPos2 = blockPos.down();
				if (mutableIntBoundingBox.contains(blockPos2)) {
					LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos2, LootTables.CHEST_END_CITY_TREASURE);
				}
			} else if (string.startsWith("Sentry")) {
				ShulkerEntity shulkerEntity = EntityType.SHULKER.create(iWorld.getWorld());
				shulkerEntity.setPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
				shulkerEntity.setAttachedBlock(blockPos);
				iWorld.spawnEntity(shulkerEntity);
			} else if (string.startsWith("Elytra")) {
				ItemFrameEntity itemFrameEntity = new ItemFrameEntity(iWorld.getWorld(), blockPos, this.rotation.rotate(Direction.SOUTH));
				itemFrameEntity.setHeldItemStack(new ItemStack(Items.field_8833), false);
				iWorld.spawnEntity(itemFrameEntity);
			}
		}
	}

	interface class_3344 {
		void method_14688();

		boolean method_14687(StructureManager structureManager, int i, EndCityGenerator.Piece piece, BlockPos blockPos, List<StructurePiece> list, Random random);
	}
}
