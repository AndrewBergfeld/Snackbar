#Snackbar

This library is an implementation of Google's Snackbar designs since Google doesn't actually provide one.

##Setup
Adding a Snackbar to your project is easy. Simply add a Snackbar to the bottom of your activity's layout and make you activity implement SnackbarProvider to return the Snackbar. Then you may cast your activity's context as the provider and access the Snackbar anywhere within the activity. This decision was made due to static methods requiring a static reference to a view which can lead to things being overly complicated and cause memory leaks.

There are examples in the library as well.

## To Do list
- Tablet layouts (as I don't have a large enough device to test on)
- Gradle-ize project for building library locally
- Alternative methods to accessing the Snackbar, rather than casting context

##Example Usage
``` java

  getSnackbar().withMessage("Message Here")
                 .withAction("Ok", new Snackbar.ActionListener() {
                     @Override
                     public void onActionClicked() {
                         //Called when action is clicked
                     }
                 })
                 .withListener(new Snackbar.Listener(){
                     @Override
                     public void onShowMessage() {
                         //Called before Snackbar starts animating
                     }

                     @Override
                     public void onMessageShown() {
                         //Called when Snackbar is fully shown
                     }

                     @Override
                     public void onHideMessage() {
                         //Called before Snackbar starts to animate away
                     }

                     @Override
                     public void onMessageDone() {
                         //Called when Snackbar is fully gone
                     }
                 })
                 .withBackgroundColor(R.color.snackbar_background)
                 .withActionTextColor(R.color.snackbar_action_text_color)
                 .withMessageTextColor(R.color.snackbar_message_text_color)
                 .withDuration(Snackbar.Duration.LONG)
                 .show();

```

Above is an example of all of the functionality currently built into Snackbar. Most of which is straightforward.

The Snackbar.Listener class is useful for telling FloatingActionButtons to adjust up when the Snackbar is about to show, as to not block user input as per designs guidelines.

There are currently only 2 durations SHORT and LONG, more will most likely be added.
