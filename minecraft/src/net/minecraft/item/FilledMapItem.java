package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

public class FilledMapItem extends NetworkSyncedItem {
	public FilledMapItem(Item.Settings settings) {
		super(settings);
	}

	public static ItemStack createMap(World world, int i, int j, byte b, boolean bl, boolean bl2) {
		ItemStack itemStack = new ItemStack(Items.field_8204);
		createMapState(itemStack, world, i, j, b, bl, bl2, world.dimension.getType());
		return itemStack;
	}

	@Nullable
	public static MapState getMapState(ItemStack itemStack, World world) {
		return world.getMapState(getMapName(getMapId(itemStack)));
	}

	@Nullable
	public static MapState getOrCreateMapState(ItemStack itemStack, World world) {
		MapState mapState = getMapState(itemStack, world);
		if (mapState == null && !world.isClient) {
			mapState = createMapState(
				itemStack, world, world.getLevelProperties().getSpawnX(), world.getLevelProperties().getSpawnZ(), 3, false, false, world.dimension.getType()
			);
		}

		return mapState;
	}

	public static int getMapId(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null && compoundTag.containsKey("map", 99) ? compoundTag.getInt("map") : 0;
	}

	private static MapState createMapState(ItemStack itemStack, World world, int i, int j, int k, boolean bl, boolean bl2, DimensionType dimensionType) {
		int l = world.getNextMapId();
		MapState mapState = new MapState(getMapName(l));
		mapState.init(i, j, k, bl, bl2, dimensionType);
		world.putMapState(mapState);
		itemStack.getOrCreateTag().putInt("map", l);
		return mapState;
	}

	public static String getMapName(int i) {
		return "map_" + i;
	}

