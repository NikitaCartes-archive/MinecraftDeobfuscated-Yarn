/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.loot.LootTables;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MagmaCubeEntity
extends SlimeEntity {
    public MagmaCubeEntity(EntityType<? extends MagmaCubeEntity> entityType, World world) {
        super((EntityType<? extends SlimeEntity>)entityType, world);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2f);
    }

    public static boolean canMagmaCubeSpawn(EntityType<MagmaCubeEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        return iWorld.getDifficulty() != Difficulty.PEACEFUL;
    }

    @Override
    public boolean canSpawn(WorldView worldView) {
        return worldView.intersectsEntities(this) && !worldView.containsFluid(this.getBoundingBox());
    }

    @Override
    protected void setSize(int i, boolean bl) {
        super.setSize(i, bl);
        this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(i * 3);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getLightmapCoordinates() {
        return 15;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    protected ParticleEffect getParticles() {
        return ParticleTypes.FLAME;
    }

    @Override
    protected Identifier getLootTableId() {
        return this.isSmall() ? LootTables.EMPTY : this.getType().getLootTableId();
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected int getTicksUntilNextJump() {
        return super.getTicksUntilNextJump() * 4;
    }

    @Override
    protected void updateStretch() {
        this.targetStretch *= 0.9f;
    }

    @Override
    protected void jump() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x, this.getJumpVelocity() + (float)this.getSize() * 0.1f, vec3d.z);
        this.velocityDirty = true;
    }

    @Override
    protected void swimUpward(Tag<Fluid> tag) {
        if (tag == FluidTags.LAVA) {
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.x, 0.22f + (float)this.getSize() * 0.05f, vec3d.z);
            this.velocityDirty = true;
        } else {
            super.swimUpward(tag);
        }
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        return false;
    }

    @Override
    protected boolean isBig() {
        return this.canMoveVoluntarily();
    }

    @Override
    protected float getDamageAmount() {
        return super.getDamageAmount() + 2.0f;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (this.isSmall()) {
            return SoundEvents.ENTITY_MAGMA_CUBE_HURT_SMALL;
        }
        return SoundEvents.ENTITY_MAGMA_CUBE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (this.isSmall()) {
            return SoundEvents.ENTITY_MAGMA_CUBE_DEATH_SMALL;
        }
        return SoundEvents.ENTITY_MAGMA_CUBE_DEATH;
    }

    @Override
    protected SoundEvent getSquishSound() {
        if (this.isSmall()) {
            return SoundEvents.ENTITY_MAGMA_CUBE_SQUISH_SMALL;
        }
        return SoundEvents.ENTITY_MAGMA_CUBE_SQUISH;
    }

    @Override
    protected SoundEvent getJumpSound() {
        return SoundEvents.ENTITY_MAGMA_CUBE_JUMP;
    }
}

