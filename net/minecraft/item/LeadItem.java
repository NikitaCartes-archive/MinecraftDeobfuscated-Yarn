/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class LeadItem
extends Item {
    public LeadItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos;
        World world = context.getWorld();
        Block block = world.getBlockState(blockPos = context.getBlockPos()).getBlock();
        if (block.isIn(BlockTags.FENCES)) {
            PlayerEntity playerEntity = context.getPlayer();
            if (!world.isClient && playerEntity != null) {
                LeadItem.attachHeldMobsToBlock(playerEntity, world, blockPos);
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    public static ActionResult attachHeldMobsToBlock(PlayerEntity playerEntity, World world, BlockPos blockPos) {
        LeashKnotEntity leashKnotEntity = null;
        boolean bl = false;
        double d = 7.0;
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        List<MobEntity> list = world.getNonSpectatingEntities(MobEntity.class, new Box((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0));
        for (MobEntity mobEntity : list) {
            if (mobEntity.getHoldingEntity() != playerEntity) continue;
            if (leashKnotEntity == null) {
                leashKnotEntity = LeashKnotEntity.getOrCreate(world, blockPos);
            }
            mobEntity.attachLeash(leashKnotEntity, true);
            bl = true;
        }
        return bl ? ActionResult.SUCCESS : ActionResult.PASS;
    }
}

