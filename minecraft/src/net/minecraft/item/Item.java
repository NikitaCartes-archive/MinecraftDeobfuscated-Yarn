package net.minecraft.item;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.component.type.UseRemainderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.map.MapState;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeyedValue;
import net.minecraft.registry.RegistryPair;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.slf4j.Logger;

/**
 * An item usable by players and other entities.
 * 
 * <p>Like {@link Block}, this class handles logics for a type of item, and does not
 * hold any data. Any data about a particular stack of item in a world, such as item count,
 * is held by an {@link ItemStack} which represents a stack of specific item. Therefore,
 * there is one - and only one - instance of Item for one item (like apples, oak planks, etc),
 * while there can be infinite amounts of {@link ItemStack} instances. This also means that
 * items themselves cannot hold NBT data.
 * 
 * <p>Items with no custom behavior, like diamonds, can call the constructor of Item
 * directly. If a custom behavior is needed, this should be subclassed. Items also have
 * to be registered in the {@link net.minecraft.registry.Registries#ITEM} registry.
 * 
 * <p>Many methods of this class are called on both the logical client and logical server,
 * so take caution when using those methods. The logical side can be checked using
 * {@link World#isClient}. See also <a href="https://fabricmc.net/wiki/tutorial:side">
 * the Fabric Wiki article</a>. It is also important that methods that take {@link LivingEntity}
 * as an argument can be called by non-players (such as foxes eating food), which causes
 * a crash if the code performs unchecked casting.
 * 
 * @see BlockItem
 * @see ItemStack
 * @see net.minecraft.inventory.Inventory
 */
public class Item implements ToggleableFeature, ItemConvertible {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Map<Block, Item> BLOCK_ITEMS = Maps.<Block, Item>newHashMap();
	public static final Identifier BASE_ATTACK_DAMAGE_MODIFIER_ID = Identifier.ofVanilla("base_attack_damage");
	public static final Identifier BASE_ATTACK_SPEED_MODIFIER_ID = Identifier.ofVanilla("base_attack_speed");
	public static final int DEFAULT_MAX_COUNT = 64;
	public static final int MAX_MAX_COUNT = 99;
	public static final int ITEM_BAR_STEPS = 13;
	private final RegistryEntry.Reference<Item> registryEntry = Registries.ITEM.createEntry(this);
	private final ComponentMap components;
	@Nullable
	private final Item recipeRemainder;
	protected final String translationKey;
	private final FeatureSet requiredFeatures;

	/**
	 * {@return the raw ID of {@code item}, or 0 if passed {@code null}}
	 */
	public static int getRawId(Item item) {
		return item == null ? 0 : Registries.ITEM.getRawId(item);
	}

	/**
	 * {@return the item from its raw ID}
	 */
	public static Item byRawId(int id) {
		return Registries.ITEM.get(id);
	}

	/**
	 * @deprecated Please use {@link Block#asItem}
	 */
	@Deprecated
	public static Item fromBlock(Block block) {
		return (Item)BLOCK_ITEMS.getOrDefault(block, Items.AIR);
	}

	public Item(Item.Settings settings) {
		this.translationKey = settings.getTranslationKey();
		this.components = settings.getValidatedComponents(Text.translatable(this.translationKey), settings.getModelId());
		this.recipeRemainder = settings.recipeRemainder;
		this.requiredFeatures = settings.requiredFeatures;
		if (SharedConstants.isDevelopment) {
			String string = this.getClass().getSimpleName();
			if (!string.endsWith("Item")) {
				LOGGER.error("Item classes should end with Item and {} doesn't.", string);
			}
		}
	}

	@Deprecated
	public RegistryEntry.Reference<Item> getRegistryEntry() {
		return this.registryEntry;
	}

	public ComponentMap getComponents() {
		return this.components;
	}

