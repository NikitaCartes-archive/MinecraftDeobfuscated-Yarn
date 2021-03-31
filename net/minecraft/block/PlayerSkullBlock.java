/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class PlayerSkullBlock
extends SkullBlock {
    protected PlayerSkullBlock(AbstractBlock.Settings settings) {
        super(SkullBlock.Type.PLAYER, settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SkullBlockEntity) {
            SkullBlockEntity skullBlockEntity = (SkullBlockEntity)blockEntity;
            GameProfile gameProfile = null;
            if (itemStack.hasTag()) {
                NbtCompound nbtCompound = itemStack.getTag();
                if (nbtCompound.contains("SkullOwner", 10)) {
                    gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
                } else if (nbtCompound.contains("SkullOwner", 8) && !StringUtils.isBlank(nbtCompound.getString("SkullOwner"))) {
                    gameProfile = new GameProfile(null, nbtCompound.getString("SkullOwner"));
                }
            }
            skullBlockEntity.setOwner(gameProfile);
        }
    }
}

