package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

public class FilledMapItem extends NetworkSyncedItem {
	public FilledMapItem(Item.Settings settings) {
		super(settings);
	}

	public static ItemStack method_8005(World world, int i, int j, byte b, boolean bl, boolean bl2) {
		ItemStack itemStack = new ItemStack(Items.field_8204);
		method_8000(itemStack, world, i, j, b, bl, bl2, world.field_9247.method_12460());
		return itemStack;
	}

	@Nullable
	public static MapState method_7997(ItemStack itemStack, World world) {
		return world.method_17891(getMapName(getMapId(itemStack)));
	}

	@Nullable
	public static MapState method_8001(ItemStack itemStack, World world) {
		MapState mapState = method_7997(itemStack, world);
		if (mapState == null && !world.isClient) {
			mapState = method_8000(itemStack, world, world.method_8401().getSpawnX(), world.method_8401().getSpawnZ(), 3, false, false, world.field_9247.method_12460());
		}

		return mapState;
	}

	public static int getMapId(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null && compoundTag.containsKey("map", 99) ? compoundTag.getInt("map") : 0;
	}

	private static MapState method_8000(ItemStack itemStack, World world, int i, int j, int k, boolean bl, boolean bl2, DimensionType dimensionType) {
		int l = world.getNextMapId();
		MapState mapState = new MapState(getMapName(l));
		mapState.init(i, j, k, bl, bl2, dimensionType);
		world.method_17890(mapState);
		itemStack.getOrCreateTag().putInt("map", l);
		return mapState;
	}

	public static String getMapName(int i) {
		return "map_" + i;
	}

