/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class ItemStack {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ItemStack EMPTY = new ItemStack((ItemConvertible)null);
    public static final DecimalFormat MODIFIER_FORMAT = ItemStack.createModifierFormat();
    private int count;
    private int cooldown;
    @Deprecated
    private final Item item;
    private CompoundTag tag;
    private boolean empty;
    private ItemFrameEntity frame;
    private CachedBlockPosition lastDestroyPos;
    private boolean lastDestroyResult;
    private CachedBlockPosition lastPlaceOnPos;
    private boolean lastPlaceOnResult;

    private static DecimalFormat createModifierFormat() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        return decimalFormat;
    }

    public ItemStack(ItemConvertible itemConvertible) {
        this(itemConvertible, 1);
    }

    public ItemStack(ItemConvertible itemConvertible, int i) {
        this.item = itemConvertible == null ? null : itemConvertible.asItem();
        this.count = i;
        if (this.item != null && this.item.isDamageable()) {
            this.setDamage(this.getDamage());
        }
        this.updateEmptyState();
    }

    private void updateEmptyState() {
        this.empty = false;
        this.empty = this.isEmpty();
    }

    private ItemStack(CompoundTag compoundTag) {
        this.item = Registry.ITEM.get(new Identifier(compoundTag.getString("id")));
        this.count = compoundTag.getByte("Count");
        if (compoundTag.containsKey("tag", 10)) {
            this.tag = compoundTag.getCompound("tag");
            this.getItem().postProcessTag(compoundTag);
        }
        if (this.getItem().isDamageable()) {
            this.setDamage(this.getDamage());
        }
        this.updateEmptyState();
    }

    public static ItemStack fromTag(CompoundTag compoundTag) {
        try {
            return new ItemStack(compoundTag);
        } catch (RuntimeException runtimeException) {
            LOGGER.debug("Tried to load invalid item: {}", (Object)compoundTag, (Object)runtimeException);
            return EMPTY;
        }
    }

    public boolean isEmpty() {
        if (this == EMPTY) {
            return true;
        }
        if (this.getItem() == null || this.getItem() == Items.AIR) {
            return true;
        }
        return this.count <= 0;
    }

    public ItemStack split(int i) {
        int j = Math.min(i, this.count);
        ItemStack itemStack = this.copy();
        itemStack.setCount(j);
        this.decrement(j);
        return itemStack;
    }

    public Item getItem() {
        return this.empty ? Items.AIR : this.item;
    }

    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        PlayerEntity playerEntity = itemUsageContext.getPlayer();
        BlockPos blockPos = itemUsageContext.getBlockPos();
        CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(itemUsageContext.getWorld(), blockPos, false);
        if (playerEntity != null && !playerEntity.abilities.allowModifyWorld && !this.canPlaceOn(itemUsageContext.getWorld().getTagManager(), cachedBlockPosition)) {
            return ActionResult.PASS;
        }
        Item item = this.getItem();
        ActionResult actionResult = item.useOnBlock(itemUsageContext);
        if (playerEntity != null && actionResult == ActionResult.SUCCESS) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
        }
        return actionResult;
    }

    public float getMiningSpeed(BlockState blockState) {
        return this.getItem().getMiningSpeed(this, blockState);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        return this.getItem().use(world, playerEntity, hand);
    }

    public ItemStack finishUsing(World world, LivingEntity livingEntity) {
        return this.getItem().finishUsing(this, world, livingEntity);
    }

    public CompoundTag toTag(CompoundTag compoundTag) {
        Identifier identifier = Registry.ITEM.getId(this.getItem());
        compoundTag.putString("id", identifier == null ? "minecraft:air" : identifier.toString());
        compoundTag.putByte("Count", (byte)this.count);
        if (this.tag != null) {
            compoundTag.put("tag", this.tag.method_10553());
        }
        return compoundTag;
    }

    public int getMaxCount() {
        return this.getItem().getMaxCount();
    }

    public boolean isStackable() {
        return this.getMaxCount() > 1 && (!this.isDamageable() || !this.isDamaged());
    }

    public boolean isDamageable() {
        if (this.empty || this.getItem().getMaxDamage() <= 0) {
            return false;
        }
        CompoundTag compoundTag = this.getTag();
        return compoundTag == null || !compoundTag.getBoolean("Unbreakable");
    }

    public boolean isDamaged() {
        return this.isDamageable() && this.getDamage() > 0;
    }

    public int getDamage() {
        return this.tag == null ? 0 : this.tag.getInt("Damage");
    }

    public void setDamage(int i) {
        this.getOrCreateTag().putInt("Damage", Math.max(0, i));
    }

    public int getMaxDamage() {
        return this.getItem().getMaxDamage();
    }

    public boolean damage(int i, Random random, @Nullable ServerPlayerEntity serverPlayerEntity) {
        int j;
        if (!this.isDamageable()) {
            return false;
        }
        if (i > 0) {
            j = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, this);
            int k = 0;
            for (int l = 0; j > 0 && l < i; ++l) {
                if (!UnbreakingEnchantment.shouldPreventDamage(this, j, random)) continue;
                ++k;
            }
            if ((i -= k) <= 0) {
                return false;
            }
        }
        if (serverPlayerEntity != null && i != 0) {
            Criterions.ITEM_DURABILITY_CHANGED.handle(serverPlayerEntity, this, this.getDamage() + i);
        }
        j = this.getDamage() + i;
        this.setDamage(j);
        return j >= this.getMaxDamage();
    }

    public <T extends LivingEntity> void damage(int i, T livingEntity, Consumer<T> consumer) {
        if (livingEntity.world.isClient || livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.creativeMode) {
            return;
        }
        if (!this.isDamageable()) {
            return;
        }
        if (this.damage(i, livingEntity.getRand(), livingEntity instanceof ServerPlayerEntity ? (ServerPlayerEntity)livingEntity : null)) {
            consumer.accept(livingEntity);
            Item item = this.getItem();
            this.decrement(1);
            if (livingEntity instanceof PlayerEntity) {
                ((PlayerEntity)livingEntity).incrementStat(Stats.BROKEN.getOrCreateStat(item));
            }
            this.setDamage(0);
        }
    }

    public void postHit(LivingEntity livingEntity, PlayerEntity playerEntity) {
        Item item = this.getItem();
        if (item.postHit(this, livingEntity, playerEntity)) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
        }
    }

    public void postMine(World world, BlockState blockState, BlockPos blockPos, PlayerEntity playerEntity) {
        Item item = this.getItem();
        if (item.postMine(this, world, blockState, blockPos, playerEntity)) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
        }
    }

    public boolean isEffectiveOn(BlockState blockState) {
        return this.getItem().isEffectiveOn(blockState);
    }

    public boolean useOnEntity(PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
        return this.getItem().useOnEntity(this, playerEntity, livingEntity, hand);
    }

    public ItemStack copy() {
        ItemStack itemStack = new ItemStack(this.getItem(), this.count);
        itemStack.setCooldown(this.getCooldown());
        if (this.tag != null) {
            itemStack.tag = this.tag.method_10553();
        }
        return itemStack;
    }

    public static boolean areTagsEqual(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack.isEmpty() && itemStack2.isEmpty()) {
            return true;
        }
        if (itemStack.isEmpty() || itemStack2.isEmpty()) {
            return false;
        }
        if (itemStack.tag == null && itemStack2.tag != null) {
            return false;
        }
        return itemStack.tag == null || itemStack.tag.equals(itemStack2.tag);
    }

    public static boolean areEqualIgnoreDamage(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack.isEmpty() && itemStack2.isEmpty()) {
            return true;
        }
        if (itemStack.isEmpty() || itemStack2.isEmpty()) {
            return false;
        }
        return itemStack.isEqualIgnoreDamage(itemStack2);
    }

    private boolean isEqualIgnoreDamage(ItemStack itemStack) {
        if (this.count != itemStack.count) {
            return false;
        }
        if (this.getItem() != itemStack.getItem()) {
            return false;
        }
        if (this.tag == null && itemStack.tag != null) {
            return false;
        }
        return this.tag == null || this.tag.equals(itemStack.tag);
    }

    public static boolean areItemsEqualIgnoreDamage(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack == itemStack2) {
            return true;
        }
        if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
            return itemStack.isItemEqualIgnoreDamage(itemStack2);
        }
        return false;
    }

    public static boolean areItemsEqual(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack == itemStack2) {
            return true;
        }
        if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
            return itemStack.isItemEqual(itemStack2);
        }
        return false;
    }

    public boolean isItemEqualIgnoreDamage(ItemStack itemStack) {
        return !itemStack.isEmpty() && this.getItem() == itemStack.getItem();
    }

    public boolean isItemEqual(ItemStack itemStack) {
        if (this.isDamageable()) {
            return !itemStack.isEmpty() && this.getItem() == itemStack.getItem();
        }
        return this.isItemEqualIgnoreDamage(itemStack);
    }

    public String getTranslationKey() {
        return this.getItem().getTranslationKey(this);
    }

    public String toString() {
        return this.count + " " + this.getItem();
    }

    public void inventoryTick(World world, Entity entity, int i, boolean bl) {
        if (this.cooldown > 0) {
            --this.cooldown;
        }
        if (this.getItem() != null) {
            this.getItem().inventoryTick(this, world, entity, i, bl);
        }
    }

    public void onCraft(World world, PlayerEntity playerEntity, int i) {
        playerEntity.increaseStat(Stats.CRAFTED.getOrCreateStat(this.getItem()), i);
        this.getItem().onCraft(this, world, playerEntity);
    }

    public int getMaxUseTime() {
        return this.getItem().getMaxUseTime(this);
    }

    public UseAction getUseAction() {
        return this.getItem().getUseAction(this);
    }

    public void onStoppedUsing(World world, LivingEntity livingEntity, int i) {
        this.getItem().onStoppedUsing(this, world, livingEntity, i);
    }

    public boolean isUsedOnRelease() {
        return this.getItem().isUsedOnRelease(this);
    }

    public boolean hasTag() {
        return !this.empty && this.tag != null && !this.tag.isEmpty();
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }

    public CompoundTag getOrCreateTag() {
        if (this.tag == null) {
            this.setTag(new CompoundTag());
        }
        return this.tag;
    }

    public CompoundTag getOrCreateSubTag(String string) {
        if (this.tag == null || !this.tag.containsKey(string, 10)) {
            CompoundTag compoundTag = new CompoundTag();
            this.putSubTag(string, compoundTag);
            return compoundTag;
        }
        return this.tag.getCompound(string);
    }

    @Nullable
    public CompoundTag getSubTag(String string) {
        if (this.tag == null || !this.tag.containsKey(string, 10)) {
            return null;
        }
        return this.tag.getCompound(string);
    }

    public void removeSubTag(String string) {
        if (this.tag != null && this.tag.containsKey(string)) {
            this.tag.remove(string);
            if (this.tag.isEmpty()) {
                this.tag = null;
            }
        }
    }

    public ListTag getEnchantments() {
        if (this.tag != null) {
            return this.tag.getList("Enchantments", 10);
        }
        return new ListTag();
    }

    public void setTag(@Nullable CompoundTag compoundTag) {
        this.tag = compoundTag;
    }

    public Text getName() {
        CompoundTag compoundTag = this.getSubTag("display");
        if (compoundTag != null && compoundTag.containsKey("Name", 8)) {
            try {
                Text text = Text.Serializer.fromJson(compoundTag.getString("Name"));
                if (text != null) {
                    return text;
                }
                compoundTag.remove("Name");
            } catch (JsonParseException jsonParseException) {
                compoundTag.remove("Name");
            }
        }
        return this.getItem().getName(this);
    }

    public ItemStack setCustomName(@Nullable Text text) {
        CompoundTag compoundTag = this.getOrCreateSubTag("display");
        if (text != null) {
            compoundTag.putString("Name", Text.Serializer.toJson(text));
        } else {
            compoundTag.remove("Name");
        }
        return this;
    }

    public void removeCustomName() {
        CompoundTag compoundTag = this.getSubTag("display");
        if (compoundTag != null) {
            compoundTag.remove("Name");
            if (compoundTag.isEmpty()) {
                this.removeSubTag("display");
            }
        }
        if (this.tag != null && this.tag.isEmpty()) {
            this.tag = null;
        }
    }

    public boolean hasCustomName() {
        CompoundTag compoundTag = this.getSubTag("display");
        return compoundTag != null && compoundTag.containsKey("Name", 8);
    }

    @Environment(value=EnvType.CLIENT)
    public List<Text> getTooltip(@Nullable PlayerEntity playerEntity, TooltipContext tooltipContext) {
        int k;
        ListTag listTag2;
        ArrayList<Text> list = Lists.newArrayList();
        Text text = new LiteralText("").append(this.getName()).formatted(this.getRarity().formatting);
        if (this.hasCustomName()) {
            text.formatted(Formatting.ITALIC);
        }
        list.add(text);
        if (!tooltipContext.isAdvanced() && !this.hasCustomName() && this.getItem() == Items.FILLED_MAP) {
            list.add(new LiteralText("#" + FilledMapItem.getMapId(this)).formatted(Formatting.GRAY));
        }
        int i = 0;
        if (this.hasTag() && this.tag.containsKey("HideFlags", 99)) {
            i = this.tag.getInt("HideFlags");
        }
        if ((i & 0x20) == 0) {
            this.getItem().appendTooltip(this, playerEntity == null ? null : playerEntity.world, list, tooltipContext);
        }
        if (this.hasTag()) {
            if ((i & 1) == 0) {
                ItemStack.appendEnchantments(list, this.getEnchantments());
            }
            if (this.tag.containsKey("display", 10)) {
                CompoundTag compoundTag = this.tag.getCompound("display");
                if (compoundTag.containsKey("color", 3)) {
                    if (tooltipContext.isAdvanced()) {
                        list.add(new TranslatableText("item.color", String.format("#%06X", compoundTag.getInt("color"))).formatted(Formatting.GRAY));
                    } else {
                        list.add(new TranslatableText("item.dyed", new Object[0]).formatted(Formatting.GRAY, Formatting.ITALIC));
                    }
                }
                if (compoundTag.getType("Lore") == 9) {
                    ListTag listTag = compoundTag.getList("Lore", 8);
                    for (int j = 0; j < listTag.size(); ++j) {
                        String string = listTag.getString(j);
                        try {
                            Text text2 = Text.Serializer.fromJson(string);
                            if (text2 == null) continue;
                            list.add(Texts.setStyleIfAbsent(text2, new Style().setColor(Formatting.DARK_PURPLE).setItalic(true)));
                            continue;
                        } catch (JsonParseException jsonParseException) {
                            compoundTag.remove("Lore");
                        }
                    }
                }
            }
        }
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            Multimap<String, EntityAttributeModifier> multimap = this.getAttributeModifiers(equipmentSlot);
            if (multimap.isEmpty() || (i & 2) != 0) continue;
            list.add(new LiteralText(""));
            list.add(new TranslatableText("item.modifiers." + equipmentSlot.getName(), new Object[0]).formatted(Formatting.GRAY));
            for (Map.Entry<String, EntityAttributeModifier> entry : multimap.entries()) {
                EntityAttributeModifier entityAttributeModifier = entry.getValue();
                double d = entityAttributeModifier.getAmount();
                boolean bl = false;
                if (playerEntity != null) {
                    if (entityAttributeModifier.getId() == Item.ATTACK_DAMAGE_MODIFIER_UUID) {
                        d += playerEntity.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getBaseValue();
                        d += (double)EnchantmentHelper.getAttackDamage(this, EntityGroup.DEFAULT);
                        bl = true;
                    } else if (entityAttributeModifier.getId() == Item.ATTACK_SPEED_MODIFIER_UUID) {
                        d += playerEntity.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getBaseValue();
                        bl = true;
                    }
                }
                double e = entityAttributeModifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || entityAttributeModifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL ? d * 100.0 : d;
                if (bl) {
                    list.add(new LiteralText(" ").append(new TranslatableText("attribute.modifier.equals." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(e), new TranslatableText("attribute.name." + entry.getKey(), new Object[0]))).formatted(Formatting.DARK_GREEN));
                    continue;
                }
                if (d > 0.0) {
                    list.add(new TranslatableText("attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(e), new TranslatableText("attribute.name." + entry.getKey(), new Object[0])).formatted(Formatting.BLUE));
                    continue;
                }
                if (!(d < 0.0)) continue;
                list.add(new TranslatableText("attribute.modifier.take." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(e *= -1.0), new TranslatableText("attribute.name." + entry.getKey(), new Object[0])).formatted(Formatting.RED));
            }
        }
        if (this.hasTag() && this.getTag().getBoolean("Unbreakable") && (i & 4) == 0) {
            list.add(new TranslatableText("item.unbreakable", new Object[0]).formatted(Formatting.BLUE));
        }
        if (this.hasTag() && this.tag.containsKey("CanDestroy", 9) && (i & 8) == 0 && !(listTag2 = this.tag.getList("CanDestroy", 8)).isEmpty()) {
            list.add(new LiteralText(""));
            list.add(new TranslatableText("item.canBreak", new Object[0]).formatted(Formatting.GRAY));
            for (k = 0; k < listTag2.size(); ++k) {
                list.addAll(ItemStack.parseBlockTag(listTag2.getString(k)));
            }
        }
        if (this.hasTag() && this.tag.containsKey("CanPlaceOn", 9) && (i & 0x10) == 0 && !(listTag2 = this.tag.getList("CanPlaceOn", 8)).isEmpty()) {
            list.add(new LiteralText(""));
            list.add(new TranslatableText("item.canPlace", new Object[0]).formatted(Formatting.GRAY));
            for (k = 0; k < listTag2.size(); ++k) {
                list.addAll(ItemStack.parseBlockTag(listTag2.getString(k)));
            }
        }
        if (tooltipContext.isAdvanced()) {
            if (this.isDamaged()) {
                list.add(new TranslatableText("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
            }
            list.add(new LiteralText(Registry.ITEM.getId(this.getItem()).toString()).formatted(Formatting.DARK_GRAY));
            if (this.hasTag()) {
                list.add(new TranslatableText("item.nbt_tags", this.getTag().getKeys().size()).formatted(Formatting.DARK_GRAY));
            }
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendEnchantments(List<Text> list, ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompoundTag(i);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(compoundTag.getString("id"))).ifPresent(enchantment -> list.add(enchantment.getName(compoundTag.getInt("lvl"))));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static Collection<Text> parseBlockTag(String string) {
        try {
            boolean bl2;
            BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), true).parse(true);
            BlockState blockState = blockArgumentParser.getBlockState();
            Identifier identifier = blockArgumentParser.getTagId();
            boolean bl = blockState != null;
            boolean bl3 = bl2 = identifier != null;
            if (bl || bl2) {
                Collection<Block> collection;
                if (bl) {
                    return Lists.newArrayList(blockState.getBlock().getName().formatted(Formatting.DARK_GRAY));
                }
                net.minecraft.tag.Tag<Block> tag = BlockTags.getContainer().get(identifier);
                if (tag != null && !(collection = tag.values()).isEmpty()) {
                    return collection.stream().map(Block::getName).map(text -> text.formatted(Formatting.DARK_GRAY)).collect(Collectors.toList());
                }
            }
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return Lists.newArrayList(new LiteralText("missingno").formatted(Formatting.DARK_GRAY));
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasEnchantmentGlint() {
        return this.getItem().hasEnchantmentGlint(this);
    }

    public Rarity getRarity() {
        return this.getItem().getRarity(this);
    }

    public boolean isEnchantable() {
        if (!this.getItem().isEnchantable(this)) {
            return false;
        }
        return !this.hasEnchantments();
    }

    public void addEnchantment(Enchantment enchantment, int i) {
        this.getOrCreateTag();
        if (!this.tag.containsKey("Enchantments", 9)) {
            this.tag.put("Enchantments", new ListTag());
        }
        ListTag listTag = this.tag.getList("Enchantments", 10);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
        compoundTag.putShort("lvl", (byte)i);
        listTag.add(compoundTag);
    }

    public boolean hasEnchantments() {
        if (this.tag != null && this.tag.containsKey("Enchantments", 9)) {
            return !this.tag.getList("Enchantments", 10).isEmpty();
        }
        return false;
    }

    public void putSubTag(String string, Tag tag) {
        this.getOrCreateTag().put(string, tag);
    }

    public boolean isInFrame() {
        return this.frame != null;
    }

    public void setFrame(@Nullable ItemFrameEntity itemFrameEntity) {
        this.frame = itemFrameEntity;
    }

    @Nullable
    public ItemFrameEntity getFrame() {
        return this.empty ? null : this.frame;
    }

    public int getRepairCost() {
        if (this.hasTag() && this.tag.containsKey("RepairCost", 3)) {
            return this.tag.getInt("RepairCost");
        }
        return 0;
    }

    public void setRepairCost(int i) {
        this.getOrCreateTag().putInt("RepairCost", i);
    }

    public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
        Multimap<String, EntityAttributeModifier> multimap;
        if (this.hasTag() && this.tag.containsKey("AttributeModifiers", 9)) {
            multimap = HashMultimap.create();
            ListTag listTag = this.tag.getList("AttributeModifiers", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag = listTag.getCompoundTag(i);
                EntityAttributeModifier entityAttributeModifier2 = EntityAttributes.createFromTag(compoundTag);
                if (entityAttributeModifier2 == null || compoundTag.containsKey("Slot", 8) && !compoundTag.getString("Slot").equals(equipmentSlot.getName()) || entityAttributeModifier2.getId().getLeastSignificantBits() == 0L || entityAttributeModifier2.getId().getMostSignificantBits() == 0L) continue;
                multimap.put(compoundTag.getString("AttributeName"), entityAttributeModifier2);
            }
        } else {
            multimap = this.getItem().getModifiers(equipmentSlot);
        }
        multimap.values().forEach(entityAttributeModifier -> entityAttributeModifier.setSerialize(false));
        return multimap;
    }

    public void addAttributeModifier(String string, EntityAttributeModifier entityAttributeModifier, @Nullable EquipmentSlot equipmentSlot) {
        this.getOrCreateTag();
        if (!this.tag.containsKey("AttributeModifiers", 9)) {
            this.tag.put("AttributeModifiers", new ListTag());
        }
        ListTag listTag = this.tag.getList("AttributeModifiers", 10);
        CompoundTag compoundTag = EntityAttributes.toTag(entityAttributeModifier);
        compoundTag.putString("AttributeName", string);
        if (equipmentSlot != null) {
            compoundTag.putString("Slot", equipmentSlot.getName());
        }
        listTag.add(compoundTag);
    }

    public Text toHoverableText() {
        Text text = new LiteralText("").append(this.getName());
        if (this.hasCustomName()) {
            text.formatted(Formatting.ITALIC);
        }
        Text text2 = Texts.bracketed(text);
        if (!this.empty) {
            CompoundTag compoundTag = this.toTag(new CompoundTag());
            text2.formatted(this.getRarity().formatting).styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new LiteralText(compoundTag.toString()))));
        }
        return text2;
    }

    private static boolean areBlocksEqual(CachedBlockPosition cachedBlockPosition, @Nullable CachedBlockPosition cachedBlockPosition2) {
        if (cachedBlockPosition2 == null || cachedBlockPosition.getBlockState() != cachedBlockPosition2.getBlockState()) {
            return false;
        }
        if (cachedBlockPosition.getBlockEntity() == null && cachedBlockPosition2.getBlockEntity() == null) {
            return true;
        }
        if (cachedBlockPosition.getBlockEntity() == null || cachedBlockPosition2.getBlockEntity() == null) {
            return false;
        }
        return Objects.equals(cachedBlockPosition.getBlockEntity().toTag(new CompoundTag()), cachedBlockPosition2.getBlockEntity().toTag(new CompoundTag()));
    }

    public boolean canDestroy(RegistryTagManager registryTagManager, CachedBlockPosition cachedBlockPosition) {
        if (ItemStack.areBlocksEqual(cachedBlockPosition, this.lastDestroyPos)) {
            return this.lastDestroyResult;
        }
        this.lastDestroyPos = cachedBlockPosition;
        if (this.hasTag() && this.tag.containsKey("CanDestroy", 9)) {
            ListTag listTag = this.tag.getList("CanDestroy", 8);
            for (int i = 0; i < listTag.size(); ++i) {
                String string = listTag.getString(i);
                try {
                    Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().method_9642(new StringReader(string)).create(registryTagManager);
                    if (predicate.test(cachedBlockPosition)) {
                        this.lastDestroyResult = true;
                        return true;
                    }
                    continue;
                } catch (CommandSyntaxException commandSyntaxException) {
                    // empty catch block
                }
            }
        }
        this.lastDestroyResult = false;
        return false;
    }

    public boolean canPlaceOn(RegistryTagManager registryTagManager, CachedBlockPosition cachedBlockPosition) {
        if (ItemStack.areBlocksEqual(cachedBlockPosition, this.lastPlaceOnPos)) {
            return this.lastPlaceOnResult;
        }
        this.lastPlaceOnPos = cachedBlockPosition;
        if (this.hasTag() && this.tag.containsKey("CanPlaceOn", 9)) {
            ListTag listTag = this.tag.getList("CanPlaceOn", 8);
            for (int i = 0; i < listTag.size(); ++i) {
                String string = listTag.getString(i);
                try {
                    Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().method_9642(new StringReader(string)).create(registryTagManager);
                    if (predicate.test(cachedBlockPosition)) {
                        this.lastPlaceOnResult = true;
                        return true;
                    }
                    continue;
                } catch (CommandSyntaxException commandSyntaxException) {
                    // empty catch block
                }
            }
        }
        this.lastPlaceOnResult = false;
        return false;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int i) {
        this.cooldown = i;
    }

    public int getCount() {
        return this.empty ? 0 : this.count;
    }

    public void setCount(int i) {
        this.count = i;
        this.updateEmptyState();
    }

    public void increment(int i) {
        this.setCount(this.count + i);
    }

    public void decrement(int i) {
        this.increment(-i);
    }

    public void usageTick(World world, LivingEntity livingEntity, int i) {
        this.getItem().usageTick(world, livingEntity, this, i);
    }

    public boolean isFood() {
        return this.getItem().isFood();
    }

    public SoundEvent getDrinkSound() {
        return this.getItem().getDrinkSound();
    }

    public SoundEvent getEatSound() {
        return this.getItem().getEatSound();
    }
}

