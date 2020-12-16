package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Item implements ItemConvertible {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Map<Block, Item> BLOCK_ITEMS = Maps.<Block, Item>newHashMap();
	protected static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
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

	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
	}

	public void onItemEntityDestroyed(ItemEntity entity) {
	}

	public boolean postProcessTag(CompoundTag tag) {
		return false;
	}

	/**
	 * Checks if a player can break a block while holding the item.
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

	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		return 1.0F;
	}

	/**
	 * Called when an item is used by a player.
	 * The use action, by default, is bound to the right mouse button.
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

	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity livingEntity) {
		return this.isFood() ? livingEntity.eatFood(world, stack) : stack;
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

	@Environment(EnvType.CLIENT)
	public boolean isItemBarVisible(ItemStack stack) {
		return stack.isDamaged();
	}

	@Environment(EnvType.CLIENT)
	public int getItemBarStep(ItemStack stack) {
		return Math.round(13.0F - (float)stack.getDamage() * 13.0F / (float)this.maxDamage);
	}

	@Environment(EnvType.CLIENT)
	public int getItemBarColor(ItemStack stack) {
		float f = Math.max(0.0F, ((float)this.maxDamage - (float)stack.getDamage()) / (float)this.maxDamage);
		return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}

	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerInventory playerInventory) {
		return false;
	}

	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerInventory playerInventory) {
		return false;
	}

	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return false;
	}

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
	 * </p>
	 */
	public boolean isSuitableFor(BlockState state) {
		return false;
	}

	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		return ActionResult.PASS;
	}

	@Environment(EnvType.CLIENT)
	public Text getName() {
		return new TranslatableText(this.getTranslationKey());
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
	 * Checks if an item should have its NBT data stored in {@link ItemStack#tag} sent to the client.
	 * 
	 * <p>If an item is damageable, this method is ignored and data is always synced to client.
	 */
	public boolean shouldSyncTagToClient() {
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

	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
	}

	/**
	 * Called when a player acquires the item by crafting, smelting, smithing, etc.
	 */
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
		} else {
			return 0;
		}
	}

	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
	}

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
	}

	@Environment(EnvType.CLIENT)
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return Optional.empty();
	}

	public Text getName(ItemStack stack) {
		return new TranslatableText(this.getTranslationKey(stack));
	}

	/**
	 * Checks if the glint effect should be applied when the item is rendered.
	 * 
	 * <p>By default, returns true if the item has enchantments.
	 */
	public boolean hasGlint(ItemStack stack) {
		return stack.hasEnchantments();
	}

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

	public boolean isEnchantable(ItemStack stack) {
		return this.getMaxCount() == 1 && this.isDamageable();
	}

	protected static BlockHitResult raycast(World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling) {
		float f = player.pitch;
		float g = player.yaw;
		Vec3d vec3d = player.getCameraPosVec(1.0F);
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

	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return false;
	}

	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return ImmutableMultimap.of();
	}

	public boolean isUsedOnRelease(ItemStack stack) {
		return false;
	}

	public ItemStack getDefaultStack() {
		return new ItemStack(this);
	}

	/**
	 * Checks if this item is food and therefore is edible.
	 */
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

	@Nullable
	public SoundEvent getEquipSound() {
		return null;
	}

	public boolean hasStoredInventory() {
		return true;
	}

	public static class Settings {
		private int maxCount = 64;
		private int maxDamage;
		private Item recipeRemainder;
		private ItemGroup group;
		private Rarity rarity = Rarity.COMMON;
		private FoodComponent foodComponent;
		private boolean fireproof;

		public Item.Settings food(FoodComponent foodComponent) {
			this.foodComponent = foodComponent;
			return this;
		}

		public Item.Settings maxCount(int maxCount) {
			if (this.maxDamage > 0) {
				throw new RuntimeException("Unable to have damage AND stack.");
			} else {
				this.maxCount = maxCount;
				return this;
			}
		}

		public Item.Settings maxDamageIfAbsent(int maxDamage) {
			return this.maxDamage == 0 ? this.maxDamage(maxDamage) : this;
		}

		public Item.Settings maxDamage(int maxDamage) {
			this.maxDamage = maxDamage;
			this.maxCount = 1;
			return this;
		}

		public Item.Settings recipeRemainder(Item recipeRemainder) {
			this.recipeRemainder = recipeRemainder;
			return this;
		}

		public Item.Settings group(ItemGroup group) {
			this.group = group;
			return this;
		}

		public Item.Settings rarity(Rarity rarity) {
			this.rarity = rarity;
			return this;
		}

		public Item.Settings fireproof() {
			this.fireproof = true;
			return this;
		}
	}
}
