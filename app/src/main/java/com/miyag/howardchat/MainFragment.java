package com.miyag.howardchat;

/**
 * Created by miyag on 8/7/17.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        // Get references to text views to display database data.
        final ListView messageListView = v.findViewById(R.id.all_messages_list_views);
        final EditText specificUsermessagesTextView = v.findViewById(R.id.queried_pings_list_view);
        specificUsermessagesTextView.setText("");

        // Set up messagesListView
        MessageSource.get(getContext()).getmessages(new MessageSource.PingListener() {
            @Override
            public void onmessagesReceived(List<Message> pingList) {
                messagesArrayAdapter adapter = new messagesArrayAdapter(getContext(), pingList);
                messageListView.setAdapter(adapter);
                // Whenever we set a new adapter (which usually scrolls to the top of the contents), scroll to the bottom of the contents.
                messageListView.setSelection(adapter.getCount() - 1);
            }
        });


        Button button = v.findViewById(R.id.send_ping_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get logged-in user data, create Message, send it.
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(getContext(), "Can't send messages, not logged in", Toast.LENGTH_SHORT);
                    return;
                }
                if(specificUsermessagesTextView.getText().toString().trim().isEmpty()||specificUsermessagesTextView.getText().toString().trim().length()==0
                    ||specificUsermessagesTextView.getText().toString().trim()== null){
                    Toast toast = Toast.makeText(getContext(), "Can't send messages, no text to send", Toast.LENGTH_SHORT);
                    toast.show();
                }

                Message messages = new Message(user.getDisplayName(), user.getUid(),specificUsermessagesTextView.getText().toString());
                MessageSource.get(getContext()).sendPing(messages);
                specificUsermessagesTextView.setText("");
            }
        });

        return v;
    }

    private class messagesArrayAdapter extends BaseAdapter {
        protected Context mContext;
        protected List<Message> mmessagesList;
        protected LayoutInflater mLayoutInflater;
        public messagesArrayAdapter(Context context, List<Message> messagesList) {
            mContext = context;
            mmessagesList = messagesList;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mmessagesList.size();
        }

        @Override
        public Object getItem(int position) {
            return mmessagesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Message messages = mmessagesList.get(position);
            View rowView = mLayoutInflater.inflate(R.layout.list_item_ping, parent, false);

            TextView title = rowView.findViewById(R.id.user_text_view);
            title.setText(messages.getUserName());

            TextView content = rowView.findViewById(R.id.timestamp_text_view);

            content.setText(messages.getContent());

            return rowView;
        }
    }
}

