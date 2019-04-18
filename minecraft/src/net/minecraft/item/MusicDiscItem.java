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
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MusicDiscItem extends Item {
	private static final Map<SoundEvent, MusicDiscItem> SOUND_ITEM_MAP = Maps.<SoundEvent, MusicDiscItem>newHashMap();
	private final int comparatorOutput;
	private final SoundEvent sound;

	protected MusicDiscItem(int i, SoundEvent soundEvent, Item.Settings settings) {
		super(settings);
		this.comparatorOutput = i;
		this.sound = soundEvent;
		SOUND_ITEM_MAP.put(this.sound, this);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == Blocks.field_10223 && !(Boolean)blockState.get(JukeboxBlock.HAS_RECORD)) {
			ItemStack itemStack = itemUsageContext.getItemStack();
			if (!world.isClient) {
				((JukeboxBlock)Blocks.field_10223).setRecord(world, blockPos, blockState, itemStack);
				world.playLevelEvent(null, 1010, blockPos, Item.getRawIdByItem(this));
				itemStack.subtractAmount(1);
				PlayerEntity playerEntity = itemUsageContext.getPlayer();
				if (playerEntity != null) {
					playerEntity.incrementStat(Stats.field_15375);
				}
			}

			return ActionResult.field_5812;
		} else {
			return ActionResult.PASS;
		}
	}

	public int getComparatorOutput() {
		return this.comparatorOutput;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		list.add(this.getDescription().applyFormat(TextFormat.field_1080));
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getDescription() {
		return new TranslatableTextComponent(this.getTranslationKey() + ".desc");
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static MusicDiscItem bySound(SoundEvent soundEvent) {
		return (MusicDiscItem)SOUND_ITEM_MAP.get(soundEvent);
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent getSound() {
		return this.sound;
	}
}
