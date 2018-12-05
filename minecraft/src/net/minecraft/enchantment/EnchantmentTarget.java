package net.minecraft.enchantment;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
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
	FEET {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.FEET;
		}
	},
	LEGS {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.LEGS;
		}
	},
	CHEST {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.CHEST;
		}
	},
	HELM {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.HEAD;
		}
	},
	WEAPON {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof SwordItem;
		}
	},
	BREAKER {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof MiningToolItem;
		}
	},
	FISHING {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof FishingRodItem;
		}
	},
	field_9073 {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof TridentItem;
		}
	},
	TOOL {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item.canDamage();
		}
	},
	BOW {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof BowItem;
		}
	},
	field_9078 {
		@Override
		public boolean isAcceptableItem(Item item) {
			Block block = Block.getBlockFromItem(item);
			return item instanceof ArmorItem || item instanceof ElytraItem || block instanceof AbstractSkullBlock || block instanceof PumpkinBlock;
		}
	},
	field_9081 {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof CrossbowItem;
		}
	};

	private EnchantmentTarget() {
	}

	public abstract boolean isAcceptableItem(Item item);
}
