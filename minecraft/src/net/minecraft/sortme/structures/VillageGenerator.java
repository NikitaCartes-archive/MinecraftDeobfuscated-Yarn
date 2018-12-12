package net.minecraft.sortme.structures;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.class_3443;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.village.VillagerType;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.VillageFeatureConfig;
import net.minecraft.world.loot.LootTables;

public class VillageGenerator {
	public static List<VillageGenerator.class_3455> method_14986(Random random, int i) {
		List<VillageGenerator.class_3455> list = Lists.<VillageGenerator.class_3455>newArrayList();
		list.add(new VillageGenerator.class_3455(VillageGenerator.class_3457.class, 4, MathHelper.nextInt(random, 2 + i, 4 + i * 2)));
		list.add(new VillageGenerator.class_3455(VillageGenerator.class_3459.class, 20, MathHelper.nextInt(random, 0 + i, 1 + i)));
		list.add(new VillageGenerator.class_3455(VillageGenerator.class_3451.class, 20, MathHelper.nextInt(random, 0 + i, 2 + i)));
		list.add(new VillageGenerator.class_3455(VillageGenerator.class_3458.class, 3, MathHelper.nextInt(random, 2 + i, 5 + i * 3)));
		list.add(new VillageGenerator.class_3455(VillageGenerator.class_3456.class, 15, MathHelper.nextInt(random, 0 + i, 2 + i)));
		list.add(new VillageGenerator.class_3455(VillageGenerator.class_3452.class, 3, MathHelper.nextInt(random, 1 + i, 4 + i)));
		list.add(new VillageGenerator.class_3455(VillageGenerator.class_3453.class, 3, MathHelper.nextInt(random, 2 + i, 4 + i * 2)));
		list.add(new VillageGenerator.class_3455(VillageGenerator.class_3460.class, 15, MathHelper.nextInt(random, 0, 1 + i)));
		list.add(new VillageGenerator.class_3455(VillageGenerator.class_3463.class, 8, MathHelper.nextInt(random, 0 + i, 3 + i * 2)));
		Iterator<VillageGenerator.class_3455> iterator = list.iterator();

		while (iterator.hasNext()) {
			if (((VillageGenerator.class_3455)iterator.next()).field_15337 == 0) {
				iterator.remove();
			}
		}

		return list;
	}

	private static int method_14983(List<VillageGenerator.class_3455> list) {
		boolean bl = false;
		int i = 0;

		for (VillageGenerator.class_3455 lv : list) {
			if (lv.field_15337 > 0 && lv.field_15339 < lv.field_15337) {
				bl = true;
			}

			i += lv.field_15340;
		}

		return bl ? i : -1;
	}

	private static VillageGenerator.class_3465 method_14984(
		VillageGenerator.class_3461 arg, VillageGenerator.class_3455 arg2, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
	) {
		Class<? extends VillageGenerator.class_3465> class_ = arg2.field_15338;
		VillageGenerator.class_3465 lv = null;
		if (class_ == VillageGenerator.class_3457.class) {
			lv = VillageGenerator.class_3457.method_14998(arg, list, random, i, j, k, direction, l);
		} else if (class_ == VillageGenerator.class_3459.class) {
			lv = VillageGenerator.class_3459.method_15000(arg, list, random, i, j, k, direction, l);
		} else if (class_ == VillageGenerator.class_3451.class) {
			lv = VillageGenerator.class_3451.method_14989(arg, list, random, i, j, k, direction, l);
		} else if (class_ == VillageGenerator.class_3458.class) {
			lv = VillageGenerator.class_3458.method_14999(arg, list, random, i, j, k, direction, l);
		} else if (class_ == VillageGenerator.class_3456.class) {
			lv = VillageGenerator.class_3456.method_14997(arg, list, random, i, j, k, direction, l);
		} else if (class_ == VillageGenerator.class_3452.class) {
			lv = VillageGenerator.class_3452.method_14990(arg, list, random, i, j, k, direction, l);
		} else if (class_ == VillageGenerator.class_3453.class) {
			lv = VillageGenerator.class_3453.method_14993(arg, list, random, i, j, k, direction, l);
		} else if (class_ == VillageGenerator.class_3460.class) {
			lv = VillageGenerator.class_3460.method_15001(arg, list, random, i, j, k, direction, l);
		} else if (class_ == VillageGenerator.class_3463.class) {
			lv = VillageGenerator.class_3463.method_15003(arg, list, random, i, j, k, direction, l);
		}

		return lv;
	}

	private static VillageGenerator.class_3465 method_14981(
		VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
	) {
		int m = method_14983(arg.field_15345);
		if (m <= 0) {
			return null;
		} else {
			int n = 0;

			while (n < 5) {
				n++;
				int o = random.nextInt(m);

				for (VillageGenerator.class_3455 lv : arg.field_15345) {
					o -= lv.field_15340;
					if (o < 0) {
						if (!lv.method_14996(l) || lv == arg.field_15348 && arg.field_15345.size() > 1) {
							break;
						}

						VillageGenerator.class_3465 lv2 = method_14984(arg, lv, list, random, i, j, k, direction, l);
						if (lv2 != null) {
							lv.field_15339++;
							arg.field_15348 = lv;
							if (!lv.method_14995()) {
								arg.field_15345.remove(lv);
							}

							return lv2;
						}
					}
				}
			}

			MutableIntBoundingBox mutableIntBoundingBox = VillageGenerator.class_3454.method_14994(list, i, j, k, direction);
			return mutableIntBoundingBox != null ? new VillageGenerator.class_3454(arg, l, random, mutableIntBoundingBox, direction) : null;
		}
	}

	private static class_3443 method_14988(VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l) {
		if (l > 50) {
			return null;
		} else if (Math.abs(i - arg.method_14935().minX) <= 112 && Math.abs(k - arg.method_14935().minZ) <= 112) {
			class_3443 lv = method_14981(arg, list, random, i, j, k, direction, l + 1);
			if (lv != null) {
				list.add(lv);
				arg.field_15346.add(lv);
				return lv;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private static class_3443 method_14987(VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l) {
		if (l > 3 + arg.field_15347) {
			return null;
		} else if (Math.abs(i - arg.method_14935().minX) <= 112 && Math.abs(k - arg.method_14935().minZ) <= 112) {
			MutableIntBoundingBox mutableIntBoundingBox = VillageGenerator.class_3462.method_15002(arg, list, random, i, j, k, direction);
			if (mutableIntBoundingBox != null && mutableIntBoundingBox.minY > 10) {
				class_3443 lv = new VillageGenerator.class_3462(arg, l, mutableIntBoundingBox, direction);
				list.add(lv);
				arg.field_15349.add(lv);
				return lv;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static enum Type {
		OAK("oak", 0),
		SANDSTONE("sandstone", 1),
		ACACIA("acacia", 2),
		SPRUCE("spruce", 3);

		private static final Map<String, VillageGenerator.Type> nameMap = (Map<String, VillageGenerator.Type>)Arrays.stream(values())
			.collect(Collectors.toMap(VillageGenerator.Type::getName, type -> type));
		private final int index;
		private final String name;

		private Type(String string2, int j) {
			this.name = string2;
			this.index = j;
		}

		public String getName() {
			return this.name;
		}

		public static VillageGenerator.Type byName(String string) {
			return (VillageGenerator.Type)nameMap.get(string);
		}

		public int getIndex() {
			return this.index;
		}

		public static VillageGenerator.Type byIndex(int i) {
			VillageGenerator.Type[] types = values();
			return i >= 0 && i < types.length ? types[i] : OAK;
		}
	}

	public static class class_3451 extends VillageGenerator.class_3465 {
		public class_3451(VillageGenerator.class_3461 arg, int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16920, arg, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3451(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16920, compoundTag);
		}

		public static VillageGenerator.class_3451 method_14989(
			VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
		) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, 0, 0, 0, 9, 9, 6, direction);
			return method_15009(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new VillageGenerator.class_3451(arg, l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_15014(iWorld, mutableIntBoundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.structureBounds.translate(0, this.hPos - this.structureBounds.maxY + 9 - 1, 0);
			}

			BlockState blockState = this.method_15016(Blocks.field_10445.getDefaultState());
			BlockState blockState2 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState3 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH));
			BlockState blockState4 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.EAST));
			BlockState blockState5 = this.method_15016(Blocks.field_10161.getDefaultState());
			BlockState blockState6 = this.method_15016(Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState7 = this.method_15016(Blocks.field_10620.getDefaultState());
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 7, 5, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 8, 0, 5, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 0, 8, 5, 5, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 6, 1, 8, 6, 4, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 7, 2, 8, 7, 3, blockState, blockState, false);

			for (int i = -1; i <= 2; i++) {
				for (int j = 0; j <= 8; j++) {
					this.addBlock(iWorld, blockState2, j, 6 + i, i, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState3, j, 6 + i, 5 - i, mutableIntBoundingBox);
				}
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 0, 0, 1, 5, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 5, 8, 1, 5, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 1, 0, 8, 1, 4, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 1, 0, 7, 1, 0, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 0, 4, 0, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 5, 0, 4, 5, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 2, 5, 8, 4, 5, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 2, 0, 8, 4, 0, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 1, 0, 4, 4, blockState5, blockState5, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 5, 7, 4, 5, blockState5, blockState5, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 2, 1, 8, 4, 4, blockState5, blockState5, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 0, 7, 4, 0, blockState5, blockState5, false);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				4,
				2,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				5,
				2,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				6,
				2,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				4,
				3,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				5,
				3,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				6,
				3,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				2,
				3,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				3,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				3,
				3,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				8,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				8,
				2,
				3,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				8,
				3,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				8,
				3,
				3,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				2,
				2,
				5,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				3,
				2,
				5,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				5,
				2,
				5,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				6,
				2,
				5,
				mutableIntBoundingBox
			);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 1, 7, 4, 1, blockState5, blockState5, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 4, 7, 4, 4, blockState5, blockState5, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 4, 7, 3, 4, Blocks.field_10504.getDefaultState(), Blocks.field_10504.getDefaultState(), false);
			this.addBlock(iWorld, blockState5, 7, 1, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 7, 1, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 6, 1, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 5, 1, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 4, 1, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 3, 1, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7, 6, 1, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10484.getDefaultState(), 6, 2, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7, 4, 1, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10484.getDefaultState(), 4, 2, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_9980.getDefaultState(), 7, 1, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 1, 1, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 1, 2, 0, mutableIntBoundingBox);
			this.method_15018(iWorld, mutableIntBoundingBox, random, 1, 1, 0, Direction.NORTH);
			if (this.getBlockAt(iWorld, 1, 0, -1, mutableIntBoundingBox).isAir() && !this.getBlockAt(iWorld, 1, -1, -1, mutableIntBoundingBox).isAir()) {
				this.addBlock(iWorld, blockState6, 1, 0, -1, mutableIntBoundingBox);
				if (this.getBlockAt(iWorld, 1, -1, -1, mutableIntBoundingBox).getBlock() == Blocks.field_10194) {
					this.addBlock(iWorld, Blocks.field_10219.getDefaultState(), 1, -1, -1, mutableIntBoundingBox);
				}
			}

			for (int j = 0; j < 6; j++) {
				for (int k = 0; k < 9; k++) {
					this.method_14920(iWorld, k, 9, j, mutableIntBoundingBox);
					this.method_14936(iWorld, blockState, k, -1, j, mutableIntBoundingBox);
				}
			}

			this.method_15010(iWorld, mutableIntBoundingBox, 2, 1, 2, 1);
			return true;
		}
	}