	/**
	 * {@return the maximum stack count of any ItemStack with this item} Can be configured through {@link Item.Settings#maxCount(int) settings.maxCount()}.
	 */
	public int getMaxCount() {
		return this.components.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1);
	}

	/**
	 * Called on both the server and the client every tick while an entity uses
	 * the item. Currently used by {@link CrossbowItem} to charge the crossbow.
	 * If this is overridden, {@link #getMaxUseTime} should also be overridden to
	 * return a positive value.
	 * 
	 * @see #finishUsing
	 * @see #use
	 * 
	 * @param remainingUseTicks how long it's left until the entity finishes using the item, in ticks
	 */
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
	}

	/**
	 * Called on the server when an {@link ItemEntity} holding this item gets destroyed.
	 * This can happen due to fire, lava, cactus, explosion, etc. Items that can hold
	 * other items should override this to drop its contents.
	 * 
	 * @see ItemUsage#spawnItemContents
	 */
	public void onItemEntityDestroyed(ItemEntity entity) {
	}

	/**
	 * Processes the components applied to an item stack of this item.
	 * 
	 * <p>This is only used in vanilla to process player head component data.
	 */
	public void postProcessComponents(ItemStack stack) {
	}

	/**
	 * {@return whether a player can break a block while holding the item}
	 * 
	 * <p>This is to check whether the player can start breaking the block in the
	 * first place; this does not check if the item is a correct tool to mine the block.
	 * Melee weapons should override this to return {@code false}, unless it is also
	 * intended to be used as a tool.
	 */
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return true;
	}

	@Override
	public Item asItem() {
		return this;
	}

	/**
	 * Called when an item is used on a block.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution when using this method.
	 * The logical side can be checked using {@link net.minecraft.world.World#isClient() context.getWorld().isClient()}.
	 * 
	 * @return an action result that specifies if using the item on a block was successful.
	 * 
	 * @param context the usage context
	 */
	public ActionResult useOnBlock(ItemUsageContext context) {
		return ActionResult.PASS;
	}

	public float getMiningSpeed(ItemStack stack, BlockState state) {
		ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);
		return toolComponent != null ? toolComponent.getSpeed(state) : 1.0F;
	}

	/**
	 * Called when the player uses (or starts using) the item.
	 * The use action, by default, is bound to the right mouse button.
	 * This method checks the player's hunger when the item is a food, and will
	 * {@linkplain ActionResult#PASS pass} in all other cases by default.
	 * 
	 * <p>If the item {@linkplain #getMaxUseTime can be used for multiple ticks}, then
	 * this will only be called when the player starts using it. After that,
	 * {@link #usageTick} is called every tick until the player {@linkplain #finishUsing
	 * finishes using the item}.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution when overriding this method.
	 * The logical side can be checked using {@link net.minecraft.world.World#isClient() world.isClient()}.
	 * 
	 * @return a typed action result that specifies whether using the item was successful.
	 * The action result contains the new item stack that the player's hand will be set to.
	 * 
	 * @param world the world the item was used in
	 * @param user the player who used the item
	 * @param hand the hand used
	 */
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		ConsumableComponent consumableComponent = itemStack.get(DataComponentTypes.CONSUMABLE);
		if (consumableComponent != null) {
			return consumableComponent.consume(user, itemStack, hand);
		} else {
			EquippableComponent equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);
			return (ActionResult)(equippableComponent != null ? equippableComponent.equip(itemStack, user) : ActionResult.PASS);
		}
	}

	/**
	 * Called when an entity finishes using the item, such as eating food or drinking a potion.
	 * This method handles eating food by default.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 * 
	 * <p>{@code user} might not be a player in some cases. For example, this occurs when a fox
	 * eats food or when a wandering trader drinks milk.
	 * 
	 * @return the new item stack after using the item
	 */
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ConsumableComponent consumableComponent = stack.get(DataComponentTypes.CONSUMABLE);
		return consumableComponent != null ? consumableComponent.finishConsumption(world, user, stack) : stack;
	}

	/**
	 * {@return whether to show the item bar for {@code stack}}
	 * 
	 * <p>Item bar is usually used to display durability of the stack.
	 * 
	 * <p>When overriding this, {@link #getItemBarStep} and {@link #getItemBarColor} should
	 * also be overridden.
	 */
	public boolean isItemBarVisible(ItemStack stack) {
		return stack.isDamaged();
	}

	/**
	 * {@return the step, or the length of the colored area of the item bar, for
	 * {@code stack}}
	 * 
	 * <p>This is between {@code 0.0f} and {code 13.0f}. By default, this is
	 * {@code durability * 13.0f / maxDurability}.
	 * 
	 * <p>When overriding this, {@link #isItemBarVisible} and {@link #getItemBarColor} should
	 * also be overridden.
	 */
	public int getItemBarStep(ItemStack stack) {
		return MathHelper.clamp(Math.round(13.0F - (float)stack.getDamage() * 13.0F / (float)stack.getMaxDamage()), 0, 13);
	}

	/**
	 * {@return the RGB color of the item bar, usually used for durability display}
	 * 
	 * <p>When overriding this, {@link #isItemBarVisible} and {@link #getItemBarStep} should
	 * also be overridden.
	 */
	public int getItemBarColor(ItemStack stack) {
		int i = stack.getMaxDamage();
		float f = Math.max(0.0F, ((float)i - (float)stack.getDamage()) / (float)i);
		return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}

	/**
	 * Called when the item at the cursor is clicked at {@code slot}.
	 * 
	 * <p>While this method is usually called on the logical server, it can also be called on
	 * the logical client, so take caution when overriding this method. The logical side can be
	 * checked using {@link World#isClient}.
	 * 
	 * <p>For example, this is called on {@link BundleItem} when the cursor holds
	 * a bundle and the player clicks on the slot.
	 * 
	 * @return whether the action was successful
	 * 
	 * @param slot the clicked slot
	 * @param stack the stack the cursor holds
	 */
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		return false;
	}

	/**
	 * Called when the item at {@code slot} gets clicked by the cursor
	 * holding {@code otherStack}.
	 * 
	 * <p>While this method is usually called on the logical server, it can also be called on
	 * the logical client, so take caution when overriding this method. The logical side can be
	 * checked using {@link World#isClient}.
	 * 
	 * <p>For example, this is called on {@link BundleItem} when the cursor holds
	 * an item and the player clicks on the slot that has a bundle.
	 * 
	 * @return whether the action was successful
	 * 
	 * @param slot the clicked slot
	 * @param stack the slot's stack
	 * @param otherStack the stack the cursor holds
	 */
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		return false;
	}

	public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
		return 0.0F;
	}

	/**
	 * Called on the server when the item is used to hit an entity.
	 * 
	 * <p>Tools and melee weapons should override this to damage the stack.
	 * 
	 * @return whether the item's use stat should be incremented
	 * @see ItemStack#damage(int, LivingEntity, EquipmentSlot)
	 */
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return false;
	}

	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
	}

	/**
	 * Called on the server when the item is used to break a block.
	 * 
	 * <p>Tools and melee weapons should override this to damage the stack, after
	 * checking if the block's hardness is larger than {@code 0.0f}.
	 * 
	 * @return whether the item's use stat should be incremented
	 * @see net.minecraft.block.AbstractBlock.AbstractBlockState#getHardness
	 * @see ItemStack#damage(int, LivingEntity, EquipmentSlot)
	 */
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);
		if (toolComponent == null) {
			return false;
		} else {
			if (!world.isClient && state.getHardness(world, pos) != 0.0F && toolComponent.damagePerBlock() > 0) {
				stack.damage(toolComponent.damagePerBlock(), miner, EquipmentSlot.MAINHAND);
			}

			return true;
		}
	}

	public boolean isCorrectForDrops(ItemStack stack, BlockState state) {
		ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);
		return toolComponent != null && toolComponent.isCorrectForDrops(state);
	}

	/**
	 * Called on both the client and the server when a player uses the item on an entity.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 * 
	 * <p>This should be used if the item can be used on multiple types of entities,
	 * such as name tags or saddles.
	 * 
	 * @return the action result
	 */
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		return ActionResult.PASS;
	}

	public String toString() {
		return Registries.ITEM.getEntry(this).getIdAsString();
	}

	/**
	 * Gets the remainder item that should be left behind when this item is used as a crafting ingredient.
	 */
	@Nullable
	public final Item getRecipeRemainder() {
		return this.recipeRemainder;
	}

	/**
	 * Checks if this item has a remainder item that is left behind when used as a crafting ingredient.
	 */
	public boolean hasRecipeRemainder() {
		return this.recipeRemainder != null;
	}

	/**
	 * Called on both the client and the server every tick if the item is in the player's inventory.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 * 
	 * @param selected whether the item is in the selected hotbar slot
	 * @param entity the entity holding the item; usually a player
	 */
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
	}

	/**
	 * Called when a player acquires the item by crafting, smelting, smithing, etc.
	 * 
	 * <p>Not called when the item is automatically created, such as via a crafter.
	 */
	public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
		this.onCraft(stack, world);
	}

	/**
	 * Called when the item is made by crafting, smelting, smithing, etc.
	 */
	public void onCraft(ItemStack stack, World world) {
	}

	/**
	 * {@return whether the item needs to sync additional data to clients}
	 * 
	 * <p>Items should ideally store all necessary information in the stack's components.
	 * However, this is not always possible for things like maps. In those cases,
	 * items can send a packet to the player holding it that syncs additional data.
	 * Such items must subclass {@link NetworkSyncedItem}.
	 * 
	 * @see NetworkSyncedItem
	 */
	public boolean isNetworkSynced() {
		return false;
	}

	/**
	 * {@return the use action the item should perform}
	 */
	public UseAction getUseAction(ItemStack stack) {
		ConsumableComponent consumableComponent = stack.get(DataComponentTypes.CONSUMABLE);
		return consumableComponent != null ? consumableComponent.useAction() : UseAction.NONE;
	}

	/**
	 * {@return the maximum use (right-click) time of this item, in ticks}
	 * Once a player has used an item for said number of ticks, they stop using it, and {@link Item#finishUsing} is called.
	 */
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		ConsumableComponent consumableComponent = stack.get(DataComponentTypes.CONSUMABLE);
		return consumableComponent != null ? consumableComponent.getConsumeTicks() : 0;
	}

	/**
	 * Called on both the client and the server when an entity stops using an item
	 * before reaching the {@linkplain #getMaxUseTime maximum use time}. If the time was
	 * reached, {@link #finishUsing} is called instead.
	 * 
	 * <p>This method is called on both the logical client and logical server, so take caution
	 * when overriding this method. The logical side can be checked using {@link
	 * World#isClient}.
	 * 
	 * <p>{@code user} might not be a player in some cases. For example, this occurs when
	 * an entity uses a crossbow.
	 */
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		return false;
	}

	/**
	 * Called by the client to append tooltips to an item. Subclasses can override
	 * this and add custom tooltips to {@code tooltip} list.
	 * 
	 * @param tooltip the list of tooltips to show
	 */
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
	}

	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return Optional.empty();
	}

	/**
	 * Gets the translation key of this item.
	 */
	public final String getTranslationKey() {
		return this.translationKey;
	}

	public final Text getName() {
		return this.components.getOrDefault(DataComponentTypes.ITEM_NAME, ScreenTexts.EMPTY);
	}

	public Text getName(ItemStack stack) {
		return stack.getComponents().getOrDefault(DataComponentTypes.ITEM_NAME, ScreenTexts.EMPTY);
	}

	/**
	 * Checks if the glint effect should be applied when the item is rendered.
	 * 
	 * <p>By default, returns true if the item has enchantments.
	 */
	public boolean hasGlint(ItemStack stack) {
		return stack.hasEnchantments();
	}

	protected static BlockHitResult raycast(World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling) {
		Vec3d vec3d = player.getEyePos();
		Vec3d vec3d2 = vec3d.add(player.getRotationVector(player.getPitch(), player.getYaw()).multiply(player.getBlockInteractionRange()));
		return world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, fluidHandling, player));
	}

	public boolean isUsedOnRelease(ItemStack stack) {
		return false;
	}

	/**
	 * {@return the default stack for this item}
	 * 
	 * <p>Items that expect certain components in the item stack should override
	 * this method to return the stack with the component data.
	 */
	public ItemStack getDefaultStack() {
		return new ItemStack(this);
	}

	public SoundEvent getBreakSound() {
		return SoundEvents.ENTITY_ITEM_BREAK;
	}

	/**
	 * @return true if the item can be placed inside of shulker boxes or bundles.
	 */
	public boolean canBeNested() {
		return true;
	}

	@Override
	public FeatureSet getRequiredFeatures() {
		return this.requiredFeatures;
	}

	/**
	 * Item settings configure behaviors common to all items, such as the stack's max
	 * count. An instance of this must be passed to the constructor
	 * of {@link Item} (or most of its subclasses).
	 */
	public static class Settings {
		private static final RegistryKeyedValue<Item, String> BLOCK_PREFIXED_TRANSLATION_KEY = key -> Util.createTranslationKey("block", key.getValue());
		private static final RegistryKeyedValue<Item, String> ITEM_PREFIXED_TRANSLATION_KEY = key -> Util.createTranslationKey("item", key.getValue());
		private final ComponentMap.Builder components = ComponentMap.builder().addAll(DataComponentTypes.DEFAULT_ITEM_COMPONENTS);
		@Nullable
		Item recipeRemainder;
		FeatureSet requiredFeatures = FeatureFlags.VANILLA_FEATURES;
		@Nullable
		private RegistryKey<Item> registryKey;
		private RegistryKeyedValue<Item, String> translationKey = ITEM_PREFIXED_TRANSLATION_KEY;
		private RegistryKeyedValue<Item, Identifier> modelId = RegistryKey::getValue;

		/**
		 * When set, any item configured with this Settings instance will be edible based on the provided {@link FoodComponent}.
		 * 
		 * @return this instance
		 * 
		 * @param foodComponent configured food properties for any item using this Settings instance
		 */
		public Item.Settings food(FoodComponent foodComponent) {
			return this.food(foodComponent, ConsumableComponents.FOOD);
		}

		public Item.Settings food(FoodComponent foodComponent, ConsumableComponent consumableComponent) {
			return this.component(DataComponentTypes.FOOD, foodComponent).component(DataComponentTypes.CONSUMABLE, consumableComponent);
		}

		public Item.Settings useRemainder(Item convertInto) {
			return this.component(DataComponentTypes.USE_REMAINDER, new UseRemainderComponent(new ItemStack(convertInto)));
		}

		public Item.Settings useCooldown(float seconds) {
			return this.component(DataComponentTypes.USE_COOLDOWN, new UseCooldownComponent(seconds));
		}

		/**
		 * Sets the maximum stack count of any ItemStack with an Item using this Settings instance.
		 * 
		 * <p>Note that a count over 64 leads to unreliable behavior in GUIs.
		 * Damageable items can not have a maximum stack count (they default to 1).
		 * An Item.Settings' max count defaults to 64.
		 * 
		 * @throws RuntimeException if this Settings instance also has a max damage value set
		 * @return this instance
		 * 
		 * @param maxCount maximum stack count of any ItemStack with an item using this Settings instance
		 */
		public Item.Settings maxCount(int maxCount) {
			return this.component(DataComponentTypes.MAX_STACK_SIZE, maxCount);
		}

		/**
		 * Sets the maximum durability of any item configured with this Settings instance.
		 * 
		 * <p>Note that max stack count is set to 1 when this method is called.
		 * 
		 * @return this instance
		 * 
		 * @param maxDamage maximum durability of an ItemStack using an item with this Item.Settings instance
		 */
		public Item.Settings maxDamage(int maxDamage) {
			this.component(DataComponentTypes.MAX_DAMAGE, maxDamage);
			this.component(DataComponentTypes.MAX_STACK_SIZE, 1);
			this.component(DataComponentTypes.DAMAGE, 0);
			return this;
		}

		/**
		 * Sets the recipe remainder for any item configured with this Settings instance.
		 * When an item with a recipe remainder is used in a crafting recipe, the remainder is left in the table or returned to the player.
		 * 
		 * @return this instance
		 */
		public Item.Settings recipeRemainder(Item recipeRemainder) {
			this.recipeRemainder = recipeRemainder;
			return this;
		}

		/**
		 * Sets the {@link Rarity} of any item configured with this Settings instance, which changes the color of its name.
		 * 
		 * <p>An item's rarity defaults to {@link Rarity#COMMON}.
		 * 
		 * @return this instance
		 * 
		 * @param rarity rarity to apply to items using this Settings instance
		 */
		public Item.Settings rarity(Rarity rarity) {
			return this.component(DataComponentTypes.RARITY, rarity);
		}

		/**
		 * If called, any item with this Settings instance is immune to fire and lava damage.
		 * 
		 * @return this instance
		 */
		public Item.Settings fireproof() {
			return this.component(DataComponentTypes.FIRE_RESISTANT, Unit.INSTANCE);
		}

		public Item.Settings jukeboxPlayable(RegistryKey<JukeboxSong> songKey) {
			return this.component(DataComponentTypes.JUKEBOX_PLAYABLE, new JukeboxPlayableComponent(new RegistryPair<>(songKey), true));
		}

		public Item.Settings enchantable(int enchantability) {
			return this.component(DataComponentTypes.ENCHANTABLE, new EnchantableComponent(enchantability));
		}

		public Item.Settings repairable(Item repairIngredient) {
			return this.component(DataComponentTypes.REPAIRABLE, new RepairableComponent(RegistryEntryList.of(repairIngredient.getRegistryEntry())));
		}

		public Item.Settings repairable(TagKey<Item> repairIngredientsTag) {
			RegistryEntryLookup<Item> registryEntryLookup = Registries.createEntryLookup(Registries.ITEM);
			return this.component(DataComponentTypes.REPAIRABLE, new RepairableComponent(registryEntryLookup.getOrThrow(repairIngredientsTag)));
		}

		public Item.Settings equippable(EquipmentSlot slot, RegistryEntry<SoundEvent> equipSound, Identifier model) {
			return this.component(DataComponentTypes.EQUIPPABLE, new EquippableComponent(slot, equipSound, Optional.of(model), Optional.empty(), true));
		}

		public Item.Settings equippable(EquipmentSlot slot) {
			return this.component(
				DataComponentTypes.EQUIPPABLE, new EquippableComponent(slot, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, Optional.empty(), Optional.empty(), true)
			);
		}

		public Item.Settings requires(FeatureFlag... features) {
			this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(features);
			return this;
		}

		public Item.Settings registryKey(RegistryKey<Item> registryKey) {
			this.registryKey = registryKey;
			return this;
		}

		public Item.Settings translationKey(String translationKey) {
			this.translationKey = RegistryKeyedValue.fixed(translationKey);
			return this;
		}

		public Item.Settings useBlockPrefixedTranslationKey() {
			this.translationKey = BLOCK_PREFIXED_TRANSLATION_KEY;
			return this;
		}

		public Item.Settings useItemPrefixedTranslationKey() {
			this.translationKey = ITEM_PREFIXED_TRANSLATION_KEY;
			return this;
		}

		protected String getTranslationKey() {
			return this.translationKey.get((RegistryKey<Item>)Objects.requireNonNull(this.registryKey, "Item id not set"));
		}

		public Item.Settings modelId(Identifier modelId) {
			this.modelId = RegistryKeyedValue.fixed(modelId);
			return this;
		}

		public Identifier getModelId() {
			return this.modelId.get((RegistryKey<Item>)Objects.requireNonNull(this.registryKey, "Item id not set"));
		}

		public <T> Item.Settings component(ComponentType<T> type, T value) {
			this.components.add(type, value);
			return this;
		}

		public Item.Settings attributeModifiers(AttributeModifiersComponent attributeModifiersComponent) {
			return this.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiersComponent);
		}

		ComponentMap getValidatedComponents(Text name, Identifier modelId) {
			ComponentMap componentMap = this.components.add(DataComponentTypes.ITEM_NAME, name).add(DataComponentTypes.ITEM_MODEL, modelId).build();
			if (componentMap.contains(DataComponentTypes.DAMAGE) && componentMap.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1) > 1) {
				throw new IllegalStateException("Item cannot have both durability and be stackable");
			} else {
				return componentMap;
			}
		}
	}

	public interface TooltipContext {
		Item.TooltipContext DEFAULT = new Item.TooltipContext() {
			@Nullable
			@Override
			public RegistryWrapper.WrapperLookup getRegistryLookup() {
				return null;
			}

			@Override
			public float getUpdateTickRate() {
				return 20.0F;
			}

			@Nullable
			@Override
			public MapState getMapState(MapIdComponent mapIdComponent) {
				return null;
			}
		};

		@Nullable
		RegistryWrapper.WrapperLookup getRegistryLookup();

		float getUpdateTickRate();

		@Nullable
		MapState getMapState(MapIdComponent mapIdComponent);

		static Item.TooltipContext create(@Nullable World world) {
			return world == null ? DEFAULT : new Item.TooltipContext() {
				@Override
				public RegistryWrapper.WrapperLookup getRegistryLookup() {
					return world.getRegistryManager();
				}

				@Override
				public float getUpdateTickRate() {
					return world.getTickManager().getTickRate();
				}

				@Override
				public MapState getMapState(MapIdComponent mapIdComponent) {
					return world.getMapState(mapIdComponent);
				}
			};
		}

		static Item.TooltipContext create(RegistryWrapper.WrapperLookup registries) {
			return new Item.TooltipContext() {
				@Override
				public RegistryWrapper.WrapperLookup getRegistryLookup() {
					return registries;
				}

				@Override
				public float getUpdateTickRate() {
					return 20.0F;
				}

				@Nullable
				@Override
				public MapState getMapState(MapIdComponent mapIdComponent) {
					return null;
				}
			};
		}
	}
}
