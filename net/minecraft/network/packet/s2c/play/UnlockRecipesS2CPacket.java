/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
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
    private Action action;
    private List<Identifier> recipeIdsToChange;
    private List<Identifier> recipeIdsToInit;
    private RecipeBookOptions options;

    public UnlockRecipesS2CPacket() {
    }

    public UnlockRecipesS2CPacket(Action action, Collection<Identifier> recipeIdsToChange, Collection<Identifier> recipeIdsToInit, RecipeBookOptions options) {
        this.action = action;
        this.recipeIdsToChange = ImmutableList.copyOf(recipeIdsToChange);
        this.recipeIdsToInit = ImmutableList.copyOf(recipeIdsToInit);
        this.options = options;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onUnlockRecipes(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        int j;
        this.action = buf.readEnumConstant(Action.class);
        this.options = RecipeBookOptions.fromPacket(buf);
        int i = buf.readVarInt();
        this.recipeIdsToChange = Lists.newArrayList();
        for (j = 0; j < i; ++j) {
            this.recipeIdsToChange.add(buf.readIdentifier());
        }
        if (this.action == Action.INIT) {
            i = buf.readVarInt();
            this.recipeIdsToInit = Lists.newArrayList();
            for (j = 0; j < i; ++j) {
                this.recipeIdsToInit.add(buf.readIdentifier());
            }
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.action);
        this.options.toPacket(buf);
        buf.writeVarInt(this.recipeIdsToChange.size());
        for (Identifier identifier : this.recipeIdsToChange) {
            buf.writeIdentifier(identifier);
        }
        if (this.action == Action.INIT) {
            buf.writeVarInt(this.recipeIdsToInit.size());
            for (Identifier identifier : this.recipeIdsToInit) {
                buf.writeIdentifier(identifier);
            }
        }
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

