package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class SheepEntity extends AnimalEntity implements Shearable {
	private static final TrackedData<Byte> COLOR = DataTracker.registerData(SheepEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final Map<DyeColor, ItemConvertible> DROPS = Util.make(Maps.newEnumMap(DyeColor.class), enumMap -> {
		enumMap.put(DyeColor.field_7952, Blocks.field_10446);
		enumMap.put(DyeColor.field_7946, Blocks.field_10095);
		enumMap.put(DyeColor.field_7958, Blocks.field_10215);
		enumMap.put(DyeColor.field_7951, Blocks.field_10294);
		enumMap.put(DyeColor.field_7947, Blocks.field_10490);
		enumMap.put(DyeColor.field_7961, Blocks.field_10028);
		enumMap.put(DyeColor.field_7954, Blocks.field_10459);
		enumMap.put(DyeColor.field_7944, Blocks.field_10423);
		enumMap.put(DyeColor.field_7967, Blocks.field_10222);
		enumMap.put(DyeColor.field_7955, Blocks.field_10619);
		enumMap.put(DyeColor.field_7945, Blocks.field_10259);
		enumMap.put(DyeColor.field_7966, Blocks.field_10514);
		enumMap.put(DyeColor.field_7957, Blocks.field_10113);
		enumMap.put(DyeColor.field_7942, Blocks.field_10170);
		enumMap.put(DyeColor.field_7964, Blocks.field_10314);
		enumMap.put(DyeColor.field_7963, Blocks.field_10146);
	});
	private static final Map<DyeColor, float[]> COLORS = Maps.newEnumMap(
		(Map)Arrays.stream(DyeColor.values()).collect(Collectors.toMap(dyeColor -> dyeColor, SheepEntity::getDyedColor))
	);
	private int eatGrassTimer;
	private EatGrassGoal eatGrassGoal;

	private static float[] getDyedColor(DyeColor color) {
		if (color == DyeColor.field_7952) {
			return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
		} else {
			float[] fs = color.getColorComponents();
			float f = 0.75F;
			return new float[]{fs[0] * 0.75F, fs[1] * 0.75F, fs[2] * 0.75F};
		}
	}

	@Environment(EnvType.CLIENT)
	public static float[] getRgbColor(DyeColor dyeColor) {
		return (float[])COLORS.get(dyeColor);
	}

	public SheepEntity(EntityType<? extends SheepEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.eatGrassGoal = new EatGrassGoal(this);
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(3, new TemptGoal(this, 1.1, Ingredient.ofItems(Items.field_8861), false));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(5, this.eatGrassGoal);
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
	}

	@Override
	protected void mobTick() {
		this.eatGrassTimer = this.eatGrassGoal.getTimer();
		super.mobTick();
	}

	@Override
	public void tickMovement() {
		if (this.world.isClient) {
			this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
		}

		super.tickMovement();
	}

	public static DefaultAttributeContainer.Builder createSheepAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.field_23716, 8.0).add(EntityAttributes.field_23719, 0.23F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(COLOR, (byte)0);
	}

	@Override
	public Identifier getLootTableId() {
		if (this.isSheared()) {
			return this.getType().getLootTableId();
		} else {
			switch (this.getColor()) {
				case field_7952:
				default:
					return LootTables.WHITE_SHEEP_ENTITY;
				case field_7946:
					return LootTables.ORANGE_SHEEP_ENTITY;
				case field_7958:
					return LootTables.MAGENTA_SHEEP_ENTITY;
				case field_7951:
					return LootTables.LIGHT_BLUE_SHEEP_ENTITY;
				case field_7947:
					return LootTables.YELLOW_SHEEP_ENTITY;
				case field_7961:
					return LootTables.LIME_SHEEP_ENTITY;
				case field_7954:
					return LootTables.PINK_SHEEP_ENTITY;
				case field_7944:
					return LootTables.GRAY_SHEEP_ENTITY;
				case field_7967:
					return LootTables.LIGHT_GRAY_SHEEP_ENTITY;
				case field_7955:
					return LootTables.CYAN_SHEEP_ENTITY;
				case field_7945:
					return LootTables.PURPLE_SHEEP_ENTITY;
				case field_7966:
					return LootTables.BLUE_SHEEP_ENTITY;
				case field_7957:
					return LootTables.BROWN_SHEEP_ENTITY;
				case field_7942:
					return LootTables.GREEN_SHEEP_ENTITY;
				case field_7964:
					return LootTables.RED_SHEEP_ENTITY;
				case field_7963:
					return LootTables.BLACK_SHEEP_ENTITY;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 10) {
			this.eatGrassTimer = 40;
		} else {
			super.handleStatus(status);
		}
	}

	@Environment(EnvType.CLIENT)
	public float getNeckAngle(float delta) {
		if (this.eatGrassTimer <= 0) {
			return 0.0F;
		} else if (this.eatGrassTimer >= 4 && this.eatGrassTimer <= 36) {
			return 1.0F;
		} else {
			return this.eatGrassTimer < 4 ? ((float)this.eatGrassTimer - delta) / 4.0F : -((float)(this.eatGrassTimer - 40) - delta) / 4.0F;
		}
	}

	@Environment(EnvType.CLIENT)
	public float getHeadAngle(float delta) {
		if (this.eatGrassTimer > 4 && this.eatGrassTimer <= 36) {
			float f = ((float)(this.eatGrassTimer - 4) - delta) / 32.0F;
			return (float) (Math.PI / 5) + 0.21991149F * MathHelper.sin(f * 28.7F);
		} else {
			return this.eatGrassTimer > 0 ? (float) (Math.PI / 5) : this.pitch * (float) (Math.PI / 180.0);
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8868) {
			if (!this.world.isClient && this.isShearable()) {
				this.sheared(SoundCategory.field_15248);
				itemStack.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.CONSUME;
			}
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Override
	public void sheared(SoundCategory shearedSoundCategory) {
		this.world.playSoundFromEntity(null, this, SoundEvents.field_14975, shearedSoundCategory, 1.0F, 1.0F);
		this.setSheared(true);
		int i = 1 + this.random.nextInt(3);

		for (int j = 0; j < i; j++) {
			ItemEntity itemEntity = this.dropItem((ItemConvertible)DROPS.get(this.getColor()), 1);
			if (itemEntity != null) {
				itemEntity.setVelocity(
					itemEntity.getVelocity()
						.add(
							(double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F),
							(double)(this.random.nextFloat() * 0.05F),
							(double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)
						)
				);
			}
		}
	}

	@Override
	public boolean isShearable() {
		return this.isAlive() && !this.isSheared() && !this.isBaby();
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putBoolean("Sheared", this.isSheared());
		tag.putByte("Color", (byte)this.getColor().getId());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setSheared(tag.getBoolean("Sheared"));
		this.setColor(DyeColor.byId(tag.getByte("Color")));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14603;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.field_14730;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14814;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.field_14870, 0.15F, 1.0F);
	}

	public DyeColor getColor() {
		return DyeColor.byId(this.dataTracker.get(COLOR) & 15);
	}

	public void setColor(DyeColor color) {
		byte b = this.dataTracker.get(COLOR);
		this.dataTracker.set(COLOR, (byte)(b & 240 | color.getId() & 15));
	}

	public boolean isSheared() {
		return (this.dataTracker.get(COLOR) & 16) != 0;
	}

	public void setSheared(boolean sheared) {
		byte b = this.dataTracker.get(COLOR);
		if (sheared) {
			this.dataTracker.set(COLOR, (byte)(b | 16));
		} else {
			this.dataTracker.set(COLOR, (byte)(b & -17));
		}
	}

	public static DyeColor generateDefaultColor(Random random) {
		int i = random.nextInt(100);
		if (i < 5) {
			return DyeColor.field_7963;
		} else if (i < 10) {
			return DyeColor.field_7944;
		} else if (i < 15) {
			return DyeColor.field_7967;
		} else if (i < 18) {
			return DyeColor.field_7957;
		} else {
			return random.nextInt(500) == 0 ? DyeColor.field_7954 : DyeColor.field_7952;
		}
	}

	public SheepEntity method_6640(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		SheepEntity sheepEntity = (SheepEntity)passiveEntity;
		SheepEntity sheepEntity2 = EntityType.field_6115.create(serverWorld);
		sheepEntity2.setColor(this.getChildColor(this, sheepEntity));
		return sheepEntity2;
	}

	@Override
	public void onEatingGrass() {
		this.setSheared(false);
		if (this.isBaby()) {
			this.growUp(60);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess serverWorldAccess, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		this.setColor(generateDefaultColor(serverWorldAccess.getRandom()));
		return super.initialize(serverWorldAccess, difficulty, spawnReason, entityData, entityTag);
	}

	private DyeColor getChildColor(AnimalEntity firstParent, AnimalEntity secondParent) {
		DyeColor dyeColor = ((SheepEntity)firstParent).getColor();
		DyeColor dyeColor2 = ((SheepEntity)secondParent).getColor();
		CraftingInventory craftingInventory = createDyeMixingCraftingInventory(dyeColor, dyeColor2);
		return (DyeColor)this.world
			.getRecipeManager()
			.getFirstMatch(RecipeType.CRAFTING, craftingInventory, this.world)
			.map(craftingRecipe -> craftingRecipe.craft(craftingInventory))
			.map(ItemStack::getItem)
			.filter(DyeItem.class::isInstance)
			.map(DyeItem.class::cast)
			.map(DyeItem::getColor)
			.orElseGet(() -> this.world.random.nextBoolean() ? dyeColor : dyeColor2);
	}

	private static CraftingInventory createDyeMixingCraftingInventory(DyeColor firstColor, DyeColor secondColor) {
		CraftingInventory craftingInventory = new CraftingInventory(new ScreenHandler(null, -1) {
			@Override
			public boolean canUse(PlayerEntity player) {
				return false;
			}
		}, 2, 1);
		craftingInventory.setStack(0, new ItemStack(DyeItem.byColor(firstColor)));
		craftingInventory.setStack(1, new ItemStack(DyeItem.byColor(secondColor)));
		return craftingInventory;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.95F * dimensions.height;
	}
}
