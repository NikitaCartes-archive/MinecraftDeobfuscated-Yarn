package net.minecraft.item;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.DamageResistantComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.component.type.UseRemainderComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.NullOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

/**
 * Represents a stack of items. This is a data container that holds the item count
 * and the stack's components. Logics for items (such as the action for using it) are delegated
 * to the stack's logic container, {@link Item}. Instances can be created using one of
 * the constructors and are usually stored in an {@link net.minecraft.inventory.Inventory}.
 * 
 * <p>Item stacks should never be compared using {@code ==} operator or {@code equals}
 * method. This also means they cannot be used as a map key. To check if an item stack
 * is of a certain item, use {@link #isOf(Item)}. To compare two item stacks, use {@link
 * #areItemsEqual} to check the item only, or {@link #areEqual} to also check the item
 * count and the components. Use {@link #isEmpty} to check if an item stack is empty instead
 * of doing {@code stack == ItemStack.EMPTY}.
 * 
 * <p>When storing an item stack in an inventory or other places, make sure that an instance
 * is never stored in multiple places. When two inventories hold the same instance, it
 * will duplicate the item stack (and become two instances) when one is saved and reloaded.
 * 
 * <h2 id="components">Components</h2>
 * <p>Components can be used to store data specific to the item stack.
 * Use {@link ComponentHolder#get} or {@link ComponentHolder#getOrDefault} to
 * get the component values. Use {@link #set} or {@link #remove} to set the components.
 * 
 * <h2 id="nbt-serialization">NBT serialization</h2>
 * <p>An Item Stack can be serialized with {@link #encode(RegistryWrapper.WrapperLookup)}, and deserialized with {@link #fromNbt(RegistryWrapper.WrapperLookup, NbtCompound)}.
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
 *   <td>{@code count}</td><td>{@link net.minecraft.nbt.NbtInt}</td><td>The count of items in the stack.</td>
 * </tr>
 * <tr>
 *   <td>{@code components}</td><td>{@link ComponentChanges}</td><td>The item stack's components.</td>
 * </tr>
 * </table>
 * </div>
 */
