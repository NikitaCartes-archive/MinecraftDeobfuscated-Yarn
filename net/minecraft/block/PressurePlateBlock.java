/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.List;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PressurePlateBlock
extends AbstractPressurePlateBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    private final ActivationRule type;

    protected PressurePlateBlock(ActivationRule activationRule, Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(POWERED, false));
        this.type = activationRule;
    }

    @Override
    protected int getRedstoneOutput(BlockState blockState) {
        return blockState.get(POWERED) != false ? 15 : 0;
    }

    @Override
    protected BlockState setRedstoneOutput(BlockState blockState, int i) {
        return (BlockState)blockState.with(POWERED, i > 0);
    }

    @Override
    protected void playPressSound(IWorld iWorld, BlockPos blockPos) {
        if (this.material == Material.WOOD) {
            iWorld.playSound(null, blockPos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3f, 0.8f);
        } else {
            iWorld.playSound(null, blockPos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3f, 0.6f);
        }
    }

    @Override
    protected void playDepressSound(IWorld iWorld, BlockPos blockPos) {
        if (this.material == Material.WOOD) {
            iWorld.playSound(null, blockPos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3f, 0.7f);
        } else {
            iWorld.playSound(null, blockPos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3f, 0.5f);
        }
    }

    @Override
    protected int getRedstoneOutput(World world, BlockPos blockPos) {
        List<Entity> list;
        Box box = BOX.offset(blockPos);
        switch (this.type) {
            case EVERYTHING: {
                list = world.getEntities(null, box);
                break;
            }
            case MOBS: {
                list = world.getNonSpectatingEntities(LivingEntity.class, box);
                break;
            }
            default: {
                return 0;
            }
        }
        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (entity.canAvoidTraps()) continue;
                return 15;
            }
        }
        return 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    public static enum ActivationRule {
        EVERYTHING,
        MOBS;

    }
}

