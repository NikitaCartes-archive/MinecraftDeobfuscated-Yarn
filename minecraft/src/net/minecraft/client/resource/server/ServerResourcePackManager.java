package net.minecraft.client.resource.server;

import com.google.common.hash.HashCode;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Downloader;

@Environment(EnvType.CLIENT)
public class ServerResourcePackManager {
	private final DownloadQueuer queuer;
	final PackStateChangeCallback stateChangeCallback;
	private final ReloadScheduler reloadScheduler;
	private final Runnable packChangeCallback;
	private ServerResourcePackManager.AcceptanceStatus acceptanceStatus;
	final List<ServerResourcePackManager.PackEntry> packs = new ArrayList();

	public ServerResourcePackManager(
		DownloadQueuer queuer,
		PackStateChangeCallback stateChangeCallback,
		ReloadScheduler reloadScheduler,
		Runnable packChangeCallback,
		ServerResourcePackManager.AcceptanceStatus acceptanceStatus
	) {
		this.queuer = queuer;
		this.stateChangeCallback = stateChangeCallback;
		this.reloadScheduler = reloadScheduler;
		this.packChangeCallback = packChangeCallback;
		this.acceptanceStatus = acceptanceStatus;
	}

	void onPackChanged() {
		this.packChangeCallback.run();
	}

	private void markReplaced(UUID id) {
		for (ServerResourcePackManager.PackEntry packEntry : this.packs) {
			if (packEntry.id.equals(id)) {
				packEntry.discard(ServerResourcePackManager.DiscardReason.SERVER_REPLACED);
			}
		}
	}

	public void addResourcePack(UUID id, URL url, @Nullable HashCode hashCode) {
		if (this.acceptanceStatus == ServerResourcePackManager.AcceptanceStatus.DECLINED) {
			this.stateChangeCallback.onFinish(id, PackStateChangeCallback.FinishState.DECLINED);
		} else {
			this.onAdd(id, new ServerResourcePackManager.PackEntry(id, url, hashCode));
		}
	}

	public void addResourcePack(UUID id, Path path) {
		if (this.acceptanceStatus == ServerResourcePackManager.AcceptanceStatus.DECLINED) {
			this.stateChangeCallback.onFinish(id, PackStateChangeCallback.FinishState.DECLINED);
		} else {
			URL uRL;
			try {
				uRL = path.toUri().toURL();
			} catch (MalformedURLException var5) {
				throw new IllegalStateException("Can't convert path to URL " + path, var5);
			}

			ServerResourcePackManager.PackEntry packEntry = new ServerResourcePackManager.PackEntry(id, uRL, null);
			packEntry.loadStatus = ServerResourcePackManager.LoadStatus.DONE;
			packEntry.path = path;
			this.onAdd(id, packEntry);
		}
	}

	private void onAdd(UUID id, ServerResourcePackManager.PackEntry pack) {
		this.markReplaced(id);
		this.packs.add(pack);
		if (this.acceptanceStatus == ServerResourcePackManager.AcceptanceStatus.ALLOWED) {
			this.accept(pack);
		}

		this.onPackChanged();
	}

	private void accept(ServerResourcePackManager.PackEntry pack) {
		this.stateChangeCallback.onStateChanged(pack.id, PackStateChangeCallback.State.ACCEPTED);
		pack.accepted = true;
	}

	@Nullable
	private ServerResourcePackManager.PackEntry get(UUID id) {
		for (ServerResourcePackManager.PackEntry packEntry : this.packs) {
			if (!packEntry.isDiscarded() && packEntry.id.equals(id)) {
				return packEntry;
			}
		}

		return null;
	}

	public void remove(UUID id) {
		ServerResourcePackManager.PackEntry packEntry = this.get(id);
		if (packEntry != null) {
			packEntry.discard(ServerResourcePackManager.DiscardReason.SERVER_REMOVED);
			this.onPackChanged();
		}
	}

	public void removeAll() {
		for (ServerResourcePackManager.PackEntry packEntry : this.packs) {
			packEntry.discard(ServerResourcePackManager.DiscardReason.SERVER_REMOVED);
		}

		this.onPackChanged();
	}

	public void acceptAll() {
		this.acceptanceStatus = ServerResourcePackManager.AcceptanceStatus.ALLOWED;

		for (ServerResourcePackManager.PackEntry packEntry : this.packs) {
			if (!packEntry.accepted && !packEntry.isDiscarded()) {
				this.accept(packEntry);
			}
		}

		this.onPackChanged();
	}