	public void updateColors(World world, Entity entity, MapState mapState) {
		if (world.dimension.getType() == mapState.dimension && entity instanceof PlayerEntity) {
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
			playerUpdateTracker.field_131++;
			boolean bl = false;

			for (int o = l - n + 1; o < l + n; o++) {
				if ((o & 15) == (playerUpdateTracker.field_131 & 15) || bl) {
					bl = false;
					double d = 0.0;

					for (int p = m - n - 1; p < m + n; p++) {
						if (o >= 0 && p >= -1 && o < 128 && p < 128) {
							int q = o - l;
							int r = p - m;
							boolean bl2 = q * q + r * r > (n - 2) * (n - 2);
							int s = (j / i + o - 64) * i;
							int t = (k / i + p - 64) * i;
							Multiset<MaterialColor> multiset = LinkedHashMultiset.create();
							WorldChunk worldChunk = world.getWorldChunk(new BlockPos(s, 0, t));
							if (!worldChunk.isEmpty()) {
								ChunkPos chunkPos = worldChunk.getPos();
								int u = s & 15;
								int v = t & 15;
								int w = 0;
								double e = 0.0;
								if (world.dimension.isNether()) {
									int x = s + t * 231871;
									x = x * x * 31287121 + x * 11;
									if ((x >> 20 & 1) == 0) {
										multiset.add(Blocks.field_10566.getDefaultState().getTopMaterialColor(world, BlockPos.ORIGIN), 10);
									} else {
										multiset.add(Blocks.field_10340.getDefaultState().getTopMaterialColor(world, BlockPos.ORIGIN), 100);
									}

									e = 100.0;
								} else {
									BlockPos.Mutable mutable = new BlockPos.Mutable();
									BlockPos.Mutable mutable2 = new BlockPos.Mutable();

									for (int y = 0; y < i; y++) {
										for (int z = 0; z < i; z++) {
											int aa = worldChunk.sampleHeightmap(Heightmap.Type.field_13202, y + u, z + v) + 1;
											BlockState blockState;
											if (aa <= 1) {
												blockState = Blocks.field_9987.getDefaultState();
											} else {
												do {
													mutable.set(chunkPos.getStartX() + y + u, --aa, chunkPos.getStartZ() + z + v);
													blockState = worldChunk.getBlockState(mutable);
												} while (blockState.getTopMaterialColor(world, mutable) == MaterialColor.AIR && aa > 0);

												if (aa > 0 && !blockState.getFluidState().isEmpty()) {
													int ab = aa - 1;
													mutable2.set(mutable);

													BlockState blockState2;
													do {
														mutable2.setY(ab--);
														blockState2 = worldChunk.getBlockState(mutable2);
														w++;
													} while (ab > 0 && !blockState2.getFluidState().isEmpty());

													blockState = this.getFluidStateIfVisible(world, blockState, mutable);
												}
											}

											mapState.removeBanner(world, chunkPos.getStartX() + y + u, chunkPos.getStartZ() + z + v);
											e += (double)aa / (double)(i * i);
											multiset.add(blockState.getTopMaterialColor(world, mutable));
										}
									}
								}

								w /= i * i;
								double f = (e - d) * 4.0 / (double)(i + 4) + ((double)(o + p & 1) - 0.5) * 0.4;
								int y = 1;
								if (f > 0.6) {
									y = 2;
								}

								if (f < -0.6) {
									y = 0;
								}

								MaterialColor materialColor = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MaterialColor.AIR);
								if (materialColor == MaterialColor.WATER) {
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
								if (p >= 0 && q * q + r * r < n * n && (!bl2 || (o + p & 1) != 0)) {
									byte b = mapState.colors[o + p * 128];
									byte c = (byte)(materialColor.id * 4 + y);
									if (b != c) {
										mapState.colors[o + p * 128] = c;
										mapState.markDirty(o, p);
										bl = true;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private BlockState getFluidStateIfVisible(World world, BlockState blockState, BlockPos blockPos) {
		FluidState fluidState = blockState.getFluidState();
		return !fluidState.isEmpty() && !Block.isSolidFullSquare(blockState, world, blockPos, Direction.field_11036) ? fluidState.getBlockState() : blockState;
	}

	private static boolean hasPositiveDepth(Biome[] biomes, int i, int j, int k) {
		return biomes[j * i + k * i * 128 * i].getDepth() >= 0.0F;
	}

	public static void fillExplorationMap(World world, ItemStack itemStack) {
		MapState mapState = getOrCreateMapState(itemStack, world);
		if (mapState != null) {
			if (world.dimension.getType() == mapState.dimension) {
				int i = 1 << mapState.scale;
				int j = mapState.xCenter;
				int k = mapState.zCenter;
				Biome[] biomes = world.getChunkManager().getChunkGenerator().getBiomeSource().sampleBiomes((j / i - 64) * i, (k / i - 64) * i, 128 * i, 128 * i, false);

				for (int l = 0; l < 128; l++) {
					for (int m = 0; m < 128; m++) {
						if (l > 0 && m > 0 && l < 127 && m < 127) {
							Biome biome = biomes[l * i + m * i * 128 * i];
							int n = 8;
							if (hasPositiveDepth(biomes, i, l - 1, m - 1)) {
								n--;
							}

							if (hasPositiveDepth(biomes, i, l - 1, m + 1)) {
								n--;
							}

							if (hasPositiveDepth(biomes, i, l - 1, m)) {
								n--;
							}

							if (hasPositiveDepth(biomes, i, l + 1, m - 1)) {
								n--;
							}

							if (hasPositiveDepth(biomes, i, l + 1, m + 1)) {
								n--;
							}

							if (hasPositiveDepth(biomes, i, l + 1, m)) {
								n--;
							}

							if (hasPositiveDepth(biomes, i, l, m - 1)) {
								n--;
							}

							if (hasPositiveDepth(biomes, i, l, m + 1)) {
								n--;
							}

							int o = 3;
							MaterialColor materialColor = MaterialColor.AIR;
							if (biome.getDepth() < 0.0F) {
								materialColor = MaterialColor.ORANGE;
								if (n > 7 && m % 2 == 0) {
									o = (l + (int)(MathHelper.sin((float)m + 0.0F) * 7.0F)) / 8 % 5;
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
								if (n > 3) {
									o = 1;
								} else {
									o = 3;
								}
							}

							if (materialColor != MaterialColor.AIR) {
								mapState.colors[l + m * 128] = (byte)(materialColor.id * 4 + o);
								mapState.markDirty(l, m);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack itemStack, World world, Entity entity, int i, boolean bl) {
		if (!world.isClient) {
			MapState mapState = getOrCreateMapState(itemStack, world);
			if (mapState != null) {
				if (entity instanceof PlayerEntity) {
					PlayerEntity playerEntity = (PlayerEntity)entity;
					mapState.update(playerEntity, itemStack);
				}

				if (!mapState.locked && (bl || entity instanceof PlayerEntity && ((PlayerEntity)entity).getOffHandStack() == itemStack)) {
					this.updateColors(world, entity, mapState);
				}
			}
		}
	}

	@Nullable
	@Override
	public Packet<?> createSyncPacket(ItemStack itemStack, World world, PlayerEntity playerEntity) {
		return getOrCreateMapState(itemStack, world).getPlayerMarkerPacket(itemStack, world, playerEntity);
	}

	@Override
	public void onCraft(ItemStack itemStack, World world, PlayerEntity playerEntity) {
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null && compoundTag.containsKey("map_scale_direction", 99)) {
			scale(itemStack, world, compoundTag.getInt("map_scale_direction"));
			compoundTag.remove("map_scale_direction");
		}
	}

	protected static void scale(ItemStack itemStack, World world, int i) {
		MapState mapState = getOrCreateMapState(itemStack, world);
		if (mapState != null) {
			createMapState(
				itemStack,
				world,
				mapState.xCenter,
				mapState.zCenter,
				MathHelper.clamp(mapState.scale + i, 0, 4),
				mapState.showIcons,
				mapState.unlimitedTracking,
				mapState.dimension
			);
		}
	}

	@Nullable
	public static ItemStack copyMap(World world, ItemStack itemStack) {
		MapState mapState = getOrCreateMapState(itemStack, world);
		if (mapState != null) {
			ItemStack itemStack2 = itemStack.copy();
			MapState mapState2 = createMapState(itemStack2, world, 0, 0, mapState.scale, mapState.showIcons, mapState.unlimitedTracking, mapState.dimension);
			mapState2.copyFrom(mapState);
			return itemStack2;
		} else {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Component> list, TooltipContext tooltipContext) {
		MapState mapState = world == null ? null : getOrCreateMapState(itemStack, world);
		if (mapState != null && mapState.locked) {
			list.add(new TranslatableComponent("filled_map.locked", getMapId(itemStack)).applyFormat(ChatFormat.field_1080));
		}

		if (tooltipContext.isAdvanced()) {
			if (mapState != null) {
				list.add(new TranslatableComponent("filled_map.id", getMapId(itemStack)).applyFormat(ChatFormat.field_1080));
				list.add(new TranslatableComponent("filled_map.scale", 1 << mapState.scale).applyFormat(ChatFormat.field_1080));
				list.add(new TranslatableComponent("filled_map.level", mapState.scale, 4).applyFormat(ChatFormat.field_1080));
			} else {
				list.add(new TranslatableComponent("filled_map.unknown").applyFormat(ChatFormat.field_1080));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static int getMapColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubTag("display");
		if (compoundTag != null && compoundTag.containsKey("MapColor", 99)) {
			int i = compoundTag.getInt("MapColor");
			return 0xFF000000 | i & 16777215;
		} else {
			return -12173266;
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		BlockState blockState = itemUsageContext.getWorld().getBlockState(itemUsageContext.getBlockPos());
		if (blockState.matches(BlockTags.field_15501)) {
			if (!itemUsageContext.world.isClient) {
				MapState mapState = getOrCreateMapState(itemUsageContext.getStack(), itemUsageContext.getWorld());
				mapState.addBanner(itemUsageContext.getWorld(), itemUsageContext.getBlockPos());
			}

			return ActionResult.field_5812;
		} else {
			return super.useOnBlock(itemUsageContext);
		}
	}
}
