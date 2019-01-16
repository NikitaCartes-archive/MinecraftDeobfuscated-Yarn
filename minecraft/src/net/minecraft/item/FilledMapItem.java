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
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;

public class FilledMapItem extends MapItem {
	public FilledMapItem(Item.Settings settings) {
		super(settings);
	}

	public static ItemStack method_8005(World world, int i, int j, byte b, boolean bl, boolean bl2) {
		ItemStack itemStack = new ItemStack(Items.field_8204);
		method_8000(itemStack, world, i, j, b, bl, bl2, world.dimension.getType());
		return itemStack;
	}

	@Nullable
	public static MapState method_17441(ItemStack itemStack, IWorld iWorld) {
		return method_7997(iWorld, method_17440(method_8003(itemStack)));
	}

	@Nullable
	public static MapState method_8001(ItemStack itemStack, World world) {
		MapState mapState = method_17441(itemStack, world);
		if (mapState == null && !world.isClient) {
			mapState = method_8000(
				itemStack, world, world.getLevelProperties().getSpawnX(), world.getLevelProperties().getSpawnZ(), 3, false, false, world.dimension.getType()
			);
		}

		return mapState;
	}

	public static int method_8003(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null && compoundTag.containsKey("map", 99) ? compoundTag.getInt("map") : 0;
	}

	private static MapState method_8000(ItemStack itemStack, World world, int i, int j, int k, boolean bl, boolean bl2, DimensionType dimensionType) {
		int l = world.getNextAvailableId(DimensionType.field_13072, "map");
		MapState mapState = new MapState(method_17440(l));
		mapState.method_105(i, j, k, bl, bl2, dimensionType);
		world.setPersistentState(DimensionType.field_13072, mapState.getId(), mapState);
		itemStack.getOrCreateTag().putInt("map", l);
		return mapState;
	}

	public static String method_17440(int i) {
		return "map_" + i;
	}

	@Nullable
	public static MapState method_7997(IWorld iWorld, String string) {
		return iWorld.getPersistentState(DimensionType.field_13072, MapState::new, string);
	}

