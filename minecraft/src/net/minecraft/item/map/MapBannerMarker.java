package net.minecraft.item.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerBlockEntity;
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
					DyeColor.CODEC.optionalFieldOf("color", DyeColor.WHITE).forGetter(MapBannerMarker::color),
					TextCodecs.STRINGIFIED_CODEC.optionalFieldOf("name").forGetter(MapBannerMarker::name)
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

	public MapIcon.Type getIconType() {
		return switch (this.color) {
			case WHITE -> MapIcon.Type.BANNER_WHITE;
			case ORANGE -> MapIcon.Type.BANNER_ORANGE;
			case MAGENTA -> MapIcon.Type.BANNER_MAGENTA;
			case LIGHT_BLUE -> MapIcon.Type.BANNER_LIGHT_BLUE;
			case YELLOW -> MapIcon.Type.BANNER_YELLOW;
			case LIME -> MapIcon.Type.BANNER_LIME;
			case PINK -> MapIcon.Type.BANNER_PINK;
			case GRAY -> MapIcon.Type.BANNER_GRAY;
			case LIGHT_GRAY -> MapIcon.Type.BANNER_LIGHT_GRAY;
			case CYAN -> MapIcon.Type.BANNER_CYAN;
			case PURPLE -> MapIcon.Type.BANNER_PURPLE;
			case BLUE -> MapIcon.Type.BANNER_BLUE;
			case BROWN -> MapIcon.Type.BANNER_BROWN;
			case GREEN -> MapIcon.Type.BANNER_GREEN;
			case RED -> MapIcon.Type.BANNER_RED;
			default -> MapIcon.Type.BANNER_BLACK;
		};
	}

	public String getKey() {
		return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
	}
}
