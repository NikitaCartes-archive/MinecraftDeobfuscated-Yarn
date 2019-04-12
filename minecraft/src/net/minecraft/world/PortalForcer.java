package net.minecraft.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkPos;

public class PortalForcer {
	private static final PortalBlock PORTAL_BLOCK = (PortalBlock)Blocks.field_10316;
	private final ServerWorld world;
	private final Random random;
	private final Long2ObjectMap<PortalForcer.class_1947> field_9287 = new Long2ObjectOpenHashMap<>(4096);

	public PortalForcer(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.random = new Random(serverWorld.getSeed());
	}

	public boolean method_8653(Entity entity, float f) {
		Vec3d vec3d = entity.method_5656();
		Direction direction = entity.method_5843();
		long l = ChunkPos.toLong(MathHelper.floor(entity.x), MathHelper.floor(entity.z));
		Pair<Vec3d, Pair<Vec3d, Integer>> pair = this.method_18475(new BlockPos(entity), entity.getVelocity(), l, direction, vec3d.x, vec3d.y);
		if (pair == null) {
			return false;
		} else {
			Vec3d vec3d2 = pair.getFirst();
			Vec3d vec3d3 = pair.getSecond().getFirst();
			entity.setVelocity(vec3d3);
			entity.yaw = f + (float)pair.getSecond().getSecond().intValue();
			if (entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)entity).networkHandler.requestTeleport(vec3d2.x, vec3d2.y, vec3d2.z, entity.yaw, entity.pitch);
				((ServerPlayerEntity)entity).networkHandler.syncWithPlayerPosition();
			} else {
				entity.setPositionAndAngles(vec3d2.x, vec3d2.y, vec3d2.z, entity.yaw, entity.pitch);
			}

