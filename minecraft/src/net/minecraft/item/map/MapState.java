package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.MapUpdateS2CPacket;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;

public class MapState extends PersistentState {
	public int xCenter;
	public int zCenter;
	public DimensionType dimension;
	public boolean showIcons;
	public boolean unlimitedTracking;
	public byte scale;
	public byte[] colors = new byte[16384];
	public boolean locked;
	public final List<MapState.PlayerUpdateTracker> updateTrackers = Lists.<MapState.PlayerUpdateTracker>newArrayList();
	private final Map<PlayerEntity, MapState.PlayerUpdateTracker> updateTrackersByPlayer = Maps.<PlayerEntity, MapState.PlayerUpdateTracker>newHashMap();
	private final Map<String, MapBannerMarker> banners = Maps.<String, MapBannerMarker>newHashMap();
	public final Map<String, MapIcon> icons = Maps.<String, MapIcon>newLinkedHashMap();
	private final Map<String, MapFrameMarker> frames = Maps.<String, MapFrameMarker>newHashMap();

	public MapState(String string) {
		super(string);
	}

	public void init(int i, int j, int k, boolean bl, boolean bl2, DimensionType dimensionType) {
		this.scale = (byte)k;
		this.calculateCenter((double)i, (double)j, this.scale);
		this.dimension = dimensionType;
		this.showIcons = bl;
		this.unlimitedTracking = bl2;
		this.markDirty();
	}

