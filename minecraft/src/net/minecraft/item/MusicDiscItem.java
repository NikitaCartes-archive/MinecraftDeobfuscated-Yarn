package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class MusicDiscItem extends Item {
	private static final Map<SoundEvent, MusicDiscItem> MUSIC_DISCS = Maps.<SoundEvent, MusicDiscItem>newHashMap();
	private final int comparatorOutput;
	private final SoundEvent sound;
	private final int lengthInTicks;

	protected MusicDiscItem(int comparatorOutput, SoundEvent sound, Item.Settings settings, int lengthInSeconds) {
		super(settings);
		this.comparatorOutput = comparatorOutput;
		this.sound = sound;
		this.lengthInTicks = lengthInSeconds * 20;
		MUSIC_DISCS.put(this.sound, this);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.isOf(Blocks.JUKEBOX) && !(Boolean)blockState.get(JukeboxBlock.HAS_RECORD)) {
			ItemStack itemStack = context.getStack();
			if (!world.isClient) {
				PlayerEntity playerEntity = context.getPlayer();
				if (world.getBlockEntity(blockPos) instanceof JukeboxBlockEntity jukeboxBlockEntity) {
					jukeboxBlockEntity.setStack(itemStack.copy());
					world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, blockState));
				}

				itemStack.decrement(1);
				if (playerEntity != null) {
					playerEntity.incrementStat(Stats.PLAY_RECORD);
				}
			}

			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.PASS;
		}
	}

	public int getComparatorOutput() {
		return this.comparatorOutput;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(this.getDescription().formatted(Formatting.GRAY));
	}

	public MutableText getDescription() {
		return Text.translatable(this.getTranslationKey() + ".desc");
	}

	@Nullable
	public static MusicDiscItem bySound(SoundEvent sound) {
		return (MusicDiscItem)MUSIC_DISCS.get(sound);
	}

	public SoundEvent getSound() {
		return this.sound;
	}

	public int getSongLengthInTicks() {
		return this.lengthInTicks;
	}
}
