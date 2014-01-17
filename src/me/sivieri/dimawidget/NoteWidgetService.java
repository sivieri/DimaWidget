package me.sivieri.dimawidget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class NoteWidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new NoteWidgetServiceFactory(this.getApplicationContext(), intent);
	}
}

class Note {
	private int id;
	private String title;
	private String content;

	public Note(int id, String title, String content) {
		this.id = id;
		this.title = title;
		this.content = content;
	}

	public int getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public String getContent() {
		return this.content;
	}

}

class NoteWidgetServiceFactory implements RemoteViewsService.RemoteViewsFactory {

	private List<Note> notes = new ArrayList<Note>();
	private Context context;

	public NoteWidgetServiceFactory(Context applicationContext, Intent intent) {
		this.context = applicationContext;
	}

	@Override
	public int getCount() {
		return this.notes.size();
	}

	@Override
	public long getItemId(int arg0) {
		return this.notes.get(arg0).getId();
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int arg0) {
		Note note = this.notes.get(arg0);
		RemoteViews rv = new RemoteViews(this.context.getPackageName(), R.layout.note_item);
		rv.setTextViewText(R.id.noteTitle, note.getTitle());
		rv.setTextViewText(R.id.noteContent, note.getContent());

		return rv;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
		String[] projection = { "_id", "title", "content" };
		Cursor cursor = this.context.getContentResolver().query(Uri.parse("content://me.sivieri.dimatodos.notescontentprovider/notes"), projection, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			for (boolean hasNext = cursor.moveToFirst(); hasNext; hasNext = cursor.moveToNext()) {
				Note note = new Note(cursor.getInt(cursor.getColumnIndexOrThrow("_id")), cursor.getString(cursor.getColumnIndexOrThrow("title")), cursor.getString(cursor
				        .getColumnIndexOrThrow("content")));
				this.notes.add(note);
			}
		}
	}

	@Override
	public void onDataSetChanged() {
	}

	@Override
	public void onDestroy() {
		this.notes.clear();
	}

}
