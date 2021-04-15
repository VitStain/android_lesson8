package com.example.makingnotes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.Calendar;
import java.util.Objects;


public class ListOfNoteBlankFragment extends Fragment {

    private boolean isLandscape;
    private Notes[] notes;
    private Notes currentNote;

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_of_note_blank, container, false);
    }

    // вызывается после создания макета фрагмента, здесь мы проинициализируем список
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initList(view);
        RecyclerView recyclerView = view.findViewById(R.id.notes_recycler_view);
        initRecyclerView(recyclerView, notes);
    }

    // создаем список заметок на экране из массива в ресурсах
    private void initList(View view) {
        notes = new Notes[]{
                new Notes(getString(R.string.one_note_title),
                        getString(R.string.one_note_content),
                        Calendar.getInstance(),
                        ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.purple_200)),
                new Notes(getString(R.string.two_note_title),
                        getString(R.string.two_note_content),
                        Calendar.getInstance(), ContextCompat.getColor(getContext(), R.color.green)),
                new Notes(getString(R.string.three_note_title),
                        getString(R.string.three_note_content),
                        Calendar.getInstance(), ContextCompat.getColor(getContext(), R.color.purple_500)),
                new Notes(getString(R.string.four_note_title),
                        getString(R.string.four_note_content),
                        Calendar.getInstance(),
                        ContextCompat.getColor(getContext(), R.color.purple_200)),
                new Notes(getString(R.string.five_note_title),
                        getString(R.string.five_note_content),
                        Calendar.getInstance(),
                        ContextCompat.getColor(getContext(), R.color.teal_700)),
        };
    }

    private void initRecyclerView(RecyclerView recyclerView, Notes[] notes) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        NoteAdapter adapter = new NoteAdapter(notes);
        adapter.setOnItemClickListener((position, note) -> {
            currentNote = note;
            showNote(currentNote);
        });
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(Objects.requireNonNull(getContext()),
                LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.separator)));
        recyclerView.addItemDecoration(itemDecoration);
    }

    // Сохраним текущую позицию (вызывается перед выходом из фрагмента)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(NoteBlankFragment.ARG_NOTE, currentNote);
        super.onSaveInstanceState(outState);
    }

    private void initCurrentNote(Notes note) {
        currentNote = note;
        showNote(note);
    }

    // activity создана, можно к ней обращаться. Выполним начальные действия
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Определение, можно ли будет расположить заметку рядом в другом фрагменте
        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        // Если это не первое создание, то восстановим текущую позицию
        if (savedInstanceState != null) {
            // Восстановление текущей позиции.
            currentNote = savedInstanceState.getParcelable(NoteBlankFragment.ARG_NOTE);
        } else {
            // Если воccтановить не удалось, то сделаем объект с первым индексом
            currentNote = notes[0];
        }

        // Если можно нарисовать рядом, то сделаем это
        if (isLandscape) {
            showLandNote(currentNote);
        }
    }

    private void showNote(Notes currentNote) {
        if (isLandscape) {
            showLandNote(currentNote);
        } else {
            showPortNote(currentNote);
        }
    }

    // Показать заметку в ландшафтной ориентации
    private void showLandNote(Notes currentNote) {
        // Создаем новый фрагмент с текущей позицией для вывода заметки
        NoteBlankFragment fragment = NoteBlankFragment.newInstance(currentNote);

        // Выполняем транзакцию по замене фрагмента
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.note_layout, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }


    private void showPortNote(Notes currentNote) {
        NoteBlankFragment fragment = NoteBlankFragment.newInstance(currentNote);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack("list_fragment");
        fragmentTransaction.replace(R.id.list_of_notes_fragment_container, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }
}