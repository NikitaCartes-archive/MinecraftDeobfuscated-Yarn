package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MusicDiscItem extends Item {
	private static final Map<SoundEvent, MusicDiscItem> MUSIC_DISCS = Maps.<SoundEvent, MusicDiscItem>newHashMap();
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
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == Blocks.JUKEBOX && !(Boolean)blockState.get(JukeboxBlock.HAS_RECORD)) {
			ItemStack itemStack = context.getStack();
			if (!world.isClient) {
				((JukeboxBlock)Blocks.JUKEBOX).setRecord(world, blockPos, blockState, itemStack);
				world.playLevelEvent(null, 1010, blockPos, Item.getRawId(this));
				itemStack.decrement(1);
				PlayerEntity playerEntity = context.getPlayer();
				if (playerEntity != null) {
					playerEntity.incrementStat(Stats.PLAY_RECORD);
				}
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	public int getComparatorOutput() {
		return this.comparatorOutput;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(this.getDescription().formatted(Formatting.GRAY));
	}

	@Environment(EnvType.CLIENT)
	public MutableText getDescription() {
		return new TranslatableText(this.getTranslationKey() + ".desc");
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static MusicDiscItem bySound(SoundEvent sound) {
		return (MusicDiscItem)MUSIC_DISCS.get(sound);
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent getSound() {
		return this.sound;
	}
}
