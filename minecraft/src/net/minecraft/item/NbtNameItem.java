package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class NbtNameItem extends Item {
	private static final String VALUE_KEY = "name";

	public NbtNameItem(Item.Settings settings) {
		super(settings);
	}

	@Nullable
	public static String getValue(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		return nbtCompound != null && nbtCompound.contains("name", NbtElement.STRING_TYPE) ? nbtCompound.getString("name") : null;
	}

	public static void setValue(ItemStack stack, String value) {
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		nbtCompound.putString("name", value);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		String string = getValue(stack);
		if (string != null) {
			tooltip.add(Text.literal(string).formatted(Formatting.GREEN));
		}

		super.appendTooltip(stack, world, tooltip, context);
	}
}
