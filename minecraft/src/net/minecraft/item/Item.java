package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class Item implements ItemConvertible {
	public static final Map<Block, Item> BLOCK_ITEMS = Maps.<Block, Item>newHashMap();
	private static final ItemPropertyGetter DAMAGED_PROPERTY_GETTER = (itemStack, world, livingEntity) -> itemStack.isDamaged() ? 1.0F : 0.0F;
	private static final ItemPropertyGetter DAMAGE_PROPERTY_GETTER = (itemStack, world, livingEntity) -> MathHelper.clamp(
			(float)itemStack.getDamage() / (float)itemStack.getMaxDamage(), 0.0F, 1.0F
		);
	private static final ItemPropertyGetter LEFTHANDED_PROPERTY_GETTER = (itemStack, world, livingEntity) -> livingEntity != null
				&& livingEntity.getMainArm() != Arm.field_6183
			? 1.0F
			: 0.0F;
	private static final ItemPropertyGetter COOLDOWN_PROPERTY_GETTER = (itemStack, world, livingEntity) -> livingEntity instanceof PlayerEntity
			? ((PlayerEntity)livingEntity).getItemCooldownManager().getCooldownProgress(itemStack.getItem(), 0.0F)
			: 0.0F;
	private static final ItemPropertyGetter CUSTOM_DATA_PROPERTY_GETTER = (itemStack, world, livingEntity) -> itemStack.hasTag()
			? (float)itemStack.getTag().getInt("CustomModelData")
			: 0.0F;
	protected static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	protected static final Random RANDOM = new Random();
	private final Map<Identifier, ItemPropertyGetter> propertyGetters = Maps.<Identifier, ItemPropertyGetter>newHashMap();
	protected final ItemGroup group;
	private final Rarity rarity;
	private final int maxCount;
	private final int maxDamage;
	private final Item recipeRemainder;
	@Nullable
	private String translationKey;
	@Nullable
	private final FoodComponent foodComponent;

	public static int getRawId(Item item) {
		return item == null ? 0 : Registry.ITEM.getRawId(item);
	}

	public static Item byRawId(int i) {
		return Registry.ITEM.get(i);
	}

	@Deprecated
	public static Item fromBlock(Block block) {
		return (Item)BLOCK_ITEMS.getOrDefault(block, Items.AIR);
	}

	public Item(Item.Settings settings) {
		this.addPropertyGetter(new Identifier("lefthanded"), LEFTHANDED_PROPERTY_GETTER);
		this.addPropertyGetter(new Identifier("cooldown"), COOLDOWN_PROPERTY_GETTER);
		this.addPropertyGetter(new Identifier("custom_model_data"), CUSTOM_DATA_PROPERTY_GETTER);
		this.group = settings.group;
		this.rarity = settings.rarity;
		this.recipeRemainder = settings.recipeRemainder;
		this.maxDamage = settings.maxDamage;
		this.maxCount = settings.maxCount;
		this.foodComponent = settings.foodComponent;
		if (this.maxDamage > 0) {
			this.addPropertyGetter(new Identifier("damaged"), DAMAGED_PROPERTY_GETTER);
			this.addPropertyGetter(new Identifier("damage"), DAMAGE_PROPERTY_GETTER);
		}
	}

	public void usageTick(World world, LivingEntity livingEntity, ItemStack itemStack, int i) {
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public ItemPropertyGetter getPropertyGetter(Identifier identifier) {
		return (ItemPropertyGetter)this.propertyGetters.get(identifier);
	}

	@Environment(EnvType.CLIENT)
	public boolean hasPropertyGetters() {
		return !this.propertyGetters.isEmpty();
	}

	public boolean postProcessTag(CompoundTag compoundTag) {
		return false;
	}

	public boolean canMine(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public Item asItem() {
		return this;
	}

	public final void addPropertyGetter(Identifier identifier, ItemPropertyGetter itemPropertyGetter) {
		this.propertyGetters.put(identifier, itemPropertyGetter);
	}

	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		return ActionResult.field_5811;
	}

	public float getMiningSpeed(ItemStack itemStack, BlockState blockState) {
		return 1.0F;
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		if (this.isFood()) {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (playerEntity.canConsume(this.getFoodComponent().isAlwaysEdible())) {
				playerEntity.setCurrentHand(hand);
				return new TypedActionResult<>(ActionResult.field_5812, itemStack);
			} else {
				return new TypedActionResult<>(ActionResult.field_5814, itemStack);
			}
		} else {
			return new TypedActionResult<>(ActionResult.field_5811, playerEntity.getStackInHand(hand));
		}
	}

	public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		return this.isFood() ? livingEntity.eatFood(world, itemStack) : itemStack;
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

	public boolean postHit(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		return false;
	}

	public boolean postMine(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		return false;
	}

	public boolean isEffectiveOn(BlockState blockState) {
		return false;
	}

	public boolean useOnEntity(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		return false;
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
			this.translationKey = SystemUtil.createTranslationKey("item", Registry.ITEM.getId(this));
		}

		return this.translationKey;
	}

	public String getTranslationKey() {
		return this.getOrCreateTranslationKey();
	}

	public String getTranslationKey(ItemStack itemStack) {
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

	public void inventoryTick(ItemStack itemStack, World world, Entity entity, int i, boolean bl) {
	}

	public void onCraft(ItemStack itemStack, World world, PlayerEntity playerEntity) {
	}

	public boolean isNetworkSynced() {
		return false;
	}

	public UseAction getUseAction(ItemStack itemStack) {
		return itemStack.getItem().isFood() ? UseAction.field_8950 : UseAction.field_8952;
	}

	public int getMaxUseTime(ItemStack itemStack) {
		if (itemStack.getItem().isFood()) {
			return this.getFoodComponent().isSnack() ? 16 : 32;
		} else {
			return 0;
		}
	}

	public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
	}

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
	}

	public Text getName(ItemStack itemStack) {
		return new TranslatableText(this.getTranslationKey(itemStack));
	}

	@Environment(EnvType.CLIENT)
	public boolean hasEnchantmentGlint(ItemStack itemStack) {
		return itemStack.hasEnchantments();
	}

	public Rarity getRarity(ItemStack itemStack) {
		if (!itemStack.hasEnchantments()) {
			return this.rarity;
		} else {
			switch (this.rarity) {
				case field_8906:
				case field_8907:
					return Rarity.field_8903;
				case field_8903:
					return Rarity.field_8904;
				case field_8904:
				default:
					return this.rarity;
			}
		}
	}

	public boolean isEnchantable(ItemStack itemStack) {
		return this.getMaxCount() == 1 && this.isDamageable();
	}

	protected static HitResult rayTrace(World world, PlayerEntity playerEntity, RayTraceContext.FluidHandling fluidHandling) {
		float f = playerEntity.pitch;
		float g = playerEntity.yaw;
		Vec3d vec3d = playerEntity.getCameraPosVec(1.0F);
		float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float i = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
		float k = MathHelper.sin(-f * (float) (Math.PI / 180.0));
		float l = i * j;
		float n = h * j;
		double d = 5.0;
		Vec3d vec3d2 = vec3d.add((double)l * 5.0, (double)k * 5.0, (double)n * 5.0);
		return world.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17559, fluidHandling, playerEntity));
	}

	public int getEnchantability() {
		return 0;
	}

	public void appendStacks(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (this.isIn(itemGroup)) {
			defaultedList.add(new ItemStack(this));
		}
	}

	protected boolean isIn(ItemGroup itemGroup) {
		ItemGroup itemGroup2 = this.getGroup();
		return itemGroup2 != null && (itemGroup == ItemGroup.SEARCH || itemGroup == itemGroup2);
	}

	@Nullable
	public final ItemGroup getGroup() {
		return this.group;
	}

	public boolean canRepair(ItemStack itemStack, ItemStack itemStack2) {
		return false;
	}

	public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot equipmentSlot) {
		return HashMultimap.create();
	}

	public boolean isUsedOnRelease(ItemStack itemStack) {
		return itemStack.getItem() == Items.field_8399;
	}

	@Environment(EnvType.CLIENT)
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

	public static class Settings {
		private int maxCount = 64;
		private int maxDamage;
		private Item recipeRemainder;
		private ItemGroup group;
		private Rarity rarity = Rarity.field_8906;
		private FoodComponent foodComponent;

		public Item.Settings food(FoodComponent foodComponent) {
			this.foodComponent = foodComponent;
			return this;
		}

		public Item.Settings maxCount(int i) {
			if (this.maxDamage > 0) {
				throw new RuntimeException("Unable to have damage AND stack.");
			} else {
				this.maxCount = i;
				return this;
			}
		}

		public Item.Settings maxDamageIfAbsent(int i) {
			return this.maxDamage == 0 ? this.maxDamage(i) : this;
		}

		public Item.Settings maxDamage(int i) {
			this.maxDamage = i;
			this.maxCount = 1;
			return this;
		}

		public Item.Settings recipeRemainder(Item item) {
			this.recipeRemainder = item;
			return this;
		}

		public Item.Settings group(ItemGroup itemGroup) {
			this.group = itemGroup;
			return this;
		}

		public Item.Settings rarity(Rarity rarity) {
			this.rarity = rarity;
			return this;
		}
	}
}
