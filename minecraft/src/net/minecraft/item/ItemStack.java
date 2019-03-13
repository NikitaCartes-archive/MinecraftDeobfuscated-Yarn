package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagManager;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.util.ActionResult;
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

public final class ItemStack {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ItemStack EMPTY = new ItemStack((Item)null);
	public static final DecimalFormat MODIFIER_FORMAT = createModifierFormat();
	private int amount;
	private int updateCooldown;
	@Deprecated
	private final Item item;
	private CompoundTag field_8040;
	private boolean empty;
	private ItemFrameEntity holdingItemFrame;
	private CachedBlockPosition field_8039;
	private boolean lastCheckedCanHarvestResult;
	private CachedBlockPosition field_8032;
	private boolean lastCheckedCanPlaceResult;

	private static DecimalFormat createModifierFormat() {
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
		return decimalFormat;
	}

	public ItemStack(ItemProvider itemProvider) {
		this(itemProvider, 1);
	}

	public ItemStack(ItemProvider itemProvider, int i) {
		this.item = itemProvider == null ? null : itemProvider.getItem();
		this.amount = i;
		this.updateEmptyFlag();
	}

	private void updateEmptyFlag() {
		this.empty = false;
		this.empty = this.isEmpty();
	}

	private ItemStack(CompoundTag compoundTag) {
		this.item = Registry.ITEM.method_10223(new Identifier(compoundTag.getString("id")));
		this.amount = compoundTag.getByte("Count");
		if (compoundTag.containsKey("tag", 10)) {
			this.field_8040 = compoundTag.getCompound("tag");
			this.getItem().method_7860(compoundTag);
		}

		if (this.getItem().canDamage()) {
			this.setDamage(this.getDamage());
		}

		this.updateEmptyFlag();
	}

	public static ItemStack method_7915(CompoundTag compoundTag) {
		try {
			return new ItemStack(compoundTag);
		} catch (RuntimeException var2) {
			LOGGER.debug("Tried to load invalid item: {}", compoundTag, var2);
			return EMPTY;
		}
	}

	public boolean isEmpty() {
		if (this == EMPTY) {
			return true;
		} else {
			return this.getItem() == null || this.getItem() == Items.AIR ? true : this.amount <= 0;
		}
	}

	public ItemStack split(int i) {
		int j = Math.min(i, this.amount);
		ItemStack itemStack = this.copy();
		itemStack.setAmount(j);
		this.subtractAmount(j);
		return itemStack;
	}

	public Item getItem() {
		return this.empty ? Items.AIR : this.item;
	}

	public ActionResult method_7981(ItemUsageContext itemUsageContext) {
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		BlockPos blockPos = itemUsageContext.method_8037();
		CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(itemUsageContext.method_8045(), blockPos, false);
		if (playerEntity != null && !playerEntity.abilities.allowModifyWorld && !this.method_7944(itemUsageContext.method_8045().method_8514(), cachedBlockPosition)) {
			return ActionResult.PASS;
		} else {
			Item item = this.getItem();
			ActionResult actionResult = item.method_7884(itemUsageContext);
			if (playerEntity != null && actionResult == ActionResult.field_5812) {
				playerEntity.method_7259(Stats.field_15372.getOrCreateStat(item));
			}

			return actionResult;
		}
	}

	public float method_7924(BlockState blockState) {
		return this.getItem().method_7865(this, blockState);
	}

	public TypedActionResult<ItemStack> method_7913(World world, PlayerEntity playerEntity, Hand hand) {
		return this.getItem().method_7836(world, playerEntity, hand);
	}

	public ItemStack method_7910(World world, LivingEntity livingEntity) {
		return this.getItem().method_7861(this, world, livingEntity);
	}

	public CompoundTag method_7953(CompoundTag compoundTag) {
		Identifier identifier = Registry.ITEM.method_10221(this.getItem());
		compoundTag.putString("id", identifier == null ? "minecraft:air" : identifier.toString());
		compoundTag.putByte("Count", (byte)this.amount);
		if (this.field_8040 != null) {
			compoundTag.method_10566("tag", this.field_8040);
		}

		return compoundTag;
	}

	public int getMaxAmount() {
		return this.getItem().getMaxAmount();
	}

