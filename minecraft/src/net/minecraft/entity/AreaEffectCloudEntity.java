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
import net.minecraft.particle.ParticleParameters;
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
	private static final TrackedData<Float> field_5938 = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> field_5936 = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_5944 = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<ParticleParameters> field_5931 = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.PARTICLE);
	private Potion field_5933 = Potions.field_8984;
	private final List<StatusEffectInstance> effects = Lists.<StatusEffectInstance>newArrayList();
	private final Map<Entity, Integer> affectedEntities = Maps.<Entity, Integer>newHashMap();
	private int duration = 600;
	private int waitTime = 20;
	private int reapplicationDelay = 20;
	private boolean customColor;
	private int durationOnUse;
	private float radiusOnUse;
	private float radiusGrowth;
	private LivingEntity field_5943;
	private UUID ownerUuid;

	public AreaEffectCloudEntity(EntityType<? extends AreaEffectCloudEntity> entityType, World world) {
		super(entityType, world);
		this.noClip = true;
		this.fireImmune = true;
		this.setRadius(3.0F);
	}

	public AreaEffectCloudEntity(World world, double d, double e, double f) {
		this(EntityType.AREA_EFFECT_CLOUD, world);
		this.setPosition(d, e, f);
	}

	@Override
	protected void initDataTracker() {
		this.method_5841().startTracking(field_5936, 0);
		this.method_5841().startTracking(field_5938, 0.5F);
		this.method_5841().startTracking(field_5944, false);
		this.method_5841().startTracking(field_5931, ParticleTypes.field_11226);
	}

	public void setRadius(float f) {
		if (!this.field_6002.isClient) {
			this.method_5841().set(field_5938, f);
		}
	}

	@Override
	public void refreshSize() {
		double d = this.x;
		double e = this.y;
		double f = this.z;
		super.refreshSize();
		this.setPosition(d, e, f);
	}

	public float getRadius() {
		return this.method_5841().get(field_5938);
	}

	public void method_5612(Potion potion) {
		this.field_5933 = potion;
		if (!this.customColor) {
			this.updateColor();
		}
	}

	private void updateColor() {
		if (this.field_5933 == Potions.field_8984 && this.effects.isEmpty()) {
			this.method_5841().set(field_5936, 0);
		} else {
			this.method_5841().set(field_5936, PotionUtil.getColor(PotionUtil.getPotionEffects(this.field_5933, this.effects)));
		}
	}

	public void setPotionEffect(StatusEffectInstance statusEffectInstance) {
		this.effects.add(statusEffectInstance);
		if (!this.customColor) {
			this.updateColor();
		}
	}

	public int getColor() {
		return this.method_5841().get(field_5936);
	}

	public void setColor(int i) {
		this.customColor = true;
		this.method_5841().set(field_5936, i);
	}

	public ParticleParameters method_5600() {
		return this.method_5841().get(field_5931);
	}

	public void method_5608(ParticleParameters particleParameters) {
		this.method_5841().set(field_5931, particleParameters);
	}

	protected void setWaiting(boolean bl) {
		this.method_5841().set(field_5944, bl);
	}

	public boolean method_5611() {
		return this.method_5841().get(field_5944);
	}

	public int getDuration() {
		return this.duration;
	}

	public void setDuration(int i) {
		this.duration = i;
	}

	@Override
	public void update() {
		super.update();
		boolean bl = this.method_5611();
		float f = this.getRadius();
		if (this.field_6002.isClient) {
			ParticleParameters particleParameters = this.method_5600();
			if (bl) {
				if (this.random.nextBoolean()) {
					for (int i = 0; i < 2; i++) {
						float g = this.random.nextFloat() * (float) (Math.PI * 2);
						float h = MathHelper.sqrt(this.random.nextFloat()) * 0.2F;
						float j = MathHelper.cos(g) * h;
						float k = MathHelper.sin(g) * h;
						if (particleParameters.method_10295() == ParticleTypes.field_11226) {
							int l = this.random.nextBoolean() ? 16777215 : this.getColor();
							int m = l >> 16 & 0xFF;
							int n = l >> 8 & 0xFF;
							int o = l & 0xFF;
							this.field_6002
								.method_8494(
									particleParameters,
									this.x + (double)j,
									this.y,
									this.z + (double)k,
									(double)((float)m / 255.0F),
									(double)((float)n / 255.0F),
									(double)((float)o / 255.0F)
								);
						} else {
							this.field_6002.method_8494(particleParameters, this.x + (double)j, this.y, this.z + (double)k, 0.0, 0.0, 0.0);
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
					if (particleParameters.method_10295() == ParticleTypes.field_11226) {
						int m = this.getColor();
						int n = m >> 16 & 0xFF;
						int o = m >> 8 & 0xFF;
						int s = m & 0xFF;
						this.field_6002
							.method_8494(
								particleParameters,
								this.x + (double)k,
								this.y,
								this.z + (double)r,
								(double)((float)n / 255.0F),
								(double)((float)o / 255.0F),
								(double)((float)s / 255.0F)
							);
					} else {
						this.field_6002
							.method_8494(
								particleParameters,
								this.x + (double)k,
								this.y,
								this.z + (double)r,
								(0.5 - this.random.nextDouble()) * 0.15,
								0.01F,
								(0.5 - this.random.nextDouble()) * 0.15
							);
					}
				}
			}
		} else {
			if (this.age >= this.waitTime + this.duration) {
				this.invalidate();
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
					this.invalidate();
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

				for (StatusEffectInstance statusEffectInstance : this.field_5933.getEffects()) {
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
					List<LivingEntity> list2 = this.field_6002.method_18467(LivingEntity.class, this.method_5829());
					if (!list2.isEmpty()) {
						for (LivingEntity livingEntity : list2) {
							if (!this.affectedEntities.containsKey(livingEntity) && livingEntity.method_6086()) {
								double d = livingEntity.x - this.x;
								double e = livingEntity.z - this.z;
								double t = d * d + e * e;
								if (t <= (double)(f * f)) {
									this.affectedEntities.put(livingEntity, this.age + this.reapplicationDelay);

									for (StatusEffectInstance statusEffectInstance2 : list) {
										if (statusEffectInstance2.getEffectType().isInstant()) {
											statusEffectInstance2.getEffectType().method_5564(this, this.method_5601(), livingEntity, statusEffectInstance2.getAmplifier(), 0.5);
										} else {
											livingEntity.addPotionEffect(new StatusEffectInstance(statusEffectInstance2));
										}
									}

									if (this.radiusOnUse != 0.0F) {
										f += this.radiusOnUse;
										if (f < 0.5F) {
											this.invalidate();
											return;
										}

										this.setRadius(f);
									}

									if (this.durationOnUse != 0) {
										this.duration = this.duration + this.durationOnUse;
										if (this.duration <= 0) {
											this.invalidate();
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

	public void setRadiusStart(float f) {
		this.radiusOnUse = f;
	}

	public void setRadiusGrowth(float f) {
		this.radiusGrowth = f;
	}

	public void setWaitTime(int i) {
		this.waitTime = i;
	}

	public void method_5607(@Nullable LivingEntity livingEntity) {
		this.field_5943 = livingEntity;
		this.ownerUuid = livingEntity == null ? null : livingEntity.getUuid();
	}

	@Nullable
	public LivingEntity method_5601() {
		if (this.field_5943 == null && this.ownerUuid != null && this.field_6002 instanceof ServerWorld) {
			Entity entity = ((ServerWorld)this.field_6002).getEntity(this.ownerUuid);
			if (entity instanceof LivingEntity) {
				this.field_5943 = (LivingEntity)entity;
			}
		}

		return this.field_5943;
	}

	@Override
	protected void method_5749(CompoundTag compoundTag) {
		this.age = compoundTag.getInt("Age");
		this.duration = compoundTag.getInt("Duration");
		this.waitTime = compoundTag.getInt("WaitTime");
		this.reapplicationDelay = compoundTag.getInt("ReapplicationDelay");
		this.durationOnUse = compoundTag.getInt("DurationOnUse");
		this.radiusOnUse = compoundTag.getFloat("RadiusOnUse");
		this.radiusGrowth = compoundTag.getFloat("RadiusPerTick");
		this.setRadius(compoundTag.getFloat("Radius"));
		this.ownerUuid = compoundTag.getUuid("OwnerUUID");
		if (compoundTag.containsKey("Particle", 8)) {
			try {
				this.method_5608(ParticleArgumentType.method_9418(new StringReader(compoundTag.getString("Particle"))));
			} catch (CommandSyntaxException var5) {
				LOGGER.warn("Couldn't load custom particle {}", compoundTag.getString("Particle"), var5);
			}
		}

		if (compoundTag.containsKey("Color", 99)) {
			this.setColor(compoundTag.getInt("Color"));
		}

		if (compoundTag.containsKey("Potion", 8)) {
			this.method_5612(PotionUtil.method_8057(compoundTag));
		}

		if (compoundTag.containsKey("Effects", 9)) {
			ListTag listTag = compoundTag.method_10554("Effects", 10);
			this.effects.clear();

			for (int i = 0; i < listTag.size(); i++) {
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.method_5583(listTag.getCompoundTag(i));
				if (statusEffectInstance != null) {
					this.setPotionEffect(statusEffectInstance);
				}
			}
		}
	}

	@Override
	protected void method_5652(CompoundTag compoundTag) {
		compoundTag.putInt("Age", this.age);
		compoundTag.putInt("Duration", this.duration);
		compoundTag.putInt("WaitTime", this.waitTime);
		compoundTag.putInt("ReapplicationDelay", this.reapplicationDelay);
		compoundTag.putInt("DurationOnUse", this.durationOnUse);
		compoundTag.putFloat("RadiusOnUse", this.radiusOnUse);
		compoundTag.putFloat("RadiusPerTick", this.radiusGrowth);
		compoundTag.putFloat("Radius", this.getRadius());
		compoundTag.putString("Particle", this.method_5600().asString());
		if (this.ownerUuid != null) {
			compoundTag.putUuid("OwnerUUID", this.ownerUuid);
		}

		if (this.customColor) {
			compoundTag.putInt("Color", this.getColor());
		}

		if (this.field_5933 != Potions.field_8984 && this.field_5933 != null) {
			compoundTag.putString("Potion", Registry.POTION.method_10221(this.field_5933).toString());
		}

		if (!this.effects.isEmpty()) {
			ListTag listTag = new ListTag();

			for (StatusEffectInstance statusEffectInstance : this.effects) {
				listTag.add(statusEffectInstance.method_5582(new CompoundTag()));
			}

			compoundTag.method_10566("Effects", listTag);
		}
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		if (field_5938.equals(trackedData)) {
			this.refreshSize();
		}

		super.method_5674(trackedData);
	}

	@Override
	public PistonBehavior method_5657() {
		return PistonBehavior.field_15975;
	}

	@Override
	public Packet<?> method_18002() {
		return new EntitySpawnS2CPacket(this);
	}

	@Override
	public EntitySize method_18377(EntityPose entityPose) {
		return EntitySize.resizeable(this.getRadius() * 2.0F, 0.5F);
	}
}
