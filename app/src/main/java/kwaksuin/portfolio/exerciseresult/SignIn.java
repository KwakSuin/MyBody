package kwaksuin.portfolio.exerciseresult;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignIn extends AppCompatActivity {
    EditText password;
    EditText repwd;
    EditText em;

    TextView error;

    private static final String TAG = "SIGN_IN_TAG";
    Button okay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        password = findViewById(R.id.pwd);
        repwd = findViewById(R.id.re_pwd);
        em = findViewById(R.id.email);
        error = findViewById(R.id.checked_txt);

       // firebaseAuth = FirebaseAuth.getInstance();

        okay = findViewById(R.id.end);
        okay.setOnClickListener(v -> {
            String pwd = password.getText().toString();
            String Repassword = repwd.getText().toString();
            String email = em.getText().toString();

            if (pwd.equals(Repassword)) {
                // 가입 성공
                Intent intent = new Intent(SignIn.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                // 가입 실패 (비밀번호가 일치하지 않을 때)
                error.setVisibility(View.VISIBLE);
            }
        });
    }

}