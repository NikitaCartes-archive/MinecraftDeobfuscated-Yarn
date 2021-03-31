/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class AreaEffectCloudEntity
extends Entity {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int field_29972 = 5;
    private static final TrackedData<Float> RADIUS = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> WAITING = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ParticleEffect> PARTICLE_ID = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.PARTICLE);
    private static final float field_29971 = 32.0f;
    private Potion potion = Potions.EMPTY;
    private final List<StatusEffectInstance> effects = Lists.newArrayList();
    private final Map<Entity, Integer> affectedEntities = Maps.newHashMap();
    private int duration = 600;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private boolean customColor;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusGrowth;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUuid;

    public AreaEffectCloudEntity(EntityType<? extends AreaEffectCloudEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = true;
        this.setRadius(3.0f);
    }

    public AreaEffectCloudEntity(World world, double x, double y, double z) {
        this((EntityType<? extends AreaEffectCloudEntity>)EntityType.AREA_EFFECT_CLOUD, world);
        this.setPosition(x, y, z);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(COLOR, 0);
        this.getDataTracker().startTracking(RADIUS, Float.valueOf(0.5f));
        this.getDataTracker().startTracking(WAITING, false);
        this.getDataTracker().startTracking(PARTICLE_ID, ParticleTypes.ENTITY_EFFECT);
    }

    public void setRadius(float radius) {
        if (!this.world.isClient) {
            this.getDataTracker().set(RADIUS, Float.valueOf(MathHelper.clamp(radius, 0.0f, 32.0f)));
        }
    }

    @Override
    public void calculateDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.calculateDimensions();
        this.setPosition(d, e, f);
    }

    public float getRadius() {
        return this.getDataTracker().get(RADIUS).floatValue();
    }

    public void setPotion(Potion potion) {
        this.potion = potion;
        if (!this.customColor) {
            this.updateColor();
        }
    }

    private void updateColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getDataTracker().set(COLOR, 0);
        } else {
            this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
        }
    }

    public void addEffect(StatusEffectInstance effect) {
        this.effects.add(effect);
        if (!this.customColor) {
            this.updateColor();
        }
    }

    public int getColor() {
        return this.getDataTracker().get(COLOR);
    }

    public void setColor(int rgb) {
        this.customColor = true;
        this.getDataTracker().set(COLOR, rgb);
    }

    public ParticleEffect getParticleType() {
        return this.getDataTracker().get(PARTICLE_ID);
    }

    public void setParticleType(ParticleEffect particle) {
        this.getDataTracker().set(PARTICLE_ID, particle);
    }

    protected void setWaiting(boolean waiting) {
        this.getDataTracker().set(WAITING, waiting);
    }

    public boolean isWaiting() {
        return this.getDataTracker().get(WAITING);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void tick() {
        block22: {
            boolean bl2;
            float f;
            boolean bl;
            block20: {
                ParticleEffect particleEffect;
                block21: {
                    super.tick();
                    bl = this.isWaiting();
                    f = this.getRadius();
                    if (!this.world.isClient) break block20;
                    particleEffect = this.getParticleType();
                    if (!bl) break block21;
                    if (!this.random.nextBoolean()) break block22;
                    for (int i = 0; i < 2; ++i) {
                        float g = this.random.nextFloat() * ((float)Math.PI * 2);
                        float h = MathHelper.sqrt(this.random.nextFloat()) * 0.2f;
                        float j = MathHelper.cos(g) * h;
                        float k = MathHelper.sin(g) * h;
                        if (particleEffect.getType() == ParticleTypes.ENTITY_EFFECT) {
                            int l = this.random.nextBoolean() ? 0xFFFFFF : this.getColor();
                            int m = l >> 16 & 0xFF;
                            int n = l >> 8 & 0xFF;
                            int o = l & 0xFF;
                            this.world.addImportantParticle(particleEffect, this.getX() + (double)j, this.getY(), this.getZ() + (double)k, (float)m / 255.0f, (float)n / 255.0f, (float)o / 255.0f);
                            continue;
                        }
                        this.world.addImportantParticle(particleEffect, this.getX() + (double)j, this.getY(), this.getZ() + (double)k, 0.0, 0.0, 0.0);
                    }
                    break block22;
                }
                float p = (float)Math.PI * f * f;
                int q = 0;
                while ((float)q < p) {
                    float h = this.random.nextFloat() * ((float)Math.PI * 2);
                    float j = MathHelper.sqrt(this.random.nextFloat()) * f;
                    float k = MathHelper.cos(h) * j;
                    float r = MathHelper.sin(h) * j;
                    if (particleEffect.getType() == ParticleTypes.ENTITY_EFFECT) {
                        int m = this.getColor();
                        int n = m >> 16 & 0xFF;
                        int o = m >> 8 & 0xFF;
                        int s = m & 0xFF;
                        this.world.addImportantParticle(particleEffect, this.getX() + (double)k, this.getY(), this.getZ() + (double)r, (float)n / 255.0f, (float)o / 255.0f, (float)s / 255.0f);
                    } else {
                        this.world.addImportantParticle(particleEffect, this.getX() + (double)k, this.getY(), this.getZ() + (double)r, (0.5 - this.random.nextDouble()) * 0.15, 0.01f, (0.5 - this.random.nextDouble()) * 0.15);
                    }
                    ++q;
                }
                break block22;
            }
            if (this.age >= this.waitTime + this.duration) {
                this.discard();
                return;
            }
            boolean bl3 = bl2 = this.age < this.waitTime;
            if (bl != bl2) {
                this.setWaiting(bl2);
            }
            if (bl2) {
                return;
            }
            if (this.radiusGrowth != 0.0f) {
                if ((f += this.radiusGrowth) < 0.5f) {
                    this.discard();
                    return;
                }
                this.setRadius(f);
            }
            if (this.age % 5 == 0) {
                this.affectedEntities.entrySet().removeIf(entry -> this.age >= (Integer)entry.getValue());
                ArrayList<StatusEffectInstance> list = Lists.newArrayList();
                for (StatusEffectInstance statusEffectInstance : this.potion.getEffects()) {
                    list.add(new StatusEffectInstance(statusEffectInstance.getEffectType(), statusEffectInstance.getDuration() / 4, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()));
                }
                list.addAll(this.effects);
                if (list.isEmpty()) {
                    this.affectedEntities.clear();
                } else {
                    List<LivingEntity> list2 = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
                    if (!list2.isEmpty()) {
                        for (LivingEntity livingEntity : list2) {
                            double e;
                            double d;
                            double t;
                            if (this.affectedEntities.containsKey(livingEntity) || !livingEntity.isAffectedBySplashPotions() || !((t = (d = livingEntity.getX() - this.getX()) * d + (e = livingEntity.getZ() - this.getZ()) * e) <= (double)(f * f))) continue;
                            this.affectedEntities.put(livingEntity, this.age + this.reapplicationDelay);
                            for (StatusEffectInstance statusEffectInstance2 : list) {
                                if (statusEffectInstance2.getEffectType().isInstant()) {
                                    statusEffectInstance2.getEffectType().applyInstantEffect(this, this.getOwner(), livingEntity, statusEffectInstance2.getAmplifier(), 0.5);
                                    continue;
                                }
                                livingEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance2));
                            }
                            if (this.radiusOnUse != 0.0f) {
                                if ((f += this.radiusOnUse) < 0.5f) {
                                    this.discard();
                                    return;
                                }
                                this.setRadius(f);
                            }
                            if (this.durationOnUse == 0) continue;
                            this.duration += this.durationOnUse;
                            if (this.duration > 0) continue;
                            this.discard();
                            return;
                        }
                    }
                }
            }
        }
    }

    public float method_35044() {
        return this.radiusOnUse;
    }

    public void setRadiusOnUse(float radius) {
        this.radiusOnUse = radius;
    }

    public float method_35045() {
        return this.radiusGrowth;
    }

    public void setRadiusGrowth(float growth) {
        this.radiusGrowth = growth;
    }

    public int method_35046() {
        return this.durationOnUse;
    }

    public void method_35043(int i) {
        this.durationOnUse = i;
    }

    public int method_35047() {
        return this.waitTime;
    }

    public void setWaitTime(int ticks) {
        this.waitTime = ticks;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner == null ? null : owner.getUuid();
    }

    @Nullable
    public LivingEntity getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld && (entity = ((ServerWorld)this.world).getEntity(this.ownerUuid)) instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
        }
        return this.owner;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.age = nbt.getInt("Age");
        this.duration = nbt.getInt("Duration");
        this.waitTime = nbt.getInt("WaitTime");
        this.reapplicationDelay = nbt.getInt("ReapplicationDelay");
        this.durationOnUse = nbt.getInt("DurationOnUse");
        this.radiusOnUse = nbt.getFloat("RadiusOnUse");
        this.radiusGrowth = nbt.getFloat("RadiusPerTick");
        this.setRadius(nbt.getFloat("Radius"));
        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
        }
        if (nbt.contains("Particle", 8)) {
            try {
                this.setParticleType(ParticleEffectArgumentType.readParameters(new StringReader(nbt.getString("Particle"))));
            } catch (CommandSyntaxException commandSyntaxException) {
                LOGGER.warn("Couldn't load custom particle {}", (Object)nbt.getString("Particle"), (Object)commandSyntaxException);
            }
        }
        if (nbt.contains("Color", 99)) {
            this.setColor(nbt.getInt("Color"));
        }
        if (nbt.contains("Potion", 8)) {
            this.setPotion(PotionUtil.getPotion(nbt));
        }
        if (nbt.contains("Effects", 9)) {
            NbtList nbtList = nbt.getList("Effects", 10);
            this.effects.clear();
            for (int i = 0; i < nbtList.size(); ++i) {
                StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtList.getCompound(i));
                if (statusEffectInstance == null) continue;
                this.addEffect(statusEffectInstance);
            }
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Age", this.age);
        nbt.putInt("Duration", this.duration);
        nbt.putInt("WaitTime", this.waitTime);
        nbt.putInt("ReapplicationDelay", this.reapplicationDelay);
        nbt.putInt("DurationOnUse", this.durationOnUse);
        nbt.putFloat("RadiusOnUse", this.radiusOnUse);
        nbt.putFloat("RadiusPerTick", this.radiusGrowth);
        nbt.putFloat("Radius", this.getRadius());
        nbt.putString("Particle", this.getParticleType().asString());
        if (this.ownerUuid != null) {
            nbt.putUuid("Owner", this.ownerUuid);
        }
        if (this.customColor) {
            nbt.putInt("Color", this.getColor());
        }
        if (this.potion != Potions.EMPTY) {
            nbt.putString("Potion", Registry.POTION.getId(this.potion).toString());
        }
        if (!this.effects.isEmpty()) {
            NbtList nbtList = new NbtList();
            for (StatusEffectInstance statusEffectInstance : this.effects) {
                nbtList.add(statusEffectInstance.writeNbt(new NbtCompound()));
            }
            nbt.put("Effects", nbtList);
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (RADIUS.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    public Potion method_35048() {
        return this.potion;
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(this.getRadius() * 2.0f, 0.5f);
    }
}

