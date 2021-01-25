package kwaksuin.portfolio.exerciseresult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

import java.util.Arrays;

public class Login extends AppCompatActivity {
    LoginButton facebook_btn;
    SignInButton google_btn;
    Button check_bt;
    Button recruit;

    GoogleSignInClient client;
    FirebaseAuth auth;
    String TAG ="";
    int SIGN_IN = 123;

    FacebookLoginCallBack FBLoginCallBack;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        FBLoginCallBack = new FacebookLoginCallBack();

        // 페이스북 로그인
        facebook_btn = findViewById(R.id.facebook_login_bt);
        facebook_btn.setReadPermissions(Arrays.asList("public_profile","email"));
        facebook_btn.registerCallback(callbackManager, FBLoginCallBack);

        // 구글 로그인
        google_btn = findViewById(R.id.google_login_bt);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, googleSignInOptions);

        auth = FirebaseAuth.getInstance();

        google_btn.setOnClickListener(v -> {
            Intent signIn = client.getSignInIntent();
            startActivityForResult(signIn, SIGN_IN);
        });

        // 확인 버튼
        check_bt = findViewById(R.id.check_bt);
        check_bt.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });

        // 회원가입 버튼
        recruit = findViewById(R.id.recruit);
        recruit.setOnClickListener(v -> {
            Intent signin = new Intent(getApplicationContext(),SignIn.class);
            startActivity(signin);
        });

        //FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);


        //getHashKey();
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // 구글
        if (requestCode == SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(getApplicationContext(), "Google sign in Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    // 페이스북 로그인 연동
    public class FacebookLoginCallBack implements FacebookCallback<LoginResult> {

        // 로그인 성공 시 호출, Access Token 발급 성공
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e("Callback :: ", "onSuccess");
            requestMe(loginResult.getAccessToken());

            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }

        // 로그인 창을 닫을 경우, onCancel 호출
        @Override
        public void onCancel() {
            Log.e("Callback :: ", "onCancel");
        }

        // 로그인 실패 시에 호출
        @Override
        public void onError(FacebookException error) {
            Log.e("Callback :: ", "onError : " + error.getMessage());
        }

        // 사용자 정보 요청
        public void requestMe(AccessToken token) {
            GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                    (object, response) -> Log.e("result",object.toString()));

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
        }
    }

    // 구글 로그인 연동
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 로그인 성공, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();

                    } else {
                        // 로그인 실패, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();

                    }

                });
    }

    /*
    // 페이스북 hashKey 받아오기
    해시키(Hash key)는 개발용 key와 릴리즈(release)용 key가 있는데,
    개발용키는 개발할 때만 사용하는 키로 안드로이드 개발환경에 기본적으로 저장되어 있는 인증서 바이너리에 대한 해시값이고,
    릴리즈용 키는 실제 앱을 배포할 때 사용하는 인증서 바이너리에 대한 해시값
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
    
     */
}