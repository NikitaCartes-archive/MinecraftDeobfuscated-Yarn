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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
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
    public static final Map<Block, Item> BLOCK_ITEMS = Maps.newHashMap();
    private static final ItemPropertyGetter DAMAGED_PROPERTY_GETTER = (stack, world, entity) -> stack.isDamaged() ? 1.0f : 0.0f;
    private static final ItemPropertyGetter DAMAGE_PROPERTY_GETTER = (stack, world, entity) -> MathHelper.clamp((float)stack.getDamage() / (float)stack.getMaxDamage(), 0.0f, 1.0f);
    private static final ItemPropertyGetter LEFTHANDED_PROPERTY_GETTER = (stack, world, entity) -> entity == null || entity.getMainArm() == Arm.RIGHT ? 0.0f : 1.0f;
    private static final ItemPropertyGetter COOLDOWN_PROPERTY_GETTER = (stack, world, entity) -> entity instanceof PlayerEntity ? ((PlayerEntity)entity).getItemCooldownManager().getCooldownProgress(stack.getItem(), 0.0f) : 0.0f;
    private static final ItemPropertyGetter CUSTOM_DATA_PROPERTY_GETTER = (stack, world, entity) -> stack.hasTag() ? (float)stack.getTag().getInt("CustomModelData") : 0.0f;
    protected static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    protected static final Random RANDOM = new Random();
    private final Map<Identifier, ItemPropertyGetter> propertyGetters = Maps.newHashMap();
    protected final ItemGroup group;
    private final Rarity rarity;
    private final int maxCount;
    private final int maxDamage;
    private final boolean fireproof;
    private final Item recipeRemainder;
    @Nullable
    private String translationKey;
    @Nullable
    private final FoodComponent foodComponent;

    public static int getRawId(Item item) {
        return item == null ? 0 : Registry.ITEM.getRawId(item);
    }

    public static Item byRawId(int id) {
        return Registry.ITEM.get(id);
    }

    @Deprecated
    public static Item fromBlock(Block block) {
        return BLOCK_ITEMS.getOrDefault(block, Items.AIR);
    }

    public Item(Settings settings) {
        this.addPropertyGetter(new Identifier("lefthanded"), LEFTHANDED_PROPERTY_GETTER);
        this.addPropertyGetter(new Identifier("cooldown"), COOLDOWN_PROPERTY_GETTER);
        this.addPropertyGetter(new Identifier("custom_model_data"), CUSTOM_DATA_PROPERTY_GETTER);
        this.group = settings.group;
        this.rarity = settings.rarity;
        this.recipeRemainder = settings.recipeRemainder;
        this.maxDamage = settings.maxDamage;
        this.maxCount = settings.maxCount;
        this.foodComponent = settings.foodComponent;
        this.fireproof = settings.fireproof;
        if (this.maxDamage > 0) {
            this.addPropertyGetter(new Identifier("damaged"), DAMAGED_PROPERTY_GETTER);
            this.addPropertyGetter(new Identifier("damage"), DAMAGE_PROPERTY_GETTER);
        }
    }

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ItemPropertyGetter getPropertyGetter(Identifier id) {
        return this.propertyGetters.get(id);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasPropertyGetters() {
        return !this.propertyGetters.isEmpty();
    }

    public boolean postProcessTag(CompoundTag tag) {
        return false;
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return true;
    }

    @Override
    public Item asItem() {
        return this;
    }

    public final void addPropertyGetter(Identifier id, ItemPropertyGetter property) {
        this.propertyGetters.put(id, property);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return 1.0f;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (this.isFood()) {
            ItemStack itemStack = user.getStackInHand(hand);
            if (user.canConsume(this.getFoodComponent().isAlwaysEdible())) {
                user.setCurrentHand(hand);
                return TypedActionResult.consume(itemStack);
            }
            return TypedActionResult.fail(itemStack);
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (this.isFood()) {
            return user.eatFood(world, stack);
        }
        return stack;
    }

    public final int getMaxCount() {
        return this.maxCount;
    }

    public final int getMaxDamage() {
        return this.maxDamage;
    }

    public boolean isDamageable() {
        return this.maxDamage > 0;
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return false;
    }

    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return false;
    }

    public boolean isEffectiveOn(BlockState state) {
        return false;
    }

    public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getName() {
        return new TranslatableText(this.getTranslationKey(), new Object[0]);
    }

    public String toString() {
        return Registry.ITEM.getId(this).getPath();
    }

    protected String getOrCreateTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("item", Registry.ITEM.getId(this));
        }
        return this.translationKey;
    }

    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    public String getTranslationKey(ItemStack stack) {
        return this.getTranslationKey();
    }

    public boolean shouldSyncTagToClient() {
        return true;
    }

    @Nullable
    public final Item getRecipeRemainder() {
        return this.recipeRemainder;
    }

    public boolean hasRecipeRemainder() {
        return this.recipeRemainder != null;
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    }

    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
    }

    public boolean isNetworkSynced() {
        return false;
    }

    public UseAction getUseAction(ItemStack stack) {
        return stack.getItem().isFood() ? UseAction.EAT : UseAction.NONE;
    }

    public int getMaxUseTime(ItemStack stack) {
        if (stack.getItem().isFood()) {
            return this.getFoodComponent().isSnack() ? 16 : 32;
        }
        return 0;
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    }

    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    }

    public Text getName(ItemStack stack) {
        return new TranslatableText(this.getTranslationKey(stack), new Object[0]);
    }

    public boolean hasEnchantmentGlint(ItemStack stack) {
        return stack.hasEnchantments();
    }

    public Rarity getRarity(ItemStack stack) {
        if (!stack.hasEnchantments()) {
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

    public boolean isEnchantable(ItemStack stack) {
        return this.getMaxCount() == 1 && this.isDamageable();
    }

    protected static HitResult rayTrace(World world, PlayerEntity player, RayTraceContext.FluidHandling fluidHandling) {
        float f = player.pitch;
        float g = player.yaw;
        Vec3d vec3d = player.getCameraPosVec(1.0f);
        float h = MathHelper.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float i = MathHelper.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float j = -MathHelper.cos(-f * ((float)Math.PI / 180));
        float k = MathHelper.sin(-f * ((float)Math.PI / 180));
        float l = i * j;
        float m = k;
        float n = h * j;
        double d = 5.0;
        Vec3d vec3d2 = vec3d.add((double)l * 5.0, (double)m * 5.0, (double)n * 5.0);
        return world.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.OUTLINE, fluidHandling, player));
    }

    public int getEnchantability() {
        return 0;
    }

    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(new ItemStack(this));
        }
    }

    protected boolean isIn(ItemGroup group) {
        ItemGroup itemGroup = this.getGroup();
        return itemGroup != null && (group == ItemGroup.SEARCH || group == itemGroup);
    }

    @Nullable
    public final ItemGroup getGroup() {
        return this.group;
    }

    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot slot) {
        return HashMultimap.create();
    }

    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.getItem() == Items.CROSSBOW;
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getStackForRender() {
        return new ItemStack(this);
    }

    public boolean isIn(Tag<Item> tag) {
        return tag.contains(this);
    }

    public boolean isFood() {
        return this.foodComponent != null;
    }

    @Nullable
    public FoodComponent getFoodComponent() {
        return this.foodComponent;
    }

    public SoundEvent getDrinkSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_EAT;
    }

    public boolean isFireproof() {
        return this.fireproof;
    }

    public boolean damage(DamageSource source) {
        return !this.fireproof || !source.isFire();
    }

    public static class Settings {
        private int maxCount = 64;
        private int maxDamage;
        private Item recipeRemainder;
        private ItemGroup group;
        private Rarity rarity = Rarity.COMMON;
        private FoodComponent foodComponent;
        private boolean fireproof;

        public Settings food(FoodComponent foodComponent) {
            this.foodComponent = foodComponent;
            return this;
        }

        public Settings maxCount(int maxCount) {
            if (this.maxDamage > 0) {
                throw new RuntimeException("Unable to have damage AND stack.");
            }
            this.maxCount = maxCount;
            return this;
        }

        public Settings maxDamageIfAbsent(int maxDamage) {
            return this.maxDamage == 0 ? this.maxDamage(maxDamage) : this;
        }

        public Settings maxDamage(int maxDamage) {
            this.maxDamage = maxDamage;
            this.maxCount = 1;
            return this;
        }

        public Settings recipeRemainder(Item recipeRemainder) {
            this.recipeRemainder = recipeRemainder;
            return this;
        }

        public Settings group(ItemGroup group) {
            this.group = group;
            return this;
        }

        public Settings rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Settings fireproof() {
            this.fireproof = true;
            return this;
        }
    }
}