	public boolean canStack() {
		return this.getMaxAmount() > 1 && (!this.hasDurability() || !this.isDamaged());
	}

	public boolean hasDurability() {
		if (!this.empty && this.getItem().getDurability() > 0) {
			CompoundTag compoundTag = this.method_7969();
			return compoundTag == null || !compoundTag.getBoolean("Unbreakable");
		} else {
			return false;
		}
	}

	public boolean isDamaged() {
		return this.hasDurability() && this.getDamage() > 0;
	}

	public int getDamage() {
		return this.field_8040 == null ? 0 : this.field_8040.getInt("Damage");
	}

	public void setDamage(int i) {
		this.method_7948().putInt("Damage", Math.max(0, i));
	}

	public int getDurability() {
		return this.getItem().getDurability();
	}

	public boolean method_7970(int i, Random random, @Nullable ServerPlayerEntity serverPlayerEntity) {
		if (!this.hasDurability()) {
			return false;
		} else {
			if (i > 0) {
				int j = EnchantmentHelper.getLevel(Enchantments.field_9119, this);
				int k = 0;

				for (int l = 0; j > 0 && l < i; l++) {
					if (UnbreakingEnchantment.method_8176(this, j, random)) {
						k++;
					}
				}

				i -= k;
				if (i <= 0) {
					return false;
				}
			}

			if (serverPlayerEntity != null && i != 0) {
				Criterions.ITEM_DURABILITY_CHANGED.method_8960(serverPlayerEntity, this, this.getDamage() + i);
			}

			int j = this.getDamage() + i;
			this.setDamage(j);
			return j >= this.getDurability();
		}
	}

	public void applyDamage(int i, LivingEntity livingEntity) {
		if (!(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).abilities.creativeMode) {
			if (this.hasDurability()) {
				if (this.method_7970(i, livingEntity.getRand(), livingEntity instanceof ServerPlayerEntity ? (ServerPlayerEntity)livingEntity : null)) {
					livingEntity.method_6045(this);
					Item item = this.getItem();
					this.subtractAmount(1);
					if (livingEntity instanceof PlayerEntity) {
						((PlayerEntity)livingEntity).method_7259(Stats.field_15383.getOrCreateStat(item));
					}

					this.setDamage(0);
				}
			}
		}
	}

	public void onEntityDamaged(LivingEntity livingEntity, PlayerEntity playerEntity) {
		Item item = this.getItem();
		if (item.method_7873(this, livingEntity, playerEntity)) {
			playerEntity.method_7259(Stats.field_15372.getOrCreateStat(item));
		}
	}

	public void method_7952(World world, BlockState blockState, BlockPos blockPos, PlayerEntity playerEntity) {
		Item item = this.getItem();
		if (item.method_7879(this, world, blockState, blockPos, playerEntity)) {
			playerEntity.method_7259(Stats.field_15372.getOrCreateStat(item));
		}
	}

	public boolean method_7951(BlockState blockState) {
		return this.getItem().method_7856(blockState);
	}

	public boolean interactWithEntity(PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		return this.getItem().method_7847(this, playerEntity, livingEntity, hand);
	}

	public ItemStack copy() {
		ItemStack itemStack = new ItemStack(this.getItem(), this.amount);
		itemStack.setUpdateCooldown(this.getUpdateCooldown());
		if (this.field_8040 != null) {
			itemStack.field_8040 = this.field_8040.method_10553();
		}

		return itemStack;
	}

	public static boolean areTagsEqual(ItemStack itemStack, ItemStack itemStack2) {
		if (itemStack.isEmpty() && itemStack2.isEmpty()) {
			return true;
		} else if (itemStack.isEmpty() || itemStack2.isEmpty()) {
			return false;
		} else {
			return itemStack.field_8040 == null && itemStack2.field_8040 != null
				? false
				: itemStack.field_8040 == null || itemStack.field_8040.equals(itemStack2.field_8040);
		}
	}

	public static boolean areEqual(ItemStack itemStack, ItemStack itemStack2) {
		if (itemStack.isEmpty() && itemStack2.isEmpty()) {
			return true;
		} else {
			return !itemStack.isEmpty() && !itemStack2.isEmpty() ? itemStack.isEqual(itemStack2) : false;
		}
	}

