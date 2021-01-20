package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class MagmaCubeEntity extends SlimeEntity {
	public MagmaCubeEntity(EntityType<? extends MagmaCubeEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createMagmaCubeAttributes() {
		return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F);
	}

	public static boolean canMagmaCubeSpawn(EntityType<MagmaCubeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getDifficulty() != Difficulty.PEACEFUL;
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return world.intersectsEntities(this) && !world.containsFluid(this.getBoundingBox());
	}

	@Override
	protected void setSize(int size, boolean heal) {
		super.setSize(size, heal);
		this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue((double)(size * 3));
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
		this.setVelocity(vec3d.x, (double)(this.getJumpVelocity() + (float)this.getSize() * 0.1F), vec3d.z);
		this.velocityDirty = true;
	}

	@Override
	protected void swimUpward(Tag<Fluid> fluid) {
		if (fluid == FluidTags.LAVA) {
			Vec3d vec3d = this.getVelocity();
			this.setVelocity(vec3d.x, (double)(0.22F + (float)this.getSize() * 0.05F), vec3d.z);
			this.velocityDirty = true;
		} else {
			super.swimUpward(fluid);
		}
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Override
	protected boolean canAttack() {
		return this.canMoveVoluntarily();
	}

	@Override
	protected float getDamageAmount() {
		return super.getDamageAmount() + 2.0F;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
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
