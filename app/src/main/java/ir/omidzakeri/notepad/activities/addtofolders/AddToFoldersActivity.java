package ir.omidzakeri.notepad.activities.addtofolders;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.omidzakeri.notepad.R;
import ir.omidzakeri.notepad.events.NoteFoldersUpdatedEvent;
import org.greenrobot.eventbus.EventBus;
import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;

/**
 * Created by Omid on 8/21/2016.
 */
@IntentBuilder
public class AddToFoldersActivity extends AppCompatActivity{
	private static final String TAG = "AddToFoldersActivity";

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.recycler_view) RecyclerView mRecyclerView;
	Adapter adapter;
	@Extra Integer noteId;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_of_folders);
		AddToFoldersActivityIntentBuilder.inject(getIntent(), this);
		ButterKnife.bind(this);
		setSupportActionBar(mToolbar);
		mToolbar.setTitle("Add to folders");
		mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				onBackPressed();
			}
		});
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(RecyclerView.VERTICAL);
		mRecyclerView.setLayoutManager(llm);
		adapter = new Adapter(noteId);
		mRecyclerView.setAdapter(adapter);
		adapter.loadFromDatabase();
	}

	@Override protected void onStart(){
		super.onStart();
		adapter.registerEventBus();
	}

	@Override protected void onStop(){
		super.onStop();
		adapter.unregisterEventBus();
		EventBus.getDefault().post(new NoteFoldersUpdatedEvent(noteId));
	}
}