	private boolean isEqual(ItemStack itemStack) {
		if (this.amount != itemStack.amount) {
			return false;
		} else if (this.getItem() != itemStack.getItem()) {
			return false;
		} else {
			return this.field_8040 == null && itemStack.field_8040 != null ? false : this.field_8040 == null || this.field_8040.equals(itemStack.field_8040);
		}
	}

	public static boolean areEqualIgnoreTags(ItemStack itemStack, ItemStack itemStack2) {
		if (itemStack == itemStack2) {
			return true;
		} else {
			return !itemStack.isEmpty() && !itemStack2.isEmpty() ? itemStack.isEqualIgnoreTags(itemStack2) : false;
		}
	}

	public static boolean areEqualIgnoreDurability(ItemStack itemStack, ItemStack itemStack2) {
		if (itemStack == itemStack2) {
			return true;
		} else {
			return !itemStack.isEmpty() && !itemStack2.isEmpty() ? itemStack.isEqualIgnoreDurability(itemStack2) : false;
		}
	}

	public boolean isEqualIgnoreTags(ItemStack itemStack) {
		return !itemStack.isEmpty() && this.getItem() == itemStack.getItem();
	}

	public boolean isEqualIgnoreDurability(ItemStack itemStack) {
		return !this.hasDurability() ? this.isEqualIgnoreTags(itemStack) : !itemStack.isEmpty() && this.getItem() == itemStack.getItem();
	}

	public String getTranslationKey() {
		return this.getItem().method_7866(this);
	}

	public String toString() {
		return this.amount + "x" + this.getItem().getTranslationKey();
	}

	public void method_7917(World world, Entity entity, int i, boolean bl) {
		if (this.updateCooldown > 0) {
			this.updateCooldown--;
		}

		if (this.getItem() != null) {
			this.getItem().method_7888(this, world, entity, i, bl);
		}
	}

	public void method_7982(World world, PlayerEntity playerEntity, int i) {
		playerEntity.method_7342(Stats.field_15370.getOrCreateStat(this.getItem()), i);
		this.getItem().method_7843(this, world, playerEntity);
	}

	public int getMaxUseTime() {
		return this.getItem().method_7881(this);
	}

	public UseAction method_7976() {
		return this.getItem().method_7853(this);
	}

	public void method_7930(World world, LivingEntity livingEntity, int i) {
		this.getItem().method_7840(this, world, livingEntity, i);
	}

	public boolean method_7967() {
		return this.getItem().method_7838(this);
	}

	public boolean hasTag() {
		return !this.empty && this.field_8040 != null && !this.field_8040.isEmpty();
	}

	@Nullable
	public CompoundTag method_7969() {
		return this.field_8040;
	}

	public CompoundTag method_7948() {
		if (this.field_8040 == null) {
			this.method_7980(new CompoundTag());
		}

		return this.field_8040;
	}

	public CompoundTag method_7911(String string) {
		if (this.field_8040 != null && this.field_8040.containsKey(string, 10)) {
			return this.field_8040.getCompound(string);
		} else {
			CompoundTag compoundTag = new CompoundTag();
			this.method_7959(string, compoundTag);
			return compoundTag;
		}
	}

	@Nullable
	public CompoundTag method_7941(String string) {
		return this.field_8040 != null && this.field_8040.containsKey(string, 10) ? this.field_8040.getCompound(string) : null;
	}

	public void removeSubTag(String string) {
		if (this.field_8040 != null && this.field_8040.containsKey(string)) {
			this.field_8040.remove(string);
			if (this.field_8040.isEmpty()) {
				this.field_8040 = null;
			}
		}
	}

	public ListTag method_7921() {
		return this.field_8040 != null ? this.field_8040.method_10554("Enchantments", 10) : new ListTag();
	}

	public void method_7980(@Nullable CompoundTag compoundTag) {
		this.field_8040 = compoundTag;
	}

