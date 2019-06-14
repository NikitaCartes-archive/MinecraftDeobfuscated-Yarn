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
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.loot.LootTables;

public class SheepEntity extends AnimalEntity {
	private static final TrackedData<Byte> COLOR = DataTracker.registerData(SheepEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final Map<DyeColor, ItemConvertible> DROPS = SystemUtil.consume(Maps.newEnumMap(DyeColor.class), enumMap -> {
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
	private int field_6865;
	private EatGrassGoal eatGrassGoal;

	private static float[] getDyedColor(DyeColor dyeColor) {
		if (dyeColor == DyeColor.field_7952) {
			return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
		} else {
			float[] fs = dyeColor.getColorComponents();
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
		this.goalSelector.add(3, new TemptGoal(this, 1.1, Ingredient.method_8091(Items.field_8861), false));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(5, this.eatGrassGoal);
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
	}

	@Override
	protected void mobTick() {
		this.field_6865 = this.eatGrassGoal.getTimer();
		super.mobTick();
	}

	@Override
	public void tickMovement() {
		if (this.field_6002.isClient) {
			this.field_6865 = Math.max(0, this.field_6865 - 1);
		}

		super.tickMovement();
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23F);
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
	public void handleStatus(byte b) {
		if (b == 10) {
			this.field_6865 = 40;
		} else {
			super.handleStatus(b);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_6628(float f) {
		if (this.field_6865 <= 0) {
			return 0.0F;
		} else if (this.field_6865 >= 4 && this.field_6865 <= 36) {
			return 1.0F;
		} else {
			return this.field_6865 < 4 ? ((float)this.field_6865 - f) / 4.0F : -((float)(this.field_6865 - 40) - f) / 4.0F;
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_6641(float f) {
		if (this.field_6865 > 4 && this.field_6865 <= 36) {
			float g = ((float)(this.field_6865 - 4) - f) / 32.0F;
			return (float) (Math.PI / 5) + 0.21991149F * MathHelper.sin(g * 28.7F);
		} else {
			return this.field_6865 > 0 ? (float) (Math.PI / 5) : this.pitch * (float) (Math.PI / 180.0);
		}
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8868 && !this.isSheared() && !this.isBaby()) {
			this.dropItems();
			if (!this.field_6002.isClient) {
				itemStack.damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(hand));
			}
		}

		return super.interactMob(playerEntity, hand);
	}

	public void dropItems() {
		if (!this.field_6002.isClient) {
			this.setSheared(true);
			int i = 1 + this.random.nextInt(3);

			for (int j = 0; j < i; j++) {
				ItemEntity itemEntity = this.method_5870((ItemConvertible)DROPS.get(this.getColor()), 1);
				if (itemEntity != null) {
					itemEntity.method_18799(
						itemEntity.method_18798()
							.add(
								(double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F),
								(double)(this.random.nextFloat() * 0.05F),
								(double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)
							)
					);
				}
			}
		}

		this.playSound(SoundEvents.field_14975, 1.0F, 1.0F);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("Sheared", this.isSheared());
		compoundTag.putByte("Color", (byte)this.getColor().getId());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setSheared(compoundTag.getBoolean("Sheared"));
		this.setColor(DyeColor.byId(compoundTag.getByte("Color")));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14603;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14730;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14814;
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_14870, 0.15F, 1.0F);
	}

	public DyeColor getColor() {
		return DyeColor.byId(this.dataTracker.get(COLOR) & 15);
	}

	public void setColor(DyeColor dyeColor) {
		byte b = this.dataTracker.get(COLOR);
		this.dataTracker.set(COLOR, (byte)(b & 240 | dyeColor.getId() & 15));
	}

	public boolean isSheared() {
		return (this.dataTracker.get(COLOR) & 16) != 0;
	}

	public void setSheared(boolean bl) {
		byte b = this.dataTracker.get(COLOR);
		if (bl) {
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

	public SheepEntity method_6640(PassiveEntity passiveEntity) {
		SheepEntity sheepEntity = (SheepEntity)passiveEntity;
		SheepEntity sheepEntity2 = EntityType.field_6115.method_5883(this.field_6002);
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
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		this.setColor(generateDefaultColor(iWorld.getRandom()));
		return entityData;
	}

	private DyeColor getChildColor(AnimalEntity animalEntity, AnimalEntity animalEntity2) {
		DyeColor dyeColor = ((SheepEntity)animalEntity).getColor();
		DyeColor dyeColor2 = ((SheepEntity)animalEntity2).getColor();
		CraftingInventory craftingInventory = createDyeMixingCraftingInventory(dyeColor, dyeColor2);
		return (DyeColor)this.field_6002
			.getRecipeManager()
			.method_8132(RecipeType.CRAFTING, craftingInventory, this.field_6002)
			.map(craftingRecipe -> craftingRecipe.craft(craftingInventory))
			.map(ItemStack::getItem)
			.filter(DyeItem.class::isInstance)
			.map(DyeItem.class::cast)
			.map(DyeItem::getColor)
			.orElseGet(() -> this.field_6002.random.nextBoolean() ? dyeColor : dyeColor2);
	}

	private static CraftingInventory createDyeMixingCraftingInventory(DyeColor dyeColor, DyeColor dyeColor2) {
		CraftingInventory craftingInventory = new CraftingInventory(new Container(null, -1) {
			@Override
			public boolean canUse(PlayerEntity playerEntity) {
				return false;
			}
		}, 2, 1);
		craftingInventory.setInvStack(0, new ItemStack(DyeItem.byColor(dyeColor)));
		craftingInventory.setInvStack(1, new ItemStack(DyeItem.byColor(dyeColor2)));
		return craftingInventory;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return 0.95F * entityDimensions.height;
	}
}
