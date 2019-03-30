package com.hochan.tumlodr.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.jumblr.types.Note;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.adapter.NotesAdapter;
import com.hochan.tumlodr.ui.component.WrapLinearLayoutManager;

import java.util.List;

/**
 * .
 * Created by hochan on 2017/11/5.
 */

public class NotesFragment extends BottomSheetDialogFragment {

	NotesAdapter mNotesAdapter = new NotesAdapter();

	public NotesFragment() {

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_notes, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mNotesAdapter.setActivity(getActivity());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		view.setBackgroundColor(AppUiConfig.sThemeColor);

		RecyclerView recyclerView = view.findViewById(R.id.recv_notes);
		recyclerView.setLayoutManager(new WrapLinearLayoutManager(getContext()));
		recyclerView.setAdapter(mNotesAdapter);
	}

	public void setNotes(List<Note> notes) {
		mNotesAdapter.setNotes(notes);
		mNotesAdapter.notifyDataSetChanged();
	}
}
