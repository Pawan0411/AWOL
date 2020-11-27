package codex.codex_iter.www.awol.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.adapter.OnlineLectureSubjectAdapter;
import codex.codex_iter.www.awol.model.Lecture;
import codex.codex_iter.www.awol.utilities.Utils;

import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_BRANCH;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_SEMESTER;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;

public class OnlineLectureSubjects extends BaseThemedActivity implements OnlineLectureSubjectAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.main_Layout)
    ConstraintLayout main_layout;

    private ArrayList<Lecture> subjectName = new ArrayList<>();
    private ArrayList<Lecture> subjectLinks = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private String jsonVideosLinks, jsonSubjectNames, branch;
    private BottomSheetDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);

        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();

        jsonVideosLinks = Utils.getJsonFromStorage(getApplicationContext(), "video.txt");
        jsonSubjectNames = Utils.getJsonFromStorage(getApplicationContext(), "data.txt");

        sharedPreferences = getSharedPreferences(STUDENT_NAME, MODE_PRIVATE);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Video Lectures");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        if (dark) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            recyclerView.setBackgroundColor(Color.parseColor("#141414"));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.black));
            Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
        branch = sharedPreferences.getString(STUDENT_BRANCH, "");
        if (bundle != null) {
            String sem = bundle.getString(STUDENT_SEMESTER);
            sharedPreferences.edit().putString(STUDENT_SEMESTER, sem).apply();
        }
        getJSONdata("");
        OnlineLectureSubjectAdapter lecturesAdapter = new OnlineLectureSubjectAdapter(this, subjectName, false, this);
        recyclerView.setAdapter(lecturesAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void getJSONdata(String subname) {
        showBottomSheetDialog();
        try {
            subjectName.clear();
            subjectLinks.clear();
            if (jsonVideosLinks != null && jsonSubjectNames != null && !jsonSubjectNames.isEmpty() && !jsonVideosLinks.isEmpty()) {
                JSONObject lectures = new JSONObject(jsonVideosLinks);
                JSONObject subject = new JSONObject(jsonSubjectNames);
                String[] semester = {"1st","2nd", "3rd", "4th", "5th", "6th", "7th", "8th"};
                for (String s : semester) {
                    if (Objects.requireNonNull(sharedPreferences.getString(STUDENT_SEMESTER, null)).trim().equals(s)) {
                        JSONObject subjects = lectures.getJSONObject(s);
                        JSONObject su = subject.getJSONObject(s);
                        Iterator<String> key_subject = su.keys();
                        while (key_subject.hasNext()) {
                            String keybranch = key_subject.next();
                            Log.d("keys_branch", keybranch);
                            if (keybranch.equals(branch)) {
                                hideBottomSheetDialog();
                                Iterator<String> sem_no = subjects.keys();
                                JSONArray subjectsname = su.getJSONArray(keybranch);
                                while (sem_no.hasNext()) {
                                    String keysubject = sem_no.next();
                                    for (int i = 0; i < subjectsname.length(); i++) {
                                        if (keysubject.equals(subjectsname.get(i))) {
                                            JSONArray links = subjects.getJSONArray(keysubject);
                                            subjectName.add(new Lecture(keysubject));
                                            if (subname.equals(keysubject) && !subname.isEmpty()) {
                                                subjectLinks.clear();
                                                for (int j = 0; j < links.length(); j++) {
                                                    JSONObject json = links.getJSONObject(j);
                                                    subjectLinks.add(new Lecture(json.getString("name"), json.getString("link")));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                hideBottomSheetDialog();
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                finish();
            }
            if (subjectName.size() == 0) {
                hideBottomSheetDialog();
                MaterialTextView no_lectures = findViewById(R.id.no_lectures);
                recyclerView.setVisibility(View.GONE);
                no_lectures.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            hideBottomSheetDialog();
            Toast.makeText(this, "Please wait while we are fetching subject list.", Toast.LENGTH_LONG).show();
            finish();
            Log.d("error", e.toString());
        }
    }

    public void showBottomSheetDialog() {
        //    private BottomSheetBehavior bottomSheetBehavior;
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.layout_bottomprogressbar, null);
        if (dialog == null) {
            dialog = new BottomSheetDialog(this);
            dialog.setContentView(view);
            dialog.setCancelable(false);
        }
        dialog.show();

    }

    public void hideBottomSheetDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClicked(String subject_name, String video_link) {
        getJSONdata(subject_name);
        Intent intent = new Intent(this, OnlineLectureVideos.class);
        Gson gson = new Gson();
        String json = gson.toJson(subjectLinks);
        sharedPreferences.edit().putString("SubjectLinks", json).apply();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
}
