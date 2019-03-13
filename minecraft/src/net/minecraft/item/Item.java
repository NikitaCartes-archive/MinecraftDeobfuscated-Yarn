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
import net.minecraft.class_4174;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
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

public class Item implements ItemProvider {
	public static final Map<Block, Item> BLOCK_ITEM_MAP = Maps.<Block, Item>newHashMap();
	private static final ItemPropertyGetter field_8010 = (itemStack, world, livingEntity) -> itemStack.isDamaged() ? 1.0F : 0.0F;
	private static final ItemPropertyGetter field_8000 = (itemStack, world, livingEntity) -> MathHelper.clamp(
			(float)itemStack.getDamage() / (float)itemStack.getDurability(), 0.0F, 1.0F
		);
	private static final ItemPropertyGetter field_8011 = (itemStack, world, livingEntity) -> livingEntity != null
				&& livingEntity.getMainHand() != OptionMainHand.field_6183
			? 1.0F
			: 0.0F;
	private static final ItemPropertyGetter field_8007 = (itemStack, world, livingEntity) -> livingEntity instanceof PlayerEntity
			? ((PlayerEntity)livingEntity).method_7357().getCooldownProgress(itemStack.getItem(), 0.0F)
			: 0.0F;
	private static final ItemPropertyGetter field_8002 = (itemStack, world, livingEntity) -> itemStack.hasTag()
			? (float)itemStack.method_7969().getInt("CustomModelData")
			: 0.0F;
	protected static final UUID MODIFIER_DAMAGE = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID MODIFIER_SWING_SPEED = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	protected static final Random random = new Random();
	private final Map<Identifier, ItemPropertyGetter> PROPERTIES = Maps.<Identifier, ItemPropertyGetter>newHashMap();
	protected final ItemGroup itemGroup;
	private final Rarity field_8009;
	private final int fullStackSize;
	private final int durability;
	private final Item recipeRemainder;
	@Nullable
	private String translationKey;
	@Nullable
	private final class_4174 field_18672;

	public static int getRawIdByItem(Item item) {
		return item == null ? 0 : Registry.ITEM.getRawId(item);
	}

	public static Item byRawId(int i) {
		return Registry.ITEM.get(i);
	}

	@Deprecated
	public static Item method_7867(Block block) {
		return (Item)BLOCK_ITEM_MAP.getOrDefault(block, Items.AIR);
	}

	public Item(Item.Settings settings) {
		this.method_7863(new Identifier("lefthanded"), field_8011);
		this.method_7863(new Identifier("cooldown"), field_8007);
		this.method_7863(new Identifier("custom_model_data"), field_8002);
		this.itemGroup = settings.itemGroup;
		this.field_8009 = settings.field_8016;
		this.recipeRemainder = settings.recipeRemainder;
		this.durability = settings.durability;
		this.fullStackSize = settings.fullStackSize;
		this.field_18672 = settings.field_18673;
		if (this.durability > 0) {
			this.method_7863(new Identifier("damaged"), field_8010);
			this.method_7863(new Identifier("damage"), field_8000);
		}
	}

	public void method_7852(World world, LivingEntity livingEntity, ItemStack itemStack, int i) {
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public ItemPropertyGetter method_7868(Identifier identifier) {
		return (ItemPropertyGetter)this.PROPERTIES.get(identifier);
	}

	@Environment(EnvType.CLIENT)
	public boolean hasProperties() {
		return !this.PROPERTIES.isEmpty();
	}

	public boolean method_7860(CompoundTag compoundTag) {
		return false;
	}

	public boolean method_7885(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public Item getItem() {
		return this;
	}

	public final void method_7863(Identifier identifier, ItemPropertyGetter itemPropertyGetter) {
		this.PROPERTIES.put(identifier, itemPropertyGetter);
	}

	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		return ActionResult.PASS;
	}

	public float method_7865(ItemStack itemStack, BlockState blockState) {
		return 1.0F;
	}

	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		if (this.method_19263()) {
			ItemStack itemStack = playerEntity.method_5998(hand);
			if (playerEntity.canConsume(this.method_19264().method_19233())) {
				playerEntity.setCurrentHand(hand);
				return new TypedActionResult<>(ActionResult.field_5812, itemStack);
			} else {
				return new TypedActionResult<>(ActionResult.field_5814, itemStack);
			}
		} else {
			return new TypedActionResult<>(ActionResult.PASS, playerEntity.method_5998(hand));
		}
	}

