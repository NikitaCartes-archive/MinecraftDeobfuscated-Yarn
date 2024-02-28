package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

public class ItemStackParticleEffect implements ParticleEffect {
	public static final ParticleEffect.Factory<ItemStackParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<ItemStackParticleEffect>() {
		public ItemStackParticleEffect read(
			ParticleType<ItemStackParticleEffect> particleType, StringReader stringReader, RegistryWrapper.WrapperLookup wrapperLookup
		) throws CommandSyntaxException {
			stringReader.expect(' ');
			ItemStringReader.ItemResult itemResult = new ItemStringReader(wrapperLookup).consume(stringReader);
			ItemStack itemStack = new ItemStackArgument(itemResult.item(), itemResult.components()).createStack(1, false);
			return new ItemStackParticleEffect(particleType, itemStack);
		}
	};
	private final ParticleType<ItemStackParticleEffect> type;
	private final ItemStack stack;

	public static Codec<ItemStackParticleEffect> createCodec(ParticleType<ItemStackParticleEffect> type) {
		return ItemStack.CODEC.xmap(stack -> new ItemStackParticleEffect(type, stack), effect -> effect.stack);
	}

	public static PacketCodec<? super RegistryByteBuf, ItemStackParticleEffect> createPacketCodec(ParticleType<ItemStackParticleEffect> type) {
		return ItemStack.PACKET_CODEC.xmap(stack -> new ItemStackParticleEffect(type, stack), effect -> effect.stack);
	}

	public ItemStackParticleEffect(ParticleType<ItemStackParticleEffect> type, ItemStack stack) {
		this.type = type;
		this.stack = stack;
	}

	@Override
	public String asString(RegistryWrapper.WrapperLookup registryLookup) {
		ItemStackArgument itemStackArgument = new ItemStackArgument(this.stack.getRegistryEntry(), this.stack.getComponents());
		return Registries.PARTICLE_TYPE.getId(this.getType()) + " " + itemStackArgument.asString(registryLookup);
	}

	@Override
	public ParticleType<ItemStackParticleEffect> getType() {
		return this.type;
	}

	public ItemStack getItemStack() {
		return this.stack;
	}
}
