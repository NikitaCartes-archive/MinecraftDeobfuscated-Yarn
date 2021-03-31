package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class ItemStackParticleEffect implements ParticleEffect {
	public static final ParticleEffect.Factory<ItemStackParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<ItemStackParticleEffect>() {
		public ItemStackParticleEffect read(ParticleType<ItemStackParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			ItemStringReader itemStringReader = new ItemStringReader(stringReader, false).consume();
			ItemStack itemStack = new ItemStackArgument(itemStringReader.getItem(), itemStringReader.getNbt()).createStack(1, false);
			return new ItemStackParticleEffect(particleType, itemStack);
		}

		public ItemStackParticleEffect read(ParticleType<ItemStackParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new ItemStackParticleEffect(particleType, packetByteBuf.readItemStack());
		}
	};
	private final ParticleType<ItemStackParticleEffect> type;
	private final ItemStack stack;

	public static Codec<ItemStackParticleEffect> method_29136(ParticleType<ItemStackParticleEffect> particleType) {
		return ItemStack.CODEC.xmap(itemStack -> new ItemStackParticleEffect(particleType, itemStack), itemStackParticleEffect -> itemStackParticleEffect.stack);
	}

	public ItemStackParticleEffect(ParticleType<ItemStackParticleEffect> type, ItemStack stack) {
		this.type = type;
		this.stack = stack;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeItemStack(this.stack);
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + new ItemStackArgument(this.stack.getItem(), this.stack.getTag()).asString();
	}

	@Override
	public ParticleType<ItemStackParticleEffect> getType() {
		return this.type;
	}

	public ItemStack getItemStack() {
		return this.stack;
	}
}
