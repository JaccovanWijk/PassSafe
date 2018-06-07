package com.example.jacco.passsave;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacco on 5-6-2018.
 */

public class AccountsAdapter extends ArrayAdapter<String>{

    private ArrayList<String> accounts;

    public AccountsAdapter(@NonNull Context context, @NonNull ArrayList<String> accounts) {
        super(context, R.layout.item, accounts);
        this.accounts = accounts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.account);
        textView.setText(accounts.get(position));

        return convertView;
    }

}
