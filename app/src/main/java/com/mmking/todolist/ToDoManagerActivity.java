package com.mmking.todolist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;

import com.mmking.todolist.ToDoItem.Priority;
import com.mmking.todolist.ToDoItem.Status;

public class ToDoManagerActivity extends ListActivity {

	private static final int ADD_TODO_ITEM_REQUEST = 0;
	private static final int EDIT_TODO_ITEM_REQUEST = 1;
	private static final String FILE_NAME = "TodoManagerActivityData.txt";
	private static final String TAG = "Lab-UserInterface";

	// IDs for menu items
	private static final int MENU_DELETE = Menu.FIRST;
	private static final int MENU_DUMP = Menu.FIRST + 1;

	ToDoListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a new TodoListAdapter for this ListActivity's ListView
		mAdapter = new ToDoListAdapter(getApplicationContext());

		// Put divider between ToDoItems and FooterView
		getListView().setFooterDividersEnabled(true);

		// Inflate footerView for footer_view.xml file
		TextView footerView = (TextView) getLayoutInflater().inflate(R.layout.footer_view, null);

		// Add footerView to ListView
		getListView().addFooterView(footerView);

		footerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// Start AddToDo Activity
				Intent intent = new Intent(ToDoManagerActivity.this, AddToDoActivity.class);
				intent.putExtra(getString(R.string.data_POSITION), mAdapter.getCount());
				startActivityForResult(intent, ADD_TODO_ITEM_REQUEST);
			}
		});

		// Attach the adapter to this ListActivity's ListView
		getListView().setAdapter(mAdapter);

		// Register ToDoItems to show context menu. Need to set ToDoItems to be long clickable
		registerForContextMenu(getListView());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG,"Entered onActivityResult()");

		// If resultCode is OK and requestCode is from AddToDoActivity
		if (resultCode == RESULT_OK && requestCode == ADD_TODO_ITEM_REQUEST){

			// Create new ToDoItem
			ToDoItem item = new ToDoItem(data);

			// Add it to the adapter
			mAdapter.add(item);
		}
		else if (resultCode == RESULT_OK && requestCode == EDIT_TODO_ITEM_REQUEST){
			// Create new ToDoItem
			ToDoItem item = new ToDoItem(data);

			// Update the adapter
			mAdapter.replace(data.getIntExtra(getString(R.string.data_POSITION), -1), item);
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		// Load saved ToDoItems, if necessary

		if (mAdapter.getCount() == 0)
			loadItems();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Save ToDoItems

		saveItems();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete all");
		//menu.add(Menu.NONE, MENU_DUMP, Menu.NONE, "Dump to log");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DELETE:

			new AlertDialog.Builder(this)
					.setTitle("Delete All")
					.setMessage("Are you sure you want to delete all entries?")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							for (int i = 0; i < mAdapter.getCount(); i++){
								// Cancel alarm
								AlarmUtils.cancelAlarm(getApplicationContext(), i);
							}
							mAdapter.clear();
						}
					})
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// do nothing
						}
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();

			return true;
//		case MENU_DUMP:
//			dump();
//			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

//	private void dump() {
//
//		for (int i = 0; i < mAdapter.getCount(); i++) {
//			String data = ((ToDoItem) mAdapter.getItem(i)).toLog();
//			Log.i(TAG,	"Item " + i + ": " + data.replace(ToDoItem.ITEM_SEP, ","));
//		}
//
//	}

	// Load stored ToDoItems
	private void loadItems() {
		BufferedReader reader = null;
		try {
			FileInputStream fis = openFileInput(FILE_NAME);
			reader = new BufferedReader(new InputStreamReader(fis));

			String title = null;
			String priority = null;
			String status = null;
			Date date = null;

			while (null != (title = reader.readLine())) {
				priority = reader.readLine();
				status = reader.readLine();
				date = ToDoItem.FORMAT.parse(reader.readLine());
				mAdapter.add(new ToDoItem(title, Priority.valueOf(priority),
						Status.valueOf(status), date));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Save ToDoItems to file
	private void saveItems() {
		PrintWriter writer = null;
		try {
			FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					fos)));

			for (int idx = 0; idx < mAdapter.getCount(); idx++) {

				writer.println(mAdapter.getItem(idx));

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				writer.close();
			}
		}
	}

	// Member variable for keeping track of the MenuInfo between submenus of a ListView's items
	private AdapterView.AdapterContextMenuInfo lastMenuInfo = null;

	// Create context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		// Keep track of the current MenuInfo
		lastMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	// Process clicks on context menu items
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		if (menuInfo == null)
		{
			// Grab it from the member placeholder.  If this is still null, it's a bug (?)
			menuInfo = lastMenuInfo;
		}

		final int position = menuInfo.position;
		ToDoItem toDoItem = (ToDoItem) mAdapter.getItem(position);

		switch (item.getItemId()){
			case R.id.delete_item:

				new AlertDialog.Builder(this)
						.setTitle("Delete")
						.setMessage("Are you sure you want to delete this entry?")
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// Cancel the alarm
								AlarmUtils.cancelAlarm(getApplicationContext(), position);

								mAdapter.remove(position);
								mAdapter.notifyDataSetChanged();
							}
						})
						.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// do nothing
							}
						})
						.setIcon(android.R.drawable.ic_dialog_alert)
						.show();

				return true;

			case R.id.edit_item:
				editToDoItem(toDoItem, position);
				return true;

			default:
				return false;
		}
	}

	public void editToDoItem(ToDoItem toDoItem, int position){

		Intent intent = new Intent(this, EditToDoActivity.class);

		ToDoItem.packageIntent(intent, toDoItem.getTitle(), toDoItem.getPriority(),
				toDoItem.getStatus(), toDoItem.getDate().toString());

		intent.putExtra(getString(R.string.data_POSITION), position);

		startActivityForResult(intent, EDIT_TODO_ITEM_REQUEST);
	}

}