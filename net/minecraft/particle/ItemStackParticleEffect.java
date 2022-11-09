/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;

public class ItemStackParticleEffect
implements ParticleEffect {
    public static final ParticleEffect.Factory<ItemStackParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<ItemStackParticleEffect>(){

        @Override
        public ItemStackParticleEffect read(ParticleType<ItemStackParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            ItemStringReader.ItemResult itemResult = ItemStringReader.item(Registries.ITEM.getReadOnlyWrapper(), stringReader);
            ItemStack itemStack = new ItemStackArgument(itemResult.item(), itemResult.nbt()).createStack(1, false);
            return new ItemStackParticleEffect(particleType, itemStack);
        }

        @Override
        public ItemStackParticleEffect read(ParticleType<ItemStackParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new ItemStackParticleEffect(particleType, packetByteBuf.readItemStack());
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType type, PacketByteBuf buf) {
            return this.read(type, buf);
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException {
            return this.read(type, reader);
        }
    };
    private final ParticleType<ItemStackParticleEffect> type;
    private final ItemStack stack;

    public static Codec<ItemStackParticleEffect> createCodec(ParticleType<ItemStackParticleEffect> type) {
        return ItemStack.CODEC.xmap(stack -> new ItemStackParticleEffect(type, (ItemStack)stack), effect -> effect.stack);
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
        return Registries.PARTICLE_TYPE.getId(this.getType()) + " " + new ItemStackArgument(this.stack.getRegistryEntry(), this.stack.getNbt()).asString();
    }

    public ParticleType<ItemStackParticleEffect> getType() {
        return this.type;
    }

    public ItemStack getItemStack() {
        return this.stack;
    }
}

