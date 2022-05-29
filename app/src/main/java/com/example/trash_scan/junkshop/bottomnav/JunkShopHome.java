package com.example.trash_scan.junkshop.bottomnav;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trash_scan.R;
import com.example.trash_scan.adapter.RecycableAdapter;
import com.example.trash_scan.databinding.FragmentAddRecycableBinding;
import com.example.trash_scan.databinding.FragmentJunkShopHomeBinding;
import com.example.trash_scan.firebase.models.Recycables;
import com.example.trash_scan.firebase.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class JunkShopHome extends Fragment implements RecycableAdapter.OnRecycableClick {
    private FragmentJunkShopHomeBinding binding;
    private List<Recycables> recycablesList;
    private FirebaseFirestore firestore;
    private final static String TAG = ".JunkSHopHome";
    private RecycableAdapter adapter;
    private void init() {
        firestore =FirebaseFirestore.getInstance();
        binding.recyclerviewRecycables.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentJunkShopHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        binding.buttonAddRecycable.setOnClickListener(view1 -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_home_to_addRecycableFragment);
        });
        getRecycables(FirebaseAuth.getInstance().getCurrentUser().getUid());
        swipeToDelete(binding.recyclerviewRecycables);
    }
    private void getRecycables(String myID) {
        recycablesList = new ArrayList<>();
        firestore.collection(User.TABLE_NAME).document(myID)
                .collection("Recycables")
                .addSnapshotListener((value, error) -> {
                    recycablesList.clear();
                    if (error != null) {
                        Log.d(TAG,error.getMessage());
                    }
                    if (value != null) {
                        for (DocumentSnapshot snapshot : value) {
                            if (snapshot != null) {
                                Recycables recycables = snapshot.toObject(Recycables.class);
                                recycablesList.add(recycables);
                            }
                        }
                        adapter = new RecycableAdapter(binding.getRoot().getContext(),recycablesList,this);
                        binding.recyclerviewRecycables.setAdapter(adapter);
                        binding.itemCount.setText(String.valueOf(recycablesList.size()));
                    }
                });
    }
    //TODO: delete category
    public void swipeToDelete(final RecyclerView recyclerView) {
        ItemTouchHelper callback = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
                alertDialogBuilder.setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete this  recycable?")
                        .setPositiveButton("ok", (dialog, which) -> {
                            deleteRecycable(FirebaseAuth.getInstance().getCurrentUser().getUid(),recycablesList.get(position).getRecycableID());
                            dialog.dismiss();
                            adapter.notifyItemRemoved(position);
                        })
                        .setNegativeButton("cancel", (dialog, which) -> {
                            dialog.dismiss();
                            adapter.notifyItemChanged(position);
                            Toast.makeText(getContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                        })

                        // Add customization options here
                        .show();

            }
        });
        callback.attachToRecyclerView(recyclerView);
    }
    private void deleteRecycable(String myID ,String id) {
        firestore.collection(User.TABLE_NAME).document(myID)
                .collection("Recycables")
                .document(id).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(),"Successfully deleted!",Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRecycableClick(int position) {

    }
}