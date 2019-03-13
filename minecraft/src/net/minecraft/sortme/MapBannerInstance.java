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
	private final BlockPos field_69;
	private final DyeColor color;
	@Nullable
	private final TextComponent field_67;

	public MapBannerInstance(BlockPos blockPos, DyeColor dyeColor, @Nullable TextComponent textComponent) {
		this.field_69 = blockPos;
		this.color = dyeColor;
		this.field_67 = textComponent;
	}

	public static MapBannerInstance method_67(CompoundTag compoundTag) {
		BlockPos blockPos = TagHelper.deserializeBlockPos(compoundTag.getCompound("Pos"));
		DyeColor dyeColor = DyeColor.byName(compoundTag.getString("Color"), DyeColor.field_7952);
		TextComponent textComponent = compoundTag.containsKey("Name") ? TextComponent.Serializer.fromJsonString(compoundTag.getString("Name")) : null;
		return new MapBannerInstance(blockPos, dyeColor, textComponent);
	}

	@Nullable
	public static MapBannerInstance method_73(BlockView blockView, BlockPos blockPos) {
		BlockEntity blockEntity = blockView.method_8321(blockPos);
		if (blockEntity instanceof BannerBlockEntity) {
			BannerBlockEntity bannerBlockEntity = (BannerBlockEntity)blockEntity;
			DyeColor dyeColor = bannerBlockEntity.getColorForState(() -> blockView.method_8320(blockPos));
			TextComponent textComponent = bannerBlockEntity.hasCustomName() ? bannerBlockEntity.method_5797() : null;
			return new MapBannerInstance(blockPos, dyeColor, textComponent);
		} else {
			return null;
		}
	}

	public BlockPos method_70() {
		return this.field_69;
	}

	public MapIcon.Type method_72() {
		switch (this.color) {
			case field_7952:
				return MapIcon.Type.field_96;
			case field_7946:
				return MapIcon.Type.field_92;
			case field_7958:
				return MapIcon.Type.field_97;
			case field_7951:
				return MapIcon.Type.field_90;
			case field_7947:
				return MapIcon.Type.field_93;
			case field_7961:
				return MapIcon.Type.field_94;
			case field_7954:
				return MapIcon.Type.field_100;
			case field_7944:
				return MapIcon.Type.field_101;
			case field_7967:
				return MapIcon.Type.field_107;
			case field_7955:
				return MapIcon.Type.field_108;
			case field_7945:
				return MapIcon.Type.field_104;
			case field_7966:
				return MapIcon.Type.field_105;
			case field_7957:
				return MapIcon.Type.field_106;
			case field_7942:
				return MapIcon.Type.field_102;
			case field_7964:
				return MapIcon.Type.field_99;
			case BLACK:
			default:
				return MapIcon.Type.field_103;
		}
	}

	@Nullable
	public TextComponent method_68() {
		return this.field_67;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			MapBannerInstance mapBannerInstance = (MapBannerInstance)object;
			return Objects.equals(this.field_69, mapBannerInstance.field_69)
				&& this.color == mapBannerInstance.color
				&& Objects.equals(this.field_67, mapBannerInstance.field_67);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_69, this.color, this.field_67});
	}

	public CompoundTag method_74() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.method_10566("Pos", TagHelper.serializeBlockPos(this.field_69));
		compoundTag.putString("Color", this.color.getName());
		if (this.field_67 != null) {
			compoundTag.putString("Name", TextComponent.Serializer.toJsonString(this.field_67));
		}

		return compoundTag;
	}

	public String method_71() {
		return "banner-" + this.field_69.getX() + "," + this.field_69.getY() + "," + this.field_69.getZ();
	}
}