public final class ItemStack implements ComponentHolder {
	public static final Codec<ItemStack> CODEC = Codec.lazyInitialized(
		() -> RecordCodecBuilder.create(
				instance -> instance.group(
							Item.ENTRY_CODEC.fieldOf("id").forGetter(ItemStack::getRegistryEntry),
							Codecs.rangedInt(1, 99).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
							ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(stack -> stack.components.getChanges())
						)
						.apply(instance, ItemStack::new)
			)
	);
	public static final Codec<ItemStack> UNCOUNTED_CODEC = Codec.lazyInitialized(
		() -> RecordCodecBuilder.create(
				instance -> instance.group(
							Item.ENTRY_CODEC.fieldOf("id").forGetter(ItemStack::getRegistryEntry),
							ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(stack -> stack.components.getChanges())
						)
						.apply(instance, (item, components) -> new ItemStack(item, 1, components))
			)
	);
	public static final Codec<ItemStack> VALIDATED_CODEC = CODEC.validate(ItemStack::validate);
	public static final Codec<ItemStack> VALIDATED_UNCOUNTED_CODEC = UNCOUNTED_CODEC.validate(ItemStack::validate);
	public static final Codec<ItemStack> OPTIONAL_CODEC = Codecs.optional(CODEC)
		.xmap(optional -> (ItemStack)optional.orElse(ItemStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
	public static final Codec<ItemStack> REGISTRY_ENTRY_CODEC = Item.ENTRY_CODEC.xmap(ItemStack::new, ItemStack::getRegistryEntry);
	public static final PacketCodec<RegistryByteBuf, ItemStack> OPTIONAL_PACKET_CODEC = new PacketCodec<RegistryByteBuf, ItemStack>() {
		private static final PacketCodec<RegistryByteBuf, RegistryEntry<Item>> ITEM_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.ITEM);

		public ItemStack decode(RegistryByteBuf registryByteBuf) {
			int i = registryByteBuf.readVarInt();
			if (i <= 0) {
				return ItemStack.EMPTY;
			} else {
				RegistryEntry<Item> registryEntry = ITEM_PACKET_CODEC.decode(registryByteBuf);
				ComponentChanges componentChanges = ComponentChanges.PACKET_CODEC.decode(registryByteBuf);
				return new ItemStack(registryEntry, i, componentChanges);
			}
		}

		public void encode(RegistryByteBuf registryByteBuf, ItemStack itemStack) {
			if (itemStack.isEmpty()) {
				registryByteBuf.writeVarInt(0);
			} else {
				registryByteBuf.writeVarInt(itemStack.getCount());
				ITEM_PACKET_CODEC.encode(registryByteBuf, itemStack.getRegistryEntry());
				ComponentChanges.PACKET_CODEC.encode(registryByteBuf, itemStack.components.getChanges());
			}
		}
	};
	public static final PacketCodec<RegistryByteBuf, ItemStack> PACKET_CODEC = new PacketCodec<RegistryByteBuf, ItemStack>() {
		public ItemStack decode(RegistryByteBuf registryByteBuf) {
			ItemStack itemStack = ItemStack.OPTIONAL_PACKET_CODEC.decode(registryByteBuf);
			if (itemStack.isEmpty()) {
				throw new DecoderException("Empty ItemStack not allowed");
			} else {
				return itemStack;
			}
		}

		public void encode(RegistryByteBuf registryByteBuf, ItemStack itemStack) {
			if (itemStack.isEmpty()) {
				throw new EncoderException("Empty ItemStack not allowed");
			} else {
				ItemStack.OPTIONAL_PACKET_CODEC.encode(registryByteBuf, itemStack);
			}
		}
	};
	public static final PacketCodec<RegistryByteBuf, List<ItemStack>> OPTIONAL_LIST_PACKET_CODEC = OPTIONAL_PACKET_CODEC.collect(
		PacketCodecs.toCollection(DefaultedList::ofSize)
	);
	private static final Logger LOGGER = LogUtils.getLogger();
	/**
	 * The empty item stack that holds no item.
	 * 
	 * <p>This should never be mutated.
	 * 
	 * @see ItemStack#isEmpty
	 */
	public static final ItemStack EMPTY = new ItemStack((Void)null);
	private static final Text DISABLED_TEXT = Text.translatable("item.disabled").formatted(Formatting.RED);
	private int count;
	private int bobbingAnimationTime;
	@Deprecated
	@Nullable
	private final Item item;
	final MergedComponentMap components;
	@Nullable
	private Entity holder;

	private static DataResult<ItemStack> validate(ItemStack stack) {
		DataResult<Unit> dataResult = validateComponents(stack.getComponents());
		if (dataResult.isError()) {
			return dataResult.map(v -> stack);
		} else {
			return stack.getCount() > stack.getMaxCount()
				? DataResult.error(() -> "Item stack with stack size of " + stack.getCount() + " was larger than maximum: " + stack.getMaxCount())
				: DataResult.success(stack);
		}
	}

	/**
	 * {@return a packet codec that ensures the validity of the decoded stack by
	 * checking if it can be re-encoded}
	 * 
	 * <p>This should be used when serializing {@link ItemStack} in C2S packets.
	 * Encoding is unaffected.
	 */
	public static PacketCodec<RegistryByteBuf, ItemStack> createExtraValidatingPacketCodec(PacketCodec<RegistryByteBuf, ItemStack> basePacketCodec) {
		return new PacketCodec<RegistryByteBuf, ItemStack>() {
			public ItemStack decode(RegistryByteBuf registryByteBuf) {
				ItemStack itemStack = basePacketCodec.decode(registryByteBuf);
				if (!itemStack.isEmpty()) {
					RegistryOps<Unit> registryOps = registryByteBuf.getRegistryManager().getOps(NullOps.INSTANCE);
					ItemStack.CODEC.encodeStart(registryOps, itemStack).getOrThrow(DecoderException::new);
				}

				return itemStack;
			}

			public void encode(RegistryByteBuf registryByteBuf, ItemStack itemStack) {
				basePacketCodec.encode(registryByteBuf, itemStack);
			}
		};
	}

	public Optional<TooltipData> getTooltipData() {
		return this.getItem().getTooltipData(this);
	}

	@Override
	public ComponentMap getComponents() {
		return (ComponentMap)(!this.isEmpty() ? this.components : ComponentMap.EMPTY);
	}

	public void clearComponentChanges() {
		this.components.clearChanges();
	}

	public ComponentMap getDefaultComponents() {
		return !this.isEmpty() ? this.getItem().getComponents() : ComponentMap.EMPTY;
	}

	public ComponentChanges getComponentChanges() {
		return !this.isEmpty() ? this.components.getChanges() : ComponentChanges.EMPTY;
	}

	public ItemStack(ItemConvertible item) {
		this(item, 1);
	}

	public ItemStack(RegistryEntry<Item> entry) {
		this(entry.value(), 1);
	}

	public ItemStack(RegistryEntry<Item> item, int count, ComponentChanges changes) {
		this(item.value(), count, MergedComponentMap.create(item.value().getComponents(), changes));
	}

	public ItemStack(RegistryEntry<Item> itemEntry, int count) {
		this(itemEntry.value(), count);
	}

	public ItemStack(ItemConvertible item, int count) {
		this(item, count, new MergedComponentMap(item.asItem().getComponents()));
	}

	private ItemStack(ItemConvertible item, int count, MergedComponentMap components) {
		this.item = item.asItem();
		this.count = count;
		this.components = components;
		this.getItem().postProcessComponents(this);
	}

	private ItemStack(@Nullable Void v) {
		this.item = null;
		this.components = new MergedComponentMap(ComponentMap.EMPTY);
	}

	public static DataResult<Unit> validateComponents(ComponentMap components) {
		if (components.contains(DataComponentTypes.MAX_DAMAGE) && components.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1) > 1) {
			return DataResult.error(() -> "Item cannot be both damageable and stackable");
		} else {
			ContainerComponent containerComponent = components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);

			for (ItemStack itemStack : containerComponent.iterateNonEmpty()) {
				int i = itemStack.getCount();
				int j = itemStack.getMaxCount();
				if (i > j) {
					return DataResult.error(() -> "Item stack with count of " + i + " was larger than maximum: " + j);
				}
			}

			return DataResult.success(Unit.INSTANCE);
		}
	}

	public static Optional<ItemStack> fromNbt(RegistryWrapper.WrapperLookup registries, NbtElement nbt) {
		return CODEC.parse(registries.getOps(NbtOps.INSTANCE), nbt).resultOrPartial(error -> LOGGER.error("Tried to load invalid item: '{}'", error));
	}

	public static ItemStack fromNbtOrEmpty(RegistryWrapper.WrapperLookup registries, NbtCompound nbt) {
		return nbt.isEmpty() ? EMPTY : (ItemStack)fromNbt(registries, nbt).orElse(EMPTY);
	}

	/**
	 * {@return whether this item stack is empty}
	 */
	public boolean isEmpty() {
		return this == EMPTY || this.item == Items.AIR || this.count <= 0;
	}

	public boolean isItemEnabled(FeatureSet enabledFeatures) {
		return this.isEmpty() || this.getItem().isEnabled(enabledFeatures);
	}

	/**
	 * {@return the copy of the stack "split" from the current stack with item count
	 * being at most {@code amount}}
	 * 
	 * <p>Splitting an item stack mutates this stack so that the sum of the stacks' item
	 * counts does not change. See the example below:
	 * 
	 * <pre>{@code
	 * ItemStack stack = new ItemStack(Items.APPLE, 64);
	 * ItemStack newStack = stack.split(10);
	 * // stack has 54 apples
	 * // newStack has 10 apples
	 * 
	 * ItemStack smallStack = new ItemStack(Items.APPLE, 4);
	 * ItemStack newSmallStack = smallStack.split(10);
	 * // smallStack is now empty
	 * // newSmallStack has 4 apples
	 * }</pre>
	 */
	public ItemStack split(int amount) {
		int i = Math.min(amount, this.getCount());
		ItemStack itemStack = this.copyWithCount(i);
		this.decrement(i);
		return itemStack;
	}

	public ItemStack copyAndEmpty() {
		if (this.isEmpty()) {
			return EMPTY;
		} else {
			ItemStack itemStack = this.copy();
			this.setCount(0);
			return itemStack;
		}
	}

	/**
	 * {@return the item of this stack}
	 * 
	 * @see #isOf(Item)
	 */
	public Item getItem() {
		return this.isEmpty() ? Items.AIR : this.item;
	}

	public RegistryEntry<Item> getRegistryEntry() {
		return this.getItem().getRegistryEntry();
	}

	/**
	 * {@return whether the item is in {@code tag}}
	 */
	public boolean isIn(TagKey<Item> tag) {
		return this.getItem().getRegistryEntry().isIn(tag);
	}

	/**
	 * {@return whether the item is {@code item}}
	 */
	public boolean isOf(Item item) {
		return this.getItem() == item;
	}

	/**
	 * {@return whether the item's registry entry passes the {@code predicate}}
	 * 
	 * @see #itemMatches(RegistryEntry)
	 * @see #isOf(Item)
	 */
	public boolean itemMatches(Predicate<RegistryEntry<Item>> predicate) {
		return predicate.test(this.getItem().getRegistryEntry());
	}

	/**
	 * {@return whether the item's registry entry matches {@code itemEntry}}
	 * 
	 * @see #itemMatches(Predicate)
	 * @see #isOf(Item)
	 */
	public boolean itemMatches(RegistryEntry<Item> itemEntry) {
		return this.getItem().getRegistryEntry() == itemEntry;
	}

	public boolean isIn(RegistryEntryList<Item> registryEntryList) {
		return registryEntryList.contains(this.getRegistryEntry());
	}

	/**
	 * {@return a stream of all tags the item is in}
	 * 
	 * @see #isIn(TagKey)
	 */
	public Stream<TagKey<Item>> streamTags() {
		return this.getItem().getRegistryEntry().streamTags();
	}

	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		BlockPos blockPos = context.getBlockPos();
		if (playerEntity != null && !playerEntity.getAbilities().allowModifyWorld && !this.canPlaceOn(new CachedBlockPosition(context.getWorld(), blockPos, false))) {
			return ActionResult.PASS;
		} else {
			Item item = this.getItem();
			ActionResult actionResult = item.useOnBlock(context);
			if (playerEntity != null && actionResult instanceof ActionResult.Success success && success.shouldIncrementStat()) {
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
			}

			return actionResult;
		}
	}

	public float getMiningSpeedMultiplier(BlockState state) {
		return this.getItem().getMiningSpeed(this, state);
	}

	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = this.copy();
		boolean bl = this.getMaxUseTime(user) <= 0;
		ActionResult actionResult = this.getItem().use(world, user, hand);
		return (ActionResult)(bl && actionResult instanceof ActionResult.Success success
			? success.withNewHandStack(
				success.getNewHandStack() == null ? this.applyRemainderAndCooldown(user, itemStack) : success.getNewHandStack().applyRemainderAndCooldown(user, itemStack)
			)
			: actionResult);
	}

	public ItemStack finishUsing(World world, LivingEntity user) {
		ItemStack itemStack = this.copy();
		ItemStack itemStack2 = this.getItem().finishUsing(this, world, user);
		return itemStack2.applyRemainderAndCooldown(user, itemStack);
	}

	private ItemStack applyRemainderAndCooldown(LivingEntity user, ItemStack stack) {
		UseRemainderComponent useRemainderComponent = stack.get(DataComponentTypes.USE_REMAINDER);
		UseCooldownComponent useCooldownComponent = stack.get(DataComponentTypes.USE_COOLDOWN);
		int i = stack.getCount();
		ItemStack itemStack = this;
		if (useRemainderComponent != null) {
			itemStack = useRemainderComponent.convert(this, i, user.isInCreativeMode(), user::giveOrDropStack);
		}

		if (useCooldownComponent != null) {
			useCooldownComponent.set(stack, user);
		}

		return itemStack;
	}

	public NbtElement toNbt(RegistryWrapper.WrapperLookup registries, NbtElement prefix) {
		if (this.isEmpty()) {
			throw new IllegalStateException("Cannot encode empty ItemStack");
		} else {
			return CODEC.encode(this, registries.getOps(NbtOps.INSTANCE), prefix).getOrThrow();
		}
	}

	public NbtElement toNbt(RegistryWrapper.WrapperLookup registries) {
		if (this.isEmpty()) {
			throw new IllegalStateException("Cannot encode empty ItemStack");
		} else {
			return CODEC.encodeStart(registries.getOps(NbtOps.INSTANCE), this).getOrThrow();
		}
	}

	public NbtElement toNbtAllowEmpty(RegistryWrapper.WrapperLookup registries) {
		return (NbtElement)(this.isEmpty() ? new NbtCompound() : this.toNbt(registries, new NbtCompound()));
	}

	public int getMaxCount() {
		return this.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, Integer.valueOf(1));
	}

	/**
	 * {@return whether the item stack can have item count above {@code 1}}
	 * 
	 * <p>Stackable items must have {@linkplain Item#getMaxCount the maximum count} that is more
	 * than {@code 1} and cannot be damaged.
	 */
	public boolean isStackable() {
		return this.getMaxCount() > 1 && (!this.isDamageable() || !this.isDamaged());
	}

	/**
	 * {@return whether the item can be damaged (lose durability)}
	 * 
	 * <p>Items with {@linkplain #getMaxDamage 0 max damage} or item stacks with {@link
	 * net.minecraft.component.DataComponentTypes#UNBREAKABLE} component cannot be damaged.
	 * 
	 * @see #getMaxDamage
	 * @see #isDamaged
	 * @see #getDamage
	 */
	public boolean isDamageable() {
		return this.contains(DataComponentTypes.MAX_DAMAGE) && !this.contains(DataComponentTypes.UNBREAKABLE) && this.contains(DataComponentTypes.DAMAGE);
	}

	/**
	 * {@return whether the item stack is {@linkplain #isDamageable damageable} and has damage}
	 * 
	 * @see #isDamageable
	 * @see #getDamage
	 */
	public boolean isDamaged() {
		return this.isDamageable() && this.getDamage() > 0;
	}

	/**
	 * {@return the damage (lost durability) of the item stack}
	 * 
	 * <p>Note that this method does not check if the item is {@linkplain #isDamageable
	 * damageable}, unlike {@link #isDamaged}.
	 * 
	 * @see #isDamageable
	 * @see #isDamaged
	 * @see #setDamage
	 */
	public int getDamage() {
		return MathHelper.clamp(this.getOrDefault(DataComponentTypes.DAMAGE, Integer.valueOf(0)), 0, this.getMaxDamage());
	}

	/**
	 * Sets the stack's damage to {@code damage}.
	 * 
	 * <p>This does not break the item if the damage reaches {@linkplain #getMaxDamage
	 * the maximum}, unlike {@link #damage(int, LivingEntity, EquipmentSlot)}.
	 * 
	 * @see #getDamage
	 * @see #damage(int, ServerWorld, ServerPlayerEntity, Consumer)
	 * @see #damage(int, LivingEntity, EquipmentSlot)
	 */
	public void setDamage(int damage) {
		this.set(DataComponentTypes.DAMAGE, MathHelper.clamp(damage, 0, this.getMaxDamage()));
	}

	public int getMaxDamage() {
		return this.getOrDefault(DataComponentTypes.MAX_DAMAGE, Integer.valueOf(0));
	}

	public boolean shouldBreak() {
		return this.isDamageable() && this.getDamage() >= this.getMaxDamage();
	}

	public boolean willBreakNextUse() {
		return this.isDamageable() && this.getDamage() >= this.getMaxDamage() - 1;
	}

	/**
	 * Damages this item stack. This method should be used when a non-entity, such as a
	 * dispenser, damages the stack. This does not damage {@linkplain #isDamageable non-damageable}
	 * stacks, and the enchantments are applied to {@code amount} before damaging.
	 * 
	 * <p>If {@code player} is not {@code null}, this triggers {@link
	 * net.minecraft.advancement.criterion.Criteria#ITEM_DURABILITY_CHANGED}.
	 * 
	 * <p>When the item "breaks", that is, the stack's damage is equal to or above
	 * {@linkplain #getMaxDamage the maximum damage}, {@code breakCallback} is run.
	 * Note that this method automatically decrements the stack size.
	 */
	public void damage(int amount, ServerWorld world, @Nullable ServerPlayerEntity player, Consumer<Item> breakCallback) {
		int i = this.calculateDamage(amount, world, player);
		if (i != 0) {
			this.onDurabilityChange(this.getDamage() + i, player, breakCallback);
		}
	}

	private int calculateDamage(int baseDamage, ServerWorld world, @Nullable ServerPlayerEntity player) {
		if (!this.isDamageable()) {
			return 0;
		} else if (player != null && player.isInCreativeMode()) {
			return 0;
		} else {
			return baseDamage > 0 ? EnchantmentHelper.getItemDamage(world, this, baseDamage) : baseDamage;
		}
	}

	private void onDurabilityChange(int damage, @Nullable ServerPlayerEntity player, Consumer<Item> breakCallback) {
		if (player != null) {
			Criteria.ITEM_DURABILITY_CHANGED.trigger(player, this, damage);
		}

		this.setDamage(damage);
		if (this.shouldBreak()) {
			Item item = this.getItem();
			this.decrement(1);
			breakCallback.accept(item);
		}
	}

	public void damage(int amount, PlayerEntity player) {
		if (player instanceof ServerPlayerEntity serverPlayerEntity) {
			int i = this.calculateDamage(amount, serverPlayerEntity.getServerWorld(), serverPlayerEntity);
			if (i == 0) {
				return;
			}

			int j = Math.min(this.getDamage() + i, this.getMaxDamage() - 1);
			this.onDurabilityChange(j, serverPlayerEntity, item -> {
			});
		}
	}

	/**
	 * Damages this item stack. This method should be used when an entity, including a player,
	 * damages the stack. This does not damage {@linkplain #isDamageable non-damageable}
	 * stacks, and the enchantments are applied to {@code amount} before damaging. Additionally,
	 * if {@code entity} is a player in creative mode, the stack will not be damaged.
	 * 
	 * <p>If {@code entity} is a player, this triggers {@link
	 * net.minecraft.advancement.criterion.Criteria#ITEM_DURABILITY_CHANGED}.
	 * 
	 * <p>If the stack's damage is equal to or above {@linkplain #getMaxDamage the maximum
	 * damage} (i.e. the item is "broken"), this will {@linkplain
	 * LivingEntity#sendEquipmentBreakStatus send the equipment break status}, decrement the
	 * stack, and increment {@link net.minecraft.stat.Stats#BROKEN} if the stack is held
	 * by a player.
	 * 
	 * @param slot the slot in which the stack is held
	 */
	public void damage(int amount, LivingEntity entity, EquipmentSlot slot) {
		if (entity.getWorld() instanceof ServerWorld serverWorld) {
			this.damage(
				amount,
				serverWorld,
				entity instanceof ServerPlayerEntity serverPlayerEntity ? serverPlayerEntity : null,
				item -> entity.sendEquipmentBreakStatus(item, slot)
			);
		}
	}

	public ItemStack damage(int amount, ItemConvertible itemAfterBreaking, LivingEntity entity, EquipmentSlot slot) {
		this.damage(amount, entity, slot);
		if (this.isEmpty()) {
			ItemStack itemStack = this.copyComponentsToNewStackIgnoreEmpty(itemAfterBreaking, 1);
			if (itemStack.isDamageable()) {
				itemStack.setDamage(0);
			}

			return itemStack;
		} else {
			return this;
		}
	}

	public boolean isItemBarVisible() {
		return this.getItem().isItemBarVisible(this);
	}

	/**
	 * {@return the length of the filled section of the durability bar in pixels (out of 13)}
	 */
	public int getItemBarStep() {
		return this.getItem().getItemBarStep(this);
	}

	/**
	 * {@return the color of the filled section of the durability bar}
	 */
	public int getItemBarColor() {
		return this.getItem().getItemBarColor(this);
	}

	public boolean onStackClicked(Slot slot, ClickType clickType, PlayerEntity player) {
		return this.getItem().onStackClicked(this, slot, clickType, player);
	}

	public boolean onClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		return this.getItem().onClicked(this, stack, slot, clickType, player, cursorStackReference);
	}

	public boolean postHit(LivingEntity target, LivingEntity user) {
		Item item = this.getItem();
		if (item.postHit(this, target, user)) {
			if (user instanceof PlayerEntity playerEntity) {
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
			}

			return true;
		} else {
			return false;
		}
	}

	public void postDamageEntity(LivingEntity target, LivingEntity user) {
		this.getItem().postDamageEntity(this, target, user);
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
	 * @return values consistent with calls to {@link Item#isCorrectForDrops}
	 * @see Item#isCorrectForDrops(ItemStack, BlockState)
	 */
	public boolean isSuitableFor(BlockState state) {
		return this.getItem().isCorrectForDrops(this, state);
	}

	public ActionResult useOnEntity(PlayerEntity user, LivingEntity entity, Hand hand) {
		return this.getItem().useOnEntity(this, user, entity, hand);
	}

	/**
	 * {@return a copy of this item stack, including the item count, components, and
	 * {@linkplain #getBobbingAnimationTime bobbing animation time}}
	 * 
	 * @see #copyWithCount
	 * @see #copyComponentsToNewStack
	 * @see #copyComponentsToNewStackIgnoreEmpty
	 */
	public ItemStack copy() {
		if (this.isEmpty()) {
			return EMPTY;
		} else {
			ItemStack itemStack = new ItemStack(this.getItem(), this.count, this.components.copy());
			itemStack.setBobbingAnimationTime(this.getBobbingAnimationTime());
			return itemStack;
		}
	}

	/**
	 * {@return a copy of this item stack, including the components, and {@linkplain #getBobbingAnimationTime bobbing animation time}},
	 * with the item count set to {@code count}
	 * 
	 * @see #copy
	 * @see #copyComponentsToNewStack
	 * @see #copyComponentsToNewStackIgnoreEmpty
	 * 
	 * @param count the item count of the resultant stack
	 */
	public ItemStack copyWithCount(int count) {
		if (this.isEmpty()) {
			return EMPTY;
		} else {
			ItemStack itemStack = this.copy();
			itemStack.setCount(count);
			return itemStack;
		}
	}

	public ItemStack withItem(ItemConvertible item) {
		return this.copyComponentsToNewStack(item, this.getCount());
	}

	/**
	 * {@return a new item stack with the components copied from this item stack}
	 * 
	 * @see #copy
	 * @see #copyWithCount
	 * @see #copyComponentsToNewStackIgnoreEmpty
	 * 
	 * @param item the item of the resultant stack
	 * @param count the item count of the resultant stack
	 */
	public ItemStack copyComponentsToNewStack(ItemConvertible item, int count) {
		return this.isEmpty() ? EMPTY : this.copyComponentsToNewStackIgnoreEmpty(item, count);
	}

	/**
	 * {@return a new item stack with the components copied from this item stack, even if this stack is empty}
	 * 
	 * @see #copy
	 * @see #copyWithCount
	 * @see #copyComponentsToNewStack
	 * 
	 * @param count the item count of the resultant stack
	 * @param item the item of the resultant stack
	 */
	private ItemStack copyComponentsToNewStackIgnoreEmpty(ItemConvertible item, int count) {
		return new ItemStack(item.asItem().getRegistryEntry(), count, this.components.getChanges());
	}

	/**
	 * {@return whether the given item stacks are equal, including the item count and components}
	 * 
	 * @see #areItemsEqual
	 * @see #areItemsAndComponentsEqual
	 */
	public static boolean areEqual(ItemStack left, ItemStack right) {
		if (left == right) {
			return true;
		} else {
			return left.getCount() != right.getCount() ? false : areItemsAndComponentsEqual(left, right);
		}
	}

	@Deprecated
	public static boolean stacksEqual(List<ItemStack> left, List<ItemStack> right) {
		if (left.size() != right.size()) {
			return false;
		} else {
			for (int i = 0; i < left.size(); i++) {
				if (!areEqual((ItemStack)left.get(i), (ItemStack)right.get(i))) {
					return false;
				}
			}

			return true;
		}
	}

	/**
	 * {@return whether the given item stacks contain the same item, regardless of item count or components}
	 * 
	 * @see #areEqual
	 * @see #areItemsAndComponentsEqual
	 */
	public static boolean areItemsEqual(ItemStack left, ItemStack right) {
		return left.isOf(right.getItem());
	}

	/**
	 * {@return whether the given item stacks' items and components are equal}
	 * 
	 * <p>If this returns {@code true}, the two item stacks can be combined into one,
	 * as long as the resulting item count does not exceed {@linkplain Item#getMaxCount
	 * the maximum item count}
	 * 
	 * @see #areEqual
	 * @see #areItemsEqual
	 */
	public static boolean areItemsAndComponentsEqual(ItemStack stack, ItemStack otherStack) {
		if (!stack.isOf(otherStack.getItem())) {
			return false;
		} else {
			return stack.isEmpty() && otherStack.isEmpty() ? true : Objects.equals(stack.components, otherStack.components);
		}
	}

	public static MapCodec<ItemStack> createOptionalCodec(String fieldName) {
		return CODEC.lenientOptionalFieldOf(fieldName)
			.xmap(optional -> (ItemStack)optional.orElse(EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));
	}

	public static int hashCode(@Nullable ItemStack stack) {
		if (stack != null) {
			int i = 31 + stack.getItem().hashCode();
			return 31 * i + stack.getComponents().hashCode();
		} else {
			return 0;
		}
	}

	@Deprecated
	public static int listHashCode(List<ItemStack> stacks) {
		int i = 0;

		for (ItemStack itemStack : stacks) {
			i = i * 31 + hashCode(itemStack);
		}

		return i;
	}

	public String toString() {
		return this.getCount() + " " + this.getItem();
	}

	public void inventoryTick(World world, Entity entity, int slot, boolean selected) {
		if (this.bobbingAnimationTime > 0) {
			this.bobbingAnimationTime--;
		}

		if (this.getItem() != null) {
			this.getItem().inventoryTick(this, world, entity, slot, selected);
		}
	}

	public void onCraftByPlayer(World world, PlayerEntity player, int amount) {
		player.increaseStat(Stats.CRAFTED.getOrCreateStat(this.getItem()), amount);
		this.getItem().onCraftByPlayer(this, world, player);
	}

	public void onCraftByCrafter(World world) {
		this.getItem().onCraft(this, world);
	}

	public int getMaxUseTime(LivingEntity user) {
		return this.getItem().getMaxUseTime(this, user);
	}

	public UseAction getUseAction() {
		return this.getItem().getUseAction(this);
	}

	public void onStoppedUsing(World world, LivingEntity user, int remainingUseTicks) {
		ItemStack itemStack = this.copy();
		if (this.getItem().onStoppedUsing(this, world, user, remainingUseTicks)) {
			ItemStack itemStack2 = this.applyRemainderAndCooldown(user, itemStack);
			if (itemStack2 != this) {
				user.setStackInHand(user.getActiveHand(), itemStack2);
			}
		}
	}

	public boolean isUsedOnRelease() {
		return this.getItem().isUsedOnRelease(this);
	}

	/**
	 * Sets the component {@code type} for this item stack to {@code value}.
	 * 
	 * <p>If {@code value} is {@code null}, the component is removed and the base component
	 * is unset. To reverse the stack-specific change, instead pass the default value
	 * as {@code value}.
	 * 
	 * @return the previous value set
	 * @see #apply(DataComponentType, Object, UnaryOperator)
	 * @see #apply(DataComponentType, Object, Object, BiFunction)
	 */
	@Nullable
	public <T> T set(ComponentType<? super T> type, @Nullable T value) {
		return this.components.set(type, value);
	}

	/**
	 * Sets the component {@code type} by passing the current value and {@code change}
	 * to {@code applier}, then setting its return value as the value. If the component is
	 * missing, {@code defaultValue} is used as the default.
	 * 
	 * <p>In practice, {@code applier} is a reference to a method of the component
	 * class with one parameter, that returns a new instance of the component with the
	 * specific value changed to {@code change}. For example, adding a lore can be accomplished
	 * by passing reference to {@link net.minecraft.component.type.LoreComponent#with}
	 * and the added lore, like
	 * {@code stack.apply(DataComponentTypes.LORE, LoreComponent.DEFAULT, text, LoreComponent::with)}.
	 * 
	 * @implNote This is the same as setting {@code applier.apply(stack.getOrDefault(type, defaultValue), change)}.
	 * 
	 * @return the previous value set
	 * @see #apply(DataComponentType, Object, UnaryOperator)
	 * @see #set
	 */
	@Nullable
	public <T, U> T apply(ComponentType<T> type, T defaultValue, U change, BiFunction<T, U, T> applier) {
		return this.set(type, (T)applier.apply(this.getOrDefault(type, defaultValue), change));
	}

	/**
	 * Sets the component {@code type} by passing the current value (or {@code defaultValue}
	 * if the component is missing) to {@code applier} and then setting its return value as
	 * the value.
	 * 
	 * @implNote This is the same as setting {@code applier.apply(stack.getOrDefault(type, defaultValue))}.
	 * 
	 * @return the previous value set
	 * @see #set
	 * @see #apply(DataComponentType, Object, Object, BiFunction)
	 */
	@Nullable
	public <T> T apply(ComponentType<T> type, T defaultValue, UnaryOperator<T> applier) {
		T object = this.getOrDefault(type, defaultValue);
		return this.set(type, (T)applier.apply(object));
	}

	/**
	 * Removes the component {@code type}. If it is in the stack's base component,
	 * it is unset and the component becomes missing. To reverse the stack-specific change,
	 * instead pass the default value as {@code value}.
	 * 
	 * @return the previous value set
	 */
	@Nullable
	public <T> T remove(ComponentType<? extends T> type) {
		return this.components.remove(type);
	}

	public void applyChanges(ComponentChanges changes) {
		ComponentChanges componentChanges = this.components.getChanges();
		this.components.applyChanges(changes);
		Optional<Error<ItemStack>> optional = validate(this).error();
		if (optional.isPresent()) {
			LOGGER.error("Failed to apply component patch '{}' to item: '{}'", changes, ((Error)optional.get()).message());
			this.components.setChanges(componentChanges);
		} else {
			this.getItem().postProcessComponents(this);
		}
	}

	public void applyUnvalidatedChanges(ComponentChanges changes) {
		this.components.applyChanges(changes);
		this.getItem().postProcessComponents(this);
	}

	public void applyComponentsFrom(ComponentMap components) {
		this.components.setAll(components);
		this.getItem().postProcessComponents(this);
	}

	/**
	 * {@return the custom name of the stack if it exists, or the item's name}
	 */
	public Text getName() {
		Text text = this.get(DataComponentTypes.CUSTOM_NAME);
		if (text != null) {
			return text;
		} else {
			WrittenBookContentComponent writtenBookContentComponent = this.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
			if (writtenBookContentComponent != null) {
				String string = writtenBookContentComponent.title().raw();
				if (!StringHelper.isBlank(string)) {
					return Text.literal(string);
				}
			}

			return this.getItemName();
		}
	}

	public Text getItemName() {
		return this.getItem().getName(this);
	}

	public Text getFormattedName() {
		MutableText mutableText = Text.empty().append(this.getName()).formatted(this.getRarity().getFormatting());
		if (this.contains(DataComponentTypes.CUSTOM_NAME)) {
			mutableText.formatted(Formatting.ITALIC);
		}

		return mutableText;
	}

	private <T extends TooltipAppender> void appendTooltip(
		ComponentType<T> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type
	) {
		T tooltipAppender = (T)this.get(componentType);
		if (tooltipAppender != null) {
			tooltipAppender.appendTooltip(context, textConsumer, type);
		}
	}

	public List<Text> getTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type) {
		if (!type.isCreative() && this.contains(DataComponentTypes.HIDE_TOOLTIP)) {
			return List.of();
		} else {
			List<Text> list = Lists.<Text>newArrayList();
			list.add(this.getFormattedName());
			if (!type.isAdvanced() && !this.contains(DataComponentTypes.CUSTOM_NAME)) {
				MapIdComponent mapIdComponent = this.get(DataComponentTypes.MAP_ID);
				if (mapIdComponent != null) {
					list.add(FilledMapItem.getIdText(mapIdComponent));
				}
			}

			Consumer<Text> consumer = list::add;
			if (!this.contains(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP)) {
				this.getItem().appendTooltip(this, context, list, type);
			}

			this.appendTooltip(DataComponentTypes.JUKEBOX_PLAYABLE, context, consumer, type);
			this.appendTooltip(DataComponentTypes.TRIM, context, consumer, type);
			this.appendTooltip(DataComponentTypes.STORED_ENCHANTMENTS, context, consumer, type);
			this.appendTooltip(DataComponentTypes.ENCHANTMENTS, context, consumer, type);
			this.appendTooltip(DataComponentTypes.DYED_COLOR, context, consumer, type);
			this.appendTooltip(DataComponentTypes.LORE, context, consumer, type);
			this.appendAttributeModifiersTooltip(consumer, player);
			this.appendTooltip(DataComponentTypes.UNBREAKABLE, context, consumer, type);
			this.appendTooltip(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, context, consumer, type);
			this.appendTooltip(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, context, consumer, type);
			BlockPredicatesChecker blockPredicatesChecker = this.get(DataComponentTypes.CAN_BREAK);
			if (blockPredicatesChecker != null && blockPredicatesChecker.showInTooltip()) {
				consumer.accept(ScreenTexts.EMPTY);
				consumer.accept(BlockPredicatesChecker.CAN_BREAK_TEXT);
				blockPredicatesChecker.addTooltips(consumer);
			}

			BlockPredicatesChecker blockPredicatesChecker2 = this.get(DataComponentTypes.CAN_PLACE_ON);
			if (blockPredicatesChecker2 != null && blockPredicatesChecker2.showInTooltip()) {
				consumer.accept(ScreenTexts.EMPTY);
				consumer.accept(BlockPredicatesChecker.CAN_PLACE_TEXT);
				blockPredicatesChecker2.addTooltips(consumer);
			}

			if (type.isAdvanced()) {
				if (this.isDamaged()) {
					list.add(Text.translatable("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
				}

				list.add(Text.literal(Registries.ITEM.getId(this.getItem()).toString()).formatted(Formatting.DARK_GRAY));
				int i = this.components.size();
				if (i > 0) {
					list.add(Text.translatable("item.components", i).formatted(Formatting.DARK_GRAY));
				}
			}

			if (player != null && !this.getItem().isEnabled(player.getWorld().getEnabledFeatures())) {
				list.add(DISABLED_TEXT);
			}

			return list;
		}
	}

	private void appendAttributeModifiersTooltip(Consumer<Text> textConsumer, @Nullable PlayerEntity player) {
		AttributeModifiersComponent attributeModifiersComponent = this.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
		if (attributeModifiersComponent.showInTooltip()) {
			for (AttributeModifierSlot attributeModifierSlot : AttributeModifierSlot.values()) {
				MutableBoolean mutableBoolean = new MutableBoolean(true);
				this.applyAttributeModifier(attributeModifierSlot, (attribute, modifier) -> {
					if (mutableBoolean.isTrue()) {
						textConsumer.accept(ScreenTexts.EMPTY);
						textConsumer.accept(Text.translatable("item.modifiers." + attributeModifierSlot.asString()).formatted(Formatting.GRAY));
						mutableBoolean.setFalse();
					}

					this.appendAttributeModifierTooltip(textConsumer, player, attribute, modifier);
				});
			}
		}
	}

	private void appendAttributeModifierTooltip(
		Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier
	) {
		double d = modifier.value();
		boolean bl = false;
		if (player != null) {
			if (modifier.idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID)) {
				d += player.getAttributeBaseValue(EntityAttributes.ATTACK_DAMAGE);
				bl = true;
			} else if (modifier.idMatches(Item.BASE_ATTACK_SPEED_MODIFIER_ID)) {
				d += player.getAttributeBaseValue(EntityAttributes.ATTACK_SPEED);
				bl = true;
			}
		}

		double e;
		if (modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
			|| modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
			e = d * 100.0;
		} else if (attribute.matches(EntityAttributes.KNOCKBACK_RESISTANCE)) {
			e = d * 10.0;
		} else {
			e = d;
		}

		if (bl) {
			textConsumer.accept(
				ScreenTexts.space()
					.append(
						Text.translatable(
							"attribute.modifier.equals." + modifier.operation().getId(),
							AttributeModifiersComponent.DECIMAL_FORMAT.format(e),
							Text.translatable(attribute.value().getTranslationKey())
						)
					)
					.formatted(Formatting.DARK_GREEN)
			);
		} else if (d > 0.0) {
			textConsumer.accept(
				Text.translatable(
						"attribute.modifier.plus." + modifier.operation().getId(),
						AttributeModifiersComponent.DECIMAL_FORMAT.format(e),
						Text.translatable(attribute.value().getTranslationKey())
					)
					.formatted(attribute.value().getFormatting(true))
			);
		} else if (d < 0.0) {
			textConsumer.accept(
				Text.translatable(
						"attribute.modifier.take." + modifier.operation().getId(),
						AttributeModifiersComponent.DECIMAL_FORMAT.format(-e),
						Text.translatable(attribute.value().getTranslationKey())
					)
					.formatted(attribute.value().getFormatting(false))
			);
		}
	}

	public boolean hasGlint() {
		Boolean boolean_ = this.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
		return boolean_ != null ? boolean_ : this.getItem().hasGlint(this);
	}

	public Rarity getRarity() {
		Rarity rarity = this.getOrDefault(DataComponentTypes.RARITY, Rarity.COMMON);
		if (!this.hasEnchantments()) {
			return rarity;
		} else {
			return switch (rarity) {
				case COMMON, UNCOMMON -> Rarity.RARE;
				case RARE -> Rarity.EPIC;
				default -> rarity;
			};
		}
	}

	/**
	 * {@return whether this item stack can be enchanted with an enchanting table}
	 * 
	 * <p>This is not used for other methods of enchanting like anvils.
	 */
	public boolean isEnchantable() {
		if (!this.contains(DataComponentTypes.ENCHANTABLE)) {
			return false;
		} else {
			ItemEnchantmentsComponent itemEnchantmentsComponent = this.get(DataComponentTypes.ENCHANTMENTS);
			return itemEnchantmentsComponent != null && itemEnchantmentsComponent.isEmpty();
		}
	}

	/**
	 * Enchants this item with the given enchantment and level.
	 * 
	 * <p>This should not be used with enchanted books, as the book itself is not
	 * enchanted and therefore does not store enchantments under {@link
	 * net.minecraft.component.DataComponentTypes#ENCHANTMENTS}.
	 * 
	 * @see net.minecraft.enchantment.EnchantmentHelper
	 */
	public void addEnchantment(RegistryEntry<Enchantment> enchantment, int level) {
		EnchantmentHelper.apply(this, builder -> builder.add(enchantment, level));
	}

	/**
	 * {@return whether the item stack has any enchantments}
	 * 
	 * <p>This will return {@code false} for enchanted books, as the book itself is not
	 * enchanted and therefore does not store enchantments under {@link
	 * net.minecraft.component.DataComponentTypes#ENCHANTMENTS}.
	 * 
	 * @see net.minecraft.enchantment.EnchantmentHelper#getEnchantments
	 */
	public boolean hasEnchantments() {
		return !this.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).isEmpty();
	}

	public ItemEnchantmentsComponent getEnchantments() {
		return this.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
	}

	/**
	 * {@return whether the item stack is in an item frame}
	 * 
	 * @see #setHolder
	 * @see #getFrame
	 * @see #getHolder
	 */
	public boolean isInFrame() {
		return this.holder instanceof ItemFrameEntity;
	}

	/**
	 * Sets the stack's holder to {@code holder}.
	 * 
	 * <p>This is used by item frames and item entities, and does not need to be called
	 * for other entities.
	 * 
	 * @see #isInFrame
	 * @see #getFrame
	 * @see #getHolder
	 */
	public void setHolder(@Nullable Entity holder) {
		if (!this.isEmpty()) {
			this.holder = holder;
		}
	}

	/**
	 * {@return the item frame that holds the stack, or {@code null} if inapplicable}
	 * 
	 * @see #isInFrame
	 * @see #setHolder
	 * @see #getHolder
	 */
	@Nullable
	public ItemFrameEntity getFrame() {
		return this.holder instanceof ItemFrameEntity ? (ItemFrameEntity)this.getHolder() : null;
	}

	/**
	 * {@return the entity that holds the stack, or {@code null} if inapplicable}
	 * 
	 * @see #isInFrame
	 * @see #getFrame
	 * @see #setHolder
	 */
	@Nullable
	public Entity getHolder() {
		return !this.isEmpty() ? this.holder : null;
	}

	public void applyAttributeModifier(AttributeModifierSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer) {
		AttributeModifiersComponent attributeModifiersComponent = this.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
		attributeModifiersComponent.applyModifiers(slot, attributeModifierConsumer);
		EnchantmentHelper.applyAttributeModifiers(this, slot, attributeModifierConsumer);
	}

	public void applyAttributeModifiers(EquipmentSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer) {
		AttributeModifiersComponent attributeModifiersComponent = this.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
		attributeModifiersComponent.applyModifiers(slot, attributeModifierConsumer);
		EnchantmentHelper.applyAttributeModifiers(this, slot, attributeModifierConsumer);
	}

	/**
	 * {@return a text consisting of the bracketed {@linkplain #getName stack name} that
	 * can be hovered to show the item stack's tooltip}
	 */
	public Text toHoverableText() {
		MutableText mutableText = Text.empty().append(this.getName());
		if (this.contains(DataComponentTypes.CUSTOM_NAME)) {
			mutableText.formatted(Formatting.ITALIC);
		}

		MutableText mutableText2 = Texts.bracketed(mutableText);
		if (!this.isEmpty()) {
			mutableText2.formatted(this.getRarity().getFormatting())
				.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(this))));
		}

		return mutableText2;
	}

	public boolean canPlaceOn(CachedBlockPosition pos) {
		BlockPredicatesChecker blockPredicatesChecker = this.get(DataComponentTypes.CAN_PLACE_ON);
		return blockPredicatesChecker != null && blockPredicatesChecker.check(pos);
	}

	public boolean canBreak(CachedBlockPosition pos) {
		BlockPredicatesChecker blockPredicatesChecker = this.get(DataComponentTypes.CAN_BREAK);
		return blockPredicatesChecker != null && blockPredicatesChecker.check(pos);
	}

	public int getBobbingAnimationTime() {
		return this.bobbingAnimationTime;
	}

	public void setBobbingAnimationTime(int bobbingAnimationTime) {
		this.bobbingAnimationTime = bobbingAnimationTime;
	}

	/**
	 * {@return the count of items in this item stack}
	 */
	public int getCount() {
		return this.isEmpty() ? 0 : this.count;
	}

	/**
	 * Sets the count of items in this item stack.
	 * 
	 * @param count the count of items
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Sets the count of items in this item stack to not exceed {@code maxCount}.
	 */
	public void capCount(int maxCount) {
		if (!this.isEmpty() && this.getCount() > maxCount) {
			this.setCount(maxCount);
		}
	}

	/**
	 * Increments the count of items in this item stack.
	 * 
	 * @param amount the amount to increment
	 */
	public void increment(int amount) {
		this.setCount(this.getCount() + amount);
	}

	/**
	 * Decrements the count of items in this item stack.
	 * 
	 * @param amount the amount to decrement
	 */
	public void decrement(int amount) {
		this.increment(-amount);
	}

	/**
	 * Decrements the count of items in this item stack, unless {@code entity}
	 * is a creative mode player.
	 */
	public void decrementUnlessCreative(int amount, @Nullable LivingEntity entity) {
		if (entity == null || !entity.isInCreativeMode()) {
			this.decrement(amount);
		}
	}

	public ItemStack splitUnlessCreative(int amount, @Nullable LivingEntity entity) {
		ItemStack itemStack = this.copyWithCount(amount);
		this.decrementUnlessCreative(amount, entity);
		return itemStack;
	}

	public void usageTick(World world, LivingEntity user, int remainingUseTicks) {
		ConsumableComponent consumableComponent = this.get(DataComponentTypes.CONSUMABLE);
		if (consumableComponent != null && consumableComponent.shouldSpawnParticlesAndPlaySounds(remainingUseTicks)) {
			consumableComponent.spawnParticlesAndPlaySound(user.getRandom(), user, this, 5);
		}

		this.getItem().usageTick(world, user, this, remainingUseTicks);
	}

	public void onItemEntityDestroyed(ItemEntity entity) {
		this.getItem().onItemEntityDestroyed(entity);
	}

	public SoundEvent getBreakSound() {
		return this.getItem().getBreakSound();
	}

	public boolean takesDamageFrom(DamageSource source) {
		DamageResistantComponent damageResistantComponent = this.get(DataComponentTypes.DAMAGE_RESISTANT);
		return damageResistantComponent == null || !damageResistantComponent.resists(source);
	}

	public boolean canRepairWith(ItemStack ingredient) {
		RepairableComponent repairableComponent = this.get(DataComponentTypes.REPAIRABLE);
		return repairableComponent != null && repairableComponent.matches(ingredient);
	}
}
