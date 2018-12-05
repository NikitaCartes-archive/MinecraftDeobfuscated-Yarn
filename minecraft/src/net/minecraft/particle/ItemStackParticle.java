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

public class ItemStackParticle implements Particle {
	public static final Particle.class_2395<ItemStackParticle> field_11191 = new Particle.class_2395<ItemStackParticle>() {
		public ItemStackParticle method_10290(ParticleType<ItemStackParticle> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			class_2291 lv = new class_2291(stringReader, false).method_9789();
			ItemStack itemStack = new class_2290(lv.method_9786(), lv.method_9797()).method_9781(1, false);
			return new ItemStackParticle(particleType, itemStack);
		}

		public ItemStackParticle method_10291(ParticleType<ItemStackParticle> particleType, PacketByteBuf packetByteBuf) {
			return new ItemStackParticle(particleType, packetByteBuf.readItemStack());
		}
	};
	private final ParticleType<ItemStackParticle> particleType;
	private final ItemStack stack;

	public ItemStackParticle(ParticleType<ItemStackParticle> particleType, ItemStack itemStack) {
		this.particleType = particleType;
		this.stack = itemStack;
	}

	@Override
	public void method_10294(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeItemStack(this.stack);
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(this.getParticleType()) + " " + new class_2290(this.stack.getItem(), this.stack.getTag()).method_9782();
	}

	@Override
	public ParticleType<ItemStackParticle> getParticleType() {
		return this.particleType;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getItemStack() {
		return this.stack;
	}
}
