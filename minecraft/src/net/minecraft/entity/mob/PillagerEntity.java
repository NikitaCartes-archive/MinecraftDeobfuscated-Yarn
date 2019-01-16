package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1361;
import net.minecraft.class_1379;
import net.minecraft.class_1399;
import net.minecraft.class_3745;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.block.BannerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class PillagerEntity extends IllagerEntity implements class_3745, RangedAttacker {
	private static final TrackedData<Boolean> field_7334 = DataTracker.registerData(PillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final BasicInventory inventory = new BasicInventory(5);

	public PillagerEntity(World world) {
		super(EntityType.PILLAGER, world);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(3, new CrossbowAttackGoal<>(this, 1.0, 8.0F));
		this.goalSelector.add(8, new class_1379(this, 0.6));
		this.goalSelector.add(9, new class_1361(this, PlayerEntity.class, 15.0F, 1.0F));
		this.goalSelector.add(10, new class_1361(this, MobEntity.class, 15.0F));
		this.targetSelector.add(1, new class_1399(this, IllagerEntity.class).method_6318());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, VillagerEntity.class, false));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.35F);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(20.0);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(32.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_7334, false);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7108() {
		return this.dataTracker.get(field_7334);
	}

	@Override
	public void method_7110(boolean bl) {
		this.dataTracker.set(field_7334, bl);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7109() {
		return this.method_6991(1);
	}

	@Override
	public void setArmsRaised(boolean bl) {
		this.method_6992(1, bl);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.inventory.getInvSize(); i++) {
			ItemStack itemStack = this.inventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				listTag.add(itemStack.toTag(new CompoundTag()));
			}
		}

		compoundTag.put("Inventory", listTag);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public IllagerEntity.State method_6990() {
		if (this.method_7109()) {
			return IllagerEntity.State.field_7211;
		} else if (this.method_7108()) {
			return IllagerEntity.State.field_7210;
		} else {
			return !this.getMainHandStack().isEmpty() && this.getMainHandStack().getItem() == Items.field_8399
				? IllagerEntity.State.field_7213
				: IllagerEntity.State.field_7207;
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		ListTag listTag = compoundTag.getList("Inventory", 10);

		for (int i = 0; i < listTag.size(); i++) {
			ItemStack itemStack = ItemStack.fromTag(listTag.getCompoundTag(i));
			if (!itemStack.isEmpty()) {
				this.inventory.add(itemStack);
			}
		}

		this.setCanPickUpLoot(true);
	}

	@Override
	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		Block block = viewableWorld.getBlockState(blockPos.down()).getBlock();
		return block != Blocks.field_10219 && block != Blocks.field_10102 ? viewableWorld.method_8610(blockPos) - 0.5F : 10.0F;
	}

	@Override
	protected boolean checkLightLevelForSpawn() {
		return true;
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return iWorld.getLightLevel(LightType.BLOCK_LIGHT, new BlockPos(this.x, this.y, this.z)) > 8 ? false : super.canSpawn(iWorld, spawnType);
	}

	@Override
	public int getLimitPerChunk() {
		return 1;
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.initEquipment(localDifficulty);
		this.method_5984(localDifficulty);
		return super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		ItemStack itemStack = new ItemStack(Items.field_8399);
		if (this.random.nextInt(300) == 0) {
			Map<Enchantment, Integer> map = Maps.<Enchantment, Integer>newHashMap();
			map.put(Enchantments.field_9132, 1);
			EnchantmentHelper.set(map, itemStack);
		}

		this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack);
	}

	@Override
	public boolean isTeammate(Entity entity) {
		if (super.isTeammate(entity)) {
			return true;
		} else {
			return entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() == EntityGroup.ILLAGER
				? this.getScoreboardTeam() == null && entity.getScoreboardTeam() == null
				: false;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14976;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15049;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15159;
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		ProjectileEntity projectileEntity = this.method_7107(f);
		double d = livingEntity.x - this.x;
		double e = livingEntity.getBoundingBox().minY + (double)(livingEntity.getHeight() / 3.0F) - projectileEntity.y;
		double g = livingEntity.z - this.z;
		double h = (double)MathHelper.sqrt(d * d + g * g);
		projectileEntity.setVelocity(d, e + h * 0.2F, g, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
		this.playSound(SoundEvents.field_15187, 1.0F, 1.0F / (this.getRand().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(projectileEntity);
		this.despawnCounter = 0;
		CrossbowItem.setCharged(this.getMainHandStack(), false);
	}

	protected ProjectileEntity method_7107(float f) {
		ArrowEntity arrowEntity = new ArrowEntity(this.world, this);
		arrowEntity.setShotFromCrossbow(true);
		arrowEntity.setSound(SoundEvents.field_14636);
		arrowEntity.method_7435(this, f);
		return arrowEntity;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasArmsRaised() {
		return false;
	}

	public BasicInventory method_16473() {
		return this.inventory;
	}

	@Override
	protected void pickupItem(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		if (itemStack.getItem() instanceof BannerItem) {
			super.pickupItem(itemEntity);
		} else {
			Item item = itemStack.getItem();
			if (this.method_7111(item)) {
				ItemStack itemStack2 = this.inventory.add(itemStack);
				if (itemStack2.isEmpty()) {
					itemEntity.invalidate();
				} else {
					itemStack.setAmount(itemStack2.getAmount());
				}
			}
		}
	}

	private boolean method_7111(Item item) {
		return this.hasActiveRaid() ? item == Items.field_8539 : false;
	}

	@Override
	public boolean method_5758(int i, ItemStack itemStack) {
		if (super.method_5758(i, itemStack)) {
			return true;
		} else {
			int j = i - 300;
			if (j >= 0 && j < this.inventory.getInvSize()) {
				this.inventory.setInvStack(j, itemStack);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void addBonusForWave(int i, boolean bl) {
		if (i > 3) {
			int j = 5;
			if (this.random.nextInt(Math.max(6 - i, 1)) == 0) {
				ItemStack itemStack = new ItemStack(Items.field_8399);
				Map<Enchantment, Integer> map = Maps.<Enchantment, Integer>newHashMap();
				if (i > 6) {
					map.put(Enchantments.field_9098, 2);
				}

				map.put(Enchantments.field_9108, 1);
				EnchantmentHelper.set(map, itemStack);
				this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack);
			}
		}
	}

	@Override
	public boolean cannotDespawn() {
		return super.cannotDespawn() && this.method_16473().isInvEmpty();
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return super.canImmediatelyDespawn(d) && this.method_16473().isInvEmpty();
	}
}
