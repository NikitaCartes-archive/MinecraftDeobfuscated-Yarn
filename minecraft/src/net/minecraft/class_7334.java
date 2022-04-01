package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class class_7334 {
	public static final int field_38576 = 10;
	public static final int field_38577 = 4;
	public static final int field_38578 = 24;
	public static final int field_38579 = 1000;
	public static final int field_38580 = 10;
	public static final int field_38581 = 5;
	public static final float field_38582 = 0.5F;
	private static final int field_38583 = 32;
	private List<class_7334.class_7335> field_38584 = new ArrayList();
	private static final Logger field_38585 = LogUtils.getLogger();

	@VisibleForTesting
	public List<class_7334.class_7335> method_42926() {
		return this.field_38584;
	}

	public void method_42931(NbtCompound nbtCompound) {
		if (nbtCompound.contains("cursors", NbtElement.LIST_TYPE)) {
			this.field_38584.clear();
			List<class_7334.class_7335> list = (List<class_7334.class_7335>)class_7334.class_7335.field_38587
				.listOf()
				.parse(new Dynamic<>(NbtOps.INSTANCE, nbtCompound.getList("cursors", NbtElement.COMPOUND_TYPE)))
				.resultOrPartial(field_38585::error)
				.orElseGet(ArrayList::new);
			int i = Math.min(list.size(), 32);

			for (int j = 0; j < i; j++) {
				this.method_42928((class_7334.class_7335)list.get(j));
			}
		}
	}

	public void method_42933(NbtCompound nbtCompound) {
		class_7334.class_7335.field_38587
			.listOf()
			.encodeStart(NbtOps.INSTANCE, this.field_38584)
			.resultOrPartial(field_38585::error)
			.ifPresent(nbtElement -> nbtCompound.put("cursors", nbtElement));
	}

	public void method_42930(BlockPos blockPos, int i) {
		while (i > 0) {
			int j = Math.min(i, 1000);
			this.method_42928(new class_7334.class_7335(blockPos, j));
			i -= j;
		}
	}

	private void method_42928(class_7334.class_7335 arg) {
		if (this.field_38584.size() < 32) {
			this.field_38584.add(arg);
		}
	}

	public void method_42927(World world, BlockPos blockPos, Random random) {
		if (!this.field_38584.isEmpty()) {
			List<class_7334.class_7335> list = new ArrayList();
			Map<BlockPos, class_7334.class_7335> map = new HashMap();
			Object2IntMap<BlockPos> object2IntMap = new Object2IntOpenHashMap<>();

			for (class_7334.class_7335 lv : this.field_38584) {
				lv.method_42937(world, blockPos, random);
				if (lv.field_38590 <= 0) {
					world.syncWorldEvent(3006, lv.method_42934(), 0);
				} else {
					BlockPos blockPos2 = lv.method_42934();
					object2IntMap.computeInt(blockPos2, (blockPosx, integer) -> (integer == null ? 0 : integer) + lv.field_38590);
					class_7334.class_7335 lv2 = (class_7334.class_7335)map.get(blockPos2);
					if (lv2 == null) {
						map.put(blockPos2, lv);
						list.add(lv);
					} else if (lv.field_38590 + lv2.field_38590 <= 1000) {
						lv2.method_42938(lv);
					} else {
						list.add(lv);
						if (lv.field_38590 < lv2.field_38590) {
							map.put(blockPos2, lv);
						}
					}
				}
			}

			for (Entry<BlockPos> entry : object2IntMap.object2IntEntrySet()) {
				BlockPos blockPos2 = (BlockPos)entry.getKey();
				int i = entry.getIntValue();
				class_7334.class_7335 lv3 = (class_7334.class_7335)map.get(blockPos2);
				Collection<Direction> collection = lv3 == null ? null : lv3.method_42950();
				if (i > 0 && collection != null) {
					int j = (int)(Math.log1p((double)i) / 2.3F) + 1;
					int k = (j << 6) + AbstractLichenBlock.method_42888(collection);
					world.syncWorldEvent(3006, blockPos2, k);
				}
			}

			this.field_38584 = list;
		}
	}

	public static class class_7335 {
		private static final List<Vec3i> field_38588 = Util.make(
			new ArrayList(18),
			arrayList -> BlockPos.stream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1))
					.filter(blockPos -> (blockPos.getX() == 0 || blockPos.getY() == 0 || blockPos.getZ() == 0) && !blockPos.equals(BlockPos.ORIGIN))
					.map(BlockPos::toImmutable)
					.forEach(arrayList::add)
		);
		public static final int field_38586 = 1;
		private BlockPos field_38589;
		int field_38590;
		private int field_38591;
		private int field_38592;
		@Nullable
		private Set<Direction> field_38593;
		private static final Codec<Set<Direction>> field_38594 = Direction.CODEC.listOf().xmap(list -> Sets.newEnumSet(list, Direction.class), Lists::newArrayList);
		public static final Codec<class_7334.class_7335> field_38587 = RecordCodecBuilder.create(
			instance -> instance.group(
						BlockPos.CODEC.fieldOf("pos").forGetter(class_7334.class_7335::method_42934),
						Codec.intRange(0, 1000).fieldOf("charge").orElse(0).forGetter(class_7334.class_7335::method_42945),
						Codec.intRange(0, 1).fieldOf("decay_delay").orElse(1).forGetter(class_7334.class_7335::method_42948),
						Codec.intRange(0, Integer.MAX_VALUE).fieldOf("update_delay").orElse(0).forGetter(arg -> arg.field_38591),
						field_38594.optionalFieldOf("facings").forGetter(arg -> Optional.ofNullable(arg.method_42950()))
					)
					.apply(instance, class_7334.class_7335::new)
		);

		private class_7335(BlockPos blockPos, int i, int j, int k, Optional<Set<Direction>> optional) {
			this.field_38589 = blockPos;
			this.field_38590 = i;
			this.field_38592 = j;
			this.field_38591 = k;
			this.field_38593 = (Set<Direction>)optional.orElse(null);
		}

		public class_7335(BlockPos blockPos, int i) {
			this(blockPos, i, 1, 0, Optional.empty());
		}

		public BlockPos method_42934() {
			return this.field_38589;
		}

		public int method_42945() {
			return this.field_38590;
		}

		public int method_42948() {
			return this.field_38592;
		}

		@Nullable
		public Set<Direction> method_42950() {
			return this.field_38593;
		}

		public void method_42937(World world, BlockPos blockPos, Random random) {
			if (world.method_42871(this.field_38589) && this.field_38590 > 0) {
				if (this.field_38591 > 0) {
					this.field_38591--;
				} else {
					BlockState blockState = world.getBlockState(this.field_38589);
					class_7331 lv = method_42940(blockState);
					if (lv.method_42918(world, this.field_38589, blockState, this.field_38593)) {
						if (lv.method_42921()) {
							blockState = world.getBlockState(this.field_38589);
							lv = method_42940(blockState);
						}

						world.playSound(null, this.field_38589, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}

					this.field_38590 = lv.method_42920(this, world, blockPos, random);
					if (this.field_38590 <= 0) {
						lv.method_42917(world, blockState, this.field_38589, random);
					} else {
						BlockPos blockPos2 = method_42946(world, this.field_38589, random);
						if (blockPos2 != null) {
							lv.method_42917(world, blockState, this.field_38589, random);
							this.field_38589 = blockPos2.toImmutable();
							blockState = world.getBlockState(blockPos2);
						}

						if (blockState.getBlock() instanceof class_7331) {
							this.field_38593 = AbstractLichenBlock.method_42889(blockState);
						}

						this.field_38592 = lv.method_42922(this.field_38592);
						this.field_38591 = lv.method_42916();
					}
				}
			}
		}

		void method_42938(class_7334.class_7335 arg) {
			this.field_38590 = this.field_38590 + arg.field_38590;
			arg.field_38590 = 0;
			this.field_38591 = Math.min(this.field_38591, arg.field_38591);
		}

		private static class_7331 method_42940(BlockState blockState) {
			return blockState.getBlock() instanceof class_7331 lv ? lv : class_7331.field_38571;
		}

		private static List<Vec3i> method_42944(Random random) {
			List<Vec3i> list = new ArrayList(field_38588);
			Collections.shuffle(list, random);
			return list;
		}

		@Nullable
		private static BlockPos method_42946(World world, BlockPos blockPos, Random random) {
			BlockPos.Mutable mutable = blockPos.mutableCopy();
			BlockPos.Mutable mutable2 = blockPos.mutableCopy();

			for (Vec3i vec3i : method_42944(random)) {
				mutable2.set(blockPos, vec3i);
				BlockState blockState = world.getBlockState(mutable2);
				if (blockState.getBlock() instanceof class_7331 && method_42935(world, blockPos, mutable2)) {
					mutable.set(mutable2);
					if (class_7336.method_42951(world, blockState, mutable2)) {
						break;
					}
				}
			}

			return mutable.equals(blockPos) ? null : mutable;
		}

		private static boolean method_42935(World world, BlockPos blockPos, BlockPos blockPos2) {
			if (blockPos.getManhattanDistance(blockPos2) == 1) {
				return true;
			} else {
				BlockPos blockPos3 = blockPos2.subtract(blockPos);
				Direction direction = Direction.from(Direction.Axis.X, blockPos3.getX() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
				Direction direction2 = Direction.from(Direction.Axis.Y, blockPos3.getY() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
				Direction direction3 = Direction.from(Direction.Axis.Z, blockPos3.getZ() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
				if (blockPos3.getX() == 0) {
					return method_42936(world, blockPos, direction2) || method_42936(world, blockPos, direction3);
				} else {
					return blockPos3.getY() == 0
						? method_42936(world, blockPos, direction) || method_42936(world, blockPos, direction3)
						: method_42936(world, blockPos, direction) || method_42936(world, blockPos, direction2);
				}
			}
		}

		private static boolean method_42936(World world, BlockPos blockPos, Direction direction) {
			BlockPos blockPos2 = blockPos.offset(direction);
			return !world.getBlockState(blockPos2).isSideSolidFullSquare(world, blockPos2, direction.getOpposite());
		}
	}
}
