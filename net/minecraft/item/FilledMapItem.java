/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multisets;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public class FilledMapItem
extends NetworkSyncedItem {
    public static final int field_30907 = 128;
    public static final int field_30908 = 128;
    private static final int DEFAULT_MAP_COLOR = -12173266;
    private static final String MAP_KEY = "map";

    public FilledMapItem(Item.Settings settings) {
        super(settings);
    }

    public static ItemStack createMap(World world, int x, int z, byte scale, boolean showIcons, boolean unlimitedTracking) {
        ItemStack itemStack = new ItemStack(Items.FILLED_MAP);
        FilledMapItem.createMapState(itemStack, world, x, z, scale, showIcons, unlimitedTracking, world.getRegistryKey());
        return itemStack;
    }

    @Nullable
    public static MapState getMapState(@Nullable Integer id, World world) {
        return id == null ? null : world.getMapState(FilledMapItem.getMapName(id));
    }

    @Nullable
    public static MapState getOrCreateMapState(ItemStack map, World world) {
        Integer integer = FilledMapItem.getMapId(map);
        return FilledMapItem.getMapState(integer, world);
    }

    @Nullable
    public static Integer getMapId(ItemStack stack) {
        NbtCompound nbtCompound = stack.getTag();
        return nbtCompound != null && nbtCompound.contains(MAP_KEY, 99) ? Integer.valueOf(nbtCompound.getInt(MAP_KEY)) : null;
    }

    private static int allocateMapId(World world, int x, int z, int scale, boolean showIcons, boolean unlimitedTracking, RegistryKey<World> dimension) {
        MapState mapState = MapState.of(x, z, (byte)scale, showIcons, unlimitedTracking, dimension);
        int i = world.getNextMapId();
        world.putMapState(FilledMapItem.getMapName(i), mapState);
        return i;
    }

    private static void setMapId(ItemStack stack, int id) {
        stack.getOrCreateTag().putInt(MAP_KEY, id);
    }

    private static void createMapState(ItemStack stack, World world, int x, int z, int scale, boolean showIcons, boolean unlimitedTracking, RegistryKey<World> dimension) {
        int i = FilledMapItem.allocateMapId(world, x, z, scale, showIcons, unlimitedTracking, dimension);
        FilledMapItem.setMapId(stack, i);
    }

    public static String getMapName(int mapId) {
        return "map_" + mapId;
    }

    public void updateColors(World world, Entity entity, MapState state) {
        if (world.getRegistryKey() != state.dimension || !(entity instanceof PlayerEntity)) {
            return;
        }
        int i = 1 << state.scale;
        int j = state.centerX;
        int k = state.centerZ;
        int l = MathHelper.floor(entity.getX() - (double)j) / i + 64;
        int m = MathHelper.floor(entity.getZ() - (double)k) / i + 64;
        int n = 128 / i;
        if (world.getDimension().hasCeiling()) {
            n /= 2;
        }
        MapState.PlayerUpdateTracker playerUpdateTracker = state.getPlayerSyncData((PlayerEntity)entity);
        ++playerUpdateTracker.field_131;
        boolean bl = false;
        for (int o = l - n + 1; o < l + n; ++o) {
            if ((o & 0xF) != (playerUpdateTracker.field_131 & 0xF) && !bl) continue;
            bl = false;
            double d = 0.0;
            for (int p = m - n - 1; p < m + n; ++p) {
                MapColor mapColor;
                int y;
                if (o < 0 || p < -1 || o >= 128 || p >= 128) continue;
                int q = o - l;
                int r = p - m;
                boolean bl2 = q * q + r * r > (n - 2) * (n - 2);
                int s = (j / i + o - 64) * i;
                int t = (k / i + p - 64) * i;
                LinkedHashMultiset<MapColor> multiset = LinkedHashMultiset.create();
                WorldChunk worldChunk = world.getWorldChunk(new BlockPos(s, 0, t));
                if (worldChunk.isEmpty()) continue;
                ChunkPos chunkPos = worldChunk.getPos();
                int u = s & 0xF;
                int v = t & 0xF;
                int w = 0;
                double e = 0.0;
                if (world.getDimension().hasCeiling()) {
                    int x = s + t * 231871;
                    if (((x = x * x * 31287121 + x * 11) >> 20 & 1) == 0) {
                        multiset.add(Blocks.DIRT.getDefaultState().getMapColor(world, BlockPos.ORIGIN), 10);
                    } else {
                        multiset.add(Blocks.STONE.getDefaultState().getMapColor(world, BlockPos.ORIGIN), 100);
                    }
                    e = 100.0;
                } else {
                    BlockPos.Mutable mutable = new BlockPos.Mutable();
                    BlockPos.Mutable mutable2 = new BlockPos.Mutable();
                    for (y = 0; y < i; ++y) {
                        for (int z = 0; z < i; ++z) {
                            BlockState blockState;
                            int aa = worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, y + u, z + v) + 1;
                            if (aa > world.getBottomY() + 1) {
                                do {
                                    mutable.set(chunkPos.getStartX() + y + u, --aa, chunkPos.getStartZ() + z + v);
                                } while ((blockState = worldChunk.getBlockState(mutable)).getMapColor(world, mutable) == MapColor.CLEAR && aa > world.getBottomY());
                                if (aa > world.getBottomY() && !blockState.getFluidState().isEmpty()) {
                                    BlockState blockState2;
                                    int ab = aa - 1;
                                    mutable2.set(mutable);
                                    do {
                                        mutable2.setY(ab--);
                                        blockState2 = worldChunk.getBlockState(mutable2);
                                        ++w;
                                    } while (ab > world.getBottomY() && !blockState2.getFluidState().isEmpty());
                                    blockState = this.getFluidStateIfVisible(world, blockState, mutable);
                                }
                            } else {
                                blockState = Blocks.BEDROCK.getDefaultState();
                            }
                            state.removeBanner(world, chunkPos.getStartX() + y + u, chunkPos.getStartZ() + z + v);
                            e += (double)aa / (double)(i * i);
                            multiset.add(blockState.getMapColor(world, mutable));
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
                if ((mapColor = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MapColor.CLEAR)) == MapColor.WATER_BLUE) {
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
                if (p < 0 || q * q + r * r >= n * n || bl2 && (o + p & 1) == 0) continue;
                bl |= state.putColor(o, p, (byte)(mapColor.id * 4 + y));
            }
        }
    }

    private BlockState getFluidStateIfVisible(World world, BlockState state, BlockPos pos) {
        FluidState fluidState = state.getFluidState();
        if (!fluidState.isEmpty() && !state.isSideSolidFullSquare(world, pos, Direction.UP)) {
            return fluidState.getBlockState();
        }
        return state;
    }

    private static boolean hasPositiveDepth(Biome[] biomes, int scale, int x, int z) {
        return biomes[x * scale + z * scale * 128 * scale].getDepth() >= 0.0f;
    }

    public static void fillExplorationMap(ServerWorld world, ItemStack map) {
        int m;
        int l;
        MapState mapState = FilledMapItem.getOrCreateMapState(map, world);
        if (mapState == null) {
            return;
        }
        if (world.getRegistryKey() != mapState.dimension) {
            return;
        }
        int i = 1 << mapState.scale;
        int j = mapState.centerX;
        int k = mapState.centerZ;
        Biome[] biomes = new Biome[128 * i * 128 * i];
        for (l = 0; l < 128 * i; ++l) {
            for (m = 0; m < 128 * i; ++m) {
                biomes[l * 128 * i + m] = world.getBiome(new BlockPos((j / i - 64) * i + m, 0, (k / i - 64) * i + l));
            }
        }
        for (l = 0; l < 128; ++l) {
            for (m = 0; m < 128; ++m) {
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
                MapColor mapColor = MapColor.CLEAR;
                if (biome.getDepth() < 0.0f) {
                    mapColor = MapColor.ORANGE;
                    if (n > 7 && m % 2 == 0) {
                        o = (l + (int)(MathHelper.sin((float)m + 0.0f) * 7.0f)) / 8 % 5;
                        if (o == 3) {
                            o = 1;
                        } else if (o == 4) {
                            o = 0;
                        }
                    } else if (n > 7) {
                        mapColor = MapColor.CLEAR;
                    } else if (n > 5) {
                        o = 1;
                    } else if (n > 3) {
                        o = 0;
                    } else if (n > 1) {
                        o = 0;
                    }
                } else if (n > 0) {
                    mapColor = MapColor.BROWN;
                    o = n > 3 ? 1 : 3;
                }
                if (mapColor == MapColor.CLEAR) continue;
                mapState.setColor(l, m, (byte)(mapColor.id * 4 + o));
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }
        MapState mapState = FilledMapItem.getOrCreateMapState(stack, world);
        if (mapState == null) {
            return;
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            mapState.update(playerEntity, stack);
        }
        if (!mapState.locked && (selected || entity instanceof PlayerEntity && ((PlayerEntity)entity).getOffHandStack() == stack)) {
            this.updateColors(world, entity, mapState);
        }
    }

    @Override
    @Nullable
    public Packet<?> createSyncPacket(ItemStack stack, World world, PlayerEntity player) {
        Integer integer = FilledMapItem.getMapId(stack);
        MapState mapState = FilledMapItem.getMapState(integer, world);
        if (mapState != null) {
            return mapState.getPlayerMarkerPacket(integer, player);
        }
        return null;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        NbtCompound nbtCompound = stack.getTag();
        if (nbtCompound != null && nbtCompound.contains("map_scale_direction", 99)) {
            FilledMapItem.scale(stack, world, nbtCompound.getInt("map_scale_direction"));
            nbtCompound.remove("map_scale_direction");
        } else if (nbtCompound != null && nbtCompound.contains("map_to_lock", 1) && nbtCompound.getBoolean("map_to_lock")) {
            FilledMapItem.copyMap(world, stack);
            nbtCompound.remove("map_to_lock");
        }
    }

    private static void scale(ItemStack map, World world, int amount) {
        MapState mapState = FilledMapItem.getOrCreateMapState(map, world);
        if (mapState != null) {
            int i = world.getNextMapId();
            world.putMapState(FilledMapItem.getMapName(i), mapState.zoomOut(amount));
            FilledMapItem.setMapId(map, i);
        }
    }

    public static void copyMap(World world, ItemStack stack) {
        MapState mapState = FilledMapItem.getOrCreateMapState(stack, world);
        if (mapState != null) {
            int i = world.getNextMapId();
            String string = FilledMapItem.getMapName(i);
            MapState mapState2 = mapState.copy();
            world.putMapState(string, mapState2);
            FilledMapItem.setMapId(stack, i);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        MapState mapState;
        Integer integer = FilledMapItem.getMapId(stack);
        MapState mapState2 = mapState = world == null ? null : FilledMapItem.getMapState(integer, world);
        if (mapState != null && mapState.locked) {
            tooltip.add(new TranslatableText("filled_map.locked", integer).formatted(Formatting.GRAY));
        }
        if (context.isAdvanced()) {
            if (mapState != null) {
                tooltip.add(new TranslatableText("filled_map.id", integer).formatted(Formatting.GRAY));
                tooltip.add(new TranslatableText("filled_map.scale", 1 << mapState.scale).formatted(Formatting.GRAY));
                tooltip.add(new TranslatableText("filled_map.level", mapState.scale, 4).formatted(Formatting.GRAY));
            } else {
                tooltip.add(new TranslatableText("filled_map.unknown").formatted(Formatting.GRAY));
            }
        }
    }

    public static int getMapColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubTag("display");
        if (nbtCompound != null && nbtCompound.contains("MapColor", 99)) {
            int i = nbtCompound.getInt("MapColor");
            return 0xFF000000 | i & 0xFFFFFF;
        }
        return -12173266;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
        if (blockState.isIn(BlockTags.BANNERS)) {
            MapState mapState;
            if (!context.getWorld().isClient && (mapState = FilledMapItem.getOrCreateMapState(context.getStack(), context.getWorld())) != null) {
                mapState.addBanner(context.getWorld(), context.getBlockPos());
            }
            return ActionResult.success(context.getWorld().isClient);
        }
        return super.useOnBlock(context);
    }
}

