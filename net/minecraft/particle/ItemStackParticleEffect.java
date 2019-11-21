/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.command.arguments.ItemStringReader;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class ItemStackParticleEffect
implements ParticleEffect {
    public static final ParticleEffect.Factory<ItemStackParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<ItemStackParticleEffect>(){

        @Override
        public ItemStackParticleEffect read(ParticleType<ItemStackParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            ItemStringReader itemStringReader = new ItemStringReader(stringReader, false).consume();
            ItemStack itemStack = new ItemStackArgument(itemStringReader.getItem(), itemStringReader.getTag()).createStack(1, false);
            return new ItemStackParticleEffect(particleType, itemStack);
        }

        @Override
        public ItemStackParticleEffect read(ParticleType<ItemStackParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new ItemStackParticleEffect(particleType, packetByteBuf.readItemStack());
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType particleType, PacketByteBuf packetByteBuf) {
            return this.read(particleType, packetByteBuf);
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType particleType, StringReader stringReader) throws CommandSyntaxException {
            return this.read(particleType, stringReader);
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
        return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + new ItemStackArgument(this.stack.getItem(), this.stack.getTag()).asString();
    }

    public ParticleType<ItemStackParticleEffect> getType() {
        return this.type;
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getItemStack() {
        return this.stack;
    }
}

