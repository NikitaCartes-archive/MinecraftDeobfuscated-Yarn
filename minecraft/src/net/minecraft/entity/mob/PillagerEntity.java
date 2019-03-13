package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1399;
import net.minecraft.class_1675;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.Quaternion;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
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
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.sortme.Projectile;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class PillagerEntity extends IllagerEntity implements CrossbowUser, RangedAttacker {
	private static final TrackedData<Boolean> field_7334 = DataTracker.registerData(PillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final BasicInventory inventory = new BasicInventory(5);

	public PillagerEntity(EntityType<? extends PillagerEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.field_6201.add(0, new SwimGoal(this));
		this.field_6201.add(3, new CrossbowAttackGoal<>(this, 1.0, 8.0F));
		this.field_6201.add(8, new WanderAroundGoal(this, 0.6));
		this.field_6201.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 15.0F, 1.0F));
		this.field_6201.add(10, new LookAtEntityGoal(this, MobEntity.class, 15.0F));
		this.field_6185.add(1, new class_1399(this, IllagerEntity.class).method_6318());
		this.field_6185.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.field_6185.add(3, new FollowTargetGoal(this, AbstractTraderEntity.class, false));
		this.field_6185.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.35F);
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
		this.method_5996(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
		this.method_5996(EntityAttributes.FOLLOW_RANGE).setBaseValue(32.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7334, false);
	}

	@Environment(EnvType.CLIENT)
	public boolean isCharging() {
		return this.field_6011.get(field_7334);
	}

	@Override
	public void setCharging(boolean bl) {
		this.field_6011.set(field_7334, bl);
	}

	@Override
	public void setArmsRaised(boolean bl) {
		this.method_6992(1, bl);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.inventory.getInvSize(); i++) {
			ItemStack itemStack = this.inventory.method_5438(i);
			if (!itemStack.isEmpty()) {
				listTag.add(itemStack.method_7953(new CompoundTag()));
			}
		}

		compoundTag.method_10566("Inventory", listTag);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public IllagerEntity.State method_6990() {
		if (this.hasArmsRaised()) {
			return IllagerEntity.State.field_7211;
		} else if (this.isCharging()) {
			return IllagerEntity.State.field_7210;
		} else {
			return this.method_18809(Items.field_8399) ? IllagerEntity.State.field_7213 : IllagerEntity.State.field_7207;
		}
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		ListTag listTag = compoundTag.method_10554("Inventory", 10);

		for (int i = 0; i < listTag.size(); i++) {
			ItemStack itemStack = ItemStack.method_7915(listTag.getCompoundTag(i));
			if (!itemStack.isEmpty()) {
				this.inventory.method_5491(itemStack);
			}
		}

		this.setCanPickUpLoot(true);
	}

	@Override
	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		Block block = viewableWorld.method_8320(blockPos.down()).getBlock();
		return block != Blocks.field_10219 && block != Blocks.field_10102 ? 0.5F - viewableWorld.method_8610(blockPos) : 10.0F;
	}

	@Override
	protected boolean checkLightLevelForSpawn() {
		return true;
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		return iWorld.method_8314(LightType.BLOCK, new BlockPos(this.x, this.y, this.z)) > 8 ? false : super.method_5979(iWorld, spawnType);
	}

	@Override
	public int getLimitPerChunk() {
		return 1;
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.initEquipment(localDifficulty);
		this.method_5984(localDifficulty);
		return super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		ItemStack itemStack = new ItemStack(Items.field_8399);
		if (this.random.nextInt(300) == 0) {
			Map<Enchantment, Integer> map = Maps.<Enchantment, Integer>newHashMap();
			map.put(Enchantments.field_9132, 1);
			EnchantmentHelper.set(map, itemStack);
		}

		this.method_5673(EquipmentSlot.HAND_MAIN, itemStack);
	}

	@Override
	public boolean isTeammate(Entity entity) {
		if (super.isTeammate(entity)) {
			return true;
		} else {
			return entity instanceof LivingEntity && ((LivingEntity)entity).method_6046() == EntityGroup.ILLAGER
				? this.method_5781() == null && entity.method_5781() == null
				: false;
		}
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14976;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15049;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15159;
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		ItemStack itemStack = this.method_5998(class_1675.method_18812(this, Items.field_8399));
		if (this.method_18809(Items.field_8399)) {
			CrossbowItem.method_7777(this.field_6002, this, itemStack, 1.6F, (float)(14 - this.field_6002.getDifficulty().getId() * 4));
		}

		this.despawnCounter = 0;
	}

	@Override
	public void method_18811(LivingEntity livingEntity, ItemStack itemStack, Projectile projectile, float f) {
		Entity entity = (Entity)projectile;
		double d = livingEntity.x - this.x;
		double e = livingEntity.z - this.z;
		double g = (double)MathHelper.sqrt(d * d + e * e);
		double h = livingEntity.method_5829().minY + (double)(livingEntity.getHeight() / 3.0F) - entity.y + g * 0.2F;
		Vector3f vector3f = this.method_19168(new Vec3d(d, h, e), f);
		projectile.setVelocity((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), 1.6F, (float)(14 - this.field_6002.getDifficulty().getId() * 4));
		this.method_5783(SoundEvents.field_15187, 1.0F, 1.0F / (this.getRand().nextFloat() * 0.4F + 0.8F));
	}

	private Vector3f method_19168(Vec3d vec3d, float f) {
		Vec3d vec3d2 = vec3d.normalize();
		Vec3d vec3d3 = vec3d2.crossProduct(new Vec3d(0.0, 1.0, 0.0));
		if (vec3d3.lengthSquared() <= 1.0E-7) {
			vec3d3 = vec3d2.crossProduct(this.method_18864(1.0F));
		}

		Quaternion quaternion = new Quaternion(new Vector3f(vec3d3), 90.0F, true);
		Vector3f vector3f = new Vector3f(vec3d2);
		vector3f.method_19262(quaternion);
		Quaternion quaternion2 = new Quaternion(vector3f, f, true);
		Vector3f vector3f2 = new Vector3f(vec3d2);
		vector3f2.method_19262(quaternion2);
		return vector3f2;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasArmsRaised() {
		return this.method_6991(1);
	}

	public BasicInventory getInventory() {
		return this.inventory;
	}

	@Override
	protected void method_5949(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.method_6983();
		if (itemStack.getItem() instanceof BannerItem) {
			super.method_5949(itemEntity);
		} else {
			Item item = itemStack.getItem();
			if (this.method_7111(item)) {
				ItemStack itemStack2 = this.inventory.method_5491(itemStack);
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
				this.inventory.method_5447(j, itemStack);
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
				this.method_5673(EquipmentSlot.HAND_MAIN, itemStack);
			}
		}
	}

	@Override
	public boolean cannotDespawn() {
		return super.cannotDespawn() && this.getInventory().isInvEmpty();
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return super.canImmediatelyDespawn(d) && this.getInventory().isInvEmpty();
	}
}