	public TextComponent method_7964() {
		CompoundTag compoundTag = this.method_7941("display");
		if (compoundTag != null && compoundTag.containsKey("Name", 8)) {
			try {
				TextComponent textComponent = TextComponent.Serializer.fromJsonString(compoundTag.getString("Name"));
				if (textComponent != null) {
					return textComponent;
				}

				compoundTag.remove("Name");
			} catch (JsonParseException var3) {
				compoundTag.remove("Name");
			}
		}

		return this.getItem().method_7864(this);
	}

	public ItemStack method_7977(@Nullable TextComponent textComponent) {
		CompoundTag compoundTag = this.method_7911("display");
		if (textComponent != null) {
			compoundTag.putString("Name", TextComponent.Serializer.toJsonString(textComponent));
		} else {
			compoundTag.remove("Name");
		}

		return this;
	}

	public void removeDisplayName() {
		CompoundTag compoundTag = this.method_7941("display");
		if (compoundTag != null) {
			compoundTag.remove("Name");
			if (compoundTag.isEmpty()) {
				this.removeSubTag("display");
			}
		}

		if (this.field_8040 != null && this.field_8040.isEmpty()) {
			this.field_8040 = null;
		}
	}

	public boolean hasDisplayName() {
		CompoundTag compoundTag = this.method_7941("display");
		return compoundTag != null && compoundTag.containsKey("Name", 8);
	}

