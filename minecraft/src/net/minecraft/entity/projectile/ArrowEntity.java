package net.minecraft.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ArrowEntity extends ProjectileEntity {
	private static final TrackedData<Integer> COLOR = DataTracker.registerData(ArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private Potion potion = Potions.EMPTY;
	private final Set<StatusEffectInstance> effects = Sets.<StatusEffectInstance>newHashSet();
	private boolean colorSet;

	public ArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);
	}

	public ArrowEntity(World world, double d, double e, double f) {
		super(EntityType.ARROW, d, e, f, world);
	}

	public ArrowEntity(World world, LivingEntity livingEntity) {
		super(EntityType.ARROW, livingEntity, world);
	}

	public void initFromStack(ItemStack itemStack) {
		if (itemStack.getItem() == Items.TIPPED_ARROW) {
			this.potion = PotionUtil.getPotion(itemStack);
			Collection<StatusEffectInstance> collection = PotionUtil.getCustomPotionEffects(itemStack);
			if (!collection.isEmpty()) {
				for (StatusEffectInstance statusEffectInstance : collection) {
					this.effects.add(new StatusEffectInstance(statusEffectInstance));
				}
			}

			int i = getCustomPotionColor(itemStack);
			if (i == -1) {
				this.initColor();
			} else {
				this.setColor(i);
			}
		} else if (itemStack.getItem() == Items.ARROW) {
			this.potion = Potions.EMPTY;
			this.effects.clear();
			this.dataTracker.set(COLOR, -1);
		}
	}

	public static int getCustomPotionColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null && compoundTag.contains("CustomPotionColor", 99) ? compoundTag.getInt("CustomPotionColor") : -1;
	}

	private void initColor() {
		this.colorSet = false;
		if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
			this.dataTracker.set(COLOR, -1);
		} else {
			this.dataTracker.set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
		}
	}

	public void addEffect(StatusEffectInstance statusEffectInstance) {
		this.effects.add(statusEffectInstance);
		this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(COLOR, -1);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient) {
			if (this.inGround) {
				if (this.inGroundTime % 5 == 0) {
					this.spawnParticles(1);
				}
			} else {
				this.spawnParticles(2);
			}
		} else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
			this.world.sendEntityStatus(this, (byte)0);
			this.potion = Potions.EMPTY;
			this.effects.clear();
			this.dataTracker.set(COLOR, -1);
		}
	}

	private void spawnParticles(int i) {
		int j = this.getColor();
		if (j != -1 && i > 0) {
			double d = (double)(j >> 16 & 0xFF) / 255.0;
			double e = (double)(j >> 8 & 0xFF) / 255.0;
			double f = (double)(j >> 0 & 0xFF) / 255.0;

			for (int k = 0; k < i; k++) {
				this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.method_23322(0.5), this.method_23319(), this.method_23325(0.5), d, e, f);
			}
		}
	}

	public int getColor() {
		return this.dataTracker.get(COLOR);
	}

	private void setColor(int i) {
		this.colorSet = true;
		this.dataTracker.set(COLOR, i);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		if (this.potion != Potions.EMPTY && this.potion != null) {
			compoundTag.putString("Potion", Registry.POTION.getId(this.potion).toString());
		}

		if (this.colorSet) {
			compoundTag.putInt("Color", this.getColor());
		}

		if (!this.effects.isEmpty()) {
			ListTag listTag = new ListTag();

			for (StatusEffectInstance statusEffectInstance : this.effects) {
				listTag.add(statusEffectInstance.serialize(new CompoundTag()));
			}

			compoundTag.put("CustomPotionEffects", listTag);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.contains("Potion", 8)) {
			this.potion = PotionUtil.getPotion(compoundTag);
		}

		for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(compoundTag)) {
			this.addEffect(statusEffectInstance);
		}

		if (compoundTag.contains("Color", 99)) {
			this.setColor(compoundTag.getInt("Color"));
		} else {
			this.initColor();
		}
	}

	@Override
	protected void onHit(LivingEntity livingEntity) {
		super.onHit(livingEntity);

		for (StatusEffectInstance statusEffectInstance : this.potion.getEffects()) {
			livingEntity.addStatusEffect(
				new StatusEffectInstance(
					statusEffectInstance.getEffectType(),
					Math.max(statusEffectInstance.getDuration() / 8, 1),
					statusEffectInstance.getAmplifier(),
					statusEffectInstance.isAmbient(),
					statusEffectInstance.shouldShowParticles()
				)
			);
		}

		if (!this.effects.isEmpty()) {
			for (StatusEffectInstance statusEffectInstance : this.effects) {
				livingEntity.addStatusEffect(statusEffectInstance);
			}
		}
	}

	@Override
	protected ItemStack asItemStack() {
		if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
			return new ItemStack(Items.ARROW);
		} else {
			ItemStack itemStack = new ItemStack(Items.TIPPED_ARROW);
			PotionUtil.setPotion(itemStack, this.potion);
			PotionUtil.setCustomPotionEffects(itemStack, this.effects);
			if (this.colorSet) {
				itemStack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
			}

			return itemStack;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 0) {
			int i = this.getColor();
			if (i != -1) {
				double d = (double)(i >> 16 & 0xFF) / 255.0;
				double e = (double)(i >> 8 & 0xFF) / 255.0;
				double f = (double)(i >> 0 & 0xFF) / 255.0;

				for (int j = 0; j < 20; j++) {
					this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.method_23322(0.5), this.method_23319(), this.method_23325(0.5), d, e, f);
				}
			}
		} else {
			super.handleStatus(b);
		}
	}
}
