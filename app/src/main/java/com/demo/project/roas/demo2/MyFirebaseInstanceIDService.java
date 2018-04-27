package com.demo.project.roas.demo2;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by is on 2017-11-26.
 */
    //사용자 Token을 생성하는 클래스
    //나중 push 알림을 특정 타겟에게 보낼떄 사용되는 고유 키 값
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{
    private static final String TAG = "MyFirebaseIIDService";

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();

    }
}
