package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record UseRemainderComponent(ItemStack convertInto) {
	public static final Codec<UseRemainderComponent> CODEC = ItemStack.CODEC.xmap(UseRemainderComponent::new, UseRemainderComponent::convertInto);
	public static final PacketCodec<RegistryByteBuf, UseRemainderComponent> PACKET_CODEC = PacketCodec.tuple(
		ItemStack.PACKET_CODEC, UseRemainderComponent::convertInto, UseRemainderComponent::new
	);

	public ItemStack convert(LivingEntity user, ItemStack stack, int count) {
		boolean bl = user.isInCreativeMode();
		ItemStack itemStack = this.convertInto.copy();
		if (bl) {
			return stack;
		} else if (stack.getCount() >= count) {
			return stack;
		} else if (stack.isEmpty()) {
			return itemStack;
		} else {
			if (!user.getWorld().isClient() && user instanceof PlayerEntity playerEntity && !playerEntity.getInventory().insertStack(itemStack)) {
				playerEntity.dropItem(itemStack, false);
			}

			return stack;
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
}
