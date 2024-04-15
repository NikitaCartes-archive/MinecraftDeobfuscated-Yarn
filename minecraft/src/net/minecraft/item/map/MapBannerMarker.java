package net.minecraft.item.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

/**
 * Represents a banner marker in world.
 * <p>
 * Used to track banners in a map state.
 */
public record MapBannerMarker(BlockPos pos, DyeColor color, Optional<Text> name) {
	public static final Codec<MapBannerMarker> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockPos.CODEC.fieldOf("pos").forGetter(MapBannerMarker::pos),
					DyeColor.CODEC.lenientOptionalFieldOf("color", DyeColor.WHITE).forGetter(MapBannerMarker::color),
					TextCodecs.STRINGIFIED_CODEC.lenientOptionalFieldOf("name").forGetter(MapBannerMarker::name)
				)
				.apply(instance, MapBannerMarker::new)
	);
	public static final Codec<List<MapBannerMarker>> LIST_CODEC = CODEC.listOf();

	@Nullable
	public static MapBannerMarker fromWorldBlock(BlockView blockView, BlockPos blockPos) {
		if (blockView.getBlockEntity(blockPos) instanceof BannerBlockEntity bannerBlockEntity) {
			DyeColor dyeColor = bannerBlockEntity.getColorForState();
			Optional<Text> optional = Optional.ofNullable(bannerBlockEntity.getCustomName());
			return new MapBannerMarker(blockPos, dyeColor, optional);
		} else {
			return null;
		}
	}

	public RegistryEntry<MapDecorationType> getDecorationType() {
		return switch (this.color) {
			case WHITE -> MapDecorationTypes.BANNER_WHITE;
			case ORANGE -> MapDecorationTypes.BANNER_ORANGE;
			case MAGENTA -> MapDecorationTypes.BANNER_MAGENTA;
			case LIGHT_BLUE -> MapDecorationTypes.BANNER_LIGHT_BLUE;
			case YELLOW -> MapDecorationTypes.BANNER_YELLOW;
			case LIME -> MapDecorationTypes.BANNER_LIME;
			case PINK -> MapDecorationTypes.BANNER_PINK;
			case GRAY -> MapDecorationTypes.BANNER_GRAY;
			case LIGHT_GRAY -> MapDecorationTypes.BANNER_LIGHT_GRAY;
			case CYAN -> MapDecorationTypes.BANNER_CYAN;
			case PURPLE -> MapDecorationTypes.BANNER_PURPLE;
			case BLUE -> MapDecorationTypes.BANNER_BLUE;
			case BROWN -> MapDecorationTypes.BANNER_BROWN;
			case GREEN -> MapDecorationTypes.BANNER_GREEN;
			case RED -> MapDecorationTypes.BANNER_RED;
			case BLACK -> MapDecorationTypes.BANNER_BLACK;
			default -> throw new MatchException(null, null);
		};
	}

	public String getKey() {
		return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
	}
}
