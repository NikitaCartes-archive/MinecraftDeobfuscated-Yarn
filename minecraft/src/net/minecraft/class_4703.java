package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.render.debug.VillagerNamer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;

@Environment(EnvType.CLIENT)
public class class_4703 implements DebugRenderer.Renderer {
	private final MinecraftClient field_21532;
	private final Map<BlockPos, class_4703.class_4705> field_21533 = Maps.<BlockPos, class_4703.class_4705>newHashMap();
	private final Map<UUID, class_4703.class_4704> field_21534 = Maps.<UUID, class_4703.class_4704>newHashMap();
	private UUID field_21535;

	public class_4703(MinecraftClient minecraftClient) {
		this.field_21532 = minecraftClient;
	}

	@Override
	public void clear() {
		this.field_21533.clear();
		this.field_21534.clear();
		this.field_21535 = null;
	}

	public void method_23807(class_4703.class_4705 arg) {
		this.field_21533.put(arg.field_21543, arg);
	}

	public void method_23805(class_4703.class_4704 arg) {
		this.field_21534.put(arg.field_21536, arg);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f, long l) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		this.method_23819();
		this.method_23952();
		this.method_23823();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
		if (!this.field_21532.player.isSpectator()) {
			this.method_23832();
		}
	}

	private void method_23952() {
		this.field_21534.entrySet().removeIf(entry -> this.field_21532.world.getEntityById(((class_4703.class_4704)entry.getValue()).field_21537) == null);
	}

	private void method_23819() {
		long l = this.field_21532.world.getTime() - 20L;
		this.field_21533.entrySet().removeIf(entry -> ((class_4703.class_4705)entry.getValue()).field_21546 < l);
	}

	private void method_23823() {
		BlockPos blockPos = this.method_23828().getBlockPos();
		this.field_21534.values().forEach(arg -> {
			if (this.method_23829(arg)) {
				this.method_23824(arg);
			}
		});
		this.method_23826();

		for (BlockPos blockPos2 : this.field_21533.keySet()) {
			if (blockPos.isWithinDistance(blockPos2, 30.0)) {
				method_23808(blockPos2);
			}
		}

		Map<BlockPos, Set<UUID>> map = this.method_24084();
		this.field_21533.values().forEach(arg -> {
			if (blockPos.isWithinDistance(arg.field_21543, 30.0)) {
				Set<UUID> set = (Set<UUID>)map.get(arg.field_21543);
				this.method_23821(arg, (Collection<UUID>)(set == null ? Sets.<UUID>newHashSet() : set));
			}
		});
		this.method_23830().forEach((blockPos2x, list) -> {
			if (blockPos.isWithinDistance(blockPos2x, 30.0)) {
				this.method_23813(blockPos2x, list);
			}
		});
	}

	private Map<BlockPos, Set<UUID>> method_24084() {
		Map<BlockPos, Set<UUID>> map = Maps.<BlockPos, Set<UUID>>newHashMap();
		this.field_21534.values().forEach(arg -> arg.field_21734.forEach(blockPos -> method_24081(map, arg, blockPos)));
		return map;
	}

	private void method_23826() {
		Map<BlockPos, Set<UUID>> map = Maps.<BlockPos, Set<UUID>>newHashMap();
		this.field_21534.values().stream().filter(class_4703.class_4704::method_23836).forEach(arg -> {
			Set<UUID> set = (Set<UUID>)map.get(arg.field_21541);
			if (set == null) {
				set = Sets.<UUID>newHashSet();
				map.put(arg.field_21541, set);
			}

			set.add(arg.method_23833());
		});
		map.entrySet().forEach(entry -> {
			BlockPos blockPos = (BlockPos)entry.getKey();
			Set<UUID> set = (Set<UUID>)entry.getValue();
			Set<String> set2 = (Set<String>)set.stream().map(VillagerNamer::name).collect(Collectors.toSet());
			int i = 1;
			method_23816(set2.toString(), blockPos, i++, -256);
			method_23816("Flower", blockPos, i++, -1);
			float f = 0.05F;
			method_23809(blockPos, 0.05F, 0.8F, 0.8F, 0.0F, 0.3F);
		});
	}

	private static String method_23825(Collection<UUID> collection) {
		if (collection.isEmpty()) {
			return "-";
		} else {
			return collection.size() > 3 ? "" + collection.size() + " bees" : ((Set)collection.stream().map(VillagerNamer::name).collect(Collectors.toSet())).toString();
		}
	}

	private static void method_24081(Map<BlockPos, Set<UUID>> map, class_4703.class_4704 arg, BlockPos blockPos) {
		Set<UUID> set = (Set<UUID>)map.get(blockPos);
		if (set == null) {
			set = Sets.<UUID>newHashSet();
			map.put(blockPos, set);
		}

		set.add(arg.method_23833());
	}

	private static void method_23808(BlockPos blockPos) {
		float f = 0.05F;
		method_23809(blockPos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
	}

	private void method_23813(BlockPos blockPos, List<String> list) {
		float f = 0.05F;
		method_23809(blockPos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
		method_23816("" + list, blockPos, 0, -256);
		method_23816("Ghost Hive", blockPos, 1, -65536);
	}

	private static void method_23809(BlockPos blockPos, float f, float g, float h, float i, float j) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		DebugRenderer.drawBox(blockPos, f, g, h, i, j);
	}

	private void method_23821(class_4703.class_4705 arg, Collection<UUID> collection) {
		int i = 0;
		if (!collection.isEmpty()) {
			method_23815("Blacklisted by " + method_23825(collection), arg, i++, -65536);
		}

		method_23815("Out: " + method_23825(this.method_23822(arg.field_21543)), arg, i++, -3355444);
		if (arg.field_21545 == 0) {
			method_23815("In: -", arg, i++, -256);
		} else if (arg.field_21545 == 1) {
			method_23815("In: 1 bee", arg, i++, -256);
		} else {
			method_23815("In: " + arg.field_21545 + " bees", arg, i++, -256);
		}

		method_23815("Honey: " + arg.field_21625, arg, i++, -23296);
		method_23815(arg.field_21544 + (arg.field_21626 ? " (sedated)" : ""), arg, i++, -1);
	}

	private void method_23820(class_4703.class_4704 arg) {
		if (arg.field_21539 != null) {
			PathfindingDebugRenderer.drawPath(
				arg.field_21539, 0.5F, false, false, this.method_23828().getPos().getX(), this.method_23828().getPos().getY(), this.method_23828().getPos().getZ()
			);
		}
	}

	private void method_23824(class_4703.class_4704 arg) {
		boolean bl = this.method_23827(arg);
		int i = 0;
		method_23814(arg.field_21538, i++, arg.toString(), -1, 0.03F);
		if (arg.field_21540 == null) {
			method_23814(arg.field_21538, i++, "No hive", -98404, 0.02F);
		} else {
			method_23814(arg.field_21538, i++, "Hive: " + this.method_23806(arg, arg.field_21540), -256, 0.02F);
		}

		if (arg.field_21541 == null) {
			method_23814(arg.field_21538, i++, "No flower", -98404, 0.02F);
		} else {
			method_23814(arg.field_21538, i++, "Flower: " + this.method_23806(arg, arg.field_21541), -256, 0.02F);
		}

		for (String string : arg.field_21542) {
			method_23814(arg.field_21538, i++, string, -16711936, 0.02F);
		}

		if (bl) {
			this.method_23820(arg);
		}

		if (arg.field_21733 > 0) {
			int j = arg.field_21733 < 600 ? -3355444 : -23296;
			method_23814(arg.field_21538, i++, "Travelling: " + arg.field_21733 + " ticks", j, 0.02F);
		}
	}

	private static void method_23815(String string, class_4703.class_4705 arg, int i, int j) {
		BlockPos blockPos = arg.field_21543;
		method_23816(string, blockPos, i, j);
	}

	private static void method_23816(String string, BlockPos blockPos, int i, int j) {
		double d = 1.3;
		double e = 0.2;
		double f = (double)blockPos.getX() + 0.5;
		double g = (double)blockPos.getY() + 1.3 + (double)i * 0.2;
		double h = (double)blockPos.getZ() + 0.5;
		DebugRenderer.drawString(string, f, g, h, j, 0.02F, true, 0.0F, true);
	}

	private static void method_23814(Position position, int i, String string, int j, float f) {
		double d = 2.4;
		double e = 0.25;
		BlockPos blockPos = new BlockPos(position);
		double g = (double)blockPos.getX() + 0.5;
		double h = position.getY() + 2.4 + (double)i * 0.25;
		double k = (double)blockPos.getZ() + 0.5;
		float l = 0.5F;
		DebugRenderer.drawString(string, g, h, k, j, f, false, 0.5F, true);
	}

	private Camera method_23828() {
		return this.field_21532.gameRenderer.getCamera();
	}

	private String method_23806(class_4703.class_4704 arg, BlockPos blockPos) {
		float f = MathHelper.sqrt(blockPos.getSquaredDistance(arg.field_21538.getX(), arg.field_21538.getY(), arg.field_21538.getZ(), true));
		double d = (double)Math.round(f * 10.0F) / 10.0;
		return blockPos.method_23854() + " (dist " + d + ")";
	}

	private boolean method_23827(class_4703.class_4704 arg) {
		return Objects.equals(this.field_21535, arg.field_21536);
	}

	private boolean method_23829(class_4703.class_4704 arg) {
		PlayerEntity playerEntity = this.field_21532.player;
		BlockPos blockPos = new BlockPos(playerEntity.getX(), arg.field_21538.getY(), playerEntity.getZ());
		BlockPos blockPos2 = new BlockPos(arg.field_21538);
		return blockPos.isWithinDistance(blockPos2, 30.0);
	}

	private Collection<UUID> method_23822(BlockPos blockPos) {
		return (Collection<UUID>)this.field_21534
			.values()
			.stream()
			.filter(arg -> arg.method_23834(blockPos))
			.map(class_4703.class_4704::method_23833)
			.collect(Collectors.toSet());
	}

	private Map<BlockPos, List<String>> method_23830() {
		Map<BlockPos, List<String>> map = Maps.<BlockPos, List<String>>newHashMap();

		for (class_4703.class_4704 lv : this.field_21534.values()) {
			if (lv.field_21540 != null && !this.field_21533.containsKey(lv.field_21540)) {
				List<String> list = (List<String>)map.get(lv.field_21540);
				if (list == null) {
					list = Lists.<String>newArrayList();
					map.put(lv.field_21540, list);
				}

				list.add(lv.method_23835());
			}
		}

		return map;
	}

	private void method_23832() {
		DebugRenderer.getTargettedEntity(this.field_21532.getCameraEntity(), 8).ifPresent(entity -> this.field_21535 = entity.getUuid());
	}

	@Environment(EnvType.CLIENT)
	public static class class_4704 {
		public final UUID field_21536;
		public final int field_21537;
		public final Position field_21538;
		@Nullable
		public final Path field_21539;
		@Nullable
		public final BlockPos field_21540;
		@Nullable
		public final BlockPos field_21541;
		public final int field_21733;
		public final List<String> field_21542 = Lists.<String>newArrayList();
		public final Set<BlockPos> field_21734 = Sets.<BlockPos>newHashSet();

		public class_4704(UUID uUID, int i, Position position, Path path, BlockPos blockPos, BlockPos blockPos2, int j) {
			this.field_21536 = uUID;
			this.field_21537 = i;
			this.field_21538 = position;
			this.field_21539 = path;
			this.field_21540 = blockPos;
			this.field_21541 = blockPos2;
			this.field_21733 = j;
		}

		public boolean method_23834(BlockPos blockPos) {
			return this.field_21540 != null && this.field_21540.equals(blockPos);
		}

		public UUID method_23833() {
			return this.field_21536;
		}

		public String method_23835() {
			return VillagerNamer.name(this.field_21536);
		}

		public String toString() {
			return this.method_23835();
		}

		public boolean method_23836() {
			return this.field_21541 != null;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4705 {
		public final BlockPos field_21543;
		public final String field_21544;
		public final int field_21545;
		public final int field_21625;
		public final boolean field_21626;
		public final long field_21546;

		public class_4705(BlockPos blockPos, String string, int i, int j, boolean bl, long l) {
			this.field_21543 = blockPos;
			this.field_21544 = string;
			this.field_21545 = i;
			this.field_21625 = j;
			this.field_21626 = bl;
			this.field_21546 = l;
		}
	}
}
