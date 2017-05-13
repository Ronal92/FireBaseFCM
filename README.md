미완성...........

#1. 서버에서 폰으로 메시지 보내기

--> FireBase에서 현재 연결되어 있는 디바이스(핸드폰)에 Notification 공지를 보낼 수 있습니다.

## 출력화면

--> 로그인 화면입니다. FireBase의 RealTime Database에 있는 아이디와 비밀번호로 로그인합니다.

![](http://i.imgur.com/Ac3UYAL.png) 

![](http://i.imgur.com/yNA0SaB.png)

--> 현재 접속한 사용자의 토큰을 FireBase로부터 먼저 받습니다.

![](http://i.imgur.com/76H48FA.png)

--> FireBase 홈페이지의 Notification을 통해 어플리케이션의 패키지명이나 혹은 위에서 받은 토큰값으로 현재 디바이스에 공지를 보낼 수 있습니다.

![](http://i.imgur.com/rG4JSgs.png)

--> 공지가 전송되고 노티바가 생성된 화면입니다.

![](http://i.imgur.com/Zswna0h.png)

>> 잠깐!! 토큰이란>??
> 서버에서 특정 기기에 클라우드 메시지를 보낼때는 사용자의 아이디와 패스워드 토큰을 다 알아야 합니다. 토큰은 디바이스의 아이디와 디바이스에 설치된 앱의 아이디 + 앱이 설치된 시간이 담긴 정보로서 특정 디바이스에 노티바를 보낼 때 필요합니다. 또한 서버를 통하여 디바이스간 채팅을 할 때도 필요합니다~~~~~!!!



## 1.1 MainActivity.java						##// TODO ##

(1) 

	     FirebaseDatabase   database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("user"); 

(2) 

					public void signIn(final View view){
							................
							................
					}

(3) 

				 final String id = editId.getText().toString();
        final String pw = editPwd.getText().toString();

        userRef.child(id) 검색에 대한 쿼리
        userRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
				.........................


		}

(4) 

					    public void addToken(View view){
        final String id = editId.getText().toString();

        userRef.child(id).child("token").setValue(getToken(view));
    }


(5) 홈페이지에서 패키지명으로 보내거나 디바이스의 토큰 사용!

![](http://i.imgur.com/X9d03sb.png)






--------------------------------------------------

#2. 중계서버(<-> FCM) 만들어서 폰끼리 통신하기하기 

--> FireBase와 중계서버를 사용해서 핸드폰간에 메시지를 주고받습니다.

## // Todo 그림

##2.1 톰캣 서버 구축

##2.2 FireBaseFCM

sendNotification()

setList()/6
/getToken()

User.class

