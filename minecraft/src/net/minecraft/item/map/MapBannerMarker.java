package net.minecraft.item.map;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class MapBannerMarker {
	private final BlockPos pos;
	private final DyeColor color;
	@Nullable
	private final Text name;

	public MapBannerMarker(BlockPos pos, DyeColor dyeColor, @Nullable Text name) {
		this.pos = pos;
		this.color = dyeColor;
		this.name = name;
	}

	public static MapBannerMarker fromNbt(CompoundTag tag) {
		BlockPos blockPos = NbtHelper.toBlockPos(tag.getCompound("Pos"));
		DyeColor dyeColor = DyeColor.byName(tag.getString("Color"), DyeColor.WHITE);
		Text text = tag.contains("Name") ? Text.Serializer.fromJson(tag.getString("Name")) : null;
		return new MapBannerMarker(blockPos, dyeColor, text);
	}

	@Nullable
	public static MapBannerMarker fromWorldBlock(BlockView blockView, BlockPos blockPos) {
		BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
		if (blockEntity instanceof BannerBlockEntity) {
			BannerBlockEntity bannerBlockEntity = (BannerBlockEntity)blockEntity;
			DyeColor dyeColor = bannerBlockEntity.getColorForState(() -> blockView.getBlockState(blockPos));
			Text text = bannerBlockEntity.hasCustomName() ? bannerBlockEntity.getCustomName() : null;
			return new MapBannerMarker(blockPos, dyeColor, text);
		} else {
			return null;
		}
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public MapIcon.Type getIconType() {
		switch (this.color) {
			case WHITE:
				return MapIcon.Type.BANNER_WHITE;
			case ORANGE:
				return MapIcon.Type.BANNER_ORANGE;
			case MAGENTA:
				return MapIcon.Type.BANNER_MAGENTA;
			case LIGHT_BLUE:
				return MapIcon.Type.BANNER_LIGHT_BLUE;
			case YELLOW:
				return MapIcon.Type.BANNER_YELLOW;
			case LIME:
				return MapIcon.Type.BANNER_LIME;
			case PINK:
				return MapIcon.Type.BANNER_PINK;
			case GRAY:
				return MapIcon.Type.BANNER_GRAY;
			case LIGHT_GRAY:
				return MapIcon.Type.BANNER_LIGHT_GRAY;
			case CYAN:
				return MapIcon.Type.BANNER_CYAN;
			case PURPLE:
				return MapIcon.Type.BANNER_PURPLE;
			case BLUE:
				return MapIcon.Type.BANNER_BLUE;
			case BROWN:
				return MapIcon.Type.BANNER_BROWN;
			case GREEN:
				return MapIcon.Type.BANNER_GREEN;
			case RED:
				return MapIcon.Type.BANNER_RED;
			case BLACK:
			default:
				return MapIcon.Type.BANNER_BLACK;
		}
	}

	@Nullable
	public Text getName() {
		return this.name;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			MapBannerMarker mapBannerMarker = (MapBannerMarker)o;
			return Objects.equals(this.pos, mapBannerMarker.pos) && this.color == mapBannerMarker.color && Objects.equals(this.name, mapBannerMarker.name);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.pos, this.color, this.name});
	}

	public CompoundTag getNbt() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.put("Pos", NbtHelper.fromBlockPos(this.pos));
		compoundTag.putString("Color", this.color.getName());
		if (this.name != null) {
			compoundTag.putString("Name", Text.Serializer.toJson(this.name));
		}

		return compoundTag;
	}

	public String getKey() {
		return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
	}
}
