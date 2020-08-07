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
		DyeColor dyeColor = DyeColor.byName(tag.getString("Color"), DyeColor.field_7952);
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
			case field_7963:
			default:
				return MapIcon.Type.field_103;
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
