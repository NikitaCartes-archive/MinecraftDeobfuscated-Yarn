package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TropicalFishEntity extends SchoolingFishEntity implements VariantHolder<TropicalFishEntity.Variety> {
	public static final String BUCKET_VARIANT_TAG_KEY = "BucketVariantTag";
	private static final TrackedData<Integer> VARIANT = DataTracker.registerData(TropicalFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final List<TropicalFishEntity.Variant> COMMON_VARIANTS = List.of(
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.FLOPPER, DyeColor.GRAY, DyeColor.GRAY),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.FLOPPER, DyeColor.GRAY, DyeColor.BLUE),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.KOB, DyeColor.ORANGE, DyeColor.WHITE),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.CLAYFISH, DyeColor.WHITE, DyeColor.RED),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.GLITTER, DyeColor.WHITE, DyeColor.GRAY),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.DASHER, DyeColor.CYAN, DyeColor.PINK),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.BETTY, DyeColor.RED, DyeColor.WHITE),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.SNOOPER, DyeColor.GRAY, DyeColor.RED),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.BLOCKFISH, DyeColor.RED, DyeColor.WHITE),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.KOB, DyeColor.RED, DyeColor.WHITE),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.DASHER, DyeColor.CYAN, DyeColor.YELLOW),
		new TropicalFishEntity.Variant(TropicalFishEntity.Variety.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)
	);
	private boolean commonSpawn = true;

	public TropicalFishEntity(EntityType<? extends TropicalFishEntity> entityType, World world) {
		super(entityType, world);
	}

	public static String getToolTipForVariant(int variant) {
		return "entity.minecraft.tropical_fish.predefined." + variant;
	}

	static int getVariantId(TropicalFishEntity.Variety variety, DyeColor baseColor, DyeColor patternColor) {
		return variety.getId() & 65535 | (baseColor.getId() & 0xFF) << 16 | (patternColor.getId() & 0xFF) << 24;
	}

	public static DyeColor getBaseDyeColor(int variant) {
		return DyeColor.byId(variant >> 16 & 0xFF);
	}

	public static DyeColor getPatternDyeColor(int variant) {
		return DyeColor.byId(variant >> 24 & 0xFF);
	}

	public static TropicalFishEntity.Variety getVariety(int variant) {
		return TropicalFishEntity.Variety.fromId(variant & 65535);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(VARIANT, 0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Variant", this.getTropicalFishVariant());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setTropicalFishVariant(nbt.getInt("Variant"));
	}

	private void setTropicalFishVariant(int variant) {
		this.dataTracker.set(VARIANT, variant);
	}

	@Override
	public boolean spawnsTooManyForEachTry(int count) {
		return !this.commonSpawn;
	}

	private int getTropicalFishVariant() {
		return this.dataTracker.get(VARIANT);
	}

	public DyeColor getBaseColorComponents() {
		return getBaseDyeColor(this.getTropicalFishVariant());
	}

	public DyeColor getPatternColorComponents() {
		return getPatternDyeColor(this.getTropicalFishVariant());
	}

	public TropicalFishEntity.Variety getVariant() {
		return getVariety(this.getTropicalFishVariant());
	}

	public void setVariant(TropicalFishEntity.Variety variety) {
		int i = this.getTropicalFishVariant();
		DyeColor dyeColor = getBaseDyeColor(i);
		DyeColor dyeColor2 = getPatternDyeColor(i);
		this.setTropicalFishVariant(getVariantId(variety, dyeColor, dyeColor2));
	}

	@Override
	public void copyDataToStack(ItemStack stack) {
		super.copyDataToStack(stack);
		NbtComponent.set(DataComponentTypes.BUCKET_ENTITY_DATA, stack, nbtCompound -> nbtCompound.putInt("BucketVariantTag", this.getTropicalFishVariant()));
	}

	@Override
	public ItemStack getBucketItem() {
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

	@Override
	public void copyDataFromNbt(NbtCompound nbt) {
		super.copyDataFromNbt(nbt);
		if (nbt.contains("BucketVariantTag", NbtElement.INT_TYPE)) {
			this.setTropicalFishVariant(nbt.getInt("BucketVariantTag"));
		}
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		entityData = super.initialize(world, difficulty, spawnReason, entityData);
		Random random = world.getRandom();
		TropicalFishEntity.Variant variant;
		if (entityData instanceof TropicalFishEntity.TropicalFishData tropicalFishData) {
			variant = tropicalFishData.variant;
		} else if ((double)random.nextFloat() < 0.9) {
			variant = Util.getRandom(COMMON_VARIANTS, random);
			entityData = new TropicalFishEntity.TropicalFishData(this, variant);
		} else {
			this.commonSpawn = false;
			TropicalFishEntity.Variety[] varietys = TropicalFishEntity.Variety.values();
			DyeColor[] dyeColors = DyeColor.values();
			TropicalFishEntity.Variety variety = Util.getRandom(varietys, random);
			DyeColor dyeColor = Util.getRandom(dyeColors, random);
			DyeColor dyeColor2 = Util.getRandom(dyeColors, random);
			variant = new TropicalFishEntity.Variant(variety, dyeColor, dyeColor2);
		}

		this.setTropicalFishVariant(variant.getId());
		return entityData;
	}

	public static boolean canTropicalFishSpawn(EntityType<TropicalFishEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
		return world.getFluidState(pos.down()).isIn(FluidTags.WATER)
			&& world.getBlockState(pos.up()).isOf(Blocks.WATER)
			&& (world.getBiome(pos).isIn(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT) || WaterCreatureEntity.canSpawn(type, world, reason, pos, random));
	}

	public static enum Size {
		SMALL(0),
		LARGE(1);

		final int id;

		private Size(final int id) {
			this.id = id;
		}
	}

	static class TropicalFishData extends SchoolingFishEntity.FishData {
		final TropicalFishEntity.Variant variant;

		TropicalFishData(TropicalFishEntity leader, TropicalFishEntity.Variant variant) {
			super(leader);
			this.variant = variant;
		}
	}

	public static record Variant(TropicalFishEntity.Variety variety, DyeColor baseColor, DyeColor patternColor) {
		public static final Codec<TropicalFishEntity.Variant> CODEC = Codec.INT.xmap(TropicalFishEntity.Variant::new, TropicalFishEntity.Variant::getId);

		public Variant(int id) {
			this(TropicalFishEntity.getVariety(id), TropicalFishEntity.getBaseDyeColor(id), TropicalFishEntity.getPatternDyeColor(id));
		}

		public int getId() {
			return TropicalFishEntity.getVariantId(this.variety, this.baseColor, this.patternColor);
		}
	}

	public static enum Variety implements StringIdentifiable {
		KOB("kob", TropicalFishEntity.Size.SMALL, 0),
		SUNSTREAK("sunstreak", TropicalFishEntity.Size.SMALL, 1),
		SNOOPER("snooper", TropicalFishEntity.Size.SMALL, 2),
		DASHER("dasher", TropicalFishEntity.Size.SMALL, 3),
		BRINELY("brinely", TropicalFishEntity.Size.SMALL, 4),
		SPOTTY("spotty", TropicalFishEntity.Size.SMALL, 5),
		FLOPPER("flopper", TropicalFishEntity.Size.LARGE, 0),
		STRIPEY("stripey", TropicalFishEntity.Size.LARGE, 1),
		GLITTER("glitter", TropicalFishEntity.Size.LARGE, 2),
		BLOCKFISH("blockfish", TropicalFishEntity.Size.LARGE, 3),
		BETTY("betty", TropicalFishEntity.Size.LARGE, 4),
		CLAYFISH("clayfish", TropicalFishEntity.Size.LARGE, 5);

		public static final Codec<TropicalFishEntity.Variety> CODEC = StringIdentifiable.createCodec(TropicalFishEntity.Variety::values);
		private static final IntFunction<TropicalFishEntity.Variety> BY_ID = ValueLists.createIdToValueFunction(TropicalFishEntity.Variety::getId, values(), KOB);
		private final String name;
		private final Text text;
		private final TropicalFishEntity.Size size;
		private final int id;

		private Variety(final String name, final TropicalFishEntity.Size size, final int id) {
			this.name = name;
			this.size = size;
			this.id = size.id | id << 8;
			this.text = Text.translatable("entity.minecraft.tropical_fish.type." + this.name);
		}

		public static TropicalFishEntity.Variety fromId(int id) {
			return (TropicalFishEntity.Variety)BY_ID.apply(id);
		}

		public TropicalFishEntity.Size getSize() {
			return this.size;
		}

		public int getId() {
			return this.id;
		}

		@Override
		public String asString() {
			return this.name;
		}

		public Text getText() {
			return this.text;
		}
	}
}
