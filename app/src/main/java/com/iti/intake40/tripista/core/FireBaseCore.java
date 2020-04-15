package com.iti.intake40.tripista.core;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.iti.intake40.tripista.OnTripsLoaded;
import com.iti.intake40.tripista.R;
import com.iti.intake40.tripista.core.model.Note;
import com.iti.intake40.tripista.core.model.Trip;
import com.iti.intake40.tripista.core.model.UserModel;
import com.iti.intake40.tripista.features.auth.home.HomeContract;
import com.iti.intake40.tripista.features.auth.signin.SigninContract;
import com.iti.intake40.tripista.features.auth.signup.SignupContract;
import com.iti.intake40.tripista.features.auth.splash.SplashContract;
import com.iti.intake40.tripista.map.MapContract;
import com.iti.intake40.tripista.note.AddNoteContract;
import com.iti.intake40.tripista.note.AddNotePrsenter;
import com.iti.intake40.tripista.trip.AddTripPresenter;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FireBaseCore {
    private static final String TAG = "firebase";
    //make singletone class
    public static FireBaseCore core;
    /*
    remon
     */
    private DatabaseReference rootDB;
    private StorageReference rootStorage;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private StorageReference storagePath;
    private FirebaseAuth auth;
    private AuthCredential credential;
    private PhoneAuthCredential phoneAuthCredential;
    private DatabaseReference profilePath;
    private SigninContract.PresenterInterface signinPresenter;
    private SignupContract.PresenterInterface signupPresenter;
    private HomeContract.PresenterInterface homePresenter;
    private AddNoteContract.PresenterInterface addNote;
    private String id;
    private String verificationId;
    private DataSnapshot dataSnapshot;
    private int cancelId;
    private int backCancelId;
    private String tripTitle;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        /* get the code sent by sms automatically */
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {


        }
    };

    private FireBaseCore() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        rootDB = database.getInstance().getReference().child("tripista");
        rootStorage = FirebaseStorage.getInstance().getReference().child("tripista");
    }

    public static FireBaseCore getInstance() {
        if (core == null) {
            core = new FireBaseCore();
        }
        return core;
    }

    public void addUserData(UserModel model) {
        profilePath = rootDB.child("users").child("profile").child(id);
        profilePath.setValue(model);
        profilePath = rootDB.child("users").child("profile").child(model.getPhone());
        profilePath.setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    signupPresenter.replyByMessage(R.string.saved_in_fire_base);
                    signupPresenter.replayByChangeActivity();
                }
            }
        });
    }

    public void addUserWithImage(final UserModel model) {
        storagePath = rootStorage.child("Profile").child(id);
        Uri imageUri = Uri.parse(model.getImageUrl());
        StorageTask uploadTask = storagePath.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storagePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri imageUploaded = task.getResult();
                    String imageLink = imageUploaded.toString();
                    model.setImageUrl(imageLink);
                    profilePath = rootDB.child("users").child("profile").child(id);
                    profilePath.setValue(model);
                    profilePath = rootDB.child("users").child("profile").child(model.getPhone());
                    profilePath.setValue(id);
                    signupPresenter.replayByChangeActivity();
                }
            }
        });
    }

    //signup with email and password
    public void signUpEithEmailAndPassword(final UserModel model, SignupContract.PresenterInterface presenter) {
        this.signupPresenter = presenter;
        auth.createUserWithEmailAndPassword(model.getEmail(), model.getPassWord())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendEmailVarificationLink(model);
                        } else {
                            Log.w(TAG, task.getException());
                            signupPresenter.replyByError(R.string.error_on_send_verify);
                        }
                    }
                });
    }

    public void sendEmailVarificationLink(final UserModel model) {
        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    signupPresenter.replyByMessage(R.string.signup_email_linke_sent);
                    currentUser = auth.getCurrentUser();
                    id = currentUser.getUid();
                    model.setId(id);
                    if (model.getImageUrl() != null)
                        addUserWithImage(model);
                    else
                        addUserData(model);
                } else {
                    signupPresenter.replyByError(R.string.signup_email_linke_not_sent);
                }
            }
        });
    }

    public void signInWithEmailAndPassword(String emailAddress, String password, SigninContract.PresenterInterface presenter) {
        signinPresenter = presenter;
        auth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    currentUser = auth.getCurrentUser();
                    id = currentUser.getUid();
                    if (isCheckedEmailVerfication()) {

                        signinPresenter.replyByMessage(R.string.logged_in_successfuly);
                        signinPresenter.replayByChangeFragment();
                    } else {
                        signinPresenter.replyByError(R.string.verfy_error);
                    }

                } else {
                    signinPresenter.replyByError(R.string.login_failed);
                }
            }
        });


    }

    private boolean isCheckedEmailVerfication() {
        return auth.getCurrentUser().isEmailVerified();
    }

    public void handleFacebookAccessToken(AccessToken token, FragmentActivity signinActivity, SigninContract.PresenterInterface presenter) {
        signinPresenter = presenter;
        Log.d(TAG, "handleFacebookAccessToken: " + token);
        auth = FirebaseAuth.getInstance();
        credential = FacebookAuthProvider.getCredential(token.getToken());

        auth.signInWithCredential(credential)
                .addOnCompleteListener(signinActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            currentUser = auth.getCurrentUser();
                            UserModel model = new UserModel(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getPhotoUrl().toString(), currentUser.getEmail());
                            checkUser(model);
                            signinPresenter.replayByChangeFragment();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            signinPresenter.replyByError(R.string.signin_failed);
                        }
                    }
                });
    }

    //check facebook user id exist before or not
    private void checkUser(final UserModel model) {
        currentUser = auth.getCurrentUser();
        id = currentUser.getUid();
        profilePath = rootDB.child("users").child("profile").child(id);
        profilePath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    addFacebookUser(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //add user login by face book
    private void addFacebookUser(final UserModel model) {
        profilePath = rootDB.child("users").child("profile").child(id);
        profilePath.setValue(model);
    }

    public void signOut() {
        auth.signOut();
    }

    // get info for user
    public void getUserInfo(HomeContract.PresenterInterface home) {
        homePresenter = home;
        currentUser = auth.getCurrentUser();
        id = currentUser.getUid();
        profilePath = rootDB.child("users").child("profile").child(id);
        profilePath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);
                    homePresenter.setUserInfo(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //get user info by phone
    // get info for user
    public void getUserInfoByPhone(HomeContract.PresenterInterface home, String number) {
        homePresenter = home;
        DataSnapshot getId = checkPhoneExisit(number);
        id = getId.getValue().toString();
        profilePath = rootDB.child("users").child("profile").child(id);
        profilePath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);
                    homePresenter.setUserInfo(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void signInWithCredential(final PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signinPresenter.sendCode(credential.getSmsCode());
                        } else {
                            signinPresenter.replyByMessage(R.string.wrong_code);
                        }
                    }
                });
    }

    /*
    mahmoud

     */
