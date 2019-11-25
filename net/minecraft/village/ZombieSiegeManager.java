/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class ZombieSiegeManager {
    private boolean spawned;
    private State state = State.SIEGE_DONE;
    private int remaining;
    private int countdown;
    private int startX;
    private int startY;
    private int startZ;

    public int tick(ServerWorld serverWorld, boolean bl, boolean bl2) {
        if (serverWorld.isDay() || !bl) {
            this.state = State.SIEGE_DONE;
            this.spawned = false;
            return 0;
        }
        float f = serverWorld.getSkyAngle(0.0f);
        if ((double)f == 0.5) {
            State state = this.state = serverWorld.random.nextInt(10) == 0 ? State.SIEGE_TONIGHT : State.SIEGE_DONE;
        }
        if (this.state == State.SIEGE_DONE) {
            return 0;
        }
        if (!this.spawned) {
            if (this.spawn(serverWorld)) {
                this.spawned = true;
            } else {
                return 0;
            }
        }
        if (this.countdown > 0) {
            --this.countdown;
            return 0;
        }
        this.countdown = 2;
        if (this.remaining > 0) {
            this.trySpawnZombie(serverWorld);
            --this.remaining;
        } else {
            this.state = State.SIEGE_DONE;
        }
        return 1;
    }

    private boolean spawn(ServerWorld serverWorld) {
        for (PlayerEntity playerEntity : serverWorld.getPlayers()) {
            BlockPos blockPos;
            if (playerEntity.isSpectator() || !serverWorld.isNearOccupiedPointOfInterest(blockPos = playerEntity.getBlockPos()) || serverWorld.getBiome(blockPos).getCategory() == Biome.Category.MUSHROOM) continue;
            for (int i = 0; i < 10; ++i) {
                float f = serverWorld.random.nextFloat() * ((float)Math.PI * 2);
                this.startX = blockPos.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0f);
                this.startY = blockPos.getY();
                this.startZ = blockPos.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0f);
                if (this.getSpawnVector(serverWorld, new BlockPos(this.startX, this.startY, this.startZ)) == null) continue;
                this.countdown = 0;
                this.remaining = 20;
                break;
            }
            return true;
        }
        return false;
    }

    private void trySpawnZombie(ServerWorld serverWorld) {
        ZombieEntity zombieEntity;
        Vec3d vec3d = this.getSpawnVector(serverWorld, new BlockPos(this.startX, this.startY, this.startZ));
        if (vec3d == null) {
            return;
        }
        try {
            zombieEntity = new ZombieEntity(serverWorld);
            zombieEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(new BlockPos(zombieEntity)), SpawnType.EVENT, null, null);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        zombieEntity.setPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, serverWorld.random.nextFloat() * 360.0f, 0.0f);
        serverWorld.spawnEntity(zombieEntity);
    }

    @Nullable
    private Vec3d getSpawnVector(ServerWorld serverWorld, BlockPos blockPos) {
        for (int i = 0; i < 10; ++i) {
            int k;
            int l;
            int j = blockPos.getX() + serverWorld.random.nextInt(16) - 8;
            BlockPos blockPos2 = new BlockPos(j, l = serverWorld.getTopY(Heightmap.Type.WORLD_SURFACE, j, k = blockPos.getZ() + serverWorld.random.nextInt(16) - 8), k);
            if (!serverWorld.isNearOccupiedPointOfInterest(blockPos2) || !HostileEntity.canSpawnInDark(EntityType.ZOMBIE, serverWorld, SpawnType.EVENT, blockPos2, serverWorld.random)) continue;
            return new Vec3d((double)blockPos2.getX() + 0.5, blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
        }
        return null;
    }

    static enum State {
        SIEGE_CAN_ACTIVATE,
        SIEGE_TONIGHT,
        SIEGE_DONE;

    }
}

