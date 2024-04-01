package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LubricationComponent implements TooltipAppender {
	public static final Codec<LubricationComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.INT.fieldOf("level").forGetter(component -> component.level)).apply(instance, LubricationComponent::new)
	);
	public static final PacketCodec<ByteBuf, LubricationComponent> PACKET_CODEC = PacketCodecs.codec(CODEC);
	private final int level;
	private final float lubricationFactor;

	public LubricationComponent(int level) {
		this.level = level;
		this.lubricationFactor = getLubricationFactor(level);
	}

	public boolean isLubricated() {
		return this.level >= 1;
	}

	public int getLevel() {
		return this.level;
	}

	@Override
	public void appendTooltip(Consumer<Text> textConsumer, TooltipContext context) {
		if (this.isLubricated()) {
			MutableText mutableText = this.level == 1
				? Text.translatable("lubrication.tooltip.lubricated")
				: Text.translatable("lubrication.tooltip.lubricated_times", this.level);
			textConsumer.accept(mutableText.formatted(Formatting.GOLD));
		}

		if (context.isAdvanced()) {
			textConsumer.accept(Text.literal("lubricationFactor: " + this.lubricationFactor).formatted(Formatting.GRAY));
		}
	}

	private static float getLubricationFactor(int level) {
		return level <= 0 ? 0.0F : 1.0F - (float)Math.pow(0.75, (double)level + 6.228262518959627);
	}

	public float method_59187(float f) {
		return this.isLubricated() ? 1.0F - (1.0F - f) * (1.0F - this.lubricationFactor) : f;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			LubricationComponent lubricationComponent = (LubricationComponent)o;
			return this.level == lubricationComponent.level;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.level});
	}

	public static void addLubricationLevel(ItemStack stack) {
		LubricationComponent lubricationComponent = stack.get(DataComponentTypes.LUBRICATION);
		if (lubricationComponent != null) {
			stack.set(DataComponentTypes.LUBRICATION, new LubricationComponent(lubricationComponent.level + 1));
		} else {
			stack.set(DataComponentTypes.LUBRICATION, new LubricationComponent(1));
		}
	}
}
