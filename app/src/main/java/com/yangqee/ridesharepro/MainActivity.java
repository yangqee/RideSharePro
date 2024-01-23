package com.yangqee.ridesharepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Button googleAuth; // 定义一个按钮对象，用于触发 Google 登录
    FirebaseAuth auth; // Firebase 认证对象
    FirebaseDatabase database; // Firebase 数据库对象
    GoogleSignInClient mGoogleSignInClient; // Google 登录客户端
    int RC_SIGN_IN = 20; // 用于标识 Google 登录的请求代码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleAuth = findViewById(R.id.btnGoogleLogin); // 通过按钮的资源 ID 获取按钮对象

        auth = FirebaseAuth.getInstance(); // 获取 FirebaseAuth 实例，用于进行用户身份验证
        database = FirebaseDatabase.getInstance(); // 获取 FirebaseDatabase 实例，用于操作 Firebase 数据库

        // 创建 Google 登录选项，设置要请求的信息，如 ID 令牌和电子邮件
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // 获取 Web 客户端 ID
                .requestEmail() // 请求用户的电子邮件信息
                .build();

        // 创建 Google 登录客户端
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 设置按钮的点击事件监听器，当用户点击按钮时执行 googleSignIn 方法
        googleAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
    }

    // 启动 Google 登录流程
    private void googleSignIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN); // 启动 Google 登录意图，并传入请求代码
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 当 Google 登录活动完成时，该方法会被调用
        if (requestCode == RC_SIGN_IN) { // 检查请求代码是否匹配
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken()); // 使用 Google 账号的 ID 令牌进行 Firebase 身份验证
            } catch (ApiException e) {
                // 处理异常，通常在登录失败时显示错误消息
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 使用 Google 账号的 ID 令牌进行 Firebase 身份验证
    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        // 使用 Firebase 身份验证对象进行身份验证
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // 身份验证成功
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // 创建一个包含用户信息的映射
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("id", user.getUid()); // 用户的唯一标识符
                                map.put("name", user.getDisplayName()); // 用户的显示名称
                                if (user.getPhotoUrl() != null) {
                                    map.put("profile", user.getPhotoUrl().toString()); // 用户的头像 URL（如果可用）
                                }
                                // 在 Firebase 数据库中的 "users" 节点下设置用户信息
                                database.getReference().child("users").child(user.getUid()).setValue(map);
                                // 启动第二个活动
                                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                                startActivity(intent);
                            }
                        } else { // 身份验证失败
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