	public void calculateCenter(double d, double e, int i) {
		int j = 128 * (1 << i);
		int k = MathHelper.floor((d + 64.0) / (double)j);
		int l = MathHelper.floor((e + 64.0) / (double)j);
		this.xCenter = k * j + j / 2 - 64;
		this.zCenter = l * j + j / 2 - 64;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		this.dimension = DimensionType.byRawId(compoundTag.getInt("dimension"));
		this.xCenter = compoundTag.getInt("xCenter");
		this.zCenter = compoundTag.getInt("zCenter");
		this.scale = (byte)MathHelper.clamp(compoundTag.getByte("scale"), 0, 4);
		this.showIcons = !compoundTag.containsKey("trackingPosition", 1) || compoundTag.getBoolean("trackingPosition");
		this.unlimitedTracking = compoundTag.getBoolean("unlimitedTracking");
		this.locked = compoundTag.getBoolean("locked");
		this.colors = compoundTag.getByteArray("colors");
		if (this.colors.length != 16384) {
			this.colors = new byte[16384];
		}

		ListTag listTag = compoundTag.getList("banners", 10);

		for (int i = 0; i < listTag.size(); i++) {
			MapBannerMarker mapBannerMarker = MapBannerMarker.fromNbt(listTag.getCompoundTag(i));
			this.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
			this.method_107(
				mapBannerMarker.getIconType(),
				null,
				mapBannerMarker.getKey(),
				(double)mapBannerMarker.getPos().getX(),
				(double)mapBannerMarker.getPos().getZ(),
				180.0,
				mapBannerMarker.method_68()
			);
		}

		ListTag listTag2 = compoundTag.getList("frames", 10);

		for (int j = 0; j < listTag2.size(); j++) {
			MapFrameMarker mapFrameMarker = MapFrameMarker.fromTag(listTag2.getCompoundTag(j));
			this.frames.put(mapFrameMarker.getKey(), mapFrameMarker);
			this.method_107(
				MapIcon.Type.field_95,
				null,
				"frame-" + mapFrameMarker.getEntityId(),
				(double)mapFrameMarker.getPos().getX(),
				(double)mapFrameMarker.getPos().getZ(),
				(double)mapFrameMarker.getRotation(),
				null
			);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		compoundTag.putInt("dimension", this.dimension.getRawId());
		compoundTag.putInt("xCenter", this.xCenter);
		compoundTag.putInt("zCenter", this.zCenter);
		compoundTag.putByte("scale", this.scale);
		compoundTag.putByteArray("colors", this.colors);
		compoundTag.putBoolean("trackingPosition", this.showIcons);
		compoundTag.putBoolean("unlimitedTracking", this.unlimitedTracking);
		compoundTag.putBoolean("locked", this.locked);
		ListTag listTag = new ListTag();

		for (MapBannerMarker mapBannerMarker : this.banners.values()) {
			listTag.add(mapBannerMarker.getNbt());
		}

		compoundTag.put("banners", listTag);
		ListTag listTag2 = new ListTag();

		for (MapFrameMarker mapFrameMarker : this.frames.values()) {
			listTag2.add(mapFrameMarker.toTag());
		}

		compoundTag.put("frames", listTag2);
		return compoundTag;
	}

	public void copyFrom(MapState mapState) {
		this.locked = true;
		this.xCenter = mapState.xCenter;
		this.zCenter = mapState.zCenter;
		this.banners.putAll(mapState.banners);
		this.icons.putAll(mapState.icons);
		System.arraycopy(mapState.colors, 0, this.colors, 0, mapState.colors.length);
		this.markDirty();
	}

	public void update(PlayerEntity playerEntity, ItemStack itemStack) {
		if (!this.updateTrackersByPlayer.containsKey(playerEntity)) {
			MapState.PlayerUpdateTracker playerUpdateTracker = new MapState.PlayerUpdateTracker(playerEntity);
			this.updateTrackersByPlayer.put(playerEntity, playerUpdateTracker);
			this.updateTrackers.add(playerUpdateTracker);
		}

		if (!playerEntity.inventory.contains(itemStack)) {
			this.icons.remove(playerEntity.method_5477().getString());
		}

		for (int i = 0; i < this.updateTrackers.size(); i++) {
			MapState.PlayerUpdateTracker playerUpdateTracker2 = (MapState.PlayerUpdateTracker)this.updateTrackers.get(i);
			String string = playerUpdateTracker2.player.method_5477().getString();
			if (!playerUpdateTracker2.player.removed && (playerUpdateTracker2.player.inventory.contains(itemStack) || itemStack.isInFrame())) {
				if (!itemStack.isInFrame() && playerUpdateTracker2.player.dimension == this.dimension && this.showIcons) {
					this.method_107(
						MapIcon.Type.field_91,
						playerUpdateTracker2.player.world,
						string,
						playerUpdateTracker2.player.x,
						playerUpdateTracker2.player.z,
						(double)playerUpdateTracker2.player.yaw,
						null
					);
				}
			} else {
				this.updateTrackersByPlayer.remove(playerUpdateTracker2.player);
				this.updateTrackers.remove(playerUpdateTracker2);
				this.icons.remove(string);
			}
		}

		if (itemStack.isInFrame() && this.showIcons) {
			ItemFrameEntity itemFrameEntity = itemStack.getFrame();
			BlockPos blockPos = itemFrameEntity.getDecorationBlockPos();
			MapFrameMarker mapFrameMarker = (MapFrameMarker)this.frames.get(MapFrameMarker.getKey(blockPos));
			if (mapFrameMarker != null && itemFrameEntity.getEntityId() != mapFrameMarker.getEntityId() && this.frames.containsKey(mapFrameMarker.getKey())) {
				this.icons.remove("frame-" + mapFrameMarker.getEntityId());
			}

			MapFrameMarker mapFrameMarker2 = new MapFrameMarker(blockPos, itemFrameEntity.facing.getHorizontal() * 90, itemFrameEntity.getEntityId());
			this.method_107(
				MapIcon.Type.field_95,
				playerEntity.world,
				"frame-" + itemFrameEntity.getEntityId(),
				(double)blockPos.getX(),
				(double)blockPos.getZ(),
				(double)(itemFrameEntity.facing.getHorizontal() * 90),
				null
			);
			this.frames.put(mapFrameMarker2.getKey(), mapFrameMarker2);
		}

		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null && compoundTag.containsKey("Decorations", 9)) {
			ListTag listTag = compoundTag.getList("Decorations", 10);

			for (int j = 0; j < listTag.size(); j++) {
				CompoundTag compoundTag2 = listTag.getCompoundTag(j);
				if (!this.icons.containsKey(compoundTag2.getString("id"))) {
					this.method_107(
						MapIcon.Type.byId(compoundTag2.getByte("type")),
						playerEntity.world,
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

	public static void addDecorationsTag(ItemStack itemStack, BlockPos blockPos, String string, MapIcon.Type type) {
		ListTag listTag;
		if (itemStack.hasTag() && itemStack.getTag().containsKey("Decorations", 9)) {
			listTag = itemStack.getTag().getList("Decorations", 10);
		} else {
			listTag = new ListTag();
			itemStack.putSubTag("Decorations", listTag);
		}

		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putByte("type", type.getId());
		compoundTag.putString("id", string);
		compoundTag.putDouble("x", (double)blockPos.getX());
		compoundTag.putDouble("z", (double)blockPos.getZ());
		compoundTag.putDouble("rot", 180.0);
		listTag.add(compoundTag);
		if (type.hasTintColor()) {
			CompoundTag compoundTag2 = itemStack.getOrCreateSubTag("display");
			compoundTag2.putInt("MapColor", type.getTintColor());
		}
	}

	private void method_107(MapIcon.Type type, @Nullable IWorld iWorld, String string, double d, double e, double f, @Nullable Text text) {
		int i = 1 << this.scale;
		float g = (float)(d - (double)this.xCenter) / (float)i;
		float h = (float)(e - (double)this.zCenter) / (float)i;
		byte b = (byte)((int)((double)(g * 2.0F) + 0.5));
		byte c = (byte)((int)((double)(h * 2.0F) + 0.5));
		int j = 63;
		byte k;
		if (g >= -63.0F && h >= -63.0F && g <= 63.0F && h <= 63.0F) {
			f += f < 0.0 ? -8.0 : 8.0;
			k = (byte)((int)(f * 16.0 / 360.0));
			if (this.dimension == DimensionType.field_13076 && iWorld != null) {
				int l = (int)(iWorld.getLevelProperties().getTimeOfDay() / 10L);
				k = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
			}
		} else {
			if (type != MapIcon.Type.field_91) {
				this.icons.remove(string);
				return;
			}

			int l = 320;
			if (Math.abs(g) < 320.0F && Math.abs(h) < 320.0F) {
				type = MapIcon.Type.field_86;
			} else {
				if (!this.unlimitedTracking) {
					this.icons.remove(string);
					return;
				}

				type = MapIcon.Type.field_87;
			}

			k = 0;
			if (g <= -63.0F) {
				b = -128;
			}

			if (h <= -63.0F) {
				c = -128;
			}

			if (g >= 63.0F) {
				b = 127;
			}

			if (h >= 63.0F) {
				c = 127;
			}
		}

		this.icons.put(string, new MapIcon(type, b, c, k, text));
	}

	@Nullable
	public Packet<?> getPlayerMarkerPacket(ItemStack itemStack, BlockView blockView, PlayerEntity playerEntity) {
		MapState.PlayerUpdateTracker playerUpdateTracker = (MapState.PlayerUpdateTracker)this.updateTrackersByPlayer.get(playerEntity);
		return playerUpdateTracker == null ? null : playerUpdateTracker.getPacket(itemStack);
	}

	public void markDirty(int i, int j) {
		this.markDirty();

		for (MapState.PlayerUpdateTracker playerUpdateTracker : this.updateTrackers) {
			playerUpdateTracker.markDirty(i, j);
		}
	}

	public MapState.PlayerUpdateTracker getPlayerSyncData(PlayerEntity playerEntity) {
		MapState.PlayerUpdateTracker playerUpdateTracker = (MapState.PlayerUpdateTracker)this.updateTrackersByPlayer.get(playerEntity);
		if (playerUpdateTracker == null) {
			playerUpdateTracker = new MapState.PlayerUpdateTracker(playerEntity);
			this.updateTrackersByPlayer.put(playerEntity, playerUpdateTracker);
			this.updateTrackers.add(playerUpdateTracker);
		}

		return playerUpdateTracker;
	}

	public void addBanner(IWorld iWorld, BlockPos blockPos) {
		float f = (float)blockPos.getX() + 0.5F;
		float g = (float)blockPos.getZ() + 0.5F;
		int i = 1 << this.scale;
		float h = (f - (float)this.xCenter) / (float)i;
		float j = (g - (float)this.zCenter) / (float)i;
		int k = 63;
		boolean bl = false;
		if (h >= -63.0F && j >= -63.0F && h <= 63.0F && j <= 63.0F) {
			MapBannerMarker mapBannerMarker = MapBannerMarker.fromWorldBlock(iWorld, blockPos);
			if (mapBannerMarker == null) {
				return;
			}

			boolean bl2 = true;
			if (this.banners.containsKey(mapBannerMarker.getKey()) && ((MapBannerMarker)this.banners.get(mapBannerMarker.getKey())).equals(mapBannerMarker)) {
				this.banners.remove(mapBannerMarker.getKey());
				this.icons.remove(mapBannerMarker.getKey());
				bl2 = false;
				bl = true;
			}

			if (bl2) {
				this.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
				this.method_107(mapBannerMarker.getIconType(), iWorld, mapBannerMarker.getKey(), (double)f, (double)g, 180.0, mapBannerMarker.method_68());
				bl = true;
			}

			if (bl) {
				this.markDirty();
			}
		}
	}

	public void removeBanner(BlockView blockView, int i, int j) {
		Iterator<MapBannerMarker> iterator = this.banners.values().iterator();

		while (iterator.hasNext()) {
			MapBannerMarker mapBannerMarker = (MapBannerMarker)iterator.next();
			if (mapBannerMarker.getPos().getX() == i && mapBannerMarker.getPos().getZ() == j) {
				MapBannerMarker mapBannerMarker2 = MapBannerMarker.fromWorldBlock(blockView, mapBannerMarker.getPos());
				if (!mapBannerMarker.equals(mapBannerMarker2)) {
					iterator.remove();
					this.icons.remove(mapBannerMarker.getKey());
				}
			}
		}
	}

	public void removeFrame(BlockPos blockPos, int i) {
		this.icons.remove("frame-" + i);
		this.frames.remove(MapFrameMarker.getKey(blockPos));
	}

	public class PlayerUpdateTracker {
		public final PlayerEntity player;
		private boolean dirty = true;
		private int startX;
		private int startZ;
		private int endX = 127;
		private int endZ = 127;
		private int emptyPacketsRequested;
		public int field_131;

		public PlayerUpdateTracker(PlayerEntity playerEntity) {
			this.player = playerEntity;
		}

		@Nullable
		public Packet<?> getPacket(ItemStack itemStack) {
			if (this.dirty) {
				this.dirty = false;
				return new MapUpdateS2CPacket(
					FilledMapItem.getMapId(itemStack),
					MapState.this.scale,
					MapState.this.showIcons,
					MapState.this.locked,
					MapState.this.icons.values(),
					MapState.this.colors,
					this.startX,
					this.startZ,
					this.endX + 1 - this.startX,
					this.endZ + 1 - this.startZ
				);
			} else {
				return this.emptyPacketsRequested++ % 5 == 0
					? new MapUpdateS2CPacket(
						FilledMapItem.getMapId(itemStack),
						MapState.this.scale,
						MapState.this.showIcons,
						MapState.this.locked,
						MapState.this.icons.values(),
						MapState.this.colors,
						0,
						0,
						0,
						0
					)
					: null;
			}
		}

		public void markDirty(int i, int j) {
			if (this.dirty) {
				this.startX = Math.min(this.startX, i);
				this.startZ = Math.min(this.startZ, j);
				this.endX = Math.max(this.endX, i);
				this.endZ = Math.max(this.endZ, j);
			} else {
				this.dirty = true;
				this.startX = i;
				this.startZ = j;
				this.endX = i;
				this.endZ = j;
			}
		}
	}
}
