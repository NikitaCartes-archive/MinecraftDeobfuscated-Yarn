package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PortalForcer {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final PortalBlock PORTAL_BLOCK = (PortalBlock)Blocks.field_10316;
	private final ServerWorld world;
	private final Random random;
	private final Map<ColumnPos, PortalForcer.TicketInfo> ticketInfos = Maps.<ColumnPos, PortalForcer.TicketInfo>newHashMapWithExpectedSize(4096);
	private final Object2LongMap<ColumnPos> activePortals = new Object2LongOpenHashMap<>();

	public PortalForcer(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.random = new Random(serverWorld.getSeed());
	}

	public boolean usePortal(Entity entity, float f) {
		Vec3d vec3d = entity.method_5656();
		Direction direction = entity.method_5843();
		BlockPattern.TeleportTarget teleportTarget = this.getPortal(
			new BlockPos(entity), entity.getVelocity(), direction, vec3d.x, vec3d.y, entity instanceof PlayerEntity
		);
		if (teleportTarget == null) {
			return false;
		} else {
			Vec3d vec3d2 = teleportTarget.pos;
			Vec3d vec3d3 = teleportTarget.velocity;
			entity.setVelocity(vec3d3);
			entity.yaw = f + (float)teleportTarget.yaw;
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
	public BlockPattern.TeleportTarget getPortal(BlockPos blockPos, Vec3d vec3d, Direction direction, double d, double e, boolean bl) {
		int i = 128;
		boolean bl2 = true;
		BlockPos blockPos2 = null;
		ColumnPos columnPos = new ColumnPos(blockPos);
		if (!bl && this.activePortals.containsKey(columnPos)) {
			return null;
		} else {
			PortalForcer.TicketInfo ticketInfo = (PortalForcer.TicketInfo)this.ticketInfos.get(columnPos);
			if (ticketInfo != null) {
				blockPos2 = ticketInfo.pos;
				ticketInfo.lastUsedTime = this.world.getTime();
				bl2 = false;
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
				long l = this.world.getTime() + 300L;
				this.activePortals.put(columnPos, l);
				return null;
			} else {
				if (bl2) {
					this.ticketInfos.put(columnPos, new PortalForcer.TicketInfo(blockPos2, this.world.getTime()));
					LOGGER.debug("Adding nether portal ticket for {}:{}", this.world.getDimension()::getType, () -> columnPos);
					this.world.method_14178().addTicket(ChunkTicketType.field_19280, new ChunkPos(blockPos2), 3, columnPos);
				}

				BlockPattern.Result result = PORTAL_BLOCK.findPortal(this.world, blockPos2);
				return result.method_18478(direction, blockPos2, e, vec3d, d);
			}
		}
	}

	public boolean createPortal(Entity entity) {
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
										if (z < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || z >= 0 && !this.world.isAir(mutable)) {
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
										if (y < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || y >= 0 && !this.world.isAir(mutable)) {
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
			this.removeOldActivePortals(l);
			this.removeOldTickets(l);
		}
	}

	private void removeOldActivePortals(long l) {
		LongIterator longIterator = this.activePortals.values().iterator();

		while (longIterator.hasNext()) {
			long m = longIterator.nextLong();
			if (m <= l) {
				longIterator.remove();
			}
		}
	}

	private void removeOldTickets(long l) {
		long m = l - 300L;
		Iterator<Entry<ColumnPos, PortalForcer.TicketInfo>> iterator = this.ticketInfos.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<ColumnPos, PortalForcer.TicketInfo> entry = (Entry<ColumnPos, PortalForcer.TicketInfo>)iterator.next();
			PortalForcer.TicketInfo ticketInfo = (PortalForcer.TicketInfo)entry.getValue();
			if (ticketInfo.lastUsedTime < m) {
				ColumnPos columnPos = (ColumnPos)entry.getKey();
				LOGGER.debug("Removing nether portal ticket for {}:{}", this.world.getDimension()::getType, () -> columnPos);
				this.world.method_14178().removeTicket(ChunkTicketType.field_19280, new ChunkPos(ticketInfo.pos), 3, columnPos);
				iterator.remove();
			}
		}
	}

	static class TicketInfo {
		public final BlockPos pos;
		public long lastUsedTime;

		public TicketInfo(BlockPos blockPos, long l) {
			this.pos = blockPos;
			this.lastUsedTime = l;
		}
	}
}
