/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
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

    public UnlockRecipesS2CPacket(Action action, Collection<Identifier> recipeIdsToChange, Collection<Identifier> recipeIdsToInit, RecipeBookOptions options) {
        this.action = action;
        this.recipeIdsToChange = ImmutableList.copyOf(recipeIdsToChange);
        this.recipeIdsToInit = ImmutableList.copyOf(recipeIdsToInit);
        this.options = options;
    }

    public UnlockRecipesS2CPacket(PacketByteBuf buf) {
        this.action = buf.readEnumConstant(Action.class);
        this.options = RecipeBookOptions.fromPacket(buf);
        this.recipeIdsToChange = buf.readList(PacketByteBuf::readIdentifier);
        this.recipeIdsToInit = this.action == Action.INIT ? buf.readList(PacketByteBuf::readIdentifier) : ImmutableList.of();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.action);
        this.options.toPacket(buf);
        buf.writeCollection(this.recipeIdsToChange, PacketByteBuf::writeIdentifier);
        if (this.action == Action.INIT) {
            buf.writeCollection(this.recipeIdsToInit, PacketByteBuf::writeIdentifier);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onUnlockRecipes(this);
    }

    public List<Identifier> getRecipeIdsToChange() {
        return this.recipeIdsToChange;
    }

    public List<Identifier> getRecipeIdsToInit() {
        return this.recipeIdsToInit;
    }

    public RecipeBookOptions getOptions() {
        return this.options;
    }

    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        INIT,
        ADD,
        REMOVE;

    }
}