	@Environment(EnvType.CLIENT)
	public List<TextComponent> method_7950(@Nullable PlayerEntity playerEntity, TooltipContext tooltipContext) {
		List<TextComponent> list = Lists.<TextComponent>newArrayList();
		TextComponent textComponent = new StringTextComponent("").append(this.method_7964()).applyFormat(this.method_7932().field_8908);
		if (this.hasDisplayName()) {
			textComponent.applyFormat(TextFormat.field_1056);
		}

		list.add(textComponent);
		if (!tooltipContext.isAdvanced() && !this.hasDisplayName() && this.getItem() == Items.field_8204) {
			list.add(new StringTextComponent("#" + FilledMapItem.method_8003(this)).applyFormat(TextFormat.field_1080));
		}

		int i = 0;
		if (this.hasTag() && this.field_8040.containsKey("HideFlags", 99)) {
			i = this.field_8040.getInt("HideFlags");
		}

		if ((i & 32) == 0) {
			this.getItem().method_7851(this, playerEntity == null ? null : playerEntity.field_6002, list, tooltipContext);
		}

		if (this.hasTag()) {
			if ((i & 1) == 0) {
				method_17870(list, this.method_7921());
			}

			if (this.field_8040.containsKey("display", 10)) {
				CompoundTag compoundTag = this.field_8040.getCompound("display");
				if (compoundTag.containsKey("color", 3)) {
					if (tooltipContext.isAdvanced()) {
						list.add(new TranslatableTextComponent("item.color", String.format("#%06X", compoundTag.getInt("color"))).applyFormat(TextFormat.field_1080));
					} else {
						list.add(new TranslatableTextComponent("item.dyed").applyFormat(new TextFormat[]{TextFormat.field_1080, TextFormat.field_1056}));
					}
				}

				if (compoundTag.getType("Lore") == 9) {
					ListTag listTag = compoundTag.method_10554("Lore", 8);

					for (int j = 0; j < listTag.size(); j++) {
						String string = listTag.getString(j);

						try {
							TextComponent textComponent2 = TextComponent.Serializer.fromJsonString(string);
							if (textComponent2 != null) {
								list.add(TextFormatter.method_10889(textComponent2, new Style().setColor(TextFormat.field_1064).setItalic(true)));
							}
						} catch (JsonParseException var19) {
							compoundTag.remove("Lore");
						}
					}
				}
			}
		}

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			Multimap<String, EntityAttributeModifier> multimap = this.getAttributeModifiers(equipmentSlot);
			if (!multimap.isEmpty() && (i & 2) == 0) {
				list.add(new StringTextComponent(""));
				list.add(new TranslatableTextComponent("item.modifiers." + equipmentSlot.getName()).applyFormat(TextFormat.field_1080));

				for (Entry<String, EntityAttributeModifier> entry : multimap.entries()) {
					EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)entry.getValue();
					double d = entityAttributeModifier.getAmount();
					boolean bl = false;
					if (playerEntity != null) {
						if (entityAttributeModifier.getId() == Item.MODIFIER_DAMAGE) {
							d += playerEntity.method_5996(EntityAttributes.ATTACK_DAMAGE).getBaseValue();
							d += (double)EnchantmentHelper.getAttackDamage(this, EntityGroup.DEFAULT);
							bl = true;
						} else if (entityAttributeModifier.getId() == Item.MODIFIER_SWING_SPEED) {
							d += playerEntity.method_5996(EntityAttributes.ATTACK_SPEED).getBaseValue();
							bl = true;
						}
					}

					double e;
					if (entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.field_6330
						&& entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.field_6331) {
						e = d;
					} else {
						e = d * 100.0;
					}

					if (bl) {
						list.add(
							new StringTextComponent(" ")
								.append(
									new TranslatableTextComponent(
										"attribute.modifier.equals." + entityAttributeModifier.getOperation().getId(),
										MODIFIER_FORMAT.format(e),
										new TranslatableTextComponent("attribute.name." + (String)entry.getKey())
									)
								)
								.applyFormat(TextFormat.field_1077)
						);
					} else if (d > 0.0) {
						list.add(
							new TranslatableTextComponent(
									"attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(),
									MODIFIER_FORMAT.format(e),
									new TranslatableTextComponent("attribute.name." + (String)entry.getKey())
								)
								.applyFormat(TextFormat.field_1078)
						);
					} else if (d < 0.0) {
						e *= -1.0;
						list.add(
							new TranslatableTextComponent(
									"attribute.modifier.take." + entityAttributeModifier.getOperation().getId(),
									MODIFIER_FORMAT.format(e),
									new TranslatableTextComponent("attribute.name." + (String)entry.getKey())
								)
								.applyFormat(TextFormat.field_1061)
						);
					}
				}
			}
		}

		if (this.hasTag() && this.method_7969().getBoolean("Unbreakable") && (i & 4) == 0) {
			list.add(new TranslatableTextComponent("item.unbreakable").applyFormat(TextFormat.field_1078));
		}

		if (this.hasTag() && this.field_8040.containsKey("CanDestroy", 9) && (i & 8) == 0) {
			ListTag listTag2 = this.field_8040.method_10554("CanDestroy", 8);
			if (!listTag2.isEmpty()) {
				list.add(new StringTextComponent(""));
				list.add(new TranslatableTextComponent("item.canBreak").applyFormat(TextFormat.field_1080));

				for (int k = 0; k < listTag2.size(); k++) {
					list.addAll(method_7937(listTag2.getString(k)));
				}
			}
		}

		if (this.hasTag() && this.field_8040.containsKey("CanPlaceOn", 9) && (i & 16) == 0) {
			ListTag listTag2 = this.field_8040.method_10554("CanPlaceOn", 8);
			if (!listTag2.isEmpty()) {
				list.add(new StringTextComponent(""));
				list.add(new TranslatableTextComponent("item.canPlace").applyFormat(TextFormat.field_1080));

				for (int k = 0; k < listTag2.size(); k++) {
					list.addAll(method_7937(listTag2.getString(k)));
				}
			}
		}

		if (tooltipContext.isAdvanced()) {
			if (this.isDamaged()) {
				list.add(new TranslatableTextComponent("item.durability", this.getDurability() - this.getDamage(), this.getDurability()));
			}

			list.add(new StringTextComponent(Registry.ITEM.method_10221(this.getItem()).toString()).applyFormat(TextFormat.field_1063));
			if (this.hasTag()) {
				list.add(new TranslatableTextComponent("item.nbt_tags", this.method_7969().getKeys().size()).applyFormat(TextFormat.field_1063));
			}
		}

		return list;
	}

	@Environment(EnvType.CLIENT)
	public static void method_17870(List<TextComponent> list, ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			Registry.ENCHANTMENT
				.method_17966(Identifier.create(compoundTag.getString("id")))
				.ifPresent(enchantment -> list.add(enchantment.method_8179(compoundTag.getInt("lvl"))));
		}
	}

	@Environment(EnvType.CLIENT)
	private static Collection<TextComponent> method_7937(String string) {
		try {
			BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), true).parse(true);
			BlockState blockState = blockArgumentParser.getBlockState();
			Identifier identifier = blockArgumentParser.method_9664();
			boolean bl = blockState != null;
			boolean bl2 = identifier != null;
			if (bl || bl2) {
				if (bl) {
					return Lists.<TextComponent>newArrayList(blockState.getBlock().method_9518().applyFormat(TextFormat.field_1063));
				}

				Tag<Block> tag = BlockTags.method_15073().get(identifier);
				if (tag != null) {
					Collection<Block> collection = tag.values();
					if (!collection.isEmpty()) {
						return (Collection<TextComponent>)collection.stream()
							.map(Block::method_9518)
							.map(textComponent -> textComponent.applyFormat(TextFormat.field_1063))
							.collect(Collectors.toList());
					}
				}
			}
		} catch (CommandSyntaxException var8) {
		}

		return Lists.<TextComponent>newArrayList(new StringTextComponent("missingno").applyFormat(TextFormat.field_1063));
	}

	@Environment(EnvType.CLIENT)
	public boolean hasEnchantmentGlint() {
		return this.getItem().method_7886(this);
	}

	public Rarity method_7932() {
		return this.getItem().method_7862(this);
	}

	public boolean isEnchantable() {
		return !this.getItem().method_7870(this) ? false : !this.hasEnchantments();
	}

	public void method_7978(Enchantment enchantment, int i) {
		this.method_7948();
		if (!this.field_8040.containsKey("Enchantments", 9)) {
			this.field_8040.method_10566("Enchantments", new ListTag());
		}

		ListTag listTag = this.field_8040.method_10554("Enchantments", 10);
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.method_10221(enchantment)));
		compoundTag.putShort("lvl", (short)((byte)i));
		listTag.add(compoundTag);
	}

	public boolean hasEnchantments() {
		return this.field_8040 != null && this.field_8040.containsKey("Enchantments", 9) ? !this.field_8040.method_10554("Enchantments", 10).isEmpty() : false;
	}

	public void method_7959(String string, net.minecraft.nbt.Tag tag) {
		this.method_7948().method_10566(string, tag);
	}

	public boolean isHeldInItemFrame() {
		return this.holdingItemFrame != null;
	}

	public void setHoldingItemFrame(@Nullable ItemFrameEntity itemFrameEntity) {
		this.holdingItemFrame = itemFrameEntity;
	}

	@Nullable
	public ItemFrameEntity getHoldingItemFrame() {
		return this.empty ? null : this.holdingItemFrame;
	}

	public int getRepairCost() {
		return this.hasTag() && this.field_8040.containsKey("RepairCost", 3) ? this.field_8040.getInt("RepairCost") : 0;
	}

	public void setRepairCost(int i) {
		this.method_7948().putInt("RepairCost", i);
	}

	public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
		Multimap<String, EntityAttributeModifier> multimap;
		if (this.hasTag() && this.field_8040.containsKey("AttributeModifiers", 9)) {
			multimap = HashMultimap.create();
			ListTag listTag = this.field_8040.method_10554("AttributeModifiers", 10);

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag = listTag.getCompoundTag(i);
				EntityAttributeModifier entityAttributeModifier = EntityAttributes.method_7133(compoundTag);
				if (entityAttributeModifier != null
					&& (!compoundTag.containsKey("Slot", 8) || compoundTag.getString("Slot").equals(equipmentSlot.getName()))
					&& entityAttributeModifier.getId().getLeastSignificantBits() != 0L
					&& entityAttributeModifier.getId().getMostSignificantBits() != 0L) {
					multimap.put(compoundTag.getString("AttributeName"), entityAttributeModifier);
				}
			}
		} else {
			multimap = this.getItem().getAttributeModifiers(equipmentSlot);
		}

		return multimap;
	}

	public void addAttributeModifier(String string, EntityAttributeModifier entityAttributeModifier, @Nullable EquipmentSlot equipmentSlot) {
		this.method_7948();
		if (!this.field_8040.containsKey("AttributeModifiers", 9)) {
			this.field_8040.method_10566("AttributeModifiers", new ListTag());
		}

		ListTag listTag = this.field_8040.method_10554("AttributeModifiers", 10);
		CompoundTag compoundTag = EntityAttributes.method_7135(entityAttributeModifier);
		compoundTag.putString("AttributeName", string);
		if (equipmentSlot != null) {
			compoundTag.putString("Slot", equipmentSlot.getName());
		}

		listTag.add(compoundTag);
	}

	public TextComponent method_7954() {
		TextComponent textComponent = new StringTextComponent("").append(this.method_7964());
		if (this.hasDisplayName()) {
			textComponent.applyFormat(TextFormat.field_1056);
		}

		TextComponent textComponent2 = TextFormatter.bracketed(textComponent);
		if (!this.empty) {
			CompoundTag compoundTag = this.method_7953(new CompoundTag());
			textComponent2.applyFormat(this.method_7932().field_8908)
				.modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new StringTextComponent(compoundTag.toString()))));
		}

		return textComponent2;
	}

	private static boolean method_7918(CachedBlockPosition cachedBlockPosition, @Nullable CachedBlockPosition cachedBlockPosition2) {
		if (cachedBlockPosition2 == null || cachedBlockPosition.getBlockState() != cachedBlockPosition2.getBlockState()) {
			return false;
		} else if (cachedBlockPosition.getBlockEntity() == null && cachedBlockPosition2.getBlockEntity() == null) {
			return true;
		} else {
			return cachedBlockPosition.getBlockEntity() != null && cachedBlockPosition2.getBlockEntity() != null
				? Objects.equals(
					cachedBlockPosition.getBlockEntity().method_11007(new CompoundTag()), cachedBlockPosition2.getBlockEntity().method_11007(new CompoundTag())
				)
				: false;
		}
	}

	public boolean method_7940(TagManager tagManager, CachedBlockPosition cachedBlockPosition) {
		if (method_7918(cachedBlockPosition, this.field_8039)) {
			return this.lastCheckedCanHarvestResult;
		} else {
			this.field_8039 = cachedBlockPosition;
			if (this.hasTag() && this.field_8040.containsKey("CanDestroy", 9)) {
				ListTag listTag = this.field_8040.method_10554("CanDestroy", 8);

				for (int i = 0; i < listTag.size(); i++) {
					String string = listTag.getString(i);

					try {
						Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.create().method_9642(new StringReader(string)).create(tagManager);
						if (predicate.test(cachedBlockPosition)) {
							this.lastCheckedCanHarvestResult = true;
							return true;
						}
					} catch (CommandSyntaxException var7) {
					}
				}
			}

			this.lastCheckedCanHarvestResult = false;
			return false;
		}
	}

	public boolean method_7944(TagManager tagManager, CachedBlockPosition cachedBlockPosition) {
		if (method_7918(cachedBlockPosition, this.field_8032)) {
			return this.lastCheckedCanPlaceResult;
		} else {
			this.field_8032 = cachedBlockPosition;
			if (this.hasTag() && this.field_8040.containsKey("CanPlaceOn", 9)) {
				ListTag listTag = this.field_8040.method_10554("CanPlaceOn", 8);

				for (int i = 0; i < listTag.size(); i++) {
					String string = listTag.getString(i);

					try {
						Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.create().method_9642(new StringReader(string)).create(tagManager);
						if (predicate.test(cachedBlockPosition)) {
							this.lastCheckedCanPlaceResult = true;
							return true;
						}
					} catch (CommandSyntaxException var7) {
					}
				}
			}

			this.lastCheckedCanPlaceResult = false;
			return false;
		}
	}

	public int getUpdateCooldown() {
		return this.updateCooldown;
	}

	public void setUpdateCooldown(int i) {
		this.updateCooldown = i;
	}

	public int getAmount() {
		return this.empty ? 0 : this.amount;
	}

	public void setAmount(int i) {
		this.amount = i;
		this.updateEmptyFlag();
	}

	public void addAmount(int i) {
		this.setAmount(this.amount + i);
	}

	public void subtractAmount(int i) {
		this.addAmount(-i);
	}

	public void method_7949(World world, LivingEntity livingEntity, int i) {
		this.getItem().method_7852(world, livingEntity, this, i);
	}

	public boolean method_19267() {
		return this.getItem().method_19263();
	}
}
