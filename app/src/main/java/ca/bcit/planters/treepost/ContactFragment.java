package ca.bcit.planters.treepost;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.app.ListFragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class ContactFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;

    com.google.firebase.database.DatabaseReference myRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();
    List<User> userList = new ArrayList<>();
    ArrayAdapter<User> adapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        myRef.addValueEventListener(new ContactFragmentValueEventListener(inflater));
//        AnimationBottomNavigationView.addAnimation(
//                rootView,
//                getActivity().findViewById(R.id.navigation_contacts),
//                ContextCompat.getColor(getActivity(), R.color.animation_start),
//                ContextCompat.getColor(getActivity(), R.color.animation_end)
//        );

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    class ContactFragmentValueEventListener implements ValueEventListener {
        LayoutInflater inflater;

        ContactFragmentValueEventListener(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot ds : dataSnapshot.child("friends").child(FirebaseUIActivity.currentUser.userId).getChildren()) {
                if (ds.getValue() != null && ds.getKey() != null) {
                    String email = ds.getValue().toString();
                    String userId = ds.getKey();
                    String nickName = email.split("@")[0];
                    userList.add(new User(email, userId, nickName));
                }
            }
            adapter = new ArrayAdapter<>(
                    inflater.getContext(), android.R.layout.simple_expandable_list_item_1, userList.toArray(new User[0])
            );
            setListAdapter(adapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

    // Rename method, update argument and hook method into UI event
    @SuppressWarnings("unused")
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}

