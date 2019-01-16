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
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.tag.Tag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.HitResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class Item implements ItemProvider {
	public static final Map<Block, Item> BLOCK_ITEM_MAP = Maps.<Block, Item>newHashMap();
	private static final ItemPropertyGetter GETTER_DAMAGED = (itemStack, world, livingEntity) -> itemStack.isDamaged() ? 1.0F : 0.0F;
	private static final ItemPropertyGetter GETTER_DAMAGE = (itemStack, world, livingEntity) -> MathHelper.clamp(
			(float)itemStack.getDamage() / (float)itemStack.getDurability(), 0.0F, 1.0F
		);
	private static final ItemPropertyGetter GETTER_HAND = (itemStack, world, livingEntity) -> livingEntity != null
				&& livingEntity.getMainHand() != OptionMainHand.field_6183
			? 1.0F
			: 0.0F;
	private static final ItemPropertyGetter GETTER_COOLDOWN = (itemStack, world, livingEntity) -> livingEntity instanceof PlayerEntity
			? ((PlayerEntity)livingEntity).getItemCooldownManager().method_7905(itemStack.getItem(), 0.0F)
			: 0.0F;
	private static final ItemPropertyGetter GETTER_CUSTOM_MODEL_DATA = (itemStack, world, livingEntity) -> itemStack.hasTag()
			? (float)itemStack.getTag().getInt("CustomModelData")
			: 0.0F;
	protected static final UUID MODIFIER_DAMAGE = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID MODIFIER_SWING_SPEED = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	protected static final Random random = new Random();
	private final Map<Identifier, ItemPropertyGetter> PROPERTIES = Maps.<Identifier, ItemPropertyGetter>newHashMap();
	protected final ItemGroup itemGroup;
	private final Rarity rarity;
	private final int fullStackSize;
	private final int durability;
	private final Item recipeRemainder;
	@Nullable
	private String translationKey;

	public static int getRawIdByItem(Item item) {
		return item == null ? 0 : Registry.ITEM.getRawId(item);
	}

	public static Item byRawId(int i) {
		return Registry.ITEM.getInt(i);
	}

	@Deprecated
	public static Item getItemFromBlock(Block block) {
		return (Item)BLOCK_ITEM_MAP.getOrDefault(block, Items.AIR);
	}

	public Item(Item.Settings settings) {
		this.addProperty(new Identifier("lefthanded"), GETTER_HAND);
		this.addProperty(new Identifier("cooldown"), GETTER_COOLDOWN);
		this.addProperty(new Identifier("custom_model_data"), GETTER_CUSTOM_MODEL_DATA);
		this.itemGroup = settings.itemGroup;
		this.rarity = settings.rarity;
		this.recipeRemainder = settings.recipeRemainder;
		this.durability = settings.durability;
		this.fullStackSize = settings.fullStackSize;
		if (this.durability > 0) {
			this.addProperty(new Identifier("damaged"), GETTER_DAMAGED);
			this.addProperty(new Identifier("damage"), GETTER_DAMAGE);
		}
	}

	public void method_7852(World world, LivingEntity livingEntity, ItemStack itemStack, int i) {
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public ItemPropertyGetter getProperty(Identifier identifier) {
		return (ItemPropertyGetter)this.PROPERTIES.get(identifier);
	}

	@Environment(EnvType.CLIENT)
	public boolean hasProperties() {
		return !this.PROPERTIES.isEmpty();
	}

	public boolean onTagDeserialized(CompoundTag compoundTag) {
		return false;
	}

	public boolean beforeBlockBreak(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public Item getItem() {
		return this;
	}

	public final void addProperty(Identifier identifier, ItemPropertyGetter itemPropertyGetter) {
		this.PROPERTIES.put(identifier, itemPropertyGetter);
	}

	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		return ActionResult.PASS;
	}

	public float getBlockBreakingSpeed(ItemStack itemStack, BlockState blockState) {
		return 1.0F;
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		return new TypedActionResult<>(ActionResult.PASS, playerEntity.getStackInHand(hand));
	}

	public ItemStack onItemFinishedUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		return itemStack;
	}

	public final int getMaxAmount() {
		return this.fullStackSize;
	}

	public final int getDurability() {
		return this.durability;
	}

	public boolean canDamage() {
		return this.durability > 0;
	}

	public boolean onEntityDamaged(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		return false;
	}

	public boolean onBlockBroken(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		return false;
	}

	public boolean isEffectiveOn(BlockState blockState) {
		return false;
	}

	public boolean interactWithEntity(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getTextComponent() {
		return new TranslatableTextComponent(this.getTranslationKey());
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

	public boolean requiresClientSync() {
		return true;
	}

	@Nullable
	public final Item getRecipeRemainder() {
		return this.recipeRemainder;
	}

	public boolean hasRecipeRemainder() {
		return this.recipeRemainder != null;
	}

	public void onUpdate(ItemStack itemStack, World world, Entity entity, int i, boolean bl) {
	}

	public void onCrafted(ItemStack itemStack, World world, PlayerEntity playerEntity) {
	}

	public boolean isMap() {
		return false;
	}

	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.field_8952;
	}

	public int getMaxUseTime(ItemStack itemStack) {
		return 0;
	}

	public void onItemStopUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
	}

	@Environment(EnvType.CLIENT)
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipOptions tooltipOptions) {
	}

	public TextComponent getTranslatedNameTrimmed(ItemStack itemStack) {
		return new TranslatableTextComponent(this.getTranslationKey(itemStack));
	}

	@Environment(EnvType.CLIENT)
	public boolean hasEnchantmentGlow(ItemStack itemStack) {
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

	public boolean isTool(ItemStack itemStack) {
		return this.getMaxAmount() == 1 && this.canDamage();
	}

	protected static HitResult method_7872(World world, PlayerEntity playerEntity, RayTraceContext.FluidHandling fluidHandling) {
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

	public void addStacksForDisplay(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (this.isInItemGroup(itemGroup)) {
			defaultedList.add(new ItemStack(this));
		}
	}

	protected boolean isInItemGroup(ItemGroup itemGroup) {
		ItemGroup itemGroup2 = this.getItemGroup();
		return itemGroup2 != null && (itemGroup == ItemGroup.SEARCH || itemGroup == itemGroup2);
	}

	@Nullable
	public final ItemGroup getItemGroup() {
		return this.itemGroup;
	}

	public boolean canRepair(ItemStack itemStack, ItemStack itemStack2) {
		return false;
	}

	public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
		return HashMultimap.create();
	}

	public boolean method_7838(ItemStack itemStack) {
		return itemStack.getItem() == Items.field_8399;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getDefaultStack() {
		return new ItemStack(this);
	}

	public boolean matches(Tag<Item> tag) {
		return tag.contains(this);
	}

	public static class Settings {
		private int fullStackSize = 64;
		private int durability;
		private Item recipeRemainder;
		private ItemGroup itemGroup;
		private Rarity rarity = Rarity.field_8906;

		public Item.Settings stackSize(int i) {
			if (this.durability > 0) {
				throw new RuntimeException("Unable to have damage AND stack.");
			} else {
				this.fullStackSize = i;
				return this;
			}
		}

		public Item.Settings durabilityIfNotSet(int i) {
			return this.durability == 0 ? this.durability(i) : this;
		}

		public Item.Settings durability(int i) {
			this.durability = i;
			this.fullStackSize = 1;
			return this;
		}

		public Item.Settings recipeRemainder(Item item) {
			this.recipeRemainder = item;
			return this;
		}

		public Item.Settings itemGroup(ItemGroup itemGroup) {
			this.itemGroup = itemGroup;
			return this;
		}

		public Item.Settings rarity(Rarity rarity) {
			this.rarity = rarity;
			return this;
		}
	}
}
