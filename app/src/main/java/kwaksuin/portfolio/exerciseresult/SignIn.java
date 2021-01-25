package kwaksuin.portfolio.exerciseresult;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignIn extends AppCompatActivity {
    Button end;

    EditText pwd;
    EditText repwd;

    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        pwd = findViewById(R.id.pwd);
        String password = pwd.getText().toString();

        repwd = findViewById(R.id.re_pwd);
        String Repassword = repwd.getText().toString();

        error = findViewById(R.id.checked_txt);

        end = findViewById(R.id.end);
        end.setOnClickListener(v -> {

            if(password != Repassword){
                error.setVisibility(View.VISIBLE);
            } else {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                Toast.makeText(getApplicationContext(),"가입이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                startActivity(main);
            }

        });
    }
}