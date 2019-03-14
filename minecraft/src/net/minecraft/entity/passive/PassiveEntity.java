package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class PassiveEntity extends MobEntityWithAi {
	private static final TrackedData<Boolean> CHILD = DataTracker.registerData(PassiveEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected int breedingAge;
	protected int field_5948;
	protected int field_5947;

	protected PassiveEntity(EntityType<? extends PassiveEntity> entityType, World world) {
		super(entityType, world);
	}

	@Nullable
	public abstract PassiveEntity createChild(PassiveEntity passiveEntity);

	protected void method_18249(PlayerEntity playerEntity, PassiveEntity passiveEntity) {
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (item instanceof SpawnEggItem && ((SpawnEggItem)item).method_8018(itemStack.getTag(), this.getType())) {
			if (!this.world.isClient) {
				PassiveEntity passiveEntity = this.createChild(this);
				if (passiveEntity != null) {
					passiveEntity.setBreedingAge(-24000);
					passiveEntity.setPositionAndAngles(this.x, this.y, this.z, 0.0F, 0.0F);
					this.world.spawnEntity(passiveEntity);
					if (itemStack.hasDisplayName()) {
						passiveEntity.setCustomName(itemStack.getDisplayName());
					}

					this.method_18249(playerEntity, passiveEntity);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CHILD, false);
	}

	public int getBreedingAge() {
		if (this.world.isClient) {
			return this.dataTracker.get(CHILD) ? -1 : 1;
		} else {
			return this.breedingAge;
		}
	}

	public void method_5620(int i, boolean bl) {
		int j = this.getBreedingAge();
		j += i * 20;
		if (j > 0) {
			j = 0;
		}

		int l = j - j;
		this.setBreedingAge(j);
		if (bl) {
			this.field_5948 += l;
			if (this.field_5947 == 0) {
				this.field_5947 = 40;
			}
		}

		if (this.getBreedingAge() == 0) {
			this.setBreedingAge(this.field_5948);
		}
	}

	public void method_5615(int i) {
		this.method_5620(i, false);
	}

	public void setBreedingAge(int i) {
		int j = this.breedingAge;
		this.breedingAge = i;
		if (j < 0 && i >= 0 || j >= 0 && i < 0) {
			this.dataTracker.set(CHILD, i < 0);
			this.method_5619();
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Age", this.getBreedingAge());
		compoundTag.putInt("ForcedAge", this.field_5948);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setBreedingAge(compoundTag.getInt("Age"));
		this.field_5948 = compoundTag.getInt("ForcedAge");
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (CHILD.equals(trackedData)) {
			this.refreshSize();
		}

		super.onTrackedDataSet(trackedData);
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (this.world.isClient) {
			if (this.field_5947 > 0) {
				if (this.field_5947 % 4 == 0) {
					this.world
						.addParticle(
							ParticleTypes.field_11211,
							this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
							this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
							this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
							0.0,
							0.0,
							0.0
						);
				}

				this.field_5947--;
			}
		} else if (this.isValid()) {
			int i = this.getBreedingAge();
			if (i < 0) {
				this.setBreedingAge(++i);
			} else if (i > 0) {
				this.setBreedingAge(--i);
			}
		}
	}

	protected void method_5619() {
	}

	@Override
	public boolean isChild() {
		return this.getBreedingAge() < 0;
	}
}
