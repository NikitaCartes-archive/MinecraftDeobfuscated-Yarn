/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
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
import org.jetbrains.annotations.Nullable;

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
        BlockPattern.TeleportTarget teleportTarget = this.getPortal(entity.getBlockPos(), entity.getVelocity(), direction, vec3d.x, vec3d.y, entity instanceof PlayerEntity);
        if (teleportTarget == null) {
            return false;
        }
        Vec3d vec3d2 = teleportTarget.pos;
        Vec3d vec3d3 = teleportTarget.velocity;
        entity.setVelocity(vec3d3);
        entity.yaw = yawOffset + (float)teleportTarget.yaw;
        entity.positAfterTeleport(vec3d2.x, vec3d2.y, vec3d2.z);
        return true;
    }

    @Nullable
    public BlockPattern.TeleportTarget getPortal(BlockPos pos, Vec3d vec3d, Direction direction, double x, double y, boolean canActivate) {
        PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
        pointOfInterestStorage.preloadChunks(this.world, pos, 128);
        List list = pointOfInterestStorage.getInSquare(pointOfInterestType -> pointOfInterestType == PointOfInterestType.NETHER_PORTAL, pos, 128, PointOfInterestStorage.OccupationStatus.ANY).collect(Collectors.toList());
        Optional<PointOfInterest> optional = list.stream().min(Comparator.comparingDouble(pointOfInterest -> pointOfInterest.getPos().getSquaredDistance(pos)).thenComparingInt(pointOfInterest -> pointOfInterest.getPos().getY()));
        return optional.map(pointOfInterest -> {
            BlockPos blockPos = pointOfInterest.getPos();
            this.world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(blockPos), 3, blockPos);
            BlockPattern.Result result = NetherPortalBlock.findPortal(this.world, blockPos);
            return result.getTeleportTarget(direction, blockPos, y, vec3d, x);
        }).orElse(null);
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
        int j = MathHelper.floor(entity.getX());
        int k = MathHelper.floor(entity.getY());
        int l = MathHelper.floor(entity.getZ());
        int m = j;
        int n = k;
        int o = l;
        int p = 0;
        int q = this.random.nextInt(4);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (r = j - 16; r <= j + 16; ++r) {
            e = (double)r + 0.5 - entity.getX();
            for (s = l - 16; s <= l + 16; ++s) {
                f = (double)s + 0.5 - entity.getZ();
                block2: for (t = this.world.getDimensionHeight() - 1; t >= 0; --t) {
                    if (!this.world.isAir(mutable.set(r, t, s))) continue;
                    while (t > 0 && this.world.isAir(mutable.set(r, t - 1, s))) {
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
                                    if (z < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || z >= 0 && !this.world.isAir(mutable)) continue block2;
                                }
                            }
                        }
                        double g = (double)t + 0.5 - entity.getY();
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
                e = (double)r + 0.5 - entity.getX();
                for (s = l - 16; s <= l + 16; ++s) {
                    f = (double)s + 0.5 - entity.getZ();
                    block10: for (t = this.world.getDimensionHeight() - 1; t >= 0; --t) {
                        if (!this.world.isAir(mutable.set(r, t, s))) continue;
                        while (t > 0 && this.world.isAir(mutable.set(r, t - 1, s))) {
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
                                    if (y < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || y >= 0 && !this.world.isAir(mutable)) continue block10;
                                }
                            }
                            double g = (double)t + 0.5 - entity.getY();
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
            ae = n = MathHelper.clamp(n, 70, this.world.getDimensionHeight() - 10);
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
        BlockState blockState = (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, af == 0 ? Direction.Axis.Z : Direction.Axis.X);
        for (u = 0; u < 2; ++u) {
            for (v = 0; v < 3; ++v) {
                mutable.set(ad + u * af, ae + v, s + u * ag);
                this.world.setBlockState(mutable, blockState, 18);
            }
        }
        return true;
    }
}

