/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=SkinOverlayOwner.class)})
public class WitherEntity
extends HostileEntity
implements SkinOverlayOwner,
RangedAttackMob {
    private static final TrackedData<Integer> TRACKED_ENTITY_ID_1 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TRACKED_ENTITY_ID_2 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TRACKED_ENTITY_ID_3 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final List<TrackedData<Integer>> TRACKED_ENTITY_IDS = ImmutableList.of(TRACKED_ENTITY_ID_1, TRACKED_ENTITY_ID_2, TRACKED_ENTITY_ID_3);
    private static final TrackedData<Integer> INVUL_TIMER = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final float[] sideHeadPitches = new float[2];
    private final float[] sideHeadYaws = new float[2];
    private final float[] prevSideHeadPitches = new float[2];
    private final float[] prevSideHeadYaws = new float[2];
    private final int[] field_7091 = new int[2];
    private final int[] field_7092 = new int[2];
    private int field_7082;
    private final ServerBossBar bossBar = (ServerBossBar)new ServerBossBar(this.getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS).setDarkenSky(true);
    private static final Predicate<LivingEntity> CAN_ATTACK_PREDICATE = livingEntity -> livingEntity.getGroup() != EntityGroup.UNDEAD && livingEntity.method_6102();
    private static final TargetPredicate HEAD_TARGET_PREDICATE = new TargetPredicate().setBaseMaxDistance(20.0).setPredicate(CAN_ATTACK_PREDICATE);

    public WitherEntity(EntityType<? extends WitherEntity> entityType, World world) {
        super((EntityType<? extends HostileEntity>)entityType, world);
        this.setHealth(this.getMaximumHealth());
        this.getNavigation().setCanSwim(true);
        this.experiencePoints = 50;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new DescendAtHalfHealthGoal());
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 40, 20.0f));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<MobEntity>(this, MobEntity.class, 0, false, false, CAN_ATTACK_PREDICATE));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TRACKED_ENTITY_ID_1, 0);
        this.dataTracker.startTracking(TRACKED_ENTITY_ID_2, 0);
        this.dataTracker.startTracking(TRACKED_ENTITY_ID_3, 0);
        this.dataTracker.startTracking(INVUL_TIMER, 0);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Invul", this.getInvulnerableTimer());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setInvulTimer(tag.getInt("Invul"));
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        this.bossBar.setName(this.getDisplayName());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_DEATH;
    }

    @Override
    public void tickMovement() {
        int j;
        int i;
        Entity entity;
        Vec3d vec3d = this.getVelocity().multiply(1.0, 0.6, 1.0);
        if (!this.world.isClient && this.getTrackedEntityId(0) > 0 && (entity = this.world.getEntityById(this.getTrackedEntityId(0))) != null) {
            double d = vec3d.y;
            if (this.getY() < entity.getY() || !this.shouldRenderOverlay() && this.getY() < entity.getY() + 5.0) {
                d = Math.max(0.0, d);
                d += 0.3 - d * (double)0.6f;
            }
            vec3d = new Vec3d(vec3d.x, d, vec3d.z);
            Vec3d vec3d2 = new Vec3d(entity.getX() - this.getX(), 0.0, entity.getZ() - this.getZ());
            if (WitherEntity.squaredHorizontalLength(vec3d2) > 9.0) {
                Vec3d vec3d3 = vec3d2.normalize();
                vec3d = vec3d.add(vec3d3.x * 0.3 - vec3d.x * 0.6, 0.0, vec3d3.z * 0.3 - vec3d.z * 0.6);
            }
        }
        this.setVelocity(vec3d);
        if (WitherEntity.squaredHorizontalLength(vec3d) > 0.05) {
            this.yaw = (float)MathHelper.atan2(vec3d.z, vec3d.x) * 57.295776f - 90.0f;
        }
        super.tickMovement();
        for (i = 0; i < 2; ++i) {
            this.prevSideHeadYaws[i] = this.sideHeadYaws[i];
            this.prevSideHeadPitches[i] = this.sideHeadPitches[i];
        }
        for (i = 0; i < 2; ++i) {
            int j2 = this.getTrackedEntityId(i + 1);
            Entity entity2 = null;
            if (j2 > 0) {
                entity2 = this.world.getEntityById(j2);
            }
            if (entity2 != null) {
                double e = this.getHeadX(i + 1);
                double f = this.getHeadY(i + 1);
                double g = this.getHeadZ(i + 1);
                double h = entity2.getX() - e;
                double k = entity2.getEyeY() - f;
                double l = entity2.getZ() - g;
                double m = MathHelper.sqrt(h * h + l * l);
                float n = (float)(MathHelper.atan2(l, h) * 57.2957763671875) - 90.0f;
                float o = (float)(-(MathHelper.atan2(k, m) * 57.2957763671875));
                this.sideHeadPitches[i] = this.getNextAngle(this.sideHeadPitches[i], o, 40.0f);
                this.sideHeadYaws[i] = this.getNextAngle(this.sideHeadYaws[i], n, 10.0f);
                continue;
            }
            this.sideHeadYaws[i] = this.getNextAngle(this.sideHeadYaws[i], this.bodyYaw, 10.0f);
        }
        boolean bl = this.shouldRenderOverlay();
        for (j = 0; j < 3; ++j) {
            double p = this.getHeadX(j);
            double q = this.getHeadY(j);
            double r = this.getHeadZ(j);
            this.world.addParticle(ParticleTypes.SMOKE, p + this.random.nextGaussian() * (double)0.3f, q + this.random.nextGaussian() * (double)0.3f, r + this.random.nextGaussian() * (double)0.3f, 0.0, 0.0, 0.0);
            if (!bl || this.world.random.nextInt(4) != 0) continue;
            this.world.addParticle(ParticleTypes.ENTITY_EFFECT, p + this.random.nextGaussian() * (double)0.3f, q + this.random.nextGaussian() * (double)0.3f, r + this.random.nextGaussian() * (double)0.3f, 0.7f, 0.7f, 0.5);
        }
        if (this.getInvulnerableTimer() > 0) {
            for (j = 0; j < 3; ++j) {
                this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + this.random.nextGaussian(), this.getY() + (double)(this.random.nextFloat() * 3.3f), this.getZ() + this.random.nextGaussian(), 0.7f, 0.7f, 0.9f);
            }
        }
    }

    @Override
    protected void mobTick() {
        int j;
        int i;
        if (this.getInvulnerableTimer() > 0) {
            int i2 = this.getInvulnerableTimer() - 1;
            if (i2 <= 0) {
                Explosion.DestructionType destructionType = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
                this.world.createExplosion(this, this.getX(), this.getEyeY(), this.getZ(), 7.0f, false, destructionType);
                this.world.playGlobalEvent(1023, new BlockPos(this), 0);
            }
            this.setInvulTimer(i2);
            if (this.age % 10 == 0) {
                this.heal(10.0f);
            }
            return;
        }
        super.mobTick();
        block0: for (i = 1; i < 3; ++i) {
            if (this.age < this.field_7091[i - 1]) continue;
            this.field_7091[i - 1] = this.age + 10 + this.random.nextInt(10);
            if (this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD) {
                int n = i - 1;
                int n2 = this.field_7092[n];
                this.field_7092[n] = n2 + 1;
                if (n2 > 15) {
                    float f = 10.0f;
                    float g = 5.0f;
                    double d = MathHelper.nextDouble(this.random, this.getX() - 10.0, this.getX() + 10.0);
                    double e = MathHelper.nextDouble(this.random, this.getY() - 5.0, this.getY() + 5.0);
                    double h = MathHelper.nextDouble(this.random, this.getZ() - 10.0, this.getZ() + 10.0);
                    this.method_6877(i + 1, d, e, h, true);
                    this.field_7092[i - 1] = 0;
                }
            }
            if ((j = this.getTrackedEntityId(i)) > 0) {
                Entity entity = this.world.getEntityById(j);
                if (entity == null || !entity.isAlive() || this.squaredDistanceTo(entity) > 900.0 || !this.canSee(entity)) {
                    this.setTrackedEntityId(i, 0);
                    continue;
                }
                if (entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.invulnerable) {
                    this.setTrackedEntityId(i, 0);
                    continue;
                }
                this.method_6878(i + 1, (LivingEntity)entity);
                this.field_7091[i - 1] = this.age + 40 + this.random.nextInt(20);
                this.field_7092[i - 1] = 0;
                continue;
            }
            List<LivingEntity> list = this.world.getTargets(LivingEntity.class, HEAD_TARGET_PREDICATE, this, this.getBoundingBox().expand(20.0, 8.0, 20.0));
            for (int k = 0; k < 10 && !list.isEmpty(); ++k) {
                LivingEntity livingEntity = list.get(this.random.nextInt(list.size()));
                if (livingEntity != this && livingEntity.isAlive() && this.canSee(livingEntity)) {
                    if (livingEntity instanceof PlayerEntity) {
                        if (((PlayerEntity)livingEntity).abilities.invulnerable) continue block0;
                        this.setTrackedEntityId(i, livingEntity.getEntityId());
                        continue block0;
                    }
                    this.setTrackedEntityId(i, livingEntity.getEntityId());
                    continue block0;
                }
                list.remove(livingEntity);
            }
        }
        if (this.getTarget() != null) {
            this.setTrackedEntityId(0, this.getTarget().getEntityId());
        } else {
            this.setTrackedEntityId(0, 0);
        }
        if (this.field_7082 > 0) {
            --this.field_7082;
            if (this.field_7082 == 0 && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                i = MathHelper.floor(this.getY());
                j = MathHelper.floor(this.getX());
                int l = MathHelper.floor(this.getZ());
                boolean bl = false;
                for (int m = -1; m <= 1; ++m) {
                    for (int n = -1; n <= 1; ++n) {
                        for (int o = 0; o <= 3; ++o) {
                            int p = j + m;
                            int q = i + o;
                            int r = l + n;
                            BlockPos blockPos = new BlockPos(p, q, r);
                            BlockState blockState = this.world.getBlockState(blockPos);
                            if (!WitherEntity.canDestroy(blockState)) continue;
                            bl = this.world.breakBlock(blockPos, true, this) || bl;
                        }
                    }
                }
                if (bl) {
                    this.world.playLevelEvent(null, 1022, new BlockPos(this), 0);
                }
            }
        }
        if (this.age % 20 == 0) {
            this.heal(1.0f);
        }
        this.bossBar.setPercent(this.getHealth() / this.getMaximumHealth());
    }

    public static boolean canDestroy(BlockState block) {
        return !block.isAir() && !BlockTags.WITHER_IMMUNE.contains(block.getBlock());
    }

    public void method_6885() {
        this.setInvulTimer(220);
        this.setHealth(this.getMaximumHealth() / 3.0f);
    }

    @Override
    public void slowMovement(BlockState state, Vec3d multiplier) {
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    private double getHeadX(int headIndex) {
        if (headIndex <= 0) {
            return this.getX();
        }
        float f = (this.bodyYaw + (float)(180 * (headIndex - 1))) * ((float)Math.PI / 180);
        float g = MathHelper.cos(f);
        return this.getX() + (double)g * 1.3;
    }

    private double getHeadY(int headIndex) {
        if (headIndex <= 0) {
            return this.getY() + 3.0;
        }
        return this.getY() + 2.2;
    }

    private double getHeadZ(int headIndex) {
        if (headIndex <= 0) {
            return this.getZ();
        }
        float f = (this.bodyYaw + (float)(180 * (headIndex - 1))) * ((float)Math.PI / 180);
        float g = MathHelper.sin(f);
        return this.getZ() + (double)g * 1.3;
    }

    private float getNextAngle(float prevAngle, float desiredAngle, float maxDifference) {
        float f = MathHelper.wrapDegrees(desiredAngle - prevAngle);
        if (f > maxDifference) {
            f = maxDifference;
        }
        if (f < -maxDifference) {
            f = -maxDifference;
        }
        return prevAngle + f;
    }

    private void method_6878(int i, LivingEntity livingEntity) {
        this.method_6877(i, livingEntity.getX(), livingEntity.getY() + (double)livingEntity.getStandingEyeHeight() * 0.5, livingEntity.getZ(), i == 0 && this.random.nextFloat() < 0.001f);
    }

    private void method_6877(int headIndex, double d, double e, double f, boolean bl) {
        this.world.playLevelEvent(null, 1024, new BlockPos(this), 0);
        double g = this.getHeadX(headIndex);
        double h = this.getHeadY(headIndex);
        double i = this.getHeadZ(headIndex);
        double j = d - g;
        double k = e - h;
        double l = f - i;
        WitherSkullEntity witherSkullEntity = new WitherSkullEntity(this.world, this, j, k, l);
        if (bl) {
            witherSkullEntity.setCharged(true);
        }
        witherSkullEntity.setPos(g, h, i);
        this.world.spawnEntity(witherSkullEntity);
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        this.method_6878(0, target);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        Entity entity;
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source == DamageSource.DROWN || source.getAttacker() instanceof WitherEntity) {
            return false;
        }
        if (this.getInvulnerableTimer() > 0 && source != DamageSource.OUT_OF_WORLD) {
            return false;
        }
        if (this.shouldRenderOverlay() && (entity = source.getSource()) instanceof ProjectileEntity) {
            return false;
        }
        entity = source.getAttacker();
        if (entity != null && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() == this.getGroup()) {
            return false;
        }
        if (this.field_7082 <= 0) {
            this.field_7082 = 20;
        }
        int i = 0;
        while (i < this.field_7092.length) {
            int n = i++;
            this.field_7092[n] = this.field_7092[n] + 3;
        }
        return super.damage(source, amount);
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        ItemEntity itemEntity = this.dropItem(Items.NETHER_STAR);
        if (itemEntity != null) {
            itemEntity.setCovetedItem();
        }
    }

    @Override
    public void checkDespawn() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
            this.remove();
            return;
        }
        this.despawnCounter = 0;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
        return false;
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance effect) {
        return false;
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(300.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.6f);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(40.0);
        this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(4.0);
    }

    @Environment(value=EnvType.CLIENT)
    public float getHeadYaw(int headIndex) {
        return this.sideHeadYaws[headIndex];
    }

    @Environment(value=EnvType.CLIENT)
    public float getHeadPitch(int headIndex) {
        return this.sideHeadPitches[headIndex];
    }

    public int getInvulnerableTimer() {
        return this.dataTracker.get(INVUL_TIMER);
    }

    public void setInvulTimer(int ticks) {
        this.dataTracker.set(INVUL_TIMER, ticks);
    }

    public int getTrackedEntityId(int headIndex) {
        return this.dataTracker.get(TRACKED_ENTITY_IDS.get(headIndex));
    }

    public void setTrackedEntityId(int headIndex, int id) {
        this.dataTracker.set(TRACKED_ENTITY_IDS.get(headIndex), id);
    }

    @Override
    public boolean shouldRenderOverlay() {
        return this.getHealth() <= this.getMaximumHealth() / 2.0f;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        if (effect.getEffectType() == StatusEffects.WITHER) {
            return false;
        }
        return super.canHaveStatusEffect(effect);
    }

    class DescendAtHalfHealthGoal
    extends Goal {
        public DescendAtHalfHealthGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return WitherEntity.this.getInvulnerableTimer() > 0;
        }
    }
}

