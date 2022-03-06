package com.resume.maker.repository;

import android.app.Application;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.resume.maker.models.EducationModel;
import com.resume.maker.models.ExperienceModel;
import com.resume.maker.models.PersonalDetailsModel;
import com.resume.maker.models.ProjectModel;
import com.resume.maker.models.SecondaryDetailsModel;
import com.resume.maker.utils.UtilityFunctionsKt;

import java.util.ArrayList;
import java.util.Objects;

public class Repository {
    private final Application application;

    private final CollectionReference collectionReference;
    private DocumentReference documentReference;
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<ArrayList<ExperienceModel>> experienceList;
    private final MutableLiveData<ArrayList<SecondaryDetailsModel>> langList;
    private final MutableLiveData<ArrayList<SecondaryDetailsModel>> skillList;
    private final MutableLiveData<ArrayList<ProjectModel>> projectList;
    private final MutableLiveData<ArrayList<EducationModel>> eduList;
    private final MutableLiveData<PersonalDetailsModel> personalDetails;

    private final MutableLiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<Boolean> isUserLoggedIn;

    private final MutableLiveData<Boolean> isResumeAdded;
    private final MutableLiveData<Boolean> isExperienceAdded;
    private final MutableLiveData<Boolean> isLanguageAdded;
    private final MutableLiveData<Boolean> isSkillAdded;

    public Repository(Application application) {
        this.application = application;
        this.collectionReference = FirebaseFirestore.getInstance().collection("Users");
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.experienceList = new MutableLiveData<>();
        this.projectList = new MutableLiveData<>();
        this.personalDetails = new MutableLiveData<>();
        this.userLiveData = new MutableLiveData<>();
        this.isUserLoggedIn = new MutableLiveData<>();
        isExperienceAdded = new MutableLiveData<>();
        isResumeAdded = new MutableLiveData<>();
        isSkillAdded = new MutableLiveData<>();
        isLanguageAdded = new MutableLiveData<>();
        langList = new MutableLiveData<>();
        skillList = new MutableLiveData<>();
        eduList = new MutableLiveData<>();
    }

    // -------------------------------  Add Resume  -----------------------------

    public void addResumeWithPersonalDetails(PersonalDetailsModel personalDetailsModel) {
        collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()))
                .collection("resume_list").document(personalDetailsModel.getId()).
                set(personalDetailsModel)
                .addOnCompleteListener(task -> {
                })
                .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Adding Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    public void addSecondaryDetails(String id, ArrayList<String> langList, ArrayList<String> skillList) {
        if (!langList.isEmpty()) {
            for (String lang : langList) {
                collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()))
                        .collection("resume_list").document(id)
                        .collection("language").add(new SecondaryDetailsModel(lang))
                        .addOnCompleteListener(task -> {
                        })
                        .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Adding Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }

        if (!skillList.isEmpty()) {
            for (String skill : skillList) {
                collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()))
                        .collection("resume_list").document(id)
                        .collection("skill").add(new SecondaryDetailsModel(skill))
                        .addOnCompleteListener(task -> {
                        })
                        .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Adding Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    }

    public void addExperience(String id, ArrayList<ExperienceModel> modelArrayList) {
        for (ExperienceModel data : modelArrayList) {
            collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()))
                    .collection("resume_list").document(id)
                    .collection("experience").document(data.getId())
                    .set(data)
                    .addOnCompleteListener(task -> {
                    })
                    .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Adding Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    public void addProject(String id, ArrayList<ProjectModel> modelArrayList) {
        for (ProjectModel data : modelArrayList) {
            collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()))
                    .collection("resume_list").document(id)
                    .collection("project").document(data.getId())
                    .set(data)
                    .addOnCompleteListener(task -> {
                    })
                    .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Adding Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    public void addEducation(String id, ArrayList<EducationModel> modelArrayList) {
        for (EducationModel data : modelArrayList) {
            collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()))
                    .collection("resume_list").document(id)
                    .collection("education").document(data.getId())
                    .set(data)
                    .addOnCompleteListener(task -> {
                    })
                    .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Adding Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    public void uploadImage(String id, Uri filePath) {
        if (filePath != null) {
            // Defining the child of storageReference
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + id);

            ref.putFile(filePath).addOnSuccessListener(
                    taskSnapshot -> {
                    }).addOnFailureListener(e -> {
            });
        }
    }

    // -------------------------------  GET  --------------------------------------

    public MutableLiveData<ArrayList<ExperienceModel>> getExperiences(String id) {
        documentReference = collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()));
        documentReference.collection("resume_list").document(id).collection("experience")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ExperienceModel> list = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            list.add(snapshot.toObject(ExperienceModel.class));
                        }
                        experienceList.setValue(list);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Failed to reach db " + e.getMessage(), Toast.LENGTH_SHORT).show());

        return experienceList;
    }

    public MutableLiveData<ArrayList<SecondaryDetailsModel>> getLanguages(String id) {
        documentReference = collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()));
        documentReference.collection("resume_list").document(id).collection("language")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<SecondaryDetailsModel> list = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            list.add(snapshot.toObject(SecondaryDetailsModel.class));
                        }
                        langList.setValue(list);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Failed to reach db " + e.getMessage(), Toast.LENGTH_SHORT).show());

        return langList;
    }

    public MutableLiveData<ArrayList<SecondaryDetailsModel>> getSkills(String id) {
        documentReference = collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()));
        documentReference.collection("resume_list").document(id).collection("skill")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<SecondaryDetailsModel> list = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            list.add(snapshot.toObject(SecondaryDetailsModel.class));
                        }
                        skillList.setValue(list);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Failed to reach db " + e.getMessage(), Toast.LENGTH_SHORT).show());

        return skillList;
    }

    public MutableLiveData<ArrayList<ProjectModel>> getProjects(String id) {
        documentReference = collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()));
        documentReference.collection("resume_list").document(id).collection("projects")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ProjectModel> list = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            list.add(snapshot.toObject(ProjectModel.class));
                        }
                        projectList.setValue(list);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Failed to reach db " + e.getMessage(), Toast.LENGTH_SHORT).show());

        return projectList;
    }

    public MutableLiveData<ArrayList<EducationModel>> getEducation(String id) {
        documentReference = collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()));
        documentReference.collection("resume_list").document(id).collection("education")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<EducationModel> list = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            list.add(snapshot.toObject(EducationModel.class));
                        }
                        eduList.setValue(list);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Failed to reach db " + e.getMessage(), Toast.LENGTH_SHORT).show());

        return eduList;
    }

    public MutableLiveData<PersonalDetailsModel> getPersonalDetails(String id) {
        documentReference = collectionReference.document(Objects.requireNonNull(firebaseAuth.getUid()));
        documentReference.collection("resume_list").document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        personalDetails.setValue(task.getResult().toObject(PersonalDetailsModel.class));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(application.getApplicationContext(), "Failed to reach db " + e.getMessage(), Toast.LENGTH_SHORT).show());

        return personalDetails;
    }


    // -------------------------------  Firebase Auth  -----------------------------

    public MutableLiveData<FirebaseUser> login() {
        if (firebaseAuth.getCurrentUser() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                firebaseAuth.signInAnonymously().addOnCompleteListener(application.getMainExecutor(), task -> {
                    if (task.isSuccessful()) {
                        userLiveData.postValue(firebaseAuth.getCurrentUser());
                    } else {
                        Toast.makeText(application.getApplicationContext(), "Login Failure: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        return userLiveData;
    }

    public void logOut() {
        firebaseAuth.signOut();
        isUserLoggedIn.postValue(false);
    }
}
