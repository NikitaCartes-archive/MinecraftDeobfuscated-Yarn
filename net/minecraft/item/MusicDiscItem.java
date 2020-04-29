/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MusicDiscItem
extends Item {
    private static final Map<SoundEvent, MusicDiscItem> MUSIC_DISCS = Maps.newHashMap();
    private final int comparatorOutput;
    private final SoundEvent sound;

    protected MusicDiscItem(int comparatorOutput, SoundEvent sound, Item.Settings settings) {
        super(settings);
        this.comparatorOutput = comparatorOutput;
        this.sound = sound;
        MUSIC_DISCS.put(this.sound, this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos;
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos = context.getBlockPos());
        if (!blockState.isOf(Blocks.JUKEBOX) || blockState.get(JukeboxBlock.HAS_RECORD).booleanValue()) {
            return ActionResult.PASS;
        }
        ItemStack itemStack = context.getStack();
        if (!world.isClient) {
            ((JukeboxBlock)Blocks.JUKEBOX).setRecord(world, blockPos, blockState, itemStack);
            world.syncWorldEvent(null, 1010, blockPos, Item.getRawId(this));
            itemStack.decrement(1);
            PlayerEntity playerEntity = context.getPlayer();
            if (playerEntity != null) {
                playerEntity.incrementStat(Stats.PLAY_RECORD);
            }
        }
        return ActionResult.SUCCESS;
    }

    public int getComparatorOutput() {
        return this.comparatorOutput;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(this.getDescription().formatted(Formatting.GRAY));
    }

    @Environment(value=EnvType.CLIENT)
    public MutableText getDescription() {
        return new TranslatableText(this.getTranslationKey() + ".desc");
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static MusicDiscItem bySound(SoundEvent sound) {
        return MUSIC_DISCS.get(sound);
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
    }
}