//send phone
    public void verifyCode(String code) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
        /* sign in method */
        signInWithCredential(phoneAuthCredential);
    }

    public void sendVerificationCode(String number, SigninContract.PresenterInterface presenter) {
        dataSnapshot = checkPhoneExisit(number);
        if (dataSnapshot != null) {
            signinPresenter = presenter;
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    number,
                    60,
                    TimeUnit.SECONDS,
                    TaskExecutors.MAIN_THREAD,
                    mCallBack
            );
        }

    }

    private DataSnapshot checkPhoneExisit(final String number) {
        profilePath = rootDB.child("users").child("profile").child(number);
        profilePath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.getValue() != null) {
                    dataSnapshot = data;
                } else
                    dataSnapshot = null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return dataSnapshot;
    }

    /*
    shrouq
     */
    public void addTrip(final Trip trip, final AddTripPresenter addTripPresenter) {
        //here we just only refer to path
        profilePath = rootDB.child("users").child("trips").child(id);
        //to add trips we should take snapshot from this path
        final String key = profilePath.push().getKey();
        trip.setTripId(key);
        profilePath.child(key).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    addTripPresenter.setData(trip);
                }
            }
        });
    }

    public void checkCurrentUser(SplashContract.PresenterInterface splashInterface) {
        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            splashInterface.replayByChangeActivty(true);


        } else {
            splashInterface.replayByChangeActivty(false);

        }
    }

    /*
    remon

     */
    public void getTripsForCurrentUser(final OnTripsLoaded onTripsLoaded) {
        rootDB.child("users")
                .child("trips")
                .child(auth.getCurrentUser().getUid())
                //check that title is equal to test3
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Trip> upcommingTrips = new ArrayList<Trip>();
                        for (DataSnapshot tripSnapShot : dataSnapshot.getChildren()) {
                            Trip t = tripSnapShot.getValue(Trip.class);
                            if (t.getStatus().equals(Trip.Status.UPCOMMING) || t.getStatus().equals(Trip.Status.IN_PROGRESS)) {
                                upcommingTrips.add(tripSnapShot.getValue(Trip.class));
                            }
                        }
                        onTripsLoaded.onTripsLoaded(upcommingTrips);
                        Log.d("firebase", "onDataChange: \n" + upcommingTrips);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void getHistoryTripsForCurrentUser(final OnTripsLoaded onTripsLoaded) {
        rootDB.child("users")
                .child("trips")
                .child(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Trip> historyTrips = new ArrayList<>();
                        //historyTrips.clear();
                        for (DataSnapshot tripSnapShot : dataSnapshot.getChildren()) {
                            Trip t = tripSnapShot.getValue(Trip.class);
                            if (t.getStatus().equals(Trip.Status.DONE)
                                    || t.getStatus().equals(Trip.Status.CANCELLED)) {
                                historyTrips.add(tripSnapShot.getValue(Trip.class));
                            }
                        }
                        onTripsLoaded.onTripsLoaded(historyTrips);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void deleteTrip(final String tripId, final Context context) {
        rootDB.child("users")
                .child("trips")
                .child(auth.getCurrentUser().getUid())
                .child(tripId)
                .removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Log.d(TAG, "onComplete: deleted " + tripId);
                        Toast.makeText(context, "Trip Deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void addNote(Note note, String tripID, AddNotePrsenter addNotePrsenter) {
        addNote = addNotePrsenter;
        profilePath = rootDB.child("users").child("trips").child(id).child(tripID);
        final String key = profilePath.push().getKey();
        note.setNoteID(key);
        profilePath.child("notes").child(key).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    addNote.replyByMessage(R.string.note_added_successfully);
                } else {
                    addNote.replyByError(R.string.note_didnt_added);
                }
            }
        });

    }

    public void getSpecificTrip(String tripID, final MapContract.ViewInterface service) {
        rootDB.child("users")
                .child("trips")
                .child(auth.getCurrentUser().getUid())
                .child(tripID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Trip trip = dataSnapshot.getValue(Trip.class);
                        service.setTripData(trip);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void updateTrip(final Trip updatedTrip) {
        rootDB.child("users")
                .child("trips")
                .child(auth.getCurrentUser().getUid())
                .child(updatedTrip.getTripId())
                .setValue(updatedTrip, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        homePresenter.replyByMessage(R.string.updated_successfuly);
                    }
                });
    }

    public void changeStateOfNote(int state, String noteID, String tripId) {
        profilePath = rootDB.child("users").child("trips").child(id).child(tripId).child("notes").child(noteID).child("noteState");
        profilePath.setValue(state);
    }

    public void changeStateOfTrip(String state, String tripId) {
        profilePath = rootDB.child("users").child("trips").child(id).child(tripId).child("status");
        profilePath.setValue(state);
    }


    public int getTripBackCancelID(Trip t) {
        backCancelId = (int) t.getBackCancelID();
        return backCancelId;
    }

    public int getTripCancelID(Trip t) {
        cancelId = (int) t.getCancelID();
        return cancelId;
    }
    public String getTripTitle(Trip t) {
        tripTitle =  t.getTitle();
        return tripTitle;
    }
}