package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1371;
import net.minecraft.class_1394;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class OcelotEntity extends AnimalEntity {
	private static final Ingredient field_16299 = Ingredient.method_8091(Items.field_8429, Items.field_8209);
	private static final TrackedData<Boolean> field_16301 = DataTracker.registerData(OcelotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private OcelotEntity.OcelotFleeGoal<PlayerEntity> field_16300;
	private OcelotEntity.class_3703 field_16302;

	public OcelotEntity(EntityType<? extends OcelotEntity> entityType, World world) {
		super(entityType, world);
		this.method_16103();
	}

	private boolean isTrusting() {
		return this.field_6011.get(field_16301);
	}

	private void setTrusting(boolean bl) {
		this.field_6011.set(field_16301, bl);
		this.method_16103();
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putBoolean("Trusting", this.isTrusting());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.setTrusting(compoundTag.getBoolean("Trusting"));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_16301, false);
	}

	@Override
	protected void initGoals() {
		this.field_16302 = new OcelotEntity.class_3703(this, 0.6, field_16299, true);
		this.field_6201.add(1, new SwimGoal(this));
		this.field_6201.add(3, this.field_16302);
		this.field_6201.add(7, new PounceAtTargetGoal(this, 0.3F));
		this.field_6201.add(8, new class_1371(this));
		this.field_6201.add(9, new AnimalMateGoal(this, 0.8));
		this.field_6201.add(10, new class_1394(this, 0.8, 1.0000001E-5F));
		this.field_6201.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
		this.field_6185.add(1, new FollowTargetGoal(this, ChickenEntity.class, false));
		this.field_6185.add(1, new FollowTargetGoal(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
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

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return !this.isTrusting() && this.age > 2400;
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

	@Nullable
	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_16437;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 900;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_16441;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_16442;
	}

	@Override
	public boolean attack(Entity entity) {
		return entity.damage(DamageSource.method_5511(this), 3.0F);
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return this.isInvulnerableTo(damageSource) ? false : super.damage(damageSource, f);
	}

	@Override
	public boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if ((this.field_16302 == null || this.field_16302.method_6313())
			&& !this.isTrusting()
			&& this.method_6481(itemStack)
			&& playerEntity.squaredDistanceTo(this) < 9.0) {
			this.method_6475(playerEntity, itemStack);
			if (!this.field_6002.isClient) {
				if (this.random.nextInt(3) == 0) {
					this.setTrusting(true);
					this.method_16100(true);
					this.field_6002.summonParticle(this, (byte)41);
				} else {
					this.method_16100(false);
					this.field_6002.summonParticle(this, (byte)40);
				}
			}

			return true;
		} else {
			return super.method_5992(playerEntity, hand);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 41) {
			this.method_16100(true);
		} else if (b == 40) {
			this.method_16100(false);
		} else {
			super.method_5711(b);
		}
	}

	private void method_16100(boolean bl) {
		ParticleParameters particleParameters = ParticleTypes.field_11201;
		if (!bl) {
			particleParameters = ParticleTypes.field_11251;
		}

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.field_6002
				.method_8406(
					particleParameters,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					d,
					e,
					f
				);
		}
	}

	protected void method_16103() {
		if (this.field_16300 == null) {
			this.field_16300 = new OcelotEntity.OcelotFleeGoal<>(this, PlayerEntity.class, 16.0F, 0.8, 1.33);
		}

		this.field_6201.remove(this.field_16300);
		if (!this.isTrusting()) {
			this.field_6201.add(4, this.field_16300);
		}
	}

	public OcelotEntity method_16104(PassiveEntity passiveEntity) {
		return EntityType.OCELOT.method_5883(this.field_6002);
	}

	@Override
	public boolean method_6481(ItemStack itemStack) {
		return field_16299.method_8093(itemStack);
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		return this.random.nextInt(3) != 0;
	}

	@Override
	public boolean method_5957(ViewableWorld viewableWorld) {
		if (viewableWorld.method_8606(this) && !viewableWorld.method_8599(this.method_5829())) {
			BlockPos blockPos = new BlockPos(this.x, this.method_5829().minY, this.z);
			if (blockPos.getY() < viewableWorld.getSeaLevel()) {
				return false;
			}

			BlockState blockState = viewableWorld.method_8320(blockPos.down());
			Block block = blockState.getBlock();
			if (block == Blocks.field_10219 || blockState.method_11602(BlockTags.field_15503)) {
				return true;
			}
		}

		return false;
	}

	protected void method_16105() {
		for (int i = 0; i < 2; i++) {
			OcelotEntity ocelotEntity = EntityType.OCELOT.method_5883(this.field_6002);
			ocelotEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
			ocelotEntity.setBreedingAge(-24000);
			this.field_6002.spawnEntity(ocelotEntity);
		}
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (iWorld.getRandom().nextInt(7) == 0) {
			this.method_16105();
		}

		return entityData;
	}

	static class OcelotFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final OcelotEntity field_16303;

		public OcelotFleeGoal(OcelotEntity ocelotEntity, Class<T> class_, float f, double d, double e) {
			super(ocelotEntity, class_, f, d, e, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
			this.field_16303 = ocelotEntity;
		}

		@Override
		public boolean canStart() {
			return !this.field_16303.isTrusting() && super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return !this.field_16303.isTrusting() && super.shouldContinue();
		}
	}

	static class class_3703 extends TemptGoal {
		private final OcelotEntity field_16304;

		public class_3703(OcelotEntity ocelotEntity, double d, Ingredient ingredient, boolean bl) {
			super(ocelotEntity, d, ingredient, bl);
			this.field_16304 = ocelotEntity;
		}

		@Override
		protected boolean method_16081() {
			return super.method_16081() && !this.field_16304.isTrusting();
		}
	}
}
