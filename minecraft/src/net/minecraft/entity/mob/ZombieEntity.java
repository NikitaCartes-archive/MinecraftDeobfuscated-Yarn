package net.minecraft.entity.mob;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1394;
import net.minecraft.class_1396;
import net.minecraft.class_1399;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.StepAndDestroyBlockGoal;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ZombieEntity extends HostileEntity {
	protected static final EntityAttribute SPAWN_REINFORCEMENTS = new ClampedEntityAttribute(null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0)
		.setName("Spawn Reinforcements Chance");
	private static final UUID BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final EntityAttributeModifier BABY_SPEED_BONUS = new EntityAttributeModifier(
		BABY_SPEED_ID, "Baby speed boost", 0.5, EntityAttributeModifier.Operation.field_6330
	);
	private static final TrackedData<Boolean> field_7434 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_7427 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_7431 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_7425 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final BreakDoorGoal field_7433 = new BreakDoorGoal(this);
	private boolean field_7432;
	private int field_7426;
	private int field_7424;

	public ZombieEntity(EntityType<? extends ZombieEntity> entityType, World world) {
		super(entityType, world);
	}

	public ZombieEntity(World world) {
		this(EntityType.ZOMBIE, world);
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(4, new ZombieEntity.DestroyEggGoal(Blocks.field_10195, this, 1.0, 3));
		this.field_6201.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.field_6201.add(8, new LookAroundGoal(this));
		this.method_7208();
	}

	protected void method_7208() {
		this.field_6201.add(2, new class_1396(this, 1.0, false));
		this.field_6201.add(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
		this.field_6201.add(7, new class_1394(this, 1.0));
		this.field_6185.add(1, new class_1399(this).method_6318(PigZombieEntity.class));
		this.field_6185.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.field_6185.add(3, new FollowTargetGoal(this, AbstractTraderEntity.class, false));
		this.field_6185.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.field_6185.add(5, new FollowTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.FOLLOW_RANGE).setBaseValue(35.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23F);
		this.method_5996(EntityAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
		this.method_5996(EntityAttributes.ARMOR).setBaseValue(2.0);
		this.method_6127().register(SPAWN_REINFORCEMENTS).setBaseValue(this.random.nextDouble() * 0.1F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.method_5841().startTracking(field_7434, false);
		this.method_5841().startTracking(field_7427, 0);
		this.method_5841().startTracking(field_7431, false);
		this.method_5841().startTracking(field_7425, false);
	}

	public boolean isDrowning() {
		return this.method_5841().get(field_7425);
	}

	public void setArmsRaised(boolean bl) {
		this.method_5841().set(field_7431, bl);
	}

	@Environment(EnvType.CLIENT)
	public boolean hasArmsRaised() {
		return this.method_5841().get(field_7431);
	}

	public boolean canBreakDoors() {
		return this.field_7432;
	}

	public void setBreakDoors(boolean bl) {
		if (this.method_7212()) {
			if (this.field_7432 != bl) {
				this.field_7432 = bl;
				((EntityMobNavigation)this.method_5942()).setCanPathThroughDoors(bl);
				if (bl) {
					this.field_6201.add(1, this.field_7433);
				} else {
					this.field_6201.remove(this.field_7433);
				}
			}
		} else if (this.field_7432) {
			this.field_6201.remove(this.field_7433);
			this.field_7432 = false;
		}
	}

	protected boolean method_7212() {
		return true;
	}

	@Override
	public boolean isChild() {
		return this.method_5841().get(field_7434);
	}

	@Override
	protected int method_6110(PlayerEntity playerEntity) {
		if (this.isChild()) {
			this.experiencePoints = (int)((float)this.experiencePoints * 2.5F);
		}

		return super.method_6110(playerEntity);
	}

	public void setChild(boolean bl) {
		this.method_5841().set(field_7434, bl);
		if (this.field_6002 != null && !this.field_6002.isClient) {
			EntityAttributeInstance entityAttributeInstance = this.method_5996(EntityAttributes.MOVEMENT_SPEED);
			entityAttributeInstance.method_6202(BABY_SPEED_BONUS);
			if (bl) {
				entityAttributeInstance.method_6197(BABY_SPEED_BONUS);
			}
		}
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		if (field_7434.equals(trackedData)) {
			this.refreshSize();
		}

		super.method_5674(trackedData);
	}

	protected boolean method_7209() {
		return true;
	}

	@Override
	public void update() {
		if (!this.field_6002.isClient && this.isValid()) {
			if (this.isDrowning()) {
				this.field_7424--;
				if (this.field_7424 < 0) {
					this.method_7218();
				}
			} else if (this.method_7209()) {
				if (this.method_5777(FluidTags.field_15517)) {
					this.field_7426++;
					if (this.field_7426 >= 600) {
						this.method_7213(300);
					}
				} else {
					this.field_7426 = -1;
				}
			}
		}

		super.update();
	}

	@Override
	public void updateMovement() {
		if (this.isValid()) {
			boolean bl = this.method_7216() && this.method_5972();
			if (bl) {
				ItemStack itemStack = this.method_6118(EquipmentSlot.HEAD);
				if (!itemStack.isEmpty()) {
					if (itemStack.hasDurability()) {
						itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
						if (itemStack.getDamage() >= itemStack.getDurability()) {
							this.method_6045(itemStack);
							this.method_5673(EquipmentSlot.HEAD, ItemStack.EMPTY);
						}
					}

					bl = false;
				}

				if (bl) {
					this.setOnFireFor(8);
				}
			}
		}

		super.updateMovement();
	}

	private void method_7213(int i) {
		this.field_7424 = i;
		this.method_5841().set(field_7425, true);
	}

	protected void method_7218() {
		this.method_7200(EntityType.DROWNED);
		this.field_6002.method_8444(null, 1040, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
	}

	protected void method_7200(EntityType<? extends ZombieEntity> entityType) {
		if (!this.invalid) {
			ZombieEntity zombieEntity = entityType.method_5883(this.field_6002);
			zombieEntity.setPositionAndAngles(this);
			zombieEntity.setCanPickUpLoot(this.canPickUpLoot());
			zombieEntity.setBreakDoors(zombieEntity.method_7212() && this.canBreakDoors());
			zombieEntity.method_7205(zombieEntity.field_6002.method_8404(new BlockPos(zombieEntity)).getClampedLocalDifficulty());
			zombieEntity.setChild(this.isChild());
			zombieEntity.setAiDisabled(this.isAiDisabled());

			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				ItemStack itemStack = this.method_6118(equipmentSlot);
				if (!itemStack.isEmpty()) {
					zombieEntity.method_5673(equipmentSlot, itemStack);
					zombieEntity.setEquipmentDropChance(equipmentSlot, this.method_5929(equipmentSlot));
				}
			}

			if (this.hasCustomName()) {
				zombieEntity.method_5665(this.method_5797());
				zombieEntity.setCustomNameVisible(this.isCustomNameVisible());
			}

			this.field_6002.spawnEntity(zombieEntity);
			this.invalidate();
		}
	}

	protected boolean method_7216() {
		return true;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (super.damage(damageSource, f)) {
			LivingEntity livingEntity = this.getTarget();
			if (livingEntity == null && damageSource.method_5529() instanceof LivingEntity) {
				livingEntity = (LivingEntity)damageSource.method_5529();
			}

			if (livingEntity != null
				&& this.field_6002.getDifficulty() == Difficulty.HARD
				&& (double)this.random.nextFloat() < this.method_5996(SPAWN_REINFORCEMENTS).getValue()
				&& this.field_6002.getGameRules().getBoolean("doMobSpawning")) {
				int i = MathHelper.floor(this.x);
				int j = MathHelper.floor(this.y);
				int k = MathHelper.floor(this.z);
				ZombieEntity zombieEntity = new ZombieEntity(this.field_6002);

				for (int l = 0; l < 50; l++) {
					int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					int o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
					BlockPos blockPos = new BlockPos(m, n - 1, o);
					if (this.field_6002.method_8320(blockPos).method_11631(this.field_6002, blockPos) && this.field_6002.method_8602(new BlockPos(m, n, o)) < 10) {
						zombieEntity.setPosition((double)m, (double)n, (double)o);
						if (!this.field_6002.method_18458((double)m, (double)n, (double)o, 7.0)
							&& this.field_6002.method_8606(zombieEntity)
							&& this.field_6002.method_17892(zombieEntity)
							&& !this.field_6002.method_8599(zombieEntity.method_5829())) {
							this.field_6002.spawnEntity(zombieEntity);
							zombieEntity.setTarget(livingEntity);
							zombieEntity.method_5943(this.field_6002, this.field_6002.method_8404(new BlockPos(zombieEntity)), SpawnType.field_16463, null, null);
							this.method_5996(SPAWN_REINFORCEMENTS)
								.method_6197(new EntityAttributeModifier("Zombie reinforcement caller charge", -0.05F, EntityAttributeModifier.Operation.field_6328));
							zombieEntity.method_5996(SPAWN_REINFORCEMENTS)
								.method_6197(new EntityAttributeModifier("Zombie reinforcement callee charge", -0.05F, EntityAttributeModifier.Operation.field_6328));
							break;
						}
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean attack(Entity entity) {
		boolean bl = super.attack(entity);
		if (bl) {
			float f = this.field_6002.method_8404(new BlockPos(this)).getLocalDifficulty();
			if (this.method_6047().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
				entity.setOnFireFor(2 * (int)f);
			}
		}

		return bl;
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_15174;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15088;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14930;
	}

	protected SoundEvent method_7207() {
		return SoundEvents.field_14621;
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		this.method_5783(this.method_7207(), 0.15F, 1.0F);
	}

	@Override
	public EntityGroup method_6046() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		super.initEquipment(localDifficulty);
		if (this.random.nextFloat() < (this.field_6002.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
			int i = this.random.nextInt(3);
			if (i == 0) {
				this.method_5673(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8371));
			} else {
				this.method_5673(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8699));
			}
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		if (this.isChild()) {
			compoundTag.putBoolean("IsBaby", true);
		}

		compoundTag.putBoolean("CanBreakDoors", this.canBreakDoors());
		compoundTag.putInt("InWaterTime", this.isInsideWater() ? this.field_7426 : -1);
		compoundTag.putInt("DrownedConversionTime", this.isDrowning() ? this.field_7424 : -1);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.getBoolean("IsBaby")) {
			this.setChild(true);
		}

		this.setBreakDoors(compoundTag.getBoolean("CanBreakDoors"));
		this.field_7426 = compoundTag.getInt("InWaterTime");
		if (compoundTag.containsKey("DrownedConversionTime", 99) && compoundTag.getInt("DrownedConversionTime") > -1) {
			this.method_7213(compoundTag.getInt("DrownedConversionTime"));
		}
	}

	@Override
	public void method_5874(LivingEntity livingEntity) {
		super.method_5874(livingEntity);
		if ((this.field_6002.getDifficulty() == Difficulty.NORMAL || this.field_6002.getDifficulty() == Difficulty.HARD) && livingEntity instanceof VillagerEntity) {
			if (this.field_6002.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
				return;
			}

			VillagerEntity villagerEntity = (VillagerEntity)livingEntity;
			ZombieVillagerEntity zombieVillagerEntity = EntityType.ZOMBIE_VILLAGER.method_5883(this.field_6002);
			zombieVillagerEntity.setPositionAndAngles(villagerEntity);
			villagerEntity.invalidate();
			zombieVillagerEntity.method_5943(
				this.field_6002, this.field_6002.method_8404(new BlockPos(zombieVillagerEntity)), SpawnType.field_16468, new ZombieEntity.class_1644(false), null
			);
			zombieVillagerEntity.method_7195(villagerEntity.getVillagerData());
			zombieVillagerEntity.method_16916(villagerEntity.method_8264().method_8268());
			zombieVillagerEntity.setChild(villagerEntity.isChild());
			zombieVillagerEntity.setAiDisabled(villagerEntity.isAiDisabled());
			if (villagerEntity.hasCustomName()) {
				zombieVillagerEntity.method_5665(villagerEntity.method_5797());
				zombieVillagerEntity.setCustomNameVisible(villagerEntity.isCustomNameVisible());
			}

			this.field_6002.spawnEntity(zombieVillagerEntity);
			this.field_6002.method_8444(null, 1026, new BlockPos(this), 0);
		}
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return this.isChild() ? 0.93F : 1.74F;
	}

	@Override
	protected boolean method_5939(ItemStack itemStack) {
		return itemStack.getItem() == Items.field_8803 && this.isChild() && this.hasVehicle() ? false : super.method_5939(itemStack);
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		float f = localDifficulty.getClampedLocalDifficulty();
		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * f);
		if (entityData == null) {
			entityData = new ZombieEntity.class_1644(iWorld.getRandom().nextFloat() < 0.05F);
		}

		if (entityData instanceof ZombieEntity.class_1644) {
			ZombieEntity.class_1644 lv = (ZombieEntity.class_1644)entityData;
			if (lv.field_7439) {
				this.setChild(true);
				if ((double)iWorld.getRandom().nextFloat() < 0.05) {
					List<ChickenEntity> list = iWorld.method_8390(ChickenEntity.class, this.method_5829().expand(5.0, 3.0, 5.0), EntityPredicates.NOT_MOUNTED);
					if (!list.isEmpty()) {
						ChickenEntity chickenEntity = (ChickenEntity)list.get(0);
						chickenEntity.setHasJockey(true);
						this.startRiding(chickenEntity);
					}
				} else if ((double)iWorld.getRandom().nextFloat() < 0.05) {
					ChickenEntity chickenEntity2 = EntityType.CHICKEN.method_5883(this.field_6002);
					chickenEntity2.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
					chickenEntity2.method_5943(iWorld, localDifficulty, SpawnType.field_16460, null, null);
					chickenEntity2.setHasJockey(true);
					iWorld.spawnEntity(chickenEntity2);
					this.startRiding(chickenEntity2);
				}
			}

			this.setBreakDoors(this.method_7212() && this.random.nextFloat() < f * 0.1F);
			this.initEquipment(localDifficulty);
			this.method_5984(localDifficulty);
		}

		if (this.method_6118(EquipmentSlot.HEAD).isEmpty()) {
			LocalDate localDate = LocalDate.now();
			int i = localDate.get(ChronoField.DAY_OF_MONTH);
			int j = localDate.get(ChronoField.MONTH_OF_YEAR);
			if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
				this.method_5673(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.field_10009 : Blocks.field_10147));
				this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0F;
			}
		}

		this.method_7205(f);
		return entityData;
	}

	protected void method_7205(float f) {
		this.method_5996(EntityAttributes.KNOCKBACK_RESISTANCE)
			.method_6197(new EntityAttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05F, EntityAttributeModifier.Operation.field_6328));
		double d = this.random.nextDouble() * 1.5 * (double)f;
		if (d > 1.0) {
			this.method_5996(EntityAttributes.FOLLOW_RANGE)
				.method_6197(new EntityAttributeModifier("Random zombie-spawn bonus", d, EntityAttributeModifier.Operation.field_6331));
		}

		if (this.random.nextFloat() < f * 0.05F) {
			this.method_5996(SPAWN_REINFORCEMENTS)
				.method_6197(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, EntityAttributeModifier.Operation.field_6328));
			this.method_5996(EntityAttributes.MAX_HEALTH)
				.method_6197(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, EntityAttributeModifier.Operation.field_6331));
			this.setBreakDoors(this.method_7212());
		}
	}

	@Override
	public double getHeightOffset() {
		return this.isChild() ? 0.0 : -0.45;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		Entity entity = damageSource.method_5529();
		if (entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.method_7008()) {
				creeperEntity.method_7002();
				ItemStack itemStack = this.method_7215();
				if (!itemStack.isEmpty()) {
					this.method_5775(itemStack);
				}
			}
		}
	}

	protected ItemStack method_7215() {
		return new ItemStack(Items.ZOMBIE_HEAD);
	}

	class DestroyEggGoal extends StepAndDestroyBlockGoal {
		DestroyEggGoal(Block block, MobEntityWithAi mobEntityWithAi, double d, int i) {
			super(block, mobEntityWithAi, d, i);
		}

		@Override
		public void method_6307(IWorld iWorld, BlockPos blockPos) {
			iWorld.method_8396(null, blockPos, SoundEvents.field_15023, SoundCategory.field_15251, 0.5F, 0.9F + ZombieEntity.this.random.nextFloat() * 0.2F);
		}

		@Override
		public void method_6309(World world, BlockPos blockPos) {
			world.method_8396(null, blockPos, SoundEvents.field_14687, SoundCategory.field_15245, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
		}

		@Override
		public double getDesiredSquaredDistanceToTarget() {
			return 1.3;
		}
	}

	public class class_1644 implements EntityData {
		public final boolean field_7439;

		private class_1644(boolean bl) {
			this.field_7439 = bl;
		}
	}
}
