package com.athome.zubaliy.mylifeontheroad;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ApplicationTest {
    @Test
    public void shouldHaveApplicationName() {
        String appName = new MainActivity().getResources().getString(R.string.app_name);
        assertThat(appName, equalTo("My life on the road"));
    }


}


