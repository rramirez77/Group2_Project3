package android.example.myapplication.ui.bottomNavigation;

import android.content.Intent;
import android.example.myapplication.R;
import android.example.myapplication.SignInActivity;
import android.example.myapplication.databinding.FragmentHomeBinding;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Comment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment factory method to
 * create an instance of this fragment.
 */


public class HomeFragment extends Fragment {

    private Button startBtn;
    private Button stopBtn;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //return inflater.inflate(R.layout.fragment_home, container, false);
        return root;


    }




    //original


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     // TODO: Rename parameter arguments, choose names that match
     // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
     private static final String ARG_PARAM1 = "param1";
     private static final String ARG_PARAM2 = "param2";

     // TODO: Rename and change types of parameters
     private String mParam1;
     private String mParam2;

     public HomeFragment() {
     // Required empty public constructor
     }

     /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    /**
     // TODO: Rename and change types and number of parameters
     public static HomeFragment newInstance(String param1, String param2) {
     HomeFragment fragment = new HomeFragment();
     Bundle args = new Bundle();
     args.putString(ARG_PARAM1, param1);
     args.putString(ARG_PARAM2, param2);
     fragment.setArguments(args);
     return fragment;
     }

     @Override public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     if (getArguments() != null) {
     mParam1 = getArguments().getString(ARG_PARAM1);
     mParam2 = getArguments().getString(ARG_PARAM2);
     }
     }

     @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
     Bundle savedInstanceState) {
     // Inflate the layout for this fragment
     return inflater.inflate(R.layout.fragment_home, container, false);
     }**/
}