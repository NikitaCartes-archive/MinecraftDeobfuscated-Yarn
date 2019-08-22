/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class RecipeBookDataC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Mode mode;
    private Identifier recipeId;
    private boolean guiOpen;
    private boolean filteringCraftable;
    private boolean furnaceGuiOpen;
    private boolean furnaceFilteringCraftable;
    private boolean blastFurnaceGuiOpen;
    private boolean blastFurnaceFilteringCraftable;
    private boolean smokerGuiOpen;
    private boolean smokerGuiFilteringCraftable;

    public RecipeBookDataC2SPacket() {
    }

    public RecipeBookDataC2SPacket(Recipe<?> recipe) {
        this.mode = Mode.SHOWN;
        this.recipeId = recipe.getId();
    }

    @Environment(value=EnvType.CLIENT)
    public RecipeBookDataC2SPacket(boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6) {
        this.mode = Mode.SETTINGS;
        this.guiOpen = bl;
        this.filteringCraftable = bl2;
        this.furnaceGuiOpen = bl3;
        this.furnaceFilteringCraftable = bl4;
        this.blastFurnaceGuiOpen = bl5;
        this.blastFurnaceFilteringCraftable = bl6;
        this.smokerGuiOpen = bl5;
        this.smokerGuiFilteringCraftable = bl6;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.mode = packetByteBuf.readEnumConstant(Mode.class);
        if (this.mode == Mode.SHOWN) {
            this.recipeId = packetByteBuf.readIdentifier();
        } else if (this.mode == Mode.SETTINGS) {
            this.guiOpen = packetByteBuf.readBoolean();
            this.filteringCraftable = packetByteBuf.readBoolean();
            this.furnaceGuiOpen = packetByteBuf.readBoolean();
            this.furnaceFilteringCraftable = packetByteBuf.readBoolean();
            this.blastFurnaceGuiOpen = packetByteBuf.readBoolean();
            this.blastFurnaceFilteringCraftable = packetByteBuf.readBoolean();
            this.smokerGuiOpen = packetByteBuf.readBoolean();
            this.smokerGuiFilteringCraftable = packetByteBuf.readBoolean();
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeEnumConstant(this.mode);
        if (this.mode == Mode.SHOWN) {
            packetByteBuf.writeIdentifier(this.recipeId);
        } else if (this.mode == Mode.SETTINGS) {
            packetByteBuf.writeBoolean(this.guiOpen);
            packetByteBuf.writeBoolean(this.filteringCraftable);
            packetByteBuf.writeBoolean(this.furnaceGuiOpen);
            packetByteBuf.writeBoolean(this.furnaceFilteringCraftable);
            packetByteBuf.writeBoolean(this.blastFurnaceGuiOpen);
            packetByteBuf.writeBoolean(this.blastFurnaceFilteringCraftable);
            packetByteBuf.writeBoolean(this.smokerGuiOpen);
            packetByteBuf.writeBoolean(this.smokerGuiFilteringCraftable);
        }
    }

    public void method_12400(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onRecipeBookData(this);
    }

    public Mode getMode() {
        return this.mode;
    }

    public Identifier getRecipeId() {
        return this.recipeId;
    }

    public boolean isGuiOpen() {
        return this.guiOpen;
    }

    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }

    public boolean isFurnaceGuiOpen() {
        return this.furnaceGuiOpen;
    }

    public boolean isFurnaceFilteringCraftable() {
        return this.furnaceFilteringCraftable;
    }

    public boolean isBlastFurnaceGuiOpen() {
        return this.blastFurnaceGuiOpen;
    }

    public boolean isBlastFurnaceFilteringCraftable() {
        return this.blastFurnaceFilteringCraftable;
    }

    public boolean isSmokerGuiOpen() {
        return this.smokerGuiOpen;
    }

    public boolean isSmokerGuiFilteringCraftable() {
        return this.smokerGuiFilteringCraftable;
    }

    public static enum Mode {
        SHOWN,
        SETTINGS;

    }
}