	public void declineAll() {
		this.acceptanceStatus = ServerResourcePackManager.AcceptanceStatus.DECLINED;

		for (ServerResourcePackManager.PackEntry packEntry : this.packs) {
			if (!packEntry.accepted) {
				packEntry.discard(ServerResourcePackManager.DiscardReason.DECLINED);
			}
		}

		this.onPackChanged();
	}

	public void resetAcceptanceStatus() {
		this.acceptanceStatus = ServerResourcePackManager.AcceptanceStatus.PENDING;
	}

	public void update() {
		boolean bl = this.enqueueDownloads();
		if (!bl) {
			this.applyDownloadedPacks();
		}

		this.removeInactivePacks();
	}

	private void removeInactivePacks() {
		this.packs.removeIf(pack -> {
			if (pack.status != ServerResourcePackManager.Status.INACTIVE) {
				return false;
			} else if (pack.discardReason != null) {
				PackStateChangeCallback.FinishState finishState = pack.discardReason.state;
				if (finishState != null) {
					this.stateChangeCallback.onFinish(pack.id, finishState);
				}

				return true;
			} else {
				return false;
			}
		});
	}

	private void onDownload(Collection<ServerResourcePackManager.PackEntry> packs, Downloader.DownloadResult result) {
		if (!result.failed().isEmpty()) {
			for (ServerResourcePackManager.PackEntry packEntry : this.packs) {
				if (packEntry.status != ServerResourcePackManager.Status.ACTIVE) {
					if (result.failed().contains(packEntry.id)) {
						packEntry.discard(ServerResourcePackManager.DiscardReason.DOWNLOAD_FAILED);
					} else {
						packEntry.discard(ServerResourcePackManager.DiscardReason.DISCARDED);
					}
				}
			}
		}

		for (ServerResourcePackManager.PackEntry packEntryx : packs) {
			Path path = (Path)result.downloaded().get(packEntryx.id);
			if (path != null) {
				packEntryx.loadStatus = ServerResourcePackManager.LoadStatus.DONE;
				packEntryx.path = path;
				if (!packEntryx.isDiscarded()) {
					this.stateChangeCallback.onStateChanged(packEntryx.id, PackStateChangeCallback.State.DOWNLOADED);
				}
			}
		}

		this.onPackChanged();
	}

	private boolean enqueueDownloads() {
		List<ServerResourcePackManager.PackEntry> list = new ArrayList();
		boolean bl = false;

		for (ServerResourcePackManager.PackEntry packEntry : this.packs) {
			if (!packEntry.isDiscarded() && packEntry.accepted) {
				if (packEntry.loadStatus != ServerResourcePackManager.LoadStatus.DONE) {
					bl = true;
				}

				if (packEntry.loadStatus == ServerResourcePackManager.LoadStatus.REQUESTED) {
					packEntry.loadStatus = ServerResourcePackManager.LoadStatus.PENDING;
					list.add(packEntry);
				}
			}
		}

		if (!list.isEmpty()) {
			Map<UUID, Downloader.DownloadEntry> map = new HashMap();

			for (ServerResourcePackManager.PackEntry packEntry2 : list) {
				map.put(packEntry2.id, new Downloader.DownloadEntry(packEntry2.url, packEntry2.hashCode));
			}

			this.queuer.enqueue(map, result -> this.onDownload(list, result));
		}

		return bl;
	}

