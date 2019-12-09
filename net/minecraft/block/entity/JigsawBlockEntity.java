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

    public void setAttachmentType(Identifier value) {
        this.attachmentType = value;
    }

    public void setTargetPool(Identifier value) {
        this.targetPool = value;
    }

    public void setFinalState(String value) {
        this.finalState = value;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putString("attachement_type", this.attachmentType.toString());
        tag.putString("target_pool", this.targetPool.toString());
        tag.putString("final_state", this.finalState);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.attachmentType = new Identifier(tag.getString("attachement_type"));
        this.targetPool = new Identifier(tag.getString("target_pool"));
        this.finalState = tag.getString("final_state");
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

