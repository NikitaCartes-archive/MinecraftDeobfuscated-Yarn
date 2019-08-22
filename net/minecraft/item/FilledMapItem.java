/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multisets;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public class FilledMapItem
extends NetworkSyncedItem {
    public FilledMapItem(Item.Settings settings) {
        super(settings);
    }

    public static ItemStack createMap(World world, int i, int j, byte b, boolean bl, boolean bl2) {
        ItemStack itemStack = new ItemStack(Items.FILLED_MAP);
        FilledMapItem.createMapState(itemStack, world, i, j, b, bl, bl2, world.dimension.getType());
        return itemStack;
    }

    @Nullable
    public static MapState getMapState(ItemStack itemStack, World world) {
        return world.getMapState(FilledMapItem.getMapName(FilledMapItem.getMapId(itemStack)));
    }

    @Nullable
    public static MapState getOrCreateMapState(ItemStack itemStack, World world) {
        MapState mapState = FilledMapItem.getMapState(itemStack, world);
        if (mapState == null && !world.isClient) {
            mapState = FilledMapItem.createMapState(itemStack, world, world.getLevelProperties().getSpawnX(), world.getLevelProperties().getSpawnZ(), 3, false, false, world.dimension.getType());
        }
        return mapState;
    }

    public static int getMapId(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        return compoundTag != null && compoundTag.containsKey("map", 99) ? compoundTag.getInt("map") : 0;
    }

    private static MapState createMapState(ItemStack itemStack, World world, int i, int j, int k, boolean bl, boolean bl2, DimensionType dimensionType) {
        int l = world.getNextMapId();
        MapState mapState = new MapState(FilledMapItem.getMapName(l));
        mapState.init(i, j, k, bl, bl2, dimensionType);
        world.putMapState(mapState);
        itemStack.getOrCreateTag().putInt("map", l);
        return mapState;
    }

    public static String getMapName(int i) {
        return "map_" + i;
    }

    public void updateColors(World world, Entity entity, MapState mapState) {
        if (world.dimension.getType() != mapState.dimension || !(entity instanceof PlayerEntity)) {
            return;
        }
        int i = 1 << mapState.scale;
        int j = mapState.xCenter;
        int k = mapState.zCenter;
        int l = MathHelper.floor(entity.x - (double)j) / i + 64;
        int m = MathHelper.floor(entity.z - (double)k) / i + 64;
        int n = 128 / i;
        if (world.dimension.isNether()) {
            n /= 2;
        }
        MapState.PlayerUpdateTracker playerUpdateTracker = mapState.getPlayerSyncData((PlayerEntity)entity);
        ++playerUpdateTracker.field_131;
        boolean bl = false;
        for (int o = l - n + 1; o < l + n; ++o) {
            if ((o & 0xF) != (playerUpdateTracker.field_131 & 0xF) && !bl) continue;
            bl = false;
            double d = 0.0;
            for (int p = m - n - 1; p < m + n; ++p) {
                byte c;
                byte b;
                MaterialColor materialColor;
                int y;
                if (o < 0 || p < -1 || o >= 128 || p >= 128) continue;
                int q = o - l;
                int r = p - m;
                boolean bl2 = q * q + r * r > (n - 2) * (n - 2);
                int s = (j / i + o - 64) * i;
                int t = (k / i + p - 64) * i;
                LinkedHashMultiset<MaterialColor> multiset = LinkedHashMultiset.create();
                WorldChunk worldChunk = world.getWorldChunk(new BlockPos(s, 0, t));
                if (worldChunk.isEmpty()) continue;
                ChunkPos chunkPos = worldChunk.getPos();
                int u = s & 0xF;
                int v = t & 0xF;
                int w = 0;
                double e = 0.0;
                if (world.dimension.isNether()) {
                    int x = s + t * 231871;
                    if (((x = x * x * 31287121 + x * 11) >> 20 & 1) == 0) {
                        multiset.add(Blocks.DIRT.getDefaultState().getTopMaterialColor(world, BlockPos.ORIGIN), 10);
                    } else {
                        multiset.add(Blocks.STONE.getDefaultState().getTopMaterialColor(world, BlockPos.ORIGIN), 100);
                    }
                    e = 100.0;
                } else {
                    BlockPos.Mutable mutable = new BlockPos.Mutable();
                    BlockPos.Mutable mutable2 = new BlockPos.Mutable();
                    for (y = 0; y < i; ++y) {
                        for (int z = 0; z < i; ++z) {
                            BlockState blockState;
                            int aa = worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, y + u, z + v) + 1;
                            if (aa > 1) {
                                do {
                                    mutable.set(chunkPos.getStartX() + y + u, --aa, chunkPos.getStartZ() + z + v);
                                } while ((blockState = worldChunk.getBlockState(mutable)).getTopMaterialColor(world, mutable) == MaterialColor.AIR && aa > 0);
                                if (aa > 0 && !blockState.getFluidState().isEmpty()) {
                                    BlockState blockState2;
                                    int ab = aa - 1;
                                    mutable2.set(mutable);
                                    do {
                                        mutable2.setY(ab--);
                                        blockState2 = worldChunk.getBlockState(mutable2);
                                        ++w;
                                    } while (ab > 0 && !blockState2.getFluidState().isEmpty());
                                    blockState = this.getFluidStateIfVisible(world, blockState, mutable);
                                }
                            } else {
                                blockState = Blocks.BEDROCK.getDefaultState();
                            }
                            mapState.removeBanner(world, chunkPos.getStartX() + y + u, chunkPos.getStartZ() + z + v);
                            e += (double)aa / (double)(i * i);
                            multiset.add(blockState.getTopMaterialColor(world, mutable));
                        }
                    }
                }
                w /= i * i;
                double f = (e - d) * 4.0 / (double)(i + 4) + ((double)(o + p & 1) - 0.5) * 0.4;
                y = 1;
                if (f > 0.6) {
                    y = 2;
                }
                if (f < -0.6) {
                    y = 0;
                }
                if ((materialColor = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MaterialColor.AIR)) == MaterialColor.WATER) {
                    f = (double)w * 0.1 + (double)(o + p & 1) * 0.2;
                    y = 1;
                    if (f < 0.5) {
                        y = 2;
                    }
                    if (f > 0.9) {
                        y = 0;
                    }
                }
                d = e;
                if (p < 0 || q * q + r * r >= n * n || bl2 && (o + p & 1) == 0 || (b = mapState.colors[o + p * 128]) == (c = (byte)(materialColor.id * 4 + y))) continue;
                mapState.colors[o + p * 128] = c;
                mapState.markDirty(o, p);
                bl = true;
            }
        }
    }

    private BlockState getFluidStateIfVisible(World world, BlockState blockState, BlockPos blockPos) {
        FluidState fluidState = blockState.getFluidState();
        if (!fluidState.isEmpty() && !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
            return fluidState.getBlockState();
        }
        return blockState;
    }

    private static boolean hasPositiveDepth(Biome[] biomes, int i, int j, int k) {
        return biomes[j * i + k * i * 128 * i].getDepth() >= 0.0f;
    }

    public static void fillExplorationMap(World world, ItemStack itemStack) {
        MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, world);
        if (mapState == null) {
            return;
        }
        if (world.dimension.getType() != mapState.dimension) {
            return;
        }
        int i = 1 << mapState.scale;
        int j = mapState.xCenter;
        int k = mapState.zCenter;
        Biome[] biomes = world.getChunkManager().getChunkGenerator().getBiomeSource().sampleBiomes((j / i - 64) * i, (k / i - 64) * i, 128 * i, 128 * i, false);
        for (int l = 0; l < 128; ++l) {
            for (int m = 0; m < 128; ++m) {
                if (l <= 0 || m <= 0 || l >= 127 || m >= 127) continue;
                Biome biome = biomes[l * i + m * i * 128 * i];
                int n = 8;
                if (FilledMapItem.hasPositiveDepth(biomes, i, l - 1, m - 1)) {
                    --n;
                }
                if (FilledMapItem.hasPositiveDepth(biomes, i, l - 1, m + 1)) {
                    --n;
                }
                if (FilledMapItem.hasPositiveDepth(biomes, i, l - 1, m)) {
                    --n;
                }
                if (FilledMapItem.hasPositiveDepth(biomes, i, l + 1, m - 1)) {
                    --n;
                }
                if (FilledMapItem.hasPositiveDepth(biomes, i, l + 1, m + 1)) {
                    --n;
                }
                if (FilledMapItem.hasPositiveDepth(biomes, i, l + 1, m)) {
                    --n;
                }
                if (FilledMapItem.hasPositiveDepth(biomes, i, l, m - 1)) {
                    --n;
                }
                if (FilledMapItem.hasPositiveDepth(biomes, i, l, m + 1)) {
                    --n;
                }
                int o = 3;
                MaterialColor materialColor = MaterialColor.AIR;
                if (biome.getDepth() < 0.0f) {
                    materialColor = MaterialColor.ORANGE;
                    if (n > 7 && m % 2 == 0) {
                        o = (l + (int)(MathHelper.sin((float)m + 0.0f) * 7.0f)) / 8 % 5;
                        if (o == 3) {
                            o = 1;
                        } else if (o == 4) {
                            o = 0;
                        }
                    } else if (n > 7) {
                        materialColor = MaterialColor.AIR;
                    } else if (n > 5) {
                        o = 1;
                    } else if (n > 3) {
                        o = 0;
                    } else if (n > 1) {
                        o = 0;
                    }
                } else if (n > 0) {
                    materialColor = MaterialColor.BROWN;
                    o = n > 3 ? 1 : 3;
                }
                if (materialColor == MaterialColor.AIR) continue;
                mapState.colors[l + m * 128] = (byte)(materialColor.id * 4 + o);
                mapState.markDirty(l, m);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int i, boolean bl) {
        if (world.isClient) {
            return;
        }
        MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, world);
        if (mapState == null) {
            return;
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            mapState.update(playerEntity, itemStack);
        }
        if (!mapState.locked && (bl || entity instanceof PlayerEntity && ((PlayerEntity)entity).getOffHandStack() == itemStack)) {
            this.updateColors(world, entity, mapState);
        }
    }

    @Override
    @Nullable
    public Packet<?> createSyncPacket(ItemStack itemStack, World world, PlayerEntity playerEntity) {
        return FilledMapItem.getOrCreateMapState(itemStack, world).getPlayerMarkerPacket(itemStack, world, playerEntity);
    }

    @Override
    public void onCraft(ItemStack itemStack, World world, PlayerEntity playerEntity) {
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null && compoundTag.containsKey("map_scale_direction", 99)) {
            FilledMapItem.scale(itemStack, world, compoundTag.getInt("map_scale_direction"));
            compoundTag.remove("map_scale_direction");
        }
    }

    protected static void scale(ItemStack itemStack, World world, int i) {
        MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, world);
        if (mapState != null) {
            FilledMapItem.createMapState(itemStack, world, mapState.xCenter, mapState.zCenter, MathHelper.clamp(mapState.scale + i, 0, 4), mapState.showIcons, mapState.unlimitedTracking, mapState.dimension);
        }
    }

    @Nullable
    public static ItemStack copyMap(World world, ItemStack itemStack) {
        MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, world);
        if (mapState != null) {
            ItemStack itemStack2 = itemStack.copy();
            MapState mapState2 = FilledMapItem.createMapState(itemStack2, world, 0, 0, mapState.scale, mapState.showIcons, mapState.unlimitedTracking, mapState.dimension);
            mapState2.copyFrom(mapState);
            return itemStack2;
        }
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
        MapState mapState;
        MapState mapState2 = mapState = world == null ? null : FilledMapItem.getOrCreateMapState(itemStack, world);
        if (mapState != null && mapState.locked) {
            list.add(new TranslatableText("filled_map.locked", FilledMapItem.getMapId(itemStack)).formatted(Formatting.GRAY));
        }
        if (tooltipContext.isAdvanced()) {
            if (mapState != null) {
                list.add(new TranslatableText("filled_map.id", FilledMapItem.getMapId(itemStack)).formatted(Formatting.GRAY));
                list.add(new TranslatableText("filled_map.scale", 1 << mapState.scale).formatted(Formatting.GRAY));
                list.add(new TranslatableText("filled_map.level", mapState.scale, 4).formatted(Formatting.GRAY));
            } else {
                list.add(new TranslatableText("filled_map.unknown", new Object[0]).formatted(Formatting.GRAY));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static int getMapColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getSubTag("display");
        if (compoundTag != null && compoundTag.containsKey("MapColor", 99)) {
            int i = compoundTag.getInt("MapColor");
            return 0xFF000000 | i & 0xFFFFFF;
        }
        return -12173266;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        BlockState blockState = itemUsageContext.getWorld().getBlockState(itemUsageContext.getBlockPos());
        if (blockState.matches(BlockTags.BANNERS)) {
            if (!itemUsageContext.world.isClient) {
                MapState mapState = FilledMapItem.getOrCreateMapState(itemUsageContext.getStack(), itemUsageContext.getWorld());
                mapState.addBanner(itemUsageContext.getWorld(), itemUsageContext.getBlockPos());
            }
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(itemUsageContext);
    }
}

