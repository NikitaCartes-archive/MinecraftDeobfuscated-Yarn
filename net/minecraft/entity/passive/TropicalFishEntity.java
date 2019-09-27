/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.SchoolingFishEntity;
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
import org.jetbrains.annotations.Nullable;

public class TropicalFishEntity
extends SchoolingFishEntity {
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(TropicalFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final Identifier[] SHAPE_IDS = new Identifier[]{new Identifier("textures/entity/fish/tropical_a.png"), new Identifier("textures/entity/fish/tropical_b.png")};
    private static final Identifier[] SMALL_FISH_VARIETY_IDS = new Identifier[]{new Identifier("textures/entity/fish/tropical_a_pattern_1.png"), new Identifier("textures/entity/fish/tropical_a_pattern_2.png"), new Identifier("textures/entity/fish/tropical_a_pattern_3.png"), new Identifier("textures/entity/fish/tropical_a_pattern_4.png"), new Identifier("textures/entity/fish/tropical_a_pattern_5.png"), new Identifier("textures/entity/fish/tropical_a_pattern_6.png")};
    private static final Identifier[] LARGE_FISH_VARIETY_IDS = new Identifier[]{new Identifier("textures/entity/fish/tropical_b_pattern_1.png"), new Identifier("textures/entity/fish/tropical_b_pattern_2.png"), new Identifier("textures/entity/fish/tropical_b_pattern_3.png"), new Identifier("textures/entity/fish/tropical_b_pattern_4.png"), new Identifier("textures/entity/fish/tropical_b_pattern_5.png"), new Identifier("textures/entity/fish/tropical_b_pattern_6.png")};
    public static final int[] COMMON_VARIANTS = new int[]{TropicalFishEntity.toVariant(Variety.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), TropicalFishEntity.toVariant(Variety.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), TropicalFishEntity.toVariant(Variety.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), TropicalFishEntity.toVariant(Variety.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), TropicalFishEntity.toVariant(Variety.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), TropicalFishEntity.toVariant(Variety.KOB, DyeColor.ORANGE, DyeColor.WHITE), TropicalFishEntity.toVariant(Variety.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), TropicalFishEntity.toVariant(Variety.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), TropicalFishEntity.toVariant(Variety.CLAYFISH, DyeColor.WHITE, DyeColor.RED), TropicalFishEntity.toVariant(Variety.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), TropicalFishEntity.toVariant(Variety.GLITTER, DyeColor.WHITE, DyeColor.GRAY), TropicalFishEntity.toVariant(Variety.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), TropicalFishEntity.toVariant(Variety.DASHER, DyeColor.CYAN, DyeColor.PINK), TropicalFishEntity.toVariant(Variety.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), TropicalFishEntity.toVariant(Variety.BETTY, DyeColor.RED, DyeColor.WHITE), TropicalFishEntity.toVariant(Variety.SNOOPER, DyeColor.GRAY, DyeColor.RED), TropicalFishEntity.toVariant(Variety.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), TropicalFishEntity.toVariant(Variety.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), TropicalFishEntity.toVariant(Variety.KOB, DyeColor.RED, DyeColor.WHITE), TropicalFishEntity.toVariant(Variety.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), TropicalFishEntity.toVariant(Variety.DASHER, DyeColor.CYAN, DyeColor.YELLOW), TropicalFishEntity.toVariant(Variety.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)};
    private boolean commonSpawn = true;

    private static int toVariant(Variety variety, DyeColor dyeColor, DyeColor dyeColor2) {
        return variety.getShape() & 0xFF | (variety.getPattern() & 0xFF) << 8 | (dyeColor.getId() & 0xFF) << 16 | (dyeColor2.getId() & 0xFF) << 24;
    }

    public TropicalFishEntity(EntityType<? extends TropicalFishEntity> entityType, World world) {
        super((EntityType<? extends SchoolingFishEntity>)entityType, world);
    }

    @Environment(value=EnvType.CLIENT)
    public static String getToolTipForVariant(int i) {
        return "entity.minecraft.tropical_fish.predefined." + i;
    }

    @Environment(value=EnvType.CLIENT)
    public static DyeColor getBaseDyeColor(int i) {
        return DyeColor.byId(TropicalFishEntity.getBaseDyeColorIndex(i));
    }

    @Environment(value=EnvType.CLIENT)
    public static DyeColor getPatternDyeColor(int i) {
        return DyeColor.byId(TropicalFishEntity.getPatternDyeColorIndex(i));
    }

    @Environment(value=EnvType.CLIENT)
    public static String getTranslationKey(int i) {
        int j = TropicalFishEntity.getShape(i);
        int k = TropicalFishEntity.getPattern(i);
        return "entity.minecraft.tropical_fish.type." + Variety.getTranslateKey(j, k);
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
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_TROPICAL_FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_TROPICAL_FISH_FLOP;
    }

    @Environment(value=EnvType.CLIENT)
    private static int getBaseDyeColorIndex(int i) {
        return (i & 0xFF0000) >> 16;
    }

    @Environment(value=EnvType.CLIENT)
    public float[] getBaseColorComponents() {
        return DyeColor.byId(TropicalFishEntity.getBaseDyeColorIndex(this.getVariant())).getColorComponents();
    }

    @Environment(value=EnvType.CLIENT)
    private static int getPatternDyeColorIndex(int i) {
        return (i & 0xFF000000) >> 24;
    }

    @Environment(value=EnvType.CLIENT)
    public float[] getPatternColorComponents() {
        return DyeColor.byId(TropicalFishEntity.getPatternDyeColorIndex(this.getVariant())).getColorComponents();
    }

    @Environment(value=EnvType.CLIENT)
    public static int getShape(int i) {
        return Math.min(i & 0xFF, 1);
    }

    @Environment(value=EnvType.CLIENT)
    public int getShape() {
        return TropicalFishEntity.getShape(this.getVariant());
    }

    @Environment(value=EnvType.CLIENT)
    private static int getPattern(int i) {
        return Math.min((i & 0xFF00) >> 8, 5);
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getVarietyId() {
        if (TropicalFishEntity.getShape(this.getVariant()) == 0) {
            return SMALL_FISH_VARIETY_IDS[TropicalFishEntity.getPattern(this.getVariant())];
        }
        return LARGE_FISH_VARIETY_IDS[TropicalFishEntity.getPattern(this.getVariant())];
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getShapeId() {
        return SHAPE_IDS[TropicalFishEntity.getShape(this.getVariant())];
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag) {
        int l;
        int k;
        int j;
        int i;
        entityData = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
        if (compoundTag != null && compoundTag.contains("BucketVariantTag", 3)) {
            this.setVariant(compoundTag.getInt("BucketVariantTag"));
            return entityData;
        }
        if (entityData instanceof Data) {
            Data data = (Data)entityData;
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
            entityData = new Data(this, i, j, k, l);
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

    static class Data
    extends SchoolingFishEntity.Data {
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
        private static final Variety[] VALUES;

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

        @Environment(value=EnvType.CLIENT)
        public static String getTranslateKey(int i, int j) {
            return VALUES[j + 6 * i].getTranslationKey();
        }

        @Environment(value=EnvType.CLIENT)
        public String getTranslationKey() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        static {
            VALUES = Variety.values();
        }
    }
}

