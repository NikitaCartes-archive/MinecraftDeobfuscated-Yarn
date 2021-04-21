/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WrittenBookItem
extends Item {
    public static final int field_30929 = 16;
    public static final int MAX_TITLE_LENGTH = 32;
    public static final int field_30931 = 1024;
    public static final int field_30932 = Short.MAX_VALUE;
    public static final int field_30933 = 100;
    public static final int field_30934 = 2;
    public static final String TITLE_KEY = "title";
    public static final String FILTERED_TITLE_KEY = "filtered_title";
    public static final String AUTHOR_KEY = "author";
    public static final String PAGES_KEY = "pages";
    public static final String FILTERED_PAGES_KEY = "filtered_pages";
    public static final String GENERATION_KEY = "generation";
    public static final String RESOLVED_KEY = "resolved";

    public WrittenBookItem(Item.Settings settings) {
        super(settings);
    }

    public static boolean isValid(@Nullable NbtCompound nbt) {
        if (!WritableBookItem.isValid(nbt)) {
            return false;
        }
        if (!nbt.contains(TITLE_KEY, 8)) {
            return false;
        }
        String string = nbt.getString(TITLE_KEY);
        if (string.length() > 32) {
            return false;
        }
        return nbt.contains(AUTHOR_KEY, 8);
    }

    public static int getGeneration(ItemStack stack) {
        return stack.getTag().getInt(GENERATION_KEY);
    }

    public static int getPageCount(ItemStack stack) {
        NbtCompound nbtCompound = stack.getTag();
        return nbtCompound != null ? nbtCompound.getList(PAGES_KEY, 8).size() : 0;
    }

    @Override
    public Text getName(ItemStack stack) {
        String string;
        NbtCompound nbtCompound = stack.getTag();
        if (nbtCompound != null && !ChatUtil.isEmpty(string = nbtCompound.getString(TITLE_KEY))) {
            return new LiteralText(string);
        }
        return super.getName(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasTag()) {
            NbtCompound nbtCompound = stack.getTag();
            String string = nbtCompound.getString(AUTHOR_KEY);
            if (!ChatUtil.isEmpty(string)) {
                tooltip.add(new TranslatableText("book.byAuthor", string).formatted(Formatting.GRAY));
            }
            tooltip.add(new TranslatableText("book.generation." + nbtCompound.getInt(GENERATION_KEY)).formatted(Formatting.GRAY));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos;
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos = context.getBlockPos());
        if (blockState.isOf(Blocks.LECTERN)) {
            return LecternBlock.putBookIfAbsent(context.getPlayer(), world, blockPos, blockState, context.getStack()) ? ActionResult.success(world.isClient) : ActionResult.PASS;
        }
        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.useBook(itemStack, hand);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(itemStack, world.isClient());
    }

    public static boolean resolve(ItemStack book, @Nullable ServerCommandSource commandSource, @Nullable PlayerEntity player) {
        NbtCompound nbtCompound = book.getTag();
        if (nbtCompound == null || nbtCompound.getBoolean(RESOLVED_KEY)) {
            return false;
        }
        nbtCompound.putBoolean(RESOLVED_KEY, true);
        if (!WrittenBookItem.isValid(nbtCompound)) {
            return false;
        }
        NbtList nbtList = nbtCompound.getList(PAGES_KEY, 8);
        for (int i = 0; i < nbtList.size(); ++i) {
            nbtList.set(i, NbtString.of(WrittenBookItem.method_33826(commandSource, player, nbtList.getString(i))));
        }
        if (nbtCompound.contains(FILTERED_PAGES_KEY, 10)) {
            NbtCompound nbtCompound2 = nbtCompound.getCompound(FILTERED_PAGES_KEY);
            for (String string : nbtCompound2.getKeys()) {
                nbtCompound2.putString(string, WrittenBookItem.method_33826(commandSource, player, nbtCompound2.getString(string)));
            }
        }
        return true;
    }

    private static String method_33826(@Nullable ServerCommandSource serverCommandSource, @Nullable PlayerEntity playerEntity, String string) {
        MutableText text;
        try {
            text = Text.Serializer.fromLenientJson(string);
            text = Texts.parse(serverCommandSource, text, (Entity)playerEntity, 0);
        } catch (Exception exception) {
            text = new LiteralText(string);
        }
        return Text.Serializer.toJson(text);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}

