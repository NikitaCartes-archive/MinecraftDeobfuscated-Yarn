package net.minecraft.item;

import java.util.function.Supplier;
import net.minecraft.block.Blocks;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Lazy;

public enum ToolMaterials implements ToolMaterial {
	field_8922(0, 59, 2.0F, 0.0F, 15, () -> Ingredient.method_8106(ItemTags.field_15537)),
	field_8927(1, 131, 4.0F, 1.0F, 5, () -> Ingredient.method_8091(Blocks.field_10445)),
	field_8923(2, 250, 6.0F, 2.0F, 14, () -> Ingredient.method_8091(Items.field_8620)),
	field_8930(3, 1561, 8.0F, 3.0F, 10, () -> Ingredient.method_8091(Items.field_8477)),
	field_8929(0, 32, 12.0F, 0.0F, 22, () -> Ingredient.method_8091(Items.field_8695));

	private final int miningLevel;
	private final int durability;
	private final float blockBreakSpeed;
	private final float attackDamage;
	private final int enchantability;
	private final Lazy<Ingredient> field_8928;

	private ToolMaterials(int j, int k, float f, float g, int l, Supplier<Ingredient> supplier) {
		this.miningLevel = j;
		this.durability = k;
		this.blockBreakSpeed = f;
		this.attackDamage = g;
		this.enchantability = l;
		this.field_8928 = new Lazy<>(supplier);
	}

	@Override
	public int getDurability() {
		return this.durability;
	}

	@Override
	public float getBlockBreakingSpeed() {
		return this.blockBreakSpeed;
	}

	@Override
	public float getAttackDamage() {
		return this.attackDamage;
	}

	@Override
	public int getMiningLevel() {
		return this.miningLevel;
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public Ingredient method_8023() {
		return this.field_8928.get();
	}
}
