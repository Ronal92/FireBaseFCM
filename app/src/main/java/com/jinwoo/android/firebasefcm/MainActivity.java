package com.jinwoo.android.firebasefcm;



import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference userRef;


    EditText editMsg, editId, editPwd;
    TextView txtToken;
    Button btnSend, btnSignin;
    ListView listView;
    ListAdapter adapter;

    List<User> datas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 데이터베이스 연결
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");

        // 위젯
        editId = (EditText)findViewById(R.id.editId);
        editPwd = (EditText)findViewById(R.id.editPwd);
        editMsg = (EditText)findViewById(R.id.editMsg);
        txtToken = (TextView)findViewById(R.id.txtToken);
        btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification(v);
            }
        });
        btnSignin = (Button)findViewById(R.id.btnSignin);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(v);
            }
        });

        // 사용자 리스트 세팅
        listView = (ListView)findViewById(R.id.listView);
        adapter = new ListAdapter(this, datas);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = datas.get(position);
                txtToken.setText(user.getToken());
            }
        });

    }


    public void sendNotification(View view) {
        final String msg = editMsg.getText().toString();
        final String token = txtToken.getText().toString();
        if ("".equals(msg)) { // 입력값이 있을때만 노티를 날려준다.
            Toast.makeText(MainActivity.this, "메세지를 입력하세요", Toast.LENGTH_SHORT).show();

        } else if("".equals(token)) {
            Toast.makeText(MainActivity.this, "받는 사람을 선택하세요", Toast.LENGTH_SHORT).show();

        } else {

            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... params) {

                    String result = "";
                    // 1. FCM 서버정보 세팅
                    //String server_url = "http://192.168.1.184:8080/sendMsgToFCM.jsp";
                    String server_url = "http://192.168.1.184:8080/fcmsender/sender";

                    // 2. Post message 세팅
                    String post_data = "to_token=" + token + "&msg=" + msg;

                    try {
                        // 3. FCM 서버로 메시지를 전송
                        // 3.1 수신한 메시지를 json 형태로 바꿔준다.

                        // 3.2 HttpUrlConnection을 사용해서 FCM 서버측으로 메시지를 전송한다.
                        //     3.2.1 서버연결
                        URL url = new URL(server_url);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        //      3.2.2 header 설정
                        con.setRequestMethod("POST");
                        //		3.2.3 POST데이터(body) 전송
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(post_data.getBytes());
                        os.flush();
                        os.close();
                        // 		3.2.4 전송후 결과 처리
                        int responseCode = con.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) { // code 200
                            // 결과처리후 FCM 서버측에서 발송한 결과메시지를 꺼낸다.
                            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String dataLine = "";
                            // 메시지를 한줄씩 읽어서 result 변수에 담아두고
                            while ((dataLine = br.readLine()) != null) {
                                result = result + dataLine;
                            }
                            br.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            }.execute();
        }
    }

    // 사용자가 로그인하면 아이디와 패스워드가 맞는지 확인!
    public void signIn(final View view){
            final String id = editId.getText().toString();
            final String pw = editPwd.getText().toString();

            // DB 1. 파이어베이스로 child(id) 레퍼런스에 대한 쿼리를 날린다. userRef.child(id) 검색에 대한 쿼리
            userRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {

            // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO 코드 추가 필요
                if(dataSnapshot.getChildrenCount() > 0){
                    String fbPw = dataSnapshot.child("password").getValue().toString();
                    if(fbPw.equals(pw)){
                        addToken(view);
                        setList();
                    } else {
                        Toast.makeText(MainActivity.this, "Wrong PassWord", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Nothing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // 자신의 토큰을 등록한다.
    public void addToken(View view){
        final String id = editId.getText().toString();

        userRef.child(id).child("token").setValue(getToken(view));
    }

    // 등록한 토큰 리스트로 보여준다.
    public void setList(){
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                datas.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    User user = data.getValue(User.class);
                    user.setId((data.getKey()));
                    datas.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // 토큰을 얻는다.
    public String getToken(View view){
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token=========", token);
        return token;

    }



}

class ListAdapter extends BaseAdapter{

    Context context;
    List<User> datas;
    LayoutInflater inflater;

    public ListAdapter(Context context, List<User> datas){
        this.context = context;
        this.datas = datas;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_list, null);
        }

        User user = datas.get(position);

        TextView userId  = (TextView)convertView.findViewById(R.id.userId);
        userId.setText(user.getId());

        return convertView;
    }
}