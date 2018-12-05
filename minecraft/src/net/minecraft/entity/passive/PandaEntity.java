package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1361;
import net.minecraft.class_1374;
import net.minecraft.class_1376;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.class_3730;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ItemStackParticle;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class PandaEntity extends AnimalEntity {
	private static final TrackedData<Integer> field_6764 = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_6771 = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_6780 = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Byte> field_6766 = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Byte> field_6781 = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Byte> field_6768 = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
	private boolean field_6769;
	private boolean field_6770;
	public int field_6767;
	private double field_6776;
	private double field_6778;
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

	public PandaEntity(World world) {
		super(EntityType.PANDA, world);
		this.setSize(1.3F, 1.25F);
		this.moveControl = new PandaEntity.PandaMoveControl(this);
		if (!this.isChild()) {
			this.setCanPickUpLoot(true);
		}
	}

	public int method_6521() {
		return this.dataTracker.get(field_6764);
	}

	public void method_6517(int i) {
		this.dataTracker.set(field_6764, i);
	}

	public boolean method_6545() {
		return this.method_6533(2);
	}

	public boolean method_6535() {
		return this.method_6533(8);
	}

	public void method_6513(boolean bl) {
		this.method_6557(8, bl);
	}

	public boolean method_6514() {
		return this.method_6533(16);
	}

	public void method_6505(boolean bl) {
		this.method_6557(16, bl);
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
		this.method_6557(2, bl);
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

	public PandaEntity.class_1443 method_6525() {
		return PandaEntity.class_1443.method_6566(this.dataTracker.get(field_6766));
	}

	public void method_6529(PandaEntity.class_1443 arg) {
		if (arg.method_6564() > 6) {
			arg = this.method_6543();
		}

		this.dataTracker.set(field_6766, (byte)arg.method_6564());
	}

	public PandaEntity.class_1443 method_6508() {
		return PandaEntity.class_1443.method_6566(this.dataTracker.get(field_6781));
	}

	public void method_6547(PandaEntity.class_1443 arg) {
		if (arg.method_6564() > 6) {
			arg = this.method_6543();
		}

		this.dataTracker.set(field_6781, (byte)arg.method_6564());
	}

	public boolean method_6526() {
		return this.method_6533(4);
	}

	public void method_6541(boolean bl) {
		this.method_6557(4, bl);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_6764, 0);
		this.dataTracker.startTracking(field_6771, 0);
		this.dataTracker.startTracking(field_6766, (byte)0);
		this.dataTracker.startTracking(field_6781, (byte)0);
		this.dataTracker.startTracking(field_6768, (byte)0);
		this.dataTracker.startTracking(field_6780, 0);
	}

	private boolean method_6533(int i) {
		return (this.dataTracker.get(field_6768) & i) != 0;
	}

	private void method_6557(int i, boolean bl) {
		byte b = this.dataTracker.get(field_6768);
		if (bl) {
			this.dataTracker.set(field_6768, (byte)(b | i));
		} else {
			this.dataTracker.set(field_6768, (byte)(b & ~i));
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putString("MainGene", this.method_6525().method_6565());
		compoundTag.putString("HiddenGene", this.method_6508().method_6565());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.method_6529(PandaEntity.class_1443.method_6567(compoundTag.getString("MainGene")));
		this.method_6547(PandaEntity.class_1443.method_6567(compoundTag.getString("HiddenGene")));
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		PandaEntity pandaEntity = new PandaEntity(this.world);
		if (passiveEntity instanceof PandaEntity) {
			pandaEntity.method_6515(this, (PandaEntity)passiveEntity);
		}

		pandaEntity.method_6538();
		return pandaEntity;
	}

	@Override
	protected void method_5959() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(2, new PandaEntity.class_1447(this, 2.0));
		this.goalSelector.add(2, new PandaEntity.PandaMateGoal(this, 1.0));
		this.goalSelector.add(3, new MeleeAttackGoal(this, 1.2F, true));
		this.goalSelector.add(4, new TemptGoal(this, 1.0, Ingredient.ofItems(Blocks.field_10211.getItem()), false));
		this.goalSelector.add(6, new PandaEntity.PandaFleeGoal(this, PlayerEntity.class, 8.0F, 2.0, 2.0));
		this.goalSelector.add(6, new PandaEntity.PandaFleeGoal(this, HostileEntity.class, 4.0F, 2.0, 2.0));
		this.goalSelector.add(7, new PandaEntity.class_1449());
		this.goalSelector.add(8, new PandaEntity.class_1445(this));
		this.goalSelector.add(8, new PandaEntity.class_1450(this));
		this.goalSelector.add(9, new class_1361(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(10, new class_1376(this));
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

	public PandaEntity.class_1443 method_6554() {
		return PandaEntity.class_1443.method_6569(this.method_6525(), this.method_6508());
	}

	public boolean method_6549() {
		return this.method_6554() == PandaEntity.class_1443.field_6794;
	}

	public boolean method_6509() {
		return this.method_6554() == PandaEntity.class_1443.field_6795;
	}

	public boolean method_6522() {
		return this.method_6554() == PandaEntity.class_1443.field_6791;
	}

	public boolean method_6550() {
		return this.method_6554() == PandaEntity.class_1443.field_6793;
	}

	public boolean method_6510() {
		return this.method_6554() == PandaEntity.class_1443.field_6789;
	}

	private PandaEntity.class_1443 method_6543() {
		int i = this.random.nextInt(16);
		if (i == 0) {
			return PandaEntity.class_1443.field_6794;
		} else if (i == 1) {
			return PandaEntity.class_1443.field_6795;
		} else if (i == 2) {
			return PandaEntity.class_1443.field_6791;
		} else if (i == 4) {
			return PandaEntity.class_1443.field_6789;
		} else if (i < 9) {
			return PandaEntity.class_1443.field_6793;
		} else {
			return i < 11 ? PandaEntity.class_1443.field_6792 : PandaEntity.class_1443.field_6788;
		}
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return false;
	}

	@Override
	public boolean method_6121(Entity entity) {
		this.playSoundAtEntity(SoundEvents.field_14552, 1.0F, 1.0F);
		if (!this.method_6510()) {
			this.field_6770 = true;
		}

		return super.method_6121(entity);
	}

	@Override
	public void update() {
		super.update();
		if (this.method_6509()) {
			if (this.world.isThundering()) {
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
				this.playSoundAtEntity(SoundEvents.field_14936, 1.0F, 1.0F);
			}

			this.method_6517(this.method_6521() - 1);
		}

		if (this.method_6545()) {
			this.method_6539(this.method_6532() + 1);
			if (this.method_6532() > 20) {
				this.method_6546(false);
				this.method_6548();
			} else if (this.method_6532() == 1) {
				this.playSoundAtEntity(SoundEvents.field_14997, 1.0F, 1.0F);
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
		return this.method_6509() && this.world.isThundering();
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
			if (!this.world.isRemote && this.method_6528() > 80 && this.random.nextInt(20) == 1) {
				if (this.method_6528() > 100 && this.method_16106(this.getEquippedStack(EquipmentSlot.HAND_MAIN))) {
					if (!this.world.isRemote) {
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
			this.playSoundAtEntity(
				SoundEvents.field_15106, 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F
			);

			for (int i = 0; i < 6; i++) {
				Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, ((double)this.random.nextFloat() - 0.5) * 0.1);
				vec3d = vec3d.rotateX(-this.pitch * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(-this.yaw * (float) (Math.PI / 180.0));
				double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
				Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.8, d, 1.0 + ((double)this.random.nextFloat() - 0.5) * 0.4);
				vec3d2 = vec3d2.rotateY(-this.field_6283 * (float) (Math.PI / 180.0));
				vec3d2 = vec3d2.add(this.x, this.y + (double)this.getEyeHeight() + 1.0, this.z);
				this.world
					.method_8406(
						new ItemStackParticle(ParticleTypes.field_11218, this.getEquippedStack(EquipmentSlot.HAND_MAIN)),
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
			if (!this.world.isRemote) {
				if (this.field_6767 == 1) {
					this.velocityY = 0.27F;
					float f = this.yaw * (float) (Math.PI / 180.0);
					float g = this.isChild() ? 0.1F : 0.2F;
					this.velocityX = this.velocityX - (double)(MathHelper.sin(f) * g);
					this.velocityZ = this.velocityZ + (double)(MathHelper.cos(f) * g);
					this.field_6776 = this.velocityX;
					this.field_6778 = this.velocityZ;
				} else if ((float)this.field_6767 != 7.0F && (float)this.field_6767 != 15.0F && (float)this.field_6767 != 23.0F) {
					this.velocityX = this.field_6776;
					this.velocityZ = this.field_6778;
				} else {
					if (this.onGround) {
						this.velocityY = 0.27F;
					}

					this.velocityX = 0.0;
					this.velocityZ = 0.0;
				}
			}
		}
	}

	private void method_6548() {
		this.world
			.method_8406(
				ParticleTypes.field_11234,
				this.x - (double)(this.width + 1.0F) * 0.5 * (double)MathHelper.sin(this.field_6283 * (float) (Math.PI / 180.0)),
				this.y + (double)this.getEyeHeight() - 0.1F,
				this.z + (double)(this.width + 1.0F) * 0.5 * (double)MathHelper.cos(this.field_6283 * (float) (Math.PI / 180.0)),
				this.velocityX,
				0.0,
				this.velocityZ
			);
		this.playSoundAtEntity(SoundEvents.field_15076, 1.0F, 1.0F);

		for (PandaEntity pandaEntity : this.world.getVisibleEntities(PandaEntity.class, this.getBoundingBox().expand(10.0))) {
			if (!pandaEntity.isChild() && pandaEntity.onGround && !pandaEntity.isInsideWater() && !pandaEntity.method_6514()) {
				pandaEntity.method_6043();
			}
		}

		if (this.random.nextInt(700) == 0 && this.world.getGameRules().getBoolean("doMobLoot")) {
			this.dropItem(Items.field_8777);
		}
	}

	@Override
	protected void method_5949(ItemEntity itemEntity) {
		if (this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty() && field_6765.test(itemEntity)) {
			ItemStack itemStack = itemEntity.getStack();
			this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack);
			this.handDropChances[EquipmentSlot.HAND_MAIN.getEntitySlotId()] = 2.0F;
			this.method_6103(itemEntity, itemStack.getAmount());
			itemEntity.invalidate();
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		this.method_6513(false);
		return super.damage(damageSource, f);
	}

	@Override
	public void setAttacker(@Nullable LivingEntity livingEntity) {
		super.setAttacker(livingEntity);
		if (livingEntity instanceof PlayerEntity) {
			List<VillagerEntity> list = this.world.getVisibleEntities(VillagerEntity.class, this.getBoundingBox().expand(10.0));
			boolean bl = false;

			for (VillagerEntity villagerEntity : list) {
				if (villagerEntity.isValid()) {
					this.world.method_8421(villagerEntity, (byte)13);
					if (!bl) {
						VillageProperties villageProperties = villagerEntity.method_7232();
						if (villageProperties != null) {
							bl = true;
							villageProperties.addAttacker(livingEntity);
							int i = -1;
							if (this.isChild()) {
								i = -3;
							}

							villageProperties.method_6393(((PlayerEntity)livingEntity).getGameProfile().getName(), i);
						}
					}
				}
			}
		}
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, class_3730 arg, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.method_6529(this.method_6543());
		this.method_6547(this.method_6543());
		this.method_6538();
		return super.method_5943(iWorld, localDifficulty, arg, entityData, compoundTag);
	}

	public void method_6515(PandaEntity pandaEntity, @Nullable PandaEntity pandaEntity2) {
		if (pandaEntity2 == null) {
			if (this.random.nextBoolean()) {
				this.method_6529(pandaEntity.method_6519());
				this.method_6547(this.method_6543());
			} else {
				this.method_6529(this.method_6543());
				this.method_6547(pandaEntity.method_6519());
			}
		} else if (this.random.nextBoolean()) {
			this.method_6529(pandaEntity.method_6519());
			this.method_6547(pandaEntity2.method_6519());
		} else {
			this.method_6529(pandaEntity2.method_6519());
			this.method_6547(pandaEntity.method_6519());
		}

		if (this.random.nextInt(32) == 0) {
			this.method_6529(this.method_6543());
		}

		if (this.random.nextInt(32) == 0) {
			this.method_6547(this.method_6543());
		}
	}

	private PandaEntity.class_1443 method_6519() {
		return this.random.nextBoolean() ? this.method_6525() : this.method_6508();
	}

	public void method_6538() {
		if (this.method_6550()) {
			this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		}

		if (this.method_6549()) {
			this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.07F);
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
		} else if (this.method_6481(itemStack)) {
			if (this.getTarget() != null) {
				this.field_6769 = true;
			}

			if (this.isChild()) {
				this.method_6475(playerEntity, itemStack);
				this.method_5620((int)((float)(-this.getBreedingAge() / 20) * 0.1F), true);
			} else if (!this.world.isRemote && this.getBreedingAge() == 0 && this.method_6482()) {
				this.method_6475(playerEntity, itemStack);
				this.method_6480(playerEntity);
			} else {
				if (this.world.isRemote || this.method_6535()) {
					return false;
				}

				this.method_6513(true);
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
		if (this.method_6510()) {
			return SoundEvents.field_14801;
		} else {
			return this.method_6509() ? SoundEvents.field_14715 : SoundEvents.field_14604;
		}
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSoundAtEntity(SoundEvents.field_15035, 0.15F, 1.0F);
	}

	@Override
	public boolean method_6481(ItemStack itemStack) {
		return itemStack.getItem() == Blocks.field_10211.getItem();
	}

	private boolean method_16106(ItemStack itemStack) {
		return this.method_6481(itemStack) || itemStack.getItem() == Blocks.field_10183.getItem();
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

	static class PandaFleeGoal<T extends Entity> extends FleeEntityGoal<T> {
		private final PandaEntity field_6782;

		public PandaFleeGoal(PandaEntity pandaEntity, Class<T> class_, float f, double d, double e) {
			super(pandaEntity, class_, f, d, e, EntityPredicates.EXCEPT_SPECTATOR);
			this.field_6782 = pandaEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_6782.method_6509() && super.canStart();
		}
	}

	static class PandaMateGoal extends AnimalMateGoal {
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
						PlayerEntity playerEntity = this.world.getClosestPlayer(this.field_6784, 8.0);
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
							mutable.set(blockPos).method_10100(k, i, l);
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
			if (!this.panda.method_6535() && !this.panda.method_6514() && !this.panda.method_6524()) {
				super.tick();
			}
		}
	}

	public static enum class_1443 {
		field_6788(0, "normal", false, "textures/entity/panda/panda.png"),
		field_6794(1, "lazy", false, "textures/entity/panda/lazy_panda.png"),
		field_6795(2, "worried", false, "textures/entity/panda/worried_panda.png"),
		field_6791(3, "playful", false, "textures/entity/panda/playful_panda.png"),
		field_6792(4, "brown", true, "textures/entity/panda/brown_panda.png"),
		field_6793(5, "weak", true, "textures/entity/panda/weak_panda.png"),
		field_6789(6, "aggressive", false, "textures/entity/panda/aggressive_panda.png");

		private static final PandaEntity.class_1443[] field_6786 = (PandaEntity.class_1443[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(PandaEntity.class_1443::method_6564))
			.toArray(PandaEntity.class_1443[]::new);
		private final int field_6785;
		private final String field_6797;
		private final boolean field_6790;
		private final Identifier field_6787;

		private class_1443(int j, String string2, boolean bl, String string3) {
			this.field_6785 = j;
			this.field_6797 = string2;
			this.field_6790 = bl;
			this.field_6787 = new Identifier(string3);
		}

		public int method_6564() {
			return this.field_6785;
		}

		public String method_6565() {
			return this.field_6797;
		}

		public boolean method_6568() {
			return this.field_6790;
		}

		private static PandaEntity.class_1443 method_6569(PandaEntity.class_1443 arg, PandaEntity.class_1443 arg2) {
			if (arg.method_6568()) {
				return arg == arg2 ? arg : field_6788;
			} else {
				return arg;
			}
		}

		@Environment(EnvType.CLIENT)
		public Identifier method_6562() {
			return this.field_6787;
		}

		public static PandaEntity.class_1443 method_6566(int i) {
			if (i < 0 || i >= field_6786.length) {
				i = 0;
			}

			return field_6786[i];
		}

		public static PandaEntity.class_1443 method_6567(String string) {
			for (PandaEntity.class_1443 lv : values()) {
				if (lv.field_6797.equals(string)) {
					return lv;
				}
			}

			return field_6788;
		}
	}

	static class class_1444 extends class_1399 {
		final PandaEntity field_6798;

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
		protected void method_6319(MobEntityWithAi mobEntityWithAi, LivingEntity livingEntity) {
			if (mobEntityWithAi instanceof PandaEntity && ((PandaEntity)mobEntityWithAi).method_6510()) {
				mobEntityWithAi.setTarget(livingEntity);
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
			return this.field_6799 < this.field_6800.age
				&& this.field_6800.method_6549()
				&& !this.field_6800.method_6535()
				&& !this.field_6800.method_6526()
				&& this.field_6800.random.nextInt(400) == 1;
		}

		@Override
		public boolean shouldContinue() {
			return !this.field_6800.isInsideWater() && (this.field_6800.method_6549() || this.field_6800.random.nextInt(600) != 1)
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

	static class class_1447 extends class_1374 {
		private final PandaEntity field_6802;

		public class_1447(PandaEntity pandaEntity, double d) {
			super(pandaEntity, d);
			this.field_6802 = pandaEntity;
		}

		@Override
		public boolean canStart() {
			if (!this.field_6802.isOnFire()) {
				return false;
			} else {
				BlockPos blockPos = this.method_6300(this.field_6549.world, this.field_6549, 5, 4);
				if (blockPos != null) {
					this.field_6547 = (double)blockPos.getX();
					this.field_6546 = (double)blockPos.getY();
					this.field_6550 = (double)blockPos.getZ();
					return true;
				} else {
					return this.method_6301();
				}
			}
		}

		@Override
		public boolean shouldContinue() {
			if (this.field_6802.method_6535()) {
				this.field_6802.getNavigation().method_6340();
				return false;
			} else {
				return super.shouldContinue();
			}
		}
	}

	static class class_1448 extends Goal {
		private final PandaEntity field_6803;

		public class_1448(PandaEntity pandaEntity) {
			this.field_6803 = pandaEntity;
			this.setControlBits(7);
		}

		@Override
		public boolean canStart() {
			if ((this.field_6803.isChild() || this.field_6803.method_6522())
				&& this.field_6803.onGround
				&& !this.field_6803.method_6514()
				&& !this.field_6803.method_6545()) {
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
					return this.field_6803.method_6522() && this.field_6803.random.nextInt(60) == 1 ? true : this.field_6803.random.nextInt(500) == 1;
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
			this.setControlBits(1);
		}

		@Override
		public boolean canStart() {
			if (this.field_6804 <= PandaEntity.this.age
				&& !PandaEntity.this.isChild()
				&& !PandaEntity.this.method_6535()
				&& !PandaEntity.this.isInsideWater()
				&& !PandaEntity.this.method_6514()
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
			return !PandaEntity.this.isInsideWater() && (PandaEntity.this.method_6549() || PandaEntity.this.random.nextInt(600) != 1)
				? PandaEntity.this.random.nextInt(2000) != 1
				: false;
		}

		@Override
		public void tick() {
			if (!PandaEntity.this.method_6535() && !PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
				PandaEntity.this.method_6513(true);
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = PandaEntity.this.world
				.getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PandaEntity.field_6765);
			if (!list.isEmpty() && PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
				PandaEntity.this.getNavigation().method_6335((Entity)list.get(0), 1.2F);
			} else if (!PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
				PandaEntity.this.method_6513(true);
			}

			this.field_6804 = 0;
		}

		@Override
		public void onRemove() {
			ItemStack itemStack = PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN);
			if (!itemStack.isEmpty()) {
				PandaEntity.this.dropStack(itemStack);
				PandaEntity.this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
				int i = PandaEntity.this.method_6549() ? PandaEntity.this.random.nextInt(50) + 10 : PandaEntity.this.random.nextInt(150) + 10;
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
			if (this.field_6806.isChild() && !this.field_6806.method_6526()) {
				return this.field_6806.method_6550() && this.field_6806.random.nextInt(500) == 1 ? true : this.field_6806.random.nextInt(6000) == 1;
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
}
