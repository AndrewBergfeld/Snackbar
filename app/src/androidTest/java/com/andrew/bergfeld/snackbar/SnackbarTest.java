package com.andrew.bergfeld.snackbar;

import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;

import com.andrew.bergfeld.snackbar.activity.ExampleActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;

public class SnackbarTest extends ActivityInstrumentationTestCase2<ExampleActivity> {

    public SnackbarTest() {
        super(ExampleActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        getActivity();
    }

    public void testShowingSingleMessage() {
        clickOnListItem(0);

        onView(withText("List Item 0 Clicked!")).check(matches(isDisplayed()))
                                                .perform(click());

        assertSnackbarHidden();
    }

    public void testMessageQueueing() {
        clickOnListItem(0);
        clickOnListItem(1);
        clickOnListItem(2);

        assertViewIsDisplayedWithText("List Item 0 Clicked!");
        clickOnViewWithText("List Item 0 Clicked!");

        sleep(300);

        assertViewIsDisplayedWithText("List Item 1 Clicked!");
        clickOnViewWithText("List Item 1 Clicked!");

        sleep(300);

        assertViewIsDisplayedWithText("List Item 2 Clicked!");
    }

    public void testStateSaving() {
        clickOnListItem(0);
        clickOnListItem(1);
        clickOnListItem(2);

        assertViewIsDisplayedWithText("List Item 0 Clicked!");

        forceStateSaving();

        assertViewIsDisplayedWithText("List Item 0 Clicked!");

        forceStateSaving();

        assertViewIsDisplayedWithText("List Item 0 Clicked!");
        clickOnViewWithText("List Item 0 Clicked!");

        getInstrumentation().waitForIdleSync();

        assertViewIsDisplayedWithText("List Item 1 Clicked!");
        clickOnViewWithText("List Item 1 Clicked!");

        getInstrumentation().waitForIdleSync();

        assertViewIsDisplayedWithText("List Item 2 Clicked!");
    }

    public void testSwipingAway() {
        clickOnListItem(0);

        getInstrumentation().waitForIdleSync();

        onView(withText("List Item 0 Clicked!")).perform(swipeLeft());

        getInstrumentation().waitForIdleSync();

        assertSnackbarHidden();
    }

    private void clickOnViewWithText(String text) {
        onView(withText(text)).perform(click());
    }

    private void assertViewIsDisplayedWithText(String text) {
        onView(withText(text)).check(matches(isDisplayed()));
    }

    private void assertSnackbarHidden() {
        onView(withId(R.id.container)).check(matches(not(isDisplayed())));
    }

    private void clickOnListItem(int position) {
        onData(instanceOf(Object.class)).inAdapterView(withId(R.id.list)).atPosition(position).perform(click());
    }

    private void forceStateSaving() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().recreate();
            }
        });

        sleep(400);
    }

    private void sleep(long duration) {
        SystemClock.sleep(duration);
    }
}
