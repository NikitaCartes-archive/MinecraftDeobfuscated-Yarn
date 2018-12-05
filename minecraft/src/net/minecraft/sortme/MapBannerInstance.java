package net.minecraft.sortme;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.map.MapIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class MapBannerInstance {
	private final BlockPos pos;
	private final DyeColor color;
	@Nullable
	private final TextComponent text;

	public MapBannerInstance(BlockPos blockPos, DyeColor dyeColor, @Nullable TextComponent textComponent) {
		this.pos = blockPos;
		this.color = dyeColor;
		this.text = textComponent;
	}

	public static MapBannerInstance fromNbt(CompoundTag compoundTag) {
		BlockPos blockPos = TagHelper.deserializeBlockPos(compoundTag.getCompound("Pos"));
		DyeColor dyeColor = DyeColor.byName(compoundTag.getString("Color"), DyeColor.WHITE);
		TextComponent textComponent = compoundTag.containsKey("Name") ? TextComponent.Serializer.fromJsonString(compoundTag.getString("Name")) : null;
		return new MapBannerInstance(blockPos, dyeColor, textComponent);
	}

	@Nullable
	public static MapBannerInstance fromWorldBlock(BlockView blockView, BlockPos blockPos) {
		BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
		if (blockEntity instanceof BannerBlockEntity) {
			BannerBlockEntity bannerBlockEntity = (BannerBlockEntity)blockEntity;
			DyeColor dyeColor = bannerBlockEntity.getColorForState(() -> blockView.getBlockState(blockPos));
			TextComponent textComponent = bannerBlockEntity.hasCustomName() ? bannerBlockEntity.getCustomName() : null;
			return new MapBannerInstance(blockPos, dyeColor, textComponent);
		} else {
			return null;
		}
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public MapIcon.Direction method_72() {
		switch (this.color) {
			case WHITE:
				return MapIcon.Direction.field_96;
			case ORANGE:
				return MapIcon.Direction.field_92;
			case MAGENTA:
				return MapIcon.Direction.field_97;
			case LIGHT_BLUE:
				return MapIcon.Direction.field_90;
			case YELLOW:
				return MapIcon.Direction.field_93;
			case LIME:
				return MapIcon.Direction.field_94;
			case PINK:
				return MapIcon.Direction.field_100;
			case GRAY:
				return MapIcon.Direction.field_101;
			case LIGHT_GRAY:
				return MapIcon.Direction.field_107;
			case CYAN:
				return MapIcon.Direction.field_108;
			case PURPLE:
				return MapIcon.Direction.field_104;
			case BLUE:
				return MapIcon.Direction.field_105;
			case BROWN:
				return MapIcon.Direction.field_106;
			case GREEN:
				return MapIcon.Direction.field_102;
			case RED:
				return MapIcon.Direction.field_99;
			case BLACK:
			default:
				return MapIcon.Direction.field_103;
		}
	}

	@Nullable
	public TextComponent getText() {
		return this.text;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			MapBannerInstance mapBannerInstance = (MapBannerInstance)object;
			return Objects.equals(this.pos, mapBannerInstance.pos) && this.color == mapBannerInstance.color && Objects.equals(this.text, mapBannerInstance.text);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.pos, this.color, this.text});
	}

	public CompoundTag getNbt() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.put("Pos", TagHelper.serializeBlockPos(this.pos));
		compoundTag.putString("Color", this.color.getName());
		if (this.text != null) {
			compoundTag.putString("Name", TextComponent.Serializer.toJsonString(this.text));
		}

		return compoundTag;
	}

	public String method_71() {
		return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
	}
}
