package net.minecraft.entity.mob;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4538;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.Fluid;
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
import net.minecraft.world.loot.LootTables;

public class MagmaCubeEntity extends SlimeEntity {
	public MagmaCubeEntity(EntityType<? extends MagmaCubeEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	public static boolean canMagmaCubeSpawn(EntityType<MagmaCubeEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return iWorld.getDifficulty() != Difficulty.PEACEFUL;
	}

	@Override
	public boolean canSpawn(class_4538 arg) {
		return arg.intersectsEntities(this) && !arg.containsFluid(this.getBoundingBox());
	}

	@Override
	protected void setSize(int i, boolean bl) {
		super.setSize(i, bl);
		this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue((double)(i * 3));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	@Override
	public float getBrightnessAtEyes() {
		return 1.0F;
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
		this.targetStretch *= 0.9F;
	}

	@Override
	protected void jump() {
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x, (double)(0.42F + (float)this.getSize() * 0.1F), vec3d.z);
		this.velocityDirty = true;
	}

	@Override
	protected void swimUpward(Tag<Fluid> tag) {
		if (tag == FluidTags.LAVA) {
			Vec3d vec3d = this.getVelocity();
			this.setVelocity(vec3d.x, (double)(0.22F + (float)this.getSize() * 0.05F), vec3d.z);
			this.velocityDirty = true;
		} else {
			super.swimUpward(tag);
		}
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	protected boolean isBig() {
		return this.canMoveVoluntarily();
	}

	@Override
	protected float getDamageAmount() {
		return super.getDamageAmount() + 2.0F;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return this.isSmall() ? SoundEvents.ENTITY_MAGMA_CUBE_HURT_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isSmall() ? SoundEvents.ENTITY_MAGMA_CUBE_DEATH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_DEATH;
	}

	@Override
	protected SoundEvent getSquishSound() {
		return this.isSmall() ? SoundEvents.ENTITY_MAGMA_CUBE_SQUISH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_SQUISH;
	}

	@Override
	protected SoundEvent getJumpSound() {
		return SoundEvents.ENTITY_MAGMA_CUBE_JUMP;
	}
}
