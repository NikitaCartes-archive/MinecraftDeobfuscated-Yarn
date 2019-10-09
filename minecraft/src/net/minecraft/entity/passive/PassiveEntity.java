package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class PassiveEntity extends MobEntityWithAi {
	private static final TrackedData<Boolean> CHILD = DataTracker.registerData(PassiveEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected int breedingAge;
	protected int forcedAge;
	protected int happyTicksRemaining;

	protected PassiveEntity(EntityType<? extends PassiveEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		if (entityData == null) {
			entityData = new PassiveEntity$1();
		}

		PassiveEntity$1 lv = (PassiveEntity$1)entityData;
		if (lv.method_22436() && lv.method_22432() > 0 && this.random.nextFloat() <= lv.method_22437()) {
			this.setBreedingAge(-24000);
		}

		lv.method_22435();
		return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Nullable
	public abstract PassiveEntity createChild(PassiveEntity passiveEntity);

	protected void onPlayerSpawnedChild(PlayerEntity playerEntity, PassiveEntity passiveEntity) {
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (item instanceof SpawnEggItem && ((SpawnEggItem)item).isOfSameEntityType(itemStack.getTag(), this.getType())) {
			if (!this.world.isClient) {
				PassiveEntity passiveEntity = this.createChild(this);
				if (passiveEntity != null) {
					passiveEntity.setBreedingAge(-24000);
					passiveEntity.setPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
					this.world.spawnEntity(passiveEntity);
					if (itemStack.hasCustomName()) {
						passiveEntity.setCustomName(itemStack.getName());
					}

					this.onPlayerSpawnedChild(playerEntity, passiveEntity);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
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

	public void growUp(int i, boolean bl) {
		int j = this.getBreedingAge();
		j += i * 20;
		if (j > 0) {
			j = 0;
		}

		int l = j - j;
		this.setBreedingAge(j);
		if (bl) {
			this.forcedAge += l;
			if (this.happyTicksRemaining == 0) {
				this.happyTicksRemaining = 40;
			}
		}

		if (this.getBreedingAge() == 0) {
			this.setBreedingAge(this.forcedAge);
		}
	}

	public void growUp(int i) {
		this.growUp(i, false);
	}

	public void setBreedingAge(int i) {
		int j = this.breedingAge;
		this.breedingAge = i;
		if (j < 0 && i >= 0 || j >= 0 && i < 0) {
			this.dataTracker.set(CHILD, i < 0);
			this.onGrowUp();
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Age", this.getBreedingAge());
		compoundTag.putInt("ForcedAge", this.forcedAge);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setBreedingAge(compoundTag.getInt("Age"));
		this.forcedAge = compoundTag.getInt("ForcedAge");
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (CHILD.equals(trackedData)) {
			this.calculateDimensions();
		}

		super.onTrackedDataSet(trackedData);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.world.isClient) {
			if (this.happyTicksRemaining > 0) {
				if (this.happyTicksRemaining % 4 == 0) {
					this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.method_23322(1.0), this.method_23319() + 0.5, this.method_23325(1.0), 0.0, 0.0, 0.0);
				}

				this.happyTicksRemaining--;
			}
		} else if (this.isAlive()) {
			int i = this.getBreedingAge();
			if (i < 0) {
				this.setBreedingAge(++i);
			} else if (i > 0) {
				this.setBreedingAge(--i);
			}
		}
	}

	protected void onGrowUp() {
	}

	@Override
	public boolean isBaby() {
		return this.getBreedingAge() < 0;
	}
}
