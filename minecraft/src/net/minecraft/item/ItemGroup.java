package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public abstract class ItemGroup {
	public static final ItemGroup[] GROUPS = new ItemGroup[12];
	public static final ItemGroup BUILDING_BLOCKS = (new ItemGroup(0, "buildingBlocks") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.BRICKS);
		}
	}).setName("building_blocks");
	public static final ItemGroup DECORATIONS = new ItemGroup(1, "decorations") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.PEONY);
		}
	};
	public static final ItemGroup REDSTONE = new ItemGroup(2, "redstone") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.REDSTONE);
		}
	};
	public static final ItemGroup TRANSPORTATION = new ItemGroup(3, "transportation") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.POWERED_RAIL);
		}
	};
	public static final ItemGroup MISC = new ItemGroup(6, "misc") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.LAVA_BUCKET);
		}
	};
	public static final ItemGroup SEARCH = (new ItemGroup(5, "search") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.COMPASS);
		}
	}).setTexture("item_search.png");
	public static final ItemGroup FOOD = new ItemGroup(7, "food") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.APPLE);
		}
	};
	public static final ItemGroup TOOLS = (new ItemGroup(8, "tools") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.IRON_AXE);
		}
	}).setEnchantments(new EnchantmentTarget[]{EnchantmentTarget.VANISHABLE, EnchantmentTarget.DIGGER, EnchantmentTarget.FISHING_ROD, EnchantmentTarget.BREAKABLE});
	public static final ItemGroup COMBAT = (new ItemGroup(9, "combat") {
			@Override
			public ItemStack createIcon() {
				return new ItemStack(Items.GOLDEN_SWORD);
			}
		})
		.setEnchantments(
			new EnchantmentTarget[]{
				EnchantmentTarget.VANISHABLE,
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
		@Override
		public ItemStack createIcon() {
			return PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
		}
	};
	public static final ItemGroup MATERIALS = MISC;
	public static final ItemGroup HOTBAR = new ItemGroup(4, "hotbar") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.BOOKSHELF);
		}

		@Override
		public void appendStacks(DefaultedList<ItemStack> stacks) {
			throw new RuntimeException("Implement exception client-side.");
		}

		@Override
		public boolean isSpecial() {
			return true;
		}
	};
	public static final ItemGroup INVENTORY = (new ItemGroup(11, "inventory") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.CHEST);
		}
	}).setTexture("inventory.png").setNoScrollbar().hideName();
	private final int index;
	private final String id;
	private final Text displayName;
	private String name;
	private String texture = "items.png";
	private boolean scrollbar = true;
	private boolean renderName = true;
	private EnchantmentTarget[] enchantments = new EnchantmentTarget[0];
	private ItemStack icon;

	public ItemGroup(int index, String id) {
		this.index = index;
		this.id = id;
		this.displayName = Text.translatable("itemGroup." + id);
		this.icon = ItemStack.EMPTY;
		GROUPS[index] = this;
	}

	public int getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name == null ? this.id : this.name;
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public ItemStack getIcon() {
		if (this.icon.isEmpty()) {
			this.icon = this.createIcon();
		}

		return this.icon;
	}

	public abstract ItemStack createIcon();

	public String getTexture() {
		return this.texture;
	}

	public ItemGroup setTexture(String texture) {
		this.texture = texture;
		return this;
	}

	public ItemGroup setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Checks if this item group should render its name.
	 * 
	 * <p>The name is rendered below the top row of item groups and above the inventory.
	 */
	public boolean shouldRenderName() {
		return this.renderName;
	}

	/**
	 * Specifies that when this item group is selected, the name of the item group should not be rendered.
	 */
	public ItemGroup hideName() {
		this.renderName = false;
		return this;
	}

	public boolean hasScrollbar() {
		return this.scrollbar;
	}

	public ItemGroup setNoScrollbar() {
		this.scrollbar = false;
		return this;
	}

	public int getColumn() {
		return this.index % 6;
	}

	public boolean isTopRow() {
		return this.index < 6;
	}

	public boolean isSpecial() {
		return this.getColumn() == 5;
	}

	public EnchantmentTarget[] getEnchantments() {
		return this.enchantments;
	}

	public ItemGroup setEnchantments(EnchantmentTarget... targets) {
		this.enchantments = targets;
		return this;
	}

	public boolean containsEnchantments(@Nullable EnchantmentTarget target) {
		if (target != null) {
			for (EnchantmentTarget enchantmentTarget : this.enchantments) {
				if (enchantmentTarget == target) {
					return true;
				}
			}
		}

		return false;
	}

	public void appendStacks(DefaultedList<ItemStack> stacks) {
		for (Item item : Registry.ITEM) {
			item.appendStacks(this, stacks);
		}
	}
}
