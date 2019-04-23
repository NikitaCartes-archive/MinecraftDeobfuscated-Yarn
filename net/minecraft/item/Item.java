/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodItemSetting;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tag.Tag;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Item
implements ItemConvertible {
    public static final Map<Block, Item> BLOCK_ITEM_MAP = Maps.newHashMap();
    private static final ItemPropertyGetter GETTER_DAMAGED = (itemStack, world, livingEntity) -> itemStack.isDamaged() ? 1.0f : 0.0f;
    private static final ItemPropertyGetter GETTER_DAMAGE = (itemStack, world, livingEntity) -> MathHelper.clamp((float)itemStack.getDamage() / (float)itemStack.getDurability(), 0.0f, 1.0f);
    private static final ItemPropertyGetter GETTER_HAND = (itemStack, world, livingEntity) -> livingEntity == null || livingEntity.getMainHand() == AbsoluteHand.RIGHT ? 0.0f : 1.0f;
    private static final ItemPropertyGetter GETTER_COOLDOWN = (itemStack, world, livingEntity) -> livingEntity instanceof PlayerEntity ? ((PlayerEntity)livingEntity).getItemCooldownManager().getCooldownProgress(itemStack.getItem(), 0.0f) : 0.0f;
    private static final ItemPropertyGetter GETTER_CUSTOM_MODEL_DATA = (itemStack, world, livingEntity) -> itemStack.hasTag() ? (float)itemStack.getTag().getInt("CustomModelData") : 0.0f;
    protected static final UUID MODIFIER_DAMAGE = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID MODIFIER_SWING_SPEED = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    protected static final Random random = new Random();
    private final Map<Identifier, ItemPropertyGetter> PROPERTIES = Maps.newHashMap();
    protected final ItemGroup itemGroup;
    private final Rarity rarity;
    private final int fullStackSize;
    private final int durability;
    private final Item recipeRemainder;
    @Nullable
    private String translationKey;
    @Nullable
    private final FoodItemSetting foodSetting;

    public static int getRawIdByItem(Item item) {
        return item == null ? 0 : Registry.ITEM.getRawId(item);
    }

    public static Item byRawId(int i) {
        return Registry.ITEM.get(i);
    }

    @Deprecated
    public static Item getItemFromBlock(Block block) {
        return BLOCK_ITEM_MAP.getOrDefault(block, Items.AIR);
    }

    public Item(Settings settings) {
        this.addProperty(new Identifier("lefthanded"), GETTER_HAND);
        this.addProperty(new Identifier("cooldown"), GETTER_COOLDOWN);
        this.addProperty(new Identifier("custom_model_data"), GETTER_CUSTOM_MODEL_DATA);
        this.itemGroup = settings.itemGroup;
        this.rarity = settings.rarity;
        this.recipeRemainder = settings.recipeRemainder;
        this.durability = settings.durability;
        this.fullStackSize = settings.fullStackSize;
        this.foodSetting = settings.foodSetting;
        if (this.durability > 0) {
            this.addProperty(new Identifier("damaged"), GETTER_DAMAGED);
            this.addProperty(new Identifier("damage"), GETTER_DAMAGE);
        }
    }

    public void onUsingTick(World world, LivingEntity livingEntity, ItemStack itemStack, int i) {
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ItemPropertyGetter getProperty(Identifier identifier) {
        return this.PROPERTIES.get(identifier);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasProperties() {
        return !this.PROPERTIES.isEmpty();
    }

    public boolean onTagDeserialized(CompoundTag compoundTag) {
        return false;
    }

    public boolean beforeBlockBreak(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public Item asItem() {
        return this;
    }

    public final void addProperty(Identifier identifier, ItemPropertyGetter itemPropertyGetter) {
        this.PROPERTIES.put(identifier, itemPropertyGetter);
    }

    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        return ActionResult.PASS;
    }

    public float getBlockBreakingSpeed(ItemStack itemStack, BlockState blockState) {
        return 1.0f;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        if (this.isFood()) {
            ItemStack itemStack = playerEntity.getStackInHand(hand);
            if (playerEntity.canConsume(this.getFoodSetting().isAlwaysEdible())) {
                playerEntity.setCurrentHand(hand);
                return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack);
            }
            return new TypedActionResult<ItemStack>(ActionResult.FAIL, itemStack);
        }
        return new TypedActionResult<ItemStack>(ActionResult.PASS, playerEntity.getStackInHand(hand));
    }

    public ItemStack onItemFinishedUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
        if (this.isFood()) {
            return livingEntity.eatFood(world, itemStack);
        }
        return itemStack;
    }

    public final int getMaxAmount() {
        return this.fullStackSize;
    }

    public final int getDurability() {
        return this.durability;
    }

    public boolean canDamage() {
        return this.durability > 0;
    }

    public boolean onEntityDamaged(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
        return false;
    }

    public boolean onBlockBroken(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
        return false;
    }

    public boolean isEffectiveOn(BlockState blockState) {
        return false;
    }

    public boolean interactWithEntity(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public Component getTextComponent() {
        return new TranslatableComponent(this.getTranslationKey(), new Object[0]);
    }

    protected String getOrCreateTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = SystemUtil.createTranslationKey("item", Registry.ITEM.getId(this));
        }
        return this.translationKey;
    }

    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    public String getTranslationKey(ItemStack itemStack) {
        return this.getTranslationKey();
    }

    public boolean requiresClientSync() {
        return true;
    }

    @Nullable
    public final Item getRecipeRemainder() {
        return this.recipeRemainder;
    }

    public boolean hasRecipeRemainder() {
        return this.recipeRemainder != null;
    }

    public void onEntityTick(ItemStack itemStack, World world, Entity entity, int i, boolean bl) {
    }

    public void onCrafted(ItemStack itemStack, World world, PlayerEntity playerEntity) {
    }

    public boolean isMap() {
        return false;
    }

    public UseAction getUseAction(ItemStack itemStack) {
        return itemStack.getItem().isFood() ? UseAction.EAT : UseAction.NONE;
    }

    public int getMaxUseTime(ItemStack itemStack) {
        if (itemStack.getItem().isFood()) {
            return this.getFoodSetting().isEatenFast() ? 16 : 32;
        }
        return 0;
    }

    public void onItemStopUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
    }

    @Environment(value=EnvType.CLIENT)
    public void buildTooltip(ItemStack itemStack, @Nullable World world, List<Component> list, TooltipContext tooltipContext) {
    }

    public Component getTranslatedNameTrimmed(ItemStack itemStack) {
        return new TranslatableComponent(this.getTranslationKey(itemStack), new Object[0]);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasEnchantmentGlint(ItemStack itemStack) {
        return itemStack.hasEnchantments();
    }

    public Rarity getRarity(ItemStack itemStack) {
        if (!itemStack.hasEnchantments()) {
            return this.rarity;
        }
        switch (this.rarity) {
            case COMMON: 
            case UNCOMMON: {
                return Rarity.RARE;
            }
            case RARE: {
                return Rarity.EPIC;
            }
        }
        return this.rarity;
    }

    public boolean isTool(ItemStack itemStack) {
        return this.getMaxAmount() == 1 && this.canDamage();
    }

    protected static HitResult getHitResult(World world, PlayerEntity playerEntity, RayTraceContext.FluidHandling fluidHandling) {
        float f = playerEntity.pitch;
        float g = playerEntity.yaw;
        Vec3d vec3d = playerEntity.getCameraPosVec(1.0f);
        float h = MathHelper.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float i = MathHelper.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float j = -MathHelper.cos(-f * ((float)Math.PI / 180));
        float k = MathHelper.sin(-f * ((float)Math.PI / 180));
        float l = i * j;
        float m = k;
        float n = h * j;
        double d = 5.0;
        Vec3d vec3d2 = vec3d.add((double)l * 5.0, (double)m * 5.0, (double)n * 5.0);
        return world.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.OUTLINE, fluidHandling, playerEntity));
    }

    public int getEnchantability() {
        return 0;
    }

    public void appendItemsForGroup(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
        if (this.isInItemGroup(itemGroup)) {
            defaultedList.add(new ItemStack(this));
        }
    }

    protected boolean isInItemGroup(ItemGroup itemGroup) {
        ItemGroup itemGroup2 = this.getItemGroup();
        return itemGroup2 != null && (itemGroup == ItemGroup.SEARCH || itemGroup == itemGroup2);
    }

    @Nullable
    public final ItemGroup getItemGroup() {
        return this.itemGroup;
    }

    public boolean canRepair(ItemStack itemStack, ItemStack itemStack2) {
        return false;
    }

    public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
        return HashMultimap.create();
    }

    public boolean method_7838(ItemStack itemStack) {
        return itemStack.getItem() == Items.CROSSBOW;
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getDefaultStack() {
        return new ItemStack(this);
    }

    public boolean matches(Tag<Item> tag) {
        return tag.contains(this);
    }

    public boolean isFood() {
        return this.foodSetting != null;
    }

    @Nullable
    public FoodItemSetting getFoodSetting() {
        return this.foodSetting;
    }

    public static class Settings {
        private int fullStackSize = 64;
        private int durability;
        private Item recipeRemainder;
        private ItemGroup itemGroup;
        private Rarity rarity = Rarity.COMMON;
        private FoodItemSetting foodSetting;

        public Settings food(FoodItemSetting foodItemSetting) {
            this.foodSetting = foodItemSetting;
            return this;
        }

        public Settings stackSize(int i) {
            if (this.durability > 0) {
                throw new RuntimeException("Unable to have damage AND stack.");
            }
            this.fullStackSize = i;
            return this;
        }

        public Settings durabilityIfNotSet(int i) {
            return this.durability == 0 ? this.durability(i) : this;
        }

        public Settings durability(int i) {
            this.durability = i;
            this.fullStackSize = 1;
            return this;
        }

        public Settings recipeRemainder(Item item) {
            this.recipeRemainder = item;
            return this;
        }

        public Settings itemGroup(ItemGroup itemGroup) {
            this.itemGroup = itemGroup;
            return this;
        }

        public Settings rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }
    }
}

