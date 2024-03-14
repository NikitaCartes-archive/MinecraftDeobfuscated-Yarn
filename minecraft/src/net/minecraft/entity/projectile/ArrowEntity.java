package net.minecraft.entity.projectile;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

public class ArrowEntity extends PersistentProjectileEntity {
	private static final int MAX_POTION_DURATION_TICKS = 600;
	private static final int NO_POTION_COLOR = -1;
	private static final TrackedData<Integer> COLOR = DataTracker.registerData(ArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final byte PARTICLE_EFFECT_STATUS = 0;

	public ArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);
	}

	public ArrowEntity(World world, double x, double y, double z, ItemStack stack) {
		super(EntityType.ARROW, x, y, z, world, stack);
		this.initColor();
	}

	public ArrowEntity(World world, LivingEntity owner, ItemStack stack) {
		super(EntityType.ARROW, owner, world, stack);
		this.initColor();
	}

	private PotionContentsComponent getPotionContents() {
		return this.getItemStack().getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
	}

	private void setPotionContents(PotionContentsComponent potionContentsComponent) {
		this.getItemStack().set(DataComponentTypes.POTION_CONTENTS, potionContentsComponent);
		this.initColor();
	}

	@Override
	protected void setStack(ItemStack stack) {
		super.setStack(stack);
		this.initColor();
	}

	private void initColor() {
		PotionContentsComponent potionContentsComponent = this.getPotionContents();
		this.dataTracker.set(COLOR, potionContentsComponent.equals(PotionContentsComponent.DEFAULT) ? -1 : potionContentsComponent.getCustomColor());
	}

	public void addEffect(StatusEffectInstance effect) {
		this.setPotionContents(this.getPotionContents().with(effect));
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(COLOR, -1);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getWorld().isClient) {
			if (this.inGround) {
				if (this.inGroundTime % 5 == 0) {
					this.spawnParticles(1);
				}
			} else {
				this.spawnParticles(2);
			}
		} else if (this.inGround && this.inGroundTime != 0 && !this.getPotionContents().equals(PotionContentsComponent.DEFAULT) && this.inGroundTime >= 600) {
			this.getWorld().sendEntityStatus(this, (byte)0);
			this.setStack(new ItemStack(Items.ARROW));
		}
	}

	private void spawnParticles(int amount) {
		int i = this.getColor();
		if (i != -1 && amount > 0) {
			for (int j = 0; j < amount; j++) {
				this.getWorld()
					.addParticle(
						EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, i), this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, 0.0, 0.0
					);
			}
		}
	}

	public int getColor() {
		return this.dataTracker.get(COLOR);
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);
		Entity entity = this.getEffectCause();
		PotionContentsComponent potionContentsComponent = this.getPotionContents();
		if (potionContentsComponent.potion().isPresent()) {
			for (StatusEffectInstance statusEffectInstance : ((Potion)((RegistryEntry)potionContentsComponent.potion().get()).value()).getEffects()) {
				target.addStatusEffect(
					new StatusEffectInstance(
						statusEffectInstance.getEffectType(),
						Math.max(statusEffectInstance.mapDuration(i -> i / 8), 1),
						statusEffectInstance.getAmplifier(),
						statusEffectInstance.isAmbient(),
						statusEffectInstance.shouldShowParticles()
					),
					entity
				);
			}
		}

		for (StatusEffectInstance statusEffectInstance : potionContentsComponent.customEffects()) {
			target.addStatusEffect(statusEffectInstance, entity);
		}
	}

	@Override
	protected ItemStack getDefaultItemStack() {
		return new ItemStack(Items.ARROW);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 0) {
			int i = this.getColor();
			if (i != -1) {
				float f = (float)(i >> 16 & 0xFF) / 255.0F;
				float g = (float)(i >> 8 & 0xFF) / 255.0F;
				float h = (float)(i >> 0 & 0xFF) / 255.0F;

				for (int j = 0; j < 20; j++) {
					this.getWorld()
						.addParticle(
							EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, f, g, h),
							this.getParticleX(0.5),
							this.getRandomBodyY(),
							this.getParticleZ(0.5),
							0.0,
							0.0,
							0.0
						);
				}
			}
		} else {
			super.handleStatus(status);
		}
	}
}
