package net.minecraft.entity.passive;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class TropicalFishEntity extends SchoolingFishEntity {
	private static final TrackedData<Integer> VARIANT = DataTracker.registerData(TropicalFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Identifier[] SHAPE_IDS = new Identifier[]{
		new Identifier("textures/entity/fish/tropical_a.png"), new Identifier("textures/entity/fish/tropical_b.png")
	};
	private static final Identifier[] SMALL_FISH_VARIETY_IDS = new Identifier[]{
		new Identifier("textures/entity/fish/tropical_a_pattern_1.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_2.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_3.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_4.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_5.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_6.png")
	};
	private static final Identifier[] LARGE_FISH_VARIETY_IDS = new Identifier[]{
		new Identifier("textures/entity/fish/tropical_b_pattern_1.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_2.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_3.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_4.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_5.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_6.png")
	};
	public static final int[] COMMON_VARIANTS = new int[]{
		toVariant(TropicalFishEntity.Variety.field_6887, DyeColor.ORANGE, DyeColor.GRAY),
		toVariant(TropicalFishEntity.Variety.field_6893, DyeColor.GRAY, DyeColor.GRAY),
		toVariant(TropicalFishEntity.Variety.field_6893, DyeColor.GRAY, DyeColor.BLUE),
		toVariant(TropicalFishEntity.Variety.field_6889, DyeColor.WHITE, DyeColor.GRAY),
		toVariant(TropicalFishEntity.Variety.field_6880, DyeColor.BLUE, DyeColor.GRAY),
		toVariant(TropicalFishEntity.Variety.field_6881, DyeColor.ORANGE, DyeColor.WHITE),
		toVariant(TropicalFishEntity.Variety.field_6892, DyeColor.PINK, DyeColor.LIGHT_BLUE),
		toVariant(TropicalFishEntity.Variety.field_6884, DyeColor.PURPLE, DyeColor.YELLOW),
		toVariant(TropicalFishEntity.Variety.field_6889, DyeColor.WHITE, DyeColor.RED),
		toVariant(TropicalFishEntity.Variety.field_6892, DyeColor.WHITE, DyeColor.YELLOW),
		toVariant(TropicalFishEntity.Variety.field_6883, DyeColor.WHITE, DyeColor.GRAY),
		toVariant(TropicalFishEntity.Variety.field_6889, DyeColor.WHITE, DyeColor.ORANGE),
		toVariant(TropicalFishEntity.Variety.field_6890, DyeColor.CYAN, DyeColor.PINK),
		toVariant(TropicalFishEntity.Variety.field_6891, DyeColor.LIME, DyeColor.LIGHT_BLUE),
		toVariant(TropicalFishEntity.Variety.field_6888, DyeColor.RED, DyeColor.WHITE),
		toVariant(TropicalFishEntity.Variety.field_6882, DyeColor.GRAY, DyeColor.RED),
		toVariant(TropicalFishEntity.Variety.field_6884, DyeColor.RED, DyeColor.WHITE),
		toVariant(TropicalFishEntity.Variety.field_6893, DyeColor.WHITE, DyeColor.YELLOW),
		toVariant(TropicalFishEntity.Variety.field_6881, DyeColor.RED, DyeColor.WHITE),
		toVariant(TropicalFishEntity.Variety.field_6880, DyeColor.GRAY, DyeColor.WHITE),
		toVariant(TropicalFishEntity.Variety.field_6890, DyeColor.CYAN, DyeColor.YELLOW),
		toVariant(TropicalFishEntity.Variety.field_6893, DyeColor.YELLOW, DyeColor.YELLOW)
	};
	private boolean commonSpawn = true;

	private static int toVariant(TropicalFishEntity.Variety variety, DyeColor dyeColor, DyeColor dyeColor2) {
		return variety.getShape() & 0xFF | (variety.getPattern() & 0xFF) << 8 | (dyeColor.getId() & 0xFF) << 16 | (dyeColor2.getId() & 0xFF) << 24;
	}

	public TropicalFishEntity(World world) {
		super(EntityType.TROPICAL_FISH, world);
	}

	@Environment(EnvType.CLIENT)
	public static String getToolTipForVariant(int i) {
		return "entity.minecraft.tropical_fish.predefined." + i;
	}

	@Environment(EnvType.CLIENT)
	public static DyeColor getBaseDyeColor(int i) {
		return DyeColor.byId(getBaseDyeColorIndex(i));
	}

	@Environment(EnvType.CLIENT)
	public static DyeColor getPatternDyeColor(int i) {
		return DyeColor.byId(getPatternDyeColorIndex(i));
	}

	@Environment(EnvType.CLIENT)
	public static String getTranslationKey(int i) {
		int j = getShape(i);
		int k = getPattern(i);
		return "entity.minecraft.tropical_fish.type." + TropicalFishEntity.Variety.getTranslateKey(j, k);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VARIANT, 0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Variant", this.getVariant());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setVariant(compoundTag.getInt("Variant"));
	}

	public void setVariant(int i) {
		this.dataTracker.set(VARIANT, i);
	}

	@Override
	public boolean spawnsTooManyForEachTry(int i) {
		return !this.commonSpawn;
	}

	public int getVariant() {
		return this.dataTracker.get(VARIANT);
	}

	@Override
	protected void copyDataToStack(ItemStack itemStack) {
		super.copyDataToStack(itemStack);
		CompoundTag compoundTag = itemStack.getOrCreateTag();
		compoundTag.putInt("BucketVariantTag", this.getVariant());
	}

	@Override
	protected ItemStack getFishBucketItem() {
		return new ItemStack(Items.field_8478);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15085;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15201;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14985;
	}

	@Override
	protected SoundEvent getFlopSound() {
		return SoundEvents.field_14878;
	}

	@Environment(EnvType.CLIENT)
	private static int getBaseDyeColorIndex(int i) {
		return (i & 0xFF0000) >> 16;
	}

	@Environment(EnvType.CLIENT)
	public float[] getBaseColorComponents() {
		return DyeColor.byId(getBaseDyeColorIndex(this.getVariant())).getColorComponents();
	}

	@Environment(EnvType.CLIENT)
	private static int getPatternDyeColorIndex(int i) {
		return (i & 0xFF000000) >> 24;
	}

	@Environment(EnvType.CLIENT)
	public float[] getPatternColorComponents() {
		return DyeColor.byId(getPatternDyeColorIndex(this.getVariant())).getColorComponents();
	}

	@Environment(EnvType.CLIENT)
	public static int getShape(int i) {
		return Math.min(i & 0xFF, 1);
	}

	@Environment(EnvType.CLIENT)
	public int getShape() {
		return getShape(this.getVariant());
	}

	@Environment(EnvType.CLIENT)
	private static int getPattern(int i) {
		return Math.min((i & 0xFF00) >> 8, 5);
	}

	@Environment(EnvType.CLIENT)
	public Identifier getVarietyId() {
		return getShape(this.getVariant()) == 0 ? SMALL_FISH_VARIETY_IDS[getPattern(this.getVariant())] : LARGE_FISH_VARIETY_IDS[getPattern(this.getVariant())];
	}

	@Environment(EnvType.CLIENT)
	public Identifier getShapeId() {
		return SHAPE_IDS[getShape(this.getVariant())];
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (compoundTag != null && compoundTag.containsKey("BucketVariantTag", 3)) {
			this.setVariant(compoundTag.getInt("BucketVariantTag"));
			return entityData;
		} else {
			int i;
			int j;
			int k;
			int l;
			if (entityData instanceof TropicalFishEntity.Data) {
				TropicalFishEntity.Data data = (TropicalFishEntity.Data)entityData;
				i = data.shape;
				j = data.pattern;
				k = data.baseColor;
				l = data.patternColor;
			} else if ((double)this.random.nextFloat() < 0.9) {
				int m = COMMON_VARIANTS[this.random.nextInt(COMMON_VARIANTS.length)];
				i = m & 0xFF;
				j = (m & 0xFF00) >> 8;
				k = (m & 0xFF0000) >> 16;
				l = (m & 0xFF000000) >> 24;
				entityData = new TropicalFishEntity.Data(this, i, j, k, l);
			} else {
				this.commonSpawn = false;
				i = this.random.nextInt(2);
				j = this.random.nextInt(6);
				k = this.random.nextInt(15);
				l = this.random.nextInt(15);
			}

			this.setVariant(i | j << 8 | k << 16 | l << 24);
			return entityData;
		}
	}

	static class Data extends SchoolingFishEntity.Data {
		private final int shape;
		private final int pattern;
		private final int baseColor;
		private final int patternColor;

		private Data(TropicalFishEntity tropicalFishEntity, int i, int j, int k, int l) {
			super(tropicalFishEntity);
			this.shape = i;
			this.pattern = j;
			this.baseColor = k;
			this.patternColor = l;
		}
	}

	static enum Variety {
		field_6881(0, 0),
		field_6880(0, 1),
		field_6882(0, 2),
		field_6890(0, 3),
		field_6891(0, 4),
		field_6892(0, 5),
		field_6893(1, 0),
		field_6887(1, 1),
		field_6883(1, 2),
		field_6884(1, 3),
		field_6888(1, 4),
		field_6889(1, 5);

		private final int shape;
		private final int pattern;
		private static final TropicalFishEntity.Variety[] field_6885 = values();

		private Variety(int j, int k) {
			this.shape = j;
			this.pattern = k;
		}

		public int getShape() {
			return this.shape;
		}

		public int getPattern() {
			return this.pattern;
		}

		@Environment(EnvType.CLIENT)
		public static String getTranslateKey(int i, int j) {
			return field_6885[j + 6 * i].getTranslationKey();
		}

		@Environment(EnvType.CLIENT)
		public String getTranslationKey() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
