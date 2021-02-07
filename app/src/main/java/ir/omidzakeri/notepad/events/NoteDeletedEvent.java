package ir.omidzakeri.notepad.events;

import ir.omidzakeri.notepad.models.Note;

/**
 * Created by Omid on 8/21/2016.
 */
public class NoteDeletedEvent{
	Note note;

	public NoteDeletedEvent(Note note){
		this.note = note;
	}

	public Note getNote(){
		return note;
	}
}
