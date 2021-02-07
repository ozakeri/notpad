package ir.omidzakeri.notepad.events;

import ir.omidzakeri.notepad.database.NotesDAO;
import ir.omidzakeri.notepad.models.Note;

/**
 * Created by Omid on 8/22/2016.
 */
public class NoteFoldersUpdatedEvent{

	int noteId;

	public NoteFoldersUpdatedEvent(int noteId){
		this.noteId = noteId;
	}

	public int getNoteId(){
		return noteId;
	}

	public Note getNote(){
		return NotesDAO.getNote(noteId);
	}
}
