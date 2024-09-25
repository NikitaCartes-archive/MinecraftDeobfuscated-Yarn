package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record UseRemainderComponent(ItemStack convertInto) {
	public static final Codec<UseRemainderComponent> CODEC = ItemStack.CODEC.xmap(UseRemainderComponent::new, UseRemainderComponent::convertInto);
	public static final PacketCodec<RegistryByteBuf, UseRemainderComponent> PACKET_CODEC = PacketCodec.tuple(
		ItemStack.PACKET_CODEC, UseRemainderComponent::convertInto, UseRemainderComponent::new
	);

	public ItemStack convert(ItemStack stack, int oldCount, boolean inCreative, UseRemainderComponent.StackInserter inserter) {
		if (inCreative) {
			return stack;
		} else if (stack.getCount() >= oldCount) {
			return stack;
		} else {
			ItemStack itemStack = this.convertInto.copy();
			if (stack.isEmpty()) {
				return itemStack;
			} else {
				inserter.apply(itemStack);
				return stack;
			}
		}
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			UseRemainderComponent useRemainderComponent = (UseRemainderComponent)o;
			return ItemStack.areEqual(this.convertInto, useRemainderComponent.convertInto);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return ItemStack.hashCode(this.convertInto);
	}

	@FunctionalInterface
	public interface StackInserter {
		void apply(ItemStack stack);
	}
}
