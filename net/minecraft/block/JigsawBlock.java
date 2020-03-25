/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.class_5000;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class JigsawBlock
extends Block
implements BlockEntityProvider {
    public static final EnumProperty<class_5000> field_23262 = Properties.field_23333;

    protected JigsawBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(field_23262, class_5000.field_23391));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(field_23262);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(field_23262, rotation.method_26383().method_26389(state.get(field_23262)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)state.with(field_23262, mirror.method_26380().method_26389(state.get(field_23262)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        Direction direction2 = direction.getAxis() == Direction.Axis.Y ? ctx.getPlayerFacing().getOpposite() : Direction.UP;
        return (BlockState)this.getDefaultState().with(field_23262, class_5000.method_26425(direction, direction2));
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView world) {
        return new JigsawBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof JigsawBlockEntity && player.isCreativeLevelTwoOp()) {
            player.openJigsawScreen((JigsawBlockEntity)blockEntity);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static boolean attachmentMatches(Structure.StructureBlockInfo info1, Structure.StructureBlockInfo info2) {
        Direction direction = JigsawBlock.method_26378(info1.state);
        Direction direction2 = JigsawBlock.method_26378(info2.state);
        Direction direction3 = JigsawBlock.method_26379(info1.state);
        Direction direction4 = JigsawBlock.method_26379(info2.state);
        JigsawBlockEntity.class_4991 lv = JigsawBlockEntity.class_4991.method_26401(info1.tag.getString("joint")).orElseGet(() -> direction.getAxis().isHorizontal() ? JigsawBlockEntity.class_4991.field_23330 : JigsawBlockEntity.class_4991.field_23329);
        boolean bl = lv == JigsawBlockEntity.class_4991.field_23329;
        return direction == direction2.getOpposite() && (bl || direction3 == direction4) && info1.tag.getString("target").equals(info2.tag.getString("name"));
    }

    public static Direction method_26378(BlockState blockState) {
        return blockState.get(field_23262).method_26426();
    }

    public static Direction method_26379(BlockState blockState) {
        return blockState.get(field_23262).method_26428();
    }
}

