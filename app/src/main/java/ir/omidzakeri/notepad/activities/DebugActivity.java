package ir.omidzakeri.notepad.activities;

import android.os.Bundle;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.omidzakeri.notepad.R;
import ir.omidzakeri.notepad.database.AppDatabase;
import ir.omidzakeri.notepad.database.FoldersDAO;
import ir.omidzakeri.notepad.models.Folder;
import ir.omidzakeri.notepad.models.FolderNoteRelation;
import ir.omidzakeri.notepad.models.Note;
import ir.omidzakeri.notepad.models.Note_Table;

/**
 * Created by Omid on 8/20/2016.
 */
public class DebugActivity extends AppCompatActivity{
	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		ButterKnife.bind(this);
	}

	@OnClick(R.id.assign_to_folders) void assignToFolders(){
		Note note = SQLite.select().from(Note.class).orderBy(Note_Table.createdAt, false).querySingle();
		List<Folder> folders = FoldersDAO.getLatestFolders();
		for (Folder folder : folders){
			FolderNoteRelation fnr = new FolderNoteRelation();
			fnr.setFolder(folder);
			fnr.setNote(note);
			fnr.save();
		}
	}

	@OnClick(R.id.create_5_notes) void create5Notes(){
		AppDatabase.Utils.createSomeNotes(5);
	}
}
