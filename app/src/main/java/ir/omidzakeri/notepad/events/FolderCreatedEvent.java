package ir.omidzakeri.notepad.events;

import ir.omidzakeri.notepad.models.Folder;

/**
 * Created by Omid on 8/19/2016.
 */
public class FolderCreatedEvent{
	private Folder folder;

	public FolderCreatedEvent(Folder folder){
		this.folder = folder;
	}

	public Folder getFolder(){
		return folder;
	}
}
