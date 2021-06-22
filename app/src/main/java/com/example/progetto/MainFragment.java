package com.example.progetto;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progetto.RecyclerView.CardAdapter;
import com.example.progetto.RecyclerView.OnItemListener;
import com.example.progetto.ViewModel.ListViewModel;

import java.util.List;

public class MainFragment extends Fragment implements OnItemListener{

    private static final String LOG = "Home-Fragment_LAB";

    private CardAdapter adapter;
    private RecyclerView recyclerView;
    private ListViewModel listViewModel;

    final Fragment fragmentMessage = new SmsFragment();
    final Fragment fragmentChat = new ChatFragment();
    final Fragment fragmentHome = new HomePageFragment();
    final Fragment fragmentProfile = new DetailsFragment();
    final FragmentManager fm = getChildFragmentManager();
    FragmentTransaction fragmentTransaction = fm.beginTransaction();

    Fragment active = fragmentHome;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();
        if (activity != null) {

            Utilities.setUpToolbar((AppCompatActivity) getActivity(), getString(R.string.app_name));

            setRecyclerView(activity);

            listViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(ListViewModel.class);
            //when the list of the items changed, the adapter gets the new list.
            listViewModel.getCardItems().observe((LifecycleOwner) activity, new Observer<List<CardItem>>() {
                @Override
                public void onChanged(List<CardItem> cardItems) {
                    adapter.setData(cardItems);
                }
            });

            //signup button in first login page takes me to sign up fragment
            view.findViewById(R.id.signupBt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utilities.insertFragment((AppCompatActivity) activity, new SignupFragment(), "SignupFragment");
                }
            });

            view.findViewById(R.id.loginBt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.insertFragment((AppCompatActivity) activity, new MainFragment(), "MainFragment");
                }
            });

            view.findViewById(R.id.homeNav).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.insertFragment((AppCompatActivity) activity, new HomePageFragment(), "HomePageFragment");
                }
            });

            view.findViewById(R.id.messageNav).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.insertFragment((AppCompatActivity) activity, new ChatFragment(), "ChatFragment");
                }
            });

            view.findViewById(R.id.profileNav).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.insertFragment((AppCompatActivity) activity, new DetailsFragment(), "DetailsFragment");
                }
            });


        } else {
            Log.e(LOG, "Activity is null");
        }
    }

    /**
     * Method to set the RecyclerView and the relative adapter
     *
     * @param activity the current activity
     */
    private void setRecyclerView(final Activity activity) {
        // Set up the RecyclerView
        recyclerView = getView().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final OnItemListener listener = this;
        adapter = new CardAdapter(activity, listener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null) {
            listViewModel.select(adapter.getItemFiltered(position));

            Utilities.insertFragment(appCompatActivity, new DetailsFragment(),
                    DetailsFragment.class.getSimpleName());
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /**
             * Called when the user submits the query. This could be due to a key press on the keyboard
             * or due to pressing a submit button.
             * @param query the query text that is to be submitted
             * @return true if the query has been handled by the listener, false to let the
             * SearchView perform the default action.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            /**
             * Called when the query text is changed by the user.
             * @param newText the new content of the query text field.
             * @return false if the SearchView should perform the default action of showing any
             * suggestions if available, true if the action was handled by the listener.
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }

        });
    }
}

