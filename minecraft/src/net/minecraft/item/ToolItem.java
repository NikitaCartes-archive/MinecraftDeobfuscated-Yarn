package net.minecraft.item;

public class ToolItem extends Item {
	private final ToolMaterial material;

	public ToolItem(ToolMaterial toolMaterial, Item.Settings settings) {
		super(settings.durabilityIfNotSet(toolMaterial.getDurability()));
		this.material = toolMaterial;
	}

	public ToolMaterial getType() {
		return this.material;
	}

	@Override
	public int getEnchantability() {
		return this.material.getEnchantability();
	}

	@Override
	public boolean canRepair(ItemStack itemStack, ItemStack itemStack2) {
		return this.material.getRepairIngredient().matches(itemStack2) || super.canRepair(itemStack, itemStack2);
	}
}
