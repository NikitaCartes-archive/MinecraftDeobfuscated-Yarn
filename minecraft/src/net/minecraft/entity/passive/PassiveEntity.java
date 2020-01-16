package net.minecraft.entity.passive;

import javax.annotation.Nullable;
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

	protected PassiveEntity(EntityType<? extends PassiveEntity> type, World world) {
		super(type, world);
	}

	@Override
	public net.minecraft.entity.EntityData initialize(
		IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable net.minecraft.entity.EntityData entityData, @Nullable CompoundTag entityTag
	) {
		if (entityData == null) {
			entityData = new PassiveEntity.EntityData();
		}

		PassiveEntity.EntityData entityData2 = (PassiveEntity.EntityData)entityData;
		if (entityData2.canSpawnBaby() && entityData2.getSpawnedCount() > 0 && this.random.nextFloat() <= entityData2.getBabyChance()) {
			this.setBreedingAge(-24000);
		}

		entityData2.countSpawned();
		return super.initialize(world, difficulty, spawnType, entityData, entityTag);
	}

	@Nullable
	public abstract PassiveEntity createChild(PassiveEntity mate);

	protected void onPlayerSpawnedChild(PlayerEntity player, PassiveEntity child) {
	}

	@Override
	public boolean interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (item instanceof SpawnEggItem && ((SpawnEggItem)item).isOfSameEntityType(itemStack.getTag(), this.getType())) {
			if (!this.world.isClient) {
				PassiveEntity passiveEntity = this.createChild(this);
				if (passiveEntity != null) {
					passiveEntity.setBreedingAge(-24000);
					passiveEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
					this.world.spawnEntity(passiveEntity);
					if (itemStack.hasCustomName()) {
						passiveEntity.setCustomName(itemStack.getName());
					}

					this.onPlayerSpawnedChild(player, passiveEntity);
					if (!player.abilities.creativeMode) {
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

	public void growUp(int age, boolean overGrow) {
		int i = this.getBreedingAge();
		i += age * 20;
		if (i > 0) {
			i = 0;
		}

		int k = i - i;
		this.setBreedingAge(i);
		if (overGrow) {
			this.forcedAge += k;
			if (this.happyTicksRemaining == 0) {
				this.happyTicksRemaining = 40;
			}
		}

		if (this.getBreedingAge() == 0) {
			this.setBreedingAge(this.forcedAge);
		}
	}

	public void growUp(int age) {
		this.growUp(age, false);
	}

	public void setBreedingAge(int age) {
		int i = this.breedingAge;
		this.breedingAge = age;
		if (i < 0 && age >= 0 || i >= 0 && age < 0) {
			this.dataTracker.set(CHILD, age < 0);
			this.onGrowUp();
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("Age", this.getBreedingAge());
		tag.putInt("ForcedAge", this.forcedAge);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setBreedingAge(tag.getInt("Age"));
		this.forcedAge = tag.getInt("ForcedAge");
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (CHILD.equals(data)) {
			this.calculateDimensions();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.world.isClient) {
			if (this.happyTicksRemaining > 0) {
				if (this.happyTicksRemaining % 4 == 0) {
					this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
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

	public static class EntityData implements net.minecraft.entity.EntityData {
		private int spawnCount;
		private boolean babyAllowed = true;
		private float babyChance = 0.05F;

		public int getSpawnedCount() {
			return this.spawnCount;
		}

		public void countSpawned() {
			this.spawnCount++;
		}

		public boolean canSpawnBaby() {
			return this.babyAllowed;
		}

		public void setBabyAllowed(boolean babyAllowed) {
			this.babyAllowed = babyAllowed;
		}

		public float getBabyChance() {
			return this.babyChance;
		}

		public void setBabyChance(float babyChance) {
			this.babyChance = babyChance;
		}
	}
}
