package net.minecraft.resource;

import java.util.List;
import java.util.Map;
import net.minecraft.util.Identifier;

public class ResourceFinder {
	private final String directoryName;
	private final String fileExtension;

	public ResourceFinder(String directoryName, String fileExtension) {
		this.directoryName = directoryName;
		this.fileExtension = fileExtension;
	}

	public static ResourceFinder json(String directoryName) {
		return new ResourceFinder(directoryName, ".json");
	}

	/**
	 * {@return an identifier that is used as a file path for locating the resource {@code id}}
	 */
	public Identifier toResourcePath(Identifier id) {
		return id.withPath(this.directoryName + "/" + id.getPath() + this.fileExtension);
	}

	/**
	 * {@return an identifier of the resource located at the file path {@code path}}
	 */
	public Identifier toResourceId(Identifier path) {
		String string = path.getPath();
		return path.withPath(string.substring(this.directoryName.length() + 1, string.length() - this.fileExtension.length()));
	}

	public Map<Identifier, Resource> findResources(ResourceManager resourceManager) {
		return resourceManager.findResources(this.directoryName, path -> path.getPath().endsWith(this.fileExtension));
	}

	public Map<Identifier, List<Resource>> findAllResources(ResourceManager resourceManager) {
		return resourceManager.findAllResources(this.directoryName, path -> path.getPath().endsWith(this.fileExtension));
	}
}
