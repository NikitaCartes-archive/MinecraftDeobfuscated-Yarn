package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1394;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AvoidGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class PandaEntity extends AnimalEntity {
	private static final TrackedData<Integer> field_6764 = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_6771 = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_6780 = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Byte> MAIN_GENE = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Byte> HIDDEN_GENE = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Byte> FLAGS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
	private boolean field_6769;
	private boolean field_6770;
	public int field_6767;
	private Vec3d field_18277;
	private float field_6777;
	private float field_6779;
	private float field_6774;
	private float field_6775;
	private float field_6772;
	private float field_6773;
	private static final Predicate<ItemEntity> field_6765 = itemEntity -> {
		Item item = itemEntity.getStack().getItem();
		return (item == Blocks.field_10211.getItem() || item == Blocks.field_10183.getItem()) && itemEntity.isValid() && !itemEntity.cannotPickup();
	};

	public PandaEntity(EntityType<? extends PandaEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new PandaEntity.PandaMoveControl(this);
		if (!this.isChild()) {
			this.setCanPickUpLoot(true);
		}
	}

	@Override
	public boolean canPickUp(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		return !this.getEquippedStack(equipmentSlot).isEmpty() ? false : equipmentSlot == EquipmentSlot.HAND_MAIN && super.canPickUp(itemStack);
	}

	public int method_6521() {
		return this.dataTracker.get(field_6764);
	}

	public void method_6517(int i) {
		this.dataTracker.set(field_6764, i);
	}

	public boolean method_6545() {
		return this.hasFlag(2);
	}

	public boolean method_6535() {
		return this.hasFlag(8);
	}

	public void method_6513(boolean bl) {
		this.setFlag(8, bl);
	}

	public boolean method_6514() {
		return this.hasFlag(16);
	}

	public void method_6505(boolean bl) {
		this.setFlag(16, bl);
	}

	public boolean method_6527() {
		return this.dataTracker.get(field_6780) > 0;
	}

	public void method_6552(boolean bl) {
		this.dataTracker.set(field_6780, bl ? 1 : 0);
	}

	private int method_6528() {
		return this.dataTracker.get(field_6780);
	}

	private void method_6558(int i) {
		this.dataTracker.set(field_6780, i);
	}

	public void method_6546(boolean bl) {
		this.setFlag(2, bl);
		if (!bl) {
			this.method_6539(0);
		}
	}

	public int method_6532() {
		return this.dataTracker.get(field_6771);
	}

	public void method_6539(int i) {
		this.dataTracker.set(field_6771, i);
	}

	public PandaEntity.Gene getMainGene() {
		return PandaEntity.Gene.byId(this.dataTracker.get(MAIN_GENE));
	}

	public void setMainGene(PandaEntity.Gene gene) {
		if (gene.getId() > 6) {
			gene = PandaEntity.Gene.createRandom(this.random);
		}

		this.dataTracker.set(MAIN_GENE, (byte)gene.getId());
	}

	public PandaEntity.Gene getHiddenGene() {
		return PandaEntity.Gene.byId(this.dataTracker.get(HIDDEN_GENE));
	}

	public void setHiddenGene(PandaEntity.Gene gene) {
		if (gene.getId() > 6) {
			gene = PandaEntity.Gene.createRandom(this.random);
		}

		this.dataTracker.set(HIDDEN_GENE, (byte)gene.getId());
	}

	public boolean method_6526() {
		return this.hasFlag(4);
	}

	public void method_6541(boolean bl) {
		this.setFlag(4, bl);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_6764, 0);
		this.dataTracker.startTracking(field_6771, 0);
		this.dataTracker.startTracking(MAIN_GENE, (byte)0);
		this.dataTracker.startTracking(HIDDEN_GENE, (byte)0);
		this.dataTracker.startTracking(FLAGS, (byte)0);
		this.dataTracker.startTracking(field_6780, 0);
	}

	private boolean hasFlag(int i) {
		return (this.dataTracker.get(FLAGS) & i) != 0;
	}

	private void setFlag(int i, boolean bl) {
		byte b = this.dataTracker.get(FLAGS);
		if (bl) {
			this.dataTracker.set(FLAGS, (byte)(b | i));
		} else {
			this.dataTracker.set(FLAGS, (byte)(b & ~i));
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putString("MainGene", this.getMainGene().getName());
		compoundTag.putString("HiddenGene", this.getHiddenGene().getName());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setMainGene(PandaEntity.Gene.byName(compoundTag.getString("MainGene")));
		this.setHiddenGene(PandaEntity.Gene.byName(compoundTag.getString("HiddenGene")));
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		PandaEntity pandaEntity = EntityType.PANDA.create(this.world);
		if (passiveEntity instanceof PandaEntity) {
			pandaEntity.initGenes(this, (PandaEntity)passiveEntity);
		}

		pandaEntity.resetAttributes();
		return pandaEntity;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(2, new PandaEntity.ExtinguishFireGoal(this, 2.0));
		this.goalSelector.add(2, new PandaEntity.PandaMateGoal(this, 1.0));
		this.goalSelector.add(3, new PandaEntity.class_4054(this, 1.2F, true));
		this.goalSelector.add(4, new TemptGoal(this, 1.0, Ingredient.ofItems(Blocks.field_10211.getItem()), false));
		this.goalSelector.add(6, new PandaEntity.PandaFleeGoal(this, PlayerEntity.class, 8.0F, 2.0, 2.0));
		this.goalSelector.add(6, new PandaEntity.PandaFleeGoal(this, HostileEntity.class, 4.0F, 2.0, 2.0));
		this.goalSelector.add(7, new PandaEntity.class_1449());
		this.goalSelector.add(8, new PandaEntity.class_1445(this));
		this.goalSelector.add(8, new PandaEntity.class_1450(this));
		this.goalSelector.add(9, new PandaEntity.class_4056(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(10, new LookAroundGoal(this));
		this.goalSelector.add(12, new PandaEntity.class_1448(this));
		this.goalSelector.add(13, new FollowParentGoal(this, 1.25));
		this.goalSelector.add(14, new class_1394(this, 1.0));
		this.targetSelector.add(1, new PandaEntity.class_1444(this).method_6318(new Class[0]));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.15F);
		this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
	}

	public PandaEntity.Gene getProductGene() {
		return PandaEntity.Gene.getProductGene(this.getMainGene(), this.getHiddenGene());
	}

	public boolean isLazy() {
		return this.getProductGene() == PandaEntity.Gene.field_6794;
	}

	public boolean isWorried() {
		return this.getProductGene() == PandaEntity.Gene.field_6795;
	}

	public boolean isPlayful() {
		return this.getProductGene() == PandaEntity.Gene.field_6791;
	}

	public boolean isWeak() {
		return this.getProductGene() == PandaEntity.Gene.field_6793;
	}

	public boolean isAggressive() {
		return this.getProductGene() == PandaEntity.Gene.field_6789;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return false;
	}

	@Override
	public boolean attack(Entity entity) {
		this.playSound(SoundEvents.field_14552, 1.0F, 1.0F);
		if (!this.isAggressive()) {
			this.field_6770 = true;
		}

		return super.attack(entity);
	}

	@Override
	public void update() {
		super.update();
		if (this.isWorried()) {
			if (this.world.isThundering() && !this.isInsideWater()) {
				this.method_6513(true);
				this.method_6552(false);
			} else if (!this.method_6527()) {
				this.method_6513(false);
			}
		}

		if (this.getTarget() == null) {
			this.field_6769 = false;
			this.field_6770 = false;
		}

		if (this.method_6521() > 0) {
			if (this.getTarget() != null) {
				this.method_5951(this.getTarget(), 90.0F, 90.0F);
			}

			if (this.method_6521() == 29 || this.method_6521() == 14) {
				this.playSound(SoundEvents.field_14936, 1.0F, 1.0F);
			}

			this.method_6517(this.method_6521() - 1);
		}

		if (this.method_6545()) {
			this.method_6539(this.method_6532() + 1);
			if (this.method_6532() > 20) {
				this.method_6546(false);
				this.sneeze();
			} else if (this.method_6532() == 1) {
				this.playSound(SoundEvents.field_14997, 1.0F, 1.0F);
			}
		}

		if (this.method_6526()) {
			this.method_6537();
		} else {
			this.field_6767 = 0;
		}

		if (this.method_6535()) {
			this.pitch = 0.0F;
		}

		this.method_6544();
		this.method_6536();
		this.method_6503();
		this.method_6523();
	}

	public boolean method_6524() {
		return this.isWorried() && this.world.isThundering();
	}

	private void method_6536() {
		if (!this.method_6527()
			&& this.method_6535()
			&& !this.method_6524()
			&& !this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()
			&& this.random.nextInt(80) == 1) {
			this.method_6552(true);
		} else if (this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty() || !this.method_6535()) {
			this.method_6552(false);
		}

		if (this.method_6527()) {
			this.method_6512();
			if (!this.world.isClient && this.method_6528() > 80 && this.random.nextInt(20) == 1) {
				if (this.method_6528() > 100 && this.method_16106(this.getEquippedStack(EquipmentSlot.HAND_MAIN))) {
					if (!this.world.isClient) {
						this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
					}

					this.method_6513(false);
				}

				this.method_6552(false);
				return;
			}

			this.method_6558(this.method_6528() + 1);
		}
	}

	private void method_6512() {
		if (this.method_6528() % 5 == 0) {
			this.playSound(SoundEvents.field_15106, 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

			for (int i = 0; i < 6; i++) {
				Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, ((double)this.random.nextFloat() - 0.5) * 0.1);
				vec3d = vec3d.rotateX(-this.pitch * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(-this.yaw * (float) (Math.PI / 180.0));
				double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
				Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.8, d, 1.0 + ((double)this.random.nextFloat() - 0.5) * 0.4);
				vec3d2 = vec3d2.rotateY(-this.field_6283 * (float) (Math.PI / 180.0));
				vec3d2 = vec3d2.add(this.x, this.y + (double)this.getStandingEyeHeight() + 1.0, this.z);
				this.world
					.addParticle(
						new ItemStackParticleParameters(ParticleTypes.field_11218, this.getEquippedStack(EquipmentSlot.HAND_MAIN)),
						vec3d2.x,
						vec3d2.y,
						vec3d2.z,
						vec3d.x,
						vec3d.y + 0.05,
						vec3d.z
					);
			}
		}
	}

	private void method_6544() {
		this.field_6779 = this.field_6777;
		if (this.method_6535()) {
			this.field_6777 = Math.min(1.0F, this.field_6777 + 0.15F);
		} else {
			this.field_6777 = Math.max(0.0F, this.field_6777 - 0.19F);
		}
	}

	private void method_6503() {
		this.field_6775 = this.field_6774;
		if (this.method_6514()) {
			this.field_6774 = Math.min(1.0F, this.field_6774 + 0.15F);
		} else {
			this.field_6774 = Math.max(0.0F, this.field_6774 - 0.19F);
		}
	}

	private void method_6523() {
		this.field_6773 = this.field_6772;
		if (this.method_6526()) {
			this.field_6772 = Math.min(1.0F, this.field_6772 + 0.15F);
		} else {
			this.field_6772 = Math.max(0.0F, this.field_6772 - 0.19F);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_6534(float f) {
		return MathHelper.lerp(f, this.field_6779, this.field_6777);
	}

	@Environment(EnvType.CLIENT)
	public float method_6555(float f) {
		return MathHelper.lerp(f, this.field_6775, this.field_6774);
	}

	@Environment(EnvType.CLIENT)
	public float method_6560(float f) {
		return MathHelper.lerp(f, this.field_6773, this.field_6772);
	}

	private void method_6537() {
		this.field_6767++;
		if (this.field_6767 > 32) {
			this.method_6541(false);
		} else {
			if (!this.world.isClient) {
				Vec3d vec3d = this.getVelocity();
				if (this.field_6767 == 1) {
					float f = this.yaw * (float) (Math.PI / 180.0);
					float g = this.isChild() ? 0.1F : 0.2F;
					this.field_18277 = new Vec3d(vec3d.x + (double)(-MathHelper.sin(f) * g), 0.0, vec3d.z + (double)(MathHelper.cos(f) * g));
					this.setVelocity(this.field_18277.add(0.0, 0.27, 0.0));
				} else if ((float)this.field_6767 != 7.0F && (float)this.field_6767 != 15.0F && (float)this.field_6767 != 23.0F) {
					this.setVelocity(this.field_18277.x, vec3d.y, this.field_18277.z);
				} else {
					this.setVelocity(0.0, this.onGround ? 0.27 : vec3d.y, 0.0);
				}
			}
		}
	}

	private void sneeze() {
		Vec3d vec3d = this.getVelocity();
		this.world
			.addParticle(
				ParticleTypes.field_11234,
				this.x - (double)(this.getWidth() + 1.0F) * 0.5 * (double)MathHelper.sin(this.field_6283 * (float) (Math.PI / 180.0)),
				this.y + (double)this.getStandingEyeHeight() - 0.1F,
				this.z + (double)(this.getWidth() + 1.0F) * 0.5 * (double)MathHelper.cos(this.field_6283 * (float) (Math.PI / 180.0)),
				vec3d.x,
				0.0,
				vec3d.z
			);
		this.playSound(SoundEvents.field_15076, 1.0F, 1.0F);

		for (PandaEntity pandaEntity : this.world.method_18467(PandaEntity.class, this.getBoundingBox().expand(10.0))) {
			if (!pandaEntity.isChild() && pandaEntity.onGround && !pandaEntity.isInsideWater() && pandaEntity.method_18442()) {
				pandaEntity.jump();
			}
		}

		if (!this.world.isClient() && this.random.nextInt(700) == 0 && this.world.getGameRules().getBoolean("doMobLoot")) {
			this.dropItem(Items.field_8777);
		}
	}

	@Override
	protected void pickupItem(ItemEntity itemEntity) {
		if (this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty() && field_6765.test(itemEntity)) {
			ItemStack itemStack = itemEntity.getStack();
			this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack);
			this.handDropChances[EquipmentSlot.HAND_MAIN.getEntitySlotId()] = 2.0F;
			this.pickUpEntity(itemEntity, itemStack.getAmount());
			itemEntity.invalidate();
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		this.method_6513(false);
		return super.damage(damageSource, f);
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		this.setMainGene(PandaEntity.Gene.createRandom(this.random));
		this.setHiddenGene(PandaEntity.Gene.createRandom(this.random));
		this.resetAttributes();
		if (entityData instanceof PandaEntity.SpawnData) {
			if (this.random.nextInt(5) == 0) {
				this.setBreedingAge(-24000);
			}
		} else {
			entityData = new PandaEntity.SpawnData();
		}

		return entityData;
	}

	public void initGenes(PandaEntity pandaEntity, @Nullable PandaEntity pandaEntity2) {
		if (pandaEntity2 == null) {
			if (this.random.nextBoolean()) {
				this.setMainGene(pandaEntity.getRandomGene());
				this.setHiddenGene(PandaEntity.Gene.createRandom(this.random));
			} else {
				this.setMainGene(PandaEntity.Gene.createRandom(this.random));
				this.setHiddenGene(pandaEntity.getRandomGene());
			}
		} else if (this.random.nextBoolean()) {
			this.setMainGene(pandaEntity.getRandomGene());
			this.setHiddenGene(pandaEntity2.getRandomGene());
		} else {
			this.setMainGene(pandaEntity2.getRandomGene());
			this.setHiddenGene(pandaEntity.getRandomGene());
		}

		if (this.random.nextInt(32) == 0) {
			this.setMainGene(PandaEntity.Gene.createRandom(this.random));
		}

		if (this.random.nextInt(32) == 0) {
			this.setHiddenGene(PandaEntity.Gene.createRandom(this.random));
		}
	}

	private PandaEntity.Gene getRandomGene() {
		return this.random.nextBoolean() ? this.getMainGene() : this.getHiddenGene();
	}

	public void resetAttributes() {
		if (this.isWeak()) {
			this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		}

		if (this.isLazy()) {
			this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.07F);
		}
	}

	private void method_18057() {
		if (!this.isInsideWater()) {
			this.method_5930(0.0F);
			this.getNavigation().stop();
			this.method_6513(true);
		}
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() instanceof SpawnEggItem) {
			return super.interactMob(playerEntity, hand);
		} else if (this.method_6524()) {
			return false;
		} else if (this.method_6514()) {
			this.method_6505(false);
			return true;
		} else if (this.isBreedingItem(itemStack)) {
			if (this.getTarget() != null) {
				this.field_6769 = true;
			}

			if (this.isChild()) {
				this.method_6475(playerEntity, itemStack);
				this.method_5620((int)((float)(-this.getBreedingAge() / 20) * 0.1F), true);
			} else if (!this.world.isClient && this.getBreedingAge() == 0 && this.method_6482()) {
				this.method_6475(playerEntity, itemStack);
				this.method_6480(playerEntity);
			} else {
				if (this.world.isClient || this.method_6535() || this.isInsideWater()) {
					return false;
				}

				this.method_18057();
				this.method_6552(true);
				ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.HAND_MAIN);
				if (!itemStack2.isEmpty() && !playerEntity.abilities.creativeMode) {
					this.dropStack(itemStack2);
				}

				this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(itemStack.getItem(), 1));
				this.method_6475(playerEntity, itemStack);
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (this.isAggressive()) {
			return SoundEvents.field_14801;
		} else {
			return this.isWorried() ? SoundEvents.field_14715 : SoundEvents.field_14604;
		}
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_15035, 0.15F, 1.0F);
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return itemStack.getItem() == Blocks.field_10211.getItem();
	}

	private boolean method_16106(ItemStack itemStack) {
		return this.isBreedingItem(itemStack) || itemStack.getItem() == Blocks.field_10183.getItem();
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15208;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14668;
	}

	public boolean method_18442() {
		return !this.method_6514() && !this.method_6524() && !this.method_6527() && !this.method_6526() && !this.method_6535();
	}

	static class ExtinguishFireGoal extends EscapeDangerGoal {
		private final PandaEntity field_6802;

		public ExtinguishFireGoal(PandaEntity pandaEntity, double d) {
			super(pandaEntity, d);
			this.field_6802 = pandaEntity;
		}

		@Override
		public boolean canStart() {
			if (!this.field_6802.isOnFire()) {
				return false;
			} else {
				BlockPos blockPos = this.locateClosestWater(this.owner.world, this.owner, 5, 4);
				if (blockPos != null) {
					this.targetX = (double)blockPos.getX();
					this.targetY = (double)blockPos.getY();
					this.targetZ = (double)blockPos.getZ();
					return true;
				} else {
					return this.method_6301();
				}
			}
		}

		@Override
		public boolean shouldContinue() {
			if (this.field_6802.method_6535()) {
				this.field_6802.getNavigation().stop();
				return false;
			} else {
				return super.shouldContinue();
			}
		}
	}

	public static enum Gene {
		field_6788(0, "normal", false),
		field_6794(1, "lazy", false),
		field_6795(2, "worried", false),
		field_6791(3, "playful", false),
		field_6792(4, "brown", true),
		field_6793(5, "weak", true),
		field_6789(6, "aggressive", false);

		private static final PandaEntity.Gene[] VALUES = (PandaEntity.Gene[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(PandaEntity.Gene::getId))
			.toArray(PandaEntity.Gene[]::new);
		private final int id;
		private final String name;
		private final boolean recessive;

		private Gene(int j, String string2, boolean bl) {
			this.id = j;
			this.name = string2;
			this.recessive = bl;
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public boolean isRecessive() {
			return this.recessive;
		}

		private static PandaEntity.Gene getProductGene(PandaEntity.Gene gene, PandaEntity.Gene gene2) {
			if (gene.isRecessive()) {
				return gene == gene2 ? gene : field_6788;
			} else {
				return gene;
			}
		}

		public static PandaEntity.Gene byId(int i) {
			if (i < 0 || i >= VALUES.length) {
				i = 0;
			}

			return VALUES[i];
		}

		public static PandaEntity.Gene byName(String string) {
			for (PandaEntity.Gene gene : values()) {
				if (gene.name.equals(string)) {
					return gene;
				}
			}

			return field_6788;
		}

		public static PandaEntity.Gene createRandom(Random random) {
			int i = random.nextInt(16);
			if (i == 0) {
				return field_6794;
			} else if (i == 1) {
				return field_6795;
			} else if (i == 2) {
				return field_6791;
			} else if (i == 4) {
				return field_6789;
			} else if (i < 9) {
				return field_6793;
			} else {
				return i < 11 ? field_6792 : field_6788;
			}
		}
	}

	static class PandaFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final PandaEntity field_6782;

		public PandaFleeGoal(PandaEntity pandaEntity, Class<T> class_, float f, double d, double e) {
			super(pandaEntity, class_, f, d, e, EntityPredicates.EXCEPT_SPECTATOR::test);
			this.field_6782 = pandaEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_6782.isWorried() && this.field_6782.method_18442() && super.canStart();
		}
	}

	static class PandaMateGoal extends AnimalMateGoal {
		private static final TargetPredicate field_18115 = new TargetPredicate().setBaseMaxDistance(8.0).includeTeammates().includeInvulnerable();
		private final PandaEntity field_6784;
		private int field_6783;

		public PandaMateGoal(PandaEntity pandaEntity, double d) {
			super(pandaEntity, d);
			this.field_6784 = pandaEntity;
		}

		@Override
		public boolean canStart() {
			if (!super.canStart() || this.field_6784.method_6521() != 0) {
				return false;
			} else if (!this.method_6561()) {
				if (this.field_6783 <= this.field_6784.age) {
					this.field_6784.method_6517(32);
					this.field_6783 = this.field_6784.age + 600;
					if (this.field_6784.method_6034()) {
						PlayerEntity playerEntity = this.world.method_18462(field_18115, this.field_6784);
						this.field_6784.setTarget(playerEntity);
					}
				}

				return false;
			} else {
				return true;
			}
		}

		private boolean method_6561() {
			BlockPos blockPos = new BlockPos(this.field_6784);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 8; j++) {
					for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
						for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
							mutable.set(blockPos).setOffset(k, i, l);
							if (this.world.getBlockState(mutable).getBlock() == Blocks.field_10211) {
								return true;
							}
						}
					}
				}
			}

			return false;
		}
	}

	static class PandaMoveControl extends MoveControl {
		private final PandaEntity panda;

		public PandaMoveControl(PandaEntity pandaEntity) {
			super(pandaEntity);
			this.panda = pandaEntity;
		}

		@Override
		public void tick() {
			if (this.panda.method_18442()) {
				super.tick();
			}
		}
	}

	static class SpawnData implements EntityData {
		private SpawnData() {
		}
	}

	static class class_1444 extends AvoidGoal {
		private final PandaEntity field_6798;

		public class_1444(PandaEntity pandaEntity, Class<?>... classs) {
			super(pandaEntity, classs);
			this.field_6798 = pandaEntity;
		}

		@Override
		public boolean shouldContinue() {
			if (!this.field_6798.field_6769 && !this.field_6798.field_6770) {
				return super.shouldContinue();
			} else {
				this.field_6798.setTarget(null);
				return false;
			}
		}

		@Override
		protected void method_6319(MobEntity mobEntity, LivingEntity livingEntity) {
			if (mobEntity instanceof PandaEntity && ((PandaEntity)mobEntity).isAggressive()) {
				mobEntity.setTarget(livingEntity);
			}
		}
	}

	static class class_1445 extends Goal {
		private final PandaEntity field_6800;
		private int field_6799;

		public class_1445(PandaEntity pandaEntity) {
			this.field_6800 = pandaEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_6799 < this.field_6800.age && this.field_6800.isLazy() && this.field_6800.method_18442() && this.field_6800.random.nextInt(400) == 1;
		}

		@Override
		public boolean shouldContinue() {
			return !this.field_6800.isInsideWater() && (this.field_6800.isLazy() || this.field_6800.random.nextInt(600) != 1)
				? this.field_6800.random.nextInt(2000) != 1
				: false;
		}

		@Override
		public void start() {
			this.field_6800.method_6505(true);
			this.field_6799 = 0;
		}

		@Override
		public void onRemove() {
			this.field_6800.method_6505(false);
			this.field_6799 = this.field_6800.age + 200;
		}
	}

	static class class_1448 extends Goal {
		private final PandaEntity field_6803;

		public class_1448(PandaEntity pandaEntity) {
			this.field_6803 = pandaEntity;
			this.setControlBits(EnumSet.of(Goal.ControlBit.field_18405, Goal.ControlBit.field_18406, Goal.ControlBit.field_18407));
		}

		@Override
		public boolean canStart() {
			if ((this.field_6803.isChild() || this.field_6803.isPlayful()) && this.field_6803.onGround) {
				if (!this.field_6803.method_18442()) {
					return false;
				} else {
					float f = this.field_6803.yaw * (float) (Math.PI / 180.0);
					int i = 0;
					int j = 0;
					float g = -MathHelper.sin(f);
					float h = MathHelper.cos(f);
					if ((double)Math.abs(g) > 0.5) {
						i = (int)((float)i + g / Math.abs(g));
					}

					if ((double)Math.abs(h) > 0.5) {
						j = (int)((float)j + h / Math.abs(h));
					}

					if (this.field_6803.world.getBlockState(new BlockPos(this.field_6803).add(i, -1, j)).isAir()) {
						return true;
					} else {
						return this.field_6803.isPlayful() && this.field_6803.random.nextInt(60) == 1 ? true : this.field_6803.random.nextInt(500) == 1;
					}
				}
			} else {
				return false;
			}
		}

		@Override
		public boolean shouldContinue() {
			return false;
		}

		@Override
		public void start() {
			this.field_6803.method_6541(true);
		}

		@Override
		public boolean canStop() {
			return false;
		}
	}

	class class_1449 extends Goal {
		private int field_6804;

		public class_1449() {
			this.setControlBits(EnumSet.of(Goal.ControlBit.field_18405));
		}

		@Override
		public boolean canStart() {
			if (this.field_6804 <= PandaEntity.this.age
				&& !PandaEntity.this.isChild()
				&& !PandaEntity.this.isInsideWater()
				&& PandaEntity.this.method_18442()
				&& PandaEntity.this.method_6521() <= 0) {
				List<ItemEntity> list = PandaEntity.this.world
					.getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(6.0, 6.0, 6.0), PandaEntity.field_6765);
				return !list.isEmpty() || !PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty();
			} else {
				return false;
			}
		}

		@Override
		public boolean shouldContinue() {
			return !PandaEntity.this.isInsideWater() && (PandaEntity.this.isLazy() || PandaEntity.this.random.nextInt(600) != 1)
				? PandaEntity.this.random.nextInt(2000) != 1
				: false;
		}

		@Override
		public void tick() {
			if (!PandaEntity.this.method_6535() && !PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
				PandaEntity.this.method_18057();
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = PandaEntity.this.world
				.getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PandaEntity.field_6765);
			if (!list.isEmpty() && PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
				PandaEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
			} else if (!PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
				PandaEntity.this.method_18057();
			}

			this.field_6804 = 0;
		}

		@Override
		public void onRemove() {
			ItemStack itemStack = PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN);
			if (!itemStack.isEmpty()) {
				PandaEntity.this.dropStack(itemStack);
				PandaEntity.this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
				int i = PandaEntity.this.isLazy() ? PandaEntity.this.random.nextInt(50) + 10 : PandaEntity.this.random.nextInt(150) + 10;
				this.field_6804 = PandaEntity.this.age + i * 20;
			}

			PandaEntity.this.method_6513(false);
		}
	}

	static class class_1450 extends Goal {
		private final PandaEntity field_6806;

		public class_1450(PandaEntity pandaEntity) {
			this.field_6806 = pandaEntity;
		}

		@Override
		public boolean canStart() {
			if (this.field_6806.isChild() && this.field_6806.method_18442()) {
				return this.field_6806.isWeak() && this.field_6806.random.nextInt(500) == 1 ? true : this.field_6806.random.nextInt(6000) == 1;
			} else {
				return false;
			}
		}

		@Override
		public boolean shouldContinue() {
			return false;
		}

		@Override
		public void start() {
			this.field_6806.method_6546(true);
		}
	}

	static class class_4054 extends MeleeAttackGoal {
		private final PandaEntity field_18114;

		public class_4054(PandaEntity pandaEntity, double d, boolean bl) {
			super(pandaEntity, d, bl);
			this.field_18114 = pandaEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_18114.method_18442() && super.canStart();
		}
	}

	static class class_4056 extends LookAtEntityGoal {
		private final PandaEntity field_18116;

		public class_4056(PandaEntity pandaEntity, Class<? extends LivingEntity> class_, float f) {
			super(pandaEntity, class_, f);
			this.field_18116 = pandaEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_18116.method_18442() && super.canStart();
		}
	}
}
