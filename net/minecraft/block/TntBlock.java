/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class TntBlock
extends Block {
    public static final BooleanProperty UNSTABLE = Properties.UNSTABLE;

    public TntBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(UNSTABLE, false));
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.getBlock() == blockState.getBlock()) {
            return;
        }
        if (world.isReceivingRedstonePower(blockPos)) {
            TntBlock.primeTnt(world, blockPos);
            world.removeBlock(blockPos, false);
        }
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (world.isReceivingRedstonePower(blockPos)) {
            TntBlock.primeTnt(world, blockPos);
            world.removeBlock(blockPos, false);
        }
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        if (!world.isClient() && !playerEntity.isCreative() && blockState.get(UNSTABLE).booleanValue()) {
            TntBlock.primeTnt(world, blockPos);
        }
        super.onBreak(world, blockPos, blockState, playerEntity);
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
        if (world.isClient) {
            return;
        }
        TntEntity tntEntity = new TntEntity(world, (float)blockPos.getX() + 0.5f, blockPos.getY(), (float)blockPos.getZ() + 0.5f, explosion.getCausingEntity());
        tntEntity.setFuse((short)(world.random.nextInt(tntEntity.getFuseTimer() / 4) + tntEntity.getFuseTimer() / 8));
        world.spawnEntity(tntEntity);
    }

    public static void primeTnt(World world, BlockPos blockPos) {
        TntBlock.primeTnt(world, blockPos, null);
    }

    private static void primeTnt(World world, BlockPos blockPos, @Nullable LivingEntity livingEntity) {
        if (world.isClient) {
            return;
        }
        TntEntity tntEntity = new TntEntity(world, (float)blockPos.getX() + 0.5f, blockPos.getY(), (float)blockPos.getZ() + 0.5f, livingEntity);
        world.spawnEntity(tntEntity);
        world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity2, Hand hand, BlockHitResult blockHitResult) {
        ItemStack itemStack = playerEntity2.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (item == Items.FLINT_AND_STEEL || item == Items.FIRE_CHARGE) {
            TntBlock.primeTnt(world, blockPos, playerEntity2);
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 11);
            if (item == Items.FLINT_AND_STEEL) {
                itemStack.damage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(hand));
            } else {
                itemStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(blockState, world, blockPos, playerEntity2, hand, blockHitResult);
    }

    @Override
    public void onProjectileHit(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
        if (!world.isClient && entity instanceof ProjectileEntity) {
            ProjectileEntity projectileEntity = (ProjectileEntity)entity;
            Entity entity2 = projectileEntity.getOwner();
            if (projectileEntity.isOnFire()) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                TntBlock.primeTnt(world, blockPos, entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null);
                world.removeBlock(blockPos, false);
            }
        }
    }

    @Override
    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UNSTABLE);
    }
}

