package net.minecraft.world;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class PortalForcer {
	private final ServerWorld world;
	private final Random random;

	public PortalForcer(ServerWorld world) {
		this.world = world;
		this.random = new Random(world.getSeed());
	}

	public boolean usePortal(Entity entity, float yawOffset) {
		Vec3d vec3d = entity.getLastNetherPortalDirectionVector();
		Direction direction = entity.getLastNetherPortalDirection();
		BlockPattern.TeleportTarget teleportTarget = this.getPortal(
			entity.getBlockPos(), entity.getVelocity(), direction, vec3d.x, vec3d.y, entity instanceof PlayerEntity
		);
		if (teleportTarget == null) {
			return false;
		} else {
			Vec3d vec3d2 = teleportTarget.pos;
			Vec3d vec3d3 = teleportTarget.velocity;
			entity.setVelocity(vec3d3);
			entity.yaw = yawOffset + (float)teleportTarget.yaw;
			entity.positAfterTeleport(vec3d2.x, vec3d2.y, vec3d2.z);
			return true;
		}
	}

	@Nullable
	public BlockPattern.TeleportTarget getPortal(BlockPos pos, Vec3d vec3d, Direction direction, double x, double y, boolean canActivate) {
		PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
		pointOfInterestStorage.preloadChunks(this.world, pos, 128);
		List<PointOfInterest> list = (List<PointOfInterest>)pointOfInterestStorage.getInSquare(
				pointOfInterestType -> pointOfInterestType == PointOfInterestType.NETHER_PORTAL, pos, 128, PointOfInterestStorage.OccupationStatus.ANY
			)
			.collect(Collectors.toList());
		Optional<PointOfInterest> optional = list.stream()
			.min(
				Comparator.comparingDouble(pointOfInterest -> pointOfInterest.getPos().getSquaredDistance(pos))
					.thenComparingInt(pointOfInterest -> pointOfInterest.getPos().getY())
			);
		return (BlockPattern.TeleportTarget)optional.map(pointOfInterest -> {
			BlockPos blockPos = pointOfInterest.getPos();
			this.world.getChunkManager().addTicket(ChunkTicketType.field_19280, new ChunkPos(blockPos), 3, blockPos);
			BlockPattern.Result result = NetherPortalBlock.findPortal(this.world, blockPos);
			return result.getTeleportTarget(direction, blockPos, y, vec3d, x);
		}).orElse(null);
	}

	public boolean createPortal(Entity entity) {
		int i = 16;
		double d = -1.0;
		int j = MathHelper.floor(entity.getX());
		int k = MathHelper.floor(entity.getY());
		int l = MathHelper.floor(entity.getZ());
		int m = j;
		int n = k;
		int o = l;
		int p = 0;
		int q = this.random.nextInt(4);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int r = j - 16; r <= j + 16; r++) {
			double e = (double)r + 0.5 - entity.getX();

			for (int s = l - 16; s <= l + 16; s++) {
				double f = (double)s + 0.5 - entity.getZ();

				label279:
				for (int t = this.world.getDimensionHeight() - 1; t >= 0; t--) {
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
										if (z < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || z >= 0 && !this.world.isAir(mutable)) {
											continue label279;
										}
									}
								}
							}

							double g = (double)t + 0.5 - entity.getY();
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
				double e = (double)r + 0.5 - entity.getX();

				for (int s = l - 16; s <= l + 16; s++) {
					double f = (double)s + 0.5 - entity.getZ();

					label216:
					for (int tx = this.world.getDimensionHeight() - 1; tx >= 0; tx--) {
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
										if (y < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || y >= 0 && !this.world.isAir(mutable)) {
											continue label216;
										}
									}
								}

								double g = (double)tx + 0.5 - entity.getY();
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
			n = MathHelper.clamp(n, 70, this.world.getDimensionHeight() - 10);
			ae = n;

			for (int txx = -1; txx <= 1; txx++) {
				for (int u = 1; u < 3; u++) {
					for (int vx = -1; vx < 3; vx++) {
						int wx = ad + (u - 1) * af + txx * ag;
						int x = ae + vx;
						int yx = s + (u - 1) * ag - txx * af;
						boolean bl = vx < 0;
						mutable.set(wx, x, yx);
						this.world.setBlockState(mutable, bl ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
					}
				}
			}
		}

		for (int txx = -1; txx < 3; txx++) {
			for (int u = -1; u < 4; u++) {
				if (txx == -1 || txx == 2 || u == -1 || u == 3) {
					mutable.set(ad + txx * af, ae + u, s + txx * ag);
					this.world.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), 3);
				}
			}
		}

		BlockState blockState = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, af == 0 ? Direction.Axis.Z : Direction.Axis.X);

		for (int ux = 0; ux < 2; ux++) {
			for (int vx = 0; vx < 3; vx++) {
				mutable.set(ad + ux * af, ae + vx, s + ux * ag);
				this.world.setBlockState(mutable, blockState, 18);
			}
		}

		return true;
	}
}
