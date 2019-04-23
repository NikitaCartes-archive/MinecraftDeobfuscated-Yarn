/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SignBlockEntity
extends BlockEntity {
    public final Component[] text = new Component[]{new TextComponent(""), new TextComponent(""), new TextComponent(""), new TextComponent("")};
    @Environment(value=EnvType.CLIENT)
    private boolean caretVisible;
    private int currentRow = -1;
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private boolean editable = true;
    private PlayerEntity editor;
    private final String[] textBeingEdited = new String[4];
    private DyeColor textColor = DyeColor.BLACK;

    public SignBlockEntity() {
        super(BlockEntityType.SIGN);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        for (int i = 0; i < 4; ++i) {
            String string = Component.Serializer.toJsonString(this.text[i]);
            compoundTag.putString("Text" + (i + 1), string);
        }
        compoundTag.putString("Color", this.textColor.getName());
        return compoundTag;
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        this.editable = false;
        super.fromTag(compoundTag);
        this.textColor = DyeColor.byName(compoundTag.getString("Color"), DyeColor.BLACK);
        for (int i = 0; i < 4; ++i) {
            String string = compoundTag.getString("Text" + (i + 1));
            Component component = Component.Serializer.fromJsonString(string);
            if (this.world instanceof ServerWorld) {
                try {
                    this.text[i] = Components.resolveAndStyle(this.getCommandSource(null), component, null);
                } catch (CommandSyntaxException commandSyntaxException) {
                    this.text[i] = component;
                }
            } else {
                this.text[i] = component;
            }
            this.textBeingEdited[i] = null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public Component getTextOnRow(int i) {
        return this.text[i];
    }

    public void setTextOnRow(int i, Component component) {
        this.text[i] = component;
        this.textBeingEdited[i] = null;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public String getTextBeingEditedOnRow(int i, Function<Component, String> function) {
        if (this.textBeingEdited[i] == null && this.text[i] != null) {
            this.textBeingEdited[i] = function.apply(this.text[i]);
        }
        return this.textBeingEdited[i];
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 9, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    @Override
    public boolean shouldNotCopyTagFromItem() {
        return true;
    }

    public boolean isEditable() {
        return this.editable;
    }

    @Environment(value=EnvType.CLIENT)
    public void setEditable(boolean bl) {
        this.editable = bl;
        if (!bl) {
            this.editor = null;
        }
    }

    public void setEditor(PlayerEntity playerEntity) {
        this.editor = playerEntity;
    }

    public PlayerEntity getEditor() {
        return this.editor;
    }

    public boolean onActivate(PlayerEntity playerEntity) {
        for (Component component : this.text) {
            ClickEvent clickEvent;
            Style style;
            Style style2 = style = component == null ? null : component.getStyle();
            if (style == null || style.getClickEvent() == null || (clickEvent = style.getClickEvent()).getAction() != ClickEvent.Action.RUN_COMMAND) continue;
            playerEntity.getServer().getCommandManager().execute(this.getCommandSource((ServerPlayerEntity)playerEntity), clickEvent.getValue());
        }
        return true;
    }

    public ServerCommandSource getCommandSource(@Nullable ServerPlayerEntity serverPlayerEntity) {
        String string = serverPlayerEntity == null ? "Sign" : serverPlayerEntity.getName().getString();
        Component component = serverPlayerEntity == null ? new TextComponent("Sign") : serverPlayerEntity.getDisplayName();
        return new ServerCommandSource(CommandOutput.DUMMY, new Vec3d((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5), Vec2f.ZERO, (ServerWorld)this.world, 2, string, component, this.world.getServer(), serverPlayerEntity);
    }

    public DyeColor getTextColor() {
        return this.textColor;
    }

    public boolean setTextColor(DyeColor dyeColor) {
        if (dyeColor != this.getTextColor()) {
            this.textColor = dyeColor;
            this.markDirty();
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
            return true;
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public void setSelectionState(int i, int j, int k, boolean bl) {
        this.currentRow = i;
        this.selectionStart = j;
        this.selectionEnd = k;
        this.caretVisible = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public void resetSelectionState() {
        this.currentRow = -1;
        this.selectionStart = -1;
        this.selectionEnd = -1;
        this.caretVisible = false;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isCaretVisible() {
        return this.caretVisible;
    }

    @Environment(value=EnvType.CLIENT)
    public int getCurrentRow() {
        return this.currentRow;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSelectionStart() {
        return this.selectionStart;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSelectionEnd() {
        return this.selectionEnd;
    }
}

