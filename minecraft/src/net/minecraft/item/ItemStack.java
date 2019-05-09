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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.tag.Tag;
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
	private CompoundTag tag;
	private boolean empty;
	private ItemFrameEntity holdingItemFrame;
	private CachedBlockPosition lastCheckedCanHarvestBlock;
	private boolean lastCheckedCanHarvestResult;
	private CachedBlockPosition lastCheckedCanPlaceBlock;
	private boolean lastCheckedCanPlaceResult;

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
		this.amount = i;
		this.updateEmptyFlag();
	}

	private void updateEmptyFlag() {
		this.empty = false;
		this.empty = this.isEmpty();
	}

	private ItemStack(CompoundTag compoundTag) {
		this.item = Registry.ITEM.get(new Identifier(compoundTag.getString("id")));
		this.amount = compoundTag.getByte("Count");
		if (compoundTag.containsKey("tag", 10)) {
			this.tag = compoundTag.getCompound("tag");
			this.getItem().onTagDeserialized(compoundTag);
		}

		if (this.getItem().canDamage()) {
			this.setDamage(this.getDamage());
		}

		this.updateEmptyFlag();
	}

	public static ItemStack fromTag(CompoundTag compoundTag) {
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

	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(itemUsageContext.getWorld(), blockPos, false);
		if (playerEntity != null
			&& !playerEntity.abilities.allowModifyWorld
			&& !this.getCustomCanPlace(itemUsageContext.getWorld().getTagManager(), cachedBlockPosition)) {
			return ActionResult.field_5811;
		} else {
			Item item = this.getItem();
			ActionResult actionResult = item.useOnBlock(itemUsageContext);
			if (playerEntity != null && actionResult == ActionResult.field_5812) {
				playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(item));
			}

			return actionResult;
		}
	}

	public float getBlockBreakingSpeed(BlockState blockState) {
		return this.getItem().getBlockBreakingSpeed(this, blockState);
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		return this.getItem().use(world, playerEntity, hand);
	}

	public ItemStack onItemFinishedUsing(World world, LivingEntity livingEntity) {
		return this.getItem().onItemFinishedUsing(this, world, livingEntity);
	}

	public CompoundTag toTag(CompoundTag compoundTag) {
		Identifier identifier = Registry.ITEM.getId(this.getItem());
		compoundTag.putString("id", identifier == null ? "minecraft:air" : identifier.toString());
		compoundTag.putByte("Count", (byte)this.amount);
		if (this.tag != null) {
			compoundTag.put("tag", this.tag);
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
			CompoundTag compoundTag = this.getTag();
			return compoundTag == null || !compoundTag.getBoolean("Unbreakable");
		} else {
			return false;
		}
	}

	public boolean isDamaged() {
		return this.hasDurability() && this.getDamage() > 0;
	}

	public int getDamage() {
		return this.tag == null ? 0 : this.tag.getInt("Damage");
	}

	public void setDamage(int i) {
		this.getOrCreateTag().putInt("Damage", Math.max(0, i));
	}

	public int getDurability() {
		return this.getItem().getDurability();
	}

	public boolean applyDamage(int i, Random random, @Nullable ServerPlayerEntity serverPlayerEntity) {
		if (!this.hasDurability()) {
			return false;
		} else {
			if (i > 0) {
				int j = EnchantmentHelper.getLevel(Enchantments.field_9119, this);
				int k = 0;

				for (int l = 0; j > 0 && l < i; l++) {
					if (UnbreakingEnchantment.shouldPreventDamage(this, j, random)) {
						k++;
					}
				}

				i -= k;
				if (i <= 0) {
					return false;
				}
			}

			if (serverPlayerEntity != null && i != 0) {
				Criterions.ITEM_DURABILITY_CHANGED.handle(serverPlayerEntity, this, this.getDamage() + i);
			}

			int j = this.getDamage() + i;
			this.setDamage(j);
			return j >= this.getDurability();
		}
	}

	public <T extends LivingEntity> void applyDamage(int i, T livingEntity, Consumer<T> consumer) {
		if (!livingEntity.world.isClient && (!(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).abilities.creativeMode)) {
			if (this.hasDurability()) {
				if (this.applyDamage(i, livingEntity.getRand(), livingEntity instanceof ServerPlayerEntity ? (ServerPlayerEntity)livingEntity : null)) {
					consumer.accept(livingEntity);
					Item item = this.getItem();
					this.subtractAmount(1);
					if (livingEntity instanceof PlayerEntity) {
						((PlayerEntity)livingEntity).incrementStat(Stats.field_15383.getOrCreateStat(item));
					}

					this.setDamage(0);
				}
			}
		}
	}

	public void onEntityDamaged(LivingEntity livingEntity, PlayerEntity playerEntity) {
		Item item = this.getItem();
		if (item.onEntityDamaged(this, livingEntity, playerEntity)) {
			playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(item));
		}
	}

	public void onBlockBroken(World world, BlockState blockState, BlockPos blockPos, PlayerEntity playerEntity) {
		Item item = this.getItem();
		if (item.onBlockBroken(this, world, blockState, blockPos, playerEntity)) {
			playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(item));
		}
	}

	public boolean isEffectiveOn(BlockState blockState) {
		return this.getItem().isEffectiveOn(blockState);
	}

	public boolean interactWithEntity(PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		return this.getItem().interactWithEntity(this, playerEntity, livingEntity, hand);
	}

	public ItemStack copy() {
		ItemStack itemStack = new ItemStack(this.getItem(), this.amount);
		itemStack.setUpdateCooldown(this.getUpdateCooldown());
		if (this.tag != null) {
			itemStack.tag = this.tag.method_10553();
		}

		return itemStack;
	}

	public static boolean areTagsEqual(ItemStack itemStack, ItemStack itemStack2) {
		if (itemStack.isEmpty() && itemStack2.isEmpty()) {
			return true;
		} else if (itemStack.isEmpty() || itemStack2.isEmpty()) {
			return false;
		} else {
			return itemStack.tag == null && itemStack2.tag != null ? false : itemStack.tag == null || itemStack.tag.equals(itemStack2.tag);
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
			return this.tag == null && itemStack.tag != null ? false : this.tag == null || this.tag.equals(itemStack.tag);
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
		return this.getItem().getTranslationKey(this);
	}

	public String toString() {
		return this.amount + "x" + this.getItem().getTranslationKey();
	}

	public void update(World world, Entity entity, int i, boolean bl) {
		if (this.updateCooldown > 0) {
			this.updateCooldown--;
		}

		if (this.getItem() != null) {
			this.getItem().onEntityTick(this, world, entity, i, bl);
		}
	}

	public void onCrafted(World world, PlayerEntity playerEntity, int i) {
		playerEntity.increaseStat(Stats.field_15370.getOrCreateStat(this.getItem()), i);
		this.getItem().onCrafted(this, world, playerEntity);
	}

	public int getMaxUseTime() {
		return this.getItem().getMaxUseTime(this);
	}

	public UseAction getUseAction() {
		return this.getItem().getUseAction(this);
	}

	public void onItemStopUsing(World world, LivingEntity livingEntity, int i) {
		this.getItem().onItemStopUsing(this, world, livingEntity, i);
	}

	public boolean method_7967() {
		return this.getItem().method_7838(this);
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

	public CompoundTag getOrCreateSubCompoundTag(String string) {
		if (this.tag != null && this.tag.containsKey(string, 10)) {
			return this.tag.getCompound(string);
		} else {
			CompoundTag compoundTag = new CompoundTag();
			this.setChildTag(string, compoundTag);
			return compoundTag;
		}
	}

	@Nullable
	public CompoundTag getSubCompoundTag(String string) {
		return this.tag != null && this.tag.containsKey(string, 10) ? this.tag.getCompound(string) : null;
	}

	public void removeSubTag(String string) {
		if (this.tag != null && this.tag.containsKey(string)) {
			this.tag.remove(string);
			if (this.tag.isEmpty()) {
				this.tag = null;
			}
		}
	}

	public ListTag getEnchantmentList() {
		return this.tag != null ? this.tag.getList("Enchantments", 10) : new ListTag();
	}

	public void setTag(@Nullable CompoundTag compoundTag) {
		this.tag = compoundTag;
	}

	public Component getDisplayName() {
		CompoundTag compoundTag = this.getSubCompoundTag("display");
		if (compoundTag != null && compoundTag.containsKey("Name", 8)) {
			try {
				Component component = Component.Serializer.fromJsonString(compoundTag.getString("Name"));
				if (component != null) {
					return component;
				}

				compoundTag.remove("Name");
			} catch (JsonParseException var3) {
				compoundTag.remove("Name");
			}
		}

		return this.getItem().getTranslatedNameTrimmed(this);
	}

	public ItemStack setDisplayName(@Nullable Component component) {
		CompoundTag compoundTag = this.getOrCreateSubCompoundTag("display");
		if (component != null) {
			compoundTag.putString("Name", Component.Serializer.toJsonString(component));
		} else {
			compoundTag.remove("Name");
		}

		return this;
	}

	public void removeDisplayName() {
		CompoundTag compoundTag = this.getSubCompoundTag("display");
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

	public boolean hasDisplayName() {
		CompoundTag compoundTag = this.getSubCompoundTag("display");
		return compoundTag != null && compoundTag.containsKey("Name", 8);
	}

	@Environment(EnvType.CLIENT)
	public List<Component> getTooltipText(@Nullable PlayerEntity playerEntity, TooltipContext tooltipContext) {
		List<Component> list = Lists.<Component>newArrayList();
		Component component = new TextComponent("").append(this.getDisplayName()).applyFormat(this.getRarity().formatting);
		if (this.hasDisplayName()) {
			component.applyFormat(ChatFormat.field_1056);
		}

		list.add(component);
		if (!tooltipContext.isAdvanced() && !this.hasDisplayName() && this.getItem() == Items.field_8204) {
			list.add(new TextComponent("#" + FilledMapItem.getMapId(this)).applyFormat(ChatFormat.field_1080));
		}

		int i = 0;
		if (this.hasTag() && this.tag.containsKey("HideFlags", 99)) {
			i = this.tag.getInt("HideFlags");
		}

		if ((i & 32) == 0) {
			this.getItem().buildTooltip(this, playerEntity == null ? null : playerEntity.world, list, tooltipContext);
		}

		if (this.hasTag()) {
			if ((i & 1) == 0) {
				appendEnchantmentComponents(list, this.getEnchantmentList());
			}

			if (this.tag.containsKey("display", 10)) {
				CompoundTag compoundTag = this.tag.getCompound("display");
				if (compoundTag.containsKey("color", 3)) {
					if (tooltipContext.isAdvanced()) {
						list.add(new TranslatableComponent("item.color", String.format("#%06X", compoundTag.getInt("color"))).applyFormat(ChatFormat.field_1080));
					} else {
						list.add(new TranslatableComponent("item.dyed").applyFormat(new ChatFormat[]{ChatFormat.field_1080, ChatFormat.field_1056}));
					}
				}

				if (compoundTag.getType("Lore") == 9) {
					ListTag listTag = compoundTag.getList("Lore", 8);

					for (int j = 0; j < listTag.size(); j++) {
						String string = listTag.getString(j);

						try {
							Component component2 = Component.Serializer.fromJsonString(string);
							if (component2 != null) {
								list.add(Components.style(component2, new Style().setColor(ChatFormat.field_1064).setItalic(true)));
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
				list.add(new TextComponent(""));
				list.add(new TranslatableComponent("item.modifiers." + equipmentSlot.getName()).applyFormat(ChatFormat.field_1080));

				for (Entry<String, EntityAttributeModifier> entry : multimap.entries()) {
					EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)entry.getValue();
					double d = entityAttributeModifier.getAmount();
					boolean bl = false;
					if (playerEntity != null) {
						if (entityAttributeModifier.getId() == Item.MODIFIER_DAMAGE) {
							d += playerEntity.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getBaseValue();
							d += (double)EnchantmentHelper.getAttackDamage(this, EntityGroup.DEFAULT);
							bl = true;
						} else if (entityAttributeModifier.getId() == Item.MODIFIER_SWING_SPEED) {
							d += playerEntity.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getBaseValue();
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
							new TextComponent(" ")
								.append(
									new TranslatableComponent(
										"attribute.modifier.equals." + entityAttributeModifier.getOperation().getId(),
										MODIFIER_FORMAT.format(e),
										new TranslatableComponent("attribute.name." + (String)entry.getKey())
									)
								)
								.applyFormat(ChatFormat.field_1077)
						);
					} else if (d > 0.0) {
						list.add(
							new TranslatableComponent(
									"attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(),
									MODIFIER_FORMAT.format(e),
									new TranslatableComponent("attribute.name." + (String)entry.getKey())
								)
								.applyFormat(ChatFormat.field_1078)
						);
					} else if (d < 0.0) {
						e *= -1.0;
						list.add(
							new TranslatableComponent(
									"attribute.modifier.take." + entityAttributeModifier.getOperation().getId(),
									MODIFIER_FORMAT.format(e),
									new TranslatableComponent("attribute.name." + (String)entry.getKey())
								)
								.applyFormat(ChatFormat.field_1061)
						);
					}
				}
			}
		}

		if (this.hasTag() && this.getTag().getBoolean("Unbreakable") && (i & 4) == 0) {
			list.add(new TranslatableComponent("item.unbreakable").applyFormat(ChatFormat.field_1078));
		}

		if (this.hasTag() && this.tag.containsKey("CanDestroy", 9) && (i & 8) == 0) {
			ListTag listTag2 = this.tag.getList("CanDestroy", 8);
			if (!listTag2.isEmpty()) {
				list.add(new TextComponent(""));
				list.add(new TranslatableComponent("item.canBreak").applyFormat(ChatFormat.field_1080));

				for (int k = 0; k < listTag2.size(); k++) {
					list.addAll(method_7937(listTag2.getString(k)));
				}
			}
		}

		if (this.hasTag() && this.tag.containsKey("CanPlaceOn", 9) && (i & 16) == 0) {
			ListTag listTag2 = this.tag.getList("CanPlaceOn", 8);
			if (!listTag2.isEmpty()) {
				list.add(new TextComponent(""));
				list.add(new TranslatableComponent("item.canPlace").applyFormat(ChatFormat.field_1080));

				for (int k = 0; k < listTag2.size(); k++) {
					list.addAll(method_7937(listTag2.getString(k)));
				}
			}
		}

		if (tooltipContext.isAdvanced()) {
			if (this.isDamaged()) {
				list.add(new TranslatableComponent("item.durability", this.getDurability() - this.getDamage(), this.getDurability()));
			}

			list.add(new TextComponent(Registry.ITEM.getId(this.getItem()).toString()).applyFormat(ChatFormat.field_1063));
			if (this.hasTag()) {
				list.add(new TranslatableComponent("item.nbt_tags", this.getTag().getKeys().size()).applyFormat(ChatFormat.field_1063));
			}
		}

		return list;
	}

	@Environment(EnvType.CLIENT)
	public static void appendEnchantmentComponents(List<Component> list, ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			Registry.ENCHANTMENT
				.getOrEmpty(Identifier.ofNullable(compoundTag.getString("id")))
				.ifPresent(enchantment -> list.add(enchantment.getTextComponent(compoundTag.getInt("lvl"))));
		}
	}

	@Environment(EnvType.CLIENT)
	private static Collection<Component> method_7937(String string) {
		try {
			BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(string), true).parse(true);
			BlockState blockState = blockArgumentParser.getBlockState();
			Identifier identifier = blockArgumentParser.getTagId();
			boolean bl = blockState != null;
			boolean bl2 = identifier != null;
			if (bl || bl2) {
				if (bl) {
					return Lists.<Component>newArrayList(blockState.getBlock().getTextComponent().applyFormat(ChatFormat.field_1063));
				}

				Tag<Block> tag = BlockTags.getContainer().get(identifier);
				if (tag != null) {
					Collection<Block> collection = tag.values();
					if (!collection.isEmpty()) {
						return (Collection<Component>)collection.stream()
							.map(Block::getTextComponent)
							.map(component -> component.applyFormat(ChatFormat.field_1063))
							.collect(Collectors.toList());
					}
				}
			}
		} catch (CommandSyntaxException var8) {
		}

		return Lists.<Component>newArrayList(new TextComponent("missingno").applyFormat(ChatFormat.field_1063));
	}

	@Environment(EnvType.CLIENT)
	public boolean hasEnchantmentGlint() {
		return this.getItem().hasEnchantmentGlint(this);
	}

	public Rarity getRarity() {
		return this.getItem().getRarity(this);
	}

	public boolean isEnchantable() {
		return !this.getItem().isTool(this) ? false : !this.hasEnchantments();
	}

	public void addEnchantment(Enchantment enchantment, int i) {
		this.getOrCreateTag();
		if (!this.tag.containsKey("Enchantments", 9)) {
			this.tag.put("Enchantments", new ListTag());
		}

		ListTag listTag = this.tag.getList("Enchantments", 10);
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
		compoundTag.putShort("lvl", (short)((byte)i));
		listTag.add(compoundTag);
	}

	public boolean hasEnchantments() {
		return this.tag != null && this.tag.containsKey("Enchantments", 9) ? !this.tag.getList("Enchantments", 10).isEmpty() : false;
	}

	public void setChildTag(String string, net.minecraft.nbt.Tag tag) {
		this.getOrCreateTag().put(string, tag);
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
		return this.hasTag() && this.tag.containsKey("RepairCost", 3) ? this.tag.getInt("RepairCost") : 0;
	}

	public void setRepairCost(int i) {
		this.getOrCreateTag().putInt("RepairCost", i);
	}

	public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
		Multimap<String, EntityAttributeModifier> multimap;
		if (this.hasTag() && this.tag.containsKey("AttributeModifiers", 9)) {
			multimap = HashMultimap.create();
			ListTag listTag = this.tag.getList("AttributeModifiers", 10);

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag = listTag.getCompoundTag(i);
				EntityAttributeModifier entityAttributeModifier = EntityAttributes.createFromTag(compoundTag);
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

	public Component toTextComponent() {
		Component component = new TextComponent("").append(this.getDisplayName());
		if (this.hasDisplayName()) {
			component.applyFormat(ChatFormat.field_1056);
		}

		Component component2 = Components.bracketed(component);
		if (!this.empty) {
			CompoundTag compoundTag = this.toTag(new CompoundTag());
			component2.applyFormat(this.getRarity().formatting)
				.modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11757, new TextComponent(compoundTag.toString()))));
		}

		return component2;
	}

	private static boolean areBlocksEqual(CachedBlockPosition cachedBlockPosition, @Nullable CachedBlockPosition cachedBlockPosition2) {
		if (cachedBlockPosition2 == null || cachedBlockPosition.getBlockState() != cachedBlockPosition2.getBlockState()) {
			return false;
		} else if (cachedBlockPosition.getBlockEntity() == null && cachedBlockPosition2.getBlockEntity() == null) {
			return true;
		} else {
			return cachedBlockPosition.getBlockEntity() != null && cachedBlockPosition2.getBlockEntity() != null
				? Objects.equals(cachedBlockPosition.getBlockEntity().toTag(new CompoundTag()), cachedBlockPosition2.getBlockEntity().toTag(new CompoundTag()))
				: false;
		}
	}

	public boolean getCustomCanHarvest(RegistryTagManager registryTagManager, CachedBlockPosition cachedBlockPosition) {
		if (areBlocksEqual(cachedBlockPosition, this.lastCheckedCanHarvestBlock)) {
			return this.lastCheckedCanHarvestResult;
		} else {
			this.lastCheckedCanHarvestBlock = cachedBlockPosition;
			if (this.hasTag() && this.tag.containsKey("CanDestroy", 9)) {
				ListTag listTag = this.tag.getList("CanDestroy", 8);

				for (int i = 0; i < listTag.size(); i++) {
					String string = listTag.getString(i);

					try {
						Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.create().method_9642(new StringReader(string)).create(registryTagManager);
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

	public boolean getCustomCanPlace(RegistryTagManager registryTagManager, CachedBlockPosition cachedBlockPosition) {
		if (areBlocksEqual(cachedBlockPosition, this.lastCheckedCanPlaceBlock)) {
			return this.lastCheckedCanPlaceResult;
		} else {
			this.lastCheckedCanPlaceBlock = cachedBlockPosition;
			if (this.hasTag() && this.tag.containsKey("CanPlaceOn", 9)) {
				ListTag listTag = this.tag.getList("CanPlaceOn", 8);

				for (int i = 0; i < listTag.size(); i++) {
					String string = listTag.getString(i);

					try {
						Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.create().method_9642(new StringReader(string)).create(registryTagManager);
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
		this.getItem().onUsingTick(world, livingEntity, this, i);
	}

	public boolean isFood() {
		return this.getItem().isFood();
	}
}
