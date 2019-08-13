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
	field_9075 {
		@Override
		public boolean isAcceptableItem(Item item) {
			for (EnchantmentTarget enchantmentTarget : EnchantmentTarget.values()) {
				if (enchantmentTarget != EnchantmentTarget.field_9075 && enchantmentTarget.isAcceptableItem(item)) {
					return true;
				}
			}

			return false;
		}
	},
	field_9068 {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem;
		}
	},
	field_9079 {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.field_6166;
		}
	},
	field_9076 {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.field_6172;
		}
	},
	field_9071 {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.field_6174;
		}
	},
	field_9080 {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.field_6169;
		}
	},
	field_9074 {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof SwordItem;
		}
	},
	field_9069 {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item instanceof MiningToolItem;
		}
	},
	field_9072 {
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
	field_9082 {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item.isDamageable();
		}
	},
	field_9070 {
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
