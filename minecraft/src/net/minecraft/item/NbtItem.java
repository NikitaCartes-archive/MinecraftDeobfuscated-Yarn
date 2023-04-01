package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class NbtItem<T extends NbtElement> extends Item {
	private static final String VALUE_KEY = "value";
	private final NbtType<T> type;

	public NbtItem(Item.Settings settings, NbtType<T> type) {
		super(settings);
		this.type = type;
	}

	public NbtType<T> getType() {
		return this.type;
	}

	@Nullable
	public T getValue(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound == null) {
			return null;
		} else if (this.type != NbtCompound.TYPE) {
			NbtElement nbtElement = nbtCompound.get("value");
			return (T)(nbtElement != null && nbtElement.getNbtType() == this.type ? nbtElement : null);
		} else {
			return (T)nbtCompound;
		}
	}

	public void setValue(ItemStack stack, T nbt) {
		if (this.type != NbtCompound.TYPE) {
			NbtCompound nbtCompound = stack.getOrCreateNbt();
			nbtCompound.put("value", nbt);
		} else if (nbt instanceof NbtCompound nbtCompound2) {
			stack.setNbt(nbtCompound2);
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		T nbtElement = this.getValue(stack);
		if (nbtElement != null) {
			tooltip.add(Text.literal(nbtElement.asString()).formatted(Formatting.GREEN));
		}

		super.appendTooltip(stack, world, tooltip, context);
	}
}