	public static class class_3452 extends VillageGenerator.class_3465 {
		private BlockState field_15332;
		private BlockState field_15334;
		private BlockState field_15333;
		private BlockState field_15331;

		public class_3452(VillageGenerator.class_3461 arg, int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16956, arg, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
			this.field_15332 = VillageGenerator.class_3453.method_14991(random);
			this.field_15334 = VillageGenerator.class_3453.method_14991(random);
			this.field_15333 = VillageGenerator.class_3453.method_14991(random);
			this.field_15331 = VillageGenerator.class_3453.method_14991(random);
		}

		public class_3452(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16956, compoundTag);
			this.field_15332 = TagHelper.deserializeBlockState(compoundTag.getCompound("CA"));
			this.field_15334 = TagHelper.deserializeBlockState(compoundTag.getCompound("CB"));
			this.field_15333 = TagHelper.deserializeBlockState(compoundTag.getCompound("CC"));
			this.field_15331 = TagHelper.deserializeBlockState(compoundTag.getCompound("CD"));
			if (!(this.field_15332.getBlock() instanceof CropBlock)) {
				this.field_15332 = Blocks.field_10293.getDefaultState();
			}

			if (!(this.field_15334.getBlock() instanceof CropBlock)) {
				this.field_15334 = Blocks.field_10609.getDefaultState();
			}

			if (!(this.field_15333.getBlock() instanceof CropBlock)) {
				this.field_15333 = Blocks.field_10247.getDefaultState();
			}

			if (!(this.field_15331.getBlock() instanceof CropBlock)) {
				this.field_15331 = Blocks.field_10341.getDefaultState();
			}
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.put("CA", TagHelper.serializeBlockState(this.field_15332));
			compoundTag.put("CB", TagHelper.serializeBlockState(this.field_15334));
			compoundTag.put("CC", TagHelper.serializeBlockState(this.field_15333));
			compoundTag.put("CD", TagHelper.serializeBlockState(this.field_15331));
		}

