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

public class RecordItem extends Item {
	private static final Map<SoundEvent, RecordItem> SOUND_ITEM_MAP = Maps.<SoundEvent, RecordItem>newHashMap();
	private final int field_8902;
	private final SoundEvent field_8900;

	protected RecordItem(int i, SoundEvent soundEvent, Item.Settings settings) {
		super(settings);
		this.field_8902 = i;
		this.field_8900 = soundEvent;
		SOUND_ITEM_MAP.put(this.field_8900, this);
	}

	@Override
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.method_8045();
		BlockPos blockPos = itemUsageContext.method_8037();
		BlockState blockState = world.method_8320(blockPos);
		if (blockState.getBlock() == Blocks.field_10223 && !(Boolean)blockState.method_11654(JukeboxBlock.field_11180)) {
			ItemStack itemStack = itemUsageContext.getItemStack();
			if (!world.isClient) {
				((JukeboxBlock)Blocks.field_10223).method_10276(world, blockPos, blockState, itemStack);
				world.method_8444(null, 1010, blockPos, Item.getRawIdByItem(this));
				itemStack.subtractAmount(1);
				PlayerEntity playerEntity = itemUsageContext.getPlayer();
				if (playerEntity != null) {
					playerEntity.method_7281(Stats.field_15375);
				}
			}

			return ActionResult.field_5812;
		} else {
			return ActionResult.PASS;
		}
	}

	public int method_8010() {
		return this.field_8902;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		list.add(this.method_8011().applyFormat(TextFormat.field_1080));
	}

	@Environment(EnvType.CLIENT)
	public TextComponent method_8011() {
		return new TranslatableTextComponent(this.getTranslationKey() + ".desc");
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static RecordItem method_8012(SoundEvent soundEvent) {
		return (RecordItem)SOUND_ITEM_MAP.get(soundEvent);
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent method_8009() {
		return this.field_8900;
	}
}
