package net.minecraft.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class FishItem extends FoodItem {
	private final boolean cooked;
	private final FishItem.Type type;

	public FishItem(FishItem.Type type, boolean bl, Item.Settings settings) {
		super(0, 0.0F, false, settings);
		this.type = type;
		this.cooked = bl;
	}

	@Override
	public int getHungerRestored(ItemStack itemStack) {
		FishItem.Type type = FishItem.Type.fromStack(itemStack);
		return this.cooked && type.hasCookedVariant() ? type.getHungerRestoredCooked() : type.getHungerRestored();
	}

	@Override
	public float getSaturationModifier(ItemStack itemStack) {
		return this.cooked && this.type.hasCookedVariant() ? this.type.getSaturationModifierCooked() : this.type.getSaturationModifier();
	}

	@Override
	protected void onConsumed(ItemStack itemStack, World world, PlayerEntity playerEntity) {
		FishItem.Type type = FishItem.Type.fromStack(itemStack);
		if (type == FishItem.Type.PUFFERFISH) {
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5899, 1200, 3));
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5903, 300, 2));
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5916, 300, 1));
		}

		super.onConsumed(itemStack, world, playerEntity);
	}

	public static enum Type {
		COD(2, 0.1F, 5, 0.6F),
		SALMON(2, 0.1F, 6, 0.8F),
		TROPICAL_FISH(1, 0.1F),
		PUFFERFISH(1, 0.1F);

		private final int hungerRestored;
		private final float saturationModifier;
		private final int hungerRestoredCooked;
		private final float saturationModifierCooked;
		private final boolean cookedVariant;

		private Type(int j, float f, int k, float g) {
			this.hungerRestored = j;
			this.saturationModifier = f;
			this.hungerRestoredCooked = k;
			this.saturationModifierCooked = g;
			this.cookedVariant = k != 0;
		}

		private Type(int j, float f) {
			this(j, f, 0, 0.0F);
		}

		public int getHungerRestored() {
			return this.hungerRestored;
		}

		public float getSaturationModifier() {
			return this.saturationModifier;
		}

		public int getHungerRestoredCooked() {
			return this.hungerRestoredCooked;
		}

		public float getSaturationModifierCooked() {
			return this.saturationModifierCooked;
		}

		public boolean hasCookedVariant() {
			return this.cookedVariant;
		}

		public static FishItem.Type fromStack(ItemStack itemStack) {
			Item item = itemStack.getItem();
			return item instanceof FishItem ? ((FishItem)item).type : COD;
		}
	}
}
