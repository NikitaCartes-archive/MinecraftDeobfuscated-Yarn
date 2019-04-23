/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class JigsawBlockEntity
extends BlockEntity {
    private Identifier attachmentType = new Identifier("empty");
    private Identifier targetPool = new Identifier("empty");
    private String finalState = "minecraft:air";

    public JigsawBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    public JigsawBlockEntity() {
        this(BlockEntityType.JIGSAW);
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getAttachmentType() {
        return this.attachmentType;
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getTargetPool() {
        return this.targetPool;
    }

    @Environment(value=EnvType.CLIENT)
    public String getFinalState() {
        return this.finalState;
    }

    public void setAttachmentType(Identifier identifier) {
        this.attachmentType = identifier;
    }

    public void setTargetPool(Identifier identifier) {
        this.targetPool = identifier;
    }

    public void setFinalState(String string) {
        this.finalState = string;
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.putString("attachement_type", this.attachmentType.toString());
        compoundTag.putString("target_pool", this.targetPool.toString());
        compoundTag.putString("final_state", this.finalState);
        return compoundTag;
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.attachmentType = new Identifier(compoundTag.getString("attachement_type"));
        this.targetPool = new Identifier(compoundTag.getString("target_pool"));
        this.finalState = compoundTag.getString("final_state");
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 12, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }
}

