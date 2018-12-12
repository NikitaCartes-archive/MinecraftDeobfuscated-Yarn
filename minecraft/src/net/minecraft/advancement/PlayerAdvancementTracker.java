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
import com.mojang.datafixers.DataFixTypes;
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
import net.minecraft.client.network.packet.AdvancementUpdateClientPacket;
import net.minecraft.client.network.packet.SelectAdvancementTabClientPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancementTracker {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer())
		.registerTypeAdapter(Identifier.class, new Identifier.DeSerializer())
		.setPrettyPrinting()
		.create();
	private static final TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE = new TypeToken<Map<Identifier, AdvancementProgress>>() {
	};
	private final MinecraftServer server;
	private final File advancementFile;
	private final Map<SimpleAdvancement, AdvancementProgress> advancementToProgress = Maps.<SimpleAdvancement, AdvancementProgress>newLinkedHashMap();
	private final Set<SimpleAdvancement> visibleAdvancements = Sets.<SimpleAdvancement>newLinkedHashSet();
	private final Set<SimpleAdvancement> visibilityUpdates = Sets.<SimpleAdvancement>newLinkedHashSet();
	private final Set<SimpleAdvancement> progressUpdates = Sets.<SimpleAdvancement>newLinkedHashSet();
	private ServerPlayerEntity owner;
	@Nullable
	private SimpleAdvancement currentDisplayTab;
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
		for (SimpleAdvancement simpleAdvancement : this.server.method_3851().getAdvancements()) {
			this.beginTracking(simpleAdvancement);
		}
	}

	private void updateCompleted() {
		List<SimpleAdvancement> list = Lists.<SimpleAdvancement>newArrayList();

		for (Entry<SimpleAdvancement, AdvancementProgress> entry : this.advancementToProgress.entrySet()) {
			if (((AdvancementProgress)entry.getValue()).isDone()) {
				list.add(entry.getKey());
				this.progressUpdates.add(entry.getKey());
			}
		}

		for (SimpleAdvancement simpleAdvancement : list) {
			this.updateDisplay(simpleAdvancement);
		}
	}

	private void rewardEmptyAdvancements() {
		for (SimpleAdvancement simpleAdvancement : this.server.method_3851().getAdvancements()) {
			if (simpleAdvancement.getCriteria().isEmpty()) {
				this.grantCriterion(simpleAdvancement, "");
				simpleAdvancement.getRewards().apply(this.owner);
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
					if (!dynamic.get("DataVersion").flatMap(Dynamic::getNumberValue).isPresent()) {
						dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
					}

					dynamic = this.server
						.getDataFixer()
						.update(DataFixTypes.ADVANCEMENTS, dynamic, dynamic.getInt("DataVersion"), SharedConstants.getGameVersion().getWorldVersion());
					dynamic = dynamic.remove("DataVersion");
					Map<Identifier, AdvancementProgress> map = GSON.getAdapter(JSON_TYPE).fromJsonTree(dynamic.getValue());
					if (map == null) {
						throw new JsonParseException("Found null for advancements");
					}

					Stream<Entry<Identifier, AdvancementProgress>> stream = map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));

					for (Entry<Identifier, AdvancementProgress> entry : (List)stream.collect(Collectors.toList())) {
						SimpleAdvancement simpleAdvancement = this.server.method_3851().get((Identifier)entry.getKey());
						if (simpleAdvancement == null) {
							LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), this.advancementFile);
						} else {
							this.initProgress(simpleAdvancement, (AdvancementProgress)entry.getValue());
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

		for (Entry<SimpleAdvancement, AdvancementProgress> entry : this.advancementToProgress.entrySet()) {
			AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
			if (advancementProgress.isAnyObtained()) {
				map.put(((SimpleAdvancement)entry.getKey()).getId(), advancementProgress);
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

	public boolean grantCriterion(SimpleAdvancement simpleAdvancement, String string) {
		boolean bl = false;
		AdvancementProgress advancementProgress = this.getProgress(simpleAdvancement);
		boolean bl2 = advancementProgress.isDone();
		if (advancementProgress.obtain(string)) {
			this.endTrackingCompleted(simpleAdvancement);
			this.progressUpdates.add(simpleAdvancement);
			bl = true;
			if (!bl2 && advancementProgress.isDone()) {
				simpleAdvancement.getRewards().apply(this.owner);
				if (simpleAdvancement.getDisplay() != null
					&& simpleAdvancement.getDisplay().shouldAnnounceToChat()
					&& this.owner.world.getGameRules().getBoolean("announceAdvancements")) {
					this.server
						.getPlayerManager()
						.sendToAll(
							new TranslatableTextComponent(
								"chat.type.advancement." + simpleAdvancement.getDisplay().getFrame().getId(), this.owner.getDisplayName(), simpleAdvancement.getTextComponent()
							)
						);
				}
			}
		}

		if (advancementProgress.isDone()) {
			this.updateDisplay(simpleAdvancement);
		}

		return bl;
	}

	public boolean revokeCriterion(SimpleAdvancement simpleAdvancement, String string) {
		boolean bl = false;
		AdvancementProgress advancementProgress = this.getProgress(simpleAdvancement);
		if (advancementProgress.reset(string)) {
			this.beginTracking(simpleAdvancement);
			this.progressUpdates.add(simpleAdvancement);
			bl = true;
		}

		if (!advancementProgress.isAnyObtained()) {
			this.updateDisplay(simpleAdvancement);
		}

		return bl;
	}

	private void beginTracking(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = this.getProgress(simpleAdvancement);
		if (!advancementProgress.isDone()) {
			for (Entry<String, AdvancementCriterion> entry : simpleAdvancement.getCriteria().entrySet()) {
				CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
				if (criterionProgress != null && !criterionProgress.isObtained()) {
					CriterionConditions criterionConditions = ((AdvancementCriterion)entry.getValue()).getConditions();
					if (criterionConditions != null) {
						Criterion<CriterionConditions> criterion = Criterions.getById(criterionConditions.getId());
						if (criterion != null) {
							criterion.beginTrackingCondition(this, new Criterion.ConditionsContainer<>(criterionConditions, simpleAdvancement, (String)entry.getKey()));
						}
					}
				}
			}
		}
	}

	private void endTrackingCompleted(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = this.getProgress(simpleAdvancement);

		for (Entry<String, AdvancementCriterion> entry : simpleAdvancement.getCriteria().entrySet()) {
			CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
			if (criterionProgress != null && (criterionProgress.isObtained() || advancementProgress.isDone())) {
				CriterionConditions criterionConditions = ((AdvancementCriterion)entry.getValue()).getConditions();
				if (criterionConditions != null) {
					Criterion<CriterionConditions> criterion = Criterions.getById(criterionConditions.getId());
					if (criterion != null) {
						criterion.endTrackingCondition(this, new Criterion.ConditionsContainer<>(criterionConditions, simpleAdvancement, (String)entry.getKey()));
					}
				}
			}
		}
	}

	public void sendUpdate(ServerPlayerEntity serverPlayerEntity) {
		if (this.dirty || !this.visibilityUpdates.isEmpty() || !this.progressUpdates.isEmpty()) {
			Map<Identifier, AdvancementProgress> map = Maps.<Identifier, AdvancementProgress>newHashMap();
			Set<SimpleAdvancement> set = Sets.<SimpleAdvancement>newLinkedHashSet();
			Set<Identifier> set2 = Sets.<Identifier>newLinkedHashSet();

			for (SimpleAdvancement simpleAdvancement : this.progressUpdates) {
				if (this.visibleAdvancements.contains(simpleAdvancement)) {
					map.put(simpleAdvancement.getId(), this.advancementToProgress.get(simpleAdvancement));
				}
			}

			for (SimpleAdvancement simpleAdvancementx : this.visibilityUpdates) {
				if (this.visibleAdvancements.contains(simpleAdvancementx)) {
					set.add(simpleAdvancementx);
				} else {
					set2.add(simpleAdvancementx.getId());
				}
			}

			if (this.dirty || !map.isEmpty() || !set.isEmpty() || !set2.isEmpty()) {
				serverPlayerEntity.networkHandler.sendPacket(new AdvancementUpdateClientPacket(this.dirty, set, set2, map));
				this.visibilityUpdates.clear();
				this.progressUpdates.clear();
			}
		}

		this.dirty = false;
	}

	public void setDisplayTab(@Nullable SimpleAdvancement simpleAdvancement) {
		SimpleAdvancement simpleAdvancement2 = this.currentDisplayTab;
		if (simpleAdvancement != null && simpleAdvancement.getParent() == null && simpleAdvancement.getDisplay() != null) {
			this.currentDisplayTab = simpleAdvancement;
		} else {
			this.currentDisplayTab = null;
		}

		if (simpleAdvancement2 != this.currentDisplayTab) {
			this.owner.networkHandler.sendPacket(new SelectAdvancementTabClientPacket(this.currentDisplayTab == null ? null : this.currentDisplayTab.getId()));
		}
	}

	public AdvancementProgress getProgress(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = (AdvancementProgress)this.advancementToProgress.get(simpleAdvancement);
		if (advancementProgress == null) {
			advancementProgress = new AdvancementProgress();
			this.initProgress(simpleAdvancement, advancementProgress);
		}

		return advancementProgress;
	}

	private void initProgress(SimpleAdvancement simpleAdvancement, AdvancementProgress advancementProgress) {
		advancementProgress.init(simpleAdvancement.getCriteria(), simpleAdvancement.getRequirements());
		this.advancementToProgress.put(simpleAdvancement, advancementProgress);
	}

	private void updateDisplay(SimpleAdvancement simpleAdvancement) {
		boolean bl = this.canSee(simpleAdvancement);
		boolean bl2 = this.visibleAdvancements.contains(simpleAdvancement);
		if (bl && !bl2) {
			this.visibleAdvancements.add(simpleAdvancement);
			this.visibilityUpdates.add(simpleAdvancement);
			if (this.advancementToProgress.containsKey(simpleAdvancement)) {
				this.progressUpdates.add(simpleAdvancement);
			}
		} else if (!bl && bl2) {
			this.visibleAdvancements.remove(simpleAdvancement);
			this.visibilityUpdates.add(simpleAdvancement);
		}

		if (bl != bl2 && simpleAdvancement.getParent() != null) {
			this.updateDisplay(simpleAdvancement.getParent());
		}

		for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
			this.updateDisplay(simpleAdvancement2);
		}
	}

	private boolean canSee(SimpleAdvancement simpleAdvancement) {
		for (int i = 0; simpleAdvancement != null && i <= 2; i++) {
			if (i == 0 && this.hasChildrenDone(simpleAdvancement)) {
				return true;
			}

			if (simpleAdvancement.getDisplay() == null) {
				return false;
			}

			AdvancementProgress advancementProgress = this.getProgress(simpleAdvancement);
			if (advancementProgress.isDone()) {
				return true;
			}

			if (simpleAdvancement.getDisplay().isHidden()) {
				return false;
			}

			simpleAdvancement = simpleAdvancement.getParent();
		}

		return false;
	}

	private boolean hasChildrenDone(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = this.getProgress(simpleAdvancement);
		if (advancementProgress.isDone()) {
			return true;
		} else {
			for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
				if (this.hasChildrenDone(simpleAdvancement2)) {
					return true;
				}
			}

			return false;
		}
	}
}
