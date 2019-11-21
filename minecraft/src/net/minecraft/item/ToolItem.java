package net.minecraft.item;

public class ToolItem extends Item {
	private final ToolMaterial material;

	public ToolItem(ToolMaterial material, Item.Settings settings) {
		super(settings.maxDamageIfAbsent(material.getDurability()));
		this.material = material;
	}

	public ToolMaterial getMaterial() {
		return this.material;
	}

	@Override
	public int getEnchantability() {
		return this.material.getEnchantability();
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
	}
}
