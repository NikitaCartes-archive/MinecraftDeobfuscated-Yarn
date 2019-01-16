package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Random;
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
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.DimensionType;

public class PortalForcer {
	private static final PortalBlock PORTAL_BLOCK = (PortalBlock)Blocks.field_10316;
	private final ServerWorld world;
	private final Random random;
	private final Long2ObjectMap<PortalForcer.class_1947> field_9287 = new Long2ObjectOpenHashMap<>(4096);

	public PortalForcer(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.random = new Random(serverWorld.getSeed());
	}

	public void method_8655(Entity entity, float f) {
		if (this.world.dimension.getType() != DimensionType.field_13078) {
			if (!this.method_8653(entity, f)) {
				this.method_8654(entity);
				this.method_8653(entity, f);
			}
		} else {
			int i = MathHelper.floor(entity.x);
			int j = MathHelper.floor(entity.y) - 1;
			int k = MathHelper.floor(entity.z);
			int l = 1;
			int m = 0;

			for (int n = -2; n <= 2; n++) {
				for (int o = -2; o <= 2; o++) {
					for (int p = -1; p < 3; p++) {
						int q = i + o * 1 + n * 0;
						int r = j + p;
						int s = k + o * 0 - n * 1;
						boolean bl = p < 0;
						this.world.setBlockState(new BlockPos(q, r, s), bl ? Blocks.field_10540.getDefaultState() : Blocks.field_10124.getDefaultState());
					}
				}
			}

			entity.setPositionAndAngles((double)i, (double)j, (double)k, entity.yaw, 0.0F);
			entity.velocityX = 0.0;
			entity.velocityY = 0.0;
			entity.velocityZ = 0.0;
		}
	}

	public boolean method_8653(Entity entity, float f) {
		int i = 128;
		double d = -1.0;
		int j = MathHelper.floor(entity.x);
		int k = MathHelper.floor(entity.z);
		boolean bl = true;
		BlockPos blockPos = BlockPos.ORIGIN;
		long l = ChunkPos.toLong(j, k);
		if (this.field_9287.containsKey(l)) {
			PortalForcer.class_1947 lv = this.field_9287.get(l);
			d = 0.0;
			blockPos = lv;
			lv.field_9290 = this.world.getTime();
			bl = false;
		} else {
			BlockPos blockPos2 = new BlockPos(entity);

			for (int m = -128; m <= 128; m++) {
				for (int n = -128; n <= 128; n++) {
					BlockPos blockPos3 = blockPos2.add(m, this.world.getEffectiveHeight() - 1 - blockPos2.getY(), n);

					while (blockPos3.getY() >= 0) {
						BlockPos blockPos4 = blockPos3.down();
						if (this.world.getBlockState(blockPos3).getBlock() == PORTAL_BLOCK) {
							for (blockPos4 = blockPos3.down(); this.world.getBlockState(blockPos4).getBlock() == PORTAL_BLOCK; blockPos4 = blockPos4.down()) {
								blockPos3 = blockPos4;
							}

							double e = blockPos3.squaredDistanceTo(blockPos2);
							if (d < 0.0 || e < d) {
								d = e;
								blockPos = blockPos3;
							}
						}

						blockPos3 = blockPos4;
					}
				}
			}
		}

		if (d >= 0.0) {
			if (bl) {
				this.field_9287.put(l, new PortalForcer.class_1947(blockPos, this.world.getTime()));
			}

			double g = (double)blockPos.getX() + 0.5;
			double h = (double)blockPos.getZ() + 0.5;
			BlockPattern.Result result = PORTAL_BLOCK.method_10350(this.world, blockPos);
			boolean bl2 = result.getForwards().rotateYClockwise().getDirection() == Direction.AxisDirection.NEGATIVE;
			double o = result.getForwards().getAxis() == Direction.Axis.X ? (double)result.getFrontTopLeft().getZ() : (double)result.getFrontTopLeft().getX();
			double p = (double)(result.getFrontTopLeft().getY() + 1) - entity.method_5656().y * (double)result.getHeight();
			if (bl2) {
				o++;
			}

			if (result.getForwards().getAxis() == Direction.Axis.X) {
				h = o + (1.0 - entity.method_5656().x) * (double)result.getWidth() * (double)result.getForwards().rotateYClockwise().getDirection().offset();
			} else {
				g = o + (1.0 - entity.method_5656().x) * (double)result.getWidth() * (double)result.getForwards().rotateYClockwise().getDirection().offset();
			}

			float q = 0.0F;
			float r = 0.0F;
			float s = 0.0F;
			float t = 0.0F;
			if (result.getForwards().getOpposite() == entity.method_5843()) {
				q = 1.0F;
				r = 1.0F;
			} else if (result.getForwards().getOpposite() == entity.method_5843().getOpposite()) {
				q = -1.0F;
				r = -1.0F;
			} else if (result.getForwards().getOpposite() == entity.method_5843().rotateYClockwise()) {
				s = 1.0F;
				t = -1.0F;
			} else {
				s = -1.0F;
				t = 1.0F;
			}

			double u = entity.velocityX;
			double v = entity.velocityZ;
			entity.velocityX = u * (double)q + v * (double)t;
			entity.velocityZ = u * (double)s + v * (double)r;
			entity.yaw = f - (float)(entity.method_5843().getOpposite().getHorizontal() * 90) + (float)(result.getForwards().getHorizontal() * 90);
			if (entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)entity).networkHandler.teleportRequest(g, p, h, entity.yaw, entity.pitch);
				((ServerPlayerEntity)entity).networkHandler.syncWithPlayerPosition();
			} else {
				entity.setPositionAndAngles(g, p, h, entity.yaw, entity.pitch);
			}

			return true;
		} else {
			return false;
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
						this.world.setBlockState(mutable, bl ? Blocks.field_10540.getDefaultState() : Blocks.field_10124.getDefaultState());
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

		BlockState blockState = PORTAL_BLOCK.getDefaultState().with(PortalBlock.field_11310, af == 0 ? Direction.Axis.Z : Direction.Axis.X);

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