	public void method_7998(World world, Entity entity, MapState mapState) {
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

			MapState.class_23 lv = mapState.method_101((PlayerEntity)entity);
			lv.field_131++;
			boolean bl = false;

			for (int o = l - n + 1; o < l + n; o++) {
				if ((o & 15) == (lv.field_131 & 15) || bl) {
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
										multiset.add(Blocks.field_10566.getDefaultState().getMaterialColor(world, BlockPos.ORIGIN), 10);
									} else {
										multiset.add(Blocks.field_10340.getDefaultState().getMaterialColor(world, BlockPos.ORIGIN), 100);
									}

									e = 100.0;
								} else {
									BlockPos.Mutable mutable = new BlockPos.Mutable();
									BlockPos.Mutable mutable2 = new BlockPos.Mutable();

									for (int y = 0; y < i; y++) {
										for (int z = 0; z < i; z++) {
											int aa = worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, y + u, z + v) + 1;
											BlockState blockState;
											if (aa <= 1) {
												blockState = Blocks.field_9987.getDefaultState();
											} else {
												do {
													mutable.set(chunkPos.getXStart() + y + u, --aa, chunkPos.getZStart() + z + v);
													blockState = worldChunk.getBlockState(mutable);
												} while (blockState.getMaterialColor(world, mutable) == MaterialColor.AIR && aa > 0);

												if (aa > 0 && !blockState.getFluidState().isEmpty()) {
													int ab = aa - 1;
													mutable2.set(mutable);

													BlockState blockState2;
													do {
														mutable2.setY(ab--);
														blockState2 = worldChunk.getBlockState(mutable2);
														w++;
													} while (ab > 0 && !blockState2.getFluidState().isEmpty());

													blockState = this.method_7995(world, blockState, mutable);
												}
											}

											mapState.method_109(world, chunkPos.getXStart() + y + u, chunkPos.getZStart() + z + v);
											e += (double)aa / (double)(i * i);
											multiset.add(blockState.getMaterialColor(world, mutable));
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
									byte b = mapState.colorArray[o + p * 128];
									byte c = (byte)(materialColor.id * 4 + y);
									if (b != c) {
										mapState.colorArray[o + p * 128] = c;
										mapState.method_103(o, p);
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
		FluidState fluidState = blockState.getFluidState();
		return !fluidState.isEmpty() && !Block.isFaceFullCube(blockState.getCollisionShape(world, blockPos), Direction.UP) ? fluidState.getBlockState() : blockState;
	}

	private static boolean method_8004(Biome[] biomes, int i, int j, int k) {
		return biomes[j * i + k * i * 128 * i].getDepth() >= 0.0F;
	}

	public static void method_8002(World world, ItemStack itemStack) {
		MapState mapState = method_8001(itemStack, world);
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
								mapState.colorArray[l + m * 128] = (byte)(materialColor.id * 4 + o);
								mapState.method_103(l, m);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int i, boolean bl) {
		if (!world.isClient) {
			MapState mapState = method_8001(itemStack, world);
			if (mapState != null) {
				if (entity instanceof PlayerEntity) {
					PlayerEntity playerEntity = (PlayerEntity)entity;
					mapState.method_102(playerEntity, itemStack);
				}

				if (!mapState.field_17403 && (bl || entity instanceof PlayerEntity && ((PlayerEntity)entity).getOffHandStack() == itemStack)) {
					this.method_7998(world, entity, mapState);
				}
			}
		}
	}

	@Nullable
	@Override
	public Packet<?> createMapPacket(ItemStack itemStack, World world, PlayerEntity playerEntity) {
		return method_8001(itemStack, world).method_100(itemStack, world, playerEntity);
	}

	@Override
	public void onCrafted(ItemStack itemStack, World world, PlayerEntity playerEntity) {
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
			mapState2.xCenter = mapState.xCenter;
			mapState2.zCenter = mapState.zCenter;
			mapState2.field_17403 = true;
			mapState2.markDirty();
			System.arraycopy(mapState.colorArray, 0, mapState2.colorArray, 0, mapState.colorArray.length);
			return itemStack2;
		} else {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipOptions tooltipOptions) {
		MapState mapState = world == null ? null : method_8001(itemStack, world);
		if (mapState != null && mapState.field_17403) {
			list.add(new TranslatableTextComponent("filled_map.locked", method_8003(itemStack)).applyFormat(TextFormat.GRAY));
		}

		if (tooltipOptions.isAdvanced()) {
			if (mapState != null) {
				list.add(new TranslatableTextComponent("filled_map.id", method_8003(itemStack)).applyFormat(TextFormat.GRAY));
				list.add(new TranslatableTextComponent("filled_map.scale", 1 << mapState.scale).applyFormat(TextFormat.GRAY));
				list.add(new TranslatableTextComponent("filled_map.level", mapState.scale, 4).applyFormat(TextFormat.GRAY));
			} else {
				list.add(new TranslatableTextComponent("filled_map.unknown").applyFormat(TextFormat.GRAY));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static int method_7999(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("display");
		if (compoundTag != null && compoundTag.containsKey("MapColor", 99)) {
			int i = compoundTag.getInt("MapColor");
			return 0xFF000000 | i & 16777215;
		} else {
			return -12173266;
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		BlockState blockState = itemUsageContext.getWorld().getBlockState(itemUsageContext.getPos());
		if (blockState.matches(BlockTags.field_15501)) {
			if (!itemUsageContext.world.isClient) {
				MapState mapState = method_8001(itemUsageContext.getItemStack(), itemUsageContext.getWorld());
				mapState.method_108(itemUsageContext.getWorld(), itemUsageContext.getPos());
			}

			return ActionResult.SUCCESS;
		} else {
			return super.useOnBlock(itemUsageContext);
		}
	}
}
