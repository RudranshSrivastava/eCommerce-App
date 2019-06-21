package com.example.android.ecommerce.Interface;

/**
 * Created by Rudransh on 01-Jun-19.
 */

import android.view.View;

public interface ItemClickListner
{
    void onClick(View view, int position, boolean isLongClick);
}