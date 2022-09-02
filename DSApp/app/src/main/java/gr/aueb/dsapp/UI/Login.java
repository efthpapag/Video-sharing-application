package gr.aueb.dsapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import gr.aueb.dsapp.BackEnd.User;
import gr.aueb.dsapp.BackEnd.UserDao;
import gr.aueb.dsapp.R;

public class Login extends AppCompatActivity {

    EditText editUsername;
    EditText editTextPort;
    Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.editUsername);
        editTextPort = (findViewById(R.id.editTextPort));
        buttonLogin = findViewById(R.id.buttonLogin);
    }

    @Override
    protected void onStart() {
        super.onStart();

        UserDao.user = new User();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDao.user.channelName = editUsername.getText().toString();
                UserDao.user.port = Integer.parseInt(editTextPort.getText().toString());
                Intent intent = new Intent(Login.this, Menu.class);
                startActivity(intent);
                finish();
            }
        });

    }

}