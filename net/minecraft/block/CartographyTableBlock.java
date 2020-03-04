/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.BlockContext;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.NameableScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CartographyTableBlock
extends Block {
    private static final TranslatableText CONTAINER_NAME = new TranslatableText("container.cartography_table", new Object[0]);

    protected CartographyTableBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        player.openHandledScreen(state.createContainerFactory(world, pos));
        player.incrementStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);
        return ActionResult.SUCCESS;
    }

    @Override
    @Nullable
    public NameableScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> new CartographyTableScreenHandler(i, playerInventory, BlockContext.create(world, pos)), CONTAINER_NAME);
    }
}

