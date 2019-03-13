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
	public boolean method_7878(ItemStack itemStack, ItemStack itemStack2) {
		return this.material.method_8023().method_8093(itemStack2) || super.method_7878(itemStack, itemStack2);
	}
}
