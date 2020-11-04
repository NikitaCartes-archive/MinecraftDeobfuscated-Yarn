package net.minecraft.client.render.debug;

import com.google.common.collect.Iterables;
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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class VillageDebugRenderer implements DebugRenderer.Renderer {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final Map<BlockPos, VillageDebugRenderer.PointOfInterest> pointsOfInterest = Maps.<BlockPos, VillageDebugRenderer.PointOfInterest>newHashMap();
	private final Map<UUID, VillageDebugRenderer.Brain> brains = Maps.<UUID, VillageDebugRenderer.Brain>newHashMap();
	@Nullable
	private UUID targetedEntity;

	public VillageDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void clear() {
		this.pointsOfInterest.clear();
		this.brains.clear();
		this.targetedEntity = null;
	}

	public void addPointOfInterest(VillageDebugRenderer.PointOfInterest pointOfInterest) {
		this.pointsOfInterest.put(pointOfInterest.pos, pointOfInterest);
	}

	public void removePointOfInterest(BlockPos blockPos) {
		this.pointsOfInterest.remove(blockPos);
	}

	public void setFreeTicketCount(BlockPos pos, int freeTicketCount) {
		VillageDebugRenderer.PointOfInterest pointOfInterest = (VillageDebugRenderer.PointOfInterest)this.pointsOfInterest.get(pos);
		if (pointOfInterest == null) {
			LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: {}", pos);
		} else {
			pointOfInterest.freeTicketCount = freeTicketCount;
		}
	}

	public void addBrain(VillageDebugRenderer.Brain brain) {
		this.brains.put(brain.uuid, brain);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		this.method_24805();
		this.method_23135(cameraX, cameraY, cameraZ);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
		if (!this.client.player.isSpectator()) {
			this.updateTargetedEntity();
		}
	}

	private void method_24805() {
		this.brains.entrySet().removeIf(entry -> {
			Entity entity = this.client.world.getEntityById(((VillageDebugRenderer.Brain)entry.getValue()).field_18924);
			return entity == null || entity.isRemoved();
		});
	}

	private void method_23135(double d, double e, double f) {
		BlockPos blockPos = new BlockPos(d, e, f);
		this.brains.values().forEach(brain -> {
			if (this.isClose(brain)) {
				this.drawBrain(brain, d, e, f);
			}
		});

		for (BlockPos blockPos2 : this.pointsOfInterest.keySet()) {
			if (blockPos.isWithinDistance(blockPos2, 30.0)) {
				drawPointOfInterest(blockPos2);
			}
		}

		this.pointsOfInterest.values().forEach(pointOfInterest -> {
			if (blockPos.isWithinDistance(pointOfInterest.pos, 30.0)) {
				this.drawPointOfInterestInfo(pointOfInterest);
			}
		});
		this.getGhostPointsOfInterest().forEach((blockPos2x, list) -> {
			if (blockPos.isWithinDistance(blockPos2x, 30.0)) {
				this.drawGhostPointOfInterest(blockPos2x, list);
			}
		});
	}

	private static void drawPointOfInterest(BlockPos pos) {
		float f = 0.05F;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		DebugRenderer.drawBox(pos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
	}

	private void drawGhostPointOfInterest(BlockPos pos, List<String> brains) {
		float f = 0.05F;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		DebugRenderer.drawBox(pos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
		drawString("" + brains, pos, 0, -256);
		drawString("Ghost POI", pos, 1, -65536);
	}

	private void drawPointOfInterestInfo(VillageDebugRenderer.PointOfInterest pointOfInterest) {
		int i = 0;
		Set<String> set = this.getVillagerNames(pointOfInterest);
		if (set.size() < 4) {
			drawString("Owners: " + set, pointOfInterest, i, -256);
		} else {
			drawString("" + set.size() + " ticket holders", pointOfInterest, i, -256);
		}

		i++;
		Set<String> set2 = this.method_29385(pointOfInterest);
		if (set2.size() < 4) {
			drawString("Candidates: " + set2, pointOfInterest, i, -23296);
		} else {
			drawString("" + set2.size() + " potential owners", pointOfInterest, i, -23296);
		}

		drawString("Free tickets: " + pointOfInterest.freeTicketCount, pointOfInterest, ++i, -256);
		drawString(pointOfInterest.field_18932, pointOfInterest, ++i, -1);
	}

	private void drawPath(VillageDebugRenderer.Brain brain, double cameraX, double cameraY, double cameraZ) {
		if (brain.path != null) {
			PathfindingDebugRenderer.drawPath(brain.path, 0.5F, false, false, cameraX, cameraY, cameraZ);
		}
	}

	private void drawBrain(VillageDebugRenderer.Brain brain, double cameraX, double cameraY, double cameraZ) {
		boolean bl = this.isTargeted(brain);
		int i = 0;
		drawString(brain.pos, i, brain.field_19328, -1, 0.03F);
		i++;
		if (bl) {
			drawString(brain.pos, i, brain.profession + " " + brain.xp + " xp", -1, 0.02F);
			i++;
		}

		if (bl) {
			int j = brain.field_22406 < brain.field_22407 ? -23296 : -1;
			drawString(brain.pos, i, "health: " + String.format("%.1f", brain.field_22406) + " / " + String.format("%.1f", brain.field_22407), j, 0.02F);
			i++;
		}

		if (bl && !brain.field_19372.equals("")) {
			drawString(brain.pos, i, brain.field_19372, -98404, 0.02F);
			i++;
		}

		if (bl) {
			for (String string : brain.field_18928) {
				drawString(brain.pos, i, string, -16711681, 0.02F);
				i++;
			}
		}

		if (bl) {
			for (String string : brain.field_18927) {
				drawString(brain.pos, i, string, -16711936, 0.02F);
				i++;
			}
		}

		if (brain.wantsGolem) {
			drawString(brain.pos, i, "Wants Golem", -23296, 0.02F);
			i++;
		}

		if (bl) {
			for (String string : brain.field_19375) {
				if (string.startsWith(brain.field_19328)) {
					drawString(brain.pos, i, string, -1, 0.02F);
				} else {
					drawString(brain.pos, i, string, -23296, 0.02F);
				}

				i++;
			}
		}

		if (bl) {
			for (String string : Lists.reverse(brain.field_19374)) {
				drawString(brain.pos, i, string, -3355444, 0.02F);
				i++;
			}
		}

		if (bl) {
			this.drawPath(brain, cameraX, cameraY, cameraZ);
		}
	}

	private static void drawString(String string, VillageDebugRenderer.PointOfInterest pointOfInterest, int offsetY, int color) {
		BlockPos blockPos = pointOfInterest.pos;
		drawString(string, blockPos, offsetY, color);
	}

	private static void drawString(String string, BlockPos pos, int offsetY, int color) {
		double d = 1.3;
		double e = 0.2;
		double f = (double)pos.getX() + 0.5;
		double g = (double)pos.getY() + 1.3 + (double)offsetY * 0.2;
		double h = (double)pos.getZ() + 0.5;
		DebugRenderer.drawString(string, f, g, h, color, 0.02F, true, 0.0F, true);
	}

	private static void drawString(Position pos, int offsetY, String string, int color, float size) {
		double d = 2.4;
		double e = 0.25;
		BlockPos blockPos = new BlockPos(pos);
		double f = (double)blockPos.getX() + 0.5;
		double g = pos.getY() + 2.4 + (double)offsetY * 0.25;
		double h = (double)blockPos.getZ() + 0.5;
		float i = 0.5F;
		DebugRenderer.drawString(string, f, g, h, color, size, false, 0.5F, true);
	}

	private Set<String> getVillagerNames(VillageDebugRenderer.PointOfInterest pointOfInterest) {
		return (Set<String>)this.getBrains(pointOfInterest.pos).stream().map(NameGenerator::name).collect(Collectors.toSet());
	}

	private Set<String> method_29385(VillageDebugRenderer.PointOfInterest pointOfInterest) {
		return (Set<String>)this.method_29386(pointOfInterest.pos).stream().map(NameGenerator::name).collect(Collectors.toSet());
	}

	private boolean isTargeted(VillageDebugRenderer.Brain brain) {
		return Objects.equals(this.targetedEntity, brain.uuid);
	}

	private boolean isClose(VillageDebugRenderer.Brain brain) {
		PlayerEntity playerEntity = this.client.player;
		BlockPos blockPos = new BlockPos(playerEntity.getX(), brain.pos.getY(), playerEntity.getZ());
		BlockPos blockPos2 = new BlockPos(brain.pos);
		return blockPos.isWithinDistance(blockPos2, 30.0);
	}

	private Collection<UUID> getBrains(BlockPos pointOfInterest) {
		return (Collection<UUID>)this.brains
			.values()
			.stream()
			.filter(brain -> brain.isPointOfInterest(pointOfInterest))
			.map(VillageDebugRenderer.Brain::getUuid)
			.collect(Collectors.toSet());
	}

	private Collection<UUID> method_29386(BlockPos blockPos) {
		return (Collection<UUID>)this.brains
			.values()
			.stream()
			.filter(brain -> brain.method_29388(blockPos))
			.map(VillageDebugRenderer.Brain::getUuid)
			.collect(Collectors.toSet());
	}

	private Map<BlockPos, List<String>> getGhostPointsOfInterest() {
		Map<BlockPos, List<String>> map = Maps.<BlockPos, List<String>>newHashMap();

		for (VillageDebugRenderer.Brain brain : this.brains.values()) {
			for (BlockPos blockPos : Iterables.concat(brain.pointsOfInterest, brain.field_25287)) {
				if (!this.pointsOfInterest.containsKey(blockPos)) {
					((List)map.computeIfAbsent(blockPos, blockPosx -> Lists.newArrayList())).add(brain.field_19328);
				}
			}
		}

		return map;
	}

	private void updateTargetedEntity() {
		DebugRenderer.getTargetedEntity(this.client.getCameraEntity(), 8).ifPresent(entity -> this.targetedEntity = entity.getUuid());
	}

	@Environment(EnvType.CLIENT)
	public static class Brain {
		public final UUID uuid;
		public final int field_18924;
		public final String field_19328;
		public final String profession;
		public final int xp;
		public final float field_22406;
		public final float field_22407;
		public final Position pos;
		public final String field_19372;
		public final Path path;
		public final boolean wantsGolem;
		public final List<String> field_18927 = Lists.<String>newArrayList();
		public final List<String> field_18928 = Lists.<String>newArrayList();
		public final List<String> field_19374 = Lists.<String>newArrayList();
		public final List<String> field_19375 = Lists.<String>newArrayList();
		public final Set<BlockPos> pointsOfInterest = Sets.<BlockPos>newHashSet();
		public final Set<BlockPos> field_25287 = Sets.<BlockPos>newHashSet();

		public Brain(UUID uUID, int i, String string, String profession, int xp, float f, float g, Position position, String string2, @Nullable Path path, boolean bl) {
			this.uuid = uUID;
			this.field_18924 = i;
			this.field_19328 = string;
			this.profession = profession;
			this.xp = xp;
			this.field_22406 = f;
			this.field_22407 = g;
			this.pos = position;
			this.field_19372 = string2;
			this.path = path;
			this.wantsGolem = bl;
		}

		private boolean isPointOfInterest(BlockPos blockPos) {
			return this.pointsOfInterest.stream().anyMatch(blockPos::equals);
		}

		private boolean method_29388(BlockPos blockPos) {
			return this.field_25287.contains(blockPos);
		}

		public UUID getUuid() {
			return this.uuid;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class PointOfInterest {
		public final BlockPos pos;
		public String field_18932;
		public int freeTicketCount;

		public PointOfInterest(BlockPos blockPos, String string, int i) {
			this.pos = blockPos;
			this.field_18932 = string;
			this.freeTicketCount = i;
		}
	}
}
