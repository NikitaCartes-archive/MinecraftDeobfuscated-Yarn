package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1361;
import net.minecraft.class_1374;
import net.minecraft.class_1376;
import net.minecraft.class_1394;
import net.minecraft.class_3730;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.container.Container;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
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
	private final CraftingInventory field_6866 = new CraftingInventory(new Container() {
		@Override
		public boolean canUse(PlayerEntity playerEntity) {
			return false;
		}
	}, 2, 1);
	private static final Map<DyeColor, ItemContainer> field_6868 = SystemUtil.consume(Maps.newEnumMap(DyeColor.class), enumMap -> {
		enumMap.put(DyeColor.WHITE, Blocks.field_10446);
		enumMap.put(DyeColor.ORANGE, Blocks.field_10095);
		enumMap.put(DyeColor.MAGENTA, Blocks.field_10215);
		enumMap.put(DyeColor.LIGHT_BLUE, Blocks.field_10294);
		enumMap.put(DyeColor.YELLOW, Blocks.field_10490);
		enumMap.put(DyeColor.LIME, Blocks.field_10028);
		enumMap.put(DyeColor.PINK, Blocks.field_10459);
		enumMap.put(DyeColor.GRAY, Blocks.field_10423);
		enumMap.put(DyeColor.LIGHT_GRAY, Blocks.field_10222);
		enumMap.put(DyeColor.CYAN, Blocks.field_10619);
		enumMap.put(DyeColor.PURPLE, Blocks.field_10259);
		enumMap.put(DyeColor.BLUE, Blocks.field_10514);
		enumMap.put(DyeColor.BROWN, Blocks.field_10113);
		enumMap.put(DyeColor.GREEN, Blocks.field_10170);
		enumMap.put(DyeColor.RED, Blocks.field_10314);
		enumMap.put(DyeColor.BLACK, Blocks.field_10146);
	});
	private static final Map<DyeColor, float[]> field_6867 = Maps.newEnumMap(
		(Map)Arrays.stream(DyeColor.values()).collect(Collectors.toMap(dyeColor -> dyeColor, SheepEntity::method_6630))
	);
	private int field_6865;
	private EatGrassGoal field_6869;

	private static float[] method_6630(DyeColor dyeColor) {
		if (dyeColor == DyeColor.WHITE) {
			return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
		} else {
			float[] fs = dyeColor.getColorComponents();
			float f = 0.75F;
			return new float[]{fs[0] * 0.75F, fs[1] * 0.75F, fs[2] * 0.75F};
		}
	}

	@Environment(EnvType.CLIENT)
	public static float[] getRgbColor(DyeColor dyeColor) {
		return (float[])field_6867.get(dyeColor);
	}

	public SheepEntity(World world) {
		super(EntityType.SHEEP, world);
		this.setSize(0.9F, 1.3F);
	}

	@Override
	protected void method_5959() {
		this.field_6869 = new EatGrassGoal(this);
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new class_1374(this, 1.25));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(3, new TemptGoal(this, 1.1, Ingredient.ofItems(Items.field_8861), false));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(5, this.field_6869);
		this.goalSelector.add(6, new class_1394(this, 1.0));
		this.goalSelector.add(7, new class_1361(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new class_1376(this));
	}

	@Override
	protected void mobTick() {
		this.field_6865 = this.field_6869.getTimer();
		super.mobTick();
	}

	@Override
	public void updateMovement() {
		if (this.world.isRemote) {
			this.field_6865 = Math.max(0, this.field_6865 - 1);
		}

		super.updateMovement();
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
				case WHITE:
				default:
					return LootTables.ENTITY_SHEEP_WHITE;
				case ORANGE:
					return LootTables.ENTITY_SHEEP_ORANGE;
				case MAGENTA:
					return LootTables.ENTITY_SHEEP_MAGENTA;
				case LIGHT_BLUE:
					return LootTables.ENTITY_SHEEP_LIGHT_BLUE;
				case YELLOW:
					return LootTables.ENTITY_SHEEP_YELLOW;
				case LIME:
					return LootTables.ENTITY_SHEEP_LIME;
				case PINK:
					return LootTables.ENTITY_SHEEP_PINK;
				case GRAY:
					return LootTables.ENTITY_SHEEP_GRAY;
				case LIGHT_GRAY:
					return LootTables.field_806;
				case CYAN:
					return LootTables.ENTITY_SHEEP_CYAN;
				case PURPLE:
					return LootTables.ENTITY_SHEEP_PURPLE;
				case BLUE:
					return LootTables.ENTITY_SHEEP_BLUE;
				case BROWN:
					return LootTables.ENTITY_SHEEP_BROWN;
				case GREEN:
					return LootTables.ENTITY_SHEEP_GREEN;
				case RED:
					return LootTables.ENTITY_SHEEP_RED;
				case BLACK:
					return LootTables.ENTITY_SHEEP_BLACK;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 10) {
			this.field_6865 = 40;
		} else {
			super.method_5711(b);
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
		if (itemStack.getItem() == Items.field_8868 && !this.isSheared() && !this.isChild()) {
			this.method_6636();
			itemStack.applyDamage(1, playerEntity);
		}

		return super.interactMob(playerEntity, hand);
	}

	public void method_6636() {
		if (!this.world.isRemote) {
			this.setSheared(true);
			int i = 1 + this.random.nextInt(3);

			for (int j = 0; j < i; j++) {
				ItemEntity itemEntity = this.dropItem((ItemContainer)field_6868.get(this.getColor()), 1);
				if (itemEntity != null) {
					itemEntity.velocityY = itemEntity.velocityY + (double)(this.random.nextFloat() * 0.05F);
					itemEntity.velocityX = itemEntity.velocityX + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
					itemEntity.velocityZ = itemEntity.velocityZ + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
				}
			}
		}

		this.playSoundAtEntity(SoundEvents.field_14975, 1.0F, 1.0F);
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
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSoundAtEntity(SoundEvents.field_14870, 0.15F, 1.0F);
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

	public static DyeColor method_6632(Random random) {
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

	public SheepEntity createChild(PassiveEntity passiveEntity) {
		SheepEntity sheepEntity = (SheepEntity)passiveEntity;
		SheepEntity sheepEntity2 = new SheepEntity(this.world);
		sheepEntity2.setColor(this.method_6639(this, sheepEntity));
		return sheepEntity2;
	}

	@Override
	public void method_5983() {
		this.setSheared(false);
		if (this.isChild()) {
			this.method_5615(60);
		}
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, class_3730 arg, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, arg, entityData, compoundTag);
		this.setColor(method_6632(iWorld.getRandom()));
		return entityData;
	}

	private DyeColor method_6639(AnimalEntity animalEntity, AnimalEntity animalEntity2) {
		DyeColor dyeColor = ((SheepEntity)animalEntity).getColor();
		DyeColor dyeColor2 = ((SheepEntity)animalEntity2).getColor();
		this.field_6866.setInvStack(0, new ItemStack(DyeItem.fromColor(dyeColor)));
		this.field_6866.setInvStack(1, new ItemStack(DyeItem.fromColor(dyeColor2)));
		ItemStack itemStack = animalEntity.world.getRecipeManager().craft(this.field_6866, ((SheepEntity)animalEntity).world);
		Item item = itemStack.getItem();
		DyeColor dyeColor3;
		if (item instanceof DyeItem) {
			dyeColor3 = ((DyeItem)item).getColor();
		} else {
			dyeColor3 = this.world.random.nextBoolean() ? dyeColor : dyeColor2;
		}

		return dyeColor3;
	}

	@Override
	public float getEyeHeight() {
		return 0.95F * this.height;
	}
}
