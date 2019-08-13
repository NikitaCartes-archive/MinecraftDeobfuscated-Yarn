package net.minecraft.item;

public class ToolItem extends Item {
	private final ToolMaterial material;

	public ToolItem(ToolMaterial toolMaterial, Item.Settings settings) {
		super(settings.maxDamageIfAbsent(toolMaterial.getDurability()));
		this.material = toolMaterial;
	}

	public ToolMaterial getMaterial() {
		return this.material;
	}

	@Override
	public int getEnchantability() {
		return this.material.getEnchantability();
	}

	@Override
	public boolean canRepair(ItemStack itemStack, ItemStack itemStack2) {
		return this.material.getRepairIngredient().method_8093(itemStack2) || super.canRepair(itemStack, itemStack2);
	}
}
