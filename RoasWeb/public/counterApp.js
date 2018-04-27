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
  }; //ini app
  firebase.initializeApp(config);
  const auth = firebase.auth();
  const viewId = document.getElementById('viewId');
  const backSelect = document.getElementById('backSelect');
  const logout = document.getElementById('logout');
  const tableName = document.getElementById('tableNamte');
  const foodsName = document.getElementById('foodsName');
  const total = document.getElementById('total');
  database= firebase.database(); //database init

  //실시간으로 user의 인증 정보를 체크
 //firebaseUser는 모든 Firebase의 사용자
 //로그인 안했을시 firebaseUser의 값은 NULL
 firebase.auth().onAuthStateChanged(firebaseUser => {
   if(firebaseUser){
     //로그인 성공
     userInfo = firebaseUser
     userEmail = firebaseUser.email
     //E-mail중 아이디만 가져옴
     var at=0;
     for(var i=0; i<userEmail.length;i++)
     {
       if(userEmail.charAt(i)=='@')
       {
         at=i;
         break;
       }
     }
     id=userEmail.substring(0,at);
     viewId.innerText=id+"님 어서오세요"
     createButton();
     orderList();
   }else {
     //로그인 실패 및 로그인이 안되었을떄 로그인 페이지로 이동
     document.location.href="/index.html";
   }
 });

//버튼을 클릭시 select.html로 이동
backSelect.addEventListener('click', e=>
{
  document.location.href="/select.html"
});
//logout 버튼 클릭시 logout 하고 index.html로 이동
logout.addEventListener('click', e=>
{
  firebase.auth().signOut();
  window.alert("로그아웃 되었습니다.");
  document.location.href="/index.html";
});
//현재 있는 음식 이름 삭제 하는 함수
function deleteFoods()
{
  //appen라는 id를 가진 태그 요소 삭제
  //append 라는 ID를 가진 요소가 없을때에는 NULL이 리턴됨
  //foodsName 테이블의 요소를 while문으로 찾아가면서 삭제
  var append = document.getElementById('append');
  if(append!=null)
  {
    while(foodsName.childElementCount)
    {
     foodsName.firstElementChild.remove();
    }
  }
}

//buuton을 생성하는 함수
function createButton()
{
  //좌석수를 가져오기 위한 참조
  var seat = database.ref(id+'/seat');
  seat.on('value', snap=>
  {
    //기존에 있던 버튼을 지우는 작업
    for(var i=0; i<snap.val(); i++)
    {
      var temp = document.getElementById('t'+i);
      if(temp !=null)
      {
        while(left.childElementCount)
        {
         left.firstElementChild.remove();
        }
      }
    }
    //for문으로 좌석수만큼의 버튼을 생성하는 함수
    var left = document.getElementById('left');
    for(var i=0; i<snap.val(); i++)
    {
      //left의 아이디를 갖는 태그 밑에 버튼을 추가
      var html =
      "<button class=\"table\" id=\"t" +(i+1)+ "\"> " +(i+1)+ " 테이블 </button>";
      $("#left").append(html);
      //생성된 태그에 대해 콜백 리스너 등록
      listener(i+1);
    }
  });
}

//인자로 받아온 테이블 번호에 대해 콜백 리스너에 등록하는 함수
function listener(data)
{
  //생성된 테이블의 번호의 아이디를 가져옴
  const listen = document.getElementById('t'+data);
  listen.addEventListener('click', e=>
  {
    // 테이블 이름 수정
    //현 접속한 id의 order에 접근
    $("#tableName>.title").text(data+"번째 테이블");
    //DB에 저장된 테이블의 번호에 대한 주문을 가져옴
    getOrder(data);

  });
}

 //tableNamem에 해당되는 data를 가져오는 함수
 function getOrder(tableNumber)
 {
   //해당 테이블에 주문이 있는지 확인한다.
   var check = database.ref(id + '/order');
   check.on('value', snap=>{
     //음식 이름 삭제
     deleteFoods();
     if(snap.child(tableNumber).val() == null)
     {
       //html 태그로 저장하여 append함수로 foodsname 태그에 추가
        var html =
        "<tr id=\"append\">"
        +"<td colspan=\"2\" class=\"title\"> 주문이 없습니다. </td>"
        +"</tr>";
        $("#foodsName").append(html);
      } else
      {
       var orderRef = database.ref(id+'/order/'+tableNumber);
       orderRef.once('value', snap =>
       {
         //현재는 order의 테이블 번호 첨조
         //테이블 번호의 child인 foods 참조
         //음식 이름을 배열로 하나씩 저장
         var names = snap.val().name.split(',');
         //저장된 요소를 하나씩 출력하고 그에 대한 갯수 뽑기
         //var foodsRef = database.ref(id+'/order/1/foods');
         for(var i=0; i<names.length;i++)
         {
           var chRef = snap.child('foods/'+names[i]);
            const number = chRef.val().number;
            const chSum = chRef.val().sum;
            //html 태그로 저장하여 append함수로 foodsname 태그에 추가
            var html =
            "<tr id=\"append\">"
            +"<th class=\"list_middle\">" +names[i]+ "</th>"
            +"<th class=\"list_right\">" +number+"개</th>"
            +"</tr>"
            +"<tr id=\"append\">"
            +"<td colspan=\"2\" class=\"sum\">"+ chSum +"</td>"
            +"</tr>";
            $("#foodsName").append(html);
          }
          //첫번째 테이블에 대하여 계산된 총액을 출력
          const sum = snap.val().sum;
          total.innerText=sum;
        });
      }
    });
  };

 //시작시 order가 있는 테이블의 버튼색을 변경
 function orderList()
 {
    var orderList = database.ref(id+'/order');
    orderList.on('value', snap=>
    {

      //for문으로 버튼이 존재하는지 할때 색을 변경
      var i=1;
      for(;document.getElementById('t'+i)!= null ; i++)
      {
        //해당 번호의 버튼에 해당되는 주문이 있을경우 색을 노란색으로 변경
        if(snap.child(i).val() != null)
        {
          var table = document.getElementById('t'+i);
          table.style.backgroundColor = "yellow";
        }
        else{
          //해당 번호의 버튼에 해당되는 주문이 없을 경우 색을 흰색으로 변경
          var table = document.getElementById('t'+i);
          table.style.backgroundColor = "white";
        }
      }
    });
 }

}());
