package io.github.jitinsharma.insplore.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jitin on 01/07/16.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    int minYear;
    int minMonth;
    int maxMonth;
    int maxYear;
    Date minDate;
    Date maxDate;
    Bundle bundle;

    public OnDatePickedListener getOnDatePickedListener() {
        return onDatePickedListener;
    }

    public void setOnDatePickedListener(OnDatePickedListener onDatePickedListener) {
        this.onDatePickedListener = onDatePickedListener;
    }

    private OnDatePickedListener onDatePickedListener;

    public static DatePickerFragment newInstance(int requestCode, OnDatePickedListener listener) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        datePickerFragment.setOnDatePickedListener(listener);
        return datePickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (bundle!=null){
        }
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, -1);

        Calendar minDate = Calendar.getInstance();
        minDate.set(2011,1,1);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.getDatePicker().updateDate(year-1, month+1, day);
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        onDatePickedListener.onDatePicked(year, month, day, 1);
    }

    public interface OnDatePickedListener{
        void onDatePicked(int year, int month, int day, int requestCode);
    }
}
