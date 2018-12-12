package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sortme.structures.processor.AbstractStructureProcessor;
import net.minecraft.util.IdList;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.AbstractVoxelShapeContainer;
import net.minecraft.util.shape.BitSetVoxelShapeContainer;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class class_3499 {
	private final List<List<class_3499.class_3501>> field_15586 = Lists.<List<class_3499.class_3501>>newArrayList();
	private final List<class_3499.class_3502> field_15589 = Lists.<class_3499.class_3502>newArrayList();
	private BlockPos field_15587 = BlockPos.ORIGIN;
	private String field_15588 = "?";

	public BlockPos method_15160() {
		return this.field_15587;
	}

	public void method_15161(String string) {
		this.field_15588 = string;
	}

	public String method_15181() {
		return this.field_15588;
	}

	public void method_15174(World world, BlockPos blockPos, BlockPos blockPos2, boolean bl, @Nullable Block block) {
		if (blockPos2.getX() >= 1 && blockPos2.getY() >= 1 && blockPos2.getZ() >= 1) {
			BlockPos blockPos3 = blockPos.add(blockPos2).add(-1, -1, -1);
			List<class_3499.class_3501> list = Lists.<class_3499.class_3501>newArrayList();
			List<class_3499.class_3501> list2 = Lists.<class_3499.class_3501>newArrayList();
			List<class_3499.class_3501> list3 = Lists.<class_3499.class_3501>newArrayList();
			BlockPos blockPos4 = new BlockPos(
				Math.min(blockPos.getX(), blockPos3.getX()), Math.min(blockPos.getY(), blockPos3.getY()), Math.min(blockPos.getZ(), blockPos3.getZ())
			);
			BlockPos blockPos5 = new BlockPos(
				Math.max(blockPos.getX(), blockPos3.getX()), Math.max(blockPos.getY(), blockPos3.getY()), Math.max(blockPos.getZ(), blockPos3.getZ())
			);
			this.field_15587 = blockPos2;

			for (BlockPos.Mutable mutable : BlockPos.iterateBoxPositionsMutable(blockPos4, blockPos5)) {
				BlockPos blockPos6 = mutable.subtract(blockPos4);
				BlockState blockState = world.getBlockState(mutable);
				if (block == null || block != blockState.getBlock()) {
					BlockEntity blockEntity = world.getBlockEntity(mutable);
					if (blockEntity != null) {
						CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
						compoundTag.remove("x");
						compoundTag.remove("y");
						compoundTag.remove("z");
						list2.add(new class_3499.class_3501(blockPos6, blockState, compoundTag));
					} else if (!blockState.isFullOpaque(world, mutable) && !blockState.method_11604(world, mutable)) {
						list3.add(new class_3499.class_3501(blockPos6, blockState, null));
					} else {
						list.add(new class_3499.class_3501(blockPos6, blockState, null));
					}
				}
			}

			List<class_3499.class_3501> list4 = Lists.<class_3499.class_3501>newArrayList();
			list4.addAll(list);
			list4.addAll(list2);
			list4.addAll(list3);
			this.field_15586.clear();
			this.field_15586.add(list4);
			if (bl) {
				this.method_15164(world, blockPos4, blockPos5.add(1, 1, 1));
			} else {
				this.field_15589.clear();
			}
		}
	}

	private void method_15164(World world, BlockPos blockPos, BlockPos blockPos2) {
		List<Entity> list = world.getEntities(Entity.class, new BoundingBox(blockPos, blockPos2), entityx -> !(entityx instanceof PlayerEntity));
		this.field_15589.clear();

		for (Entity entity : list) {
			Vec3d vec3d = new Vec3d(entity.x - (double)blockPos.getX(), entity.y - (double)blockPos.getY(), entity.z - (double)blockPos.getZ());
			CompoundTag compoundTag = new CompoundTag();
			entity.saveToTag(compoundTag);
			BlockPos blockPos3;
			if (entity instanceof PaintingEntity) {
				blockPos3 = ((PaintingEntity)entity).getDecorationBlockPos().subtract(blockPos);
			} else {
				blockPos3 = new BlockPos(vec3d);
			}

			this.field_15589.add(new class_3499.class_3502(vec3d, blockPos3, compoundTag));
		}
	}

	public List<class_3499.class_3501> method_16445(BlockPos blockPos, class_3492 arg, Block block) {
		return this.method_15165(blockPos, arg, block, true);
	}

	public List<class_3499.class_3501> method_15165(BlockPos blockPos, class_3492 arg, Block block, boolean bl) {
		List<class_3499.class_3501> list = Lists.<class_3499.class_3501>newArrayList();
		MutableIntBoundingBox mutableIntBoundingBox = arg.method_15124();

		for (class_3499.class_3501 lv : arg.method_15121(this.field_15586, blockPos)) {
			BlockPos blockPos2 = bl ? method_15171(arg, lv.field_15597).add(blockPos) : lv.field_15597;
			if (mutableIntBoundingBox == null || mutableIntBoundingBox.contains(blockPos2)) {
				BlockState blockState = lv.field_15596;
				if (blockState.getBlock() == block) {
					list.add(new class_3499.class_3501(blockPos2, blockState.applyRotation(arg.method_15113()), lv.field_15595));
				}
			}
		}

		return list;
	}

	public BlockPos method_15180(class_3492 arg, BlockPos blockPos, class_3492 arg2, BlockPos blockPos2) {
		BlockPos blockPos3 = method_15171(arg, blockPos);
		BlockPos blockPos4 = method_15171(arg2, blockPos2);
		return blockPos3.subtract(blockPos4);
	}

	public static BlockPos method_15171(class_3492 arg, BlockPos blockPos) {
		return method_15168(blockPos, arg.method_15114(), arg.method_15113(), arg.method_15134());
	}

	public void method_15182(IWorld iWorld, BlockPos blockPos, class_3492 arg) {
		arg.method_15132();
		this.method_15178(iWorld, blockPos, arg);
	}

	public void method_15178(IWorld iWorld, BlockPos blockPos, class_3492 arg) {
		this.method_15172(iWorld, blockPos, arg, 2);
	}

	public boolean method_15172(IWorld iWorld, BlockPos blockPos, class_3492 arg, int i) {
		if (this.field_15586.isEmpty()) {
			return false;
		} else {
			List<class_3499.class_3501> list = arg.method_15121(this.field_15586, blockPos);
			if ((!list.isEmpty() || !arg.method_15135() && !this.field_15589.isEmpty())
				&& this.field_15587.getX() >= 1
				&& this.field_15587.getY() >= 1
				&& this.field_15587.getZ() >= 1) {
				MutableIntBoundingBox mutableIntBoundingBox = arg.method_15124();
				List<BlockPos> list2 = Lists.<BlockPos>newArrayListWithCapacity(arg.method_15120() ? list.size() : 0);
				List<Pair<BlockPos, CompoundTag>> list3 = Lists.<Pair<BlockPos, CompoundTag>>newArrayListWithCapacity(list.size());
				int j = Integer.MAX_VALUE;
				int k = Integer.MAX_VALUE;
				int l = Integer.MAX_VALUE;
				int m = Integer.MIN_VALUE;
				int n = Integer.MIN_VALUE;
				int o = Integer.MIN_VALUE;

				for (class_3499.class_3501 lv : method_16446(iWorld, blockPos, arg, list)) {
					BlockPos blockPos2 = lv.field_15597;
					if (mutableIntBoundingBox == null || mutableIntBoundingBox.contains(blockPos2)) {
						FluidState fluidState = arg.method_15120() ? iWorld.getFluidState(blockPos2) : null;
						BlockState blockState = lv.field_15596.applyMirror(arg.method_15114()).applyRotation(arg.method_15113());
						if (lv.field_15595 != null) {
							BlockEntity blockEntity = iWorld.getBlockEntity(blockPos2);
							class_3829.method_16825(blockEntity);
							iWorld.setBlockState(blockPos2, Blocks.field_10499.getDefaultState(), 4);
						}

						if (iWorld.setBlockState(blockPos2, blockState, i)) {
							j = Math.min(j, blockPos2.getX());
							k = Math.min(k, blockPos2.getY());
							l = Math.min(l, blockPos2.getZ());
							m = Math.max(m, blockPos2.getX());
							n = Math.max(n, blockPos2.getY());
							o = Math.max(o, blockPos2.getZ());
							list3.add(Pair.of(blockPos2, lv.field_15595));
							if (lv.field_15595 != null) {
								BlockEntity blockEntity = iWorld.getBlockEntity(blockPos2);
								if (blockEntity != null) {
									lv.field_15595.putInt("x", blockPos2.getX());
									lv.field_15595.putInt("y", blockPos2.getY());
									lv.field_15595.putInt("z", blockPos2.getZ());
									blockEntity.fromTag(lv.field_15595);
									blockEntity.applyMirror(arg.method_15114());
									blockEntity.applyRotation(arg.method_15113());
								}
							}

							if (fluidState != null && blockState.getBlock() instanceof FluidFillable) {
								((FluidFillable)blockState.getBlock()).tryFillWithFluid(iWorld, blockPos2, blockState, fluidState);
								if (!fluidState.isStill()) {
									list2.add(blockPos2);
								}
							}
						}
					}
				}

				boolean bl = true;
				Direction[] directions = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

				while (bl && !list2.isEmpty()) {
					bl = false;
					Iterator<BlockPos> iterator = list2.iterator();

					while (iterator.hasNext()) {
						BlockPos blockPos3 = (BlockPos)iterator.next();
						FluidState fluidState2 = iWorld.getFluidState(blockPos3);

						for (int p = 0; p < directions.length && !fluidState2.isStill(); p++) {
							FluidState fluidState3 = iWorld.getFluidState(blockPos3.offset(directions[p]));
							if (fluidState3.method_15763() > fluidState2.method_15763() || fluidState3.isStill() && !fluidState2.isStill()) {
								fluidState2 = fluidState3;
							}
						}

						if (fluidState2.isStill()) {
							BlockState blockState2 = iWorld.getBlockState(blockPos3);
							if (blockState2.getBlock() instanceof FluidFillable) {
								((FluidFillable)blockState2.getBlock()).tryFillWithFluid(iWorld, blockPos3, blockState2, fluidState2);
								bl = true;
								iterator.remove();
							}
						}
					}
				}

				if (j <= m) {
					if (!arg.method_16444()) {
						AbstractVoxelShapeContainer abstractVoxelShapeContainer = new BitSetVoxelShapeContainer(m - j + 1, n - k + 1, o - l + 1);
						int q = j;
						int r = k;
						int px = l;

						for (Pair<BlockPos, CompoundTag> pair : list3) {
							BlockPos blockPos4 = pair.getFirst();
							abstractVoxelShapeContainer.modify(blockPos4.getX() - q, blockPos4.getY() - r, blockPos4.getZ() - px, true, true);
						}

						abstractVoxelShapeContainer.method_1046((direction, mx, nx, ox) -> {
							BlockPos blockPosx = new BlockPos(q + mx, r + nx, p + ox);
							BlockPos blockPos2 = blockPosx.offset(direction);
							BlockState blockStatex = iWorld.getBlockState(blockPosx);
							BlockState blockState2 = iWorld.getBlockState(blockPos2);
							BlockState blockState3x = blockStatex.getStateForNeighborUpdate(direction, blockState2, iWorld, blockPosx, blockPos2);
							if (blockStatex != blockState3x) {
								iWorld.setBlockState(blockPosx, blockState3x, i & -2 | 16);
							}

							BlockState blockState4 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), blockState3x, iWorld, blockPos2, blockPosx);
							if (blockState2 != blockState4) {
								iWorld.setBlockState(blockPos2, blockState4, i & -2 | 16);
							}
						});
					}

					for (Pair<BlockPos, CompoundTag> pair2 : list3) {
						BlockPos blockPos5 = pair2.getFirst();
						if (!arg.method_16444()) {
							BlockState blockState2 = iWorld.getBlockState(blockPos5);
							BlockState blockState3 = Block.getRenderingState(blockState2, iWorld, blockPos5);
							if (blockState2 != blockState3) {
								iWorld.setBlockState(blockPos5, blockState3, i & -2 | 16);
							}

							iWorld.updateNeighbors(blockPos5, blockState3.getBlock());
						}

						if (pair2.getSecond() != null) {
							BlockEntity blockEntity = iWorld.getBlockEntity(blockPos5);
							if (blockEntity != null) {
								blockEntity.markDirty();
							}
						}
					}
				}

				if (!arg.method_15135()) {
					this.method_15179(iWorld, blockPos, arg.method_15114(), arg.method_15113(), arg.method_15134(), mutableIntBoundingBox);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public static List<class_3499.class_3501> method_16446(IWorld iWorld, BlockPos blockPos, class_3492 arg, List<class_3499.class_3501> list) {
		List<class_3499.class_3501> list2 = Lists.<class_3499.class_3501>newArrayList();

		for (class_3499.class_3501 lv : list) {
			BlockPos blockPos2 = method_15171(arg, lv.field_15597).add(blockPos);
			class_3499.class_3501 lv2 = new class_3499.class_3501(blockPos2, lv.field_15596, lv.field_15595);
			Iterator<AbstractStructureProcessor> iterator = arg.method_16182().iterator();

			while (lv2 != null && iterator.hasNext()) {
				lv2 = ((AbstractStructureProcessor)iterator.next()).process(iWorld, blockPos, lv, lv2, arg);
			}

			if (lv2 != null) {
				list2.add(lv2);
			}
		}

		return list2;
	}

	private void method_15179(
		IWorld iWorld, BlockPos blockPos, Mirror mirror, Rotation rotation, BlockPos blockPos2, @Nullable MutableIntBoundingBox mutableIntBoundingBox
	) {
		for (class_3499.class_3502 lv : this.field_15589) {
			BlockPos blockPos3 = method_15168(lv.field_15600, mirror, rotation, blockPos2).add(blockPos);
			if (mutableIntBoundingBox == null || mutableIntBoundingBox.contains(blockPos3)) {
				CompoundTag compoundTag = lv.field_15598;
				Vec3d vec3d = method_15176(lv.field_15599, mirror, rotation, blockPos2);
				Vec3d vec3d2 = vec3d.add((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
				ListTag listTag = new ListTag();
				listTag.add((Tag)(new DoubleTag(vec3d2.x)));
				listTag.add((Tag)(new DoubleTag(vec3d2.y)));
				listTag.add((Tag)(new DoubleTag(vec3d2.z)));
				compoundTag.put("Pos", listTag);
				compoundTag.putUuid("UUID", UUID.randomUUID());

				Entity entity;
				try {
					entity = EntityType.fromTag(compoundTag, iWorld.getWorld());
				} catch (Exception var16) {
					entity = null;
				}

				if (entity != null) {
					float f = entity.applyMirror(mirror);
					f += entity.yaw - entity.applyRotation(rotation);
					entity.setPositionAndAngles(vec3d2.x, vec3d2.y, vec3d2.z, f, entity.pitch);
					iWorld.spawnEntity(entity);
				}
			}
		}
	}

	public BlockPos method_15166(Rotation rotation) {
		switch (rotation) {
			case ROT_270:
			case ROT_90:
				return new BlockPos(this.field_15587.getZ(), this.field_15587.getY(), this.field_15587.getX());
			default:
				return this.field_15587;
		}
	}

	public static BlockPos method_15168(BlockPos blockPos, Mirror mirror, Rotation rotation, BlockPos blockPos2) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		boolean bl = true;
		switch (mirror) {
			case LEFT_RIGHT:
				k = -k;
				break;
			case FRONT_BACK:
				i = -i;
				break;
			default:
				bl = false;
		}

		int l = blockPos2.getX();
		int m = blockPos2.getZ();
		switch (rotation) {
			case ROT_270:
				return new BlockPos(l - m + k, j, l + m - i);
			case ROT_90:
				return new BlockPos(l + m - k, j, m - l + i);
			case ROT_180:
				return new BlockPos(l + l - i, j, m + m - k);
			default:
				return bl ? new BlockPos(i, j, k) : blockPos;
		}
	}

	private static Vec3d method_15176(Vec3d vec3d, Mirror mirror, Rotation rotation, BlockPos blockPos) {
		double d = vec3d.x;
		double e = vec3d.y;
		double f = vec3d.z;
		boolean bl = true;
		switch (mirror) {
			case LEFT_RIGHT:
				f = 1.0 - f;
				break;
			case FRONT_BACK:
				d = 1.0 - d;
				break;
			default:
				bl = false;
		}

		int i = blockPos.getX();
		int j = blockPos.getZ();
		switch (rotation) {
			case ROT_270:
				return new Vec3d((double)(i - j) + f, e, (double)(i + j + 1) - d);
			case ROT_90:
				return new Vec3d((double)(i + j + 1) - f, e, (double)(j - i) + d);
			case ROT_180:
				return new Vec3d((double)(i + i + 1) - d, e, (double)(j + j + 1) - f);
			default:
				return bl ? new Vec3d(d, e, f) : vec3d;
		}
	}

	public BlockPos method_15167(BlockPos blockPos, Mirror mirror, Rotation rotation) {
		return method_15162(blockPos, mirror, rotation, this.method_15160().getX(), this.method_15160().getZ());
	}

	public static BlockPos method_15162(BlockPos blockPos, Mirror mirror, Rotation rotation, int i, int j) {
		i--;
		j--;
		int k = mirror == Mirror.FRONT_BACK ? i : 0;
		int l = mirror == Mirror.LEFT_RIGHT ? j : 0;
		BlockPos blockPos2 = blockPos;
		switch (rotation) {
			case ROT_270:
				blockPos2 = blockPos.add(l, 0, i - k);
				break;
			case ROT_90:
				blockPos2 = blockPos.add(j - l, 0, k);
				break;
			case ROT_180:
				blockPos2 = blockPos.add(i - k, 0, j - l);
				break;
			case ROT_0:
				blockPos2 = blockPos.add(k, 0, l);
		}

		return blockPos2;
	}

	public MutableIntBoundingBox method_16187(class_3492 arg, BlockPos blockPos) {
		Rotation rotation = arg.method_15113();
		BlockPos blockPos2 = arg.method_15134();
		BlockPos blockPos3 = this.method_15166(rotation);
		Mirror mirror = arg.method_15114();
		int i = blockPos2.getX();
		int j = blockPos2.getZ();
		int k = blockPos3.getX() - 1;
		int l = blockPos3.getY() - 1;
		int m = blockPos3.getZ() - 1;
		MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(0, 0, 0, 0, 0, 0);
		switch (rotation) {
			case ROT_270:
				mutableIntBoundingBox = new MutableIntBoundingBox(i - j, 0, i + j - m, i - j + k, l, i + j);
				break;
			case ROT_90:
				mutableIntBoundingBox = new MutableIntBoundingBox(i + j - k, 0, j - i, i + j, l, j - i + m);
				break;
			case ROT_180:
				mutableIntBoundingBox = new MutableIntBoundingBox(i + i - k, 0, j + j - m, i + i, l, j + j);
				break;
			case ROT_0:
				mutableIntBoundingBox = new MutableIntBoundingBox(0, 0, 0, k, l, m);
		}

		switch (mirror) {
			case LEFT_RIGHT:
				this.method_16186(rotation, m, k, mutableIntBoundingBox, Direction.NORTH, Direction.SOUTH);
				break;
			case FRONT_BACK:
				this.method_16186(rotation, k, m, mutableIntBoundingBox, Direction.WEST, Direction.EAST);
			case NONE:
		}

		mutableIntBoundingBox.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		return mutableIntBoundingBox;
	}

	private void method_16186(Rotation rotation, int i, int j, MutableIntBoundingBox mutableIntBoundingBox, Direction direction, Direction direction2) {
		BlockPos blockPos = BlockPos.ORIGIN;
		if (rotation == Rotation.ROT_90 || rotation == Rotation.ROT_270) {
			blockPos = blockPos.offset(rotation.method_10503(direction), j);
		} else if (rotation == Rotation.ROT_180) {
			blockPos = blockPos.offset(direction2, i);
		} else {
			blockPos = blockPos.offset(direction, i);
		}

		mutableIntBoundingBox.translate(blockPos.getX(), 0, blockPos.getZ());
	}

	public CompoundTag method_15175(CompoundTag compoundTag) {
		if (this.field_15586.isEmpty()) {
			compoundTag.put("blocks", new ListTag());
			compoundTag.put("palette", new ListTag());
		} else {
			List<class_3499.class_3500> list = Lists.<class_3499.class_3500>newArrayList();
			class_3499.class_3500 lv = new class_3499.class_3500();
			list.add(lv);

			for (int i = 1; i < this.field_15586.size(); i++) {
				list.add(new class_3499.class_3500());
			}

			ListTag listTag = new ListTag();
			List<class_3499.class_3501> list2 = (List<class_3499.class_3501>)this.field_15586.get(0);

			for (int j = 0; j < list2.size(); j++) {
				class_3499.class_3501 lv2 = (class_3499.class_3501)list2.get(j);
				CompoundTag compoundTag2 = new CompoundTag();
				compoundTag2.put("pos", this.method_15169(lv2.field_15597.getX(), lv2.field_15597.getY(), lv2.field_15597.getZ()));
				int k = lv.method_15187(lv2.field_15596);
				compoundTag2.putInt("state", k);
				if (lv2.field_15595 != null) {
					compoundTag2.put("nbt", lv2.field_15595);
				}

				listTag.add((Tag)compoundTag2);

				for (int l = 1; l < this.field_15586.size(); l++) {
					class_3499.class_3500 lv3 = (class_3499.class_3500)list.get(l);
					lv3.method_15186(((class_3499.class_3501)((List)this.field_15586.get(l)).get(j)).field_15596, k);
				}
			}

			compoundTag.put("blocks", listTag);
			if (list.size() == 1) {
				ListTag listTag2 = new ListTag();

				for (BlockState blockState : lv) {
					listTag2.add((Tag)TagHelper.serializeBlockState(blockState));
				}

				compoundTag.put("palette", listTag2);
			} else {
				ListTag listTag2 = new ListTag();

				for (class_3499.class_3500 lv4 : list) {
					ListTag listTag3 = new ListTag();

					for (BlockState blockState2 : lv4) {
						listTag3.add((Tag)TagHelper.serializeBlockState(blockState2));
					}

					listTag2.add((Tag)listTag3);
				}

				compoundTag.put("palettes", listTag2);
			}
		}

		ListTag listTag4 = new ListTag();

		for (class_3499.class_3502 lv5 : this.field_15589) {
			CompoundTag compoundTag3 = new CompoundTag();
			compoundTag3.put("pos", this.method_15184(lv5.field_15599.x, lv5.field_15599.y, lv5.field_15599.z));
			compoundTag3.put("blockPos", this.method_15169(lv5.field_15600.getX(), lv5.field_15600.getY(), lv5.field_15600.getZ()));
			if (lv5.field_15598 != null) {
				compoundTag3.put("nbt", lv5.field_15598);
			}

			listTag4.add((Tag)compoundTag3);
		}

		compoundTag.put("entities", listTag4);
		compoundTag.put("size", this.method_15169(this.field_15587.getX(), this.field_15587.getY(), this.field_15587.getZ()));
		compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		return compoundTag;
	}

	public void method_15183(CompoundTag compoundTag) {
		this.field_15586.clear();
		this.field_15589.clear();
		ListTag listTag = compoundTag.getList("size", 3);
		this.field_15587 = new BlockPos(listTag.getInt(0), listTag.getInt(1), listTag.getInt(2));
		ListTag listTag2 = compoundTag.getList("blocks", 10);
		if (compoundTag.containsKey("palettes", 9)) {
			ListTag listTag3 = compoundTag.getList("palettes", 9);

			for (int i = 0; i < listTag3.size(); i++) {
				this.method_15177(listTag3.getListTag(i), listTag2);
			}
		} else {
			this.method_15177(compoundTag.getList("palette", 10), listTag2);
		}

		ListTag listTag3 = compoundTag.getList("entities", 10);

		for (int i = 0; i < listTag3.size(); i++) {
			CompoundTag compoundTag2 = listTag3.getCompoundTag(i);
			ListTag listTag4 = compoundTag2.getList("pos", 6);
			Vec3d vec3d = new Vec3d(listTag4.getDouble(0), listTag4.getDouble(1), listTag4.getDouble(2));
			ListTag listTag5 = compoundTag2.getList("blockPos", 3);
			BlockPos blockPos = new BlockPos(listTag5.getInt(0), listTag5.getInt(1), listTag5.getInt(2));
			if (compoundTag2.containsKey("nbt")) {
				CompoundTag compoundTag3 = compoundTag2.getCompound("nbt");
				this.field_15589.add(new class_3499.class_3502(vec3d, blockPos, compoundTag3));
			}
		}
	}

	private void method_15177(ListTag listTag, ListTag listTag2) {
		class_3499.class_3500 lv = new class_3499.class_3500();
		List<class_3499.class_3501> list = Lists.<class_3499.class_3501>newArrayList();

		for (int i = 0; i < listTag.size(); i++) {
			lv.method_15186(TagHelper.deserializeBlockState(listTag.getCompoundTag(i)), i);
		}

		for (int i = 0; i < listTag2.size(); i++) {
			CompoundTag compoundTag = listTag2.getCompoundTag(i);
			ListTag listTag3 = compoundTag.getList("pos", 3);
			BlockPos blockPos = new BlockPos(listTag3.getInt(0), listTag3.getInt(1), listTag3.getInt(2));
			BlockState blockState = lv.method_15185(compoundTag.getInt("state"));
			CompoundTag compoundTag2;
			if (compoundTag.containsKey("nbt")) {
				compoundTag2 = compoundTag.getCompound("nbt");
			} else {
				compoundTag2 = null;
			}

			list.add(new class_3499.class_3501(blockPos, blockState, compoundTag2));
		}

		list.sort(Comparator.comparingInt(arg -> arg.field_15597.getY()));
		this.field_15586.add(list);
	}

	private ListTag method_15169(int... is) {
		ListTag listTag = new ListTag();

		for (int i : is) {
			listTag.add((Tag)(new IntTag(i)));
		}

		return listTag;
	}

	private ListTag method_15184(double... ds) {
		ListTag listTag = new ListTag();

		for (double d : ds) {
			listTag.add((Tag)(new DoubleTag(d)));
		}

		return listTag;
	}

	static class class_3500 implements Iterable<BlockState> {
		public static final BlockState field_15590 = Blocks.field_10124.getDefaultState();
		private final IdList<BlockState> field_15591 = new IdList<>(16);
		private int field_15592;

		private class_3500() {
		}

		public int method_15187(BlockState blockState) {
			int i = this.field_15591.getId(blockState);
			if (i == -1) {
				i = this.field_15592++;
				this.field_15591.set(blockState, i);
			}

			return i;
		}

		@Nullable
		public BlockState method_15185(int i) {
			BlockState blockState = this.field_15591.getInt(i);
			return blockState == null ? field_15590 : blockState;
		}

		public Iterator<BlockState> iterator() {
			return this.field_15591.iterator();
		}

		public void method_15186(BlockState blockState, int i) {
			this.field_15591.set(blockState, i);
		}
	}

	public static class class_3501 {
		public final BlockPos field_15597;
		public final BlockState field_15596;
		public final CompoundTag field_15595;

		public class_3501(BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag) {
			this.field_15597 = blockPos;
			this.field_15596 = blockState;
			this.field_15595 = compoundTag;
		}

		public String toString() {
			return String.format("<StructureBlockInfo | %s | %s | %s>", this.field_15597, this.field_15596, this.field_15595);
		}
	}

	public static class class_3502 {
		public final Vec3d field_15599;
		public final BlockPos field_15600;
		public final CompoundTag field_15598;

		public class_3502(Vec3d vec3d, BlockPos blockPos, CompoundTag compoundTag) {
			this.field_15599 = vec3d;
			this.field_15600 = blockPos;
			this.field_15598 = compoundTag;
		}
	}
}
