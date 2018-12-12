package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2290;
import net.minecraft.class_2291;
import net.minecraft.item.ItemStack;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class ItemStackParticleParameters implements ParticleParameters {
	public static final ParticleParameters.Factory<ItemStackParticleParameters> field_11191 = new ParticleParameters.Factory<ItemStackParticleParameters>() {
		public ItemStackParticleParameters read(ParticleType<ItemStackParticleParameters> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			class_2291 lv = new class_2291(stringReader, false).method_9789();
			ItemStack itemStack = new class_2290(lv.method_9786(), lv.method_9797()).method_9781(1, false);
			return new ItemStackParticleParameters(particleType, itemStack);
		}

		public ItemStackParticleParameters read(ParticleType<ItemStackParticleParameters> particleType, PacketByteBuf packetByteBuf) {
			return new ItemStackParticleParameters(particleType, packetByteBuf.readItemStack());
		}
	};
	private final ParticleType<ItemStackParticleParameters> particleType;
	private final ItemStack stack;

	public ItemStackParticleParameters(ParticleType<ItemStackParticleParameters> particleType, ItemStack itemStack) {
		this.particleType = particleType;
		this.stack = itemStack;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeItemStack(this.stack);
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + new class_2290(this.stack.getItem(), this.stack.getTag()).method_9782();
	}

	@Override
	public ParticleType<ItemStackParticleParameters> getType() {
		return this.particleType;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getItemStack() {
		return this.stack;
	}
}
