package net.minecraft.entity.passive;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
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
		toVariant(TropicalFishEntity.Variety.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY),
		toVariant(TropicalFishEntity.Variety.FLOPPER, DyeColor.GRAY, DyeColor.GRAY),
		toVariant(TropicalFishEntity.Variety.FLOPPER, DyeColor.GRAY, DyeColor.BLUE),
		toVariant(TropicalFishEntity.Variety.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY),
		toVariant(TropicalFishEntity.Variety.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY),
		toVariant(TropicalFishEntity.Variety.KOB, DyeColor.ORANGE, DyeColor.WHITE),
		toVariant(TropicalFishEntity.Variety.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE),
		toVariant(TropicalFishEntity.Variety.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW),
		toVariant(TropicalFishEntity.Variety.CLAYFISH, DyeColor.WHITE, DyeColor.RED),
		toVariant(TropicalFishEntity.Variety.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW),
		toVariant(TropicalFishEntity.Variety.GLITTER, DyeColor.WHITE, DyeColor.GRAY),
		toVariant(TropicalFishEntity.Variety.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE),
		toVariant(TropicalFishEntity.Variety.DASHER, DyeColor.CYAN, DyeColor.PINK),
		toVariant(TropicalFishEntity.Variety.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE),
		toVariant(TropicalFishEntity.Variety.BETTY, DyeColor.RED, DyeColor.WHITE),
		toVariant(TropicalFishEntity.Variety.SNOOPER, DyeColor.GRAY, DyeColor.RED),
		toVariant(TropicalFishEntity.Variety.BLOCKFISH, DyeColor.RED, DyeColor.WHITE),
		toVariant(TropicalFishEntity.Variety.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW),
		toVariant(TropicalFishEntity.Variety.KOB, DyeColor.RED, DyeColor.WHITE),
		toVariant(TropicalFishEntity.Variety.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE),
		toVariant(TropicalFishEntity.Variety.DASHER, DyeColor.CYAN, DyeColor.YELLOW),
		toVariant(TropicalFishEntity.Variety.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)
	};
	private boolean commonSpawn = true;

	private static int toVariant(TropicalFishEntity.Variety variety, DyeColor baseColor, DyeColor patternColor) {
		return variety.getShape() & 0xFF | (variety.getPattern() & 0xFF) << 8 | (baseColor.getId() & 0xFF) << 16 | (patternColor.getId() & 0xFF) << 24;
	}

	public TropicalFishEntity(EntityType<? extends TropicalFishEntity> entityType, World world) {
		super(entityType, world);
	}

	@Environment(EnvType.CLIENT)
	public static String getToolTipForVariant(int variant) {
		return "entity.minecraft.tropical_fish.predefined." + variant;
	}

	@Environment(EnvType.CLIENT)
	public static DyeColor getBaseDyeColor(int variant) {
		return DyeColor.byId(getBaseDyeColorIndex(variant));
	}

	@Environment(EnvType.CLIENT)
	public static DyeColor getPatternDyeColor(int variant) {
		return DyeColor.byId(getPatternDyeColorIndex(variant));
	}

	@Environment(EnvType.CLIENT)
	public static String getTranslationKey(int variant) {
		int i = getShape(variant);
		int j = getPattern(variant);
		return "entity.minecraft.tropical_fish.type." + TropicalFishEntity.Variety.getTranslateKey(i, j);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VARIANT, 0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Variant", this.getVariant());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setVariant(nbt.getInt("Variant"));
	}

	public void setVariant(int variant) {
		this.dataTracker.set(VARIANT, variant);
	}

	@Override
	public boolean spawnsTooManyForEachTry(int count) {
		return !this.commonSpawn;
	}

	public int getVariant() {
		return this.dataTracker.get(VARIANT);
	}

	@Override
	protected void copyDataToStack(ItemStack stack) {
		super.copyDataToStack(stack);
		NbtCompound nbtCompound = stack.getOrCreateTag();
		nbtCompound.putInt("BucketVariantTag", this.getVariant());
	}

	@Override
	protected ItemStack getFishBucketItem() {
		return new ItemStack(Items.TROPICAL_FISH_BUCKET);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_TROPICAL_FISH_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_TROPICAL_FISH_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_TROPICAL_FISH_HURT;
	}

	@Override
	protected SoundEvent getFlopSound() {
		return SoundEvents.ENTITY_TROPICAL_FISH_FLOP;
	}

	@Environment(EnvType.CLIENT)
	private static int getBaseDyeColorIndex(int variant) {
		return (variant & 0xFF0000) >> 16;
	}

	@Environment(EnvType.CLIENT)
	public float[] getBaseColorComponents() {
		return DyeColor.byId(getBaseDyeColorIndex(this.getVariant())).getColorComponents();
	}

	@Environment(EnvType.CLIENT)
	private static int getPatternDyeColorIndex(int variant) {
		return (variant & 0xFF000000) >> 24;
	}

	@Environment(EnvType.CLIENT)
	public float[] getPatternColorComponents() {
		return DyeColor.byId(getPatternDyeColorIndex(this.getVariant())).getColorComponents();
	}

	@Environment(EnvType.CLIENT)
	public static int getShape(int variant) {
		return Math.min(variant & 0xFF, 1);
	}

	@Environment(EnvType.CLIENT)
	public int getShape() {
		return getShape(this.getVariant());
	}

	@Environment(EnvType.CLIENT)
	private static int getPattern(int variant) {
		return Math.min((variant & 0xFF00) >> 8, 5);
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
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
		if (entityNbt != null && entityNbt.contains("BucketVariantTag", 3)) {
			this.setVariant(entityNbt.getInt("BucketVariantTag"));
			return entityData;
		} else {
			int i;
			int j;
			int k;
			int l;
			if (entityData instanceof TropicalFishEntity.TropicalFishData) {
				TropicalFishEntity.TropicalFishData tropicalFishData = (TropicalFishEntity.TropicalFishData)entityData;
				i = tropicalFishData.shape;
				j = tropicalFishData.pattern;
				k = tropicalFishData.baseColor;
				l = tropicalFishData.patternColor;
			} else if ((double)this.random.nextFloat() < 0.9) {
				int m = Util.getRandom(COMMON_VARIANTS, this.random);
				i = m & 0xFF;
				j = (m & 0xFF00) >> 8;
				k = (m & 0xFF0000) >> 16;
				l = (m & 0xFF000000) >> 24;
				entityData = new TropicalFishEntity.TropicalFishData(this, i, j, k, l);
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

	static class TropicalFishData extends SchoolingFishEntity.FishData {
		private final int shape;
		private final int pattern;
		private final int baseColor;
		private final int patternColor;

		private TropicalFishData(TropicalFishEntity leader, int shape, int pattern, int baseColor, int patternColor) {
			super(leader);
			this.shape = shape;
			this.pattern = pattern;
			this.baseColor = baseColor;
			this.patternColor = patternColor;
		}
	}

	static enum Variety {
		KOB(0, 0),
		SUNSTREAK(0, 1),
		SNOOPER(0, 2),
		DASHER(0, 3),
		BRINELY(0, 4),
		SPOTTY(0, 5),
		FLOPPER(1, 0),
		STRIPEY(1, 1),
		GLITTER(1, 2),
		BLOCKFISH(1, 3),
		BETTY(1, 4),
		CLAYFISH(1, 5);

		private final int shape;
		private final int pattern;
		private static final TropicalFishEntity.Variety[] VALUES = values();

		private Variety(int shape, int pattern) {
			this.shape = shape;
			this.pattern = pattern;
		}

		public int getShape() {
			return this.shape;
		}

		public int getPattern() {
			return this.pattern;
		}

		@Environment(EnvType.CLIENT)
		public static String getTranslateKey(int shape, int pattern) {
			return VALUES[pattern + 6 * shape].getTranslationKey();
		}

		@Environment(EnvType.CLIENT)
		public String getTranslationKey() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
