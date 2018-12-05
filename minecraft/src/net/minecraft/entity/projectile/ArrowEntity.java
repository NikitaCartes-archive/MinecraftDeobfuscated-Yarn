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
import net.minecraft.nbt.Tag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ArrowEntity extends ProjectileEntity {
	private static final TrackedData<Integer> COLOR = DataTracker.registerData(ArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private Potion potion = Potions.field_8984;
	private final Set<StatusEffectInstance> effects = Sets.<StatusEffectInstance>newHashSet();
	private boolean field_7596;

	public ArrowEntity(World world) {
		super(EntityType.ARROW, world);
	}

	public ArrowEntity(World world, double d, double e, double f) {
		super(EntityType.ARROW, d, e, f, world);
	}

	public ArrowEntity(World world, LivingEntity livingEntity) {
		super(EntityType.ARROW, livingEntity, world);
	}

	public void initFromStack(ItemStack itemStack) {
		if (itemStack.getItem() == Items.field_8087) {
			this.potion = PotionUtil.getPotion(itemStack);
			Collection<StatusEffectInstance> collection = PotionUtil.getCustomPotionEffects(itemStack);
			if (!collection.isEmpty()) {
				for (StatusEffectInstance statusEffectInstance : collection) {
					this.effects.add(new StatusEffectInstance(statusEffectInstance));
				}
			}

			int i = method_7464(itemStack);
			if (i == -1) {
				this.initColor();
			} else {
				this.method_7465(i);
			}
		} else if (itemStack.getItem() == Items.field_8107) {
			this.potion = Potions.field_8984;
			this.effects.clear();
			this.dataTracker.set(COLOR, -1);
		}
	}

	public static int method_7464(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null && compoundTag.containsKey("CustomPotionColor", 99) ? compoundTag.getInt("CustomPotionColor") : -1;
	}

	private void initColor() {
		this.field_7596 = false;
		this.dataTracker.set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
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
	public void update() {
		super.update();
		if (this.world.isRemote) {
			if (this.inGround) {
				if (this.field_7576 % 5 == 0) {
					this.spawnParticles(1);
				}
			} else {
				this.spawnParticles(2);
			}
		} else if (this.inGround && this.field_7576 != 0 && !this.effects.isEmpty() && this.field_7576 >= 600) {
			this.world.method_8421(this, (byte)0);
			this.potion = Potions.field_8984;
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
				this.world
					.method_8406(
						ParticleTypes.field_11226,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
						this.y + this.random.nextDouble() * (double)this.height,
						this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
						d,
						e,
						f
					);
			}
		}
	}

	public int getColor() {
		return this.dataTracker.get(COLOR);
	}

	private void method_7465(int i) {
		this.field_7596 = true;
		this.dataTracker.set(COLOR, i);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		if (this.potion != Potions.field_8984 && this.potion != null) {
			compoundTag.putString("Potion", Registry.POTION.getId(this.potion).toString());
		}

		if (this.field_7596) {
			compoundTag.putInt("Color", this.getColor());
		}

		if (!this.effects.isEmpty()) {
			ListTag listTag = new ListTag();

			for (StatusEffectInstance statusEffectInstance : this.effects) {
				listTag.add((Tag)statusEffectInstance.serialize(new CompoundTag()));
			}

			compoundTag.put("CustomPotionEffects", listTag);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("Potion", 8)) {
			this.potion = PotionUtil.getPotion(compoundTag);
		}

		for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(compoundTag)) {
			this.addEffect(statusEffectInstance);
		}

		if (compoundTag.containsKey("Color", 99)) {
			this.method_7465(compoundTag.getInt("Color"));
		} else {
			this.initColor();
		}
	}

	@Override
	protected void onHit(LivingEntity livingEntity) {
		super.onHit(livingEntity);

		for (StatusEffectInstance statusEffectInstance : this.potion.getEffects()) {
			livingEntity.addPotionEffect(
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
				livingEntity.addPotionEffect(statusEffectInstance);
			}
		}
	}

	@Override
	protected ItemStack asItemStack() {
		if (this.effects.isEmpty() && this.potion == Potions.field_8984) {
			return new ItemStack(Items.field_8107);
		} else {
			ItemStack itemStack = new ItemStack(Items.field_8087);
			PotionUtil.setPotion(itemStack, this.potion);
			PotionUtil.setCustomPotionEffects(itemStack, this.effects);
			if (this.field_7596) {
				itemStack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
			}

			return itemStack;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 0) {
			int i = this.getColor();
			if (i != -1) {
				double d = (double)(i >> 16 & 0xFF) / 255.0;
				double e = (double)(i >> 8 & 0xFF) / 255.0;
				double f = (double)(i >> 0 & 0xFF) / 255.0;

				for (int j = 0; j < 20; j++) {
					this.world
						.method_8406(
							ParticleTypes.field_11226,
							this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
							this.y + this.random.nextDouble() * (double)this.height,
							this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
							d,
							e,
							f
						);
				}
			}
		} else {
			super.method_5711(b);
		}
	}
}
