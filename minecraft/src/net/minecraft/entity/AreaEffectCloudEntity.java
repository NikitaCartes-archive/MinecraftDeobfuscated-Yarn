package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.command.arguments.ParticleArgumentType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
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

public class AreaEffectCloudEntity extends Entity {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final TrackedData<Float> RADIUS = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> COLOR = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> WAITING = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<ParticleEffect> PARTICLE_ID = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.PARTICLE);
	private Potion potion = Potions.EMPTY;
	private final List<StatusEffectInstance> effects = Lists.<StatusEffectInstance>newArrayList();
	private final Map<Entity, Integer> affectedEntities = Maps.<Entity, Integer>newHashMap();
	private int duration = 600;
	private int waitTime = 20;
	private int reapplicationDelay = 20;
	private boolean customColor;
	private int durationOnUse;
	private float radiusOnUse;
	private float radiusGrowth;
	private LivingEntity owner;
	private UUID ownerUuid;

	public AreaEffectCloudEntity(EntityType<? extends AreaEffectCloudEntity> entityType, World world) {
		super(entityType, world);
		this.noClip = true;
		this.setRadius(3.0F);
	}

	public AreaEffectCloudEntity(World world, double x, double y, double z) {
		this(EntityType.AREA_EFFECT_CLOUD, world);
		this.updatePosition(x, y, z);
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(COLOR, 0);
		this.getDataTracker().startTracking(RADIUS, 0.5F);
		this.getDataTracker().startTracking(WAITING, false);
		this.getDataTracker().startTracking(PARTICLE_ID, ParticleTypes.ENTITY_EFFECT);
	}

	public void setRadius(float radius) {
		if (!this.world.isClient) {
			this.getDataTracker().set(RADIUS, radius);
		}
	}

	@Override
	public void calculateDimensions() {
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		super.calculateDimensions();
		this.updatePosition(d, e, f);
	}

	public float getRadius() {
		return this.getDataTracker().get(RADIUS);
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
		super.tick();
		boolean bl = this.isWaiting();
		float f = this.getRadius();
		if (this.world.isClient) {
			ParticleEffect particleEffect = this.getParticleType();
			if (bl) {
				if (this.random.nextBoolean()) {
					for (int i = 0; i < 2; i++) {
						float g = this.random.nextFloat() * (float) (Math.PI * 2);
						float h = MathHelper.sqrt(this.random.nextFloat()) * 0.2F;
						float j = MathHelper.cos(g) * h;
						float k = MathHelper.sin(g) * h;
						if (particleEffect.getType() == ParticleTypes.ENTITY_EFFECT) {
							int l = this.random.nextBoolean() ? 16777215 : this.getColor();
							int m = l >> 16 & 0xFF;
							int n = l >> 8 & 0xFF;
							int o = l & 0xFF;
							this.world
								.addImportantParticle(
									particleEffect,
									this.getX() + (double)j,
									this.getY(),
									this.getZ() + (double)k,
									(double)((float)m / 255.0F),
									(double)((float)n / 255.0F),
									(double)((float)o / 255.0F)
								);
						} else {
							this.world.addImportantParticle(particleEffect, this.getX() + (double)j, this.getY(), this.getZ() + (double)k, 0.0, 0.0, 0.0);
						}
					}
				}
			} else {
				float p = (float) Math.PI * f * f;

				for (int q = 0; (float)q < p; q++) {
					float h = this.random.nextFloat() * (float) (Math.PI * 2);
					float j = MathHelper.sqrt(this.random.nextFloat()) * f;
					float k = MathHelper.cos(h) * j;
					float r = MathHelper.sin(h) * j;
					if (particleEffect.getType() == ParticleTypes.ENTITY_EFFECT) {
						int m = this.getColor();
						int n = m >> 16 & 0xFF;
						int o = m >> 8 & 0xFF;
						int s = m & 0xFF;
						this.world
							.addImportantParticle(
								particleEffect,
								this.getX() + (double)k,
								this.getY(),
								this.getZ() + (double)r,
								(double)((float)n / 255.0F),
								(double)((float)o / 255.0F),
								(double)((float)s / 255.0F)
							);
					} else {
						this.world
							.addImportantParticle(
								particleEffect,
								this.getX() + (double)k,
								this.getY(),
								this.getZ() + (double)r,
								(0.5 - this.random.nextDouble()) * 0.15,
								0.01F,
								(0.5 - this.random.nextDouble()) * 0.15
							);
					}
				}
			}
		} else {
			if (this.age >= this.waitTime + this.duration) {
				this.remove();
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
					this.remove();
					return;
				}

				this.setRadius(f);
			}

			if (this.age % 5 == 0) {
				Iterator<Entry<Entity, Integer>> iterator = this.affectedEntities.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<Entity, Integer> entry = (Entry<Entity, Integer>)iterator.next();
					if (this.age >= (Integer)entry.getValue()) {
						iterator.remove();
					}
				}

				List<StatusEffectInstance> list = Lists.<StatusEffectInstance>newArrayList();

				for (StatusEffectInstance statusEffectInstance : this.potion.getEffects()) {
					list.add(
						new StatusEffectInstance(
							statusEffectInstance.getEffectType(),
							statusEffectInstance.getDuration() / 4,
							statusEffectInstance.getAmplifier(),
							statusEffectInstance.isAmbient(),
							statusEffectInstance.shouldShowParticles()
						)
					);
				}

				list.addAll(this.effects);
				if (list.isEmpty()) {
					this.affectedEntities.clear();
				} else {
					List<LivingEntity> list2 = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
					if (!list2.isEmpty()) {
						for (LivingEntity livingEntity : list2) {
							if (!this.affectedEntities.containsKey(livingEntity) && livingEntity.isAffectedBySplashPotions()) {
								double d = livingEntity.getX() - this.getX();
								double e = livingEntity.getZ() - this.getZ();
								double t = d * d + e * e;
								if (t <= (double)(f * f)) {
									this.affectedEntities.put(livingEntity, this.age + this.reapplicationDelay);

									for (StatusEffectInstance statusEffectInstance2 : list) {
										if (statusEffectInstance2.getEffectType().isInstant()) {
											statusEffectInstance2.getEffectType().applyInstantEffect(this, this.getOwner(), livingEntity, statusEffectInstance2.getAmplifier(), 0.5);
										} else {
											livingEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance2));
										}
									}

									if (this.radiusOnUse != 0.0F) {
										f += this.radiusOnUse;
										if (f < 0.5F) {
											this.remove();
											return;
										}

										this.setRadius(f);
									}

									if (this.durationOnUse != 0) {
										this.duration = this.duration + this.durationOnUse;
										if (this.duration <= 0) {
											this.remove();
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

	public void setRadiusOnUse(float radius) {
		this.radiusOnUse = radius;
	}

	public void setRadiusGrowth(float growth) {
		this.radiusGrowth = growth;
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
		if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld) {
			Entity entity = ((ServerWorld)this.world).getEntity(this.ownerUuid);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity)entity;
			}
		}

		return this.owner;
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		this.age = tag.getInt("Age");
		this.duration = tag.getInt("Duration");
		this.waitTime = tag.getInt("WaitTime");
		this.reapplicationDelay = tag.getInt("ReapplicationDelay");
		this.durationOnUse = tag.getInt("DurationOnUse");
		this.radiusOnUse = tag.getFloat("RadiusOnUse");
		this.radiusGrowth = tag.getFloat("RadiusPerTick");
		this.setRadius(tag.getFloat("Radius"));
		this.ownerUuid = tag.getUuid("OwnerUUID");
		if (tag.contains("Particle", 8)) {
			try {
				this.setParticleType(ParticleArgumentType.readParameters(new StringReader(tag.getString("Particle"))));
			} catch (CommandSyntaxException var5) {
				LOGGER.warn("Couldn't load custom particle {}", tag.getString("Particle"), var5);
			}
		}

		if (tag.contains("Color", 99)) {
			this.setColor(tag.getInt("Color"));
		}

		if (tag.contains("Potion", 8)) {
			this.setPotion(PotionUtil.getPotion(tag));
		}

		if (tag.contains("Effects", 9)) {
			ListTag listTag = tag.getList("Effects", 10);
			this.effects.clear();

			for (int i = 0; i < listTag.size(); i++) {
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.deserialize(listTag.getCompound(i));
				if (statusEffectInstance != null) {
					this.addEffect(statusEffectInstance);
				}
			}
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		tag.putInt("Age", this.age);
		tag.putInt("Duration", this.duration);
		tag.putInt("WaitTime", this.waitTime);
		tag.putInt("ReapplicationDelay", this.reapplicationDelay);
		tag.putInt("DurationOnUse", this.durationOnUse);
		tag.putFloat("RadiusOnUse", this.radiusOnUse);
		tag.putFloat("RadiusPerTick", this.radiusGrowth);
		tag.putFloat("Radius", this.getRadius());
		tag.putString("Particle", this.getParticleType().asString());
		if (this.ownerUuid != null) {
			tag.putUuid("OwnerUUID", this.ownerUuid);
		}

		if (this.customColor) {
			tag.putInt("Color", this.getColor());
		}

		if (this.potion != Potions.EMPTY && this.potion != null) {
			tag.putString("Potion", Registry.POTION.getId(this.potion).toString());
		}

		if (!this.effects.isEmpty()) {
			ListTag listTag = new ListTag();

			for (StatusEffectInstance statusEffectInstance : this.effects) {
				listTag.add(statusEffectInstance.serialize(new CompoundTag()));
			}

			tag.put("Effects", listTag);
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
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return EntityDimensions.changing(this.getRadius() * 2.0F, 0.5F);
	}
}