	private void applyDownloadedPacks() {
		boolean bl = false;
		final List<ServerResourcePackManager.PackEntry> list = new ArrayList();
		final List<ServerResourcePackManager.PackEntry> list2 = new ArrayList();

		for (ServerResourcePackManager.PackEntry packEntry : this.packs) {
			if (packEntry.status == ServerResourcePackManager.Status.PENDING) {
				return;
			}

			boolean bl2 = packEntry.accepted && packEntry.loadStatus == ServerResourcePackManager.LoadStatus.DONE && !packEntry.isDiscarded();
			if (bl2 && packEntry.status == ServerResourcePackManager.Status.INACTIVE) {
				list.add(packEntry);
				bl = true;
			}

			if (packEntry.status == ServerResourcePackManager.Status.ACTIVE) {
				if (!bl2) {
					bl = true;
					list2.add(packEntry);
				} else {
					list.add(packEntry);
				}
			}
		}

		if (bl) {
			for (ServerResourcePackManager.PackEntry packEntry : list) {
				if (packEntry.status != ServerResourcePackManager.Status.ACTIVE) {
					packEntry.status = ServerResourcePackManager.Status.PENDING;
				}
			}

			for (ServerResourcePackManager.PackEntry packEntryx : list2) {
				packEntryx.status = ServerResourcePackManager.Status.PENDING;
			}

			this.reloadScheduler.scheduleReload(new ReloadScheduler.ReloadContext() {
				@Override
				public void onSuccess() {
					for (ServerResourcePackManager.PackEntry packEntry : list) {
						packEntry.status = ServerResourcePackManager.Status.ACTIVE;
						if (packEntry.discardReason == null) {
							ServerResourcePackManager.this.stateChangeCallback.onFinish(packEntry.id, PackStateChangeCallback.FinishState.APPLIED);
						}
					}

					for (ServerResourcePackManager.PackEntry packEntryx : list2) {
						packEntryx.status = ServerResourcePackManager.Status.INACTIVE;
					}

					ServerResourcePackManager.this.onPackChanged();
				}

				@Override
				public void onFailure(boolean force) {
					if (!force) {
						list.clear();

						for (ServerResourcePackManager.PackEntry packEntry : ServerResourcePackManager.this.packs) {
							switch (packEntry.status) {
								case ACTIVE:
									list.add(packEntry);
									break;
								case PENDING:
									packEntry.status = ServerResourcePackManager.Status.INACTIVE;
									packEntry.discard(ServerResourcePackManager.DiscardReason.ACTIVATION_FAILED);
									break;
								case INACTIVE:
									packEntry.discard(ServerResourcePackManager.DiscardReason.DISCARDED);
							}
						}

						ServerResourcePackManager.this.onPackChanged();
					} else {
						for (ServerResourcePackManager.PackEntry packEntry : ServerResourcePackManager.this.packs) {
							if (packEntry.status == ServerResourcePackManager.Status.PENDING) {
								packEntry.status = ServerResourcePackManager.Status.INACTIVE;
							}
						}
					}
				}

				@Override
				public List<ReloadScheduler.PackInfo> getPacks() {
					return list.stream().map(pack -> new ReloadScheduler.PackInfo(pack.id, pack.path)).toList();
				}
			});
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum AcceptanceStatus {
		PENDING,
		ALLOWED,
		DECLINED;
	}

	@Environment(EnvType.CLIENT)
	static enum DiscardReason {
		DOWNLOAD_FAILED(PackStateChangeCallback.FinishState.DOWNLOAD_FAILED),
		ACTIVATION_FAILED(PackStateChangeCallback.FinishState.ACTIVATION_FAILED),
		DECLINED(PackStateChangeCallback.FinishState.DECLINED),
		DISCARDED(PackStateChangeCallback.FinishState.DISCARDED),
		SERVER_REMOVED(null),
		SERVER_REPLACED(null);

		@Nullable
		final PackStateChangeCallback.FinishState state;

		private DiscardReason(@Nullable PackStateChangeCallback.FinishState state) {
			this.state = state;
		}
	}

	@Environment(EnvType.CLIENT)
	static enum LoadStatus {
		REQUESTED,
		PENDING,
		DONE;
	}

	@Environment(EnvType.CLIENT)
	static class PackEntry {
		final UUID id;
		final URL url;
		@Nullable
		final HashCode hashCode;
		@Nullable
		Path path;
		@Nullable
		ServerResourcePackManager.DiscardReason discardReason;
		ServerResourcePackManager.LoadStatus loadStatus = ServerResourcePackManager.LoadStatus.REQUESTED;
		ServerResourcePackManager.Status status = ServerResourcePackManager.Status.INACTIVE;
		boolean accepted;

		PackEntry(UUID id, URL url, @Nullable HashCode hashCode) {
			this.id = id;
			this.url = url;
			this.hashCode = hashCode;
		}

		public void discard(ServerResourcePackManager.DiscardReason reason) {
			if (this.discardReason == null) {
				this.discardReason = reason;
			}
		}

		public boolean isDiscarded() {
			return this.discardReason != null;
		}
	}

	@Environment(EnvType.CLIENT)
	static enum Status {
		INACTIVE,
		PENDING,
		ACTIVE;
	}
}
