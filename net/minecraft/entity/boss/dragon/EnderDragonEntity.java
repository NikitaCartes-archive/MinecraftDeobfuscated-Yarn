/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class EnderDragonEntity
extends MobEntity
implements Monster {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final TrackedData<Integer> PHASE_TYPE = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
    public final double[][] field_7026 = new double[64][3];
    public int field_7010 = -1;
    private final EnderDragonPart[] parts;
    public final EnderDragonPart partHead;
    private final EnderDragonPart partNeck;
    private final EnderDragonPart partBody;
    private final EnderDragonPart partTail1;
    private final EnderDragonPart partTail2;
    private final EnderDragonPart partTail3;
    private final EnderDragonPart partWingRight;
    private final EnderDragonPart partWingLeft;
    public float field_7019;
    public float field_7030;
    public boolean field_7027;
    public int field_7031;
    public float field_20865;
    @Nullable
    public EnderCrystalEntity connectedCrystal;
    @Nullable
    private final EnderDragonFight fight;
    private final PhaseManager phaseManager;
    private int field_7018 = 100;
    private int field_7029;
    private final PathNode[] field_7012 = new PathNode[24];
    private final int[] field_7025 = new int[24];
    private final PathMinHeap field_7008 = new PathMinHeap();

    public EnderDragonEntity(EntityType<? extends EnderDragonEntity> entityType, World world) {
        super((EntityType<? extends MobEntity>)EntityType.ENDER_DRAGON, world);
        this.partHead = new EnderDragonPart(this, "head", 1.0f, 1.0f);
        this.partNeck = new EnderDragonPart(this, "neck", 3.0f, 3.0f);
        this.partBody = new EnderDragonPart(this, "body", 5.0f, 3.0f);
        this.partTail1 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partTail2 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partTail3 = new EnderDragonPart(this, "tail", 2.0f, 2.0f);
        this.partWingRight = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.partWingLeft = new EnderDragonPart(this, "wing", 4.0f, 2.0f);
        this.parts = new EnderDragonPart[]{this.partHead, this.partNeck, this.partBody, this.partTail1, this.partTail2, this.partTail3, this.partWingRight, this.partWingLeft};
        this.setHealth(this.getMaximumHealth());
        this.noClip = true;
        this.ignoreCameraFrustum = true;
        this.fight = !world.isClient && world.dimension instanceof TheEndDimension ? ((TheEndDimension)world.dimension).method_12513() : null;
        this.phaseManager = new PhaseManager(this);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(200.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(PHASE_TYPE, PhaseType.HOVER.getTypeId());
    }

    public double[] method_6817(int i, float f) {
        if (this.getHealth() <= 0.0f) {
            f = 0.0f;
        }
        f = 1.0f - f;
        int j = this.field_7010 - i & 0x3F;
        int k = this.field_7010 - i - 1 & 0x3F;
        double[] ds = new double[3];
        double d = this.field_7026[j][0];
        double e = MathHelper.wrapDegrees(this.field_7026[k][0] - d);
        ds[0] = d + e * (double)f;
        d = this.field_7026[j][1];
        e = this.field_7026[k][1] - d;
        ds[1] = d + e * (double)f;
        ds[2] = MathHelper.lerp((double)f, this.field_7026[j][2], this.field_7026[k][2]);
        return ds;
    }

    @Override
    public void tickMovement() {
        int ad;
        float q;
        float p;
        double k;
        double j;
        double e;
        float g;
        float f;
        if (this.world.isClient) {
            this.setHealth(this.getHealth());
            if (!this.isSilent()) {
                f = MathHelper.cos(this.field_7030 * ((float)Math.PI * 2));
                g = MathHelper.cos(this.field_7019 * ((float)Math.PI * 2));
                if (g <= -0.3f && f >= -0.3f) {
                    this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, this.getSoundCategory(), 5.0f, 0.8f + this.random.nextFloat() * 0.3f, false);
                }
                if (!this.phaseManager.getCurrent().method_6848() && --this.field_7018 < 0) {
                    this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.getSoundCategory(), 2.5f, 0.8f + this.random.nextFloat() * 0.3f, false);
                    this.field_7018 = 200 + this.random.nextInt(200);
                }
            }
        }
        this.field_7019 = this.field_7030;
        if (this.getHealth() <= 0.0f) {
            f = (this.random.nextFloat() - 0.5f) * 8.0f;
            g = (this.random.nextFloat() - 0.5f) * 4.0f;
            float h = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.world.addParticle(ParticleTypes.EXPLOSION, this.getX() + (double)f, this.getY() + 2.0 + (double)g, this.getZ() + (double)h, 0.0, 0.0, 0.0);
            return;
        }
        this.method_6830();
        Vec3d vec3d = this.getVelocity();
        g = 0.2f / (MathHelper.sqrt(EnderDragonEntity.squaredHorizontalLength(vec3d)) * 10.0f + 1.0f);
        this.field_7030 = this.phaseManager.getCurrent().method_6848() ? (this.field_7030 += 0.1f) : (this.field_7027 ? (this.field_7030 += g * 0.5f) : (this.field_7030 += (g *= (float)Math.pow(2.0, vec3d.y))));
        this.yaw = MathHelper.wrapDegrees(this.yaw);
        if (this.isAiDisabled()) {
            this.field_7030 = 0.5f;
            return;
        }
        if (this.field_7010 < 0) {
            for (int i = 0; i < this.field_7026.length; ++i) {
                this.field_7026[i][0] = this.yaw;
                this.field_7026[i][1] = this.getY();
            }
        }
        if (++this.field_7010 == this.field_7026.length) {
            this.field_7010 = 0;
        }
        this.field_7026[this.field_7010][0] = this.yaw;
        this.field_7026[this.field_7010][1] = this.getY();
        if (this.world.isClient) {
            if (this.bodyTrackingIncrements > 0) {
                double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
                e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
                j = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
                k = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
                this.yaw = (float)((double)this.yaw + k / (double)this.bodyTrackingIncrements);
                this.pitch = (float)((double)this.pitch + (this.serverPitch - (double)this.pitch) / (double)this.bodyTrackingIncrements);
                --this.bodyTrackingIncrements;
                this.setPosition(d, e, j);
                this.setRotation(this.yaw, this.pitch);
            }
            this.phaseManager.getCurrent().clientTick();
        } else {
            Vec3d vec3d2;
            Phase phase = this.phaseManager.getCurrent();
            phase.serverTick();
            if (this.phaseManager.getCurrent() != phase) {
                phase = this.phaseManager.getCurrent();
                phase.serverTick();
            }
            if ((vec3d2 = phase.getTarget()) != null) {
                e = vec3d2.x - this.getX();
                j = vec3d2.y - this.getY();
                k = vec3d2.z - this.getZ();
                double l = e * e + j * j + k * k;
                float m = phase.method_6846();
                double n = MathHelper.sqrt(e * e + k * k);
                if (n > 0.0) {
                    j = MathHelper.clamp(j / n, (double)(-m), (double)m);
                }
                this.setVelocity(this.getVelocity().add(0.0, j * 0.01, 0.0));
                this.yaw = MathHelper.wrapDegrees(this.yaw);
                double o = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(e, k) * 57.2957763671875 - (double)this.yaw), -50.0, 50.0);
                Vec3d vec3d3 = vec3d2.subtract(this.getX(), this.getY(), this.getZ()).normalize();
                Vec3d vec3d4 = new Vec3d(MathHelper.sin(this.yaw * ((float)Math.PI / 180)), this.getVelocity().y, -MathHelper.cos(this.yaw * ((float)Math.PI / 180))).normalize();
                p = Math.max(((float)vec3d4.dotProduct(vec3d3) + 0.5f) / 1.5f, 0.0f);
                this.field_20865 *= 0.8f;
                this.field_20865 = (float)((double)this.field_20865 + o * (double)phase.method_6847());
                this.yaw += this.field_20865 * 0.1f;
                q = (float)(2.0 / (l + 1.0));
                float r = 0.06f;
                this.updateVelocity(0.06f * (p * q + (1.0f - q)), new Vec3d(0.0, 0.0, -1.0));
                if (this.field_7027) {
                    this.move(MovementType.SELF, this.getVelocity().multiply(0.8f));
                } else {
                    this.move(MovementType.SELF, this.getVelocity());
                }
                Vec3d vec3d5 = this.getVelocity().normalize();
                double s = 0.8 + 0.15 * (vec3d5.dotProduct(vec3d4) + 1.0) / 2.0;
                this.setVelocity(this.getVelocity().multiply(s, 0.91f, s));
            }
        }
        this.bodyYaw = this.yaw;
        Vec3d[] vec3ds = new Vec3d[this.parts.length];
        for (int t = 0; t < this.parts.length; ++t) {
            vec3ds[t] = new Vec3d(this.parts[t].getX(), this.parts[t].getY(), this.parts[t].getZ());
        }
        float u = (float)(this.method_6817(5, 1.0f)[1] - this.method_6817(10, 1.0f)[1]) * 10.0f * ((float)Math.PI / 180);
        float v = MathHelper.cos(u);
        float w = MathHelper.sin(u);
        float x = this.yaw * ((float)Math.PI / 180);
        float y = MathHelper.sin(x);
        float z = MathHelper.cos(x);
        this.method_22863(this.partBody, y * 0.5f, 0.0, -z * 0.5f);
        this.method_22863(this.partWingRight, z * 4.5f, 2.0, y * 4.5f);
        this.method_22863(this.partWingLeft, z * -4.5f, 2.0, y * -4.5f);
        if (!this.world.isClient && this.hurtTime == 0) {
            this.method_6825(this.world.getEntities(this, this.partWingRight.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.method_6825(this.world.getEntities(this, this.partWingLeft.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.method_6827(this.world.getEntities(this, this.partHead.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
            this.method_6827(this.world.getEntities(this, this.partNeck.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
        }
        float aa = MathHelper.sin(this.yaw * ((float)Math.PI / 180) - this.field_20865 * 0.01f);
        float ab = MathHelper.cos(this.yaw * ((float)Math.PI / 180) - this.field_20865 * 0.01f);
        float ac = this.method_6820();
        this.method_22863(this.partHead, aa * 6.5f * v, ac + w * 6.5f, -ab * 6.5f * v);
        this.method_22863(this.partNeck, aa * 5.5f * v, ac + w * 5.5f, -ab * 5.5f * v);
        double[] ds = this.method_6817(5, 1.0f);
        for (ad = 0; ad < 3; ++ad) {
            EnderDragonPart enderDragonPart = null;
            if (ad == 0) {
                enderDragonPart = this.partTail1;
            }
            if (ad == 1) {
                enderDragonPart = this.partTail2;
            }
            if (ad == 2) {
                enderDragonPart = this.partTail3;
            }
            double[] es = this.method_6817(12 + ad * 2, 1.0f);
            float ae = this.yaw * ((float)Math.PI / 180) + this.method_6832(es[0] - ds[0]) * ((float)Math.PI / 180);
            float af = MathHelper.sin(ae);
            float ag = MathHelper.cos(ae);
            p = 1.5f;
            q = (float)(ad + 1) * 2.0f;
            this.method_22863(enderDragonPart, -(y * 1.5f + af * q) * v, es[1] - ds[1] - (double)((q + 1.5f) * w) + 1.5, (z * 1.5f + ag * q) * v);
        }
        if (!this.world.isClient) {
            this.field_7027 = this.method_6821(this.partHead.getBoundingBox()) | this.method_6821(this.partNeck.getBoundingBox()) | this.method_6821(this.partBody.getBoundingBox());
            if (this.fight != null) {
                this.fight.updateFight(this);
            }
        }
        for (ad = 0; ad < this.parts.length; ++ad) {
            this.parts[ad].prevX = vec3ds[ad].x;
            this.parts[ad].prevY = vec3ds[ad].y;
            this.parts[ad].prevZ = vec3ds[ad].z;
            this.parts[ad].prevRenderX = vec3ds[ad].x;
            this.parts[ad].prevRenderY = vec3ds[ad].y;
            this.parts[ad].prevRenderZ = vec3ds[ad].z;
        }
    }

    private void method_22863(EnderDragonPart enderDragonPart, double d, double e, double f) {
        enderDragonPart.setPosition(this.getX() + d, this.getY() + e, this.getZ() + f);
    }

    private float method_6820() {
        if (this.phaseManager.getCurrent().method_6848()) {
            return -1.0f;
        }
        double[] ds = this.method_6817(5, 1.0f);
        double[] es = this.method_6817(0, 1.0f);
        return (float)(ds[1] - es[1]);
    }

    private void method_6830() {
        if (this.connectedCrystal != null) {
            if (this.connectedCrystal.removed) {
                this.connectedCrystal = null;
            } else if (this.age % 10 == 0 && this.getHealth() < this.getMaximumHealth()) {
                this.setHealth(this.getHealth() + 1.0f);
            }
        }
        if (this.random.nextInt(10) == 0) {
            List<EnderCrystalEntity> list = this.world.getNonSpectatingEntities(EnderCrystalEntity.class, this.getBoundingBox().expand(32.0));
            EnderCrystalEntity enderCrystalEntity = null;
            double d = Double.MAX_VALUE;
            for (EnderCrystalEntity enderCrystalEntity2 : list) {
                double e = enderCrystalEntity2.squaredDistanceTo(this);
                if (!(e < d)) continue;
                d = e;
                enderCrystalEntity = enderCrystalEntity2;
            }
            this.connectedCrystal = enderCrystalEntity;
        }
    }

    private void method_6825(List<Entity> list) {
        double d = (this.partBody.getBoundingBox().minX + this.partBody.getBoundingBox().maxX) / 2.0;
        double e = (this.partBody.getBoundingBox().minZ + this.partBody.getBoundingBox().maxZ) / 2.0;
        for (Entity entity : list) {
            if (!(entity instanceof LivingEntity)) continue;
            double f = entity.getX() - d;
            double g = entity.getZ() - e;
            double h = f * f + g * g;
            entity.addVelocity(f / h * 4.0, 0.2f, g / h * 4.0);
            if (this.phaseManager.getCurrent().method_6848() || ((LivingEntity)entity).getLastAttackedTime() >= entity.age - 2) continue;
            entity.damage(DamageSource.mob(this), 5.0f);
            this.dealDamage(this, entity);
        }
    }

    private void method_6827(List<Entity> list) {
        for (Entity entity : list) {
            if (!(entity instanceof LivingEntity)) continue;
            entity.damage(DamageSource.mob(this), 10.0f);
            this.dealDamage(this, entity);
        }
    }

    private float method_6832(double d) {
        return (float)MathHelper.wrapDegrees(d);
    }

    private boolean method_6821(Box box) {
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.floor(box.minY);
        int k = MathHelper.floor(box.minZ);
        int l = MathHelper.floor(box.maxX);
        int m = MathHelper.floor(box.maxY);
        int n = MathHelper.floor(box.maxZ);
        boolean bl = false;
        boolean bl2 = false;
        for (int o = i; o <= l; ++o) {
            for (int p = j; p <= m; ++p) {
                for (int q = k; q <= n; ++q) {
                    BlockPos blockPos = new BlockPos(o, p, q);
                    BlockState blockState = this.world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    if (blockState.isAir() || blockState.getMaterial() == Material.FIRE) continue;
                    if (!this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) || BlockTags.DRAGON_IMMUNE.contains(block)) {
                        bl = true;
                        continue;
                    }
                    bl2 = this.world.removeBlock(blockPos, false) || bl2;
                }
            }
        }
        if (bl2) {
            BlockPos blockPos2 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(m - j + 1), k + this.random.nextInt(n - k + 1));
            this.world.playLevelEvent(2008, blockPos2, 0);
        }
        return bl;
    }

    public boolean damagePart(EnderDragonPart enderDragonPart, DamageSource damageSource, float f) {
        f = this.phaseManager.getCurrent().modifyDamageTaken(damageSource, f);
        if (enderDragonPart != this.partHead) {
            f = f / 4.0f + Math.min(f, 1.0f);
        }
        if (f < 0.01f) {
            return false;
        }
        if (damageSource.getAttacker() instanceof PlayerEntity || damageSource.isExplosive()) {
            float g = this.getHealth();
            this.method_6819(damageSource, f);
            if (this.getHealth() <= 0.0f && !this.phaseManager.getCurrent().method_6848()) {
                this.setHealth(1.0f);
                this.phaseManager.setPhase(PhaseType.DYING);
            }
            if (this.phaseManager.getCurrent().method_6848()) {
                this.field_7029 = (int)((float)this.field_7029 + (g - this.getHealth()));
                if ((float)this.field_7029 > 0.25f * this.getMaximumHealth()) {
                    this.field_7029 = 0;
                    this.phaseManager.setPhase(PhaseType.TAKEOFF);
                }
            }
        }
        return true;
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        if (damageSource instanceof EntityDamageSource && ((EntityDamageSource)damageSource).method_5549()) {
            this.damagePart(this.partBody, damageSource, f);
        }
        return false;
    }

    protected boolean method_6819(DamageSource damageSource, float f) {
        return super.damage(damageSource, f);
    }

    @Override
    public void kill() {
        this.remove();
        if (this.fight != null) {
            this.fight.updateFight(this);
            this.fight.dragonKilled(this);
        }
    }

    @Override
    protected void updatePostDeath() {
        if (this.fight != null) {
            this.fight.updateFight(this);
        }
        ++this.field_7031;
        if (this.field_7031 >= 180 && this.field_7031 <= 200) {
            float f = (this.random.nextFloat() - 0.5f) * 8.0f;
            float g = (this.random.nextFloat() - 0.5f) * 4.0f;
            float h = (this.random.nextFloat() - 0.5f) * 8.0f;
            this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX() + (double)f, this.getY() + 2.0 + (double)g, this.getZ() + (double)h, 0.0, 0.0, 0.0);
        }
        boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT);
        int i = 500;
        if (this.fight != null && !this.fight.hasPreviouslyKilled()) {
            i = 12000;
        }
        if (!this.world.isClient) {
            if (this.field_7031 > 150 && this.field_7031 % 5 == 0 && bl) {
                this.method_6824(MathHelper.floor((float)i * 0.08f));
            }
            if (this.field_7031 == 1) {
                this.world.playGlobalEvent(1028, new BlockPos(this), 0);
            }
        }
        this.move(MovementType.SELF, new Vec3d(0.0, 0.1f, 0.0));
        this.yaw += 20.0f;
        this.bodyYaw = this.yaw;
        if (this.field_7031 == 200 && !this.world.isClient) {
            if (bl) {
                this.method_6824(MathHelper.floor((float)i * 0.2f));
            }
            if (this.fight != null) {
                this.fight.dragonKilled(this);
            }
            this.remove();
        }
    }

    private void method_6824(int i) {
        while (i > 0) {
            int j = ExperienceOrbEntity.roundToOrbSize(i);
            i -= j;
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY(), this.getZ(), j));
        }
    }

    public int method_6818() {
        if (this.field_7012[0] == null) {
            for (int i = 0; i < 24; ++i) {
                int m;
                int l;
                int j = 5;
                int k = i;
                if (i < 12) {
                    l = MathHelper.floor(60.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)k)));
                    m = MathHelper.floor(60.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)k)));
                } else if (i < 20) {
                    l = MathHelper.floor(40.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)(k -= 12))));
                    m = MathHelper.floor(40.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)k)));
                    j += 10;
                } else {
                    l = MathHelper.floor(20.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)(k -= 20))));
                    m = MathHelper.floor(20.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)k)));
                }
                int n = Math.max(this.world.getSeaLevel() + 10, this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(l, 0, m)).getY() + j);
                this.field_7012[i] = new PathNode(l, n, m);
            }
            this.field_7025[0] = 6146;
            this.field_7025[1] = 8197;
            this.field_7025[2] = 8202;
            this.field_7025[3] = 16404;
            this.field_7025[4] = 32808;
            this.field_7025[5] = 32848;
            this.field_7025[6] = 65696;
            this.field_7025[7] = 131392;
            this.field_7025[8] = 131712;
            this.field_7025[9] = 263424;
            this.field_7025[10] = 526848;
            this.field_7025[11] = 525313;
            this.field_7025[12] = 1581057;
            this.field_7025[13] = 3166214;
            this.field_7025[14] = 2138120;
            this.field_7025[15] = 6373424;
            this.field_7025[16] = 4358208;
            this.field_7025[17] = 12910976;
            this.field_7025[18] = 9044480;
            this.field_7025[19] = 9706496;
            this.field_7025[20] = 15216640;
            this.field_7025[21] = 0xD0E000;
            this.field_7025[22] = 11763712;
            this.field_7025[23] = 0x7E0000;
        }
        return this.method_6822(this.getX(), this.getY(), this.getZ());
    }

    public int method_6822(double d, double e, double f) {
        float g = 10000.0f;
        int i = 0;
        PathNode pathNode = new PathNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
        int j = 0;
        if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
            j = 12;
        }
        for (int k = j; k < 24; ++k) {
            float h;
            if (this.field_7012[k] == null || !((h = this.field_7012[k].getSquaredDistance(pathNode)) < g)) continue;
            g = h;
            i = k;
        }
        return i;
    }

    @Nullable
    public Path method_6833(int i, int j, @Nullable PathNode pathNode) {
        PathNode pathNode2;
        for (int k = 0; k < 24; ++k) {
            pathNode2 = this.field_7012[k];
            pathNode2.visited = false;
            pathNode2.heapWeight = 0.0f;
            pathNode2.penalizedPathLength = 0.0f;
            pathNode2.distanceToNearestTarget = 0.0f;
            pathNode2.previous = null;
            pathNode2.heapIndex = -1;
        }
        PathNode pathNode3 = this.field_7012[i];
        pathNode2 = this.field_7012[j];
        pathNode3.penalizedPathLength = 0.0f;
        pathNode3.heapWeight = pathNode3.distanceToNearestTarget = pathNode3.getDistance(pathNode2);
        this.field_7008.clear();
        this.field_7008.push(pathNode3);
        PathNode pathNode4 = pathNode3;
        int l = 0;
        if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
            l = 12;
        }
        while (!this.field_7008.isEmpty()) {
            int n;
            PathNode pathNode5 = this.field_7008.pop();
            if (pathNode5.equals(pathNode2)) {
                if (pathNode != null) {
                    pathNode.previous = pathNode2;
                    pathNode2 = pathNode;
                }
                return this.method_6826(pathNode3, pathNode2);
            }
            if (pathNode5.getDistance(pathNode2) < pathNode4.getDistance(pathNode2)) {
                pathNode4 = pathNode5;
            }
            pathNode5.visited = true;
            int m = 0;
            for (n = 0; n < 24; ++n) {
                if (this.field_7012[n] != pathNode5) continue;
                m = n;
                break;
            }
            for (n = l; n < 24; ++n) {
                if ((this.field_7025[m] & 1 << n) <= 0) continue;
                PathNode pathNode6 = this.field_7012[n];
                if (pathNode6.visited) continue;
                float f = pathNode5.penalizedPathLength + pathNode5.getDistance(pathNode6);
                if (pathNode6.isInHeap() && !(f < pathNode6.penalizedPathLength)) continue;
                pathNode6.previous = pathNode5;
                pathNode6.penalizedPathLength = f;
                pathNode6.distanceToNearestTarget = pathNode6.getDistance(pathNode2);
                if (pathNode6.isInHeap()) {
                    this.field_7008.setNodeWeight(pathNode6, pathNode6.penalizedPathLength + pathNode6.distanceToNearestTarget);
                    continue;
                }
                pathNode6.heapWeight = pathNode6.penalizedPathLength + pathNode6.distanceToNearestTarget;
                this.field_7008.push(pathNode6);
            }
        }
        if (pathNode4 == pathNode3) {
            return null;
        }
        LOGGER.debug("Failed to find path from {} to {}", (Object)i, (Object)j);
        if (pathNode != null) {
            pathNode.previous = pathNode4;
            pathNode4 = pathNode;
        }
        return this.method_6826(pathNode3, pathNode4);
    }

    private Path method_6826(PathNode pathNode, PathNode pathNode2) {
        ArrayList<PathNode> list = Lists.newArrayList();
        PathNode pathNode3 = pathNode2;
        list.add(0, pathNode3);
        while (pathNode3.previous != null) {
            pathNode3 = pathNode3.previous;
            list.add(0, pathNode3);
        }
        return new Path(list, new BlockPos(pathNode2.x, pathNode2.y, pathNode2.z), true);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putInt("DragonPhase", this.phaseManager.getCurrent().getType().getTypeId());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        if (compoundTag.contains("DragonPhase")) {
            this.phaseManager.setPhase(PhaseType.getFromId(compoundTag.getInt("DragonPhase")));
        }
    }

    @Override
    protected void checkDespawn() {
    }

    public EnderDragonPart[] method_5690() {
        return this.parts;
    }

    @Override
    public boolean collides() {
        return false;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public float method_6823(int i, double[] ds, double[] es) {
        double d;
        Phase phase = this.phaseManager.getCurrent();
        PhaseType<? extends Phase> phaseType = phase.getType();
        if (phaseType == PhaseType.LANDING || phaseType == PhaseType.TAKEOFF) {
            BlockPos blockPos = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            float f = Math.max(MathHelper.sqrt(blockPos.getSquaredDistance(this.getPos(), true)) / 4.0f, 1.0f);
            d = (float)i / f;
        } else {
            d = phase.method_6848() ? (double)i : (i == 6 ? 0.0 : es[1] - ds[1]);
        }
        return (float)d;
    }

    public Vec3d method_6834(float f) {
        Vec3d vec3d;
        Phase phase = this.phaseManager.getCurrent();
        PhaseType<? extends Phase> phaseType = phase.getType();
        if (phaseType == PhaseType.LANDING || phaseType == PhaseType.TAKEOFF) {
            BlockPos blockPos = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            float g = Math.max(MathHelper.sqrt(blockPos.getSquaredDistance(this.getPos(), true)) / 4.0f, 1.0f);
            float h = 6.0f / g;
            float i = this.pitch;
            float j = 1.5f;
            this.pitch = -h * 1.5f * 5.0f;
            vec3d = this.getRotationVec(f);
            this.pitch = i;
        } else if (phase.method_6848()) {
            float k = this.pitch;
            float g = 1.5f;
            this.pitch = -45.0f;
            vec3d = this.getRotationVec(f);
            this.pitch = k;
        } else {
            vec3d = this.getRotationVec(f);
        }
        return vec3d;
    }

    public void crystalDestroyed(EnderCrystalEntity enderCrystalEntity, BlockPos blockPos, DamageSource damageSource) {
        PlayerEntity playerEntity = damageSource.getAttacker() instanceof PlayerEntity ? (PlayerEntity)damageSource.getAttacker() : this.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (enderCrystalEntity == this.connectedCrystal) {
            this.damagePart(this.partHead, DamageSource.explosion(playerEntity), 10.0f);
        }
        this.phaseManager.getCurrent().crystalDestroyed(enderCrystalEntity, blockPos, damageSource, playerEntity);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> trackedData) {
        if (PHASE_TYPE.equals(trackedData) && this.world.isClient) {
            this.phaseManager.setPhase(PhaseType.getFromId(this.getDataTracker().get(PHASE_TYPE)));
        }
        super.onTrackedDataSet(trackedData);
    }

    public PhaseManager getPhaseManager() {
        return this.phaseManager;
    }

    @Nullable
    public EnderDragonFight getFight() {
        return this.fight;
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance statusEffectInstance) {
        return false;
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }
}

