package net.minecraft.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

public class ResourceNotFoundException extends FileNotFoundException {
	public ResourceNotFoundException(File packSource, String resource) {
		super(String.format(Locale.ROOT, "'%s' in ResourcePack '%s'", resource, packSource));
	}
}
