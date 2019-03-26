package ca.bcit.planters.treepost;

import android.app.Activity;
import android.os.Bundle;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import static android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends Activity implements OnFragmentInteractionListener{


    private static final String FRAGMENT_NORMAL = "FRAGMENT_NORMAL";
    private BottomNavigationView navigation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewFragment( new TreepostFragment(), FRAGMENT_NORMAL);
        viewFragment(new Fragment(), FRAGMENT_NORMAL);
        viewFragment( new TreepostFragment(), FRAGMENT_NORMAL);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_treepost:
                        viewFragment(new TreepostFragment(), FRAGMENT_NORMAL);
                        return true;
                    case R.id.navigation_contacts:
                        viewFragment(new ContactFragment(), FRAGMENT_NORMAL);
                        return true;
                    case R.id.navigation_me:
                        viewFragment(new MeFragment(), FRAGMENT_NORMAL);
                        return true;
                }
                return false;
            }

        });
    }

    @Override
    public void onFragmentInteraction(Uri uri){
    }

    private void viewFragment(Fragment fragment, String name){

        final FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        if(name == null){
            fragmentManager.popBackStack(FRAGMENT_NORMAL, POP_BACK_STACK_INCLUSIVE);
            fragmentTransaction.commit();
            return;
        }
        final int count = fragmentManager.getBackStackEntryCount();
        if( name.equals( FRAGMENT_NORMAL) ) {
            fragmentTransaction.addToBackStack(name);
        }
        fragmentTransaction.commit();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if( fragmentManager.getBackStackEntryCount() <= count){
                    fragmentManager.popBackStack(FRAGMENT_NORMAL, POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.removeOnBackStackChangedListener(this);
                    navigation.getMenu().getItem(0).setChecked(true);
                }
            }
        });

    }

    @SuppressWarnings("unused")
    private void viewFragment(Fragment fragment){
        viewFragment( fragment, null );
    }
}