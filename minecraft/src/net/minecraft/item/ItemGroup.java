package net.minecraft.item;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.registry.Registry;

public abstract class ItemGroup {
	public static final ItemGroup[] GROUPS = new ItemGroup[12];
	public static final ItemGroup BUILDING_BLOCKS = (new ItemGroup(0, "buildingBlocks") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack getIconItem() {
			return new ItemStack(Blocks.field_10104);
		}
	}).method_7739("building_blocks");
	public static final ItemGroup DECORATIONS = new ItemGroup(1, "decorations") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack getIconItem() {
			return new ItemStack(Blocks.field_10003);
		}
	};
	public static final ItemGroup REDSTONE = new ItemGroup(2, "redstone") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack getIconItem() {
			return new ItemStack(Items.field_8725);
		}
	};
	public static final ItemGroup TRANSPORTATION = new ItemGroup(3, "transportation") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack getIconItem() {
			return new ItemStack(Blocks.field_10425);
		}
	};
	public static final ItemGroup MISC = new ItemGroup(6, "misc") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack getIconItem() {
			return new ItemStack(Items.field_8187);
		}
	};
	public static final ItemGroup SEARCH = (new ItemGroup(5, "search") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack getIconItem() {
			return new ItemStack(Items.field_8251);
		}
	}).setTexture("item_search.png");
	public static final ItemGroup FOOD = new ItemGroup(7, "food") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack getIconItem() {
			return new ItemStack(Items.field_8279);
		}
	};
	public static final ItemGroup TOOLS = (new ItemGroup(8, "tools") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack getIconItem() {
			return new ItemStack(Items.field_8475);
		}
	}).setEnchantmentTypes(new EnchantmentTarget[]{EnchantmentTarget.ALL, EnchantmentTarget.BREAKER, EnchantmentTarget.FISHING, EnchantmentTarget.TOOL});
	public static final ItemGroup COMBAT = (new ItemGroup(9, "combat") {
			@Environment(EnvType.CLIENT)
			@Override
			public ItemStack getIconItem() {
				return new ItemStack(Items.field_8845);
			}
		})
		.setEnchantmentTypes(
			new EnchantmentTarget[]{
				EnchantmentTarget.ALL,
				EnchantmentTarget.ARMOR,
				EnchantmentTarget.FEET,
				EnchantmentTarget.HELM,
				EnchantmentTarget.LEGS,
				EnchantmentTarget.CHEST,
				EnchantmentTarget.BOW,
				EnchantmentTarget.WEAPON,
				EnchantmentTarget.WEARABLE,
				EnchantmentTarget.TOOL,
				EnchantmentTarget.TRIDENT,
				EnchantmentTarget.CROSSBOW
			}
		);
	public static final ItemGroup BREWING = new ItemGroup(10, "brewing") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack getIconItem() {
			return PotionUtil.setPotion(new ItemStack(Items.field_8574), Potions.field_8991);
		}
	};
	public static final ItemGroup MATERIALS = MISC;
	public static final ItemGroup HOTBAR = new ItemGroup(4, "hotbar") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack getIconItem() {
			return new ItemStack(Blocks.field_10504);
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void getStacksForDisplay(DefaultedList<ItemStack> defaultedList) {
			throw new RuntimeException("Implement exception client-side.");
		}

		@Environment(EnvType.CLIENT)
		@Override
		public boolean isTabRightAligned() {
			return true;
		}
	};
	public static final ItemGroup INVENTORY = (new ItemGroup(11, "inventory") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack getIconItem() {
			return new ItemStack(Blocks.field_10034);
		}
	}).setTexture("inventory.png").setNotUseScrollBar().disableTooltip();
	private final int id;
	private final String unlocalizedName;
	private String field_7926;
	private String texture = "items.png";
	private boolean useScrollBar = true;
	private boolean tooltip = true;
	private EnchantmentTarget[] enchantmentTypes = new EnchantmentTarget[0];
	private ItemStack stack;

	public ItemGroup(int i, String string) {
		this.id = i;
		this.unlocalizedName = string;
		this.stack = ItemStack.EMPTY;
		GROUPS[i] = this;
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public String getUntranslatedName() {
		return this.unlocalizedName;
	}

	public String method_7751() {
		return this.field_7926 == null ? this.unlocalizedName : this.field_7926;
	}

	@Environment(EnvType.CLIENT)
	public String getTranslationKey() {
		return "itemGroup." + this.getUntranslatedName();
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getIconItemStack() {
		if (this.stack.isEmpty()) {
			this.stack = this.getIconItem();
		}

		return this.stack;
	}

	@Environment(EnvType.CLIENT)
	public abstract ItemStack getIconItem();

	@Environment(EnvType.CLIENT)
	public String getTexture() {
		return this.texture;
	}

	public ItemGroup setTexture(String string) {
		this.texture = string;
		return this;
	}

	public ItemGroup method_7739(String string) {
		this.field_7926 = string;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasTooltip() {
		return this.tooltip;
	}

	public ItemGroup disableTooltip() {
		this.tooltip = false;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public boolean useScrollBar() {
		return this.useScrollBar;
	}

	public ItemGroup setNotUseScrollBar() {
		this.useScrollBar = false;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public int getColumn() {
		return this.id % 6;
	}

	@Environment(EnvType.CLIENT)
	public boolean isTopRow() {
		return this.id < 6;
	}

	@Environment(EnvType.CLIENT)
	public boolean isTabRightAligned() {
		return this.getColumn() == 5;
	}

	public EnchantmentTarget[] getEnchantmentTypes() {
		return this.enchantmentTypes;
	}

	public ItemGroup setEnchantmentTypes(EnchantmentTarget... enchantmentTargets) {
		this.enchantmentTypes = enchantmentTargets;
		return this;
	}

	public boolean containsEnchantmentType(@Nullable EnchantmentTarget enchantmentTarget) {
		if (enchantmentTarget != null) {
			for (EnchantmentTarget enchantmentTarget2 : this.enchantmentTypes) {
				if (enchantmentTarget2 == enchantmentTarget) {
					return true;
				}
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	public void getStacksForDisplay(DefaultedList<ItemStack> defaultedList) {
		for (Item item : Registry.ITEM) {
			item.addStacksForDisplay(this, defaultedList);
		}
	}
}
