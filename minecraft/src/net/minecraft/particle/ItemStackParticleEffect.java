package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class ItemStackParticleEffect implements ParticleEffect {
	private static final Codec<ItemStack> ITEM_STACK_CODEC = Codec.withAlternative(ItemStack.UNCOUNTED_CODEC, ItemStack.ITEM_CODEC, ItemStack::new);
	private final ParticleType<ItemStackParticleEffect> type;
	private final ItemStack stack;

	public static MapCodec<ItemStackParticleEffect> createCodec(ParticleType<ItemStackParticleEffect> type) {
		return ITEM_STACK_CODEC.<ItemStackParticleEffect>xmap(stack -> new ItemStackParticleEffect(type, stack), effect -> effect.stack).fieldOf("item");
	}

	public static PacketCodec<? super RegistryByteBuf, ItemStackParticleEffect> createPacketCodec(ParticleType<ItemStackParticleEffect> type) {
		return ItemStack.PACKET_CODEC.xmap(stack -> new ItemStackParticleEffect(type, stack), effect -> effect.stack);
	}

	public ItemStackParticleEffect(ParticleType<ItemStackParticleEffect> type, ItemStack stack) {
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Empty stacks are not allowed");
		} else {
			this.type = type;
			this.stack = stack;
		}
	}

	@Override
	public ParticleType<ItemStackParticleEffect> getType() {
		return this.type;
	}

	public ItemStack getItemStack() {
		return this.stack;
	}
}
