/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.projectile;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FishingBobberEntity
extends ProjectileEntity {
    private final Random velocityRandom = new Random();
    private boolean caughtFish;
    private int outOfOpenWaterTicks;
    private static final TrackedData<Integer> HOOK_ENTITY_ID = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CAUGHT_FISH = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int removalTimer;
    private int hookCountdown;
    private int waitCountdown;
    private int fishTravelCountdown;
    private float fishAngle;
    private boolean inOpenWater = true;
    private Entity hookedEntity;
    private State state = State.FLYING;
    private final int luckOfTheSeaLevel;
    private final int lureLevel;

    private FishingBobberEntity(World world, PlayerEntity owner, int lureLevel, int luckOfTheSeaLevel) {
        super((EntityType<? extends ProjectileEntity>)EntityType.FISHING_BOBBER, world);
        this.ignoreCameraFrustum = true;
        this.setOwner(owner);
        owner.fishHook = this;
        this.luckOfTheSeaLevel = Math.max(0, lureLevel);
        this.lureLevel = Math.max(0, luckOfTheSeaLevel);
    }

    @Environment(value=EnvType.CLIENT)
    public FishingBobberEntity(World world, PlayerEntity thrower, double x, double y, double z) {
        this(world, thrower, 0, 0);
        this.setPosition(x, y, z);
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
    }

    public FishingBobberEntity(PlayerEntity thrower, World world, int lureLevel, int luckOfTheSeaLevel) {
        this(world, thrower, lureLevel, luckOfTheSeaLevel);
        float f = thrower.pitch;
        float g = thrower.yaw;
        float h = MathHelper.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float i = MathHelper.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float j = -MathHelper.cos(-f * ((float)Math.PI / 180));
        float k = MathHelper.sin(-f * ((float)Math.PI / 180));
        double d = thrower.getX() - (double)i * 0.3;
        double e = thrower.getEyeY();
        double l = thrower.getZ() - (double)h * 0.3;
        this.refreshPositionAndAngles(d, e, l, g, f);
        Vec3d vec3d = new Vec3d(-i, MathHelper.clamp(-(k / j), -5.0f, 5.0f), -h);
        double m = vec3d.length();
        vec3d = vec3d.multiply(0.6 / m + 0.5 + this.random.nextGaussian() * 0.0045, 0.6 / m + 0.5 + this.random.nextGaussian() * 0.0045, 0.6 / m + 0.5 + this.random.nextGaussian() * 0.0045);
        this.setVelocity(vec3d);
        this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d.y, MathHelper.sqrt(FishingBobberEntity.squaredHorizontalLength(vec3d))) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(HOOK_ENTITY_ID, 0);
        this.getDataTracker().startTracking(CAUGHT_FISH, false);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (HOOK_ENTITY_ID.equals(data)) {
            int i = this.getDataTracker().get(HOOK_ENTITY_ID);
            Entity entity = this.hookedEntity = i > 0 ? this.world.getEntityById(i - 1) : null;
        }
        if (CAUGHT_FISH.equals(data)) {
            this.caughtFish = this.getDataTracker().get(CAUGHT_FISH);
            if (this.caughtFish) {
                this.setVelocity(this.getVelocity().x, -0.4f * MathHelper.nextFloat(this.velocityRandom, 0.6f, 1.0f), this.getVelocity().z);
            }
        }
        super.onTrackedDataSet(data);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double d = 64.0;
        return distance < 4096.0;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
    }

    @Override
    public void tick() {
        boolean bl;
        this.velocityRandom.setSeed(this.getUuid().getLeastSignificantBits() ^ this.world.getTime());
        super.tick();
        PlayerEntity playerEntity = this.getPlayerOwner();
        if (playerEntity == null) {
            this.remove();
            return;
        }
        if (!this.world.isClient && this.removeIfInvalid(playerEntity)) {
            return;
        }
        if (this.onGround) {
            ++this.removalTimer;
            if (this.removalTimer >= 1200) {
                this.remove();
                return;
            }
        } else {
            this.removalTimer = 0;
        }
        float f = 0.0f;
        BlockPos blockPos = this.getBlockPos();
        FluidState fluidState = this.world.getFluidState(blockPos);
        if (fluidState.isIn(FluidTags.WATER)) {
            f = fluidState.getHeight(this.world, blockPos);
        }
        boolean bl2 = bl = f > 0.0f;
        if (this.state == State.FLYING) {
            if (this.hookedEntity != null) {
                this.setVelocity(Vec3d.ZERO);
                this.state = State.HOOKED_IN_ENTITY;
                return;
            }
            if (bl) {
                this.setVelocity(this.getVelocity().multiply(0.3, 0.2, 0.3));
                this.state = State.BOBBING;
                return;
            }
            this.checkForCollision();
        } else {
            if (this.state == State.HOOKED_IN_ENTITY) {
                if (this.hookedEntity != null) {
                    if (this.hookedEntity.removed) {
                        this.hookedEntity = null;
                        this.state = State.FLYING;
                    } else {
                        this.setPosition(this.hookedEntity.getX(), this.hookedEntity.getBodyY(0.8), this.hookedEntity.getZ());
                    }
                }
                return;
            }
            if (this.state == State.BOBBING) {
                Vec3d vec3d = this.getVelocity();
                double d = this.getY() + vec3d.y - (double)blockPos.getY() - (double)f;
                if (Math.abs(d) < 0.01) {
                    d += Math.signum(d) * 0.1;
                }
                this.setVelocity(vec3d.x * 0.9, vec3d.y - d * (double)this.random.nextFloat() * 0.2, vec3d.z * 0.9);
                this.inOpenWater = this.hookCountdown > 0 || this.fishTravelCountdown > 0 ? this.inOpenWater && this.outOfOpenWaterTicks < 10 && this.isOpenOrWaterAround(blockPos) : true;
                if (bl) {
                    this.outOfOpenWaterTicks = Math.max(0, this.outOfOpenWaterTicks - 1);
                    if (this.caughtFish) {
                        this.setVelocity(this.getVelocity().add(0.0, -0.1 * (double)this.velocityRandom.nextFloat() * (double)this.velocityRandom.nextFloat(), 0.0));
                    }
                    if (!this.world.isClient) {
                        this.tickFishingLogic(blockPos);
                    }
                } else {
                    this.outOfOpenWaterTicks = Math.min(10, this.outOfOpenWaterTicks + 1);
                }
            }
        }
        if (!fluidState.isIn(FluidTags.WATER)) {
            this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
        }
        this.move(MovementType.SELF, this.getVelocity());
        this.method_26962();
        if (this.state == State.FLYING && (this.onGround || this.horizontalCollision)) {
            this.setVelocity(Vec3d.ZERO);
        }
        double e = 0.92;
        this.setVelocity(this.getVelocity().multiply(0.92));
        this.refreshPosition();
    }

    private boolean removeIfInvalid(PlayerEntity player) {
        boolean bl2;
        ItemStack itemStack = player.getMainHandStack();
        ItemStack itemStack2 = player.getOffHandStack();
        boolean bl = itemStack.getItem() == Items.FISHING_ROD;
        boolean bl3 = bl2 = itemStack2.getItem() == Items.FISHING_ROD;
        if (player.removed || !player.isAlive() || !bl && !bl2 || this.squaredDistanceTo(player) > 1024.0) {
            this.remove();
            return true;
        }
        return false;
    }

    private void checkForCollision() {
        HitResult hitResult = ProjectileUtil.getCollision(this, this::method_26958);
        this.onCollision(hitResult);
    }

    @Override
    protected boolean method_26958(Entity entity) {
        return super.method_26958(entity) || entity.isAlive() && entity instanceof ItemEntity;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.world.isClient) {
            this.hookedEntity = entityHitResult.getEntity();
            this.updateHookedEntityId();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.setVelocity(this.getVelocity().normalize().multiply(blockHitResult.squaredDistanceTo(this)));
    }

    private void updateHookedEntityId() {
        this.getDataTracker().set(HOOK_ENTITY_ID, this.hookedEntity.getEntityId() + 1);
    }

    private void tickFishingLogic(BlockPos pos) {
        ServerWorld serverWorld = (ServerWorld)this.world;
        int i = 1;
        BlockPos blockPos = pos.up();
        if (this.random.nextFloat() < 0.25f && this.world.hasRain(blockPos)) {
            ++i;
        }
        if (this.random.nextFloat() < 0.5f && !this.world.isSkyVisible(blockPos)) {
            --i;
        }
        if (this.hookCountdown > 0) {
            --this.hookCountdown;
            if (this.hookCountdown <= 0) {
                this.waitCountdown = 0;
                this.fishTravelCountdown = 0;
                this.getDataTracker().set(CAUGHT_FISH, false);
            }
        } else if (this.fishTravelCountdown > 0) {
            this.fishTravelCountdown -= i;
            if (this.fishTravelCountdown > 0) {
                double j;
                double e;
                this.fishAngle = (float)((double)this.fishAngle + this.random.nextGaussian() * 4.0);
                float f = this.fishAngle * ((float)Math.PI / 180);
                float g = MathHelper.sin(f);
                float h = MathHelper.cos(f);
                double d = this.getX() + (double)(g * (float)this.fishTravelCountdown * 0.1f);
                BlockState blockState = serverWorld.getBlockState(new BlockPos(d, (e = (double)((float)MathHelper.floor(this.getY()) + 1.0f)) - 1.0, j = this.getZ() + (double)(h * (float)this.fishTravelCountdown * 0.1f)));
                if (blockState.isOf(Blocks.WATER)) {
                    if (this.random.nextFloat() < 0.15f) {
                        serverWorld.spawnParticles(ParticleTypes.BUBBLE, d, e - (double)0.1f, j, 1, g, 0.1, h, 0.0);
                    }
                    float k = g * 0.04f;
                    float l = h * 0.04f;
                    serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, l, 0.01, -k, 1.0);
                    serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, -l, 0.01, k, 1.0);
                }
            } else {
                this.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
                double m = this.getY() + 0.5;
                serverWorld.spawnParticles(ParticleTypes.BUBBLE, this.getX(), m, this.getZ(), (int)(1.0f + this.getWidth() * 20.0f), this.getWidth(), 0.0, this.getWidth(), 0.2f);
                serverWorld.spawnParticles(ParticleTypes.FISHING, this.getX(), m, this.getZ(), (int)(1.0f + this.getWidth() * 20.0f), this.getWidth(), 0.0, this.getWidth(), 0.2f);
                this.hookCountdown = MathHelper.nextInt(this.random, 20, 40);
                this.getDataTracker().set(CAUGHT_FISH, true);
            }
        } else if (this.waitCountdown > 0) {
            this.waitCountdown -= i;
            float f = 0.15f;
            if (this.waitCountdown < 20) {
                f = (float)((double)f + (double)(20 - this.waitCountdown) * 0.05);
            } else if (this.waitCountdown < 40) {
                f = (float)((double)f + (double)(40 - this.waitCountdown) * 0.02);
            } else if (this.waitCountdown < 60) {
                f = (float)((double)f + (double)(60 - this.waitCountdown) * 0.01);
            }
            if (this.random.nextFloat() < f) {
                double j;
                double e;
                float g = MathHelper.nextFloat(this.random, 0.0f, 360.0f) * ((float)Math.PI / 180);
                float h = MathHelper.nextFloat(this.random, 25.0f, 60.0f);
                double d = this.getX() + (double)(MathHelper.sin(g) * h * 0.1f);
                BlockState blockState = serverWorld.getBlockState(new BlockPos(d, (e = (double)((float)MathHelper.floor(this.getY()) + 1.0f)) - 1.0, j = this.getZ() + (double)(MathHelper.cos(g) * h * 0.1f)));
                if (blockState.isOf(Blocks.WATER)) {
                    serverWorld.spawnParticles(ParticleTypes.SPLASH, d, e, j, 2 + this.random.nextInt(2), 0.1f, 0.0, 0.1f, 0.0);
                }
            }
            if (this.waitCountdown <= 0) {
                this.fishAngle = MathHelper.nextFloat(this.random, 0.0f, 360.0f);
                this.fishTravelCountdown = MathHelper.nextInt(this.random, 20, 80);
            }
        } else {
            this.waitCountdown = MathHelper.nextInt(this.random, 100, 600);
            this.waitCountdown -= this.lureLevel * 20 * 5;
        }
    }

    private boolean isOpenOrWaterAround(BlockPos pos) {
        PositionType positionType = PositionType.INVALID;
        for (int i = -1; i <= 2; ++i) {
            PositionType positionType2 = this.getPositionType(pos.add(-2, i, -2), pos.add(2, i, 2));
            switch (positionType2) {
                case INVALID: {
                    return false;
                }
                case ABOVE_WATER: {
                    if (positionType != PositionType.INVALID) break;
                    return false;
                }
                case INSIDE_WATER: {
                    if (positionType != PositionType.ABOVE_WATER) break;
                    return false;
                }
            }
            positionType = positionType2;
        }
        return true;
    }

    private PositionType getPositionType(BlockPos start, BlockPos end) {
        return BlockPos.stream(start, end).map(this::getPositionType).reduce((positionType, positionType2) -> positionType == positionType2 ? positionType : PositionType.INVALID).orElse(PositionType.INVALID);
    }

    private PositionType getPositionType(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        if (blockState.isAir() || blockState.isOf(Blocks.LILY_PAD)) {
            return PositionType.ABOVE_WATER;
        }
        FluidState fluidState = blockState.getFluidState();
        if (fluidState.isIn(FluidTags.WATER) && fluidState.isStill() && blockState.getCollisionShape(this.world, pos).isEmpty()) {
            return PositionType.INSIDE_WATER;
        }
        return PositionType.INVALID;
    }

    public boolean isInOpenWater() {
        return this.inOpenWater;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
    }

    public int use(ItemStack usedItem) {
        PlayerEntity playerEntity = this.getPlayerOwner();
        if (this.world.isClient || playerEntity == null) {
            return 0;
        }
        int i = 0;
        if (this.hookedEntity != null) {
            this.pullHookedEntity();
            Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerEntity, usedItem, this, Collections.emptyList());
            this.world.sendEntityStatus(this, (byte)31);
            i = this.hookedEntity instanceof ItemEntity ? 3 : 5;
        } else if (this.hookCountdown > 0) {
            LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world).parameter(LootContextParameters.ORIGIN, this.getPos()).parameter(LootContextParameters.TOOL, usedItem).parameter(LootContextParameters.THIS_ENTITY, this).random(this.random).luck((float)this.luckOfTheSeaLevel + playerEntity.getLuck());
            LootTable lootTable = this.world.getServer().getLootManager().getTable(LootTables.FISHING_GAMEPLAY);
            List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.FISHING));
            Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerEntity, usedItem, this, list);
            for (ItemStack itemStack : list) {
                ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), itemStack);
                double d = playerEntity.getX() - this.getX();
                double e = playerEntity.getY() - this.getY();
                double f = playerEntity.getZ() - this.getZ();
                double g = 0.1;
                itemEntity.setVelocity(d * 0.1, e * 0.1 + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08, f * 0.1);
                this.world.spawnEntity(itemEntity);
                playerEntity.world.spawnEntity(new ExperienceOrbEntity(playerEntity.world, playerEntity.getX(), playerEntity.getY() + 0.5, playerEntity.getZ() + 0.5, this.random.nextInt(6) + 1));
                if (!itemStack.getItem().isIn(ItemTags.FISHES)) continue;
                playerEntity.increaseStat(Stats.FISH_CAUGHT, 1);
            }
            i = 1;
        }
        if (this.onGround) {
            i = 2;
        }
        this.remove();
        return i;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 31 && this.world.isClient && this.hookedEntity instanceof PlayerEntity && ((PlayerEntity)this.hookedEntity).isMainPlayer()) {
            this.pullHookedEntity();
        }
        super.handleStatus(status);
    }

    protected void pullHookedEntity() {
        Entity entity = this.getOwner();
        if (entity == null) {
            return;
        }
        Vec3d vec3d = new Vec3d(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ()).multiply(0.1);
        this.hookedEntity.setVelocity(this.hookedEntity.getVelocity().add(vec3d));
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    public void remove() {
        super.remove();
        PlayerEntity playerEntity = this.getPlayerOwner();
        if (playerEntity != null) {
            playerEntity.fishHook = null;
        }
    }

    @Nullable
    public PlayerEntity getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
    }

    @Nullable
    public Entity getHookedEntity() {
        return this.hookedEntity;
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        Entity entity = this.getOwner();
        return new EntitySpawnS2CPacket(this, entity == null ? this.getEntityId() : entity.getEntityId());
    }

    static enum PositionType {
        ABOVE_WATER,
        INSIDE_WATER,
        INVALID;

    }

    static enum State {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING;

    }
}

