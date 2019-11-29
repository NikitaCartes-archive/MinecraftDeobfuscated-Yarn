package net.minecraft.enchantment;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;

public enum EnchantmentTarget {
	ALL {
		@Override
		public boolean isAcceptableItem(Item item) {
			for (EnchantmentTarget enchantmentTarget : EnchantmentTarget.values()) {
				if (enchantmentTarget != EnchantmentTarget.ALL && enchantmentTarget.isAcceptableItem(item)) {
					return true;
				}
			}

			return false;
		}
	},
	ARMOR {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem;
		}
	},
	ARMOR_FEET {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.FEET;
		}
	},
	ARMOR_LEGS {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.LEGS;
		}
	},
	ARMOR_CHEST {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.CHEST;
		}
	},
	ARMOR_HEAD {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.HEAD;
		}
	},
	WEAPON {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof SwordItem || item instanceof AxeItem;
		}
	},
	DIGGER {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof MiningToolItem;
		}
	},
	FISHING_ROD {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof FishingRodItem;
		}
	},
	TRIDENT {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof TridentItem;
		}
	},
	BREAKABLE {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item.isDamageable();
		}
	},
	BOW {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof BowItem;
		}
	},
	WEARABLE {
		@Override
		public boolean isAcceptableItem(Item item) {
			Block block = Block.getBlockFromItem(item);
			return item instanceof ArmorItem || item instanceof ElytraItem || block instanceof AbstractSkullBlock || block instanceof CarvedPumpkinBlock;
		}
	},
	CROSSBOW {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof CrossbowItem;
		}
	},
	AXE {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof AxeItem;
		}
	};

	private EnchantmentTarget() {
	}

	public abstract boolean isAcceptableItem(Item item);
}
