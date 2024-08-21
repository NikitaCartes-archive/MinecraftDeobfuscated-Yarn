package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.WordPackedArray;
import org.slf4j.Logger;

public class ChunkPalettedStorageFix extends DataFix {
	private static final int field_29871 = 128;
	private static final int field_29872 = 64;
	private static final int field_29873 = 32;
	private static final int field_29874 = 16;
	private static final int field_29875 = 8;
	private static final int field_29876 = 4;
	private static final int field_29877 = 2;
	private static final int field_29878 = 1;
	static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_29870 = 4096;

	public ChunkPalettedStorageFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public static String getName(Dynamic<?> dynamic) {
		return dynamic.get("Name").asString("");
	}

	public static String getProperty(Dynamic<?> dynamic, String propertyKey) {
		return dynamic.get("Properties").get(propertyKey).asString("");
	}

	public static int addTo(Int2ObjectBiMap<Dynamic<?>> int2ObjectBiMap, Dynamic<?> dynamic) {
		int i = int2ObjectBiMap.getRawId(dynamic);
		if (i == -1) {
			i = int2ObjectBiMap.add(dynamic);
		}

		return i;
	}

	private Dynamic<?> fixChunk(Dynamic<?> chunkDynamic) {
		Optional<? extends Dynamic<?>> optional = chunkDynamic.get("Level").result();
		return optional.isPresent() && ((Dynamic)optional.get()).get("Sections").asStreamOpt().result().isPresent()
			? chunkDynamic.set("Level", new ChunkPalettedStorageFix.Level((Dynamic<?>)optional.get()).transform())
			: chunkDynamic;
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.CHUNK);
		return this.writeFixAndRead("ChunkPalettedStorageFix", type, type2, this::fixChunk);
	}

	public static int getSideToUpgradeFlag(boolean west, boolean east, boolean north, boolean south) {
		int i = 0;
		if (north) {
			if (east) {
				i |= 2;
			} else if (west) {
				i |= 128;
			} else {
				i |= 1;
			}
		} else if (south) {
			if (west) {
				i |= 32;
			} else if (east) {
				i |= 8;
			} else {
				i |= 16;
			}
		} else if (east) {
			i |= 4;
		} else if (west) {
			i |= 64;
		}

		return i;
	}

	static class ChunkNibbleArray {
		private static final int CONTENTS_LENGTH = 2048;
		private static final int field_29880 = 4;
		private final byte[] contents;

		public ChunkNibbleArray() {
			this.contents = new byte[2048];
		}

		public ChunkNibbleArray(byte[] contents) {
			this.contents = contents;
			if (contents.length != 2048) {
				throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + contents.length);
			}
		}

		public int get(int x, int y, int z) {
			int i = this.getRawIndex(y << 8 | z << 4 | x);
			return this.usesLowNibble(y << 8 | z << 4 | x) ? this.contents[i] & 15 : this.contents[i] >> 4 & 15;
		}

		private boolean usesLowNibble(int index) {
			return (index & 1) == 0;
		}

		private int getRawIndex(int index) {
			return index >> 1;
		}
	}

	public static enum Facing {
		DOWN(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.Y),
		UP(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.Y),
		NORTH(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.Z),
		SOUTH(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.Z),
		WEST(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.X),
		EAST(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.X);

		private final ChunkPalettedStorageFix.Facing.Axis axis;
		private final ChunkPalettedStorageFix.Facing.Direction direction;

		private Facing(final ChunkPalettedStorageFix.Facing.Direction direction, final ChunkPalettedStorageFix.Facing.Axis axis) {
			this.axis = axis;
			this.direction = direction;
		}

		public ChunkPalettedStorageFix.Facing.Direction getDirection() {
			return this.direction;
		}

		public ChunkPalettedStorageFix.Facing.Axis getAxis() {
			return this.axis;
		}

		public static enum Axis {
			X,
			Y,
			Z;
		}

		public static enum Direction {
			POSITIVE(1),
			NEGATIVE(-1);

			private final int offset;

			private Direction(final int offset) {
				this.offset = offset;
			}

			public int getOffset() {
				return this.offset;
			}
		}
	}

	static final class Level {
		private int sidesToUpgrade;
		private final ChunkPalettedStorageFix.Section[] sections = new ChunkPalettedStorageFix.Section[16];
		private final Dynamic<?> level;
		private final int x;
		private final int z;
		private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap<>(16);

		public Level(Dynamic<?> chunkTag) {
			this.level = chunkTag;
			this.x = chunkTag.get("xPos").asInt(0) << 4;
			this.z = chunkTag.get("zPos").asInt(0) << 4;
			chunkTag.get("TileEntities").asStreamOpt().ifSuccess(stream -> stream.forEach(blockEntityTag -> {
					int ix = blockEntityTag.get("x").asInt(0) - this.x & 15;
					int jx = blockEntityTag.get("y").asInt(0);
					int k = blockEntityTag.get("z").asInt(0) - this.z & 15;
					int l = jx << 8 | k << 4 | ix;
					if (this.blockEntities.put(l, blockEntityTag) != null) {
						ChunkPalettedStorageFix.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.x, this.z, ix, jx, k);
					}
				}));
			boolean bl = chunkTag.get("convertedFromAlphaFormat").asBoolean(false);
			chunkTag.get("Sections").asStreamOpt().ifSuccess(stream -> stream.forEach(sectionTag -> {
					ChunkPalettedStorageFix.Section sectionx = new ChunkPalettedStorageFix.Section(sectionTag);
					this.sidesToUpgrade = sectionx.visit(this.sidesToUpgrade);
					this.sections[sectionx.y] = sectionx;
				}));

			for (ChunkPalettedStorageFix.Section section : this.sections) {
				if (section != null) {
					for (Entry<IntList> entry : section.inPlaceUpdates.int2ObjectEntrySet()) {
						int i = section.y << 12;
						switch (entry.getIntKey()) {
							case 2:
								for (int j : (IntList)entry.getValue()) {
									j |= i;
									Dynamic<?> dynamic = this.getBlock(j);
									if ("minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(j, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(j, ChunkPalettedStorageFix.Mapping.SNOWY_GRASS_BLOCK_STATE);
										}
									}
								}
								break;
							case 3:
								for (int jxxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlock(jxxxxxxxxx);
									if ("minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxxxx, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(jxxxxxxxxx, ChunkPalettedStorageFix.Mapping.SNOWY_PODZOL_STATE);
										}
									}
								}
								break;
							case 25:
								for (int jxxxxx : (IntList)entry.getValue()) {
									jxxxxx |= i;
									Dynamic<?> dynamic = this.removeBlockEntity(jxxxxx);
									if (dynamic != null) {
										String string = Boolean.toString(dynamic.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic.get("note").asInt(0), 0), 24);
										this.setBlock(
											jxxxxx,
											(Dynamic<?>)ChunkPalettedStorageFix.Mapping.NOTE_BLOCK_IDS_TO_STATES
												.getOrDefault(string, (Dynamic)ChunkPalettedStorageFix.Mapping.NOTE_BLOCK_IDS_TO_STATES.get("false0"))
										);
									}
								}
								break;
							case 26:
								for (int jxxxx : (IntList)entry.getValue()) {
									jxxxx |= i;
									Dynamic<?> dynamic = this.getBlockEntity(jxxxx);
									Dynamic<?> dynamic2 = this.getBlock(jxxxx);
									if (dynamic != null) {
										int k = dynamic.get("color").asInt(0);
										if (k != 14 && k >= 0 && k < 16) {
											String string2 = ChunkPalettedStorageFix.getProperty(dynamic2, "facing")
												+ ChunkPalettedStorageFix.getProperty(dynamic2, "occupied")
												+ ChunkPalettedStorageFix.getProperty(dynamic2, "part")
												+ k;
											if (ChunkPalettedStorageFix.Mapping.BED_IDS_TO_STATES.containsKey(string2)) {
												this.setBlock(jxxxx, (Dynamic<?>)ChunkPalettedStorageFix.Mapping.BED_IDS_TO_STATES.get(string2));
											}
										}
									}
								}
								break;
							case 64:
							case 71:
							case 193:
							case 194:
							case 195:
							case 196:
							case 197:
								for (int jxxx : (IntList)entry.getValue()) {
									jxxx |= i;
									Dynamic<?> dynamic = this.getBlock(jxxx);
									if (ChunkPalettedStorageFix.getName(dynamic).endsWith("_door")) {
										Dynamic<?> dynamic2 = this.getBlock(jxxx);
										if ("lower".equals(ChunkPalettedStorageFix.getProperty(dynamic2, "half"))) {
											int k = adjacentTo(jxxx, ChunkPalettedStorageFix.Facing.UP);
											Dynamic<?> dynamic3 = this.getBlock(k);
											String string4 = ChunkPalettedStorageFix.getName(dynamic2);
											if (string4.equals(ChunkPalettedStorageFix.getName(dynamic3))) {
												String string5 = ChunkPalettedStorageFix.getProperty(dynamic2, "facing");
												String string6 = ChunkPalettedStorageFix.getProperty(dynamic2, "open");
												String string7 = bl ? "left" : ChunkPalettedStorageFix.getProperty(dynamic3, "hinge");
												String string8 = bl ? "false" : ChunkPalettedStorageFix.getProperty(dynamic3, "powered");
												this.setBlock(jxxx, (Dynamic<?>)ChunkPalettedStorageFix.Mapping.DOOR_IDS_TO_STATES.get(string4 + string5 + "lower" + string7 + string6 + string8));
												this.setBlock(k, (Dynamic<?>)ChunkPalettedStorageFix.Mapping.DOOR_IDS_TO_STATES.get(string4 + string5 + "upper" + string7 + string6 + string8));
											}
										}
									}
								}
								break;
							case 86:
								for (int jxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlock(jxxxxxxxx);
									if ("minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxxx, ChunkPalettedStorageFix.Facing.DOWN)));
										if ("minecraft:grass_block".equals(string) || "minecraft:dirt".equals(string)) {
											this.setBlock(jxxxxxxxx, ChunkPalettedStorageFix.Mapping.PUMPKIN_STATE);
										}
									}
								}
								break;
							case 110:
								for (int jxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlock(jxxxxxxx);
									if ("minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxx, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(jxxxxxxx, ChunkPalettedStorageFix.Mapping.SNOWY_MYCELIUM_STATE);
										}
									}
								}
								break;
							case 140:
								for (int jxx : (IntList)entry.getValue()) {
									jxx |= i;
									Dynamic<?> dynamic = this.removeBlockEntity(jxx);
									if (dynamic != null) {
										String string = dynamic.get("Item").asString("") + dynamic.get("Data").asInt(0);
										this.setBlock(
											jxx,
											(Dynamic<?>)ChunkPalettedStorageFix.Mapping.PLANT_TO_FLOWER_POT_STATES
												.getOrDefault(string, (Dynamic)ChunkPalettedStorageFix.Mapping.PLANT_TO_FLOWER_POT_STATES.get("minecraft:air0"))
										);
									}
								}
								break;
							case 144:
								for (int jxxxxxx : (IntList)entry.getValue()) {
									jxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlockEntity(jxxxxxx);
									if (dynamic != null) {
										String string = String.valueOf(dynamic.get("SkullType").asInt(0));
										String string3 = ChunkPalettedStorageFix.getProperty(this.getBlock(jxxxxxx), "facing");
										String string2;
										if (!"up".equals(string3) && !"down".equals(string3)) {
											string2 = string + string3;
										} else {
											string2 = string + dynamic.get("Rot").asInt(0);
										}

										dynamic.remove("SkullType");
										dynamic.remove("facing");
										dynamic.remove("Rot");
										this.setBlock(
											jxxxxxx,
											(Dynamic<?>)ChunkPalettedStorageFix.Mapping.SKULL_IDS_TO_STATES
												.getOrDefault(string2, (Dynamic)ChunkPalettedStorageFix.Mapping.SKULL_IDS_TO_STATES.get("0north"))
										);
									}
								}
								break;
							case 175:
								for (int jx : (IntList)entry.getValue()) {
									jx |= i;
									Dynamic<?> dynamic = this.getBlock(jx);
									if ("upper".equals(ChunkPalettedStorageFix.getProperty(dynamic, "half"))) {
										Dynamic<?> dynamic2 = this.getBlock(adjacentTo(jx, ChunkPalettedStorageFix.Facing.DOWN));
										String string3 = ChunkPalettedStorageFix.getName(dynamic2);
										switch (string3) {
											case "minecraft:sunflower":
												this.setBlock(jx, ChunkPalettedStorageFix.Mapping.UPPER_HALF_SUNFLOWER_STATE);
												break;
											case "minecraft:lilac":
												this.setBlock(jx, ChunkPalettedStorageFix.Mapping.UPPER_HALF_LILAC_STATE);
												break;
											case "minecraft:tall_grass":
												this.setBlock(jx, ChunkPalettedStorageFix.Mapping.UPPER_HALF_TALL_GRASS_STATE);
												break;
											case "minecraft:large_fern":
												this.setBlock(jx, ChunkPalettedStorageFix.Mapping.UPPER_HALF_LARGE_FERN_STATE);
												break;
											case "minecraft:rose_bush":
												this.setBlock(jx, ChunkPalettedStorageFix.Mapping.UPPER_HALF_ROSE_BUSH_STATE);
												break;
											case "minecraft:peony":
												this.setBlock(jx, ChunkPalettedStorageFix.Mapping.UPPER_HALF_PEONY_STATE);
										}
									}
								}
								break;
							case 176:
							case 177:
								for (int jxxxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlockEntity(jxxxxxxxxxx);
									Dynamic<?> dynamic2 = this.getBlock(jxxxxxxxxxx);
									if (dynamic != null) {
										int k = dynamic.get("Base").asInt(0);
										if (k != 15 && k >= 0 && k < 16) {
											String string2 = ChunkPalettedStorageFix.getProperty(dynamic2, entry.getIntKey() == 176 ? "rotation" : "facing") + "_" + k;
											if (ChunkPalettedStorageFix.Mapping.BANNER_IDS_TO_STATES.containsKey(string2)) {
												this.setBlock(jxxxxxxxxxx, (Dynamic<?>)ChunkPalettedStorageFix.Mapping.BANNER_IDS_TO_STATES.get(string2));
											}
										}
									}
								}
						}
					}
				}
			}
		}

		@Nullable
		private Dynamic<?> getBlockEntity(int packedLocalPos) {
			return this.blockEntities.get(packedLocalPos);
		}

		@Nullable
		private Dynamic<?> removeBlockEntity(int packedLocalPos) {
			return this.blockEntities.remove(packedLocalPos);
		}

		public static int adjacentTo(int packedLocalPos, ChunkPalettedStorageFix.Facing direction) {
			int var10000;
			switch (direction.getAxis()) {
				case X: {
					int i = (packedLocalPos & 15) + direction.getDirection().getOffset();
					var10000 = i >= 0 && i <= 15 ? packedLocalPos & -16 | i : -1;
					break;
				}
				case Y: {
					int i = (packedLocalPos >> 8) + direction.getDirection().getOffset();
					var10000 = i >= 0 && i <= 255 ? packedLocalPos & 0xFF | i << 8 : -1;
					break;
				}
				case Z: {
					int i = (packedLocalPos >> 4 & 15) + direction.getDirection().getOffset();
					var10000 = i >= 0 && i <= 15 ? packedLocalPos & -241 | i << 4 : -1;
					break;
				}
				default:
					throw new MatchException(null, null);
			}

			return var10000;
		}

		private void setBlock(int packedLocalPos, Dynamic<?> dynamic) {
			if (packedLocalPos >= 0 && packedLocalPos <= 65535) {
				ChunkPalettedStorageFix.Section section = this.getSection(packedLocalPos);
				if (section != null) {
					section.setBlock(packedLocalPos & 4095, dynamic);
				}
			}
		}

		@Nullable
		private ChunkPalettedStorageFix.Section getSection(int packedLocalPos) {
			int i = packedLocalPos >> 12;
			return i < this.sections.length ? this.sections[i] : null;
		}

		public Dynamic<?> getBlock(int packedLocalPos) {
			if (packedLocalPos >= 0 && packedLocalPos <= 65535) {
				ChunkPalettedStorageFix.Section section = this.getSection(packedLocalPos);
				return section == null ? ChunkPalettedStorageFix.Mapping.AIR_STATE : section.getBlock(packedLocalPos & 4095);
			} else {
				return ChunkPalettedStorageFix.Mapping.AIR_STATE;
			}
		}

		public Dynamic<?> transform() {
			Dynamic<?> dynamic = this.level;
			if (this.blockEntities.isEmpty()) {
				dynamic = dynamic.remove("TileEntities");
			} else {
				dynamic = dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
			}

			Dynamic<?> dynamic2 = dynamic.emptyMap();
			List<Dynamic<?>> list = Lists.<Dynamic<?>>newArrayList();

			for (ChunkPalettedStorageFix.Section section : this.sections) {
				if (section != null) {
					list.add(section.transform());
					dynamic2 = dynamic2.set(String.valueOf(section.y), dynamic2.createIntList(Arrays.stream(section.innerPositions.toIntArray())));
				}
			}

			Dynamic<?> dynamic3 = dynamic.emptyMap();
			dynamic3 = dynamic3.set("Sides", dynamic3.createByte((byte)this.sidesToUpgrade));
			dynamic3 = dynamic3.set("Indices", dynamic2);
			return dynamic.set("UpgradeData", dynamic3).set("Sections", dynamic3.createList(list.stream()));
		}
	}

	static class Mapping {
		static final BitSet field_52401 = new BitSet(256);
		static final BitSet field_52402 = new BitSet(256);
		static final Dynamic<?> PUMPKIN_STATE = FixUtil.createBlockState("minecraft:pumpkin");
		static final Dynamic<?> SNOWY_PODZOL_STATE = FixUtil.createBlockState("minecraft:podzol", Map.of("snowy", "true"));
		static final Dynamic<?> SNOWY_GRASS_BLOCK_STATE = FixUtil.createBlockState("minecraft:grass_block", Map.of("snowy", "true"));
		static final Dynamic<?> SNOWY_MYCELIUM_STATE = FixUtil.createBlockState("minecraft:mycelium", Map.of("snowy", "true"));
		static final Dynamic<?> UPPER_HALF_SUNFLOWER_STATE = FixUtil.createBlockState("minecraft:sunflower", Map.of("half", "upper"));
		static final Dynamic<?> UPPER_HALF_LILAC_STATE = FixUtil.createBlockState("minecraft:lilac", Map.of("half", "upper"));
		static final Dynamic<?> UPPER_HALF_TALL_GRASS_STATE = FixUtil.createBlockState("minecraft:tall_grass", Map.of("half", "upper"));
		static final Dynamic<?> UPPER_HALF_LARGE_FERN_STATE = FixUtil.createBlockState("minecraft:large_fern", Map.of("half", "upper"));
		static final Dynamic<?> UPPER_HALF_ROSE_BUSH_STATE = FixUtil.createBlockState("minecraft:rose_bush", Map.of("half", "upper"));
		static final Dynamic<?> UPPER_HALF_PEONY_STATE = FixUtil.createBlockState("minecraft:peony", Map.of("half", "upper"));
		static final Map<String, Dynamic<?>> PLANT_TO_FLOWER_POT_STATES = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
			map.put("minecraft:air0", FixUtil.createBlockState("minecraft:flower_pot"));
			map.put("minecraft:red_flower0", FixUtil.createBlockState("minecraft:potted_poppy"));
			map.put("minecraft:red_flower1", FixUtil.createBlockState("minecraft:potted_blue_orchid"));
			map.put("minecraft:red_flower2", FixUtil.createBlockState("minecraft:potted_allium"));
			map.put("minecraft:red_flower3", FixUtil.createBlockState("minecraft:potted_azure_bluet"));
			map.put("minecraft:red_flower4", FixUtil.createBlockState("minecraft:potted_red_tulip"));
			map.put("minecraft:red_flower5", FixUtil.createBlockState("minecraft:potted_orange_tulip"));
			map.put("minecraft:red_flower6", FixUtil.createBlockState("minecraft:potted_white_tulip"));
			map.put("minecraft:red_flower7", FixUtil.createBlockState("minecraft:potted_pink_tulip"));
			map.put("minecraft:red_flower8", FixUtil.createBlockState("minecraft:potted_oxeye_daisy"));
			map.put("minecraft:yellow_flower0", FixUtil.createBlockState("minecraft:potted_dandelion"));
			map.put("minecraft:sapling0", FixUtil.createBlockState("minecraft:potted_oak_sapling"));
			map.put("minecraft:sapling1", FixUtil.createBlockState("minecraft:potted_spruce_sapling"));
			map.put("minecraft:sapling2", FixUtil.createBlockState("minecraft:potted_birch_sapling"));
			map.put("minecraft:sapling3", FixUtil.createBlockState("minecraft:potted_jungle_sapling"));
			map.put("minecraft:sapling4", FixUtil.createBlockState("minecraft:potted_acacia_sapling"));
			map.put("minecraft:sapling5", FixUtil.createBlockState("minecraft:potted_dark_oak_sapling"));
			map.put("minecraft:red_mushroom0", FixUtil.createBlockState("minecraft:potted_red_mushroom"));
			map.put("minecraft:brown_mushroom0", FixUtil.createBlockState("minecraft:potted_brown_mushroom"));
			map.put("minecraft:deadbush0", FixUtil.createBlockState("minecraft:potted_dead_bush"));
			map.put("minecraft:tallgrass2", FixUtil.createBlockState("minecraft:potted_fern"));
			map.put("minecraft:cactus0", FixUtil.createBlockState("minecraft:potted_cactus"));
		});
		static final Map<String, Dynamic<?>> SKULL_IDS_TO_STATES = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
			skull(map, 0, "skeleton", "skull");
			skull(map, 1, "wither_skeleton", "skull");
			skull(map, 2, "zombie", "head");
			skull(map, 3, "player", "head");
			skull(map, 4, "creeper", "head");
			skull(map, 5, "dragon", "head");
		});
		static final Map<String, Dynamic<?>> DOOR_IDS_TO_STATES = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
			door(map, "oak_door");
			door(map, "iron_door");
			door(map, "spruce_door");
			door(map, "birch_door");
			door(map, "jungle_door");
			door(map, "acacia_door");
			door(map, "dark_oak_door");
		});
		static final Map<String, Dynamic<?>> NOTE_BLOCK_IDS_TO_STATES = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
			for (int i = 0; i < 26; i++) {
				map.put("true" + i, FixUtil.createBlockState("minecraft:note_block", Map.of("powered", "true", "note", String.valueOf(i))));
				map.put("false" + i, FixUtil.createBlockState("minecraft:note_block", Map.of("powered", "false", "note", String.valueOf(i))));
			}
		});
		private static final Int2ObjectMap<String> COLORS_BY_IDS = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), map -> {
			map.put(0, "white");
			map.put(1, "orange");
			map.put(2, "magenta");
			map.put(3, "light_blue");
			map.put(4, "yellow");
			map.put(5, "lime");
			map.put(6, "pink");
			map.put(7, "gray");
			map.put(8, "light_gray");
			map.put(9, "cyan");
			map.put(10, "purple");
			map.put(11, "blue");
			map.put(12, "brown");
			map.put(13, "green");
			map.put(14, "red");
			map.put(15, "black");
		});
		static final Map<String, Dynamic<?>> BED_IDS_TO_STATES = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
			for (Entry<String> entry : COLORS_BY_IDS.int2ObjectEntrySet()) {
				if (!Objects.equals(entry.getValue(), "red")) {
					bed(map, entry.getIntKey(), (String)entry.getValue());
				}
			}
		});
		static final Map<String, Dynamic<?>> BANNER_IDS_TO_STATES = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
			for (Entry<String> entry : COLORS_BY_IDS.int2ObjectEntrySet()) {
				if (!Objects.equals(entry.getValue(), "white")) {
					banner(map, 15 - entry.getIntKey(), (String)entry.getValue());
				}
			}
		});
		static final Dynamic<?> AIR_STATE = FixUtil.createBlockState("minecraft:air");

		private Mapping() {
		}

		private static void skull(Map<String, Dynamic<?>> map, int id, String entity, String type) {
			map.put(id + "north", FixUtil.createBlockState("minecraft:" + entity + "_wall_" + type, Map.of("facing", "north")));
			map.put(id + "east", FixUtil.createBlockState("minecraft:" + entity + "_wall_" + type, Map.of("facing", "east")));
			map.put(id + "south", FixUtil.createBlockState("minecraft:" + entity + "_wall_" + type, Map.of("facing", "south")));
			map.put(id + "west", FixUtil.createBlockState("minecraft:" + entity + "_wall_" + type, Map.of("facing", "west")));

			for (int i = 0; i < 16; i++) {
				map.put("" + id + i, FixUtil.createBlockState("minecraft:" + entity + "_" + type, Map.of("rotation", String.valueOf(i))));
			}
		}

		private static void door(Map<String, Dynamic<?>> map, String id) {
			String string = "minecraft:" + id;
			map.put(
				"minecraft:" + id + "eastlowerleftfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "eastlowerleftfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "eastlowerlefttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "eastlowerlefttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "eastlowerrightfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "eastlowerrightfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "eastlowerrighttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "eastlowerrighttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "eastupperleftfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "eastupperleftfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "eastupperlefttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "eastupperlefttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "eastupperrightfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "eastupperrightfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "eastupperrighttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "eastupperrighttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "northlowerleftfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "northlowerleftfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "northlowerlefttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "northlowerlefttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "northlowerrightfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "northlowerrightfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "northlowerrighttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "northlowerrighttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "northupperleftfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "northupperleftfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "northupperlefttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "northupperlefttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "northupperrightfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "northupperrightfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "northupperrighttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "northupperrighttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "southlowerleftfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "southlowerleftfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "southlowerlefttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "southlowerlefttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "southlowerrightfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "southlowerrightfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "southlowerrighttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "southlowerrighttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "southupperleftfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "southupperleftfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "southupperlefttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "southupperlefttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "southupperrightfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "southupperrightfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "southupperrighttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "southupperrighttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "westlowerleftfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "westlowerleftfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "westlowerlefttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "westlowerlefttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "westlowerrightfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "westlowerrightfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "westlowerrighttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "westlowerrighttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "westupperleftfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "westupperleftfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "westupperlefttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "westupperlefttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "westupperrightfalsefalse",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "westupperrightfalsetrue",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
			);
			map.put(
				"minecraft:" + id + "westupperrighttruefalse",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
			);
			map.put(
				"minecraft:" + id + "westupperrighttruetrue",
				FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
			);
		}

		private static void bed(Map<String, Dynamic<?>> map, int id, String color) {
			map.put("southfalsefoot" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "south", "occupied", "false", "part", "foot")));
			map.put("westfalsefoot" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "west", "occupied", "false", "part", "foot")));
			map.put("northfalsefoot" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "north", "occupied", "false", "part", "foot")));
			map.put("eastfalsefoot" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "east", "occupied", "false", "part", "foot")));
			map.put("southfalsehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "south", "occupied", "false", "part", "head")));
			map.put("westfalsehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "west", "occupied", "false", "part", "head")));
			map.put("northfalsehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "north", "occupied", "false", "part", "head")));
			map.put("eastfalsehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "east", "occupied", "false", "part", "head")));
			map.put("southtruehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "south", "occupied", "true", "part", "head")));
			map.put("westtruehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "west", "occupied", "true", "part", "head")));
			map.put("northtruehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "north", "occupied", "true", "part", "head")));
			map.put("easttruehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "east", "occupied", "true", "part", "head")));
		}

		private static void banner(Map<String, Dynamic<?>> map, int id, String color) {
			for (int i = 0; i < 16; i++) {
				map.put(i + "_" + id, FixUtil.createBlockState("minecraft:" + color + "_banner", Map.of("rotation", String.valueOf(i))));
			}

			map.put("north_" + id, FixUtil.createBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "north")));
			map.put("south_" + id, FixUtil.createBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "south")));
			map.put("west_" + id, FixUtil.createBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "west")));
			map.put("east_" + id, FixUtil.createBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "east")));
		}

		static {
			field_52402.set(2);
			field_52402.set(3);
			field_52402.set(110);
			field_52402.set(140);
			field_52402.set(144);
			field_52402.set(25);
			field_52402.set(86);
			field_52402.set(26);
			field_52402.set(176);
			field_52402.set(177);
			field_52402.set(175);
			field_52402.set(64);
			field_52402.set(71);
			field_52402.set(193);
			field_52402.set(194);
			field_52402.set(195);
			field_52402.set(196);
			field_52402.set(197);
			field_52401.set(54);
			field_52401.set(146);
			field_52401.set(25);
			field_52401.set(26);
			field_52401.set(51);
			field_52401.set(53);
			field_52401.set(67);
			field_52401.set(108);
			field_52401.set(109);
			field_52401.set(114);
			field_52401.set(128);
			field_52401.set(134);
			field_52401.set(135);
			field_52401.set(136);
			field_52401.set(156);
			field_52401.set(163);
			field_52401.set(164);
			field_52401.set(180);
			field_52401.set(203);
			field_52401.set(55);
			field_52401.set(85);
			field_52401.set(113);
			field_52401.set(188);
			field_52401.set(189);
			field_52401.set(190);
			field_52401.set(191);
			field_52401.set(192);
			field_52401.set(93);
			field_52401.set(94);
			field_52401.set(101);
			field_52401.set(102);
			field_52401.set(160);
			field_52401.set(106);
			field_52401.set(107);
			field_52401.set(183);
			field_52401.set(184);
			field_52401.set(185);
			field_52401.set(186);
			field_52401.set(187);
			field_52401.set(132);
			field_52401.set(139);
			field_52401.set(199);
		}
	}

	static class Section {
		private final Int2ObjectBiMap<Dynamic<?>> paletteMap = Int2ObjectBiMap.create(32);
		private final List<Dynamic<?>> paletteData;
		private final Dynamic<?> section;
		private final boolean hasBlocks;
		final Int2ObjectMap<IntList> inPlaceUpdates = new Int2ObjectLinkedOpenHashMap<>();
		final IntList innerPositions = new IntArrayList();
		public final int y;
		private final Set<Dynamic<?>> seenStates = Sets.newIdentityHashSet();
		private final int[] states = new int[4096];

		public Section(Dynamic<?> section) {
			this.paletteData = Lists.<Dynamic<?>>newArrayList();
			this.section = section;
			this.y = section.get("Y").asInt(0);
			this.hasBlocks = section.get("Blocks").result().isPresent();
		}

		public Dynamic<?> getBlock(int index) {
			if (index >= 0 && index <= 4095) {
				Dynamic<?> dynamic = this.paletteMap.get(this.states[index]);
				return dynamic == null ? ChunkPalettedStorageFix.Mapping.AIR_STATE : dynamic;
			} else {
				return ChunkPalettedStorageFix.Mapping.AIR_STATE;
			}
		}

		public void setBlock(int pos, Dynamic<?> dynamic) {
			if (this.seenStates.add(dynamic)) {
				this.paletteData.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? ChunkPalettedStorageFix.Mapping.AIR_STATE : dynamic);
			}

			this.states[pos] = ChunkPalettedStorageFix.addTo(this.paletteMap, dynamic);
		}

		public int visit(int sidesToUpgrade) {
			if (!this.hasBlocks) {
				return sidesToUpgrade;
			} else {
				ByteBuffer byteBuffer = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Data")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.result()
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray2 = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Add")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.result()
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				this.seenStates.add(ChunkPalettedStorageFix.Mapping.AIR_STATE);
				ChunkPalettedStorageFix.addTo(this.paletteMap, ChunkPalettedStorageFix.Mapping.AIR_STATE);
				this.paletteData.add(ChunkPalettedStorageFix.Mapping.AIR_STATE);

				for (int i = 0; i < 4096; i++) {
					int j = i & 15;
					int k = i >> 8 & 15;
					int l = i >> 4 & 15;
					int m = chunkNibbleArray2.get(j, k, l) << 12 | (byteBuffer.get(i) & 255) << 4 | chunkNibbleArray.get(j, k, l);
					if (ChunkPalettedStorageFix.Mapping.field_52402.get(m >> 4)) {
						this.addInPlaceUpdate(m >> 4, i);
					}

					if (ChunkPalettedStorageFix.Mapping.field_52401.get(m >> 4)) {
						int n = ChunkPalettedStorageFix.getSideToUpgradeFlag(j == 0, j == 15, l == 0, l == 15);
						if (n == 0) {
							this.innerPositions.add(i);
						} else {
							sidesToUpgrade |= n;
						}
					}

					this.setBlock(i, BlockStateFlattening.lookupState(m));
				}

				return sidesToUpgrade;
			}
		}

		private void addInPlaceUpdate(int section, int index) {
			IntList intList = this.inPlaceUpdates.get(section);
			if (intList == null) {
				intList = new IntArrayList();
				this.inPlaceUpdates.put(section, intList);
			}

			intList.add(index);
		}

		public Dynamic<?> transform() {
			Dynamic<?> dynamic = this.section;
			if (!this.hasBlocks) {
				return dynamic;
			} else {
				dynamic = dynamic.set("Palette", dynamic.createList(this.paletteData.stream()));
				int i = Math.max(4, DataFixUtils.ceillog2(this.seenStates.size()));
				WordPackedArray wordPackedArray = new WordPackedArray(i, 4096);

				for (int j = 0; j < this.states.length; j++) {
					wordPackedArray.set(j, this.states[j]);
				}

				dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(wordPackedArray.getAlignedArray())));
				dynamic = dynamic.remove("Blocks");
				dynamic = dynamic.remove("Data");
				return dynamic.remove("Add");
			}
		}
	}
}
