package com.mmking.todolist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yang on 11/23/2015.
 */
public class EditToDoActivity extends Activity {

    // 7 days in milliseconds - 7 * 24 * 60 * 60 * 1000
    private static final int SEVEN_DAYS = 604800000;

    private Context context;

    private static final String TAG = "Lab-UserInterface";

    private static String timeString;
    private static String dateString;
    private static TextView dateView;
    private static TextView timeView;

    private String mTitle;
    private ToDoItem.Priority mPriority;
    private ToDoItem.Status mStatus;
    private Date mDate;
    private int mPosition;

    private RadioGroup mPriorityRadioGroup;
    private RadioGroup mStatusRadioGroup;
    private EditText mTitleText;
    private RadioButton mDefaultStatusButton;
    private RadioButton mDefaultPriorityButton;
    private CheckBox alarmCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo);

        mTitleText = (EditText) findViewById(R.id.title);
        mDefaultStatusButton = (RadioButton) findViewById(R.id.statusNotDone);
        mDefaultPriorityButton = (RadioButton) findViewById(R.id.medPriority);
        mPriorityRadioGroup = (RadioGroup) findViewById(R.id.priorityGroup);
        mStatusRadioGroup = (RadioGroup) findViewById(R.id.statusGroup);
        dateView = (TextView) findViewById(R.id.date);
        timeView = (TextView) findViewById(R.id.time);
        //alarmCheck = (CheckBox) findViewById(R.id.alarm_check);

        // Initialize context
        context = getApplicationContext();

        // Set up the data from the intent
        setUp();

        // OnClickListener for the Date button, calls showDatePickerDialog() to
        // show the Date dialog

        final Button datePickerButton = (Button) findViewById(R.id.date_picker_button);
        datePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // OnClickListener for the Time button, calls showTimePickerDialog() to
        // show the Time Dialog

        final Button timePickerButton = (Button) findViewById(R.id.time_picker_button);
        timePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // OnClickListener for the Cancel Button,

        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Indicate result and finish
                setResult(RESULT_CANCELED);
                finish();


            }
        });

        // OnClickListener for the Reset Button
        final Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Reset to the way it was before editing
                setUp();
            }
        });

        // Set up OnClickListener for the Submit Button

        final Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the current Priority
                ToDoItem.Priority priority = getPriority();

                // Get the current Status
                ToDoItem.Status status = getStatus();

                // Get the current ToDoItem Title
                String titleString = mTitleText.getText().toString();


                // Construct the Date string
                String fullDate = dateString + " " + timeString;

                // Package ToDoItem data into an Intent
                Intent data = new Intent();
                ToDoItem.packageIntent(data, titleString, priority, status,
                        fullDate);

                data.putExtra(getString(R.string.data_POSITION), mPosition);

                // Set the alarm
//                if (alarmCheck.isChecked() && status == ToDoItem.Status.NOTDONE) {
//                    AlarmUtils.setAlarm(context, mPosition, mDate, titleString);
//                }

                // Return data Intent and finish
                setResult(RESULT_OK, data);
                finish();

            }
        });
    }

    private void setUp(){

        Intent intent = getIntent();

        mTitle = intent.getStringExtra(ToDoItem.TITLE);
        mPriority = ToDoItem.Priority.valueOf(intent.getStringExtra(ToDoItem.PRIORITY));
        mStatus = ToDoItem.Status.valueOf(intent.getStringExtra(ToDoItem.STATUS));

        try {
            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            mDate =  df.parse(intent.getStringExtra(ToDoItem.DATE));
        } catch (ParseException e) {
            mDate = new Date();
        }

        // Set the title
        mTitleText.setText(mTitle);

        // Set the priority
        switch (mPriority){
            case LOW:
                mPriorityRadioGroup.check(R.id.lowPriority);
                break;
            case MED:
                mPriorityRadioGroup.check(R.id.medPriority);
                break;
            case HIGH:
                mPriorityRadioGroup.check(R.id.highPriority);
                break;
            default:
                mPriorityRadioGroup.check(mDefaultPriorityButton.getId());
                break;
        }

        // Set the status
        switch (mStatus){
            case DONE:
                mStatusRadioGroup.check(R.id.statusDone);
                break;
            case NOTDONE:
                mStatusRadioGroup.check(R.id.statusNotDone);
                break;
            default:
                mStatusRadioGroup.check(mDefaultStatusButton.getId());
                break;
        }

        // Set the date and time
        setDateTime(mDate);

        // Get position
        mPosition = intent.getIntExtra(getString(R.string.data_POSITION), -1);

        // FIXME might need a try catch
        // Cancel existing alarms
        AlarmUtils.cancelAlarm(context, mPosition);
    }

    private void setDateTime(Date mDate) {

        if (mDate == null) {
            // Default is current time + 7 days
            mDate = new Date();
            mDate = new Date(mDate.getTime() + SEVEN_DAYS);
        }

        Calendar c = Calendar.getInstance();
        c.setTime(mDate);

        setDateString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));

        dateView.setText(dateString);

        setTimeString(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                c.get(Calendar.MILLISECOND));

        timeView.setText(timeString);
    }

    private static void setDateString(int year, int monthOfYear, int dayOfMonth) {

        // Increment monthOfYear for Calendar/Date -> Time Format setting
        monthOfYear++;
        String mon = "" + monthOfYear;
        String day = "" + dayOfMonth;

        if (monthOfYear < 10)
            mon = "0" + monthOfYear;
        if (dayOfMonth < 10)
            day = "0" + dayOfMonth;

        dateString = year + "-" + mon + "-" + day;
    }

    private static void setTimeString(int hourOfDay, int minute, int mili) {
        String hour = "" + hourOfDay;
        String min = "" + minute;

        if (hourOfDay < 10)
            hour = "0" + hourOfDay;
        if (minute < 10)
            min = "0" + minute;

        timeString = hour + ":" + min + ":00";
    }

    private ToDoItem.Priority getPriority() {

        switch (mPriorityRadioGroup.getCheckedRadioButtonId()) {
            case R.id.lowPriority: {
                return ToDoItem.Priority.LOW;
            }
            case R.id.highPriority: {
                return ToDoItem.Priority.HIGH;
            }
            default: {
                return ToDoItem.Priority.MED;
            }
        }
    }

    private ToDoItem.Status getStatus() {

        switch (mStatusRadioGroup.getCheckedRadioButtonId()) {
            case R.id.statusDone: {
                return ToDoItem.Status.DONE;
            }
            default: {
                return ToDoItem.Status.NOTDONE;
            }
        }
    }

    private String getToDoTitle() {
        return mTitleText.getText().toString();
    }


    // DialogFragment used to pick a ToDoItem deadline date

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            setDateString(year, monthOfYear, dayOfMonth);

            dateView.setText(dateString);
        }

    }

    // DialogFragment used to pick a ToDoItem deadline time

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setTimeString(hourOfDay, minute, 0);

            timeView.setText(timeString);
        }
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

}
