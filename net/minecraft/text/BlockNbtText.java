/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.class_7419;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public record BlockNbtText(String rawPos, @Nullable PosArgument pos) implements class_7419
{
    public BlockNbtText(String rawPath) {
        this(rawPath, BlockNbtText.parsePos(rawPath));
    }

    @Nullable
    private static PosArgument parsePos(String string) {
        try {
            return BlockPosArgumentType.blockPos().parse(new StringReader(string));
        } catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    @Override
    public Stream<NbtCompound> toNbt(ServerCommandSource serverCommandSource) {
        BlockEntity blockEntity;
        BlockPos blockPos;
        ServerWorld serverWorld;
        if (this.pos != null && (serverWorld = serverCommandSource.getWorld()).canSetBlock(blockPos = this.pos.toAbsoluteBlockPos(serverCommandSource)) && (blockEntity = serverWorld.getBlockEntity(blockPos)) != null) {
            return Stream.of(blockEntity.createNbtWithIdentifyingData());
        }
        return Stream.empty();
    }

    @Override
    public String toString() {
        return "block=" + this.rawPos;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BlockNbtText)) return false;
        BlockNbtText blockNbtText = (BlockNbtText)object;
        if (!this.rawPos.equals(blockNbtText.rawPos)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return this.rawPos.hashCode();
    }

    @Nullable
    public PosArgument pos() {
        return this.pos;
    }
}

