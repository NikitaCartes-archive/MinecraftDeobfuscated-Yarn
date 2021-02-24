/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.util.Identifier;

public class UnlockRecipesS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final Action action;
    private final List<Identifier> recipeIdsToChange;
    private final List<Identifier> recipeIdsToInit;
    private final RecipeBookOptions options;

    public UnlockRecipesS2CPacket(Action action, Collection<Identifier> collection, Collection<Identifier> collection2, RecipeBookOptions options) {
        this.action = action;
        this.recipeIdsToChange = ImmutableList.copyOf(collection);
        this.recipeIdsToInit = ImmutableList.copyOf(collection2);
        this.options = options;
    }

    public UnlockRecipesS2CPacket(PacketByteBuf packetByteBuf) {
        this.action = packetByteBuf.readEnumConstant(Action.class);
        this.options = RecipeBookOptions.fromPacket(packetByteBuf);
        this.recipeIdsToChange = packetByteBuf.method_34066(PacketByteBuf::readIdentifier);
        this.recipeIdsToInit = this.action == Action.INIT ? packetByteBuf.method_34066(PacketByteBuf::readIdentifier) : ImmutableList.of();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.action);
        this.options.toPacket(buf);
        buf.method_34062(this.recipeIdsToChange, PacketByteBuf::writeIdentifier);
        if (this.action == Action.INIT) {
            buf.method_34062(this.recipeIdsToInit, PacketByteBuf::writeIdentifier);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onUnlockRecipes(this);
    }

    @Environment(value=EnvType.CLIENT)
    public List<Identifier> getRecipeIdsToChange() {
        return this.recipeIdsToChange;
    }

    @Environment(value=EnvType.CLIENT)
    public List<Identifier> getRecipeIdsToInit() {
        return this.recipeIdsToInit;
    }

    @Environment(value=EnvType.CLIENT)
    public RecipeBookOptions getOptions() {
        return this.options;
    }

    @Environment(value=EnvType.CLIENT)
    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        INIT,
        ADD,
        REMOVE;

    }
}

