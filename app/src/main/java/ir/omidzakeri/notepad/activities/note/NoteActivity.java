package ir.omidzakeri.notepad.activities.note;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.richedit.RichEditText;
import com.greenfrvr.hashtagview.HashtagView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.omidzakeri.notepad.R;
import ir.omidzakeri.notepad.Views.RichEditWidgetView;
import ir.omidzakeri.notepad.activities.addtofolders.AddToFoldersActivityIntentBuilder;
import ir.omidzakeri.notepad.database.FolderNoteDAO;
import ir.omidzakeri.notepad.database.NotesDAO;
import ir.omidzakeri.notepad.events.NoteDeletedEvent;
import ir.omidzakeri.notepad.events.NoteEditedEvent;
import ir.omidzakeri.notepad.events.NoteFoldersUpdatedEvent;
import ir.omidzakeri.notepad.models.Folder;
import ir.omidzakeri.notepad.models.Note;
import ir.omidzakeri.notepad.models.Note_Table;
import ir.omidzakeri.notepad.utils.TimeUtils;
import ir.omidzakeri.notepad.utils.Utils;
import ir.omidzakeri.notepad.utils.ViewUtils;
import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;

/**
 * Created by Omid on 8/20/2016.
 */
@IntentBuilder
public class NoteActivity extends AppCompatActivity{
	private static final String TAG = "NoteActivity";

	@Extra @Nullable
	Integer noteId;
	Note note;

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.title) EditText title;
	@BindView(R.id.body) RichEditText body;
	@BindView(R.id.folders_tag_view) HashtagView foldersTagView;
	@BindView(R.id.drawing_image) ImageView drawingImage;
	@BindView(R.id.create_time_text) TextView creationTimeTextView;
	@BindView(R.id.rich_edit_widget)
	RichEditWidgetView richEditWidgetView;
	private boolean shouldFireDeleteEvent = false;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		NoteActivityIntentBuilder.inject(getIntent(), this);
		ButterKnife.bind(this);
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationIcon(ViewUtils.tintDrawable(R.drawable.ic_arrow_back_white_24dp, R.color.md_blue_grey_400));
		mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				onBackPressed();
			}
		});

		if (noteId == null){
			note = new Note();
			Date now = new Date();
			note.setCreatedAt(now);
			note.save();
			noteId = note.getId();
		}

		richEditWidgetView.setRichEditView(body);

		bind();

		foldersTagView.addOnTagClickListener(new HashtagView.TagsClickListener(){
			@Override public void onItemClicked(Object item){
				Toast.makeText(NoteActivity.this, "Folder Clicked", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void bind(){
		note = NotesDAO.getNote(noteId);
		if (note.getTitle() != null){
			title.setText(note.getTitle());
		}
		if (note.getBody() != null){
			body.setText(note.getSpannedBody());
		}
		foldersTagView.setData(FolderNoteDAO.getFolders(note.getId()), new HashtagView.DataTransform<Folder>(){
			@Override public CharSequence prepare(Folder item){
				return item.getName();
			}
		});
		if (note.getDrawingTrimmed() == null)
			drawingImage.setVisibility(View.GONE);
		else{
			drawingImage.setVisibility(View.VISIBLE);
			drawingImage.setImageBitmap(Utils.getImage(note.getDrawingTrimmed().getBlob()));
		}
		creationTimeTextView.setText("Created " + TimeUtils.getHumanReadableTimeDiff(note.getCreatedAt()));
	}

	@Override protected void onStop(){
		super.onStop();
		EventBus.getDefault().unregister(this);
	}

	@Override protected void onStart(){
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.note_menu, menu);
		ViewUtils.tintMenu(menu, R.id.delete_note, R.color.md_blue_grey_400);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId() == R.id.delete_note){
			SQLite.delete().from(Note.class).where(Note_Table.id.is(note.getId())).execute();
			shouldFireDeleteEvent = true;
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@OnClick({ R.id.edit_drawing_button, R.id.drawing_image }) void clickEditDrawingButton(){
		Intent intent = new DrawingActivityIntentBuilder(note.getId()).build(this);
		startActivity(intent);
	}

	@OnClick(R.id.edit_folders_button) void clickEditFoldersButton(){
		Intent intent = new AddToFoldersActivityIntentBuilder(note.getId()).build(this);
		startActivity(intent);
	}

	@Subscribe(threadMode = ThreadMode.MAIN) public void onNoteEditedEvent(NoteEditedEvent noteEditedEvent){
		Log.e(TAG, "onNoteEditedEvent() called with: " + "noteEditedEvent = [" + noteEditedEvent + "]");
		if (note.getId() == noteEditedEvent.getNote().getId()){
			bind();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onNoteFoldersUpdatedEvent(NoteFoldersUpdatedEvent noteFoldersUpdatedEvent){
		if (note.getId() == noteFoldersUpdatedEvent.getNoteId()){
			bind();
		}
	}

	@Override public void onBackPressed(){
		super.onBackPressed();
		assert note != null;
		if (shouldFireDeleteEvent){
			EventBus.getDefault().postSticky(new NoteDeletedEvent(note));
		}else{
			String processedTitle = title.getText().toString().trim();
			String processedBody = body.getText().toString().trim();
			if (TextUtils.isEmpty(processedTitle) && TextUtils.isEmpty(processedBody) && note.getDrawingTrimmed() == null){
				SQLite.delete().from(Note.class).where(Note_Table.id.is(note.getId())).execute();
				return;
			}
			note.setSpannedBody(body.getText());
			note.setTitle(processedTitle);
			note.save();
			EventBus.getDefault().postSticky(new NoteEditedEvent(note.getId()));
		}
	}
}