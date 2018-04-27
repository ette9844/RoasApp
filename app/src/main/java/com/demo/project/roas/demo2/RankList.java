package com.demo.project.roas.demo2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class RankList extends AppCompatActivity {

    public static String restaurant = null;
    private List<Rank> rankList;
    private RankListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_list);

        Intent intent = getIntent();
        restaurant = intent.getStringExtra("restaurant");
        ListView rankListView = (ListView) findViewById(R.id.listView);
        adapterList(rankListView);
        ImageView backButton = (ImageView) findViewById(R.id.backButton);

        backButton.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void adapterList(final ListView listView){
        //Firebase DB 참조
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference reviewRef = rootRef.child(restaurant+"/review/");
        DatabaseReference menuRef = rootRef.child(restaurant+"/menu/");
        rankList = new ArrayList<Rank>();
        String menu=null;

        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rankList.clear();//이전에 있던 데이터 삭제
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                while(iterator.hasNext()){
                    DataSnapshot child = iterator.next();
                    final Rank rank = child.getValue(Rank.class);
                    rank.setMenu(child.getKey());

                    //평균 평점을 가져오기위해 Reivew에 접근
                    DatabaseReference reviewChil = reviewRef.child(child.getKey());
                    reviewChil.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                            int count=0;
                            int sum=0;
                            while(iterator.hasNext()){
                                DataSnapshot child = iterator.next();
                                Review review = child.getValue(Review.class);
                                count++;
                                sum +=review.getAvg();
                            }
                            int total;
                            if(count==0) {
                                total = 0;
                            }else {
                                total = sum / count;
                            }

                            rank.setRate(total);
                            Log.d("rate1",rank.getMenu()+"  "+rank.getRank());
                            rankList.add(rank);

                            Collections.sort(rankList,new CompareRateAsc());

                            //랭크를 저장
                            int rank=1;
                            for(int i=0; i<rankList.size(); i++){
                                if(i==10)
                                    break;//상위 10개만 출력
                                else{
                                    if(i==0) {
                                        rankList.get(i).setRank(rank);
                                        rank++;
                                    }else if(rankList.get(i).getRate() == rankList.get(i-1).getRate()){
                                        //이전과 별점 갯수가 같으면 같은 랭크로 표시
                                        rankList.get(i).setRank(rankList.get(i-1).getRank());
                                        rank++;
                                    }else{
                                        rankList.get(i).setRank(rank);
                                        rank++;
                                    }
                                }
                            }
                            //10이상은 짜르기
                            List<Rank> topList = new ArrayList<Rank>();
                            topList.clear();
                            for(int i=0; i<rankList.size(); i++) {
                                if(i>=10)
                                    break;
                                else{
                                    Rank rankClass = rankList.get(i);
                                    topList.add(rankClass);
                                }
                            }

                            adapter = new RankListAdapter(getApplicationContext(), topList);
                            adapter.notifyDataSetChanged();
                            listView.setAdapter(adapter);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RankList.this, MenuDetailActivity.class);
                intent.putExtra("Menukey", rankList.get(position).getMenu());
                intent.putExtra("rate", rankList.get(position).getRate());
                RankList.this.startActivity(intent);
            }
        });
    }

    static class CompareRateAsc implements Comparator<Rank> {
        int rank=1;
        @Override
        public int compare(Rank o1, Rank o2) {
            // TODO Auto-generated method stub
            return o1.getRate() > o2.getRate() ? -1 : o1.getRate() < o2.getRate() ? 1:0;
        }
    }
}
