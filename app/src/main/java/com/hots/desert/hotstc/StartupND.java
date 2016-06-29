package com.hots.desert.hotstc;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuItemView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;
import com.hots.desert.hotstc.library.hotslogsheroes;

public class StartupND extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_nd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        hotslogsheroes hlh = new hotslogsheroes();
        hlh.makeJsonArrayRequest();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Views
        //mStatusTextView = (TextView) findViewById(R.id.status);
        //mDetailTextView = (TextView) findViewById(R.id.detail);

        // Button listeners
        //findViewById(R.id.sign_in_button).setOnClickListener(this);
        //findViewById(R.id.sign_out_button).setOnClickListener(this);
        //findViewById(R.id.disconnect_button).setOnClickListener(this);
        //findViewById(R.id.profilebutton).setOnClickListener(this);




        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]


    }


    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(StartupND.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        NavigationView navigationView= (NavigationView) findViewById(R.id.nav_view);
        Menu menuNav=navigationView.getMenu();
        MenuItem login = menuNav.findItem(R.id.nav_login);
        MenuItem logout = menuNav.findItem(R.id.nav_logout);


        if(user != null)
        {
            logout.setVisible(true);
            login.setVisible(false);
            setprofileinformation(user);

        }
        else
        {
            logout.setVisible(false);
            login.setVisible(true);
            setprofileinformation(user);

        }
    }
    public void setprofileinformation (FirebaseUser user) {

        if (user != null) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            String useruid = user.getUid();
            String displayname = user.getDisplayName();
            String usermail = user.getEmail();
            Uri photouri = user.getPhotoUrl();
            //LinearLayout nav_header_layout = (LinearLayout) findViewById(R.id.nav_header_nd);
            View header = navigationView.getHeaderView(0);

            ImageView profilephoto = (ImageView) header.findViewById(R.id.profilelogo);
            TextView displaynametext = (TextView) header.findViewById(R.id.accname);
            TextView emailtext = (TextView) header.findViewById(R.id.accmail);

            Picasso.with(this).load(photouri).resize(196, 196).into(profilephoto);

            displaynametext.setText(displayname);
            displaynametext.setTextSize(18);

            emailtext.setText(usermail);
            emailtext.setTextSize(12);

        }
        else{
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            View header = navigationView.getHeaderView(0);
            int id = getResources().getIdentifier("android:drawable/sym_def_app_icon", null, null);
            ImageView profilephoto = (ImageView) header.findViewById(R.id.profilelogo);
            TextView displaynametext = (TextView) header.findViewById(R.id.accname);
            TextView emailtext = (TextView) header.findViewById(R.id.accmail);

            profilephoto.setImageResource(id);


            displaynametext.setText(R.string.heroesofthestorm);
            displaynametext.setTextSize(18);

            emailtext.setText(R.string.teamcomposer);
            emailtext.setTextSize(12);

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.startup_nd, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_heroes) {
            Intent openheroes = new Intent(this, HeroListActivity.class);
            startActivity(openheroes);
            // Handle the camera action

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_login) {
            signIn();
        } else if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
