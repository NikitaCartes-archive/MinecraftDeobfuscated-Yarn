package net.minecraft.entity.passive;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1425;
import net.minecraft.class_3730;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
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

public class TropicalFishEntity extends class_1425 {
	private static final TrackedData<Integer> field_6874 = DataTracker.registerData(TropicalFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Identifier[] field_6875 = new Identifier[]{
		new Identifier("textures/entity/fish/tropical_a.png"), new Identifier("textures/entity/fish/tropical_b.png")
	};
	private static final Identifier[] field_6878 = new Identifier[]{
		new Identifier("textures/entity/fish/tropical_a_pattern_1.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_2.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_3.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_4.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_5.png"),
		new Identifier("textures/entity/fish/tropical_a_pattern_6.png")
	};
	private static final Identifier[] field_6876 = new Identifier[]{
		new Identifier("textures/entity/fish/tropical_b_pattern_1.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_2.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_3.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_4.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_5.png"),
		new Identifier("textures/entity/fish/tropical_b_pattern_6.png")
	};
	public static final int[] field_6879 = new int[]{
		method_6647(TropicalFishEntity.class_1475.field_6887, DyeColor.ORANGE, DyeColor.GRAY),
		method_6647(TropicalFishEntity.class_1475.field_6893, DyeColor.GRAY, DyeColor.GRAY),
		method_6647(TropicalFishEntity.class_1475.field_6893, DyeColor.GRAY, DyeColor.BLUE),
		method_6647(TropicalFishEntity.class_1475.field_6889, DyeColor.WHITE, DyeColor.GRAY),
		method_6647(TropicalFishEntity.class_1475.field_6880, DyeColor.BLUE, DyeColor.GRAY),
		method_6647(TropicalFishEntity.class_1475.field_6881, DyeColor.ORANGE, DyeColor.WHITE),
		method_6647(TropicalFishEntity.class_1475.field_6892, DyeColor.PINK, DyeColor.LIGHT_BLUE),
		method_6647(TropicalFishEntity.class_1475.field_6884, DyeColor.PURPLE, DyeColor.YELLOW),
		method_6647(TropicalFishEntity.class_1475.field_6889, DyeColor.WHITE, DyeColor.RED),
		method_6647(TropicalFishEntity.class_1475.field_6892, DyeColor.WHITE, DyeColor.YELLOW),
		method_6647(TropicalFishEntity.class_1475.field_6883, DyeColor.WHITE, DyeColor.GRAY),
		method_6647(TropicalFishEntity.class_1475.field_6889, DyeColor.WHITE, DyeColor.ORANGE),
		method_6647(TropicalFishEntity.class_1475.field_6890, DyeColor.CYAN, DyeColor.PINK),
		method_6647(TropicalFishEntity.class_1475.field_6891, DyeColor.LIME, DyeColor.LIGHT_BLUE),
		method_6647(TropicalFishEntity.class_1475.field_6888, DyeColor.RED, DyeColor.WHITE),
		method_6647(TropicalFishEntity.class_1475.field_6882, DyeColor.GRAY, DyeColor.RED),
		method_6647(TropicalFishEntity.class_1475.field_6884, DyeColor.RED, DyeColor.WHITE),
		method_6647(TropicalFishEntity.class_1475.field_6893, DyeColor.WHITE, DyeColor.YELLOW),
		method_6647(TropicalFishEntity.class_1475.field_6881, DyeColor.RED, DyeColor.WHITE),
		method_6647(TropicalFishEntity.class_1475.field_6880, DyeColor.GRAY, DyeColor.WHITE),
		method_6647(TropicalFishEntity.class_1475.field_6890, DyeColor.CYAN, DyeColor.YELLOW),
		method_6647(TropicalFishEntity.class_1475.field_6893, DyeColor.YELLOW, DyeColor.YELLOW)
	};
	private boolean field_6877 = true;

	private static int method_6647(TropicalFishEntity.class_1475 arg, DyeColor dyeColor, DyeColor dyeColor2) {
		return arg.method_6662() & 0xFF | (arg.method_6663() & 0xFF) << 8 | (dyeColor.getId() & 0xFF) << 16 | (dyeColor2.getId() & 0xFF) << 24;
	}

	public TropicalFishEntity(World world) {
		super(EntityType.TROPICAL_FISH, world);
		this.setSize(0.5F, 0.4F);
	}

	@Environment(EnvType.CLIENT)
	public static String method_6649(int i) {
		return "entity.minecraft.tropical_fish.predefined." + i;
	}

	@Environment(EnvType.CLIENT)
	public static DyeColor method_6652(int i) {
		return DyeColor.byId(method_6653(i));
	}

	@Environment(EnvType.CLIENT)
	public static DyeColor method_6651(int i) {
		return DyeColor.byId(method_6648(i));
	}

	@Environment(EnvType.CLIENT)
	public static String method_6657(int i) {
		int j = method_6656(i);
		int k = method_6645(i);
		return "entity.minecraft.tropical_fish.type." + TropicalFishEntity.class_1475.method_6660(j, k);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_6874, 0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Variant", this.getVariant());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.method_6659(compoundTag.getInt("Variant"));
	}

	public void method_6659(int i) {
		this.dataTracker.set(field_6874, i);
	}

	@Override
	public boolean method_5969(int i) {
		return !this.field_6877;
	}

	public int getVariant() {
		return this.dataTracker.get(field_6874);
	}

	@Override
	protected void method_6455(ItemStack itemStack) {
		super.method_6455(itemStack);
		CompoundTag compoundTag = itemStack.getOrCreateTag();
		compoundTag.putInt("BucketVariantTag", this.getVariant());
	}

	@Override
	protected ItemStack method_6452() {
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
	protected SoundEvent method_6457() {
		return SoundEvents.field_14878;
	}

	@Environment(EnvType.CLIENT)
	private static int method_6653(int i) {
		return (i & 0xFF0000) >> 16;
	}

	@Environment(EnvType.CLIENT)
	public float[] method_6658() {
		return DyeColor.byId(method_6653(this.getVariant())).getColorComponents();
	}

	@Environment(EnvType.CLIENT)
	private static int method_6648(int i) {
		return (i & 0xFF000000) >> 24;
	}

	@Environment(EnvType.CLIENT)
	public float[] method_6655() {
		return DyeColor.byId(method_6648(this.getVariant())).getColorComponents();
	}

	@Environment(EnvType.CLIENT)
	public static int method_6656(int i) {
		return Math.min(i & 0xFF, 1);
	}

	@Environment(EnvType.CLIENT)
	public int method_6654() {
		return method_6656(this.getVariant());
	}

	@Environment(EnvType.CLIENT)
	private static int method_6645(int i) {
		return Math.min((i & 0xFF00) >> 8, 5);
	}

	@Environment(EnvType.CLIENT)
	public Identifier method_6646() {
		return method_6656(this.getVariant()) == 0 ? field_6878[method_6645(this.getVariant())] : field_6876[method_6645(this.getVariant())];
	}

	@Environment(EnvType.CLIENT)
	public Identifier method_6650() {
		return field_6875[method_6656(this.getVariant())];
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, class_3730 arg, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, arg, entityData, compoundTag);
		if (compoundTag != null && compoundTag.containsKey("BucketVariantTag", 3)) {
			this.method_6659(compoundTag.getInt("BucketVariantTag"));
			return entityData;
		} else {
			int i;
			int j;
			int k;
			int l;
			if (entityData instanceof TropicalFishEntity.class_1476) {
				TropicalFishEntity.class_1476 lv = (TropicalFishEntity.class_1476)entityData;
				i = lv.field_6899;
				j = lv.field_6898;
				k = lv.field_6897;
				l = lv.field_6896;
			} else if ((double)this.random.nextFloat() < 0.9) {
				int m = field_6879[this.random.nextInt(field_6879.length)];
				i = m & 0xFF;
				j = (m & 0xFF00) >> 8;
				k = (m & 0xFF0000) >> 16;
				l = (m & 0xFF000000) >> 24;
				entityData = new TropicalFishEntity.class_1476(this, i, j, k, l);
			} else {
				this.field_6877 = false;
				i = this.random.nextInt(2);
				j = this.random.nextInt(6);
				k = this.random.nextInt(15);
				l = this.random.nextInt(15);
			}

			this.method_6659(i | j << 8 | k << 16 | l << 24);
			return entityData;
		}
	}

	static enum class_1475 {
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

		private final int field_6895;
		private final int field_6894;
		private static final TropicalFishEntity.class_1475[] field_6885 = values();

		private class_1475(int j, int k) {
			this.field_6895 = j;
			this.field_6894 = k;
		}

		public int method_6662() {
			return this.field_6895;
		}

		public int method_6663() {
			return this.field_6894;
		}

		@Environment(EnvType.CLIENT)
		public static String method_6660(int i, int j) {
			return field_6885[j + 6 * i].method_6661();
		}

		@Environment(EnvType.CLIENT)
		public String method_6661() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}

	static class class_1476 extends class_1425.class_1426 {
		private final int field_6899;
		private final int field_6898;
		private final int field_6897;
		private final int field_6896;

		private class_1476(TropicalFishEntity tropicalFishEntity, int i, int j, int k, int l) {
			super(tropicalFishEntity);
			this.field_6899 = i;
			this.field_6898 = j;
			this.field_6897 = k;
			this.field_6896 = l;
		}
	}
}
