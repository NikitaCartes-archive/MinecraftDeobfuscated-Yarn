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

    public ItemStack(ItemConvertible item) {
        this(item, 1);
    }

    public ItemStack(ItemConvertible item, int count) {
        this.item = item == null ? null : item.asItem();
        this.count = count;
        if (this.item != null && this.item.isDamageable()) {
            this.setDamage(this.getDamage());
        }
        this.updateEmptyState();
    }

    private void updateEmptyState() {
        this.empty = false;
        this.empty = this.isEmpty();
    }

    private ItemStack(CompoundTag tag) {
        this.item = Registry.ITEM.get(new Identifier(tag.getString("id")));
        this.count = tag.getByte("Count");
        if (tag.contains("tag", 10)) {
            this.tag = tag.getCompound("tag");
            this.getItem().postProcessTag(tag);
        }
        if (this.getItem().isDamageable()) {
            this.setDamage(this.getDamage());
        }
        this.updateEmptyState();
    }

    public static ItemStack fromTag(CompoundTag tag) {
        try {
            return new ItemStack(tag);
        } catch (RuntimeException runtimeException) {
            LOGGER.debug("Tried to load invalid item: {}", (Object)tag, (Object)runtimeException);
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

    public ItemStack split(int amount) {
        int i = Math.min(amount, this.count);
        ItemStack itemStack = this.copy();
        itemStack.setCount(i);
        this.decrement(i);
        return itemStack;
    }

    public Item getItem() {
        return this.empty ? Items.AIR : this.item;
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        BlockPos blockPos = context.getBlockPos();
        CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(context.getWorld(), blockPos, false);
        if (playerEntity != null && !playerEntity.abilities.allowModifyWorld && !this.canPlaceOn(context.getWorld().getTagManager(), cachedBlockPosition)) {
            return ActionResult.PASS;
        }
        Item item = this.getItem();
        ActionResult actionResult = item.useOnBlock(context);
        if (playerEntity != null && actionResult == ActionResult.SUCCESS) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
        }
        return actionResult;
    }

    public float getMiningSpeed(BlockState state) {
        return this.getItem().getMiningSpeed(this, state);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return this.getItem().use(world, user, hand);
    }

    public ItemStack finishUsing(World world, LivingEntity user) {
        return this.getItem().finishUsing(this, world, user);
    }

    public CompoundTag toTag(CompoundTag tag) {
        Identifier identifier = Registry.ITEM.getId(this.getItem());
        tag.putString("id", identifier == null ? "minecraft:air" : identifier.toString());
        tag.putByte("Count", (byte)this.count);
        if (this.tag != null) {
            tag.put("tag", this.tag.copy());
        }
        return tag;
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

    public void setDamage(int damage) {
        this.getOrCreateTag().putInt("Damage", Math.max(0, damage));
    }

    public int getMaxDamage() {
        return this.getItem().getMaxDamage();
    }

    public boolean damage(int amount, Random random, @Nullable ServerPlayerEntity player) {
        int i;
        if (!this.isDamageable()) {
            return false;
        }
        if (amount > 0) {
            i = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, this);
            int j = 0;
            for (int k = 0; i > 0 && k < amount; ++k) {
                if (!UnbreakingEnchantment.shouldPreventDamage(this, i, random)) continue;
                ++j;
            }
            if ((amount -= j) <= 0) {
                return false;
            }
        }
        if (player != null && amount != 0) {
            Criterions.ITEM_DURABILITY_CHANGED.trigger(player, this, this.getDamage() + amount);
        }
        i = this.getDamage() + amount;
        this.setDamage(i);
        return i >= this.getMaxDamage();
    }

    public <T extends LivingEntity> void damage(int amount, T entity, Consumer<T> breakCallback) {
        if (entity.world.isClient || entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.creativeMode) {
            return;
        }
        if (!this.isDamageable()) {
            return;
        }
        if (this.damage(amount, entity.getRandom(), entity instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity : null)) {
            breakCallback.accept(entity);
            Item item = this.getItem();
            this.decrement(1);
            if (entity instanceof PlayerEntity) {
                ((PlayerEntity)entity).incrementStat(Stats.BROKEN.getOrCreateStat(item));
            }
            this.setDamage(0);
        }
    }

    public void postHit(LivingEntity target, PlayerEntity attacker) {
        Item item = this.getItem();
        if (item.postHit(this, target, attacker)) {
            attacker.incrementStat(Stats.USED.getOrCreateStat(item));
        }
    }

    public void postMine(World world, BlockState state, BlockPos pos, PlayerEntity miner) {
        Item item = this.getItem();
        if (item.postMine(this, world, state, pos, miner)) {
            miner.incrementStat(Stats.USED.getOrCreateStat(item));
        }
    }

    public boolean isEffectiveOn(BlockState state) {
        return this.getItem().isEffectiveOn(state);
    }

    public boolean useOnEntity(PlayerEntity user, LivingEntity entity, Hand hand) {
        return this.getItem().useOnEntity(this, user, entity, hand);
    }

    public ItemStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        }
        ItemStack itemStack = new ItemStack(this.getItem(), this.count);
        itemStack.setCooldown(this.getCooldown());
        if (this.tag != null) {
            itemStack.tag = this.tag.copy();
        }
        return itemStack;
    }

    public static boolean areTagsEqual(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        if (left.tag == null && right.tag != null) {
            return false;
        }
        return left.tag == null || left.tag.equals(right.tag);
    }

    public static boolean areEqualIgnoreDamage(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        return left.isEqualIgnoreDamage(right);
    }

    private boolean isEqualIgnoreDamage(ItemStack stack) {
        if (this.count != stack.count) {
            return false;
        }
        if (this.getItem() != stack.getItem()) {
            return false;
        }
        if (this.tag == null && stack.tag != null) {
            return false;
        }
        return this.tag == null || this.tag.equals(stack.tag);
    }

    public static boolean areItemsEqualIgnoreDamage(ItemStack left, ItemStack right) {
        if (left == right) {
            return true;
        }
        if (!left.isEmpty() && !right.isEmpty()) {
            return left.isItemEqualIgnoreDamage(right);
        }
        return false;
    }

    public static boolean areItemsEqual(ItemStack left, ItemStack right) {
        if (left == right) {
            return true;
        }
        if (!left.isEmpty() && !right.isEmpty()) {
            return left.isItemEqual(right);
        }
        return false;
    }

    public boolean isItemEqualIgnoreDamage(ItemStack stack) {
        return !stack.isEmpty() && this.getItem() == stack.getItem();
    }

    public boolean isItemEqual(ItemStack stack) {
        if (this.isDamageable()) {
            return !stack.isEmpty() && this.getItem() == stack.getItem();
        }
        return this.isItemEqualIgnoreDamage(stack);
    }

    public String getTranslationKey() {
        return this.getItem().getTranslationKey(this);
    }

    public String toString() {
        return this.count + " " + this.getItem();
    }

    public void inventoryTick(World world, Entity entity, int slot, boolean selected) {
        if (this.cooldown > 0) {
            --this.cooldown;
        }
        if (this.getItem() != null) {
            this.getItem().inventoryTick(this, world, entity, slot, selected);
        }
    }

    public void onCraft(World world, PlayerEntity player, int amount) {
        player.increaseStat(Stats.CRAFTED.getOrCreateStat(this.getItem()), amount);
        this.getItem().onCraft(this, world, player);
    }

    public int getMaxUseTime() {
        return this.getItem().getMaxUseTime(this);
    }

    public UseAction getUseAction() {
        return this.getItem().getUseAction(this);
    }

    public void onStoppedUsing(World world, LivingEntity user, int remainingUseTicks) {
        this.getItem().onStoppedUsing(this, world, user, remainingUseTicks);
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

    public CompoundTag getOrCreateSubTag(String key) {
        if (this.tag == null || !this.tag.contains(key, 10)) {
            CompoundTag compoundTag = new CompoundTag();
            this.putSubTag(key, compoundTag);
            return compoundTag;
        }
        return this.tag.getCompound(key);
    }

    @Nullable
    public CompoundTag getSubTag(String key) {
        if (this.tag == null || !this.tag.contains(key, 10)) {
            return null;
        }
        return this.tag.getCompound(key);
    }

    public void removeSubTag(String key) {
        if (this.tag != null && this.tag.contains(key)) {
            this.tag.remove(key);
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

    public void setTag(@Nullable CompoundTag tag) {
        this.tag = tag;
        if (this.getItem().isDamageable()) {
            this.setDamage(this.getDamage());
        }
    }

    public Text getName() {
        CompoundTag compoundTag = this.getSubTag("display");
        if (compoundTag != null && compoundTag.contains("Name", 8)) {
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

    public ItemStack setCustomName(@Nullable Text name) {
        CompoundTag compoundTag = this.getOrCreateSubTag("display");
        if (name != null) {
            compoundTag.putString("Name", Text.Serializer.toJson(name));
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
        return compoundTag != null && compoundTag.contains("Name", 8);
    }

    @Environment(value=EnvType.CLIENT)
    public List<Text> getTooltip(@Nullable PlayerEntity player, TooltipContext context) {
        int k;
        ListTag listTag2;
        ArrayList<Text> list = Lists.newArrayList();
        Text text = new LiteralText("").append(this.getName()).formatted(this.getRarity().formatting);
        if (this.hasCustomName()) {
            text.formatted(Formatting.ITALIC);
        }
        list.add(text);
        if (!context.isAdvanced() && !this.hasCustomName() && this.getItem() == Items.FILLED_MAP) {
            list.add(new LiteralText("#" + FilledMapItem.getMapId(this)).formatted(Formatting.GRAY));
        }
        int i = 0;
        if (this.hasTag() && this.tag.contains("HideFlags", 99)) {
            i = this.tag.getInt("HideFlags");
        }
        if ((i & 0x20) == 0) {
            this.getItem().appendTooltip(this, player == null ? null : player.world, list, context);
        }
        if (this.hasTag()) {
            if ((i & 1) == 0) {
                ItemStack.appendEnchantments(list, this.getEnchantments());
            }
            if (this.tag.contains("display", 10)) {
                CompoundTag compoundTag = this.tag.getCompound("display");
                if (compoundTag.contains("color", 3)) {
                    if (context.isAdvanced()) {
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
                if (player != null) {
                    if (entityAttributeModifier.getId() == Item.ATTACK_DAMAGE_MODIFIER_UUID) {
                        d += player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getBaseValue();
                        d += (double)EnchantmentHelper.getAttackDamage(this, EntityGroup.DEFAULT);
                        bl = true;
                    } else if (entityAttributeModifier.getId() == Item.ATTACK_SPEED_MODIFIER_UUID) {
                        d += player.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getBaseValue();
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
        if (this.hasTag() && this.tag.contains("CanDestroy", 9) && (i & 8) == 0 && !(listTag2 = this.tag.getList("CanDestroy", 8)).isEmpty()) {
            list.add(new LiteralText(""));
            list.add(new TranslatableText("item.canBreak", new Object[0]).formatted(Formatting.GRAY));
            for (k = 0; k < listTag2.size(); ++k) {
                list.addAll(ItemStack.parseBlockTag(listTag2.getString(k)));
            }
        }
        if (this.hasTag() && this.tag.contains("CanPlaceOn", 9) && (i & 0x10) == 0 && !(listTag2 = this.tag.getList("CanPlaceOn", 8)).isEmpty()) {
            list.add(new LiteralText(""));
            list.add(new TranslatableText("item.canPlace", new Object[0]).formatted(Formatting.GRAY));
            for (k = 0; k < listTag2.size(); ++k) {
                list.addAll(ItemStack.parseBlockTag(listTag2.getString(k)));
            }
        }
        if (context.isAdvanced()) {
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
    public static void appendEnchantments(List<Text> tooltip, ListTag enchantments) {
        for (int i = 0; i < enchantments.size(); ++i) {
            CompoundTag compoundTag = enchantments.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(compoundTag.getString("id"))).ifPresent(e -> tooltip.add(e.getName(compoundTag.getInt("lvl"))));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static Collection<Text> parseBlockTag(String tag) {
        try {
            boolean bl2;
            BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(tag), true).parse(true);
            BlockState blockState = blockArgumentParser.getBlockState();
            Identifier identifier = blockArgumentParser.getTagId();
            boolean bl = blockState != null;
            boolean bl3 = bl2 = identifier != null;
            if (bl || bl2) {
                Collection<Block> collection;
                if (bl) {
                    return Lists.newArrayList(blockState.getBlock().getName().formatted(Formatting.DARK_GRAY));
                }
                net.minecraft.tag.Tag<Block> tag2 = BlockTags.getContainer().get(identifier);
                if (tag2 != null && !(collection = tag2.values()).isEmpty()) {
                    return collection.stream().map(Block::getName).map(text -> text.formatted(Formatting.DARK_GRAY)).collect(Collectors.toList());
                }
            }
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return Lists.newArrayList(new LiteralText("missingno").formatted(Formatting.DARK_GRAY));
    }

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

    public void addEnchantment(Enchantment enchantment, int level) {
        this.getOrCreateTag();
        if (!this.tag.contains("Enchantments", 9)) {
            this.tag.put("Enchantments", new ListTag());
        }
        ListTag listTag = this.tag.getList("Enchantments", 10);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
        compoundTag.putShort("lvl", (byte)level);
        listTag.add(compoundTag);
    }

    public boolean hasEnchantments() {
        if (this.tag != null && this.tag.contains("Enchantments", 9)) {
            return !this.tag.getList("Enchantments", 10).isEmpty();
        }
        return false;
    }

    public void putSubTag(String key, Tag tag) {
        this.getOrCreateTag().put(key, tag);
    }

    public boolean isInFrame() {
        return this.frame != null;
    }

    public void setFrame(@Nullable ItemFrameEntity frame) {
        this.frame = frame;
    }

    @Nullable
    public ItemFrameEntity getFrame() {
        return this.empty ? null : this.frame;
    }

    public int getRepairCost() {
        if (this.hasTag() && this.tag.contains("RepairCost", 3)) {
            return this.tag.getInt("RepairCost");
        }
        return 0;
    }

    public void setRepairCost(int repairCost) {
        this.getOrCreateTag().putInt("RepairCost", repairCost);
    }

    public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        Multimap<String, EntityAttributeModifier> multimap;
        if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
            multimap = HashMultimap.create();
            ListTag listTag = this.tag.getList("AttributeModifiers", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag = listTag.getCompound(i);
                EntityAttributeModifier entityAttributeModifier2 = EntityAttributes.createFromTag(compoundTag);
                if (entityAttributeModifier2 == null || compoundTag.contains("Slot", 8) && !compoundTag.getString("Slot").equals(slot.getName()) || entityAttributeModifier2.getId().getLeastSignificantBits() == 0L || entityAttributeModifier2.getId().getMostSignificantBits() == 0L) continue;
                multimap.put(compoundTag.getString("AttributeName"), entityAttributeModifier2);
            }
        } else {
            multimap = this.getItem().getModifiers(slot);
        }
        multimap.values().forEach(entityAttributeModifier -> entityAttributeModifier.setSerialize(false));
        return multimap;
    }

    public void addAttributeModifier(String name, EntityAttributeModifier modifier, @Nullable EquipmentSlot slot) {
        this.getOrCreateTag();
        if (!this.tag.contains("AttributeModifiers", 9)) {
            this.tag.put("AttributeModifiers", new ListTag());
        }
        ListTag listTag = this.tag.getList("AttributeModifiers", 10);
        CompoundTag compoundTag = EntityAttributes.toTag(modifier);
        compoundTag.putString("AttributeName", name);
        if (slot != null) {
            compoundTag.putString("Slot", slot.getName());
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

    private static boolean areBlocksEqual(CachedBlockPosition first, @Nullable CachedBlockPosition second) {
        if (second == null || first.getBlockState() != second.getBlockState()) {
            return false;
        }
        if (first.getBlockEntity() == null && second.getBlockEntity() == null) {
            return true;
        }
        if (first.getBlockEntity() == null || second.getBlockEntity() == null) {
            return false;
        }
        return Objects.equals(first.getBlockEntity().toTag(new CompoundTag()), second.getBlockEntity().toTag(new CompoundTag()));
    }

    public boolean canDestroy(RegistryTagManager manager, CachedBlockPosition pos) {
        if (ItemStack.areBlocksEqual(pos, this.lastDestroyPos)) {
            return this.lastDestroyResult;
        }
        this.lastDestroyPos = pos;
        if (this.hasTag() && this.tag.contains("CanDestroy", 9)) {
            ListTag listTag = this.tag.getList("CanDestroy", 8);
            for (int i = 0; i < listTag.size(); ++i) {
                String string = listTag.getString(i);
                try {
                    Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().parse(new StringReader(string)).create(manager);
                    if (predicate.test(pos)) {
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

    public boolean canPlaceOn(RegistryTagManager manager, CachedBlockPosition pos) {
        if (ItemStack.areBlocksEqual(pos, this.lastPlaceOnPos)) {
            return this.lastPlaceOnResult;
        }
        this.lastPlaceOnPos = pos;
        if (this.hasTag() && this.tag.contains("CanPlaceOn", 9)) {
            ListTag listTag = this.tag.getList("CanPlaceOn", 8);
            for (int i = 0; i < listTag.size(); ++i) {
                String string = listTag.getString(i);
                try {
                    Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().parse(new StringReader(string)).create(manager);
                    if (predicate.test(pos)) {
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

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCount() {
        return this.empty ? 0 : this.count;
    }

    public void setCount(int count) {
        this.count = count;
        this.updateEmptyState();
    }

    public void increment(int amount) {
        this.setCount(this.count + amount);
    }

    public void decrement(int amount) {
        this.increment(-amount);
    }

    public void usageTick(World world, LivingEntity user, int remainingUseTicks) {
        this.getItem().usageTick(world, user, this, remainingUseTicks);
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

