package com.demo.project.roas.demo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import static com.demo.project.roas.demo2.QRcameraActivity.cart;

/**
 * Created by pc on 2017-11-16.
 */

public class NumControlPopup extends Activity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_numcontrol_popup);

        Intent intent = getIntent();
        final String MenuKey = intent.getStringExtra("MenuKey");
        final String strPrice = intent.getStringExtra("price");
        final int price = Integer.parseInt(strPrice);

        //레이아웃의 뷰 참조
        TextView plusBtn = (TextView)findViewById(R.id.num_plus);
        TextView minusBtn = (TextView)findViewById(R.id.num_minus);
        final TextView number = (TextView)findViewById(R.id.num);
        TextView cartBtn = (TextView)findViewById(R.id.getCartBtn);
        TextView warning = (TextView)findViewById(R.id.num_warning);

        for(Cart c : cart)
        {
            if(c.getMenu() != null && c.getMenu().equals(MenuKey))
            {
                //찾으려고 하는 메뉴가 이미 cart내에 존재하고 있을 경우
                //해당 경고를 띄우고 메뉴의 수량을 가져옴
                number.setText(""+c.getNum());
                warning.setText("※이미 장바구니에 담겨있는 항목입니다");
            }
        }

        //+버튼 onclick 리스너
        plusBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(number.getText().toString());
                num++;
                number.setText(""+num);

            }
        });

        //-버튼 onclick 리스너
        minusBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(number.getText().toString());
                num--;
                if(num <= 0)
                    num = 1;
                number.setText(""+num);
            }
        });

        //담기 버튼 onClick 리스너
        cartBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(number.getText().toString());
                for(Cart c : cart)
                {
                    if(c.getMenu() != null && c.getMenu().contains(MenuKey))
                    {
                        //찾으려고 하는 메뉴가 이미 cart내에 존재하고 있을 경우
                        //해당하는 항목의 값을 변경된 값으로
                        c.setNum(num);
                        //장바구니에 메뉴가 추가되었음을 toast
                        Toast.makeText(getApplicationContext(), "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        //메뉴 페이지로 돌아가기
                        finish();
                        return;
                    }
                }
                //장바구니 List에 해당 메뉴 등록
                Cart tmpCart = new Cart();
                tmpCart.setMenu(MenuKey);
                tmpCart.setNum(num);
                tmpCart.setPrice(price);
                cart.add(tmpCart);
                //장바구니에 메뉴가 추가되었음을 toast
                Toast.makeText(getApplicationContext(), "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                //메뉴 페이지로 돌아가기
                finish();
                return;
            }
        });
    }
}
