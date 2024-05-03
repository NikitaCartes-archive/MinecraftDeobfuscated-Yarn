package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityStatuses;
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
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SheepEntity extends AnimalEntity implements Shearable {
	private static final int MAX_GRASS_TIMER = 40;
	private static final TrackedData<Byte> COLOR = DataTracker.registerData(SheepEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final Map<DyeColor, ItemConvertible> DROPS = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
		map.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
		map.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
		map.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
		map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
		map.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
		map.put(DyeColor.LIME, Blocks.LIME_WOOL);
		map.put(DyeColor.PINK, Blocks.PINK_WOOL);
		map.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
		map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
		map.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
		map.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
		map.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
		map.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
		map.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
		map.put(DyeColor.RED, Blocks.RED_WOOL);
		map.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
	});
	private static final Map<DyeColor, float[]> COLORS = Maps.newEnumMap(
		(Map)Arrays.stream(DyeColor.values()).collect(Collectors.toMap(color -> color, SheepEntity::getDyedColor))
	);
	private int eatGrassTimer;
	private EatGrassGoal eatGrassGoal;

	private static float[] getDyedColor(DyeColor color) {
		if (color == DyeColor.WHITE) {
			return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
		} else {
			float[] fs = color.getColorComponents();
			float f = 0.75F;
			return new float[]{fs[0] * 0.75F, fs[1] * 0.75F, fs[2] * 0.75F};
		}
	}

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
		this.goalSelector.add(3, new TemptGoal(this, 1.1, stack -> stack.isIn(ItemTags.SHEEP_FOOD), false));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(5, this.eatGrassGoal);
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.isIn(ItemTags.SHEEP_FOOD);
	}

	@Override
	protected void mobTick() {
		this.eatGrassTimer = this.eatGrassGoal.getTimer();
		super.mobTick();
	}

	@Override
	public void tickMovement() {
		if (this.getWorld().isClient) {
			this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
		}

		super.tickMovement();
	}

	public static DefaultAttributeContainer.Builder createSheepAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(COLOR, (byte)0);
	}

	@Override
	public RegistryKey<LootTable> getLootTableId() {
		if (this.isSheared()) {
			return this.getType().getLootTableId();
		} else {
			return switch (this.getColor()) {
				case WHITE -> LootTables.WHITE_SHEEP_ENTITY;
				case ORANGE -> LootTables.ORANGE_SHEEP_ENTITY;
				case MAGENTA -> LootTables.MAGENTA_SHEEP_ENTITY;
				case LIGHT_BLUE -> LootTables.LIGHT_BLUE_SHEEP_ENTITY;
				case YELLOW -> LootTables.YELLOW_SHEEP_ENTITY;
				case LIME -> LootTables.LIME_SHEEP_ENTITY;
				case PINK -> LootTables.PINK_SHEEP_ENTITY;
				case GRAY -> LootTables.GRAY_SHEEP_ENTITY;
				case LIGHT_GRAY -> LootTables.LIGHT_GRAY_SHEEP_ENTITY;
				case CYAN -> LootTables.CYAN_SHEEP_ENTITY;
				case PURPLE -> LootTables.PURPLE_SHEEP_ENTITY;
				case BLUE -> LootTables.BLUE_SHEEP_ENTITY;
				case BROWN -> LootTables.BROWN_SHEEP_ENTITY;
				case GREEN -> LootTables.GREEN_SHEEP_ENTITY;
				case RED -> LootTables.RED_SHEEP_ENTITY;
				case BLACK -> LootTables.BLACK_SHEEP_ENTITY;
			};
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.SET_SHEEP_EAT_GRASS_TIMER_OR_PRIME_TNT_MINECART) {
			this.eatGrassTimer = 40;
		} else {
			super.handleStatus(status);
		}
	}

	public float getNeckAngle(float delta) {
		if (this.eatGrassTimer <= 0) {
			return 0.0F;
		} else if (this.eatGrassTimer >= 4 && this.eatGrassTimer <= 36) {
			return 1.0F;
		} else {
			return this.eatGrassTimer < 4 ? ((float)this.eatGrassTimer - delta) / 4.0F : -((float)(this.eatGrassTimer - 40) - delta) / 4.0F;
		}
	}

	public float getHeadAngle(float delta) {
		if (this.eatGrassTimer > 4 && this.eatGrassTimer <= 36) {
			float f = ((float)(this.eatGrassTimer - 4) - delta) / 32.0F;
			return (float) (Math.PI / 5) + 0.21991149F * MathHelper.sin(f * 28.7F);
		} else {
			return this.eatGrassTimer > 0 ? (float) (Math.PI / 5) : this.getPitch() * (float) (Math.PI / 180.0);
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.SHEARS)) {
			if (!this.getWorld().isClient && this.isShearable()) {
				this.sheared(SoundCategory.PLAYERS);
				this.emitGameEvent(GameEvent.SHEAR, player);
				itemStack.damage(1, player, getSlotForHand(hand));
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
		this.getWorld().playSoundFromEntity(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
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
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Sheared", this.isSheared());
		nbt.putByte("Color", (byte)this.getColor().getId());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setSheared(nbt.getBoolean("Sheared"));
		this.setColor(DyeColor.byId(nbt.getByte("Color")));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SHEEP_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SHEEP_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SHEEP_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
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
			return DyeColor.BLACK;
		} else if (i < 10) {
			return DyeColor.GRAY;
		} else if (i < 15) {
			return DyeColor.LIGHT_GRAY;
		} else if (i < 18) {
			return DyeColor.BROWN;
		} else {
			return random.nextInt(500) == 0 ? DyeColor.PINK : DyeColor.WHITE;
		}
	}

	@Nullable
	public SheepEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		SheepEntity sheepEntity = EntityType.SHEEP.create(serverWorld);
		if (sheepEntity != null) {
			sheepEntity.setColor(this.getChildColor(this, (SheepEntity)passiveEntity));
		}

		return sheepEntity;
	}

	@Override
	public void onEatingGrass() {
		super.onEatingGrass();
		this.setSheared(false);
		if (this.isBaby()) {
			this.growUp(60);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		this.setColor(generateDefaultColor(world.getRandom()));
		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	private DyeColor getChildColor(AnimalEntity firstParent, AnimalEntity secondParent) {
		DyeColor dyeColor = ((SheepEntity)firstParent).getColor();
		DyeColor dyeColor2 = ((SheepEntity)secondParent).getColor();
		CraftingRecipeInput craftingRecipeInput = createChildColorRecipeInput(dyeColor, dyeColor2);
		return (DyeColor)this.getWorld()
			.getRecipeManager()
			.getFirstMatch(RecipeType.CRAFTING, craftingRecipeInput, this.getWorld())
			.map(recipe -> ((CraftingRecipe)recipe.value()).craft(craftingRecipeInput, this.getWorld().getRegistryManager()))
			.map(ItemStack::getItem)
			.filter(DyeItem.class::isInstance)
			.map(DyeItem.class::cast)
			.map(DyeItem::getColor)
			.orElseGet(() -> this.getWorld().random.nextBoolean() ? dyeColor : dyeColor2);
	}

	private static CraftingRecipeInput createChildColorRecipeInput(DyeColor firstColor, DyeColor secondColor) {
		return CraftingRecipeInput.create(2, 1, List.of(new ItemStack(DyeItem.byColor(firstColor)), new ItemStack(DyeItem.byColor(secondColor))));
	}
}
