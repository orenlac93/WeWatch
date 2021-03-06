package com.example.wewatchapp.userPack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.wewatchapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Friends extends AppCompatActivity {

    /* firebase object */
    FirebaseDatabase database;
    /* firebase reference to the root */
    DatabaseReference rootRef;

    /* list view of users names */
    ListView listView;

    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<String> adapter;

    /* list to store the users names get from the intent */
    ArrayList<String> usersNames = new ArrayList<>();

    /* the current profile user name */
    String current_user_name;

    /* list to store my friends names from firebase */
    ArrayList<String> myFriends = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        listView = (ListView)findViewById(R.id.listView);

        /* set the path to friends table */
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference("Friends");

        /* get the names from the intent in to the names list */
        usersNames = getIntent().getStringArrayListExtra("test");

        /* get the current user name from the intent */
        current_user_name = getIntent().getStringExtra("user name");

        /*initialized my friends list   */
        initMyFriendsL();

    }

    /* get my friends names from firebse */
    private void initMyFriendsL() {

        rootRef.child(current_user_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()){

                    Friend friend = child.getValue(Friend.class);
                    String name = friend.getName();
                    myFriends.add(name);
                }
                //System.out.println(" >>>>>>> "+myFriends.toString());

                /* create the updated scroll view */
                CreateScrollView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /* create updated scroll view of optional new friends */
    private void CreateScrollView() {

        /* remove my friends names from the search friends menu  */
        for(String name : myFriends){
            if(usersNames.contains(name))
                usersNames.remove(name);
        }


        /* add the names from the names list into the 'list view' */
        for(int i = 0; i < usersNames .size(); i++) {
            if(usersNames.get(i).compareTo(current_user_name) != 0)
                list.add("" + usersNames .get(i));
        }

        initScrollView();
    }

    private void initScrollView() {
        adapter = new ArrayAdapter<>(Friends.this, android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), "You add  " + adapter.getItem(position)
                        + "  To Your Friends!", Toast.LENGTH_LONG).show();

                /* get the friend name which typed */
                String newFriend = "" + adapter.getItem(position);

                /* create new friend object */
                Friend friend = new Friend(newFriend, null);

                /* get id to the friend from fire base (in the path to the current user) */
                friend.setId(rootRef.child(current_user_name).push().getKey());

                /* add the new friend in the current user path*/
                rootRef.child(current_user_name).child(friend.getId()).setValue(friend);


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.search_view);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);


    }
}