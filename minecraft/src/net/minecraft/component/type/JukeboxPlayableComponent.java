package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryPair;
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

public record JukeboxPlayableComponent(RegistryPair<JukeboxSong> song, boolean showInTooltip) implements TooltipAppender {
	public static final Codec<JukeboxPlayableComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryPair.createCodec(RegistryKeys.JUKEBOX_SONG, JukeboxSong.ENTRY_CODEC).fieldOf("song").forGetter(JukeboxPlayableComponent::song),
					Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(JukeboxPlayableComponent::showInTooltip)
				)
				.apply(instance, JukeboxPlayableComponent::new)
	);
	public static final PacketCodec<RegistryByteBuf, JukeboxPlayableComponent> PACKET_CODEC = PacketCodec.tuple(
		RegistryPair.createPacketCodec(RegistryKeys.JUKEBOX_SONG, JukeboxSong.ENTRY_PACKET_CODEC),
		JukeboxPlayableComponent::song,
		PacketCodecs.BOOL,
		JukeboxPlayableComponent::showInTooltip,
		JukeboxPlayableComponent::new
	);

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
		RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();
		if (this.showInTooltip && wrapperLookup != null) {
			this.song.getEntry(wrapperLookup).ifPresent(registryEntry -> {
				MutableText mutableText = ((JukeboxSong)registryEntry.value()).description().copy();
				Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
				tooltip.accept(mutableText);
			});
		}
	}

	public JukeboxPlayableComponent withShowInTooltip(boolean showInTooltip) {
		return new JukeboxPlayableComponent(this.song, showInTooltip);
	}

	public static ItemActionResult tryPlayStack(World world, BlockPos pos, ItemStack stack, PlayerEntity player) {
		JukeboxPlayableComponent jukeboxPlayableComponent = stack.get(DataComponentTypes.JUKEBOX_PLAYABLE);
		if (jukeboxPlayableComponent == null) {
			return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		} else {
			BlockState blockState = world.getBlockState(pos);
			if (blockState.isOf(Blocks.JUKEBOX) && !(Boolean)blockState.get(JukeboxBlock.HAS_RECORD)) {
				if (!world.isClient) {
					ItemStack itemStack = stack.splitUnlessCreative(1, player);
					if (world.getBlockEntity(pos) instanceof JukeboxBlockEntity jukeboxBlockEntity) {
						jukeboxBlockEntity.setStack(itemStack);
						world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockState));
					}

					player.incrementStat(Stats.PLAY_RECORD);
				}

				return ItemActionResult.success(world.isClient);
			} else {
				return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
		}
	}
}
