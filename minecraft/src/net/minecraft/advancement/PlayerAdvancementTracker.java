package net.minecraft.advancement;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.network.packet.AdvancementUpdateS2CPacket;
import net.minecraft.client.network.packet.SelectAdvancementTabS2CPacket;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancementTracker {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer())
		.registerTypeAdapter(Identifier.class, new Identifier.Serializer())
		.setPrettyPrinting()
		.create();
	private static final TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE = new TypeToken<Map<Identifier, AdvancementProgress>>() {
	};
	private final MinecraftServer server;
	private final File advancementFile;
	private final Map<Advancement, AdvancementProgress> advancementToProgress = Maps.<Advancement, AdvancementProgress>newLinkedHashMap();
	private final Set<Advancement> visibleAdvancements = Sets.<Advancement>newLinkedHashSet();
	private final Set<Advancement> visibilityUpdates = Sets.<Advancement>newLinkedHashSet();
	private final Set<Advancement> progressUpdates = Sets.<Advancement>newLinkedHashSet();
	private ServerPlayerEntity owner;
	@Nullable
	private Advancement currentDisplayTab;
	private boolean dirty = true;

	public PlayerAdvancementTracker(MinecraftServer minecraftServer, File file, ServerPlayerEntity serverPlayerEntity) {
		this.server = minecraftServer;
		this.advancementFile = file;
		this.owner = serverPlayerEntity;
		this.load();
	}

	public void setOwner(ServerPlayerEntity serverPlayerEntity) {
		this.owner = serverPlayerEntity;
	}

	public void clearCriterions() {
		for (Criterion<?> criterion : Criterions.getAllCriterions()) {
			criterion.endTracking(this);
		}
	}

	public void reload() {
		this.clearCriterions();
		this.advancementToProgress.clear();
		this.visibleAdvancements.clear();
		this.visibilityUpdates.clear();
		this.progressUpdates.clear();
		this.dirty = true;
		this.currentDisplayTab = null;
		this.load();
	}

	private void beginTrackingAllAdvancements() {
		for (Advancement advancement : this.server.getAdvancementManager().getAdvancements()) {
			this.beginTracking(advancement);
		}
	}

	private void updateCompleted() {
		List<Advancement> list = Lists.<Advancement>newArrayList();

		for (Entry<Advancement, AdvancementProgress> entry : this.advancementToProgress.entrySet()) {
			if (((AdvancementProgress)entry.getValue()).isDone()) {
				list.add(entry.getKey());
				this.progressUpdates.add(entry.getKey());
			}
		}

		for (Advancement advancement : list) {
			this.updateDisplay(advancement);
		}
	}

	private void rewardEmptyAdvancements() {
		for (Advancement advancement : this.server.getAdvancementManager().getAdvancements()) {
			if (advancement.getCriteria().isEmpty()) {
				this.grantCriterion(advancement, "");
				advancement.getRewards().apply(this.owner);
			}
		}
	}

	private void load() {
		if (this.advancementFile.isFile()) {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(Files.toString(this.advancementFile, StandardCharsets.UTF_8)));
				Throwable var2 = null;

				try {
					jsonReader.setLenient(false);
					Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(jsonReader));
					if (!dynamic.get("DataVersion").asNumber().isPresent()) {
						dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
					}

					dynamic = this.server
						.getDataFixer()
						.update(DataFixTypes.field_19220.getTypeReference(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getGameVersion().getWorldVersion());
					dynamic = dynamic.remove("DataVersion");
					Map<Identifier, AdvancementProgress> map = GSON.getAdapter(JSON_TYPE).fromJsonTree(dynamic.getValue());
					if (map == null) {
						throw new JsonParseException("Found null for advancements");
					}

					Stream<Entry<Identifier, AdvancementProgress>> stream = map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));

					for (Entry<Identifier, AdvancementProgress> entry : (List)stream.collect(Collectors.toList())) {
						Advancement advancement = this.server.getAdvancementManager().get((Identifier)entry.getKey());
						if (advancement == null) {
							LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), this.advancementFile);
						} else {
							this.initProgress(advancement, (AdvancementProgress)entry.getValue());
						}
					}
				} catch (Throwable var18) {
					var2 = var18;
					throw var18;
				} finally {
					if (jsonReader != null) {
						if (var2 != null) {
							try {
								jsonReader.close();
							} catch (Throwable var17) {
								var2.addSuppressed(var17);
							}
						} else {
							jsonReader.close();
						}
					}
				}
			} catch (JsonParseException var20) {
				LOGGER.error("Couldn't parse player advancements in {}", this.advancementFile, var20);
			} catch (IOException var21) {
				LOGGER.error("Couldn't access player advancements in {}", this.advancementFile, var21);
			}
		}

		this.rewardEmptyAdvancements();
		this.updateCompleted();
		this.beginTrackingAllAdvancements();
	}

	public void save() {
		Map<Identifier, AdvancementProgress> map = Maps.<Identifier, AdvancementProgress>newHashMap();

		for (Entry<Advancement, AdvancementProgress> entry : this.advancementToProgress.entrySet()) {
			AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
			if (advancementProgress.isAnyObtained()) {
				map.put(((Advancement)entry.getKey()).getId(), advancementProgress);
			}
		}

		if (this.advancementFile.getParentFile() != null) {
			this.advancementFile.getParentFile().mkdirs();
		}

		JsonElement jsonElement = GSON.toJsonTree(map);
		jsonElement.getAsJsonObject().addProperty("DataVersion", SharedConstants.getGameVersion().getWorldVersion());

		try {
			OutputStream outputStream = new FileOutputStream(this.advancementFile);
			Throwable var38 = null;

			try {
				Writer writer = new OutputStreamWriter(outputStream, Charsets.UTF_8.newEncoder());
				Throwable var6 = null;

				try {
					GSON.toJson(jsonElement, writer);
				} catch (Throwable var31) {
					var6 = var31;
					throw var31;
				} finally {
					if (writer != null) {
						if (var6 != null) {
							try {
								writer.close();
							} catch (Throwable var30) {
								var6.addSuppressed(var30);
							}
						} else {
							writer.close();
						}
					}
				}
			} catch (Throwable var33) {
				var38 = var33;
				throw var33;
			} finally {
				if (outputStream != null) {
					if (var38 != null) {
						try {
							outputStream.close();
						} catch (Throwable var29) {
							var38.addSuppressed(var29);
						}
					} else {
						outputStream.close();
					}
				}
			}
		} catch (IOException var35) {
			LOGGER.error("Couldn't save player advancements to {}", this.advancementFile, var35);
		}
	}

	public boolean grantCriterion(Advancement advancement, String string) {
		boolean bl = false;
		AdvancementProgress advancementProgress = this.getProgress(advancement);
		boolean bl2 = advancementProgress.isDone();
		if (advancementProgress.obtain(string)) {
			this.endTrackingCompleted(advancement);
			this.progressUpdates.add(advancement);
			bl = true;
			if (!bl2 && advancementProgress.isDone()) {
				advancement.getRewards().apply(this.owner);
				if (advancement.getDisplay() != null
					&& advancement.getDisplay().shouldAnnounceToChat()
					&& this.owner.world.getGameRules().getBoolean("announceAdvancements")) {
					this.server
						.getPlayerManager()
						.sendToAll(
							new TranslatableComponent(
								"chat.type.advancement." + advancement.getDisplay().getFrame().getId(), this.owner.getDisplayName(), advancement.getTextComponent()
							)
						);
				}
			}
		}

		if (advancementProgress.isDone()) {
			this.updateDisplay(advancement);
		}

		return bl;
	}

	public boolean revokeCriterion(Advancement advancement, String string) {
		boolean bl = false;
		AdvancementProgress advancementProgress = this.getProgress(advancement);
		if (advancementProgress.reset(string)) {
			this.beginTracking(advancement);
			this.progressUpdates.add(advancement);
			bl = true;
		}

		if (!advancementProgress.isAnyObtained()) {
			this.updateDisplay(advancement);
		}

		return bl;
	}

	private void beginTracking(Advancement advancement) {
		AdvancementProgress advancementProgress = this.getProgress(advancement);
		if (!advancementProgress.isDone()) {
			for (Entry<String, AdvancementCriterion> entry : advancement.getCriteria().entrySet()) {
				CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
				if (criterionProgress != null && !criterionProgress.isObtained()) {
					CriterionConditions criterionConditions = ((AdvancementCriterion)entry.getValue()).getConditions();
					if (criterionConditions != null) {
						Criterion<CriterionConditions> criterion = Criterions.getById(criterionConditions.getId());
						if (criterion != null) {
							criterion.beginTrackingCondition(this, new Criterion.ConditionsContainer<>(criterionConditions, advancement, (String)entry.getKey()));
						}
					}
				}
			}
		}
	}

	private void endTrackingCompleted(Advancement advancement) {
		AdvancementProgress advancementProgress = this.getProgress(advancement);

		for (Entry<String, AdvancementCriterion> entry : advancement.getCriteria().entrySet()) {
			CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
			if (criterionProgress != null && (criterionProgress.isObtained() || advancementProgress.isDone())) {
				CriterionConditions criterionConditions = ((AdvancementCriterion)entry.getValue()).getConditions();
				if (criterionConditions != null) {
					Criterion<CriterionConditions> criterion = Criterions.getById(criterionConditions.getId());
					if (criterion != null) {
						criterion.endTrackingCondition(this, new Criterion.ConditionsContainer<>(criterionConditions, advancement, (String)entry.getKey()));
					}
				}
			}
		}
	}

	public void sendUpdate(ServerPlayerEntity serverPlayerEntity) {
		if (this.dirty || !this.visibilityUpdates.isEmpty() || !this.progressUpdates.isEmpty()) {
			Map<Identifier, AdvancementProgress> map = Maps.<Identifier, AdvancementProgress>newHashMap();
			Set<Advancement> set = Sets.<Advancement>newLinkedHashSet();
			Set<Identifier> set2 = Sets.<Identifier>newLinkedHashSet();

			for (Advancement advancement : this.progressUpdates) {
				if (this.visibleAdvancements.contains(advancement)) {
					map.put(advancement.getId(), this.advancementToProgress.get(advancement));
				}
			}

			for (Advancement advancementx : this.visibilityUpdates) {
				if (this.visibleAdvancements.contains(advancementx)) {
					set.add(advancementx);
				} else {
					set2.add(advancementx.getId());
				}
			}

			if (this.dirty || !map.isEmpty() || !set.isEmpty() || !set2.isEmpty()) {
				serverPlayerEntity.networkHandler.sendPacket(new AdvancementUpdateS2CPacket(this.dirty, set, set2, map));
				this.visibilityUpdates.clear();
				this.progressUpdates.clear();
			}
		}

		this.dirty = false;
	}

	public void setDisplayTab(@Nullable Advancement advancement) {
		Advancement advancement2 = this.currentDisplayTab;
		if (advancement != null && advancement.getParent() == null && advancement.getDisplay() != null) {
			this.currentDisplayTab = advancement;
		} else {
			this.currentDisplayTab = null;
		}

		if (advancement2 != this.currentDisplayTab) {
			this.owner.networkHandler.sendPacket(new SelectAdvancementTabS2CPacket(this.currentDisplayTab == null ? null : this.currentDisplayTab.getId()));
		}
	}

	public AdvancementProgress getProgress(Advancement advancement) {
		AdvancementProgress advancementProgress = (AdvancementProgress)this.advancementToProgress.get(advancement);
		if (advancementProgress == null) {
			advancementProgress = new AdvancementProgress();
			this.initProgress(advancement, advancementProgress);
		}

		return advancementProgress;
	}

	private void initProgress(Advancement advancement, AdvancementProgress advancementProgress) {
		advancementProgress.init(advancement.getCriteria(), advancement.getRequirements());
		this.advancementToProgress.put(advancement, advancementProgress);
	}

	private void updateDisplay(Advancement advancement) {
		boolean bl = this.canSee(advancement);
		boolean bl2 = this.visibleAdvancements.contains(advancement);
		if (bl && !bl2) {
			this.visibleAdvancements.add(advancement);
			this.visibilityUpdates.add(advancement);
			if (this.advancementToProgress.containsKey(advancement)) {
				this.progressUpdates.add(advancement);
			}
		} else if (!bl && bl2) {
			this.visibleAdvancements.remove(advancement);
			this.visibilityUpdates.add(advancement);
		}

		if (bl != bl2 && advancement.getParent() != null) {
			this.updateDisplay(advancement.getParent());
		}

		for (Advancement advancement2 : advancement.getChildren()) {
			this.updateDisplay(advancement2);
		}
	}

	private boolean canSee(Advancement advancement) {
		for (int i = 0; advancement != null && i <= 2; i++) {
			if (i == 0 && this.hasChildrenDone(advancement)) {
				return true;
			}

			if (advancement.getDisplay() == null) {
				return false;
			}

			AdvancementProgress advancementProgress = this.getProgress(advancement);
			if (advancementProgress.isDone()) {
				return true;
			}

			if (advancement.getDisplay().isHidden()) {
				return false;
			}

			advancement = advancement.getParent();
		}

		return false;
	}

	private boolean hasChildrenDone(Advancement advancement) {
		AdvancementProgress advancementProgress = this.getProgress(advancement);
		if (advancementProgress.isDone()) {
			return true;
		} else {
			for (Advancement advancement2 : advancement.getChildren()) {
				if (this.hasChildrenDone(advancement2)) {
					return true;
				}
			}

			return false;
		}
	}
}
