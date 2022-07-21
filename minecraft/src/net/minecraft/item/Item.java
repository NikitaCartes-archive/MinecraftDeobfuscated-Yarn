package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
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
 * items themselves cannot hold NBT data. To append item stacks with NBT data to the
 * creative inventory, override {@link #appendStacks}.
 * 
 * <p>Items with no custom behavior, like diamonds, can call the constructor of Item
 * directly. If a custom behavior is needed, this should be subclassed. Items also have
 * to be registered in the {@link net.minecraft.util.registry.Registry#ITEM} registry.
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
public class Item implements ItemConvertible {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Map<Block, Item> BLOCK_ITEMS = Maps.<Block, Item>newHashMap();
	protected static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	public static final int DEFAULT_MAX_COUNT = 64;
	public static final int field_30888 = 32;
	public static final int field_30889 = 13;
	private final RegistryEntry.Reference<Item> registryEntry = Registry.ITEM.createEntry(this);
	@Nullable
	protected final ItemGroup group;
	private final Rarity rarity;
	private final int maxCount;
	private final int maxDamage;
	private final boolean fireproof;
	@Nullable
	private final Item recipeRemainder;
	@Nullable
	private String translationKey;
	@Nullable
	private final FoodComponent foodComponent;

	/**
	 * {@return the raw ID of {@code item}, or 0 if passed {@code null}}
	 */
	public static int getRawId(Item item) {
		return item == null ? 0 : Registry.ITEM.getRawId(item);
	}

	/**
	 * {@return the item from its raw ID}
	 */
	public static Item byRawId(int id) {
		return Registry.ITEM.get(id);
	}

	/**
	 * @deprecated Please use {@link Block#asItem}
	 */
	@Deprecated
	public static Item fromBlock(Block block) {
		return (Item)BLOCK_ITEMS.getOrDefault(block, Items.AIR);
	}

	public Item(Item.Settings settings) {
		this.group = settings.group;
		this.rarity = settings.rarity;
		this.recipeRemainder = settings.recipeRemainder;
		this.maxDamage = settings.maxDamage;
		this.maxCount = settings.maxCount;
		this.foodComponent = settings.foodComponent;
		this.fireproof = settings.fireproof;
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
	 * Processes the NBT applied to an item stack of this item.
	 * 
	 * <p>This is only used in vanilla to process player head NBT data.
	 */
	public void postProcessNbt(NbtCompound nbt) {
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

	/**
	 * {@return the multiplier applied to the mining speed of {@code stack} when mining
	 * {@code state}}
	 * 
	 * <p>The default value is {@code 1.0f}. Returning larger integer will cause the block
	 * to be mined faster. Enchantments, status effects, and other effects that affect
	 * mining speed are instead handled in {@link PlayerEntity#getBlockBreakingSpeed}.
	 * 
	 * @see MiningToolItem
	 */
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		return 1.0F;
	}

	/**
	 * Called when the player uses (or starts using) the item.
	 * The use action, by default, is bound to the right mouse button.
	 * This method checks the player's hunger when the item is a food, and will
	 * {@linkplain TypedActionResult#pass pass} in all other cases by default.
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
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (this.isFood()) {
			ItemStack itemStack = user.getStackInHand(hand);
			if (user.canConsume(this.getFoodComponent().isAlwaysEdible())) {
				user.setCurrentHand(hand);
				return TypedActionResult.consume(itemStack);
			} else {
				return TypedActionResult.fail(itemStack);
			}
		} else {
			return TypedActionResult.pass(user.getStackInHand(hand));
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
		return this.isFood() ? user.eatFood(world, stack) : stack;
	}

	/**
	 * {@return the maximum stack count of any ItemStack with this item} Can be configured through {@link Item.Settings#maxCount(int) settings.maxCount()}.
	 */
	public final int getMaxCount() {
		return this.maxCount;
	}

	/**
	 * {@return the maximum durability of this item} Can be configured through {@link Item.Settings#maxDamage(int) settings.maxDamage()}.
	 */
	public final int getMaxDamage() {
		return this.maxDamage;
	}

	/**
	 * {@return whether this item can lose durability}
	 */
	public boolean isDamageable() {
		return this.maxDamage > 0;
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
		return Math.round(13.0F - (float)stack.getDamage() * 13.0F / (float)this.maxDamage);
	}

	/**
	 * {@return the RGB color of the item bar, usually used for durability display}
	 * 
	 * <p>When overriding this, {@link #isItemBarVisible} and {@link #getItemBarStep} should
	 * also be overridden.
	 */
	public int getItemBarColor(ItemStack stack) {
		float f = Math.max(0.0F, ((float)this.maxDamage - (float)stack.getDamage()) / (float)this.maxDamage);
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
	 * @param stack the stack the cursor holds
	 * @param slot the clicked slot
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
	 * @param stack the slot's stack
	 * @param otherStack the stack the cursor holds
	 * @param slot the clicked slot
	 */
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		return false;
	}

	/**
	 * Called on the server when the item is used to hit an entity.
	 * 
	 * <p>Tools and melee weapons should override this to damage the stack.
	 * 
	 * @return whether the item's use stat should be incremented
	 * @see ItemStack#damage(int, LivingEntity, java.util.function.Consumer)
	 */
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return false;
	}

	/**
	 * Called on the server when the item is used to break a block.
	 * 
	 * <p>Tools and melee weapons should override this to damage the stack, after
	 * checking if the block's hardness is larger than {@code 0.0f}.
	 * 
	 * @return whether the item's use stat should be incremented
	 * @see net.minecraft.block.AbstractBlock.AbstractBlockState#getHardness
	 * @see ItemStack#damage(int, LivingEntity, java.util.function.Consumer)
	 */
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		return false;
	}

	/**
	 * Determines whether this item can be used as a suitable tool for mining the specified block.
	 * Depending on block implementation, when combined together, the correct item and block may achieve a better mining speed and yield
	 * drops that would not be obtained when mining otherwise.
	 * <p>
	 * Note that this is not the <b>only</b> way to achieve "effectiveness" when mining.
	 * Other items, such as shears on string, may use their own logic
	 * and calls to this method might not return a value consistent to this rule for those items.
	 */
	public boolean isSuitableFor(BlockState state) {
		return false;
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

	public Text getName() {
		return Text.translatable(this.getTranslationKey());
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

	/**
	 * Gets the translation key of this item.
	 */
	public String getTranslationKey() {
		return this.getOrCreateTranslationKey();
	}

	/**
	 * Gets the translation key of this item using the provided item stack for context.
	 */
	public String getTranslationKey(ItemStack stack) {
		return this.getTranslationKey();
	}

	/**
	 * Checks if an item should have its NBT data stored in {@link ItemStack#nbt} sent to the client.
	 * 
	 * <p>If an item is damageable, this method is ignored and data is always synced to client.
	 */
	public boolean isNbtSynced() {
		return true;
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
	 * @param entity the entity holding the item; usually a player
	 * @param selected whether the item is in the selected hotbar slot
	 */
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
	}

	/**
	 * Called when a player acquires the item by crafting, smelting, smithing, etc.
	 */
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
	}

	/**
	 * {@return whether the item needs to sync additional data to clients}
	 * 
	 * <p>Items should ideally store all necessary information on the stack's NBT.
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
		return stack.getItem().isFood() ? UseAction.EAT : UseAction.NONE;
	}

	/**
	 * {@return the maximum use (right-click) time of this item, in ticks}
	 * Once a player has used an item for said number of ticks, they stop using it, and {@link Item#finishUsing} is called.
	 */
	public int getMaxUseTime(ItemStack stack) {
		if (stack.getItem().isFood()) {
			return this.getFoodComponent().isSnack() ? 16 : 32;
		} else {
			return 0;
		}
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
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
	}

	/**
	 * Called by the client to append tooltips to an item. Subclasses can override
	 * this and add custom tooltips to {@code tooltip} list.
	 * 
	 * @param tooltip the list of tooltips to show
	 */
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
	}

	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return Optional.empty();
	}

	public Text getName(ItemStack stack) {
		return Text.translatable(this.getTranslationKey(stack));
	}

	/**
	 * Checks if the glint effect should be applied when the item is rendered.
	 * 
	 * <p>By default, returns true if the item has enchantments.
	 */
	public boolean hasGlint(ItemStack stack) {
		return stack.hasEnchantments();
	}

	/**
	 * {@return this item's rarity, which changes the color of its name}
	 * 
	 * <p>By default, if an item has an enchantment, its rarity is modified:
	 * <ul>
	 * 	<li>Common and Uncommon -> Rare
	 * 	<li>Rare -> Epic
	 * </ul>
	 */
	public Rarity getRarity(ItemStack stack) {
		if (!stack.hasEnchantments()) {
			return this.rarity;
		} else {
			switch (this.rarity) {
				case COMMON:
				case UNCOMMON:
					return Rarity.RARE;
				case RARE:
					return Rarity.EPIC;
				case EPIC:
				default:
					return this.rarity;
			}
		}
	}

	/**
	 * {@return whether the given {@link ItemStack} is enchantable}
	 * 
	 * <p>By default, ItemStacks are enchantable if their max stack count is 1 and they can be damaged.
	 */
	public boolean isEnchantable(ItemStack stack) {
		return this.getMaxCount() == 1 && this.isDamageable();
	}

	protected static BlockHitResult raycast(World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling) {
		float f = player.getPitch();
		float g = player.getYaw();
		Vec3d vec3d = player.getEyePos();
		float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float i = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
		float k = MathHelper.sin(-f * (float) (Math.PI / 180.0));
		float l = i * j;
		float n = h * j;
		double d = 5.0;
		Vec3d vec3d2 = vec3d.add((double)l * 5.0, (double)k * 5.0, (double)n * 5.0);
		return world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, fluidHandling, player));
	}

	/**
	 * Gets the enchantability of an item.
	 * This specifies the ability of an item to receive enchantments when enchanted using an enchanting table.
	 * As the value increases, the amount and level of enchantments applied increase.
	 * 
	 * <p>If the value of this method is 0, the item cannot be enchanted using an enchanting table.
	 */
	public int getEnchantability() {
		return 0;
	}

	/**
	 * Appends the stacks of this item shown in the item group to the list.
	 * 
	 * <p>This can be overridden to append item stacks with custom NBT data to the
	 * creative inventory.
	 * 
	 * @see #isIn(ItemGroup)
	 */
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.isIn(group)) {
			stacks.add(new ItemStack(this));
		}
	}

	/**
	 * Checks whether this item should appear in a specified item group.
	 * 
	 * @return true if the item is in the specified item group or the item group is {@link net.minecraft.item.ItemGroup#SEARCH}.
	 */
	protected boolean isIn(ItemGroup group) {
		ItemGroup itemGroup = this.getGroup();
		return itemGroup != null && (group == ItemGroup.SEARCH || group == itemGroup);
	}

	@Nullable
	public final ItemGroup getGroup() {
		return this.group;
	}

	/**
	 * {@return whether {@code stack} can be repaired using {@code ingredient}}
	 * 
	 * <p>This only handles repairing using the ingredient such as diamonds, and does
	 * not handle combining tools or armor.
	 */
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return false;
	}

	/**
	 * {@return the attribute modifiers the item provides}
	 * 
	 * <p>Tools and armor should override this to specify the attack damage or armor points.
	 */
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return ImmutableMultimap.of();
	}

	public boolean isUsedOnRelease(ItemStack stack) {
		return false;
	}

	/**
	 * {@return the default stack for this item}
	 * 
	 * <p>Items that expect certain NBT data in the item stack should override
	 * this method to return the stack with the NBT data.
	 */
	public ItemStack getDefaultStack() {
		return new ItemStack(this);
	}

	/**
	 * Checks if this item is food and therefore is edible.
	 */
	public boolean isFood() {
		return this.foodComponent != null;
	}

	/**
	 * {@return this item's {@link #foodComponent FoodComponent}, or {@code null} if none was set}
	 */
	@Nullable
	public FoodComponent getFoodComponent() {
		return this.foodComponent;
	}

	/**
	 * {@return the sound for drinking the item}
	 */
	public SoundEvent getDrinkSound() {
		return SoundEvents.ENTITY_GENERIC_DRINK;
	}

	/**
	 * {@return the sound for eating the item}
	 */
	public SoundEvent getEatSound() {
		return SoundEvents.ENTITY_GENERIC_EAT;
	}

	/**
	 * {@return whether this item is immune to fire and lava damage}
	 */
	public boolean isFireproof() {
		return this.fireproof;
	}

	/**
	 * {@return whether this item can be damaged by the given {@link DamageSource source}}
	 */
	public boolean damage(DamageSource source) {
		return !this.fireproof || !source.isFire();
	}

	/**
	 * {@return the sound for equipping the item, or {@code null} if no sound is played}
	 */
	@Nullable
	public SoundEvent getEquipSound() {
		return null;
	}

	/**
	 * @return true if the item can be placed inside of shulker boxes or bundles.
	 */
	public boolean canBeNested() {
		return true;
	}

	/**
	 * Item settings configure behaviors common to all items, such as the stack's max
	 * count and the item group. An instance of this must be passed to the constructor
	 * of {@link Item} (or most of its subclasses).
	 */
	public static class Settings {
		int maxCount = 64;
		int maxDamage;
		@Nullable
		Item recipeRemainder;
		@Nullable
		ItemGroup group;
		Rarity rarity = Rarity.COMMON;
		@Nullable
		FoodComponent foodComponent;
		boolean fireproof;

		/**
		 * When set, any item configured with this Settings instance will be edible based on the provided {@link FoodComponent}.
		 * 
		 * @return this instance
		 * 
		 * @param foodComponent configured food properties for any item using this Settings instance
		 */
		public Item.Settings food(FoodComponent foodComponent) {
			this.foodComponent = foodComponent;
			return this;
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
			if (this.maxDamage > 0) {
				throw new RuntimeException("Unable to have damage AND stack.");
			} else {
				this.maxCount = maxCount;
				return this;
			}
		}

		/**
		 * Calls {@link Item.Settings#maxDamage} If this Settings instance has not already set max damage (or if max damage is the default value, 0).
		 * 
		 * <p>Note that max stack count is set to 1 when maxDamage is called.
		 * 
		 * @return this instance
		 * 
		 * @param maxDamage maximum durability of an ItemStack using an item with this Item.Settings instance
		 */
		public Item.Settings maxDamageIfAbsent(int maxDamage) {
			return this.maxDamage == 0 ? this.maxDamage(maxDamage) : this;
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
			this.maxDamage = maxDamage;
			this.maxCount = 1;
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
		 * Sets the ItemGroup of any item using this Settings instance. ItemGroups represent tabs in the creative inventory.
		 * 
		 * @return this instance
		 * 
		 * @param group {@link ItemGroup itemGroup} to use
		 */
		public Item.Settings group(ItemGroup group) {
			this.group = group;
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
			this.rarity = rarity;
			return this;
		}

		/**
		 * If called, any item with this Settings instance is immune to fire and lava damage.
		 * 
		 * @return this instance
		 */
		public Item.Settings fireproof() {
			this.fireproof = true;
			return this;
		}
	}
}