			return true;
		}
	}

	@Nullable
	public Pair<Vec3d, Pair<Vec3d, Integer>> method_18475(BlockPos blockPos, Vec3d vec3d, long l, Direction direction, double d, double e) {
		int i = 128;
		boolean bl = true;
		BlockPos blockPos2 = null;
		if (this.field_9287.containsKey(l)) {
			PortalForcer.class_1947 lv = this.field_9287.get(l);
			blockPos2 = lv;
			lv.field_9290 = this.world.getTime();
			bl = false;
		} else {
			double f = Double.MAX_VALUE;

			for (int j = -128; j <= 128; j++) {
				for (int k = -128; k <= 128; k++) {
					BlockPos blockPos3 = blockPos.add(j, this.world.getEffectiveHeight() - 1 - blockPos.getY(), k);

					while (blockPos3.getY() >= 0) {
						BlockPos blockPos4 = blockPos3.down();
						if (this.world.getBlockState(blockPos3).getBlock() == PORTAL_BLOCK) {
							for (blockPos4 = blockPos3.down(); this.world.getBlockState(blockPos4).getBlock() == PORTAL_BLOCK; blockPos4 = blockPos4.down()) {
								blockPos3 = blockPos4;
							}

							double g = blockPos3.getSquaredDistance(blockPos);
							if (f < 0.0 || g < f) {
								f = g;
								blockPos2 = blockPos3;
							}
						}

						blockPos3 = blockPos4;
					}
				}
			}
		}

		if (blockPos2 == null) {
			return null;
		} else {
			if (bl) {
				this.field_9287.put(l, new PortalForcer.class_1947(blockPos2, this.world.getTime()));
			}

			BlockPattern.Result result = PORTAL_BLOCK.method_10350(this.world, blockPos2);
			return result.method_18478(direction, blockPos2, e, vec3d, d);
		}
	}

	public boolean method_8654(Entity entity) {
		int i = 16;
		double d = -1.0;
		int j = MathHelper.floor(entity.x);
		int k = MathHelper.floor(entity.y);
		int l = MathHelper.floor(entity.z);
		int m = j;
		int n = k;
		int o = l;
		int p = 0;
		int q = this.random.nextInt(4);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int r = j - 16; r <= j + 16; r++) {
			double e = (double)r + 0.5 - entity.x;

			for (int s = l - 16; s <= l + 16; s++) {
				double f = (double)s + 0.5 - entity.z;

				label279:
				for (int t = this.world.getEffectiveHeight() - 1; t >= 0; t--) {
					if (this.world.isAir(mutable.set(r, t, s))) {
						while (t > 0 && this.world.isAir(mutable.set(r, t - 1, s))) {
							t--;
						}

						for (int u = q; u < q + 4; u++) {
							int v = u % 2;
							int w = 1 - v;
							if (u % 4 >= 2) {
								v = -v;
								w = -w;
							}

							for (int x = 0; x < 3; x++) {
								for (int y = 0; y < 4; y++) {
									for (int z = -1; z < 4; z++) {
										int aa = r + (y - 1) * v + x * w;
										int ab = t + z;
										int ac = s + (y - 1) * w - x * v;
										mutable.set(aa, ab, ac);
										if (z < 0 && !this.world.getBlockState(mutable).getMaterial().method_15799() || z >= 0 && !this.world.isAir(mutable)) {
											continue label279;
										}
									}
								}
							}

							double g = (double)t + 0.5 - entity.y;
							double h = e * e + g * g + f * f;
							if (d < 0.0 || h < d) {
								d = h;
								m = r;
								n = t;
								o = s;
								p = u % 4;
							}
						}
					}
				}
			}
		}

		if (d < 0.0) {
			for (int r = j - 16; r <= j + 16; r++) {
				double e = (double)r + 0.5 - entity.x;

				for (int s = l - 16; s <= l + 16; s++) {
					double f = (double)s + 0.5 - entity.z;

					label216:
					for (int tx = this.world.getEffectiveHeight() - 1; tx >= 0; tx--) {
						if (this.world.isAir(mutable.set(r, tx, s))) {
							while (tx > 0 && this.world.isAir(mutable.set(r, tx - 1, s))) {
								tx--;
							}

							for (int u = q; u < q + 2; u++) {
								int vx = u % 2;
								int wx = 1 - vx;

								for (int x = 0; x < 4; x++) {
									for (int y = -1; y < 4; y++) {
										int zx = r + (x - 1) * vx;
										int aa = tx + y;
										int ab = s + (x - 1) * wx;
										mutable.set(zx, aa, ab);
										if (y < 0 && !this.world.getBlockState(mutable).getMaterial().method_15799() || y >= 0 && !this.world.isAir(mutable)) {
											continue label216;
										}
									}
								}

								double g = (double)tx + 0.5 - entity.y;
								double h = e * e + g * g + f * f;
								if (d < 0.0 || h < d) {
									d = h;
									m = r;
									n = tx;
									o = s;
									p = u % 2;
								}
							}
						}
					}
				}
			}
		}

		int ad = m;
		int ae = n;
		int s = o;
		int af = p % 2;
		int ag = 1 - af;
		if (p % 4 >= 2) {
			af = -af;
			ag = -ag;
		}

		if (d < 0.0) {
			n = MathHelper.clamp(n, 70, this.world.getEffectiveHeight() - 10);
			ae = n;

			for (int txx = -1; txx <= 1; txx++) {
				for (int u = 1; u < 3; u++) {
					for (int vx = -1; vx < 3; vx++) {
						int wx = ad + (u - 1) * af + txx * ag;
						int x = ae + vx;
						int yx = s + (u - 1) * ag - txx * af;
						boolean bl = vx < 0;
						mutable.set(wx, x, yx);
						this.world.setBlockState(mutable, bl ? Blocks.field_10540.getDefaultState() : Blocks.AIR.getDefaultState());
					}
				}
			}
		}

		for (int txx = -1; txx < 3; txx++) {
			for (int u = -1; u < 4; u++) {
				if (txx == -1 || txx == 2 || u == -1 || u == 3) {
					mutable.set(ad + txx * af, ae + u, s + txx * ag);
					this.world.setBlockState(mutable, Blocks.field_10540.getDefaultState(), 3);
				}
			}
		}

		BlockState blockState = PORTAL_BLOCK.getDefaultState().with(PortalBlock.AXIS, af == 0 ? Direction.Axis.Z : Direction.Axis.X);

		for (int ux = 0; ux < 2; ux++) {
			for (int vx = 0; vx < 3; vx++) {
				mutable.set(ad + ux * af, ae + vx, s + ux * ag);
				this.world.setBlockState(mutable, blockState, 18);
			}
		}

		return true;
	}

	public void tick(long l) {
		if (l % 100L == 0L) {
			long m = l - 300L;
			ObjectIterator<PortalForcer.class_1947> objectIterator = this.field_9287.values().iterator();

			while (objectIterator.hasNext()) {
				PortalForcer.class_1947 lv = (PortalForcer.class_1947)objectIterator.next();
				if (lv == null || lv.field_9290 < m) {
					objectIterator.remove();
				}
			}
		}
	}

	public class class_1947 extends BlockPos {
		public long field_9290;

		public class_1947(BlockPos blockPos, long l) {
			super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			this.field_9290 = l;
		}
	}
}
