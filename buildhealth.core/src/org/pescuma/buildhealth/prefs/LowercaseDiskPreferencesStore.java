package org.pescuma.buildhealth.prefs;

public class LowercaseDiskPreferencesStore extends LowercasePreferencesStore implements DiskPreferencesStore {
	
	public LowercaseDiskPreferencesStore(DiskPreferencesStore next) {
		super(next);
	}
	
	@Override
	public void saveToDisk() {
		((DiskPreferencesStore) next).saveToDisk();
	}
}
