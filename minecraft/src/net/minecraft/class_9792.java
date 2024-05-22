package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public record class_9792(class_9791<class_9793> song, boolean showInTooltip) implements TooltipAppender {
	public static final Codec<class_9792> field_52025 = RecordCodecBuilder.create(
		instance -> instance.group(
					class_9791.method_60736(RegistryKeys.JUKEBOX_SONG, class_9793.field_52029).fieldOf("song").forGetter(class_9792::song),
					Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(class_9792::showInTooltip)
				)
				.apply(instance, class_9792::new)
	);
	public static final PacketCodec<RegistryByteBuf, class_9792> field_52026 = PacketCodec.tuple(
		class_9791.method_60737(RegistryKeys.JUKEBOX_SONG, class_9793.field_52030), class_9792::song, PacketCodecs.BOOL, class_9792::showInTooltip, class_9792::new
	);

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
		RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();
		if (this.showInTooltip && wrapperLookup != null) {
			this.song.method_60739(wrapperLookup).ifPresent(registryEntry -> {
				MutableText mutableText = ((class_9793)registryEntry.value()).description().copy();
				Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
				tooltip.accept(mutableText);
			});
		}
	}

	public class_9792 method_60749(boolean bl) {
		return new class_9792(this.song, bl);
	}

	public static ItemActionResult method_60747(World world, BlockPos blockPos, ItemStack itemStack, PlayerEntity playerEntity) {
		class_9792 lv = itemStack.get(DataComponentTypes.JUKEBOX_PLAYABLE);
		if (lv == null) {
			return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.isOf(Blocks.JUKEBOX) && !(Boolean)blockState.get(JukeboxBlock.HAS_RECORD)) {
				if (!world.isClient) {
					ItemStack itemStack2 = itemStack.splitUnlessCreative(1, playerEntity);
					if (world.getBlockEntity(blockPos) instanceof JukeboxBlockEntity jukeboxBlockEntity) {
						jukeboxBlockEntity.setStack(itemStack2);
						world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, blockState));
					}

					playerEntity.incrementStat(Stats.PLAY_RECORD);
				}

				return ItemActionResult.success(world.isClient);
			} else {
				return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
		}
	}
}
