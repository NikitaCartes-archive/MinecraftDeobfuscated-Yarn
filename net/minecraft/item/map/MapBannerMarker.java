/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item.map;

import java.util.Objects;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.map.MapIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.DyeColor;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class MapBannerMarker {
    private final BlockPos pos;
    private final DyeColor color;
    @Nullable
    private final Component name;

    public MapBannerMarker(BlockPos blockPos, DyeColor dyeColor, @Nullable Component component) {
        this.pos = blockPos;
        this.color = dyeColor;
        this.name = component;
    }

    public static MapBannerMarker fromNbt(CompoundTag compoundTag) {
        BlockPos blockPos = TagHelper.deserializeBlockPos(compoundTag.getCompound("Pos"));
        DyeColor dyeColor = DyeColor.byName(compoundTag.getString("Color"), DyeColor.WHITE);
        Component component = compoundTag.containsKey("Name") ? Component.Serializer.fromJsonString(compoundTag.getString("Name")) : null;
        return new MapBannerMarker(blockPos, dyeColor, component);
    }

    @Nullable
    public static MapBannerMarker fromWorldBlock(BlockView blockView, BlockPos blockPos) {
        BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
        if (blockEntity instanceof BannerBlockEntity) {
            BannerBlockEntity bannerBlockEntity = (BannerBlockEntity)blockEntity;
            DyeColor dyeColor = bannerBlockEntity.getColorForState(() -> blockView.getBlockState(blockPos));
            Component component = bannerBlockEntity.hasCustomName() ? bannerBlockEntity.getCustomName() : null;
            return new MapBannerMarker(blockPos, dyeColor, component);
        }
        return null;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public MapIcon.Type getIconType() {
        switch (this.color) {
            case WHITE: {
                return MapIcon.Type.BANNER_WHITE;
            }
            case ORANGE: {
                return MapIcon.Type.BANNER_ORANGE;
            }
            case MAGENTA: {
                return MapIcon.Type.BANNER_MAGENTA;
            }
            case LIGHT_BLUE: {
                return MapIcon.Type.BANNER_LIGHT_BLUE;
            }
            case YELLOW: {
                return MapIcon.Type.BANNER_YELLOW;
            }
            case LIME: {
                return MapIcon.Type.BANNER_LIME;
            }
            case PINK: {
                return MapIcon.Type.BANNER_PINK;
            }
            case GRAY: {
                return MapIcon.Type.BANNER_GRAY;
            }
            case LIGHT_GRAY: {
                return MapIcon.Type.BANNER_LIGHT_GRAY;
            }
            case CYAN: {
                return MapIcon.Type.BANNER_CYAN;
            }
            case PURPLE: {
                return MapIcon.Type.BANNER_PURPLE;
            }
            case BLUE: {
                return MapIcon.Type.BANNER_BLUE;
            }
            case BROWN: {
                return MapIcon.Type.BANNER_BROWN;
            }
            case GREEN: {
                return MapIcon.Type.BANNER_GREEN;
            }
            case RED: {
                return MapIcon.Type.BANNER_RED;
            }
        }
        return MapIcon.Type.BANNER_BLACK;
    }

    @Nullable
    public Component getName() {
        return this.name;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        MapBannerMarker mapBannerMarker = (MapBannerMarker)object;
        return Objects.equals(this.pos, mapBannerMarker.pos) && this.color == mapBannerMarker.color && Objects.equals(this.name, mapBannerMarker.name);
    }

    public int hashCode() {
        return Objects.hash(this.pos, this.color, this.name);
    }

    public CompoundTag getNbt() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Pos", TagHelper.serializeBlockPos(this.pos));
        compoundTag.putString("Color", this.color.getName());
        if (this.name != null) {
            compoundTag.putString("Name", Component.Serializer.toJsonString(this.name));
        }
        return compoundTag;
    }

    public String getKey() {
        return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
    }
}

