package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1351;
import net.minecraft.class_1386;
import net.minecraft.class_1395;
import net.minecraft.class_1407;
import net.minecraft.class_1432;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.ParrotMoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ParrotClimbOntoPlayerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ParrotEntity extends ParrotBaseEntity implements class_1432 {
	private static final TrackedData<Integer> field_6826 = DataTracker.registerData(ParrotEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Predicate<MobEntity> field_6821 = new Predicate<MobEntity>() {
		public boolean method_6590(@Nullable MobEntity mobEntity) {
			return mobEntity != null && ParrotEntity.field_6822.containsKey(mobEntity.method_5864());
		}
	};
	private static final Item field_6828 = Items.field_8423;
	private static final Set<Item> TAMING_INGREDIENTS = Sets.<Item>newHashSet(Items.field_8317, Items.field_8188, Items.field_8706, Items.field_8309);
	private static final Map<EntityType<?>, SoundEvent> field_6822 = SystemUtil.consume(Maps.<EntityType<?>, SoundEvent>newHashMap(), hashMap -> {
		hashMap.put(EntityType.BLAZE, SoundEvents.field_15199);
		hashMap.put(EntityType.CAVE_SPIDER, SoundEvents.field_15190);
		hashMap.put(EntityType.CREEPER, SoundEvents.field_14547);
		hashMap.put(EntityType.DROWNED, SoundEvents.field_14647);
		hashMap.put(EntityType.ELDER_GUARDIAN, SoundEvents.field_14777);
		hashMap.put(EntityType.ENDER_DRAGON, SoundEvents.field_14854);
		hashMap.put(EntityType.ENDERMAN, SoundEvents.field_14950);
		hashMap.put(EntityType.ENDERMITE, SoundEvents.field_15022);
		hashMap.put(EntityType.EVOKER, SoundEvents.field_15113);
		hashMap.put(EntityType.GHAST, SoundEvents.field_14577);
		hashMap.put(EntityType.GUARDIAN, SoundEvents.field_18813);
		hashMap.put(EntityType.HUSK, SoundEvents.field_15185);
		hashMap.put(EntityType.ILLUSIONER, SoundEvents.field_15064);
		hashMap.put(EntityType.MAGMA_CUBE, SoundEvents.field_14963);
		hashMap.put(EntityType.ZOMBIE_PIGMAN, SoundEvents.field_15143);
		hashMap.put(EntityType.PANDA, SoundEvents.field_18814);
		hashMap.put(EntityType.PHANTOM, SoundEvents.field_14957);
		hashMap.put(EntityType.PILLAGER, SoundEvents.field_18815);
		hashMap.put(EntityType.POLAR_BEAR, SoundEvents.field_14866);
		hashMap.put(EntityType.RAVAGER, SoundEvents.field_18816);
		hashMap.put(EntityType.SHULKER, SoundEvents.field_14768);
		hashMap.put(EntityType.SILVERFISH, SoundEvents.field_14683);
		hashMap.put(EntityType.SKELETON, SoundEvents.field_14587);
		hashMap.put(EntityType.SLIME, SoundEvents.field_15098);
		hashMap.put(EntityType.SPIDER, SoundEvents.field_15190);
		hashMap.put(EntityType.STRAY, SoundEvents.field_14885);
		hashMap.put(EntityType.VEX, SoundEvents.field_15032);
		hashMap.put(EntityType.VINDICATOR, SoundEvents.field_14790);
		hashMap.put(EntityType.WITCH, SoundEvents.field_14796);
		hashMap.put(EntityType.WITHER, SoundEvents.field_14555);
		hashMap.put(EntityType.WITHER_SKELETON, SoundEvents.field_15073);
		hashMap.put(EntityType.WOLF, SoundEvents.field_14942);
		hashMap.put(EntityType.ZOMBIE, SoundEvents.field_15220);
		hashMap.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.field_14676);
	});
	public float field_6818;
	public float field_6819;
	public float field_6827;
	public float field_6829;
	public float field_6824 = 1.0F;
	private boolean field_6823;
	private BlockPos field_6820;

	public ParrotEntity(EntityType<? extends ParrotEntity> entityType, World world) {
		super(entityType, world);
		this.field_6207 = new ParrotMoveControl(this);
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.setVariant(this.random.nextInt(5));
		return super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	protected void initGoals() {
		this.field_6321 = new class_1386(this);
		this.field_6201.add(0, new EscapeDangerGoal(this, 1.25));
		this.field_6201.add(0, new SwimGoal(this));
		this.field_6201.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.field_6201.add(2, this.field_6321);
		this.field_6201.add(2, new class_1351(this, 1.0, 5.0F, 1.0F));
		this.field_6201.add(2, new class_1395(this, 1.0));
		this.field_6201.add(3, new ParrotClimbOntoPlayerGoal(this));
		this.field_6201.add(3, new FollowMobGoal(this, 1.0, 3.0F, 7.0F));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_6127().register(EntityAttributes.FLYING_SPEED);
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(6.0);
		this.method_5996(EntityAttributes.FLYING_SPEED).setBaseValue(0.4F);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	protected EntityNavigation method_5965(World world) {
		class_1407 lv = new class_1407(this, world);
		lv.method_6332(false);
		lv.setCanSwim(true);
		lv.method_6331(true);
		return lv;
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return entitySize.height * 0.6F;
	}

	@Override
	public void updateMovement() {
		method_6587(this.field_6002, this);
		if (this.field_6820 == null
			|| this.field_6820.squaredDistanceTo(this.x, this.y, this.z) > 12.0
			|| this.field_6002.method_8320(this.field_6820).getBlock() != Blocks.field_10223) {
			this.field_6823 = false;
			this.field_6820 = null;
		}

		super.updateMovement();
		this.method_6578();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_6006(BlockPos blockPos, boolean bl) {
		this.field_6820 = blockPos;
		this.field_6823 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_6582() {
		return this.field_6823;
	}

	private void method_6578() {
		this.field_6829 = this.field_6818;
		this.field_6827 = this.field_6819;
		this.field_6819 = (float)((double)this.field_6819 + (double)(this.onGround ? -1 : 4) * 0.3);
		this.field_6819 = MathHelper.clamp(this.field_6819, 0.0F, 1.0F);
		if (!this.onGround && this.field_6824 < 1.0F) {
			this.field_6824 = 1.0F;
		}

		this.field_6824 = (float)((double)this.field_6824 * 0.9);
		Vec3d vec3d = this.method_18798();
		if (!this.onGround && vec3d.y < 0.0) {
			this.method_18799(vec3d.multiply(1.0, 0.6, 1.0));
		}

		this.field_6818 = this.field_6818 + this.field_6824 * 2.0F;
	}

	private static boolean method_6587(World world, Entity entity) {
		if (entity.isValid() && !entity.isSilent() && world.random.nextInt(50) == 0) {
			List<MobEntity> list = world.method_8390(MobEntity.class, entity.method_5829().expand(20.0), field_6821);
			if (!list.isEmpty()) {
				MobEntity mobEntity = (MobEntity)list.get(world.random.nextInt(list.size()));
				if (!mobEntity.isSilent()) {
					SoundEvent soundEvent = method_6586(mobEntity.method_5864());
					world.method_8465(null, entity.x, entity.y, entity.z, soundEvent, entity.method_5634(), 0.7F, method_6580(world.random));
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (!this.isTamed() && TAMING_INGREDIENTS.contains(itemStack.getItem())) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.subtractAmount(1);
			}

			if (!this.isSilent()) {
				this.field_6002
					.method_8465(
						null, this.x, this.y, this.z, SoundEvents.field_14960, this.method_5634(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
					);
			}

			if (!this.field_6002.isClient) {
				if (this.random.nextInt(10) == 0) {
					this.method_6170(playerEntity);
					this.method_6180(true);
					this.field_6002.summonParticle(this, (byte)7);
				} else {
					this.method_6180(false);
					this.field_6002.summonParticle(this, (byte)6);
				}
			}

			return true;
		} else if (itemStack.getItem() == field_6828) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.subtractAmount(1);
			}

			this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5899, 900));
			if (playerEntity.isCreative() || !this.isInvulnerable()) {
				this.damage(DamageSource.method_5532(playerEntity), Float.MAX_VALUE);
			}

			return true;
		} else {
			if (!this.field_6002.isClient && !this.method_6581() && this.isTamed() && this.isOwner(playerEntity)) {
				this.field_6321.method_6311(!this.isSitting());
			}

			return super.method_5992(playerEntity, hand);
		}
	}

	@Override
	public boolean method_6481(ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.method_5829().minY);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		Block block = iWorld.method_8320(blockPos.down()).getBlock();
		return block.method_9525(BlockTags.field_15503)
			|| block == Blocks.field_10479
			|| block instanceof LogBlock
			|| block == Blocks.field_10124 && super.method_5979(iWorld, spawnType);
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	protected void method_5623(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		return false;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return null;
	}

	public static void method_6589(World world, Entity entity) {
		if (!entity.isSilent() && !method_6587(world, entity) && world.random.nextInt(200) == 0) {
			world.method_8465(null, entity.x, entity.y, entity.z, method_6583(world.random), entity.method_5634(), 1.0F, method_6580(world.random));
		}
	}

	@Override
	public boolean attack(Entity entity) {
		return entity.damage(DamageSource.method_5511(this), 3.0F);
	}

	@Nullable
	@Override
	public SoundEvent method_5994() {
		return method_6583(this.random);
	}

	private static SoundEvent method_6583(Random random) {
		if (random.nextInt(1000) == 0) {
			List<EntityType<?>> list = Lists.<EntityType<?>>newArrayList(field_6822.keySet());
			return method_6586((EntityType<?>)list.get(random.nextInt(list.size())));
		} else {
			return SoundEvents.field_15132;
		}
	}

	public static SoundEvent method_6586(EntityType<?> entityType) {
		return (SoundEvent)field_6822.getOrDefault(entityType, SoundEvents.field_15132);
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15077;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15234;
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		this.method_5783(SoundEvents.field_14602, 0.15F, 1.0F);
	}

	@Override
	protected float method_5801(float f) {
		this.method_5783(SoundEvents.field_14925, 0.15F, 1.0F);
		return f + this.field_6819 / 2.0F;
	}

	@Override
	protected boolean method_5776() {
		return true;
	}

	@Override
	protected float getSoundPitch() {
		return method_6580(this.random);
	}

	private static float method_6580(Random random) {
		return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	public SoundCategory method_5634() {
		return SoundCategory.field_15254;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	protected void pushAway(Entity entity) {
		if (!(entity instanceof PlayerEntity)) {
			super.pushAway(entity);
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			if (this.field_6321 != null) {
				this.field_6321.method_6311(false);
			}

			return super.damage(damageSource, f);
		}
	}

	public int getVariant() {
		return MathHelper.clamp(this.field_6011.get(field_6826), 0, 4);
	}

	public void setVariant(int i) {
		this.field_6011.set(field_6826, i);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6826, 0);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("Variant", this.getVariant());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.setVariant(compoundTag.getInt("Variant"));
	}

	public boolean method_6581() {
		return !this.onGround;
	}
}
