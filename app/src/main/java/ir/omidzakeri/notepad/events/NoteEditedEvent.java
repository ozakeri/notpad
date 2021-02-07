package ir.omidzakeri.notepad.events;

import ir.omidzakeri.notepad.database.NotesDAO;
import ir.omidzakeri.notepad.models.Note;

/**
 * Created by Omid on 8/21/2016.
 */
public class NoteEditedEvent{
	int noteId;

	public NoteEditedEvent(int noteId){
		this.noteId = noteId;
	}

	public int getNoteId(){
		return noteId;
	}
	
	public Note getNote(){
		return NotesDAO.getNote(noteId);
	}
}
