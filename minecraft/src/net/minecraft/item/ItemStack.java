package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a stack of items.
 * 
 * <h2 id="nbt-operations">NBT operations</h2>
 * 
 * <h3>NBT serialization</h3>
 * 
 * An Item Stack can be serialized with {@link #writeNbt(NbtCompound)}, and deserialized with {@link #fromNbt(NbtCompound)}.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Serialized NBT Structure</caption>
 * <tr>
 *   <th>Key</th><th>Type</th><th>Purpose</th>
 * </tr>
 * <tr>
 *   <td>{@code id}</td><td>{@link net.minecraft.nbt.NbtString}</td><td>The identifier of the item.</td>
 * </tr>
 * <tr>
 *   <td>{@code Count}</td><td>{@link net.minecraft.nbt.NbtByte}</td><td>The count of items in the stack.</td>
 * </tr>
 * <tr>
 *   <td>{@code tag}</td><td>{@link NbtCompound}</td><td>The item stack's custom NBT.</td>
 * </tr>
 * </table>
 * </div>
 * 
 * <h3>Custom NBT</h3>
 * 
 * The item stack's custom NBT may be used to store extra information,
 * like the block entity data for shulker boxes,
 * or the damage of a damageable item, etc.
 * <p>
 * Various methods are available to interact with the custom NBT, some methods might refer to a "sub NBT",
 * a sub NBT is a child element of the custom NBT.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Custom NBT operations</caption>
 * <tr>
 *   <th>Category</th><th>Method</th><th>Summary</th>
 * </tr>
 * <tr>
 *   <td>Custom NBT</td><td>{@link #hasNbt()}</td><td>Returns whether the item stack has custom NBT.</td>
 * </tr>
 * <tr>
 *   <td>Custom NBT</td><td>{@link #getNbt()}</td><td>Returns the custom NBT of the item stack.</td>
 * </tr>
 * <tr>
 *   <td>Custom NBT</td><td>{@link #getOrCreateNbt()}</td><td>Returns the custom NBT of the item stack, or creates one if absent.</td>
 * </tr>
 * <tr>
 *   <td>Custom NBT</td><td>{@link #setNbt(NbtCompound)}</td><td>Sets the custom NBT of the item stack.</td>
 * </tr>
 * <tr>
 *   <td>Sub Custom NBT</td><td>{@link #getSubNbt(String)}</td><td>Returns the sub NBT compound at the specified key.</td>
 * </tr>
 * <tr>
 *   <td>Sub Custom NBT</td><td>{@link #getOrCreateSubNbt(String)}</td><td>Returns the sub NBT compound at the specified key, or create one if absent.</td>
 * </tr>
 * <tr>
 *   <td>Sub Custom NBT</td><td>{@link #removeSubNbt(String)}</td><td>Removes the sub NBT element at the specified key.</td>
 * </tr>
 * <tr>
 *   <td>Sub Custom NBT</td><td>{@link #setSubNbt(String, NbtElement)}</td><td>Sets the sub NBT element at the specified key.</td>
 * </tr>
 * </table>
 * </div>
 */
public final class ItemStack {
	public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Registry.ITEM.fieldOf("id").forGetter(stack -> stack.item),
					Codec.INT.fieldOf("Count").forGetter(stack -> stack.count),
					NbtCompound.CODEC.optionalFieldOf("tag").forGetter(stack -> Optional.ofNullable(stack.nbt))
				)
				.apply(instance, ItemStack::new)
	);
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ItemStack EMPTY = new ItemStack((Item)null);
	public static final DecimalFormat MODIFIER_FORMAT = Util.make(
		new DecimalFormat("#.##"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT))
	);
	/**
	 * The key of the enchantments in an item stack's custom NBT, whose value is {@value}.
	 */
	public static final String ENCHANTMENTS_KEY = "Enchantments";
	/**
	 * The key of the display NBT in an item stack's custom NBT, whose value is {@value}.
	 */
	public static final String DISPLAY_KEY = "display";
	/**
	 * The key of the item stack's name in the {@linkplain #DISPLAY_KEY display NBT}, whose value is {@value}.
	 */
	public static final String NAME_KEY = "Name";
	/**
	 * The key of the item stack's lore in the {@linkplain #DISPLAY_KEY display NBT}, whose value is {@value}.
	 */
	public static final String LORE_KEY = "Lore";
	/**
	 * The key of the damage in an item stack's custom NBT, whose value is {@value}.
	 */
	public static final String DAMAGE_KEY = "Damage";
	/**
	 * The key of the item's color in the {@linkplain #DISPLAY_KEY display NBT}, whose value is {@value}.
	 */
	public static final String COLOR_KEY = "color";
	/**
	 * The key of the unbreakable boolean in an item stack's custom NBT, whose value is {@value}.
	 */
	private static final String UNBREAKABLE_KEY = "Unbreakable";
	/**
	 * The key of the repair cost in an item stack's custom NBT, whose value is {@value}.
	 */
	private static final String REPAIR_COST_KEY = "RepairCost";
	private static final String CAN_DESTROY_KEY = "CanDestroy";
	private static final String CAN_PLACE_ON_KEY = "CanPlaceOn";
	private static final String HIDE_FLAGS_KEY = "HideFlags";
	private static final int field_30903 = 0;
	private static final Style LORE_STYLE = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
	private int count;
	private int cooldown;
	@Deprecated
	private final Item item;
	/**
	 * Repesents the item stack's custom NBT.
	 * <p>
	 * Stored at the key {@code tag} in the serialized item stack NBT.
	 * 
	 * @see <a href="nbt-operations">Item Stack NBT Operations</a>
	 */
	private NbtCompound nbt;
	private boolean empty;
	private Entity holder;
	private CachedBlockPosition lastDestroyPos;
	private boolean lastDestroyResult;
	private CachedBlockPosition lastPlaceOnPos;
	private boolean lastPlaceOnResult;

	public Optional<TooltipData> getTooltipData() {
		return this.getItem().getTooltipData(this);
	}

	public ItemStack(ItemConvertible item) {
		this(item, 1);
	}

	private ItemStack(ItemConvertible item, int count, Optional<NbtCompound> nbt) {
		this(item, count);
		nbt.ifPresent(this::setNbt);
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

	private ItemStack(NbtCompound nbt) {
		this.item = Registry.ITEM.get(new Identifier(nbt.getString("id")));
		this.count = nbt.getByte("Count");
		if (nbt.contains("tag", NbtElement.COMPOUND_TYPE)) {
			this.nbt = nbt.getCompound("tag");
			this.getItem().postProcessNbt(this.nbt);
		}

		if (this.getItem().isDamageable()) {
			this.setDamage(this.getDamage());
		}

		this.updateEmptyState();
	}

	/**
	 * Deserializes an item stack from NBT.
	 * 
	 * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
	 */
	public static ItemStack fromNbt(NbtCompound nbt) {
		try {
			return new ItemStack(nbt);
		} catch (RuntimeException var2) {
			LOGGER.debug("Tried to load invalid item: {}", nbt, var2);
			return EMPTY;
		}
	}

	/**
	 * {@return whether this item stack is empty}
	 */
	public boolean isEmpty() {
		if (this == EMPTY) {
			return true;
		} else {
			return this.getItem() == null || this.isOf(Items.AIR) ? true : this.count <= 0;
		}
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

	public boolean isIn(Tag<Item> tag) {
		return tag.contains(this.getItem());
	}

	public boolean isOf(Item item) {
		return this.getItem() == item;
	}

	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		BlockPos blockPos = context.getBlockPos();
		CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(context.getWorld(), blockPos, false);
		if (playerEntity != null && !playerEntity.getAbilities().allowModifyWorld && !this.canPlaceOn(context.getWorld().getTagManager(), cachedBlockPosition)) {
			return ActionResult.PASS;
		} else {
			Item item = this.getItem();
			ActionResult actionResult = item.useOnBlock(context);
			if (playerEntity != null && actionResult.shouldIncrementStat()) {
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
			}

			return actionResult;
		}
	}

	public float getMiningSpeedMultiplier(BlockState state) {
		return this.getItem().getMiningSpeedMultiplier(this, state);
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return this.getItem().use(world, user, hand);
	}

	public ItemStack finishUsing(World world, LivingEntity user) {
		return this.getItem().finishUsing(this, world, user);
	}

	/**
	 * Writes the serialized item stack into the given {@link NbtCompound}.
	 * 
	 * @return the written NBT compound
	 * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
	 * 
	 * @param nbt the NBT compound to write to
	 */
	public NbtCompound writeNbt(NbtCompound nbt) {
		Identifier identifier = Registry.ITEM.getId(this.getItem());
		nbt.putString("id", identifier == null ? "minecraft:air" : identifier.toString());
		nbt.putByte("Count", (byte)this.count);
		if (this.nbt != null) {
			nbt.put("tag", this.nbt.copy());
		}

		return nbt;
	}

	public int getMaxCount() {
		return this.getItem().getMaxCount();
	}

	public boolean isStackable() {
		return this.getMaxCount() > 1 && (!this.isDamageable() || !this.isDamaged());
	}

	public boolean isDamageable() {
		if (!this.empty && this.getItem().getMaxDamage() > 0) {
			NbtCompound nbtCompound = this.getNbt();
			return nbtCompound == null || !nbtCompound.getBoolean("Unbreakable");
		} else {
			return false;
		}
	}

	public boolean isDamaged() {
		return this.isDamageable() && this.getDamage() > 0;
	}

	public int getDamage() {
		return this.nbt == null ? 0 : this.nbt.getInt("Damage");
	}

	public void setDamage(int damage) {
		this.getOrCreateNbt().putInt("Damage", Math.max(0, damage));
	}

	public int getMaxDamage() {
		return this.getItem().getMaxDamage();
	}

	public boolean damage(int amount, Random random, @Nullable ServerPlayerEntity player) {
		if (!this.isDamageable()) {
			return false;
		} else {
			if (amount > 0) {
				int i = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, this);
				int j = 0;

				for (int k = 0; i > 0 && k < amount; k++) {
					if (UnbreakingEnchantment.shouldPreventDamage(this, i, random)) {
						j++;
					}
				}

				amount -= j;
				if (amount <= 0) {
					return false;
				}
			}

			if (player != null && amount != 0) {
				Criteria.ITEM_DURABILITY_CHANGED.trigger(player, this, this.getDamage() + amount);
			}

			int i = this.getDamage() + amount;
			this.setDamage(i);
			return i >= this.getMaxDamage();
		}
	}

	public <T extends LivingEntity> void damage(int amount, T entity, Consumer<T> breakCallback) {
		if (!entity.world.isClient && (!(entity instanceof PlayerEntity) || !((PlayerEntity)entity).getAbilities().creativeMode)) {
			if (this.isDamageable()) {
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
		}
	}

	public boolean isItemBarVisible() {
		return this.item.isItemBarVisible(this);
	}

	public int getItemBarStep() {
		return this.item.getItemBarStep(this);
	}

	public int getItemBarColor() {
		return this.item.getItemBarColor(this);
	}

	public boolean onStackClicked(Slot slot, ClickType clickType, PlayerEntity player) {
		return this.getItem().onStackClicked(this, slot, clickType, player);
	}

	public boolean onClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		return this.getItem().onClicked(this, stack, slot, clickType, player, cursorStackReference);
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

	/**
	 * Determines whether this item can be used as a suitable tool for mining the specified block.
	 * <p>
	 * Depending on block implementation, when combined together, the correct item and block may achieve a better mining speed and yield
	 * drops that would not be obtained when mining otherwise.
	 * 
	 * @return values consistent with calls to {@link Item#isSuitableFor}
	 * @see Item#isSuitableFor(BlockState)
	 */
	public boolean isSuitableFor(BlockState state) {
		return this.getItem().isSuitableFor(state);
	}

	public ActionResult useOnEntity(PlayerEntity user, LivingEntity entity, Hand hand) {
		return this.getItem().useOnEntity(this, user, entity, hand);
	}

	/**
	 * Creates and returns a copy of this item stack.
	 */
	public ItemStack copy() {
		if (this.isEmpty()) {
			return EMPTY;
		} else {
			ItemStack itemStack = new ItemStack(this.getItem(), this.count);
			itemStack.setCooldown(this.getCooldown());
			if (this.nbt != null) {
				itemStack.nbt = this.nbt.copy();
			}

			return itemStack;
		}
	}

	/**
	 * {@return whether the given item stacks have equivalent custom NBT}
	 */
	public static boolean areNbtEqual(ItemStack left, ItemStack right) {
		if (left.isEmpty() && right.isEmpty()) {
			return true;
		} else if (left.isEmpty() || right.isEmpty()) {
			return false;
		} else {
			return left.nbt == null && right.nbt != null ? false : left.nbt == null || left.nbt.equals(right.nbt);
		}
	}

	public static boolean areEqual(ItemStack left, ItemStack right) {
		if (left.isEmpty() && right.isEmpty()) {
			return true;
		} else {
			return !left.isEmpty() && !right.isEmpty() ? left.isEqual(right) : false;
		}
	}

	private boolean isEqual(ItemStack stack) {
		if (this.count != stack.count) {
			return false;
		} else if (!this.isOf(stack.getItem())) {
			return false;
		} else {
			return this.nbt == null && stack.nbt != null ? false : this.nbt == null || this.nbt.equals(stack.nbt);
		}
	}

	public static boolean areItemsEqualIgnoreDamage(ItemStack left, ItemStack right) {
		if (left == right) {
			return true;
		} else {
			return !left.isEmpty() && !right.isEmpty() ? left.isItemEqualIgnoreDamage(right) : false;
		}
	}

	public static boolean areItemsEqual(ItemStack left, ItemStack right) {
		if (left == right) {
			return true;
		} else {
			return !left.isEmpty() && !right.isEmpty() ? left.isItemEqual(right) : false;
		}
	}

	public boolean isItemEqualIgnoreDamage(ItemStack stack) {
		return !stack.isEmpty() && this.isOf(stack.getItem());
	}

	public boolean isItemEqual(ItemStack stack) {
		return !this.isDamageable() ? this.isItemEqualIgnoreDamage(stack) : !stack.isEmpty() && this.isOf(stack.getItem());
	}

	public static boolean canCombine(ItemStack stack, ItemStack otherStack) {
		return stack.isOf(otherStack.getItem()) && areNbtEqual(stack, otherStack);
	}

	public String getTranslationKey() {
		return this.getItem().getTranslationKey(this);
	}

	public String toString() {
		return this.count + " " + this.getItem();
	}

	public void inventoryTick(World world, Entity entity, int slot, boolean selected) {
		if (this.cooldown > 0) {
			this.cooldown--;
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

	/**
	 * {@return whether this item stack has custom NBT}
	 * 
	 * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
	 */
	public boolean hasNbt() {
		return !this.empty && this.nbt != null && !this.nbt.isEmpty();
	}

	/**
	 * {@return the custom NBT of this item stack, may be {@code null}}
	 * 
	 * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
	 */
	@Nullable
	public NbtCompound getNbt() {
		return this.nbt;
	}

	/**
	 * Returns the custom NBT of this item stack, or creates the custom NBT if the item stack did not have a custom NBT previously.
	 * 
	 * @return the custom NBT of this item stack
	 * 
	 * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
	 */
	public NbtCompound getOrCreateNbt() {
		if (this.nbt == null) {
			this.setNbt(new NbtCompound());
		}

		return this.nbt;
	}

	/**
	 * {@return the compound NBT at the specified key in this item stack's NBT, or a new compound if absent}
	 * 
	 * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
	 */
	public NbtCompound getOrCreateSubNbt(String key) {
		if (this.nbt != null && this.nbt.contains(key, NbtElement.COMPOUND_TYPE)) {
			return this.nbt.getCompound(key);
		} else {
			NbtCompound nbtCompound = new NbtCompound();
			this.setSubNbt(key, nbtCompound);
			return nbtCompound;
		}
	}

	/**
	 * {@return the NBT compound at the specified key in this item stack's custom NBT, may be {@code null}}
	 * 
	 * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
	 */
	@Nullable
	public NbtCompound getSubNbt(String key) {
		return this.nbt != null && this.nbt.contains(key, NbtElement.COMPOUND_TYPE) ? this.nbt.getCompound(key) : null;
	}

	/**
	 * Removes the sub NBT element at the specified key in this item stack's custom NBT.
	 * 
	 * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
	 */
	public void removeSubNbt(String key) {
		if (this.nbt != null && this.nbt.contains(key)) {
			this.nbt.remove(key);
			if (this.nbt.isEmpty()) {
				this.nbt = null;
			}
		}
	}

	public NbtList getEnchantments() {
		return this.nbt != null ? this.nbt.getList("Enchantments", NbtElement.COMPOUND_TYPE) : new NbtList();
	}

	/**
	 * Sets the custom NBT of this item stack.
	 * 
	 * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
	 * 
	 * @param nbt the custom NBT compound, may be {@code null} to reset
	 */
	public void setNbt(@Nullable NbtCompound nbt) {
		this.nbt = nbt;
		if (this.getItem().isDamageable()) {
			this.setDamage(this.getDamage());
		}

		if (nbt != null) {
			this.getItem().postProcessNbt(nbt);
		}
	}

	public Text getName() {
		NbtCompound nbtCompound = this.getSubNbt("display");
		if (nbtCompound != null && nbtCompound.contains("Name", NbtElement.STRING_TYPE)) {
			try {
				Text text = Text.Serializer.fromJson(nbtCompound.getString("Name"));
				if (text != null) {
					return text;
				}

				nbtCompound.remove("Name");
			} catch (JsonParseException var3) {
				nbtCompound.remove("Name");
			}
		}

		return this.getItem().getName(this);
	}

	public ItemStack setCustomName(@Nullable Text name) {
		NbtCompound nbtCompound = this.getOrCreateSubNbt("display");
		if (name != null) {
			nbtCompound.putString("Name", Text.Serializer.toJson(name));
		} else {
			nbtCompound.remove("Name");
		}

		return this;
	}

	public void removeCustomName() {
		NbtCompound nbtCompound = this.getSubNbt("display");
		if (nbtCompound != null) {
			nbtCompound.remove("Name");
			if (nbtCompound.isEmpty()) {
				this.removeSubNbt("display");
			}
		}

		if (this.nbt != null && this.nbt.isEmpty()) {
			this.nbt = null;
		}
	}

	public boolean hasCustomName() {
		NbtCompound nbtCompound = this.getSubNbt("display");
		return nbtCompound != null && nbtCompound.contains("Name", NbtElement.STRING_TYPE);
	}

	public List<Text> getTooltip(@Nullable PlayerEntity player, TooltipContext context) {
		List<Text> list = Lists.<Text>newArrayList();
		MutableText mutableText = new LiteralText("").append(this.getName()).formatted(this.getRarity().formatting);
		if (this.hasCustomName()) {
			mutableText.formatted(Formatting.ITALIC);
		}

		list.add(mutableText);
		if (!context.isAdvanced() && !this.hasCustomName() && this.isOf(Items.FILLED_MAP)) {
			Integer integer = FilledMapItem.getMapId(this);
			if (integer != null) {
				list.add(new LiteralText("#" + integer).formatted(Formatting.GRAY));
			}
		}

		int i = this.getHideFlags();
		if (isSectionVisible(i, ItemStack.TooltipSection.ADDITIONAL)) {
			this.getItem().appendTooltip(this, player == null ? null : player.world, list, context);
		}

		if (this.hasNbt()) {
			if (isSectionVisible(i, ItemStack.TooltipSection.ENCHANTMENTS)) {
				appendEnchantments(list, this.getEnchantments());
			}

			if (this.nbt.contains("display", NbtElement.COMPOUND_TYPE)) {
				NbtCompound nbtCompound = this.nbt.getCompound("display");
				if (isSectionVisible(i, ItemStack.TooltipSection.DYE) && nbtCompound.contains("color", NbtElement.NUMBER_TYPE)) {
					if (context.isAdvanced()) {
						list.add(new TranslatableText("item.color", String.format("#%06X", nbtCompound.getInt("color"))).formatted(Formatting.GRAY));
					} else {
						list.add(new TranslatableText("item.dyed").formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC}));
					}
				}

				if (nbtCompound.getType("Lore") == NbtElement.LIST_TYPE) {
					NbtList nbtList = nbtCompound.getList("Lore", NbtElement.STRING_TYPE);

					for (int j = 0; j < nbtList.size(); j++) {
						String string = nbtList.getString(j);

						try {
							MutableText mutableText2 = Text.Serializer.fromJson(string);
							if (mutableText2 != null) {
								list.add(Texts.setStyleIfAbsent(mutableText2, LORE_STYLE));
							}
						} catch (JsonParseException var19) {
							nbtCompound.remove("Lore");
						}
					}
				}
			}
		}

		if (isSectionVisible(i, ItemStack.TooltipSection.MODIFIERS)) {
			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				Multimap<EntityAttribute, EntityAttributeModifier> multimap = this.getAttributeModifiers(equipmentSlot);
				if (!multimap.isEmpty()) {
					list.add(LiteralText.EMPTY);
					list.add(new TranslatableText("item.modifiers." + equipmentSlot.getName()).formatted(Formatting.GRAY));

					for (Entry<EntityAttribute, EntityAttributeModifier> entry : multimap.entries()) {
						EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)entry.getValue();
						double d = entityAttributeModifier.getValue();
						boolean bl = false;
						if (player != null) {
							if (entityAttributeModifier.getId() == Item.ATTACK_DAMAGE_MODIFIER_ID) {
								d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
								d += (double)EnchantmentHelper.getAttackDamage(this, EntityGroup.DEFAULT);
								bl = true;
							} else if (entityAttributeModifier.getId() == Item.ATTACK_SPEED_MODIFIER_ID) {
								d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED);
								bl = true;
							}
						}

						double e;
						if (entityAttributeModifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE
							|| entityAttributeModifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
							e = d * 100.0;
						} else if (((EntityAttribute)entry.getKey()).equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)) {
							e = d * 10.0;
						} else {
							e = d;
						}

						if (bl) {
							list.add(
								new LiteralText(" ")
									.append(
										new TranslatableText(
											"attribute.modifier.equals." + entityAttributeModifier.getOperation().getId(),
											MODIFIER_FORMAT.format(e),
											new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey())
										)
									)
									.formatted(Formatting.DARK_GREEN)
							);
						} else if (d > 0.0) {
							list.add(
								new TranslatableText(
										"attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(),
										MODIFIER_FORMAT.format(e),
										new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey())
									)
									.formatted(Formatting.BLUE)
							);
						} else if (d < 0.0) {
							e *= -1.0;
							list.add(
								new TranslatableText(
										"attribute.modifier.take." + entityAttributeModifier.getOperation().getId(),
										MODIFIER_FORMAT.format(e),
										new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey())
									)
									.formatted(Formatting.RED)
							);
						}
					}
				}
			}
		}

		if (this.hasNbt()) {
			if (isSectionVisible(i, ItemStack.TooltipSection.UNBREAKABLE) && this.nbt.getBoolean("Unbreakable")) {
				list.add(new TranslatableText("item.unbreakable").formatted(Formatting.BLUE));
			}

			if (isSectionVisible(i, ItemStack.TooltipSection.CAN_DESTROY) && this.nbt.contains("CanDestroy", NbtElement.LIST_TYPE)) {
				NbtList nbtList2 = this.nbt.getList("CanDestroy", NbtElement.STRING_TYPE);
				if (!nbtList2.isEmpty()) {
					list.add(LiteralText.EMPTY);
					list.add(new TranslatableText("item.canBreak").formatted(Formatting.GRAY));

					for (int k = 0; k < nbtList2.size(); k++) {
						list.addAll(parseBlockTag(nbtList2.getString(k)));
					}
				}
			}

			if (isSectionVisible(i, ItemStack.TooltipSection.CAN_PLACE) && this.nbt.contains("CanPlaceOn", NbtElement.LIST_TYPE)) {
				NbtList nbtList2 = this.nbt.getList("CanPlaceOn", NbtElement.STRING_TYPE);
				if (!nbtList2.isEmpty()) {
					list.add(LiteralText.EMPTY);
					list.add(new TranslatableText("item.canPlace").formatted(Formatting.GRAY));

					for (int k = 0; k < nbtList2.size(); k++) {
						list.addAll(parseBlockTag(nbtList2.getString(k)));
					}
				}
			}
		}

		if (context.isAdvanced()) {
			if (this.isDamaged()) {
				list.add(new TranslatableText("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
			}

			list.add(new LiteralText(Registry.ITEM.getId(this.getItem()).toString()).formatted(Formatting.DARK_GRAY));
			if (this.hasNbt()) {
				list.add(new TranslatableText("item.nbt_tags", this.nbt.getKeys().size()).formatted(Formatting.DARK_GRAY));
			}
		}

		return list;
	}

	/**
	 * Determines whether the given tooltip section will be visible according to the given flags.
	 */
	private static boolean isSectionVisible(int flags, ItemStack.TooltipSection tooltipSection) {
		return (flags & tooltipSection.getFlag()) == 0;
	}

	private int getHideFlags() {
		return this.hasNbt() && this.nbt.contains("HideFlags", NbtElement.NUMBER_TYPE) ? this.nbt.getInt("HideFlags") : 0;
	}

	public void addHideFlag(ItemStack.TooltipSection tooltipSection) {
		NbtCompound nbtCompound = this.getOrCreateNbt();
		nbtCompound.putInt("HideFlags", nbtCompound.getInt("HideFlags") | tooltipSection.getFlag());
	}

	public static void appendEnchantments(List<Text> tooltip, NbtList enchantments) {
		for (int i = 0; i < enchantments.size(); i++) {
			NbtCompound nbtCompound = enchantments.getCompound(i);
			Registry.ENCHANTMENT
				.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound))
				.ifPresent(e -> tooltip.add(e.getName(EnchantmentHelper.getLevelFromNbt(nbtCompound))));
		}
	}

	private static Collection<Text> parseBlockTag(String tag) {
		try {
			BlockArgumentParser blockArgumentParser = new BlockArgumentParser(new StringReader(tag), true).parse(true);
			BlockState blockState = blockArgumentParser.getBlockState();
			Identifier identifier = blockArgumentParser.getTagId();
			boolean bl = blockState != null;
			boolean bl2 = identifier != null;
			if (bl || bl2) {
				if (bl) {
					return Lists.<Text>newArrayList(blockState.getBlock().getName().formatted(Formatting.DARK_GRAY));
				}

				Tag<Block> tag2 = BlockTags.getTagGroup().getTag(identifier);
				if (tag2 != null) {
					Collection<Block> collection = tag2.values();
					if (!collection.isEmpty()) {
						return (Collection<Text>)collection.stream().map(Block::getName).map(text -> text.formatted(Formatting.DARK_GRAY)).collect(Collectors.toList());
					}
				}
			}
		} catch (CommandSyntaxException var8) {
		}

		return Lists.<Text>newArrayList(new LiteralText("missingno").formatted(Formatting.DARK_GRAY));
	}

	public boolean hasGlint() {
		return this.getItem().hasGlint(this);
	}

	public Rarity getRarity() {
		return this.getItem().getRarity(this);
	}

	public boolean isEnchantable() {
		return !this.getItem().isEnchantable(this) ? false : !this.hasEnchantments();
	}

	public void addEnchantment(Enchantment enchantment, int level) {
		this.getOrCreateNbt();
		if (!this.nbt.contains("Enchantments", NbtElement.LIST_TYPE)) {
			this.nbt.put("Enchantments", new NbtList());
		}

		NbtList nbtList = this.nbt.getList("Enchantments", NbtElement.COMPOUND_TYPE);
		nbtList.add(EnchantmentHelper.createNbt(EnchantmentHelper.getEnchantmentId(enchantment), (byte)level));
	}

	public boolean hasEnchantments() {
		return this.nbt != null && this.nbt.contains("Enchantments", NbtElement.LIST_TYPE)
			? !this.nbt.getList("Enchantments", NbtElement.COMPOUND_TYPE).isEmpty()
			: false;
	}

	/**
	 * Sets the given NBT element in the item stack's custom NBT at the specified key.
	 * 
	 * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
	 * 
	 * @param key the key where to put the given {@link NbtElement}
	 * @param element the NBT element to put
	 */
	public void setSubNbt(String key, NbtElement element) {
		this.getOrCreateNbt().put(key, element);
	}

	public boolean isInFrame() {
		return this.holder instanceof ItemFrameEntity;
	}

	public void setHolder(@Nullable Entity holder) {
		this.holder = holder;
	}

	@Nullable
	public ItemFrameEntity getFrame() {
		return this.holder instanceof ItemFrameEntity ? (ItemFrameEntity)this.getHolder() : null;
	}

	@Nullable
	public Entity getHolder() {
		return !this.empty ? this.holder : null;
	}

	public int getRepairCost() {
		return this.hasNbt() && this.nbt.contains("RepairCost", NbtElement.INT_TYPE) ? this.nbt.getInt("RepairCost") : 0;
	}

	public void setRepairCost(int repairCost) {
		this.getOrCreateNbt().putInt("RepairCost", repairCost);
	}

	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		Multimap<EntityAttribute, EntityAttributeModifier> multimap;
		if (this.hasNbt() && this.nbt.contains("AttributeModifiers", NbtElement.LIST_TYPE)) {
			multimap = HashMultimap.create();
			NbtList nbtList = this.nbt.getList("AttributeModifiers", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				if (!nbtCompound.contains("Slot", NbtElement.STRING_TYPE) || nbtCompound.getString("Slot").equals(slot.getName())) {
					Optional<EntityAttribute> optional = Registry.ATTRIBUTE.getOrEmpty(Identifier.tryParse(nbtCompound.getString("AttributeName")));
					if (optional.isPresent()) {
						EntityAttributeModifier entityAttributeModifier = EntityAttributeModifier.fromNbt(nbtCompound);
						if (entityAttributeModifier != null
							&& entityAttributeModifier.getId().getLeastSignificantBits() != 0L
							&& entityAttributeModifier.getId().getMostSignificantBits() != 0L) {
							multimap.put((EntityAttribute)optional.get(), entityAttributeModifier);
						}
					}
				}
			}
		} else {
			multimap = this.getItem().getAttributeModifiers(slot);
		}

		return multimap;
	}

	public void addAttributeModifier(EntityAttribute attribute, EntityAttributeModifier modifier, @Nullable EquipmentSlot slot) {
		this.getOrCreateNbt();
		if (!this.nbt.contains("AttributeModifiers", NbtElement.LIST_TYPE)) {
			this.nbt.put("AttributeModifiers", new NbtList());
		}

		NbtList nbtList = this.nbt.getList("AttributeModifiers", NbtElement.COMPOUND_TYPE);
		NbtCompound nbtCompound = modifier.toNbt();
		nbtCompound.putString("AttributeName", Registry.ATTRIBUTE.getId(attribute).toString());
		if (slot != null) {
			nbtCompound.putString("Slot", slot.getName());
		}

		nbtList.add(nbtCompound);
	}

	public Text toHoverableText() {
		MutableText mutableText = new LiteralText("").append(this.getName());
		if (this.hasCustomName()) {
			mutableText.formatted(Formatting.ITALIC);
		}

		MutableText mutableText2 = Texts.bracketed(mutableText);
		if (!this.empty) {
			mutableText2.formatted(this.getRarity().formatting)
				.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(this))));
		}

		return mutableText2;
	}

	private static boolean areBlocksEqual(CachedBlockPosition first, @Nullable CachedBlockPosition second) {
		if (second == null || first.getBlockState() != second.getBlockState()) {
			return false;
		} else if (first.getBlockEntity() == null && second.getBlockEntity() == null) {
			return true;
		} else {
			return first.getBlockEntity() != null && second.getBlockEntity() != null
				? Objects.equals(first.getBlockEntity().writeNbt(new NbtCompound()), second.getBlockEntity().writeNbt(new NbtCompound()))
				: false;
		}
	}

	public boolean canDestroy(TagManager tagManager, CachedBlockPosition pos) {
		if (areBlocksEqual(pos, this.lastDestroyPos)) {
			return this.lastDestroyResult;
		} else {
			this.lastDestroyPos = pos;
			if (this.hasNbt() && this.nbt.contains("CanDestroy", NbtElement.LIST_TYPE)) {
				NbtList nbtList = this.nbt.getList("CanDestroy", NbtElement.STRING_TYPE);

				for (int i = 0; i < nbtList.size(); i++) {
					String string = nbtList.getString(i);

					try {
						Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().parse(new StringReader(string)).create(tagManager);
						if (predicate.test(pos)) {
							this.lastDestroyResult = true;
							return true;
						}
					} catch (CommandSyntaxException var7) {
					}
				}
			}

			this.lastDestroyResult = false;
			return false;
		}
	}

	public boolean canPlaceOn(TagManager tagManager, CachedBlockPosition pos) {
		if (areBlocksEqual(pos, this.lastPlaceOnPos)) {
			return this.lastPlaceOnResult;
		} else {
			this.lastPlaceOnPos = pos;
			if (this.hasNbt() && this.nbt.contains("CanPlaceOn", NbtElement.LIST_TYPE)) {
				NbtList nbtList = this.nbt.getList("CanPlaceOn", NbtElement.STRING_TYPE);

				for (int i = 0; i < nbtList.size(); i++) {
					String string = nbtList.getString(i);

					try {
						Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().parse(new StringReader(string)).create(tagManager);
						if (predicate.test(pos)) {
							this.lastPlaceOnResult = true;
							return true;
						}
					} catch (CommandSyntaxException var7) {
					}
				}
			}

			this.lastPlaceOnResult = false;
			return false;
		}
	}

	public int getCooldown() {
		return this.cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	/**
	 * {@return the count of items in this item stack}
	 */
	public int getCount() {
		return this.empty ? 0 : this.count;
	}

	/**
	 * Sets the count of items in this item stack.
	 * 
	 * @param count the count of items
	 */
	public void setCount(int count) {
		this.count = count;
		this.updateEmptyState();
	}

	/**
	 * Increments the count of items in this item stack.
	 * 
	 * @param amount the amount to increment
	 */
	public void increment(int amount) {
		this.setCount(this.count + amount);
	}

	/**
	 * Decrements the count of items in this item stack.
	 * 
	 * @param amount the amount to decrement
	 */
	public void decrement(int amount) {
		this.increment(-amount);
	}

	public void usageTick(World world, LivingEntity user, int remainingUseTicks) {
		this.getItem().usageTick(world, user, this, remainingUseTicks);
	}

	public void onItemEntityDestroyed(ItemEntity entity) {
		this.getItem().onItemEntityDestroyed(entity);
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

	@Nullable
	public SoundEvent getEquipSound() {
		return this.getItem().getEquipSound();
	}

	public static enum TooltipSection {
		ENCHANTMENTS,
		MODIFIERS,
		UNBREAKABLE,
		CAN_DESTROY,
		CAN_PLACE,
		ADDITIONAL,
		DYE;

		private final int flag = 1 << this.ordinal();

		public int getFlag() {
			return this.flag;
		}
	}
}
