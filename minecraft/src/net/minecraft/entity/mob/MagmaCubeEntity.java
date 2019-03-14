package net.minecraft.entity.mob;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.LootTables;

public class MagmaCubeEntity extends SlimeEntity {
	public MagmaCubeEntity(EntityType<? extends MagmaCubeEntity> entityType, World world) {
		super(entityType, world);
		this.fireImmune = true;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return iWorld.getDifficulty() != Difficulty.PEACEFUL;
	}

	@Override
	public boolean method_5957(ViewableWorld viewableWorld) {
		return viewableWorld.method_8606(this) && !viewableWorld.isInFluid(this.getBoundingBox());
	}

	@Override
	protected void method_7161(int i, boolean bl) {
		super.method_7161(i, bl);
		this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue((double)(i * 3));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	@Override
	public float method_5718() {
		return 1.0F;
	}

	@Override
	protected ParticleParameters getParticles() {
		return ParticleTypes.field_11240;
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
	protected int method_7154() {
		return super.method_7154() * 4;
	}

	@Override
	protected void method_7156() {
		this.sizeX *= 0.9F;
	}

	@Override
	protected void jump() {
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x, (double)(0.42F + (float)this.getSize() * 0.1F), vec3d.z);
		this.velocityDirty = true;
	}

	@Override
	protected void method_6010(Tag<Fluid> tag) {
		if (tag == FluidTags.field_15518) {
			Vec3d vec3d = this.getVelocity();
			this.setVelocity(vec3d.x, (double)(0.22F + (float)this.getSize() * 0.05F), vec3d.z);
			this.velocityDirty = true;
		} else {
			super.method_6010(tag);
		}
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	protected boolean method_7163() {
		return this.method_6034();
	}

	@Override
	protected int method_7158() {
		return super.method_7158() + 2;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return this.isSmall() ? SoundEvents.field_15005 : SoundEvents.field_14747;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isSmall() ? SoundEvents.field_14889 : SoundEvents.field_14662;
	}

	@Override
	protected SoundEvent getSquishSound() {
		return this.isSmall() ? SoundEvents.field_14749 : SoundEvents.field_14949;
	}

	@Override
	protected SoundEvent getJumpSound() {
		return SoundEvents.field_14847;
	}
}
