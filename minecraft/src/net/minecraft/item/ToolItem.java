package net.minecraft.item;

/**
 * An {@link Item} used as a tool, typically used for harvesting blocks or killing entities.
 * 
 * <p>
 * Each {@link ToolItem} has a {@link ToolMaterial} which defines base tool statistics for it.
 * By default, {@link ToolItem#getEnchantability()} and {@link ToolItem#canRepair(ItemStack, ItemStack)} will
 * delegate to this material for values. Behavior for other material properties is implemented in {@link net.minecraft.item.MiningToolItem}.
 * 
 * <p>
 * A list of default vanilla tool classes can be found below:
 * <ul>
 *     <li>Sword: {@link net.minecraft.item.SwordItem}</li>
 *     <li>Pickaxe: {@link net.minecraft.item.PickaxeItem}</li>
 *     <li>Shovel: {@link net.minecraft.item.ShovelItem}</li>
 *     <li>Axe: {@link net.minecraft.item.AxeItem}</li>
 *     <li>Hoe: {@link net.minecraft.item.HoeItem}</li>
 * </ul>
 */
public class ToolItem extends Item {
	private final ToolMaterial material;

	public ToolItem(ToolMaterial material, Item.Settings settings) {
		super(settings.maxDamage(material.getDurability()));
		this.material = material;
	}

	/**
	 * @return the {@link ToolMaterial} used by this {@link ToolItem}
	 */
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
