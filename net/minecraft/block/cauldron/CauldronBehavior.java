/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.cauldron;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.WaterCauldronBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface CauldronBehavior {
    public static final Map<Item, CauldronBehavior> EMPTY_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();
    public static final Map<Item, CauldronBehavior> WATER_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();
    public static final Map<Item, CauldronBehavior> LAVA_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();
    public static final CauldronBehavior FILL_WITH_WATER = (blockState, world, blockPos, playerEntity, hand, itemStack) -> CauldronBehavior.fillCauldron(world, blockPos, playerEntity, hand, itemStack, (BlockState)Blocks.WATER_CAULDRON.getDefaultState().with(WaterCauldronBlock.LEVEL, 3));
    public static final CauldronBehavior FILL_WITH_LAVA = (blockState, world, blockPos, playerEntity, hand, itemStack) -> CauldronBehavior.fillCauldron(world, blockPos, playerEntity, hand, itemStack, Blocks.LAVA_CAULDRON.getDefaultState());
    public static final CauldronBehavior CLEAN_SHULKER_BOX = (blockState, world, blockPos, playerEntity, hand, itemStack) -> {
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (!(block instanceof ShulkerBoxBlock)) {
            return ActionResult.PASS;
        }
        if (!world.isClient) {
            ItemStack itemStack2 = new ItemStack(Blocks.SHULKER_BOX);
            if (itemStack.hasTag()) {
                itemStack2.setTag(itemStack.getTag().copy());
            }
            playerEntity.setStackInHand(hand, itemStack2);
            playerEntity.incrementStat(Stats.CLEAN_SHULKER_BOX);
            WaterCauldronBlock.subtractWaterLevel(blockState, world, blockPos);
        }
        return ActionResult.success(world.isClient);
    };
    public static final CauldronBehavior CLEAN_BANNER = (blockState, world, blockPos, playerEntity, hand, itemStack) -> {
        if (BannerBlockEntity.getPatternCount(itemStack) <= 0) {
            return ActionResult.PASS;
        }
        if (!world.isClient) {
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setCount(1);
            BannerBlockEntity.loadFromItemStack(itemStack2);
            if (!playerEntity.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            if (itemStack.isEmpty()) {
                playerEntity.setStackInHand(hand, itemStack2);
            } else if (playerEntity.getInventory().insertStack(itemStack2)) {
                ((ServerPlayerEntity)playerEntity).refreshScreenHandler(playerEntity.playerScreenHandler);
            } else {
                playerEntity.dropItem(itemStack2, false);
            }
            playerEntity.incrementStat(Stats.CLEAN_BANNER);
            WaterCauldronBlock.subtractWaterLevel(blockState, world, blockPos);
        }
        return ActionResult.success(world.isClient);
    };
    public static final CauldronBehavior CLEAN_DYEABLE_ITEM = (blockState, world, blockPos, playerEntity, hand, itemStack) -> {
        Item item = itemStack.getItem();
        if (!(item instanceof DyeableItem)) {
            return ActionResult.PASS;
        }
        DyeableItem dyeableItem = (DyeableItem)((Object)item);
        if (!dyeableItem.hasColor(itemStack)) {
            return ActionResult.PASS;
        }
        if (!world.isClient) {
            dyeableItem.removeColor(itemStack);
            playerEntity.incrementStat(Stats.CLEAN_ARMOR);
            WaterCauldronBlock.subtractWaterLevel(blockState, world, blockPos);
        }
        return ActionResult.success(world.isClient);
    };

    public static Object2ObjectOpenHashMap<Item, CauldronBehavior> createMap() {
        return Util.make(new Object2ObjectOpenHashMap(), object2ObjectOpenHashMap -> object2ObjectOpenHashMap.defaultReturnValue((blockState, world, blockPos, playerEntity, hand, itemStack) -> ActionResult.PASS));
    }

    public ActionResult interact(BlockState var1, World var2, BlockPos var3, PlayerEntity var4, Hand var5, ItemStack var6);

    public static void registerBehavior() {
        EMPTY_CAULDRON_BEHAVIOR.put(Items.WATER_BUCKET, FILL_WITH_WATER);
        EMPTY_CAULDRON_BEHAVIOR.put(Items.LAVA_BUCKET, FILL_WITH_LAVA);
        EMPTY_CAULDRON_BEHAVIOR.put(Items.POTION, (blockState, world, blockPos, playerEntity, hand, itemStack) -> {
            if (PotionUtil.getPotion(itemStack) != Potions.WATER) {
                return ActionResult.PASS;
            }
            if (!world.isClient) {
                playerEntity.setStackInHand(hand, ItemUsage.method_30012(itemStack, playerEntity, new ItemStack(Items.GLASS_BOTTLE)));
                playerEntity.incrementStat(Stats.USE_CAULDRON);
                world.setBlockState(blockPos, Blocks.WATER_CAULDRON.getDefaultState());
                world.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.success(world.isClient);
        });
        WATER_CAULDRON_BEHAVIOR.put(Items.LAVA_BUCKET, FILL_WITH_LAVA);
        WATER_CAULDRON_BEHAVIOR.put(Items.WATER_BUCKET, FILL_WITH_WATER);
        WATER_CAULDRON_BEHAVIOR.put(Items.BUCKET, (blockState2, world, blockPos, playerEntity, hand, itemStack) -> CauldronBehavior.emptyCauldron(blockState2, world, blockPos, playerEntity, hand, itemStack, new ItemStack(Items.WATER_BUCKET), blockState -> blockState.get(WaterCauldronBlock.LEVEL) == 3));
        WATER_CAULDRON_BEHAVIOR.put(Items.GLASS_BOTTLE, (blockState, world, blockPos, playerEntity, hand, itemStack) -> {
            if (!world.isClient) {
                playerEntity.setStackInHand(hand, ItemUsage.method_30012(itemStack, playerEntity, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
                playerEntity.incrementStat(Stats.USE_CAULDRON);
                WaterCauldronBlock.subtractWaterLevel(blockState, world, blockPos);
                world.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.success(world.isClient);
        });
        WATER_CAULDRON_BEHAVIOR.put(Items.POTION, (blockState, world, blockPos, playerEntity, hand, itemStack) -> {
            if (blockState.get(WaterCauldronBlock.LEVEL) == 3 || PotionUtil.getPotion(itemStack) != Potions.WATER) {
                return ActionResult.PASS;
            }
            if (!world.isClient) {
                playerEntity.setStackInHand(hand, ItemUsage.method_30012(itemStack, playerEntity, new ItemStack(Items.GLASS_BOTTLE)));
                playerEntity.incrementStat(Stats.USE_CAULDRON);
                world.setBlockState(blockPos, (BlockState)blockState.cycle(WaterCauldronBlock.LEVEL));
                world.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.success(world.isClient);
        });
        WATER_CAULDRON_BEHAVIOR.put(Items.LEATHER_BOOTS, CLEAN_DYEABLE_ITEM);
        WATER_CAULDRON_BEHAVIOR.put(Items.LEATHER_LEGGINGS, CLEAN_DYEABLE_ITEM);
        WATER_CAULDRON_BEHAVIOR.put(Items.LEATHER_CHESTPLATE, CLEAN_DYEABLE_ITEM);
        WATER_CAULDRON_BEHAVIOR.put(Items.LEATHER_HELMET, CLEAN_DYEABLE_ITEM);
        WATER_CAULDRON_BEHAVIOR.put(Items.LEATHER_HORSE_ARMOR, CLEAN_DYEABLE_ITEM);
        WATER_CAULDRON_BEHAVIOR.put(Items.WHITE_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.GRAY_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.BLACK_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.BLUE_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.BROWN_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.CYAN_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.GREEN_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.LIGHT_BLUE_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.LIGHT_GRAY_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.LIME_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.MAGENTA_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.ORANGE_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.PINK_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.PURPLE_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.RED_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.YELLOW_BANNER, CLEAN_BANNER);
        WATER_CAULDRON_BEHAVIOR.put(Items.WHITE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.GRAY_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.BLACK_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.BLUE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.BROWN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.CYAN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.GREEN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.LIGHT_BLUE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.LIGHT_GRAY_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.LIME_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.MAGENTA_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.ORANGE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.PINK_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.PURPLE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.RED_SHULKER_BOX, CLEAN_SHULKER_BOX);
        WATER_CAULDRON_BEHAVIOR.put(Items.YELLOW_SHULKER_BOX, CLEAN_SHULKER_BOX);
        LAVA_CAULDRON_BEHAVIOR.put(Items.BUCKET, (blockState2, world, blockPos, playerEntity, hand, itemStack) -> CauldronBehavior.emptyCauldron(blockState2, world, blockPos, playerEntity, hand, itemStack, new ItemStack(Items.LAVA_BUCKET), blockState -> true));
        LAVA_CAULDRON_BEHAVIOR.put(Items.WATER_BUCKET, FILL_WITH_WATER);
    }

    public static ActionResult emptyCauldron(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, ItemStack output, Predicate<BlockState> predicate) {
        if (!predicate.test(state)) {
            return ActionResult.PASS;
        }
        if (!world.isClient) {
            player.setStackInHand(hand, ItemUsage.method_30012(stack, player, output));
            player.incrementStat(Stats.USE_CAULDRON);
            world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return ActionResult.success(world.isClient);
    }

    public static ActionResult fillCauldron(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state) {
        if (!world.isClient) {
            player.setStackInHand(hand, ItemUsage.method_30012(stack, player, new ItemStack(Items.BUCKET)));
            player.incrementStat(Stats.FILL_CAULDRON);
            world.setBlockState(pos, state);
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return ActionResult.success(world.isClient);
    }
}

