/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import org.jetbrains.annotations.Nullable;

public class ZombieSiegeManager {
    private final ServerWorld world;
    private boolean spawned;
    private State state = State.SIEGE_DONE;
    private int remaining;
    private int countdown;
    private int startX;
    private int startY;
    private int startZ;

    public ZombieSiegeManager(ServerWorld serverWorld) {
        this.world = serverWorld;
    }

    public void tick() {
        if (this.world.isDaylight()) {
            this.state = State.SIEGE_DONE;
            this.spawned = false;
            return;
        }
        float f = this.world.getSkyAngle(0.0f);
        if ((double)f == 0.5) {
            State state = this.state = this.world.random.nextInt(10) == 0 ? State.SIEGE_TONIGHT : State.SIEGE_DONE;
        }
        if (this.state == State.SIEGE_DONE) {
            return;
        }
        if (!this.spawned) {
            if (this.spawn()) {
                this.spawned = true;
            } else {
                return;
            }
        }
        if (this.countdown > 0) {
            --this.countdown;
            return;
        }
        this.countdown = 2;
        if (this.remaining > 0) {
            this.trySpawnZombie();
            --this.remaining;
        } else {
            this.state = State.SIEGE_DONE;
        }
    }

    private boolean spawn() {
        for (PlayerEntity playerEntity : this.world.getPlayers()) {
            BlockPos blockPos;
            if (playerEntity.isSpectator() || !this.world.isNearOccupiedPointOfInterest(blockPos = playerEntity.getBlockPos())) continue;
            for (int i = 0; i < 10; ++i) {
                float f = this.world.random.nextFloat() * ((float)Math.PI * 2);
                this.startX = blockPos.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0f);
                this.startY = blockPos.getY();
                this.startZ = blockPos.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0f);
                if (this.getSpawnVector(new BlockPos(this.startX, this.startY, this.startZ)) == null) continue;
                this.countdown = 0;
                this.remaining = 20;
                break;
            }
            return true;
        }
        return false;
    }

    private void trySpawnZombie() {
        ZombieEntity zombieEntity;
        Vec3d vec3d = this.getSpawnVector(new BlockPos(this.startX, this.startY, this.startZ));
        if (vec3d == null) {
            return;
        }
        try {
            zombieEntity = new ZombieEntity(this.world);
            zombieEntity.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(zombieEntity)), SpawnType.EVENT, null, null);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        zombieEntity.setPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, this.world.random.nextFloat() * 360.0f, 0.0f);
        this.world.spawnEntity(zombieEntity);
    }

    @Nullable
    private Vec3d getSpawnVector(BlockPos blockPos) {
        for (int i = 0; i < 10; ++i) {
            int k;
            int l;
            int j = blockPos.getX() + this.world.random.nextInt(16) - 8;
            BlockPos blockPos2 = new BlockPos(j, l = this.world.getTop(Heightmap.Type.WORLD_SURFACE, j, k = blockPos.getZ() + this.world.random.nextInt(16) - 8), k);
            if (!this.world.isNearOccupiedPointOfInterest(blockPos2) || !SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, this.world, blockPos2, EntityType.ZOMBIE)) continue;
            return new Vec3d(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
        }
        return null;
    }

    static enum State {
        SIEGE_CAN_ACTIVATE,
        SIEGE_TONIGHT,
        SIEGE_DONE;

    }
}

