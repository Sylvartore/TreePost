package ca.bcit.planters.treepost;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    TextView nickname;
    TextView email;
    public MeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_me, container, false);

//        AnimationBottomNavigationView.addAnimation(
//                rootView,
//                getActivity().findViewById(R.id.navigation_me),
//                ContextCompat.getColor(getActivity(), R.color.animation_start),
//                ContextCompat.getColor(getActivity(), R.color.animation_end)
//        );

        ImageButton userProfileBtn = rootView.findViewById(R.id.user_profile_btn);
        userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });

        nickname = rootView.findViewById(R.id.user_profile_name_tv);
        email = rootView.findViewById(R.id.user_profile_email_tv);
        nickname.setText(FirebaseUIActivity.currentUser.nickname);
        email.setText(FirebaseUIActivity.currentUser.email);
        return rootView;

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

    @Override
    public void onResume() {
        super.onResume();
        nickname.setText(FirebaseUIActivity.currentUser.nickname);
    }

}
