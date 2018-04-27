(function()
{
  const config=
  {
    apiKey: "AIzaSyBzq2pZqekEmXiGGy_kY71azd_09riJPLg",
    authDomain: "orderdb-a48a4.firebaseapp.com",
    databaseURL: "https://orderdb-a48a4.firebaseio.com",
    projectId: "orderdb-a48a4",
    storageBucket: "orderdb-a48a4.appspot.com",
    messagingSenderId: "317121088195"
  }; //firebase 와 연동
  firebase.initializeApp(config);
  //Dom으로 index.html에 있는 Button의 Id를 얻어옴
  const txtEmail = document.getElementById('txtEmail');
  const txtPassword = document.getElementById('txtPassword');
  const btnLogin = document.getElementById('btnLogin');
  const btnLogout = document.getElementById('btnLogout');

  //add login Event
  btnLogin.addEventListener('click', e=>{
    // btnLoginID인 Login 버튼을 click할시 이 함수가 실행됨
    const email = txtEmail.value;
    const pass = txtPassword.value;
    const auth = firebase.auth();
    //sing in
    const promise = auth.signInWithEmailAndPassword(email,pass);
    //login 처리 결과를 promise 에 저장
   //오류 발생시 cath해서 console 창에 띄움
   promise.catch(e=> window.alert("이메일과 비밀번호를 확인해 주세요"));
   //오류 발생시는 경우는 ID와 PW가 잘못 입력될때 이므로 그에대한 처리
 });

 //실시간으로 user의 인증 정보를 체크
 //firebaseUser는 모든 Firebase의 사용자
 //로그인 안했을시 firebaseUser의 값은 NULL
 firebase.auth().onAuthStateChanged(firebaseUser => {
   if(firebaseUser){
     //인증 성공
     console.log(firebaseUser.email);
     document.location.href="./select.html"
     firstVisit=0;
   }else {
     //인증이 안되었을때
   }
 });

}());
