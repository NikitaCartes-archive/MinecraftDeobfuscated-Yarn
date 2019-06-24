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
		public ItemStack createIcon() {
			return new ItemStack(Blocks.BRICKS);
		}
	}).setName("building_blocks");
	public static final ItemGroup DECORATIONS = new ItemGroup(1, "decorations") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.PEONY);
		}
	};
	public static final ItemGroup REDSTONE = new ItemGroup(2, "redstone") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.REDSTONE);
		}
	};
	public static final ItemGroup TRANSPORTATION = new ItemGroup(3, "transportation") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.POWERED_RAIL);
		}
	};
	public static final ItemGroup MISC = new ItemGroup(6, "misc") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.LAVA_BUCKET);
		}
	};
	public static final ItemGroup SEARCH = (new ItemGroup(5, "search") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.COMPASS);
		}
	}).setTexture("item_search.png");
	public static final ItemGroup FOOD = new ItemGroup(7, "food") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.APPLE);
		}
	};
	public static final ItemGroup TOOLS = (new ItemGroup(8, "tools") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.IRON_AXE);
		}
	}).setEnchantments(new EnchantmentTarget[]{EnchantmentTarget.ALL, EnchantmentTarget.DIGGER, EnchantmentTarget.FISHING_ROD, EnchantmentTarget.BREAKABLE});
	public static final ItemGroup COMBAT = (new ItemGroup(9, "combat") {
			@Environment(EnvType.CLIENT)
			@Override
			public ItemStack createIcon() {
				return new ItemStack(Items.GOLDEN_SWORD);
			}
		})
		.setEnchantments(
			new EnchantmentTarget[]{
				EnchantmentTarget.ALL,
				EnchantmentTarget.ARMOR,
				EnchantmentTarget.ARMOR_FEET,
				EnchantmentTarget.ARMOR_HEAD,
				EnchantmentTarget.ARMOR_LEGS,
				EnchantmentTarget.ARMOR_CHEST,
				EnchantmentTarget.BOW,
				EnchantmentTarget.WEAPON,
				EnchantmentTarget.WEARABLE,
				EnchantmentTarget.BREAKABLE,
				EnchantmentTarget.TRIDENT,
				EnchantmentTarget.CROSSBOW
			}
		);
	public static final ItemGroup BREWING = new ItemGroup(10, "brewing") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack createIcon() {
			return PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
		}
	};
	public static final ItemGroup MATERIALS = MISC;
	public static final ItemGroup HOTBAR = new ItemGroup(4, "hotbar") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.BOOKSHELF);
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void appendStacks(DefaultedList<ItemStack> defaultedList) {
			throw new RuntimeException("Implement exception client-side.");
		}

		@Environment(EnvType.CLIENT)
		@Override
		public boolean isSpecial() {
			return true;
		}
	};
	public static final ItemGroup INVENTORY = (new ItemGroup(11, "inventory") {
		@Environment(EnvType.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.CHEST);
		}
	}).setTexture("inventory.png").setNoScrollbar().setNoTooltip();
	private final int index;
	private final String id;
	private String name;
	private String texture = "items.png";
	private boolean scrollbar = true;
	private boolean tooltip = true;
	private EnchantmentTarget[] enchantments = new EnchantmentTarget[0];
	private ItemStack icon;

	public ItemGroup(int i, String string) {
		this.index = i;
		this.id = string;
		this.icon = ItemStack.EMPTY;
		GROUPS[i] = this;
	}

	@Environment(EnvType.CLIENT)
	public int getIndex() {
		return this.index;
	}

	@Environment(EnvType.CLIENT)
	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name == null ? this.id : this.name;
	}

	@Environment(EnvType.CLIENT)
	public String getTranslationKey() {
		return "itemGroup." + this.getId();
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getIcon() {
		if (this.icon.isEmpty()) {
			this.icon = this.createIcon();
		}

		return this.icon;
	}

	@Environment(EnvType.CLIENT)
	public abstract ItemStack createIcon();

	@Environment(EnvType.CLIENT)
	public String getTexture() {
		return this.texture;
	}

	public ItemGroup setTexture(String string) {
		this.texture = string;
		return this;
	}

	public ItemGroup setName(String string) {
		this.name = string;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasTooltip() {
		return this.tooltip;
	}

	public ItemGroup setNoTooltip() {
		this.tooltip = false;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasScrollbar() {
		return this.scrollbar;
	}

	public ItemGroup setNoScrollbar() {
		this.scrollbar = false;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public int getColumn() {
		return this.index % 6;
	}

	@Environment(EnvType.CLIENT)
	public boolean isTopRow() {
		return this.index < 6;
	}

	@Environment(EnvType.CLIENT)
	public boolean isSpecial() {
		return this.getColumn() == 5;
	}

	public EnchantmentTarget[] getEnchantments() {
		return this.enchantments;
	}

	public ItemGroup setEnchantments(EnchantmentTarget... enchantmentTargets) {
		this.enchantments = enchantmentTargets;
		return this;
	}

	public boolean containsEnchantments(@Nullable EnchantmentTarget enchantmentTarget) {
		if (enchantmentTarget != null) {
			for (EnchantmentTarget enchantmentTarget2 : this.enchantments) {
				if (enchantmentTarget2 == enchantmentTarget) {
					return true;
				}
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	public void appendStacks(DefaultedList<ItemStack> defaultedList) {
		for (Item item : Registry.ITEM) {
			item.appendStacks(this, defaultedList);
		}
	}
}
