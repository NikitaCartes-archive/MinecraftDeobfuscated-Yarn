package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1371;
import net.minecraft.class_1386;
import net.minecraft.class_1394;
import net.minecraft.class_1404;
import net.minecraft.class_3697;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;

public class CatEntity extends TameableEntity {
	private static final Ingredient field_6809 = Ingredient.method_8091(Items.field_8429, Items.field_8209);
	private static final TrackedData<Integer> field_6811 = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_16284 = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_16292 = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_16285 = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final Map<Integer, Identifier> field_16283 = SystemUtil.consume(Maps.<Integer, Identifier>newHashMap(), hashMap -> {
		hashMap.put(0, new Identifier("textures/entity/cat/tabby.png"));
		hashMap.put(1, new Identifier("textures/entity/cat/black.png"));
		hashMap.put(2, new Identifier("textures/entity/cat/red.png"));
		hashMap.put(3, new Identifier("textures/entity/cat/siamese.png"));
		hashMap.put(4, new Identifier("textures/entity/cat/british_shorthair.png"));
		hashMap.put(5, new Identifier("textures/entity/cat/calico.png"));
		hashMap.put(6, new Identifier("textures/entity/cat/persian.png"));
		hashMap.put(7, new Identifier("textures/entity/cat/ragdoll.png"));
		hashMap.put(8, new Identifier("textures/entity/cat/white.png"));
		hashMap.put(9, new Identifier("textures/entity/cat/jellie.png"));
		hashMap.put(10, new Identifier("textures/entity/cat/all_black.png"));
	});
	private CatEntity.CatFleeGoal<PlayerEntity> fleeGoal;
	private TemptGoal field_6810;
	private float field_16290;
	private float field_16291;
	private float field_16288;
	private float field_16289;
	private float field_16286;
	private float field_16287;

	public CatEntity(EntityType<? extends CatEntity> entityType, World world) {
		super(entityType, world);
	}

	public Identifier method_16092() {
		return (Identifier)field_16283.get(this.getOcelotType());
	}

