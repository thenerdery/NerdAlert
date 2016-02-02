# Nerd Alert Workshop Branch

## Source code for the Nerd Alert app for Android as presented at DevFestMN 2016.

This branch has the majority of the Nearby API functionality commented out. In its default state this branch provides a framework for a basic app with the following functionality:

1. Prompt the user for READ_CONTACTS permission (on Android 6.0+)
2. Retrieve the user's name and photo from the default contact card.
3. Retrieve the device manufacturer and model from system properties.

We will discuss the following code sections during the workshop and uncomment them as we go along:

##### WORKSHOP 001
+ **001a**: Add Google Play Services to the Grade dependencies.
+ **001b**: Add a GoogleApiClient member variable to MainActivity.
+ **001c**: Add a MessageListener member variable to MainActivity.

##### WORKSHOP 002
+ **002a**: Create a new GoogleApiClient using the Builder pattern and request support for Nearby.MESSAGES_API.
+ **002b**: Connect the GoogleApiClient in the MainActivity.onStart() method.
+ **002c**: Disconnect the GoogleApiClient in the MainActivity.onStop() method.

##### WORKSHOP 003
+ **003a**: Specify that MainActivity implements GoogleApiClient.ConnectionCallbacks and GoogleApiClient.OnConnectionFailedListener.
+ **003b**: Override ConnectionCallbacks.onConnected() in MainActivity to publish/subscribe or unpublish/unsubscribe depending on the current app state.
+ **003c**: Override ConnectionCallbacks.onConnectionSuspended() to display a (not so?) helpful Toast message if the GoogleApiClient connection is suspended.
+ **003d**: Override OnConnectionFailedListener.onConnectionFailed() to display a Toast message if the GoogleApiClient connection is unsuccessful.

##### WORKSHOP 004
+ **004a**: Specify that MainActivity implements our NearbyInterface, which provides publish/unpublish and subscribe/unsubscribe methods to our fragments.
+ **004b**: Initialize the MessageListener by providing onFound() and onLost() callbacks.
+ **004c**: Implement our publish() method, which creates a new Nearby Message, publishes it using our defined Strategy and creates callbacks for handling success and error conditions.
+ **004d**: Implement our unpublish() method, which unpublishes our Nearby Message and creates callbacks for handling success and error conditions.
+ **004e**: Implement our subscribe() method, which subscribes our MessageListener using our defined Strategy and creates callbacks for handling success, error and expired conditions.
+ **004f**: Implement our unsubscribe() method, which unsubscribes our MessageListener and creates callbacks for handling success and error conditions.

##### WORKSHOP 005
+ **005a**: Create a helper method for processing publish/subscribe error conditions, specifically the APP_NOT_OPTED_IN status code which indicates the user hasn't granted our app the Nearby runtime permission via Google Play Services.
+ **005b**: Implement onActivityResult() which is called after the user responds to the Google Play Services permission dialog. This checks to see if the user has granted our app the Nearby runtime permission.

##### WORKSHOP 006
+ **006**: Add unpublish() and unsubscribe() to the MainActivity.onStop() method.

##### WORKSHOP 007
+ **007a**: Implement our NearbyApiUtil.MESSAGE_STRATEGY Strategy which is controls the parameters used to publish/subscribe to Nearby messages.
+ **007b**: Implement the NearbyApiUtil.newNearbyMessage() helper method which will serialize our Neighbor object into a JSON string for transmission as a Nearby Message.
+ **007c**: Implement the NearbyApiUtil.parseNearbyMessage() helper method which will deserialize our Nearby Message payload into a Neighbor object.
+ **007d**: Implement NearbyApiUtil.Wrapper, a convenience class for wrapping a Nearby Message payload with a Google Play Services instance identifier (for uniqueness).

##### WORKSHOP 008
+ **008a**: Capture our Nearby interface when MainFragment is attached to MainActivity.
+ **008b**: Unpublish/unsubscribe when the user changes the content of their name tag.
+ **008c**: Publish/subscribe or unpublish/unsubscribe when the user taps the FAB in MainFragment.

##### WORKSHOP 009
+ **009**: General discussion of our Neighbor.class model object.
    
