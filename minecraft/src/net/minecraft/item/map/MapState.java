package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapState extends PersistentState {
	private static final Logger field_25019 = LogManager.getLogger();
	public final int xCenter;
	public final int zCenter;
	public final RegistryKey<World> dimension;
	private final boolean showIcons;
	private final boolean unlimitedTracking;
	public final byte scale;
	public byte[] colors = new byte[16384];
	public final boolean locked;
	private final List<MapState.PlayerUpdateTracker> updateTrackers = Lists.<MapState.PlayerUpdateTracker>newArrayList();
	private final Map<PlayerEntity, MapState.PlayerUpdateTracker> updateTrackersByPlayer = Maps.<PlayerEntity, MapState.PlayerUpdateTracker>newHashMap();
	private final Map<String, MapBannerMarker> banners = Maps.<String, MapBannerMarker>newHashMap();
	private final Map<String, MapIcon> icons = Maps.<String, MapIcon>newLinkedHashMap();
	private final Map<String, MapFrameMarker> frames = Maps.<String, MapFrameMarker>newHashMap();

	private MapState(int i, int j, byte b, boolean bl, boolean bl2, boolean bl3, RegistryKey<World> registryKey) {
		this.scale = b;
		this.xCenter = i;
		this.zCenter = j;
		this.dimension = registryKey;
		this.showIcons = bl;
		this.unlimitedTracking = bl2;
		this.locked = bl3;
		this.markDirty();
	}

	public static MapState method_32363(double d, double e, byte b, boolean bl, boolean bl2, RegistryKey<World> registryKey) {
		int i = 128 * (1 << b);
		int j = MathHelper.floor((d + 64.0) / (double)i);
		int k = MathHelper.floor((e + 64.0) / (double)i);
		int l = j * i + i / 2 - 64;
		int m = k * i + i / 2 - 64;
		return new MapState(l, m, b, bl, bl2, false, registryKey);
	}

	@Environment(EnvType.CLIENT)
	public static MapState method_32362(byte b, boolean bl, RegistryKey<World> registryKey) {
		return new MapState(0, 0, b, false, false, bl, registryKey);
	}

	public static MapState method_32371(CompoundTag compoundTag) {
		RegistryKey<World> registryKey = (RegistryKey<World>)DimensionType.method_28521(new Dynamic<>(NbtOps.INSTANCE, compoundTag.get("dimension")))
			.resultOrPartial(field_25019::error)
			.orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + compoundTag.get("dimension")));
		int i = compoundTag.getInt("xCenter");
		int j = compoundTag.getInt("zCenter");
		byte b = (byte)MathHelper.clamp(compoundTag.getByte("scale"), 0, 4);
		boolean bl = !compoundTag.contains("trackingPosition", 1) || compoundTag.getBoolean("trackingPosition");
		boolean bl2 = compoundTag.getBoolean("unlimitedTracking");
		boolean bl3 = compoundTag.getBoolean("locked");
		MapState mapState = new MapState(i, j, b, bl, bl2, bl3, registryKey);
		byte[] bs = compoundTag.getByteArray("colors");
		if (bs.length == 16384) {
			mapState.colors = bs;
		}

		ListTag listTag = compoundTag.getList("banners", 10);

		for (int k = 0; k < listTag.size(); k++) {
			MapBannerMarker mapBannerMarker = MapBannerMarker.fromNbt(listTag.getCompound(k));
			mapState.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
			mapState.addIcon(
				mapBannerMarker.getIconType(),
				null,
				mapBannerMarker.getKey(),
				(double)mapBannerMarker.getPos().getX(),
				(double)mapBannerMarker.getPos().getZ(),
				180.0,
				mapBannerMarker.getName()
			);
		}

		ListTag listTag2 = compoundTag.getList("frames", 10);

		for (int l = 0; l < listTag2.size(); l++) {
			MapFrameMarker mapFrameMarker = MapFrameMarker.fromTag(listTag2.getCompound(l));
			mapState.frames.put(mapFrameMarker.getKey(), mapFrameMarker);
			mapState.addIcon(
				MapIcon.Type.FRAME,
				null,
				"frame-" + mapFrameMarker.getEntityId(),
				(double)mapFrameMarker.getPos().getX(),
				(double)mapFrameMarker.getPos().getZ(),
				(double)mapFrameMarker.getRotation(),
				null
			);
		}

		return mapState;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Identifier.CODEC.encodeStart(NbtOps.INSTANCE, this.dimension.getValue()).resultOrPartial(field_25019::error).ifPresent(tagx -> tag.put("dimension", tagx));
		tag.putInt("xCenter", this.xCenter);
		tag.putInt("zCenter", this.zCenter);
		tag.putByte("scale", this.scale);
		tag.putByteArray("colors", this.colors);
		tag.putBoolean("trackingPosition", this.showIcons);
		tag.putBoolean("unlimitedTracking", this.unlimitedTracking);
		tag.putBoolean("locked", this.locked);
		ListTag listTag = new ListTag();

		for (MapBannerMarker mapBannerMarker : this.banners.values()) {
			listTag.add(mapBannerMarker.getNbt());
		}

		tag.put("banners", listTag);
		ListTag listTag2 = new ListTag();

		for (MapFrameMarker mapFrameMarker : this.frames.values()) {
			listTag2.add(mapFrameMarker.toTag());
		}

		tag.put("frames", listTag2);
		return tag;
	}

	public MapState method_32361() {
		MapState mapState = new MapState(this.xCenter, this.zCenter, this.scale, this.showIcons, this.unlimitedTracking, true, this.dimension);
		mapState.banners.putAll(this.banners);
		mapState.icons.putAll(this.icons);
		System.arraycopy(this.colors, 0, mapState.colors, 0, this.colors.length);
		mapState.markDirty();
		return mapState;
	}

	public MapState method_32364(int i) {
		return method_32363(
			(double)this.xCenter, (double)this.zCenter, (byte)MathHelper.clamp(this.scale + i, 0, 4), this.showIcons, this.unlimitedTracking, this.dimension
		);
	}

	public void update(PlayerEntity player, ItemStack stack) {
		if (!this.updateTrackersByPlayer.containsKey(player)) {
			MapState.PlayerUpdateTracker playerUpdateTracker = new MapState.PlayerUpdateTracker(player);
			this.updateTrackersByPlayer.put(player, playerUpdateTracker);
			this.updateTrackers.add(playerUpdateTracker);
		}

		if (!player.getInventory().contains(stack)) {
			this.icons.remove(player.getName().getString());
		}

		for (int i = 0; i < this.updateTrackers.size(); i++) {
			MapState.PlayerUpdateTracker playerUpdateTracker2 = (MapState.PlayerUpdateTracker)this.updateTrackers.get(i);
			String string = playerUpdateTracker2.player.getName().getString();
			if (!playerUpdateTracker2.player.isRemoved() && (playerUpdateTracker2.player.getInventory().contains(stack) || stack.isInFrame())) {
				if (!stack.isInFrame() && playerUpdateTracker2.player.world.getRegistryKey() == this.dimension && this.showIcons) {
					this.addIcon(
						MapIcon.Type.PLAYER,
						playerUpdateTracker2.player.world,
						string,
						playerUpdateTracker2.player.getX(),
						playerUpdateTracker2.player.getZ(),
						(double)playerUpdateTracker2.player.yaw,
						null
					);
				}
			} else {
				this.updateTrackersByPlayer.remove(playerUpdateTracker2.player);
				this.updateTrackers.remove(playerUpdateTracker2);
				this.method_32368(string);
			}
		}

		if (stack.isInFrame() && this.showIcons) {
			ItemFrameEntity itemFrameEntity = stack.getFrame();
			BlockPos blockPos = itemFrameEntity.getDecorationBlockPos();
			MapFrameMarker mapFrameMarker = (MapFrameMarker)this.frames.get(MapFrameMarker.getKey(blockPos));
			if (mapFrameMarker != null && itemFrameEntity.getEntityId() != mapFrameMarker.getEntityId() && this.frames.containsKey(mapFrameMarker.getKey())) {
				this.method_32368("frame-" + mapFrameMarker.getEntityId());
			}

			MapFrameMarker mapFrameMarker2 = new MapFrameMarker(blockPos, itemFrameEntity.getHorizontalFacing().getHorizontal() * 90, itemFrameEntity.getEntityId());
			this.addIcon(
				MapIcon.Type.FRAME,
				player.world,
				"frame-" + itemFrameEntity.getEntityId(),
				(double)blockPos.getX(),
				(double)blockPos.getZ(),
				(double)(itemFrameEntity.getHorizontalFacing().getHorizontal() * 90),
				null
			);
			this.frames.put(mapFrameMarker2.getKey(), mapFrameMarker2);
		}

		CompoundTag compoundTag = stack.getTag();
		if (compoundTag != null && compoundTag.contains("Decorations", 9)) {
			ListTag listTag = compoundTag.getList("Decorations", 10);

			for (int j = 0; j < listTag.size(); j++) {
				CompoundTag compoundTag2 = listTag.getCompound(j);
				if (!this.icons.containsKey(compoundTag2.getString("id"))) {
					this.addIcon(
						MapIcon.Type.byId(compoundTag2.getByte("type")),
						player.world,
						compoundTag2.getString("id"),
						compoundTag2.getDouble("x"),
						compoundTag2.getDouble("z"),
						compoundTag2.getDouble("rot"),
						null
					);
				}
			}
		}
	}

	private void method_32368(String string) {
		this.icons.remove(string);
		this.method_32374();
	}

	public static void addDecorationsTag(ItemStack stack, BlockPos pos, String id, MapIcon.Type type) {
		ListTag listTag;
		if (stack.hasTag() && stack.getTag().contains("Decorations", 9)) {
			listTag = stack.getTag().getList("Decorations", 10);
		} else {
			listTag = new ListTag();
			stack.putSubTag("Decorations", listTag);
		}

		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putByte("type", type.getId());
		compoundTag.putString("id", id);
		compoundTag.putDouble("x", (double)pos.getX());
		compoundTag.putDouble("z", (double)pos.getZ());
		compoundTag.putDouble("rot", 180.0);
		listTag.add(compoundTag);
		if (type.hasTintColor()) {
			CompoundTag compoundTag2 = stack.getOrCreateSubTag("display");
			compoundTag2.putInt("MapColor", type.getTintColor());
		}
	}

	private void addIcon(MapIcon.Type type, @Nullable WorldAccess world, String key, double x, double z, double rotation, @Nullable Text text) {
		int i = 1 << this.scale;
		float f = (float)(x - (double)this.xCenter) / (float)i;
		float g = (float)(z - (double)this.zCenter) / (float)i;
		byte b = (byte)((int)((double)(f * 2.0F) + 0.5));
		byte c = (byte)((int)((double)(g * 2.0F) + 0.5));
		int j = 63;
		byte d;
		if (f >= -63.0F && g >= -63.0F && f <= 63.0F && g <= 63.0F) {
			rotation += rotation < 0.0 ? -8.0 : 8.0;
			d = (byte)((int)(rotation * 16.0 / 360.0));
			if (this.dimension == World.NETHER && world != null) {
				int k = (int)(world.getLevelProperties().getTimeOfDay() / 10L);
				d = (byte)(k * k * 34187121 + k * 121 >> 15 & 15);
			}
		} else {
			if (type != MapIcon.Type.PLAYER) {
				this.method_32368(key);
				return;
			}

			int k = 320;
			if (Math.abs(f) < 320.0F && Math.abs(g) < 320.0F) {
				type = MapIcon.Type.PLAYER_OFF_MAP;
			} else {
				if (!this.unlimitedTracking) {
					this.method_32368(key);
					return;
				}

				type = MapIcon.Type.PLAYER_OFF_LIMITS;
			}

			d = 0;
			if (f <= -63.0F) {
				b = -128;
			}

			if (g <= -63.0F) {
				c = -128;
			}

			if (f >= 63.0F) {
				b = 127;
			}

			if (g >= 63.0F) {
				c = 127;
			}
		}

		MapIcon mapIcon = new MapIcon(type, b, c, d, text);
		MapIcon mapIcon2 = (MapIcon)this.icons.put(key, mapIcon);
		if (!mapIcon.equals(mapIcon2)) {
			this.method_32374();
		}
	}

	@Nullable
	public Packet<?> getPlayerMarkerPacket(int i, PlayerEntity playerEntity) {
		MapState.PlayerUpdateTracker playerUpdateTracker = (MapState.PlayerUpdateTracker)this.updateTrackersByPlayer.get(playerEntity);
		return playerUpdateTracker == null ? null : playerUpdateTracker.getPacket(i);
	}

	private void markDirty(int x, int z) {
		this.markDirty();

		for (MapState.PlayerUpdateTracker playerUpdateTracker : this.updateTrackers) {
			playerUpdateTracker.markDirty(x, z);
		}
	}

	private void method_32374() {
		this.markDirty();
		this.updateTrackers.forEach(object -> ((MapState.PlayerUpdateTracker)object).method_32379());
	}

	public MapState.PlayerUpdateTracker getPlayerSyncData(PlayerEntity player) {
		MapState.PlayerUpdateTracker playerUpdateTracker = (MapState.PlayerUpdateTracker)this.updateTrackersByPlayer.get(player);
		if (playerUpdateTracker == null) {
			playerUpdateTracker = new MapState.PlayerUpdateTracker(player);
			this.updateTrackersByPlayer.put(player, playerUpdateTracker);
			this.updateTrackers.add(playerUpdateTracker);
		}

		return playerUpdateTracker;
	}

	public void addBanner(WorldAccess world, BlockPos pos) {
		double d = (double)pos.getX() + 0.5;
		double e = (double)pos.getZ() + 0.5;
		int i = 1 << this.scale;
		double f = (d - (double)this.xCenter) / (double)i;
		double g = (e - (double)this.zCenter) / (double)i;
		int j = 63;
		if (f >= -63.0 && g >= -63.0 && f <= 63.0 && g <= 63.0) {
			MapBannerMarker mapBannerMarker = MapBannerMarker.fromWorldBlock(world, pos);
			if (mapBannerMarker == null) {
				return;
			}

			if (this.banners.remove(mapBannerMarker.getKey(), mapBannerMarker)) {
				this.method_32368(mapBannerMarker.getKey());
			} else {
				this.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
				this.addIcon(mapBannerMarker.getIconType(), world, mapBannerMarker.getKey(), d, e, 180.0, mapBannerMarker.getName());
			}
		}
	}

	public void removeBanner(BlockView world, int x, int z) {
		Iterator<MapBannerMarker> iterator = this.banners.values().iterator();

		while (iterator.hasNext()) {
			MapBannerMarker mapBannerMarker = (MapBannerMarker)iterator.next();
			if (mapBannerMarker.getPos().getX() == x && mapBannerMarker.getPos().getZ() == z) {
				MapBannerMarker mapBannerMarker2 = MapBannerMarker.fromWorldBlock(world, mapBannerMarker.getPos());
				if (!mapBannerMarker.equals(mapBannerMarker2)) {
					iterator.remove();
					this.method_32368(mapBannerMarker.getKey());
				}
			}
		}
	}

	public void removeFrame(BlockPos pos, int id) {
		this.method_32368("frame-" + id);
		this.frames.remove(MapFrameMarker.getKey(pos));
	}

	public boolean method_32365(int i, int j, byte b) {
		byte c = this.colors[i + j * 128];
		if (c != b) {
			this.method_32370(i, j, b);
			return true;
		} else {
			return false;
		}
	}

	public void method_32370(int i, int j, byte b) {
		this.colors[i + j * 128] = b;
		this.markDirty(i, j);
	}

	public boolean method_32372() {
		for (MapIcon mapIcon : this.icons.values()) {
			if (mapIcon.getType() == MapIcon.Type.MANSION || mapIcon.getType() == MapIcon.Type.MONUMENT) {
				return true;
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	public void method_32369(MapIcon[] mapIcons) {
		this.icons.clear();

		for (int i = 0; i < mapIcons.length; i++) {
			MapIcon mapIcon = mapIcons[i];
			this.icons.put("icon-" + i, mapIcon);
		}
	}

	@Environment(EnvType.CLIENT)
	public Iterable<MapIcon> method_32373() {
		return this.icons.values();
	}

	public class PlayerUpdateTracker {
		public final PlayerEntity player;
		private boolean dirty = true;
		private int startX;
		private int startZ;
		private int endX = 127;
		private int endZ = 127;
		private boolean field_27891 = true;
		private int emptyPacketsRequested;
		public int field_131;

		private PlayerUpdateTracker(PlayerEntity playerEntity) {
			this.player = playerEntity;
		}

		private MapState.class_5637 method_32375() {
			int i = this.startX;
			int j = this.startZ;
			int k = this.endX + 1 - this.startX;
			int l = this.endZ + 1 - this.startZ;
			byte[] bs = new byte[k * l];

			for (int m = 0; m < k; m++) {
				for (int n = 0; n < l; n++) {
					bs[m + n * k] = MapState.this.colors[i + m + (j + n) * 128];
				}
			}

			return new MapState.class_5637(i, j, k, l, bs);
		}

		@Nullable
		private Packet<?> getPacket(int i) {
			MapState.class_5637 lv;
			if (this.dirty) {
				this.dirty = false;
				lv = this.method_32375();
			} else {
				lv = null;
			}

			Collection<MapIcon> collection;
			if (this.field_27891 && this.emptyPacketsRequested++ % 5 == 0) {
				this.field_27891 = false;
				collection = MapState.this.icons.values();
			} else {
				collection = null;
			}

			return collection == null && lv == null ? null : new MapUpdateS2CPacket(i, MapState.this.scale, MapState.this.locked, collection, lv);
		}

		private void markDirty(int x, int z) {
			if (this.dirty) {
				this.startX = Math.min(this.startX, x);
				this.startZ = Math.min(this.startZ, z);
				this.endX = Math.max(this.endX, x);
				this.endZ = Math.max(this.endZ, z);
			} else {
				this.dirty = true;
				this.startX = x;
				this.startZ = z;
				this.endX = x;
				this.endZ = z;
			}
		}

		private void method_32379() {
			this.field_27891 = true;
		}
	}

	public static class class_5637 {
		public final int field_27892;
		public final int field_27893;
		public final int field_27894;
		public final int field_27895;
		public final byte[] field_27896;

		public class_5637(int i, int j, int k, int l, byte[] bs) {
			this.field_27892 = i;
			this.field_27893 = j;
			this.field_27894 = k;
			this.field_27895 = l;
			this.field_27896 = bs;
		}

		@Environment(EnvType.CLIENT)
		public void method_32380(MapState mapState) {
			for (int i = 0; i < this.field_27894; i++) {
				for (int j = 0; j < this.field_27895; j++) {
					mapState.method_32370(this.field_27892 + i, this.field_27893 + j, this.field_27896[i + j * this.field_27894]);
				}
			}
		}
	}
}
