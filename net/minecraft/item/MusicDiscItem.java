/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MusicDiscItem
extends Item {
    private static final Map<SoundEvent, MusicDiscItem> MUSIC_DISCS = Maps.newHashMap();
    private final int comparatorOutput;
    private final SoundEvent sound;

    protected MusicDiscItem(int i, SoundEvent soundEvent, Item.Settings settings) {
        super(settings);
        this.comparatorOutput = i;
        this.sound = soundEvent;
        MUSIC_DISCS.put(this.sound, this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        BlockPos blockPos;
        World world = itemUsageContext.getWorld();
        BlockState blockState = world.getBlockState(blockPos = itemUsageContext.getBlockPos());
        if (blockState.getBlock() != Blocks.JUKEBOX || blockState.get(JukeboxBlock.HAS_RECORD).booleanValue()) {
            return ActionResult.PASS;
        }
        ItemStack itemStack = itemUsageContext.getStack();
        if (!world.isClient) {
            ((JukeboxBlock)Blocks.JUKEBOX).setRecord(world, blockPos, blockState, itemStack);
            world.playLevelEvent(null, 1010, blockPos, Item.getRawId(this));
            itemStack.decrement(1);
            PlayerEntity playerEntity = itemUsageContext.getPlayer();
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
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Component> list, TooltipContext tooltipContext) {
        list.add(this.getDescription().applyFormat(ChatFormat.GRAY));
    }

    @Environment(value=EnvType.CLIENT)
    public Component getDescription() {
        return new TranslatableComponent(this.getTranslationKey() + ".desc", new Object[0]);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static MusicDiscItem bySound(SoundEvent soundEvent) {
        return MUSIC_DISCS.get(soundEvent);
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
    }
}

