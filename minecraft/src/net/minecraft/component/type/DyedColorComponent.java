package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;

public record DyedColorComponent(int rgb, boolean showInTooltip) implements TooltipAppender {
	private static final Codec<DyedColorComponent> BASE_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("rgb").forGetter(DyedColorComponent::rgb),
					Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(DyedColorComponent::showInTooltip)
				)
				.apply(instance, DyedColorComponent::new)
	);
	public static final Codec<DyedColorComponent> CODEC = Codec.withAlternative(BASE_CODEC, Codec.INT, rgb -> new DyedColorComponent(rgb, true));
	public static final PacketCodec<ByteBuf, DyedColorComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.INTEGER, DyedColorComponent::rgb, PacketCodecs.BOOL, DyedColorComponent::showInTooltip, DyedColorComponent::new
	);
	public static final int DEFAULT_COLOR = -6265536;

	public static int getColor(ItemStack stack, int defaultColor) {
		DyedColorComponent dyedColorComponent = stack.get(DataComponentTypes.DYED_COLOR);
		return dyedColorComponent != null ? ColorHelper.Argb.fullAlpha(dyedColorComponent.rgb()) : defaultColor;
	}

	public static ItemStack setColor(ItemStack stack, List<DyeItem> dyes) {
		if (!stack.isIn(ItemTags.DYEABLE)) {
			return ItemStack.EMPTY;
		} else {
			ItemStack itemStack = stack.copyWithCount(1);
			int i = 0;
			int j = 0;
			int k = 0;
			int l = 0;
			int m = 0;
			DyedColorComponent dyedColorComponent = itemStack.get(DataComponentTypes.DYED_COLOR);
			if (dyedColorComponent != null) {
				int n = ColorHelper.Argb.getRed(dyedColorComponent.rgb());
				int o = ColorHelper.Argb.getGreen(dyedColorComponent.rgb());
				int p = ColorHelper.Argb.getBlue(dyedColorComponent.rgb());
				l += Math.max(n, Math.max(o, p));
				i += n;
				j += o;
				k += p;
				m++;
			}

			for (DyeItem dyeItem : dyes) {
				float[] fs = dyeItem.getColor().getColorComponents();
				int q = (int)(fs[0] * 255.0F);
				int r = (int)(fs[1] * 255.0F);
				int s = (int)(fs[2] * 255.0F);
				l += Math.max(q, Math.max(r, s));
				i += q;
				j += r;
				k += s;
				m++;
			}

			int n = i / m;
			int o = j / m;
			int p = k / m;
			float f = (float)l / (float)m;
			float g = (float)Math.max(n, Math.max(o, p));
			n = (int)((float)n * f / g);
			o = (int)((float)o * f / g);
			p = (int)((float)p * f / g);
			int s = ColorHelper.Argb.getArgb(0, n, o, p);
			boolean bl = dyedColorComponent == null || dyedColorComponent.showInTooltip();
			itemStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(s, bl));
			return itemStack;
		}
	}

	@Override
	public void appendTooltip(Consumer<Text> textConsumer, TooltipType context) {
		if (this.showInTooltip) {
			if (context.isAdvanced()) {
				textConsumer.accept(Text.translatable("item.color", String.format(Locale.ROOT, "#%06X", this.rgb)).formatted(Formatting.GRAY));
			} else {
				textConsumer.accept(Text.translatable("item.dyed").formatted(Formatting.GRAY, Formatting.ITALIC));
			}
		}
	}

	public DyedColorComponent withShowInTooltip(boolean showInTooltip) {
		return new DyedColorComponent(this.rgb, showInTooltip);
	}
}
