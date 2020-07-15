/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface PosArgument {
    public Vec3d toAbsolutePos(ServerCommandSource var1);

    public Vec2f toAbsoluteRotation(ServerCommandSource var1);

    default public BlockPos toAbsoluteBlockPos(ServerCommandSource source) {
        return new BlockPos(this.toAbsolutePos(source));
    }

    public boolean isXRelative();

    public boolean isYRelative();

    public boolean isZRelative();
}

