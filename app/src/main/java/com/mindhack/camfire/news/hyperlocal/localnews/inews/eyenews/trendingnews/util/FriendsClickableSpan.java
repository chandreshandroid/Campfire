package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

/**
 * Created by dhavalkaka on 02/02/2018.
 */

public class FriendsClickableSpan extends ClickableSpan {
    String friendId;
    public FriendsClickableSpan(String friendId){
        this.friendId=friendId;
    }
    @Override
    public void onClick(View widget) {

        Toast.makeText(widget.getContext(), "FriendId "+friendId, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