	@Override
	protected void initGoals() {
		this.field_6321 = new class_1386(this);
		this.field_6810 = new CatEntity.CatTemptGoal(this, 0.6, field_6809, true);
		this.field_6201.add(1, new SwimGoal(this));
		this.field_6201.add(1, new CatEntity.SleepWithOwnerGoal(this));
		this.field_6201.add(2, this.field_6321);
		this.field_6201.add(3, this.field_6810);
		this.field_6201.add(5, new class_3697(this, 1.1, 8));
		this.field_6201.add(6, new FollowOwnerGoal(this, 1.0, 10.0F, 5.0F));
		this.field_6201.add(7, new CatSitOnBlockGoal(this, 0.8));
		this.field_6201.add(8, new PounceAtTargetGoal(this, 0.3F));
		this.field_6201.add(9, new class_1371(this));
		this.field_6201.add(10, new AnimalMateGoal(this, 0.8));
		this.field_6201.add(11, new class_1394(this, 0.8, 1.0000001E-5F));
		this.field_6201.add(12, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
		this.field_6185.add(1, new class_1404(this, RabbitEntity.class, false, null));
		this.field_6185.add(1, new class_1404(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	public int getOcelotType() {
		return this.field_6011.get(field_6811);
	}

	public void getOcelotType(int i) {
		this.field_6011.set(field_6811, i);
	}

	public void method_16088(boolean bl) {
		this.field_6011.set(field_16284, bl);
	}

	public boolean method_16086() {
		return this.field_6011.get(field_16284);
	}

	public void method_16087(boolean bl) {
		this.field_6011.set(field_16292, bl);
	}

	public boolean method_16093() {
		return this.field_6011.get(field_16292);
	}

	public DyeColor method_16096() {
		return DyeColor.byId(this.field_6011.get(field_16285));
	}

	public void method_16094(DyeColor dyeColor) {
		this.field_6011.set(field_16285, dyeColor.getId());
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6811, 1);
		this.field_6011.startTracking(field_16284, false);
		this.field_6011.startTracking(field_16292, false);
		this.field_6011.startTracking(field_16285, DyeColor.field_7964.getId());
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("CatType", this.getOcelotType());
		compoundTag.putByte("CollarColor", (byte)this.method_16096().getId());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.getOcelotType(compoundTag.getInt("CatType"));
		if (compoundTag.containsKey("CollarColor", 99)) {
			this.method_16094(DyeColor.byId(compoundTag.getInt("CollarColor")));
		}
	}

	@Override
	public void mobTick() {
		if (this.method_5962().isMoving()) {
			double d = this.method_5962().getSpeed();
			if (d == 0.6) {
				this.setSneaking(true);
				this.setSprinting(false);
			} else if (d == 1.33) {
				this.setSneaking(false);
				this.setSprinting(true);
			} else {
				this.setSneaking(false);
				this.setSprinting(false);
			}
		} else {
			this.setSneaking(false);
			this.setSprinting(false);
		}
	}

	@Nullable
	@Override
	protected SoundEvent method_5994() {
		if (this.isTamed()) {
			if (this.isInLove()) {
				return SoundEvents.field_14741;
			} else {
				return this.random.nextInt(4) == 0 ? SoundEvents.field_14589 : SoundEvents.field_15051;
			}
		} else {
			return SoundEvents.field_16440;
		}
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	public void method_16089() {
		this.method_5783(SoundEvents.field_14938, this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14867;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14971;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	protected void method_6475(PlayerEntity playerEntity, ItemStack itemStack) {
		if (this.method_6481(itemStack)) {
			this.method_5783(SoundEvents.field_16439, 1.0F, 1.0F);
		}

		super.method_6475(playerEntity, itemStack);
	}

	@Override
	public boolean attack(Entity entity) {
		return entity.damage(DamageSource.method_5511(this), 3.0F);
	}

	@Override
	public void update() {
		super.update();
		if (this.field_6810 != null && this.field_6810.method_6313() && !this.isTamed() && this.age % 100 == 0) {
			this.method_5783(SoundEvents.field_16438, 1.0F, 1.0F);
		}

		this.method_16085();
	}

	private void method_16085() {
		if ((this.method_16086() || this.method_16093()) && this.age % 5 == 0) {
			this.method_5783(SoundEvents.field_14741, 0.6F + 0.4F * (this.random.nextFloat() - this.random.nextFloat()), 1.0F);
		}

		this.method_16090();
		this.method_16084();
	}

	private void method_16090() {
		this.field_16291 = this.field_16290;
		this.field_16289 = this.field_16288;
		if (this.method_16086()) {
			this.field_16290 = Math.min(1.0F, this.field_16290 + 0.15F);
			this.field_16288 = Math.min(1.0F, this.field_16288 + 0.08F);
		} else {
			this.field_16290 = Math.max(0.0F, this.field_16290 - 0.22F);
			this.field_16288 = Math.max(0.0F, this.field_16288 - 0.13F);
		}
	}

	private void method_16084() {
		this.field_16287 = this.field_16286;
		if (this.method_16093()) {
			this.field_16286 = Math.min(1.0F, this.field_16286 + 0.1F);
		} else {
			this.field_16286 = Math.max(0.0F, this.field_16286 - 0.13F);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_16082(float f) {
		return MathHelper.lerp(f, this.field_16291, this.field_16290);
	}

	@Environment(EnvType.CLIENT)
	public float method_16091(float f) {
		return MathHelper.lerp(f, this.field_16289, this.field_16288);
	}

	@Environment(EnvType.CLIENT)
	public float method_16095(float f) {
		return MathHelper.lerp(f, this.field_16287, this.field_16286);
	}

	public CatEntity method_6573(PassiveEntity passiveEntity) {
		CatEntity catEntity = EntityType.CAT.method_5883(this.field_6002);
		if (passiveEntity instanceof CatEntity) {
			if (this.random.nextBoolean()) {
				catEntity.getOcelotType(this.getOcelotType());
			} else {
				catEntity.getOcelotType(((CatEntity)passiveEntity).getOcelotType());
			}

			if (this.isTamed()) {
				catEntity.setOwnerUuid(this.method_6139());
				catEntity.setTamed(true);
				if (this.random.nextBoolean()) {
					catEntity.method_16094(this.method_16096());
				} else {
					catEntity.method_16094(((CatEntity)passiveEntity).method_16096());
				}
			}
		}

		return catEntity;
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		if (!this.isTamed()) {
			return false;
		} else if (!(animalEntity instanceof CatEntity)) {
			return false;
		} else {
			CatEntity catEntity = (CatEntity)animalEntity;
			return catEntity.isTamed() && super.canBreedWith(animalEntity);
		}
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (iWorld.method_8391() > 0.9F) {
			this.getOcelotType(this.random.nextInt(11));
		} else {
			this.getOcelotType(this.random.nextInt(10));
		}

		if (Feature.field_13520.method_14024(iWorld, new BlockPos(this))) {
			this.getOcelotType(10);
			this.setPersistent();
		}

		return entityData;
	}

	@Override
	public boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		Item item = itemStack.getItem();
		if (this.isTamed()) {
			if (this.isOwner(playerEntity)) {
				if (item instanceof DyeItem) {
					DyeColor dyeColor = ((DyeItem)item).getColor();
					if (dyeColor != this.method_16096()) {
						this.method_16094(dyeColor);
						if (!playerEntity.abilities.creativeMode) {
							itemStack.subtractAmount(1);
						}

						this.setPersistent();
						return true;
					}
				} else if (this.method_6481(itemStack)) {
					if (this.getHealth() < this.getHealthMaximum() && item.method_19263()) {
						this.method_6475(playerEntity, itemStack);
						this.heal((float)item.method_19264().method_19230());
						return true;
					}
				} else if (!this.field_6002.isClient) {
					this.field_6321.method_6311(!this.isSitting());
				}
			}
		} else if (this.method_6481(itemStack)) {
			this.method_6475(playerEntity, itemStack);
			if (!this.field_6002.isClient) {
				if (this.random.nextInt(3) == 0) {
					this.method_6170(playerEntity);
					this.method_6180(true);
					this.field_6321.method_6311(true);
					this.field_6002.summonParticle(this, (byte)7);
				} else {
					this.method_6180(false);
					this.field_6002.summonParticle(this, (byte)6);
				}
			}

			this.setPersistent();
			return true;
		}

		boolean bl = super.method_5992(playerEntity, hand);
		if (bl) {
			this.setPersistent();
		}

		return bl;
	}

	@Override
	public boolean method_6481(ItemStack itemStack) {
		return field_6809.method_8093(itemStack);
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return entitySize.height * 0.5F;
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return !this.isTamed() && this.age > 2400;
	}

	@Override
	protected void onTamedChanged() {
		if (this.fleeGoal == null) {
			this.fleeGoal = new CatEntity.CatFleeGoal<>(this, PlayerEntity.class, 16.0F, 0.8, 1.33);
		}

		this.field_6201.remove(this.fleeGoal);
		if (!this.isTamed()) {
			this.field_6201.add(4, this.fleeGoal);
		}
	}

	static class CatFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final CatEntity entity;

		public CatFleeGoal(CatEntity catEntity, Class<T> class_, float f, double d, double e) {
			super(catEntity, class_, f, d, e, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
			this.entity = catEntity;
		}

		@Override
		public boolean canStart() {
			return !this.entity.isTamed() && super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return !this.entity.isTamed() && super.shouldContinue();
		}
	}

	static class CatTemptGoal extends TemptGoal {
		@Nullable
		private PlayerEntity field_16298;
		private CatEntity field_17948;

		public CatTemptGoal(CatEntity catEntity, double d, Ingredient ingredient, boolean bl) {
			super(catEntity, d, ingredient, bl);
			this.field_17948 = catEntity;
		}

		@Override
		public void tick() {
			super.tick();
			if (this.field_16298 == null && this.field_6616.getRand().nextInt(600) == 0) {
				this.field_16298 = this.field_6617;
			} else if (this.field_6616.getRand().nextInt(500) == 0) {
				this.field_16298 = null;
			}
		}

		@Override
		protected boolean method_16081() {
			return this.field_16298 != null && this.field_16298.equals(this.field_6617) ? false : super.method_16081();
		}

		@Override
		public boolean canStart() {
			return super.canStart() && !this.field_17948.isTamed();
		}
	}

	static class SleepWithOwnerGoal extends Goal {
		private final CatEntity entity;
		private PlayerEntity field_16295;
		private BlockPos field_16294;
		private int field_16296;

		public SleepWithOwnerGoal(CatEntity catEntity) {
			this.entity = catEntity;
		}

		@Override
		public boolean canStart() {
			if (!this.entity.isTamed()) {
				return false;
			} else if (this.entity.isSitting()) {
				return false;
			} else {
				LivingEntity livingEntity = this.entity.getOwner();
				if (livingEntity instanceof PlayerEntity) {
					this.field_16295 = (PlayerEntity)livingEntity;
					if (!livingEntity.isSleeping()) {
						return false;
					}

					if (this.entity.squaredDistanceTo(this.field_16295) > 100.0) {
						return false;
					}

					BlockPos blockPos = new BlockPos(this.field_16295);
					BlockState blockState = this.entity.field_6002.method_8320(blockPos);
					if (blockState.getBlock().method_9525(BlockTags.field_16443)) {
						Direction direction = blockState.method_11654(BedBlock.field_11177);
						this.field_16294 = new BlockPos(blockPos.getX() - direction.getOffsetX(), blockPos.getY(), blockPos.getZ() - direction.getOffsetZ());
						return !this.method_16098();
					}
				}

				return false;
			}
		}

		private boolean method_16098() {
			for (CatEntity catEntity : this.entity.field_6002.method_18467(CatEntity.class, new BoundingBox(this.field_16294).expand(2.0))) {
				if (catEntity != this.entity && (catEntity.method_16086() || catEntity.method_16093())) {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean shouldContinue() {
			return this.entity.isTamed()
				&& !this.entity.isSitting()
				&& this.field_16295 != null
				&& this.field_16295.isSleeping()
				&& this.field_16294 != null
				&& !this.method_16098();
		}

		@Override
		public void start() {
			if (this.field_16294 != null) {
				this.entity.method_6176().method_6311(false);
				this.entity.method_5942().startMovingTo((double)this.field_16294.getX(), (double)this.field_16294.getY(), (double)this.field_16294.getZ(), 1.1F);
			}
		}

		@Override
		public void onRemove() {
			this.entity.method_16088(false);
			float f = this.entity.field_6002.getSkyAngle(1.0F);
			if (this.field_16295.getSleepTimer() >= 100 && (double)f > 0.77 && (double)f < 0.8 && (double)this.entity.field_6002.getRandom().nextFloat() < 0.7) {
				this.method_16097();
			}

			this.field_16296 = 0;
			this.entity.method_16087(false);
			this.entity.method_5942().stop();
		}

		private void method_16097() {
			Random random = this.entity.getRand();
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			mutable.set(this.entity);
			this.entity
				.method_6082(
					(double)(mutable.getX() + random.nextInt(11) - 5),
					(double)(mutable.getY() + random.nextInt(5) - 2),
					(double)(mutable.getZ() + random.nextInt(11) - 5),
					false
				);
			mutable.set(this.entity);
			LootSupplier lootSupplier = this.entity.field_6002.getServer().getLootManager().method_367(LootTables.field_16216);
			LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.entity.field_6002)
				.method_312(LootContextParameters.field_1232, mutable)
				.method_312(LootContextParameters.field_1226, this.entity)
				.setRandom(random);

			for (ItemStack itemStack : lootSupplier.getDrops(builder.method_309(LootContextTypes.GIFT))) {
				this.entity
					.field_6002
					.spawnEntity(
						new ItemEntity(
							this.entity.field_6002,
							(double)((float)mutable.getX() - MathHelper.sin(this.entity.field_6283 * (float) (Math.PI / 180.0))),
							(double)mutable.getY(),
							(double)((float)mutable.getZ() + MathHelper.cos(this.entity.field_6283 * (float) (Math.PI / 180.0))),
							itemStack
						)
					);
			}
		}

		@Override
		public void tick() {
			if (this.field_16295 != null && this.field_16294 != null) {
				this.entity.method_6176().method_6311(false);
				this.entity.method_5942().startMovingTo((double)this.field_16294.getX(), (double)this.field_16294.getY(), (double)this.field_16294.getZ(), 1.1F);
				if (this.entity.squaredDistanceTo(this.field_16295) < 2.5) {
					this.field_16296++;
					if (this.field_16296 > 16) {
						this.entity.method_16088(true);
						this.entity.method_16087(false);
					} else {
						this.entity.method_5951(this.field_16295, 45.0F, 45.0F);
						this.entity.method_16087(true);
					}
				} else {
					this.entity.method_16088(false);
				}
			}
		}
	}
}
