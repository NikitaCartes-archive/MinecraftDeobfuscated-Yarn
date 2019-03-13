package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2290;
import net.minecraft.item.ItemStack;
import net.minecraft.sortme.ItemStringReader;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class ItemStackParticleParameters implements ParticleParameters {
	public static final ParticleParameters.Factory<ItemStackParticleParameters> PARAMETERS_FACTORY = new ParticleParameters.Factory<ItemStackParticleParameters>() {
		public ItemStackParticleParameters method_10290(ParticleType<ItemStackParticleParameters> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			ItemStringReader itemStringReader = new ItemStringReader(stringReader, false).consume();
			ItemStack itemStack = new class_2290(itemStringReader.getItem(), itemStringReader.method_9797()).method_9781(1, false);
			return new ItemStackParticleParameters(particleType, itemStack);
		}

		public ItemStackParticleParameters method_10291(ParticleType<ItemStackParticleParameters> particleType, PacketByteBuf packetByteBuf) {
			return new ItemStackParticleParameters(particleType, packetByteBuf.readItemStack());
		}
	};
	private final ParticleType<ItemStackParticleParameters> field_11193;
	private final ItemStack stack;

	public ItemStackParticleParameters(ParticleType<ItemStackParticleParameters> particleType, ItemStack itemStack) {
		this.field_11193 = particleType;
		this.stack = itemStack;
	}

	@Override
	public void method_10294(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeItemStack(this.stack);
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.method_10221(this.method_10295()) + " " + new class_2290(this.stack.getItem(), this.stack.method_7969()).method_9782();
	}

	@Override
	public ParticleType<ItemStackParticleParameters> method_10295() {
		return this.field_11193;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getItemStack() {
		return this.stack;
	}
}
