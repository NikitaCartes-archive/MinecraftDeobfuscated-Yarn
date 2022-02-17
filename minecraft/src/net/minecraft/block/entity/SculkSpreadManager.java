package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_6989;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkVeinBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class SculkSpreadManager {
	public static final int field_36853 = 10;
	public static final int field_36854 = 4;
	public static final int field_36855 = 24;
	public static final short field_36856 = 1000;
	public static final int field_36857 = 10;
	public static final int field_36858 = 5;
	public static final float field_36859 = 0.5F;
	private static final int field_36860 = 32;
	private List<SculkSpreadManager.Cursor> cursors = new ArrayList();

	@VisibleForTesting
	public List<SculkSpreadManager.Cursor> getCursors() {
		return this.cursors;
	}

	public void readNbt(NbtCompound nbt) {
		if (!this.cursors.isEmpty()) {
			throw new IllegalStateException("Some cursors have already been loaded. This is never expected to happen!");
		} else {
			for (NbtElement nbtElement : nbt.getList("cursors", NbtElement.LIST_TYPE)) {
				if (nbtElement instanceof NbtCompound nbtCompound) {
					this.addCursor(new SculkSpreadManager.Cursor(nbtCompound));
				}
			}
		}
	}

	public void writeNbt(NbtCompound nbt) {
		NbtList nbtList = new NbtList();

		for (SculkSpreadManager.Cursor cursor : this.cursors) {
			NbtCompound nbtCompound = new NbtCompound();
			cursor.writeNbt(nbtCompound);
			nbtList.add(nbtCompound);
		}

		nbt.put("cursors", nbtList);
	}

	public void spread(BlockPos pos, int charge) {
		while (charge > 0) {
			short s = (short)Math.min(charge, 1000);
			this.addCursor(new SculkSpreadManager.Cursor(pos, s));
			charge -= s;
		}
	}

	private void addCursor(SculkSpreadManager.Cursor cursor) {
		if (this.cursors.size() < 32) {
			this.cursors.add(cursor);
		}
	}

	public void tick(World world, BlockPos pos, Random random) {
		if (!this.cursors.isEmpty()) {
			List<SculkSpreadManager.Cursor> list = new ArrayList();
			HashMap<BlockPos, SculkSpreadManager.Cursor> hashMap = new HashMap();
			HashMap<BlockPos, Integer> hashMap2 = new HashMap();

			for (int i = 0; i < this.cursors.size(); i++) {
				SculkSpreadManager.Cursor cursor = (SculkSpreadManager.Cursor)this.cursors.get(i);
				cursor.method_40809(world, pos, random);
				if (cursor.charge <= 0) {
					world.syncWorldEvent(WorldEvents.SCULK_CHARGE, cursor.getPos(), 0);
				} else {
					BlockPos blockPos = cursor.getPos();
					hashMap2.put(blockPos, (Integer)hashMap2.getOrDefault(blockPos, 0) + cursor.charge);
					SculkSpreadManager.Cursor cursor2 = (SculkSpreadManager.Cursor)hashMap.get(blockPos);
					if (cursor2 == null) {
						hashMap.put(blockPos, cursor);
						list.add(cursor);
					} else if (cursor.charge + cursor2.charge <= 1000) {
						cursor2.method_40810(cursor);
					} else {
						list.add(cursor);
						if (cursor.charge < cursor2.charge) {
							hashMap.put(blockPos, cursor);
						}
					}
				}
			}

			for (BlockPos blockPos2 : hashMap2.keySet()) {
				int j = (Integer)hashMap2.get(blockPos2);
				SculkSpreadManager.Cursor cursor2 = (SculkSpreadManager.Cursor)hashMap.get(blockPos2);
				byte b = cursor2 == null ? 0 : cursor2.getFaces();
				if (b != -1 && j > 0) {
					int k = (int)(Math.log1p((double)j) / 2.3F) + 1;
					int l = (k << 6) + b;
					world.syncWorldEvent(WorldEvents.SCULK_CHARGE, blockPos2, l);
				}
			}

			this.cursors = list;
		}
	}

	public static class Cursor {
		public static final byte field_36862 = -1;
		public static final byte field_36863 = 0;
		public static final byte field_36864 = 2;
		private BlockPos pos;
		short charge;
		private byte update;
		private byte faces;
		private byte decay;
		private static final Vec3i[] field_36870 = new Vec3i[18];

		public Cursor(BlockPos pos, short charge) {
			this.pos = pos;
			this.charge = charge;
			this.update = 0;
			this.decay = 2;
			this.faces = -1;
		}

		Cursor(NbtCompound nbt) {
			this.pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
			this.charge = nbt.getShort("charge");
			this.update = nbt.getByte("update");
			this.decay = nbt.getByte("decay");
			this.faces = nbt.getByte("faces");
		}

		public void writeNbt(NbtCompound nbt) {
			nbt.putInt("x", this.pos.getX());
			nbt.putInt("y", this.pos.getY());
			nbt.putInt("z", this.pos.getZ());
			nbt.putInt("charge", this.charge);
			nbt.putByte("update", this.update);
			nbt.putByte("decay", this.decay);
			nbt.putByte("faces", this.faces);
		}

		public BlockPos getPos() {
			return this.pos;
		}

		public short getCharge() {
			return this.charge;
		}

		public int getDecay() {
			return this.decay;
		}

		public byte getFaces() {
			return this.faces;
		}

		public void method_40809(World world, BlockPos blockPos, Random random) {
			if (world.shouldTickBlockPos(this.pos)) {
				if (--this.update <= 0) {
					BlockState blockState = world.getBlockState(this.pos);
					class_6989 lv = this.method_40811(blockState);
					this.decay = lv.method_40782(this.decay);
					if (this.charge <= 0) {
						lv.method_40783(world, blockState, this.pos, random);
					} else {
						if (lv.method_40784(world, this.pos, blockState, this.faces)) {
							if (lv.method_40787()) {
								blockState = world.getBlockState(this.pos);
								lv = this.method_40811(blockState);
							}

							world.playSound(null, this.pos, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
						}

						this.charge = lv.method_40786(this, world, blockPos, random);
						if (this.charge <= 0) {
							lv.method_40783(world, blockState, this.pos, random);
						} else {
							BlockPos blockPos2 = this.method_40815(world, this.pos, random);
							if (blockPos2 != null) {
								lv.method_40783(world, blockState, this.pos, random);
								this.pos = blockPos2;
								blockState = world.getBlockState(blockPos2);
							}

							if (blockState.getBlock() instanceof class_6989) {
								this.faces = Direction.pack(AbstractLichenBlock.getDirections(blockState));
							}

							this.update = lv.method_40781();
						}
					}
				}
			}
		}

		public void method_40810(SculkSpreadManager.Cursor cursor) {
			this.charge = (short)(this.charge + cursor.charge);
			cursor.charge = 0;
			this.update = (byte)Math.min(this.update, cursor.update);
		}

		private class_6989 method_40811(BlockState blockState) {
			return blockState.getBlock() instanceof class_6989 lv ? lv : class_6989.field_36845;
		}

		private Vec3i[] method_40812(Random random) {
			int i = field_36870.length;
			Vec3i[] vec3is = new Vec3i[i];
			System.arraycopy(field_36870, 0, vec3is, 0, i);

			for (int j = i - 1; j > 0; j--) {
				int k = random.nextInt(j + 1);
				Vec3i vec3i = vec3is[j];
				vec3is[j] = vec3is[k];
				vec3is[k] = vec3i;
			}

			return vec3is;
		}

		@Nullable
		private BlockPos method_40815(World world, BlockPos blockPos, Random random) {
			BlockPos.Mutable mutable = blockPos.mutableCopy();
			BlockPos.Mutable mutable2 = blockPos.mutableCopy();

			for (Vec3i vec3i : this.method_40812(random)) {
				mutable2.set(blockPos, vec3i);
				BlockState blockState = world.getBlockState(mutable2);
				if (blockState.getBlock() instanceof class_6989 && this.canSpread(world, blockPos, mutable2)) {
					mutable.set(mutable2);
					if (SculkVeinBlock.method_40818(world, blockState, mutable2)) {
						break;
					}
				}
			}

			return mutable.equals(blockPos) ? null : mutable.toImmutable();
		}

		private boolean canSpread(World world, BlockPos to, BlockPos from) {
			double d = to.getSquaredDistance(from, false);
			if (d < 1.5) {
				return true;
			} else if (d > 2.5) {
				return false;
			} else {
				Direction direction;
				Direction direction2;
				if (to.getX() == from.getX()) {
					direction = to.getY() < from.getY() ? Direction.UP : Direction.DOWN;
					direction2 = to.getZ() < from.getZ() ? Direction.SOUTH : Direction.NORTH;
				} else if (to.getY() == from.getY()) {
					direction = to.getX() < from.getX() ? Direction.EAST : Direction.WEST;
					direction2 = to.getZ() < from.getZ() ? Direction.SOUTH : Direction.NORTH;
				} else {
					direction = to.getX() < from.getX() ? Direction.EAST : Direction.WEST;
					direction2 = to.getY() < from.getY() ? Direction.UP : Direction.DOWN;
				}

				BlockPos blockPos = to.offset(direction);
				BlockPos blockPos2 = to.offset(direction2);
				return !world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction.getOpposite())
					|| !world.getBlockState(blockPos2).isSideSolidFullSquare(world, blockPos2, direction2.getOpposite());
			}
		}

		static {
			int i = 0;

			for (int j = -1; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					for (int l = -1; l < 2; l++) {
						if ((k != 0 || j != 0 || l != 0) && (k == 0 || j == 0 || l == 0)) {
							field_36870[i++] = new Vec3i(k, j, l);
						}
					}
				}
			}
		}
	}
}