	public void method_7998(World world, Entity entity, MapState mapState) {
		if (world.field_9247.method_12460() == mapState.dimension && entity instanceof PlayerEntity) {
			int i = 1 << mapState.scale;
			int j = mapState.xCenter;
			int k = mapState.zCenter;
			int l = MathHelper.floor(entity.x - (double)j) / i + 64;
			int m = MathHelper.floor(entity.z - (double)k) / i + 64;
			int n = 128 / i;
			if (world.field_9247.isNether()) {
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
							WorldChunk worldChunk = world.method_8500(new BlockPos(s, 0, t));
							if (!worldChunk.isEmpty()) {
								ChunkPos chunkPos = worldChunk.getPos();
								int u = s & 15;
								int v = t & 15;
								int w = 0;
								double e = 0.0;
								if (world.field_9247.isNether()) {
									int x = s + t * 231871;
									x = x * x * 31287121 + x * 11;
									if ((x >> 20 & 1) == 0) {
										multiset.add(Blocks.field_10566.method_9564().method_11625(world, BlockPos.ORIGIN), 10);
									} else {
										multiset.add(Blocks.field_10340.method_9564().method_11625(world, BlockPos.ORIGIN), 100);
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
												blockState = Blocks.field_9987.method_9564();
											} else {
												do {
													mutable.set(chunkPos.getStartX() + y + u, --aa, chunkPos.getStartZ() + z + v);
													blockState = worldChunk.method_8320(mutable);
												} while (blockState.method_11625(world, mutable) == MaterialColor.AIR && aa > 0);

												if (aa > 0 && !blockState.method_11618().isEmpty()) {
													int ab = aa - 1;
													mutable2.set(mutable);

													BlockState blockState2;
													do {
														mutable2.setY(ab--);
														blockState2 = worldChunk.method_8320(mutable2);
														w++;
													} while (ab > 0 && !blockState2.method_11618().isEmpty());

													blockState = this.method_7995(world, blockState, mutable);
												}
											}

											mapState.removeBanner(world, chunkPos.getStartX() + y + u, chunkPos.getStartZ() + z + v);
											e += (double)aa / (double)(i * i);
											multiset.add(blockState.method_11625(world, mutable));
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

	private BlockState method_7995(World world, BlockState blockState, BlockPos blockPos) {
		FluidState fluidState = blockState.method_11618();
		return !fluidState.isEmpty() && !Block.method_20045(blockState, world, blockPos, Direction.field_11036) ? fluidState.getBlockState() : blockState;
	}

	private static boolean method_8004(Biome[] biomes, int i, int j, int k) {
		return biomes[j * i + k * i * 128 * i].getDepth() >= 0.0F;
	}

	public static void method_8002(World world, ItemStack itemStack) {
		MapState mapState = method_8001(itemStack, world);
		if (mapState != null) {
			if (world.field_9247.method_12460() == mapState.dimension) {
				int i = 1 << mapState.scale;
				int j = mapState.xCenter;
				int k = mapState.zCenter;
				Biome[] biomes = world.method_8398().getChunkGenerator().getBiomeSource().sampleBiomes((j / i - 64) * i, (k / i - 64) * i, 128 * i, 128 * i, false);

				for (int l = 0; l < 128; l++) {
					for (int m = 0; m < 128; m++) {
						if (l > 0 && m > 0 && l < 127 && m < 127) {
							Biome biome = biomes[l * i + m * i * 128 * i];
							int n = 8;
							if (method_8004(biomes, i, l - 1, m - 1)) {
								n--;
							}

							if (method_8004(biomes, i, l - 1, m + 1)) {
								n--;
							}

							if (method_8004(biomes, i, l - 1, m)) {
								n--;
							}

							if (method_8004(biomes, i, l + 1, m - 1)) {
								n--;
							}

							if (method_8004(biomes, i, l + 1, m + 1)) {
								n--;
							}

							if (method_8004(biomes, i, l + 1, m)) {
								n--;
							}

							if (method_8004(biomes, i, l, m - 1)) {
								n--;
							}

							if (method_8004(biomes, i, l, m + 1)) {
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
	public void method_7888(ItemStack itemStack, World world, Entity entity, int i, boolean bl) {
		if (!world.isClient) {
			MapState mapState = method_8001(itemStack, world);
			if (mapState != null) {
				if (entity instanceof PlayerEntity) {
					PlayerEntity playerEntity = (PlayerEntity)entity;
					mapState.update(playerEntity, itemStack);
				}

				if (!mapState.locked && (bl || entity instanceof PlayerEntity && ((PlayerEntity)entity).getOffHandStack() == itemStack)) {
					this.method_7998(world, entity, mapState);
				}
			}
		}
	}

	@Nullable
	@Override
	public Packet<?> method_7757(ItemStack itemStack, World world, PlayerEntity playerEntity) {
		return method_8001(itemStack, world).getPlayerMarkerPacket(itemStack, world, playerEntity);
	}

	@Override
	public void method_7843(ItemStack itemStack, World world, PlayerEntity playerEntity) {
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null && compoundTag.containsKey("map_scale_direction", 99)) {
			method_7996(itemStack, world, compoundTag.getInt("map_scale_direction"));
			compoundTag.remove("map_scale_direction");
		}
	}

	protected static void method_7996(ItemStack itemStack, World world, int i) {
		MapState mapState = method_8001(itemStack, world);
		if (mapState != null) {
			method_8000(
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
	public static ItemStack method_17442(World world, ItemStack itemStack) {
		MapState mapState = method_8001(itemStack, world);
		if (mapState != null) {
			ItemStack itemStack2 = itemStack.copy();
			MapState mapState2 = method_8000(itemStack2, world, 0, 0, mapState.scale, mapState.showIcons, mapState.unlimitedTracking, mapState.dimension);
			mapState2.copyFrom(mapState);
			return itemStack2;
		} else {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		MapState mapState = world == null ? null : method_8001(itemStack, world);
		if (mapState != null && mapState.locked) {
			list.add(new TranslatableText("filled_map.locked", getMapId(itemStack)).formatted(Formatting.field_1080));
		}

		if (tooltipContext.isAdvanced()) {
			if (mapState != null) {
				list.add(new TranslatableText("filled_map.id", getMapId(itemStack)).formatted(Formatting.field_1080));
				list.add(new TranslatableText("filled_map.scale", 1 << mapState.scale).formatted(Formatting.field_1080));
				list.add(new TranslatableText("filled_map.level", mapState.scale, 4).formatted(Formatting.field_1080));
			} else {
				list.add(new TranslatableText("filled_map.unknown").formatted(Formatting.field_1080));
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
		BlockState blockState = itemUsageContext.method_8045().method_8320(itemUsageContext.getBlockPos());
		if (blockState.matches(BlockTags.field_15501)) {
			if (!itemUsageContext.field_8945.isClient) {
				MapState mapState = method_8001(itemUsageContext.getStack(), itemUsageContext.method_8045());
				mapState.addBanner(itemUsageContext.method_8045(), itemUsageContext.getBlockPos());
			}

			return ActionResult.field_5812;
		} else {
			return super.useOnBlock(itemUsageContext);
		}
	}
}
