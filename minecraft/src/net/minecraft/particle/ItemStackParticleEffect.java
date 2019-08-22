package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.command.arguments.ItemStringReader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class ItemStackParticleEffect implements ParticleEffect {
	public static final ParticleEffect.Factory<ItemStackParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<ItemStackParticleEffect>() {
		public ItemStackParticleEffect method_10290(ParticleType<ItemStackParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			ItemStringReader itemStringReader = new ItemStringReader(stringReader, false).consume();
			ItemStack itemStack = new ItemStackArgument(itemStringReader.getItem(), itemStringReader.getTag()).createStack(1, false);
			return new ItemStackParticleEffect(particleType, itemStack);
		}

		public ItemStackParticleEffect method_10291(ParticleType<ItemStackParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new ItemStackParticleEffect(particleType, packetByteBuf.readItemStack());
		}
	};
	private final ParticleType<ItemStackParticleEffect> type;
	private final ItemStack stack;

	public ItemStackParticleEffect(ParticleType<ItemStackParticleEffect> particleType, ItemStack itemStack) {
		this.type = particleType;
		this.stack = itemStack;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeItemStack(this.stack);
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + new ItemStackArgument(this.stack.getItem(), this.stack.getTag()).method_9782();
	}

	@Override
	public ParticleType<ItemStackParticleEffect> getType() {
		return this.type;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getItemStack() {
		return this.stack;
	}
}
