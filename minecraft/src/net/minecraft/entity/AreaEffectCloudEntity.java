package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class AreaEffectCloudEntity extends Entity implements Ownable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_29972 = 5;
	private static final TrackedData<Float> RADIUS = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Boolean> WAITING = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<ParticleEffect> PARTICLE_ID = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.PARTICLE);
	private static final float MAX_RADIUS = 32.0F;
	private static final float field_40730 = 0.5F;
	private static final float field_40731 = 3.0F;
	public static final float field_40732 = 6.0F;
	public static final float field_40733 = 0.5F;
	private PotionContentsComponent potionContentsComponent = PotionContentsComponent.DEFAULT;
	private final Map<Entity, Integer> affectedEntities = Maps.<Entity, Integer>newHashMap();
	private int duration = 600;
	private int waitTime = 20;
	private int reapplicationDelay = 20;
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
	}

	public AreaEffectCloudEntity(World world, double x, double y, double z) {
		this(EntityType.AREA_EFFECT_CLOUD, world);
		this.setPosition(x, y, z);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(RADIUS, 3.0F);
		builder.add(WAITING, false);
		builder.add(PARTICLE_ID, EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, -1));
	}

	public void setRadius(float radius) {
		if (!this.getWorld().isClient) {
			this.getDataTracker().set(RADIUS, MathHelper.clamp(radius, 0.0F, 32.0F));
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
		return this.getDataTracker().get(RADIUS);
	}

	public void setPotionContents(PotionContentsComponent potionContentsComponent) {
		this.potionContentsComponent = potionContentsComponent;
		this.updateColor();
	}

	private void updateColor() {
		ParticleEffect particleEffect = this.dataTracker.get(PARTICLE_ID);
		if (particleEffect instanceof EntityEffectParticleEffect entityEffectParticleEffect) {
			int i = this.potionContentsComponent.equals(PotionContentsComponent.DEFAULT) ? 0 : this.potionContentsComponent.getColor();
			this.dataTracker.set(PARTICLE_ID, EntityEffectParticleEffect.create(entityEffectParticleEffect.getType(), i));
		}
	}

	public void addEffect(StatusEffectInstance effect) {
		this.setPotionContents(this.potionContentsComponent.with(effect));
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
		super.tick();
		boolean bl = this.isWaiting();
		float f = this.getRadius();
		if (this.getWorld().isClient) {
			if (bl && this.random.nextBoolean()) {
				return;
			}

			ParticleEffect particleEffect = this.getParticleType();
			int i;
			float g;
			if (bl) {
				i = 2;
				g = 0.2F;
			} else {
				i = MathHelper.ceil((float) Math.PI * f * f);
				g = f;
			}

			for (int j = 0; j < i; j++) {
				float h = this.random.nextFloat() * (float) (Math.PI * 2);
				float k = MathHelper.sqrt(this.random.nextFloat()) * g;
				double d = this.getX() + (double)(MathHelper.cos(h) * k);
				double e = this.getY();
				double l = this.getZ() + (double)(MathHelper.sin(h) * k);
				if (particleEffect.getType() == ParticleTypes.ENTITY_EFFECT) {
					if (bl && this.random.nextBoolean()) {
						this.getWorld().addImportantParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, -1), d, e, l, 0.0, 0.0, 0.0);
					} else {
						this.getWorld().addImportantParticle(particleEffect, d, e, l, 0.0, 0.0, 0.0);
					}
				} else if (bl) {
					this.getWorld().addImportantParticle(particleEffect, d, e, l, 0.0, 0.0, 0.0);
				} else {
					this.getWorld().addImportantParticle(particleEffect, d, e, l, (0.5 - this.random.nextDouble()) * 0.15, 0.01F, (0.5 - this.random.nextDouble()) * 0.15);
				}
			}
		} else {
			if (this.age >= this.waitTime + this.duration) {
				this.discard();
				return;
			}

			boolean bl2 = this.age < this.waitTime;
			if (bl != bl2) {
				this.setWaiting(bl2);
			}

			if (bl2) {
				return;
			}

			if (this.radiusGrowth != 0.0F) {
				f += this.radiusGrowth;
				if (f < 0.5F) {
					this.discard();
					return;
				}

				this.setRadius(f);
			}

			if (this.age % 5 == 0) {
				this.affectedEntities.entrySet().removeIf(entry -> this.age >= (Integer)entry.getValue());
				if (!this.potionContentsComponent.hasEffects()) {
					this.affectedEntities.clear();
				} else {
					List<StatusEffectInstance> list = Lists.<StatusEffectInstance>newArrayList();
					if (this.potionContentsComponent.potion().isPresent()) {
						for (StatusEffectInstance statusEffectInstance : ((Potion)((RegistryEntry)this.potionContentsComponent.potion().get()).value()).getEffects()) {
							list.add(
								new StatusEffectInstance(
									statusEffectInstance.getEffectType(),
									statusEffectInstance.mapDuration(duration -> duration / 4),
									statusEffectInstance.getAmplifier(),
									statusEffectInstance.isAmbient(),
									statusEffectInstance.shouldShowParticles()
								)
							);
						}
					}

					list.addAll(this.potionContentsComponent.customEffects());
					List<LivingEntity> list2 = this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
					if (!list2.isEmpty()) {
						for (LivingEntity livingEntity : list2) {
							if (!this.affectedEntities.containsKey(livingEntity) && livingEntity.isAffectedBySplashPotions()) {
								double m = livingEntity.getX() - this.getX();
								double n = livingEntity.getZ() - this.getZ();
								double o = m * m + n * n;
								if (o <= (double)(f * f)) {
									this.affectedEntities.put(livingEntity, this.age + this.reapplicationDelay);

									for (StatusEffectInstance statusEffectInstance2 : list) {
										if (statusEffectInstance2.getEffectType().value().isInstant()) {
											statusEffectInstance2.getEffectType().value().applyInstantEffect(this, this.getOwner(), livingEntity, statusEffectInstance2.getAmplifier(), 0.5);
										} else {
											livingEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance2), this);
										}
									}

									if (this.radiusOnUse != 0.0F) {
										f += this.radiusOnUse;
										if (f < 0.5F) {
											this.discard();
											return;
										}

										this.setRadius(f);
									}

									if (this.durationOnUse != 0) {
										this.duration = this.duration + this.durationOnUse;
										if (this.duration <= 0) {
											this.discard();
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public float getRadiusOnUse() {
		return this.radiusOnUse;
	}

	public void setRadiusOnUse(float radiusOnUse) {
		this.radiusOnUse = radiusOnUse;
	}

	public float getRadiusGrowth() {
		return this.radiusGrowth;
	}

	public void setRadiusGrowth(float radiusGrowth) {
		this.radiusGrowth = radiusGrowth;
	}

	public int getDurationOnUse() {
		return this.durationOnUse;
	}

	public void setDurationOnUse(int durationOnUse) {
		this.durationOnUse = durationOnUse;
	}

	public int getWaitTime() {
		return this.waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public void setOwner(@Nullable LivingEntity owner) {
		this.owner = owner;
		this.ownerUuid = owner == null ? null : owner.getUuid();
	}

	@Nullable
	public LivingEntity getOwner() {
		if (this.owner == null && this.ownerUuid != null && this.getWorld() instanceof ServerWorld) {
			Entity entity = ((ServerWorld)this.getWorld()).getEntity(this.ownerUuid);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity)entity;
			}
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

		if (nbt.contains("Particle", NbtElement.STRING_TYPE)) {
			try {
				this.setParticleType(ParticleEffectArgumentType.readParameters(new StringReader(nbt.getString("Particle")), this.getRegistryManager()));
			} catch (CommandSyntaxException var3) {
				LOGGER.warn("Couldn't load custom particle {}", nbt.getString("Particle"), var3);
			}
		}

		if (nbt.contains("potion_contents")) {
			PotionContentsComponent.CODEC
				.parse(NbtOps.INSTANCE, nbt.get("potion_contents"))
				.resultOrPartial(string -> LOGGER.warn("Failed to parse area effect cloud potions: '{}'", string))
				.ifPresent(this::setPotionContents);
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
		nbt.putString("Particle", this.getParticleType().asString(this.getRegistryManager()));
		if (this.ownerUuid != null) {
			nbt.putUuid("Owner", this.ownerUuid);
		}

		if (!this.potionContentsComponent.equals(PotionContentsComponent.DEFAULT)) {
			NbtElement nbtElement = PotionContentsComponent.CODEC.encodeStart(NbtOps.INSTANCE, this.potionContentsComponent).getOrThrow();
			nbt.put("potion_contents", nbtElement);
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (RADIUS.equals(data)) {
			this.calculateDimensions();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.IGNORE;
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return EntityDimensions.changing(this.getRadius() * 2.0F, 0.5F);
	}
}
