package mohit.codex_iter.www.awol.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import mohit.codex_iter.www.awol.R;
import mohit.codex_iter.www.awol.adapter.OnlineLectureSubjectAdapter;
import mohit.codex_iter.www.awol.model.Lecture;
import mohit.codex_iter.www.awol.utilities.Utils;

import static mohit.codex_iter.www.awol.utilities.Constants.STUDENTBRANCH;
import static mohit.codex_iter.www.awol.utilities.Constants.STUDENTSEMESTER;
import static mohit.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;

public class OnlineLectureSubjects extends BaseThemedActivity implements OnlineLectureSubjectAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ArrayList<Lecture> subjectName = new ArrayList<>();
    private ArrayList<Lecture> subjectLinks = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private String jsonVideosLinks, jsonSubjectNames, branch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);

        ButterKnife.bind(this);

        jsonVideosLinks = Utils.getJsonFromAssets(getApplicationContext(), "data.json");
        jsonSubjectNames = Utils.getJsonFromAssets(getApplicationContext(), "video.json");

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
        branch = sharedPreferences.getString(STUDENTBRANCH, "");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String sem = bundle.getString(STUDENTSEMESTER);
            sharedPreferences.edit().putString(STUDENTSEMESTER, sem).apply();
        }
        getJSONdata("");
        OnlineLectureSubjectAdapter lecturesAdapter = new OnlineLectureSubjectAdapter(this, subjectName, false, this);
        recyclerView.setAdapter(lecturesAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void getJSONdata(String subname) {
        try {
            subjectName.clear();
            subjectLinks.clear();
            if (jsonVideosLinks != null && jsonSubjectNames != null) {
                JSONObject lectures = new JSONObject(jsonVideosLinks);
                JSONObject subject = new JSONObject(jsonSubjectNames);
                String[] semester = {"2nd", "4th"};
                for (String s : semester) {
                    if (Objects.requireNonNull(sharedPreferences.getString(STUDENTSEMESTER, null)).trim().equals(s)) {
                        JSONObject subjects = lectures.getJSONObject(s);
                        JSONObject su = subject.getJSONObject(s);
                        Iterator<String> key_subject = su.keys();
                        while (key_subject.hasNext()) {
                            String keybranch = key_subject.next();
                            if (keybranch.equals(branch)) {
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
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.d("error", e.getMessage());
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