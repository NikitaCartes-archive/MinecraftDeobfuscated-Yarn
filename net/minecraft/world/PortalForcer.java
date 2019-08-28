/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
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
import org.apache.logging.log4j.util.Supplier;
import org.jetbrains.annotations.Nullable;

public class PortalForcer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final PortalBlock PORTAL_BLOCK = (PortalBlock)Blocks.NETHER_PORTAL;
    private final ServerWorld world;
    private final Random random;
    private final Map<ColumnPos, TicketInfo> ticketInfos = Maps.newHashMapWithExpectedSize(4096);
    private final Object2LongMap<ColumnPos> activePortals = new Object2LongOpenHashMap<ColumnPos>();

    public PortalForcer(ServerWorld serverWorld) {
        this.world = serverWorld;
        this.random = new Random(serverWorld.getSeed());
    }

    public boolean usePortal(Entity entity, float f) {
        Vec3d vec3d = entity.getLastPortalDirectionVector();
        Direction direction = entity.getLastPortalDirection();
        BlockPattern.TeleportTarget teleportTarget = this.getPortal(new BlockPos(entity), entity.getVelocity(), direction, vec3d.x, vec3d.y, entity instanceof PlayerEntity);
        if (teleportTarget == null) {
            return false;
        }
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

    @Nullable
    public BlockPattern.TeleportTarget getPortal(BlockPos blockPos, Vec3d vec3d, Direction direction, double d, double e, boolean bl) {
        int i = 128;
        boolean bl2 = true;
        BlockPos blockPos2 = null;
        ColumnPos columnPos = new ColumnPos(blockPos);
        if (!bl && this.activePortals.containsKey(columnPos)) {
            return null;
        }
        TicketInfo ticketInfo = this.ticketInfos.get(columnPos);
        if (ticketInfo != null) {
            blockPos2 = ticketInfo.pos;
            ticketInfo.lastUsedTime = this.world.getTime();
            bl2 = false;
        } else {
            double f = Double.MAX_VALUE;
            for (int j = -128; j <= 128; ++j) {
                for (int k = -128; k <= 128; ++k) {
                    BlockPos blockPos3 = blockPos.add(j, this.world.getEffectiveHeight() - 1 - blockPos.getY(), k);
                    while (blockPos3.getY() >= 0) {
                        BlockPos blockPos4 = blockPos3.down();
                        if (this.world.getBlockState(blockPos3).getBlock() == PORTAL_BLOCK) {
                            blockPos4 = blockPos3.down();
                            while (this.world.getBlockState(blockPos4).getBlock() == PORTAL_BLOCK) {
                                blockPos3 = blockPos4;
                                blockPos4 = blockPos3.down();
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
        }
        if (bl2) {
            this.ticketInfos.put(columnPos, new TicketInfo(blockPos2, this.world.getTime()));
            Supplier[] supplierArray = new Supplier[2];
            supplierArray[0] = this.world.getDimension()::getType;
            supplierArray[1] = () -> columnPos;
            LOGGER.debug("Adding nether portal ticket for {}:{}", supplierArray);
            this.world.method_14178().addTicket(ChunkTicketType.PORTAL, new ChunkPos(blockPos2), 3, columnPos);
        }
        BlockPattern.Result result = PORTAL_BLOCK.findPortal(this.world, blockPos2);
        return result.method_18478(direction, blockPos2, e, vec3d, d);
    }

    public boolean createPortal(Entity entity) {
        int ab;
        int aa;
        int y;
        int x;
        int w;
        int v;
        int u;
        int t;
        double f;
        int s;
        double e;
        int r;
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
        for (r = j - 16; r <= j + 16; ++r) {
            e = (double)r + 0.5 - entity.x;
            for (s = l - 16; s <= l + 16; ++s) {
                f = (double)s + 0.5 - entity.z;
                block2: for (t = this.world.getEffectiveHeight() - 1; t >= 0; --t) {
                    if (!this.world.method_22347(mutable.set(r, t, s))) continue;
                    while (t > 0 && this.world.method_22347(mutable.set(r, t - 1, s))) {
                        --t;
                    }
                    for (u = q; u < q + 4; ++u) {
                        v = u % 2;
                        w = 1 - v;
                        if (u % 4 >= 2) {
                            v = -v;
                            w = -w;
                        }
                        for (x = 0; x < 3; ++x) {
                            for (y = 0; y < 4; ++y) {
                                for (int z = -1; z < 4; ++z) {
                                    aa = r + (y - 1) * v + x * w;
                                    ab = t + z;
                                    int ac = s + (y - 1) * w - x * v;
                                    mutable.set(aa, ab, ac);
                                    if (z < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || z >= 0 && !this.world.method_22347(mutable)) continue block2;
                                }
                            }
                        }
                        double g = (double)t + 0.5 - entity.y;
                        double h = e * e + g * g + f * f;
                        if (!(d < 0.0) && !(h < d)) continue;
                        d = h;
                        m = r;
                        n = t;
                        o = s;
                        p = u % 4;
                    }
                }
            }
        }
        if (d < 0.0) {
            for (r = j - 16; r <= j + 16; ++r) {
                e = (double)r + 0.5 - entity.x;
                for (s = l - 16; s <= l + 16; ++s) {
                    f = (double)s + 0.5 - entity.z;
                    block10: for (t = this.world.getEffectiveHeight() - 1; t >= 0; --t) {
                        if (!this.world.method_22347(mutable.set(r, t, s))) continue;
                        while (t > 0 && this.world.method_22347(mutable.set(r, t - 1, s))) {
                            --t;
                        }
                        for (u = q; u < q + 2; ++u) {
                            v = u % 2;
                            w = 1 - v;
                            for (int x2 = 0; x2 < 4; ++x2) {
                                for (y = -1; y < 4; ++y) {
                                    int z = r + (x2 - 1) * v;
                                    aa = t + y;
                                    ab = s + (x2 - 1) * w;
                                    mutable.set(z, aa, ab);
                                    if (y < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || y >= 0 && !this.world.method_22347(mutable)) continue block10;
                                }
                            }
                            double g = (double)t + 0.5 - entity.y;
                            double h = e * e + g * g + f * f;
                            if (!(d < 0.0) && !(h < d)) continue;
                            d = h;
                            m = r;
                            n = t;
                            o = s;
                            p = u % 2;
                        }
                    }
                }
            }
        }
        r = p;
        int ad = m;
        int ae = n;
        s = o;
        int af = r % 2;
        int ag = 1 - af;
        if (r % 4 >= 2) {
            af = -af;
            ag = -ag;
        }
        if (d < 0.0) {
            ae = n = MathHelper.clamp(n, 70, this.world.getEffectiveHeight() - 10);
            for (t = -1; t <= 1; ++t) {
                for (u = 1; u < 3; ++u) {
                    for (v = -1; v < 3; ++v) {
                        w = ad + (u - 1) * af + t * ag;
                        x = ae + v;
                        y = s + (u - 1) * ag - t * af;
                        boolean bl = v < 0;
                        mutable.set(w, x, y);
                        this.world.setBlockState(mutable, bl ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
        for (t = -1; t < 3; ++t) {
            for (u = -1; u < 4; ++u) {
                if (t != -1 && t != 2 && u != -1 && u != 3) continue;
                mutable.set(ad + t * af, ae + u, s + t * ag);
                this.world.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), 3);
            }
        }
        BlockState blockState = (BlockState)PORTAL_BLOCK.getDefaultState().with(PortalBlock.AXIS, af == 0 ? Direction.Axis.Z : Direction.Axis.X);
        for (u = 0; u < 2; ++u) {
            for (v = 0; v < 3; ++v) {
                mutable.set(ad + u * af, ae + v, s + u * ag);
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
            if (m > l) continue;
            longIterator.remove();
        }
    }

    private void removeOldTickets(long l) {
        long m = l - 300L;
        Iterator<Map.Entry<ColumnPos, TicketInfo>> iterator = this.ticketInfos.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ColumnPos, TicketInfo> entry = iterator.next();
            TicketInfo ticketInfo = entry.getValue();
            if (ticketInfo.lastUsedTime >= m) continue;
            ColumnPos columnPos = entry.getKey();
            Supplier[] supplierArray = new Supplier[2];
            supplierArray[0] = this.world.getDimension()::getType;
            supplierArray[1] = () -> columnPos;
            LOGGER.debug("Removing nether portal ticket for {}:{}", supplierArray);
            this.world.method_14178().removeTicket(ChunkTicketType.PORTAL, new ChunkPos(ticketInfo.pos), 3, columnPos);
            iterator.remove();
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