	public ItemStack method_7861(ItemStack itemStack, World world, LivingEntity livingEntity) {
		return this.method_19263() ? livingEntity.method_18866(world, itemStack) : itemStack;
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

	public boolean method_7873(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		return false;
	}

	public boolean method_7879(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		return false;
	}

	public boolean method_7856(BlockState blockState) {
		return false;
	}

	public boolean method_7847(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent method_7848() {
		return new TranslatableTextComponent(this.getTranslationKey());
	}

	protected String getOrCreateTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = SystemUtil.method_646("item", Registry.ITEM.method_10221(this));
		}

		return this.translationKey;
	}

	public String getTranslationKey() {
		return this.getOrCreateTranslationKey();
	}

	public String method_7866(ItemStack itemStack) {
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

	public void method_7888(ItemStack itemStack, World world, Entity entity, int i, boolean bl) {
	}

	public void method_7843(ItemStack itemStack, World world, PlayerEntity playerEntity) {
	}

	public boolean isMap() {
		return false;
	}

	public UseAction method_7853(ItemStack itemStack) {
		return itemStack.getItem().method_19263() ? UseAction.field_8950 : UseAction.field_8952;
	}

	public int method_7881(ItemStack itemStack) {
		if (itemStack.getItem().method_19263()) {
			return this.method_19264().method_19234() ? 16 : 32;
		} else {
			return 0;
		}
	}

	public void method_7840(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
	}

	@Environment(EnvType.CLIENT)
	public void method_7851(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
	}

	public TextComponent method_7864(ItemStack itemStack) {
		return new TranslatableTextComponent(this.method_7866(itemStack));
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7886(ItemStack itemStack) {
		return itemStack.hasEnchantments();
	}

	public Rarity method_7862(ItemStack itemStack) {
		if (!itemStack.hasEnchantments()) {
			return this.field_8009;
		} else {
			switch (this.field_8009) {
				case field_8906:
				case field_8907:
					return Rarity.field_8903;
				case field_8903:
					return Rarity.field_8904;
				case field_8904:
				default:
					return this.field_8009;
			}
		}
	}

	public boolean method_7870(ItemStack itemStack) {
		return this.getMaxAmount() == 1 && this.canDamage();
	}

	protected static HitResult method_7872(World world, PlayerEntity playerEntity, RayTraceContext.FluidHandling fluidHandling) {
		float f = playerEntity.pitch;
		float g = playerEntity.yaw;
		Vec3d vec3d = playerEntity.method_5836(1.0F);
		float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float i = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
		float k = MathHelper.sin(-f * (float) (Math.PI / 180.0));
		float l = i * j;
		float n = h * j;
		double d = 5.0;
		Vec3d vec3d2 = vec3d.add((double)l * 5.0, (double)k * 5.0, (double)n * 5.0);
		return world.method_17742(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17559, fluidHandling, playerEntity));
	}

	public int getEnchantability() {
		return 0;
	}

	public void method_7850(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
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

	public boolean method_7878(ItemStack itemStack, ItemStack itemStack2) {
		return false;
	}

	public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
		return HashMultimap.create();
	}

	public boolean method_7838(ItemStack itemStack) {
		return itemStack.getItem() == Items.field_8399;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack method_7854() {
		return new ItemStack(this);
	}

	public boolean method_7855(Tag<Item> tag) {
		return tag.contains(this);
	}

	public boolean method_19263() {
		return this.field_18672 != null;
	}

	@Nullable
	public class_4174 method_19264() {
		return this.field_18672;
	}

	public static class Settings {
		private int fullStackSize = 64;
		private int durability;
		private Item recipeRemainder;
		private ItemGroup itemGroup;
		private Rarity field_8016 = Rarity.field_8906;
		private class_4174 field_18673;

		public Item.Settings method_19265(class_4174 arg) {
			this.field_18673 = arg;
			return this;
		}

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

		public Item.Settings method_7894(Rarity rarity) {
			this.field_8016 = rarity;
			return this;
		}
	}
}