		public static VillageGenerator.class_3452 method_14990(
			VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
		) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, 0, 0, 0, 13, 4, 9, direction);
			return method_15009(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new VillageGenerator.class_3452(arg, l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_15014(iWorld, mutableIntBoundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.structureBounds.translate(0, this.hPos - this.structureBounds.maxY + 4 - 1, 0);
			}

			BlockState blockState = this.method_15016(Blocks.field_10431.getDefaultState());
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 0, 12, 4, 8, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 1, 2, 0, 7, Blocks.field_10362.getDefaultState(), Blocks.field_10362.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 0, 1, 5, 0, 7, Blocks.field_10362.getDefaultState(), Blocks.field_10362.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 0, 1, 8, 0, 7, Blocks.field_10362.getDefaultState(), Blocks.field_10362.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 0, 1, 11, 0, 7, Blocks.field_10362.getDefaultState(), Blocks.field_10362.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 0, 0, 8, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 0, 0, 6, 0, 8, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 0, 0, 12, 0, 8, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 0, 11, 0, 0, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 8, 11, 0, 8, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 0, 1, 3, 0, 7, Blocks.field_10382.getDefaultState(), Blocks.field_10382.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 0, 1, 9, 0, 7, Blocks.field_10382.getDefaultState(), Blocks.field_10382.getDefaultState(), false);

			for (int i = 1; i <= 7; i++) {
				CropBlock cropBlock = (CropBlock)this.field_15332.getBlock();
				int j = cropBlock.getCropAgeMaximum();
				int k = j / 3;
				this.addBlock(iWorld, this.field_15332.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, k, j))), 1, 1, i, mutableIntBoundingBox);
				this.addBlock(iWorld, this.field_15332.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, k, j))), 2, 1, i, mutableIntBoundingBox);
				cropBlock = (CropBlock)this.field_15334.getBlock();
				int l = cropBlock.getCropAgeMaximum();
				int m = l / 3;
				this.addBlock(iWorld, this.field_15334.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, m, l))), 4, 1, i, mutableIntBoundingBox);
				this.addBlock(iWorld, this.field_15334.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, m, l))), 5, 1, i, mutableIntBoundingBox);
				cropBlock = (CropBlock)this.field_15333.getBlock();
				int n = cropBlock.getCropAgeMaximum();
				int o = n / 3;
				this.addBlock(iWorld, this.field_15333.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, o, n))), 7, 1, i, mutableIntBoundingBox);
				this.addBlock(iWorld, this.field_15333.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, o, n))), 8, 1, i, mutableIntBoundingBox);
				cropBlock = (CropBlock)this.field_15331.getBlock();
				int p = cropBlock.getCropAgeMaximum();
				int q = p / 3;
				this.addBlock(iWorld, this.field_15331.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, q, p))), 10, 1, i, mutableIntBoundingBox);
				this.addBlock(iWorld, this.field_15331.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, q, p))), 11, 1, i, mutableIntBoundingBox);
			}

			for (int i = 0; i < 9; i++) {
				for (int r = 0; r < 13; r++) {
					this.method_14920(iWorld, r, 4, i, mutableIntBoundingBox);
					this.method_14936(iWorld, Blocks.field_10566.getDefaultState(), r, -1, i, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class class_3453 extends VillageGenerator.class_3465 {
		private BlockState field_15335;
		private BlockState field_15336;

		public class_3453(VillageGenerator.class_3461 arg, int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16964, arg, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
			this.field_15335 = method_14991(random);
			this.field_15336 = method_14991(random);
		}

		public class_3453(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16964, compoundTag);
			this.field_15335 = TagHelper.deserializeBlockState(compoundTag.getCompound("CA"));
			this.field_15336 = TagHelper.deserializeBlockState(compoundTag.getCompound("CB"));
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.put("CA", TagHelper.serializeBlockState(this.field_15335));
			compoundTag.put("CB", TagHelper.serializeBlockState(this.field_15336));
		}

		private static BlockState method_14991(Random random) {
			switch (random.nextInt(10)) {
				case 0:
				case 1:
					return Blocks.field_10609.getDefaultState();
				case 2:
				case 3:
					return Blocks.field_10247.getDefaultState();
				case 4:
					return Blocks.field_10341.getDefaultState();
				default:
					return Blocks.field_10293.getDefaultState();
			}
		}

		public static VillageGenerator.class_3453 method_14993(
			VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
		) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, 0, 0, 0, 7, 4, 9, direction);
			return method_15009(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new VillageGenerator.class_3453(arg, l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_15014(iWorld, mutableIntBoundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.structureBounds.translate(0, this.hPos - this.structureBounds.maxY + 4 - 1, 0);
			}

			BlockState blockState = this.method_15016(Blocks.field_10431.getDefaultState());
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 0, 6, 4, 8, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 1, 2, 0, 7, Blocks.field_10362.getDefaultState(), Blocks.field_10362.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 0, 1, 5, 0, 7, Blocks.field_10362.getDefaultState(), Blocks.field_10362.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 0, 0, 8, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 0, 0, 6, 0, 8, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 0, 5, 0, 0, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 8, 5, 0, 8, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 0, 1, 3, 0, 7, Blocks.field_10382.getDefaultState(), Blocks.field_10382.getDefaultState(), false);

			for (int i = 1; i <= 7; i++) {
				CropBlock cropBlock = (CropBlock)this.field_15335.getBlock();
				int j = cropBlock.getCropAgeMaximum();
				int k = j / 3;
				this.addBlock(iWorld, this.field_15335.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, k, j))), 1, 1, i, mutableIntBoundingBox);
				this.addBlock(iWorld, this.field_15335.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, k, j))), 2, 1, i, mutableIntBoundingBox);
				cropBlock = (CropBlock)this.field_15336.getBlock();
				int l = cropBlock.getCropAgeMaximum();
				int m = l / 3;
				this.addBlock(iWorld, this.field_15336.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, m, l))), 4, 1, i, mutableIntBoundingBox);
				this.addBlock(iWorld, this.field_15336.with(cropBlock.getAgeProperty(), Integer.valueOf(MathHelper.nextInt(random, m, l))), 5, 1, i, mutableIntBoundingBox);
			}

			for (int i = 0; i < 9; i++) {
				for (int n = 0; n < 7; n++) {
					this.method_14920(iWorld, n, 4, i, mutableIntBoundingBox);
					this.method_14936(iWorld, Blocks.field_10566.getDefaultState(), n, -1, i, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class class_3454 extends VillageGenerator.class_3465 {
		public class_3454(VillageGenerator.class_3461 arg, int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16923, arg, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3454(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16923, compoundTag);
		}

		public static MutableIntBoundingBox method_14994(List<class_3443> list, int i, int j, int k, Direction direction) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, 0, 0, 0, 3, 4, 2, direction);
			return class_3443.method_14932(list, mutableIntBoundingBox) != null ? null : mutableIntBoundingBox;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_15014(iWorld, mutableIntBoundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.structureBounds.translate(0, this.hPos - this.structureBounds.maxY + 4 - 1, 0);
			}

			BlockState blockState = this.method_15016(Blocks.field_10620.getDefaultState());
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 2, 3, 1, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.addBlock(iWorld, blockState, 1, 0, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 1, 1, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 1, 2, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10146.getDefaultState(), 1, 3, 0, mutableIntBoundingBox);
			this.method_15019(iWorld, Direction.EAST, 2, 3, 0, mutableIntBoundingBox);
			this.method_15019(iWorld, Direction.NORTH, 1, 3, 1, mutableIntBoundingBox);
			this.method_15019(iWorld, Direction.WEST, 0, 3, 0, mutableIntBoundingBox);
			this.method_15019(iWorld, Direction.SOUTH, 1, 3, -1, mutableIntBoundingBox);
			return true;
		}
	}

	public static class class_3455 {
		public Class<? extends VillageGenerator.class_3465> field_15338;
		public final int field_15340;
		public int field_15339;
		public int field_15337;

		public class_3455(Class<? extends VillageGenerator.class_3465> class_, int i, int j) {
			this.field_15338 = class_;
			this.field_15340 = i;
			this.field_15337 = j;
		}

		public boolean method_14996(int i) {
			return this.field_15337 == 0 || this.field_15339 < this.field_15337;
		}

		public boolean method_14995() {
			return this.field_15337 == 0 || this.field_15339 < this.field_15337;
		}
	}

	public static class class_3456 extends VillageGenerator.class_3465 {
		public class_3456(VillageGenerator.class_3461 arg, int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16916, arg, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3456(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16916, compoundTag);
		}

		public static VillageGenerator.class_3456 method_14997(
			VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
		) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, 0, 0, 0, 9, 7, 11, direction);
			return method_15009(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new VillageGenerator.class_3456(arg, l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_15014(iWorld, mutableIntBoundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.structureBounds.translate(0, this.hPos - this.structureBounds.maxY + 7 - 1, 0);
			}

			BlockState blockState = this.method_15016(Blocks.field_10445.getDefaultState());
			BlockState blockState2 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState3 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH));
			BlockState blockState4 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.WEST));
			BlockState blockState5 = this.method_15016(Blocks.field_10161.getDefaultState());
			BlockState blockState6 = this.method_15016(Blocks.field_10431.getDefaultState());
			BlockState blockState7 = this.method_15016(Blocks.field_10620.getDefaultState());
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 7, 4, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 1, 6, 8, 4, 10, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 0, 6, 8, 0, 10, Blocks.field_10566.getDefaultState(), Blocks.field_10566.getDefaultState(), false);
			this.addBlock(iWorld, blockState, 6, 0, 6, mutableIntBoundingBox);
			BlockState blockState8 = blockState7.with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState9 = blockState7.with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 1, 6, 2, 1, 9, blockState8, blockState8, false);
			this.addBlock(
				iWorld, blockState7.with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)), 2, 1, 10, mutableIntBoundingBox
			);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 1, 6, 8, 1, 9, blockState8, blockState8, false);
			this.addBlock(
				iWorld, blockState7.with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)), 8, 1, 10, mutableIntBoundingBox
			);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 10, 7, 1, 10, blockState9, blockState9, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 1, 7, 0, 4, blockState5, blockState5, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 0, 3, 5, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 0, 0, 8, 3, 5, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 0, 7, 1, 0, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 5, 7, 1, 5, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 0, 7, 3, 0, blockState5, blockState5, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 5, 7, 3, 5, blockState5, blockState5, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 1, 8, 4, 1, blockState5, blockState5, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 4, 8, 4, 4, blockState5, blockState5, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 2, 8, 5, 3, blockState5, blockState5, false);
			this.addBlock(iWorld, blockState5, 0, 4, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5, 0, 4, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5, 8, 4, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5, 8, 4, 3, mutableIntBoundingBox);
			BlockState blockState10 = blockState2;
			BlockState blockState11 = blockState3;

			for (int i = -1; i <= 2; i++) {
				for (int j = 0; j <= 8; j++) {
					this.addBlock(iWorld, blockState10, j, 4 + i, i, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState11, j, 4 + i, 5 - i, mutableIntBoundingBox);
				}
			}

			this.addBlock(iWorld, blockState6, 0, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 0, 2, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 8, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 8, 2, 4, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				2,
				3,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				8,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				8,
				2,
				3,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				2,
				2,
				5,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				3,
				2,
				5,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				5,
				2,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(iWorld, blockState7, 2, 1, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10484.getDefaultState(), 2, 2, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5, 1, 1, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState10, 2, 1, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 1, 1, 3, mutableIntBoundingBox);
			BlockState blockState13 = Blocks.field_10136.getDefaultState().with(SlabBlock.field_11501, SlabType.field_12682);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 0, 1, 7, 0, 3, blockState13, blockState13, false);
			this.addBlock(iWorld, blockState13, 6, 1, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState13, 6, 1, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 2, 1, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 2, 2, 0, mutableIntBoundingBox);
			this.method_15019(iWorld, Direction.NORTH, 2, 3, 1, mutableIntBoundingBox);
			this.method_15018(iWorld, mutableIntBoundingBox, random, 2, 1, 0, Direction.NORTH);
			if (this.getBlockAt(iWorld, 2, 0, -1, mutableIntBoundingBox).isAir() && !this.getBlockAt(iWorld, 2, -1, -1, mutableIntBoundingBox).isAir()) {
				this.addBlock(iWorld, blockState10, 2, 0, -1, mutableIntBoundingBox);
				if (this.getBlockAt(iWorld, 2, -1, -1, mutableIntBoundingBox).getBlock() == Blocks.field_10194) {
					this.addBlock(iWorld, Blocks.field_10219.getDefaultState(), 2, -1, -1, mutableIntBoundingBox);
				}
			}

			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 6, 1, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 6, 2, 5, mutableIntBoundingBox);
			this.method_15019(iWorld, Direction.SOUTH, 6, 3, 4, mutableIntBoundingBox);
			this.method_15018(iWorld, mutableIntBoundingBox, random, 6, 1, 5, Direction.SOUTH);

			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < 9; k++) {
					this.method_14920(iWorld, k, 7, j, mutableIntBoundingBox);
					this.method_14936(iWorld, blockState, k, -1, j, mutableIntBoundingBox);
				}
			}

			this.method_15010(iWorld, mutableIntBoundingBox, 4, 1, 2, 2);
			return true;
		}
	}

	public static class class_3457 extends VillageGenerator.class_3465 {
		private boolean field_15341;

		public class_3457(VillageGenerator.class_3461 arg, int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16912, arg, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
			this.field_15341 = random.nextBoolean();
		}

		public class_3457(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16912, compoundTag);
			this.field_15341 = compoundTag.getBoolean("Terrace");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Terrace", this.field_15341);
		}

		public static VillageGenerator.class_3457 method_14998(
			VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
		) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, 0, 0, 0, 5, 6, 5, direction);
			return class_3443.method_14932(list, mutableIntBoundingBox) != null
				? null
				: new VillageGenerator.class_3457(arg, l, random, mutableIntBoundingBox, direction);
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_15014(iWorld, mutableIntBoundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.structureBounds.translate(0, this.hPos - this.structureBounds.maxY + 6 - 1, 0);
			}

			BlockState blockState = this.method_15016(Blocks.field_10445.getDefaultState());
			BlockState blockState2 = this.method_15016(Blocks.field_10161.getDefaultState());
			BlockState blockState3 = this.method_15016(Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState4 = this.method_15016(Blocks.field_10431.getDefaultState());
			BlockState blockState5 = this.method_15016(Blocks.field_10620.getDefaultState());
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 0, 4, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 0, 4, 4, 4, blockState4, blockState4, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 1, 3, 4, 3, blockState2, blockState2, false);
			this.addBlock(iWorld, blockState, 0, 1, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 0, 2, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 0, 3, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 4, 1, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 4, 2, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 4, 3, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 0, 1, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 0, 2, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 0, 3, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 4, 1, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 4, 2, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 4, 3, 4, mutableIntBoundingBox);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 1, 0, 3, 3, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 3, 3, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 4, 3, 3, 4, blockState2, blockState2, false);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				2,
				2,
				4,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				4,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(iWorld, blockState2, 1, 1, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 1, 2, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 1, 3, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 2, 3, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 3, 3, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 3, 2, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 3, 1, 0, mutableIntBoundingBox);
			if (this.getBlockAt(iWorld, 2, 0, -1, mutableIntBoundingBox).isAir() && !this.getBlockAt(iWorld, 2, -1, -1, mutableIntBoundingBox).isAir()) {
				this.addBlock(iWorld, blockState3, 2, 0, -1, mutableIntBoundingBox);
				if (this.getBlockAt(iWorld, 2, -1, -1, mutableIntBoundingBox).getBlock() == Blocks.field_10194) {
					this.addBlock(iWorld, Blocks.field_10219.getDefaultState(), 2, -1, -1, mutableIntBoundingBox);
				}
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 3, 3, 3, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			if (this.field_15341) {
				int i = 0;
				int j = 4;

				for (int k = 0; k <= 4; k++) {
					for (int l = 0; l <= 4; l++) {
						boolean bl = k == 0 || k == 4;
						boolean bl2 = l == 0 || l == 4;
						if (bl || bl2) {
							boolean bl3 = k == 0 || k == 4;
							boolean bl4 = l == 0 || l == 4;
							BlockState blockState6 = blockState5.with(FenceBlock.SOUTH, Boolean.valueOf(bl3 && l != 0))
								.with(FenceBlock.NORTH, Boolean.valueOf(bl3 && l != 4))
								.with(FenceBlock.WEST, Boolean.valueOf(bl4 && k != 0))
								.with(FenceBlock.EAST, Boolean.valueOf(bl4 && k != 4));
							this.addBlock(iWorld, blockState6, k, 5, l, mutableIntBoundingBox);
						}
					}
				}
			}

			if (this.field_15341) {
				BlockState blockState7 = Blocks.field_9983.getDefaultState().with(LadderBlock.field_11253, Direction.SOUTH);
				this.addBlock(iWorld, blockState7, 3, 1, 3, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 3, 2, 3, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 3, 3, 3, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 3, 4, 3, mutableIntBoundingBox);
			}

			this.method_15019(iWorld, Direction.NORTH, 2, 3, 1, mutableIntBoundingBox);

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					this.method_14920(iWorld, j, 6, i, mutableIntBoundingBox);
					this.method_14936(iWorld, blockState, j, -1, i, mutableIntBoundingBox);
				}
			}

			this.method_15010(iWorld, mutableIntBoundingBox, 1, 1, 2, 1);
			return true;
		}
	}

	public static class class_3458 extends VillageGenerator.class_3465 {
		private boolean field_15343;
		private int field_15342;

		public class_3458(VillageGenerator.class_3461 arg, int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16940, arg, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
			this.field_15343 = random.nextBoolean();
			this.field_15342 = random.nextInt(3);
		}

		public class_3458(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16940, compoundTag);
			this.field_15342 = compoundTag.getInt("T");
			this.field_15343 = compoundTag.getBoolean("C");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putInt("T", this.field_15342);
			compoundTag.putBoolean("C", this.field_15343);
		}

		public static VillageGenerator.class_3458 method_14999(
			VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
		) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, 0, 0, 0, 4, 6, 5, direction);
			return method_15009(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new VillageGenerator.class_3458(arg, l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_15014(iWorld, mutableIntBoundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.structureBounds.translate(0, this.hPos - this.structureBounds.maxY + 6 - 1, 0);
			}

			BlockState blockState = this.method_15016(Blocks.field_10445.getDefaultState());
			BlockState blockState2 = this.method_15016(Blocks.field_10161.getDefaultState());
			BlockState blockState3 = this.method_15016(Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState4 = this.method_15016(Blocks.field_10431.getDefaultState());
			BlockState blockState5 = this.method_15016(Blocks.field_10620.getDefaultState());
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 3, 5, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 3, 0, 4, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 1, 2, 0, 3, Blocks.field_10566.getDefaultState(), Blocks.field_10566.getDefaultState(), false);
			if (this.field_15343) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 1, 2, 4, 3, blockState4, blockState4, false);
			} else {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 5, 1, 2, 5, 3, blockState4, blockState4, false);
			}

			this.addBlock(iWorld, blockState4, 1, 4, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 2, 4, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 1, 4, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 2, 4, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 0, 4, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 0, 4, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 0, 4, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 3, 4, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 3, 4, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 3, 4, 3, mutableIntBoundingBox);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 0, 0, 3, 0, blockState4, blockState4, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 0, 3, 3, 0, blockState4, blockState4, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 4, 0, 3, 4, blockState4, blockState4, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 4, 3, 3, 4, blockState4, blockState4, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 1, 0, 3, 3, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 1, 3, 3, 3, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 0, 2, 3, 0, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 4, 2, 3, 4, blockState2, blockState2, false);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				3,
				2,
				2,
				mutableIntBoundingBox
			);
			if (this.field_15342 > 0) {
				this.addBlock(
					iWorld,
					blockState5.with(FenceBlock.NORTH, Boolean.valueOf(true)).with(this.field_15342 == 1 ? FenceBlock.WEST : FenceBlock.EAST, Boolean.valueOf(true)),
					this.field_15342,
					1,
					3,
					mutableIntBoundingBox
				);
				this.addBlock(iWorld, Blocks.field_10484.getDefaultState(), this.field_15342, 2, 3, mutableIntBoundingBox);
			}

			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 1, 1, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 1, 2, 0, mutableIntBoundingBox);
			this.method_15018(iWorld, mutableIntBoundingBox, random, 1, 1, 0, Direction.NORTH);
			if (this.getBlockAt(iWorld, 1, 0, -1, mutableIntBoundingBox).isAir() && !this.getBlockAt(iWorld, 1, -1, -1, mutableIntBoundingBox).isAir()) {
				this.addBlock(iWorld, blockState3, 1, 0, -1, mutableIntBoundingBox);
				if (this.getBlockAt(iWorld, 1, -1, -1, mutableIntBoundingBox).getBlock() == Blocks.field_10194) {
					this.addBlock(iWorld, Blocks.field_10219.getDefaultState(), 1, -1, -1, mutableIntBoundingBox);
				}
			}

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 4; j++) {
					this.method_14920(iWorld, j, 6, i, mutableIntBoundingBox);
					this.method_14936(iWorld, blockState, j, -1, i, mutableIntBoundingBox);
				}
			}

			this.method_15010(iWorld, mutableIntBoundingBox, 1, 1, 2, 1);
			return true;
		}
	}

	public static class class_3459 extends VillageGenerator.class_3465 {
		public class_3459(VillageGenerator.class_3461 arg, int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16947, arg, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3459(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16947, compoundTag);
		}

		public static VillageGenerator.class_3459 method_15000(
			VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
		) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, 0, 0, 0, 5, 12, 9, direction);
			return method_15009(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new VillageGenerator.class_3459(arg, l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_15014(iWorld, mutableIntBoundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.structureBounds.translate(0, this.hPos - this.structureBounds.maxY + 12 - 1, 0);
			}

			BlockState blockState = Blocks.field_10445.getDefaultState();
			BlockState blockState2 = this.method_15016(Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState3 = this.method_15016(Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.WEST));
			BlockState blockState4 = this.method_15016(Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.EAST));
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 3, 3, 7, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 5, 1, 3, 9, 3, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 0, 3, 0, 8, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 0, 3, 10, 0, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 1, 0, 10, 3, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 10, 3, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 4, 0, 4, 7, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 0, 4, 4, 4, 7, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 8, 3, 4, 8, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 5, 4, 3, 10, 4, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 5, 5, 3, 5, 7, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 9, 0, 4, 9, 4, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 0, 4, 4, 4, blockState, blockState, false);
			this.addBlock(iWorld, blockState, 0, 11, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 4, 11, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 2, 11, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 2, 11, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 1, 1, 6, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 1, 1, 7, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 2, 1, 7, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 3, 1, 6, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 3, 1, 7, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 1, 1, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 2, 1, 6, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 3, 1, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState3, 1, 2, 7, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 3, 2, 7, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				3,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				4,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				4,
				3,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				6,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				7,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				4,
				6,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				4,
				7,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				2,
				6,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				2,
				7,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				2,
				6,
				4,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				2,
				7,
				4,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				3,
				6,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				4,
				3,
				6,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				2,
				3,
				8,
				mutableIntBoundingBox
			);
			this.method_15019(iWorld, Direction.SOUTH, 2, 4, 7, mutableIntBoundingBox);
			this.method_15019(iWorld, Direction.EAST, 1, 4, 6, mutableIntBoundingBox);
			this.method_15019(iWorld, Direction.WEST, 3, 4, 6, mutableIntBoundingBox);
			this.method_15019(iWorld, Direction.NORTH, 2, 4, 5, mutableIntBoundingBox);
			BlockState blockState5 = Blocks.field_9983.getDefaultState().with(LadderBlock.field_11253, Direction.WEST);

			for (int i = 1; i <= 9; i++) {
				this.addBlock(iWorld, blockState5, 3, i, 3, mutableIntBoundingBox);
			}

			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 2, 1, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 2, 2, 0, mutableIntBoundingBox);
			this.method_15018(iWorld, mutableIntBoundingBox, random, 2, 1, 0, Direction.NORTH);
			if (this.getBlockAt(iWorld, 2, 0, -1, mutableIntBoundingBox).isAir() && !this.getBlockAt(iWorld, 2, -1, -1, mutableIntBoundingBox).isAir()) {
				this.addBlock(iWorld, blockState2, 2, 0, -1, mutableIntBoundingBox);
				if (this.getBlockAt(iWorld, 2, -1, -1, mutableIntBoundingBox).getBlock() == Blocks.field_10194) {
					this.addBlock(iWorld, Blocks.field_10219.getDefaultState(), 2, -1, -1, mutableIntBoundingBox);
				}
			}

			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 5; j++) {
					this.method_14920(iWorld, j, 12, i, mutableIntBoundingBox);
					this.method_14936(iWorld, blockState, j, -1, i, mutableIntBoundingBox);
				}
			}

			this.method_15010(iWorld, mutableIntBoundingBox, 2, 1, 2, 1);
			return true;
		}
	}

	public static class class_3460 extends VillageGenerator.class_3465 {
		private boolean field_15344;

		public class_3460(VillageGenerator.class_3461 arg, int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16951, arg, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3460(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16951, compoundTag);
			this.field_15344 = compoundTag.getBoolean("Chest");
		}

		public static VillageGenerator.class_3460 method_15001(
			VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
		) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, 0, 0, 0, 10, 6, 7, direction);
			return method_15009(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new VillageGenerator.class_3460(arg, l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Chest", this.field_15344);
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_15014(iWorld, mutableIntBoundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.structureBounds.translate(0, this.hPos - this.structureBounds.maxY + 6 - 1, 0);
			}

			BlockState blockState = Blocks.field_10445.getDefaultState();
			BlockState blockState2 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState3 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.WEST));
			BlockState blockState4 = this.method_15016(Blocks.field_10161.getDefaultState());
			BlockState blockState5 = this.method_15016(Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState6 = this.method_15016(Blocks.field_10431.getDefaultState());
			BlockState blockState7 = this.method_15016(Blocks.field_10620.getDefaultState());
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 0, 9, 4, 6, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 9, 0, 6, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 0, 9, 4, 6, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 0, 9, 5, 6, Blocks.field_10136.getDefaultState(), Blocks.field_10136.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 5, 1, 8, 5, 5, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 0, 2, 3, 0, blockState4, blockState4, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 0, 0, 4, 0, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 0, 3, 4, 0, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 6, 0, 4, 6, blockState6, blockState6, false);
			this.addBlock(iWorld, blockState4, 3, 3, 1, mutableIntBoundingBox);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 2, 3, 3, 2, blockState4, blockState4, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 3, 5, 3, 3, blockState4, blockState4, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 1, 0, 3, 5, blockState4, blockState4, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 6, 5, 3, 6, blockState4, blockState4, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 1, 0, 5, 3, 0, blockState7, blockState7, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 1, 0, 9, 3, 0, blockState7, blockState7, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 1, 4, 9, 4, 6, blockState, blockState, false);
			this.addBlock(iWorld, Blocks.field_10164.getDefaultState(), 7, 1, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10164.getDefaultState(), 8, 1, 5, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10576.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)),
				9,
				2,
				5,
				mutableIntBoundingBox
			);
			this.addBlock(iWorld, Blocks.field_10576.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)), 9, 2, 4, mutableIntBoundingBox);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 2, 4, 8, 2, 5, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.addBlock(iWorld, blockState, 6, 1, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10181.getDefaultState().with(FurnaceBlock.FACING, Direction.SOUTH), 6, 2, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10181.getDefaultState().with(FurnaceBlock.FACING, Direction.SOUTH), 6, 3, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.getDefaultState().with(SlabBlock.field_11501, SlabType.field_12682), 8, 1, 1, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				2,
				4,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				2,
				2,
				6,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				4,
				2,
				6,
				mutableIntBoundingBox
			);
			this.addBlock(iWorld, blockState7, 2, 1, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10484.getDefaultState(), 2, 2, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4, 1, 1, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 2, 1, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState3, 1, 1, 4, mutableIntBoundingBox);
			if (!this.field_15344 && mutableIntBoundingBox.contains(new BlockPos(this.applyXTransform(5, 5), this.applyYTransform(1), this.applyZTransform(5, 5)))) {
				this.field_15344 = true;
				this.method_14915(iWorld, mutableIntBoundingBox, random, 5, 1, 5, LootTables.CHEST_VILLAGE_BLACKSMITH);
			}

			for (int i = 6; i <= 8; i++) {
				if (this.getBlockAt(iWorld, i, 0, -1, mutableIntBoundingBox).isAir() && !this.getBlockAt(iWorld, i, -1, -1, mutableIntBoundingBox).isAir()) {
					this.addBlock(iWorld, blockState5, i, 0, -1, mutableIntBoundingBox);
					if (this.getBlockAt(iWorld, i, -1, -1, mutableIntBoundingBox).getBlock() == Blocks.field_10194) {
						this.addBlock(iWorld, Blocks.field_10219.getDefaultState(), i, -1, -1, mutableIntBoundingBox);
					}
				}
			}

			for (int ix = 0; ix < 7; ix++) {
				for (int j = 0; j < 10; j++) {
					this.method_14920(iWorld, j, 6, ix, mutableIntBoundingBox);
					this.method_14936(iWorld, blockState, j, -1, ix, mutableIntBoundingBox);
				}
			}

			this.method_15010(iWorld, mutableIntBoundingBox, 7, 1, 1, 1);
			return true;
		}
	}

	public static class class_3461 extends VillageGenerator.class_3467 {
		public int field_15347;
		public VillageGenerator.class_3455 field_15348;
		public List<VillageGenerator.class_3455> field_15345;
		public List<class_3443> field_15346 = Lists.<class_3443>newArrayList();
		public List<class_3443> field_15349 = Lists.<class_3443>newArrayList();

		public class_3461(Random random, int i, int j, List<VillageGenerator.class_3455> list, VillageFeatureConfig villageFeatureConfig) {
			super(StructurePiece.field_16942, null, 0, random, i, j);
			this.field_15345 = list;
			this.field_15347 = villageFeatureConfig.sizeModifier;
			this.type = villageFeatureConfig.type;
			this.method_15012(this.type);
			this.field_15360 = random.nextInt(50) == 0;
		}

		public class_3461(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16942, compoundTag);
		}
	}

	public static class class_3462 extends VillageGenerator.class_3465 {
		private int field_15350;

		public class_3462(VillageGenerator.class_3461 arg, int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16910, arg, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
			this.field_15350 = Math.max(mutableIntBoundingBox.getBlockCountX(), mutableIntBoundingBox.getBlockCountZ());
		}

		public class_3462(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16910, compoundTag);
			this.field_15350 = compoundTag.getInt("Length");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putInt("Length", this.field_15350);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			boolean bl = false;

			for (int i = random.nextInt(5); i < this.field_15350 - 8; i += 2 + random.nextInt(5)) {
				class_3443 lv = this.method_15017((VillageGenerator.class_3461)arg, list, random, 0, i);
				if (lv != null) {
					i += Math.max(lv.structureBounds.getBlockCountX(), lv.structureBounds.getBlockCountZ());
					bl = true;
				}
			}

			for (int var7 = random.nextInt(5); var7 < this.field_15350 - 8; var7 += 2 + random.nextInt(5)) {
				class_3443 lv = this.method_15015((VillageGenerator.class_3461)arg, list, random, 0, var7);
				if (lv != null) {
					var7 += Math.max(lv.structureBounds.getBlockCountX(), lv.structureBounds.getBlockCountZ());
					bl = true;
				}
			}

			Direction direction = this.getFacing();
			if (bl && random.nextInt(3) > 0 && direction != null) {
				switch (direction) {
					case NORTH:
					default:
						VillageGenerator.method_14987(
							(VillageGenerator.class_3461)arg,
							list,
							random,
							this.structureBounds.minX - 1,
							this.structureBounds.minY,
							this.structureBounds.minZ,
							Direction.WEST,
							this.method_14923()
						);
						break;
					case SOUTH:
						VillageGenerator.method_14987(
							(VillageGenerator.class_3461)arg,
							list,
							random,
							this.structureBounds.minX - 1,
							this.structureBounds.minY,
							this.structureBounds.maxZ - 2,
							Direction.WEST,
							this.method_14923()
						);
						break;
					case WEST:
						VillageGenerator.method_14987(
							(VillageGenerator.class_3461)arg,
							list,
							random,
							this.structureBounds.minX,
							this.structureBounds.minY,
							this.structureBounds.minZ - 1,
							Direction.NORTH,
							this.method_14923()
						);
						break;
					case EAST:
						VillageGenerator.method_14987(
							(VillageGenerator.class_3461)arg,
							list,
							random,
							this.structureBounds.maxX - 2,
							this.structureBounds.minY,
							this.structureBounds.minZ - 1,
							Direction.NORTH,
							this.method_14923()
						);
				}
			}

			if (bl && random.nextInt(3) > 0 && direction != null) {
				switch (direction) {
					case NORTH:
					default:
						VillageGenerator.method_14987(
							(VillageGenerator.class_3461)arg,
							list,
							random,
							this.structureBounds.maxX + 1,
							this.structureBounds.minY,
							this.structureBounds.minZ,
							Direction.EAST,
							this.method_14923()
						);
						break;
					case SOUTH:
						VillageGenerator.method_14987(
							(VillageGenerator.class_3461)arg,
							list,
							random,
							this.structureBounds.maxX + 1,
							this.structureBounds.minY,
							this.structureBounds.maxZ - 2,
							Direction.EAST,
							this.method_14923()
						);
						break;
					case WEST:
						VillageGenerator.method_14987(
							(VillageGenerator.class_3461)arg,
							list,
							random,
							this.structureBounds.minX,
							this.structureBounds.minY,
							this.structureBounds.maxZ + 1,
							Direction.SOUTH,
							this.method_14923()
						);
						break;
					case EAST:
						VillageGenerator.method_14987(
							(VillageGenerator.class_3461)arg,
							list,
							random,
							this.structureBounds.maxX - 2,
							this.structureBounds.minY,
							this.structureBounds.maxZ + 1,
							Direction.SOUTH,
							this.method_14923()
						);
				}
			}
		}

		public static MutableIntBoundingBox method_15002(
			VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction
		) {
			for (int l = 7 * MathHelper.nextInt(random, 3, 5); l >= 7; l -= 7) {
				MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, 0, 0, 0, 3, 3, l, direction);
				if (class_3443.method_14932(list, mutableIntBoundingBox) == null) {
					return mutableIntBoundingBox;
				}
			}

			return null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			BlockState blockState = this.method_15016(Blocks.field_10194.getDefaultState());
			BlockState blockState2 = this.method_15016(Blocks.field_10161.getDefaultState());
			BlockState blockState3 = this.method_15016(Blocks.field_10255.getDefaultState());
			BlockState blockState4 = this.method_15016(Blocks.field_10445.getDefaultState());
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			this.structureBounds.minY = 1000;
			this.structureBounds.maxY = 0;

			for (int i = this.structureBounds.minX; i <= this.structureBounds.maxX; i++) {
				for (int j = this.structureBounds.minZ; j <= this.structureBounds.maxZ; j++) {
					mutable.set(i, 64, j);
					if (mutableIntBoundingBox.contains(mutable)) {
						int k = iWorld.getTop(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable.getX(), mutable.getZ());
						mutable.set(mutable.getX(), k, mutable.getZ()).setOffset(Direction.DOWN);
						if (mutable.getY() < iWorld.getSeaLevel()) {
							mutable.setY(iWorld.getSeaLevel() - 1);
						}

						while (mutable.getY() >= iWorld.getSeaLevel() - 1) {
							BlockState blockState5 = iWorld.getBlockState(mutable);
							Block block = blockState5.getBlock();
							if (block == Blocks.field_10219 && iWorld.isAir(mutable.up())) {
								iWorld.setBlockState(mutable, blockState, 2);
								break;
							}

							if (blockState5.getMaterial().isLiquid()) {
								iWorld.setBlockState(new BlockPos(mutable), blockState2, 2);
								break;
							}

							if (block == Blocks.field_10102
								|| block == Blocks.field_10534
								|| block == Blocks.field_9979
								|| block == Blocks.field_10292
								|| block == Blocks.field_10361
								|| block == Blocks.field_10344
								|| block == Blocks.field_10292
								|| block == Blocks.field_10361) {
								iWorld.setBlockState(mutable, blockState3, 2);
								iWorld.setBlockState(mutable.down(), blockState4, 2);
								break;
							}

							mutable.setOffset(Direction.DOWN);
						}

						this.structureBounds.minY = Math.min(this.structureBounds.minY, mutable.getY());
						this.structureBounds.maxY = Math.max(this.structureBounds.maxY, mutable.getY());
					}
				}
			}

			return true;
		}
	}

	public static class class_3463 extends VillageGenerator.class_3465 {
		public class_3463(VillageGenerator.class_3461 arg, int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16913, arg, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3463(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16913, compoundTag);
		}

		public static VillageGenerator.class_3463 method_15003(
			VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
		) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, 0, 0, 0, 9, 7, 12, direction);
			return method_15009(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new VillageGenerator.class_3463(arg, l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_15014(iWorld, mutableIntBoundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.structureBounds.translate(0, this.hPos - this.structureBounds.maxY + 7 - 1, 0);
			}

			BlockState blockState = this.method_15016(Blocks.field_10445.getDefaultState());
			BlockState blockState2 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.NORTH));
			BlockState blockState3 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH));
			BlockState blockState4 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.EAST));
			BlockState blockState5 = this.method_15016(Blocks.field_10563.getDefaultState().with(StairsBlock.FACING, Direction.WEST));
			BlockState blockState6 = this.method_15016(Blocks.field_10161.getDefaultState());
			BlockState blockState7 = this.method_15016(Blocks.field_10431.getDefaultState());
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 7, 4, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 1, 6, 8, 4, 10, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 0, 5, 8, 0, 10, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 1, 7, 0, 4, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 0, 3, 5, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 0, 0, 8, 3, 10, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 0, 7, 2, 0, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 5, 2, 1, 5, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 0, 6, 2, 3, 10, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 0, 10, 7, 3, 10, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 0, 7, 3, 0, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 5, 2, 3, 5, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 1, 8, 4, 1, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 4, 3, 4, 4, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 2, 8, 5, 3, blockState6, blockState6, false);
			this.addBlock(iWorld, blockState6, 0, 4, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 0, 4, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 8, 4, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 8, 4, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 8, 4, 4, mutableIntBoundingBox);
			BlockState blockState8 = blockState2;
			BlockState blockState9 = blockState3;
			BlockState blockState10 = blockState5;
			BlockState blockState11 = blockState4;

			for (int i = -1; i <= 2; i++) {
				for (int j = 0; j <= 8; j++) {
					this.addBlock(iWorld, blockState8, j, 4 + i, i, mutableIntBoundingBox);
					if ((i > -1 || j <= 1) && (i > 0 || j <= 3) && (i > 1 || j <= 4 || j >= 6)) {
						this.addBlock(iWorld, blockState9, j, 4 + i, 5 - i, mutableIntBoundingBox);
					}
				}
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 4, 5, 3, 4, 10, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 4, 2, 7, 4, 10, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 4, 4, 5, 10, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 5, 4, 6, 5, 10, blockState6, blockState6, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 6, 3, 5, 6, 10, blockState6, blockState6, false);

			for (int i = 4; i >= 1; i--) {
				this.addBlock(iWorld, blockState6, i, 2 + i, 7 - i, mutableIntBoundingBox);

				for (int jx = 8 - i; jx <= 10; jx++) {
					this.addBlock(iWorld, blockState11, i, 2 + i, jx, mutableIntBoundingBox);
				}
			}

			this.addBlock(iWorld, blockState6, 6, 6, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 7, 5, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5, 6, 6, 4, mutableIntBoundingBox);

			for (int i = 6; i <= 8; i++) {
				for (int jx = 5; jx <= 10; jx++) {
					this.addBlock(iWorld, blockState10, i, 12 - i, jx, mutableIntBoundingBox);
				}
			}

			this.addBlock(iWorld, blockState7, 0, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7, 0, 2, 4, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				0,
				2,
				3,
				mutableIntBoundingBox
			);
			this.addBlock(iWorld, blockState7, 4, 2, 0, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				5,
				2,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(iWorld, blockState7, 6, 2, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7, 8, 2, 1, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				8,
				2,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				8,
				2,
				3,
				mutableIntBoundingBox
			);
			this.addBlock(iWorld, blockState7, 8, 2, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 8, 2, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7, 8, 2, 6, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				8,
				2,
				7,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				8,
				2,
				8,
				mutableIntBoundingBox
			);
			this.addBlock(iWorld, blockState7, 8, 2, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7, 2, 2, 6, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				2,
				2,
				7,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.SOUTH, Boolean.valueOf(true)).with(PaneBlock.NORTH, Boolean.valueOf(true)),
				2,
				2,
				8,
				mutableIntBoundingBox
			);
			this.addBlock(iWorld, blockState7, 2, 2, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7, 4, 4, 10, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
				5,
				4,
				10,
				mutableIntBoundingBox
			);
			this.addBlock(iWorld, blockState7, 6, 4, 10, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 5, 5, 10, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 2, 1, 0, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 2, 2, 0, mutableIntBoundingBox);
			this.method_15019(iWorld, Direction.NORTH, 2, 3, 1, mutableIntBoundingBox);
			this.method_15018(iWorld, mutableIntBoundingBox, random, 2, 1, 0, Direction.NORTH);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, -1, 3, 2, -1, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			if (this.getBlockAt(iWorld, 2, 0, -1, mutableIntBoundingBox).isAir() && !this.getBlockAt(iWorld, 2, -1, -1, mutableIntBoundingBox).isAir()) {
				this.addBlock(iWorld, blockState8, 2, 0, -1, mutableIntBoundingBox);
				if (this.getBlockAt(iWorld, 2, -1, -1, mutableIntBoundingBox).getBlock() == Blocks.field_10194) {
					this.addBlock(iWorld, Blocks.field_10219.getDefaultState(), 2, -1, -1, mutableIntBoundingBox);
				}
			}

			for (int i = 0; i < 5; i++) {
				for (int jx = 0; jx < 9; jx++) {
					this.method_14920(iWorld, jx, 7, i, mutableIntBoundingBox);
					this.method_14936(iWorld, blockState, jx, -1, i, mutableIntBoundingBox);
				}
			}

			for (int i = 5; i < 11; i++) {
				for (int jx = 2; jx < 9; jx++) {
					this.method_14920(iWorld, jx, 7, i, mutableIntBoundingBox);
					this.method_14936(iWorld, blockState, jx, -1, i, mutableIntBoundingBox);
				}
			}

			this.method_15010(iWorld, mutableIntBoundingBox, 4, 1, 2, 2);
			return true;
		}
	}

	abstract static class class_3465 extends class_3443 {
		protected int hPos = -1;
		private int vCount;
		protected VillageGenerator.Type type;
		protected boolean field_15360;

		protected class_3465(StructurePiece structurePiece, @Nullable VillageGenerator.class_3461 arg, int i) {
			super(structurePiece, i);
			if (arg != null) {
				this.type = arg.type;
				this.field_15360 = arg.field_15360;
			}
		}

		public class_3465(StructurePiece structurePiece, CompoundTag compoundTag) {
			super(structurePiece, compoundTag);
			this.hPos = compoundTag.getInt("HPos");
			this.vCount = compoundTag.getInt("VCount");
			this.type = VillageGenerator.Type.byIndex(compoundTag.getByte("Type"));
			if (compoundTag.getBoolean("Desert")) {
				this.type = VillageGenerator.Type.SANDSTONE;
			}

			this.field_15360 = compoundTag.getBoolean("Zombie");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			compoundTag.putInt("HPos", this.hPos);
			compoundTag.putInt("VCount", this.vCount);
			compoundTag.putByte("Type", (byte)this.type.getIndex());
			compoundTag.putBoolean("Zombie", this.field_15360);
		}

		@Nullable
		protected class_3443 method_15017(VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
						return VillageGenerator.method_14988(
							arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY + i, this.structureBounds.minZ + j, Direction.WEST, this.method_14923()
						);
					case SOUTH:
						return VillageGenerator.method_14988(
							arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY + i, this.structureBounds.minZ + j, Direction.WEST, this.method_14923()
						);
					case WEST:
						return VillageGenerator.method_14988(
							arg, list, random, this.structureBounds.minX + j, this.structureBounds.minY + i, this.structureBounds.minZ - 1, Direction.NORTH, this.method_14923()
						);
					case EAST:
						return VillageGenerator.method_14988(
							arg, list, random, this.structureBounds.minX + j, this.structureBounds.minY + i, this.structureBounds.minZ - 1, Direction.NORTH, this.method_14923()
						);
				}
			} else {
				return null;
			}
		}

		@Nullable
		protected class_3443 method_15015(VillageGenerator.class_3461 arg, List<class_3443> list, Random random, int i, int j) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
						return VillageGenerator.method_14988(
							arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY + i, this.structureBounds.minZ + j, Direction.EAST, this.method_14923()
						);
					case SOUTH:
						return VillageGenerator.method_14988(
							arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY + i, this.structureBounds.minZ + j, Direction.EAST, this.method_14923()
						);
					case WEST:
						return VillageGenerator.method_14988(
							arg, list, random, this.structureBounds.minX + j, this.structureBounds.minY + i, this.structureBounds.maxZ + 1, Direction.SOUTH, this.method_14923()
						);
					case EAST:
						return VillageGenerator.method_14988(
							arg, list, random, this.structureBounds.minX + j, this.structureBounds.minY + i, this.structureBounds.maxZ + 1, Direction.SOUTH, this.method_14923()
						);
				}
			} else {
				return null;
			}
		}

		protected int method_15014(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox) {
			int i = 0;
			int j = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = this.structureBounds.minZ; k <= this.structureBounds.maxZ; k++) {
				for (int l = this.structureBounds.minX; l <= this.structureBounds.maxX; l++) {
					mutable.set(l, 64, k);
					if (mutableIntBoundingBox.contains(mutable)) {
						i += iWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY();
						j++;
					}
				}
			}

			return j == 0 ? -1 : i / j;
		}

		protected static boolean method_15009(MutableIntBoundingBox mutableIntBoundingBox) {
			return mutableIntBoundingBox != null && mutableIntBoundingBox.minY > 10;
		}

		protected void method_15010(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, int i, int j, int k, int l) {
			if (this.vCount < l) {
				for (int m = this.vCount; m < l; m++) {
					int n = this.applyXTransform(i + m, k);
					int o = this.applyYTransform(j);
					int p = this.applyZTransform(i + m, k);
					BlockPos blockPos = new BlockPos(n, o, p);
					if (!mutableIntBoundingBox.contains(blockPos)) {
						break;
					}

					this.vCount++;
					VillagerType villagerType = VillagerType.forBiome(iWorld.getBiome(blockPos));
					if (this.field_15360) {
						ZombieVillagerEntity zombieVillagerEntity = new ZombieVillagerEntity(iWorld.getWorld());
						zombieVillagerEntity.setPositionAndAngles((double)n + 0.5, (double)o, (double)p + 0.5, 0.0F, 0.0F);
						zombieVillagerEntity.setVillagerData(zombieVillagerEntity.getVillagerData().withType(villagerType));
						zombieVillagerEntity.prepareEntityData(iWorld, iWorld.getLocalDifficulty(new BlockPos(zombieVillagerEntity)), SpawnType.field_16474, null, null);
						zombieVillagerEntity.setPersistent();
						iWorld.spawnEntity(zombieVillagerEntity);
					} else {
						VillagerEntity villagerEntity = new VillagerEntity(iWorld.getWorld(), villagerType);
						villagerEntity.setPositionAndAngles((double)n + 0.5, (double)o, (double)p + 0.5, 0.0F, 0.0F);
						villagerEntity.prepareEntityData(iWorld, iWorld.getLocalDifficulty(new BlockPos(villagerEntity)), SpawnType.field_16474, null, null);
						iWorld.spawnEntity(villagerEntity);
					}
				}
			}
		}

		protected BlockState method_15016(BlockState blockState) {
			Block block = blockState.getBlock();
			if (this.type == VillageGenerator.Type.SANDSTONE) {
				if (block.matches(BlockTags.field_15475) || block == Blocks.field_10445) {
					return Blocks.field_9979.getDefaultState();
				}

				if (block.matches(BlockTags.field_15471)) {
					return Blocks.field_10361.getDefaultState();
				}

				if (block == Blocks.field_10563) {
					return Blocks.field_10142.getDefaultState().with(StairsBlock.FACING, blockState.get(StairsBlock.FACING));
				}

				if (block == Blocks.field_10596) {
					return Blocks.field_10142.getDefaultState().with(StairsBlock.FACING, blockState.get(StairsBlock.FACING));
				}

				if (block == Blocks.field_10255) {
					return Blocks.field_9979.getDefaultState();
				}

				if (block == Blocks.field_10484) {
					return Blocks.field_10592.getDefaultState();
				}
			} else if (this.type == VillageGenerator.Type.SPRUCE) {
				if (block.matches(BlockTags.field_15475)) {
					return Blocks.field_10037.getDefaultState().with(LogBlock.AXIS, blockState.get(LogBlock.AXIS));
				}

				if (block.matches(BlockTags.field_15471)) {
					return Blocks.field_9975.getDefaultState();
				}

				if (block == Blocks.field_10563) {
					return Blocks.field_10569.getDefaultState().with(StairsBlock.FACING, blockState.get(StairsBlock.FACING));
				}

				if (block == Blocks.field_10620) {
					return Blocks.field_10020.getDefaultState();
				}

				if (block == Blocks.field_10484) {
					return Blocks.field_10332.getDefaultState();
				}
			} else if (this.type == VillageGenerator.Type.ACACIA) {
				if (block.matches(BlockTags.field_15475)) {
					return Blocks.field_10533.getDefaultState().with(LogBlock.AXIS, blockState.get(LogBlock.AXIS));
				}

				if (block.matches(BlockTags.field_15471)) {
					return Blocks.field_10218.getDefaultState();
				}

				if (block == Blocks.field_10563) {
					return Blocks.field_10256.getDefaultState().with(StairsBlock.FACING, blockState.get(StairsBlock.FACING));
				}

				if (block == Blocks.field_10445) {
					return Blocks.field_10533.getDefaultState().with(LogBlock.AXIS, Direction.Axis.Y);
				}

				if (block == Blocks.field_10620) {
					return Blocks.field_10144.getDefaultState();
				}

				if (block == Blocks.field_10484) {
					return Blocks.field_10397.getDefaultState();
				}
			}

			return blockState;
		}

		protected DoorBlock method_15013() {
			if (this.type == VillageGenerator.Type.ACACIA) {
				return (DoorBlock)Blocks.field_10232;
			} else {
				return this.type == VillageGenerator.Type.SPRUCE ? (DoorBlock)Blocks.field_10521 : (DoorBlock)Blocks.field_10149;
			}
		}

		protected void method_15018(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, int i, int j, int k, Direction direction) {
			if (!this.field_15360) {
				this.addDoor(iWorld, mutableIntBoundingBox, random, i, j, k, Direction.NORTH, this.method_15013());
			}
		}

		protected void method_15019(IWorld iWorld, Direction direction, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox) {
			if (!this.field_15360) {
				this.addBlock(iWorld, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, direction), i, j, k, mutableIntBoundingBox);
			}
		}

		@Override
		protected void method_14936(IWorld iWorld, BlockState blockState, int i, int j, int k, MutableIntBoundingBox mutableIntBoundingBox) {
			BlockState blockState2 = this.method_15016(blockState);
			super.method_14936(iWorld, blockState2, i, j, k, mutableIntBoundingBox);
		}

		protected void method_15012(VillageGenerator.Type type) {
			this.type = type;
		}
	}

	public static class class_3467 extends VillageGenerator.class_3465 {
		public class_3467(StructurePiece structurePiece, @Nullable VillageGenerator.class_3461 arg, int i, Random random, int j, int k) {
			super(structurePiece, arg, i);
			this.method_14926(Direction.class_2353.HORIZONTAL.random(random));
			if (this.getFacing().getAxis() == Direction.Axis.Z) {
				this.structureBounds = new MutableIntBoundingBox(j, 64, k, j + 6 - 1, 78, k + 6 - 1);
			} else {
				this.structureBounds = new MutableIntBoundingBox(j, 64, k, j + 6 - 1, 78, k + 6 - 1);
			}
		}

		public class_3467(StructurePiece structurePiece, CompoundTag compoundTag) {
			super(structurePiece, compoundTag);
		}

		public class_3467(StructureManager structureManager, CompoundTag compoundTag) {
			this(StructurePiece.field_16938, compoundTag);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			VillageGenerator.method_14987(
				(VillageGenerator.class_3461)arg,
				list,
				random,
				this.structureBounds.minX - 1,
				this.structureBounds.maxY - 4,
				this.structureBounds.minZ + 1,
				Direction.WEST,
				this.method_14923()
			);
			VillageGenerator.method_14987(
				(VillageGenerator.class_3461)arg,
				list,
				random,
				this.structureBounds.maxX + 1,
				this.structureBounds.maxY - 4,
				this.structureBounds.minZ + 1,
				Direction.EAST,
				this.method_14923()
			);
			VillageGenerator.method_14987(
				(VillageGenerator.class_3461)arg,
				list,
				random,
				this.structureBounds.minX + 1,
				this.structureBounds.maxY - 4,
				this.structureBounds.minZ - 1,
				Direction.NORTH,
				this.method_14923()
			);
			VillageGenerator.method_14987(
				(VillageGenerator.class_3461)arg,
				list,
				random,
				this.structureBounds.minX + 1,
				this.structureBounds.maxY - 4,
				this.structureBounds.maxZ + 1,
				Direction.SOUTH,
				this.method_14923()
			);
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.hPos < 0) {
				this.hPos = this.method_15014(iWorld, mutableIntBoundingBox);
				if (this.hPos < 0) {
					return true;
				}

				this.structureBounds.translate(0, this.hPos - this.structureBounds.maxY + 3, 0);
			}

			BlockState blockState = this.method_15016(Blocks.field_10445.getDefaultState());
			BlockState blockState2 = this.method_15016(Blocks.field_10620.getDefaultState());
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 1, 4, 12, 4, blockState, Blocks.field_10382.getDefaultState(), false);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 2, 12, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 3, 12, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 2, 12, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 3, 12, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 1, 13, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 1, 14, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 4, 13, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 4, 14, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 1, 13, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 1, 14, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 4, 13, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 4, 14, 4, mutableIntBoundingBox);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 15, 1, 4, 15, 4, blockState, blockState, false);

			for (int i = 0; i <= 5; i++) {
				for (int j = 0; j <= 5; j++) {
					if (j == 0 || j == 5 || i == 0 || i == 5) {
						this.addBlock(iWorld, blockState, j, 11, i, mutableIntBoundingBox);
						this.method_14920(iWorld, j, 12, i, mutableIntBoundingBox);
					}
				}
			}

			return true;
		}
	}
}
