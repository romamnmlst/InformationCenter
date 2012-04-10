//Matthew Usnick
//CS211D HW4: Information Center
//Util.java

package cs211d.hw4;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Util @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
/** Task: This class provides methods to view coordinates on a map, to search
*        a term(s) in Google (in a web browser), and to call a phone number.
*
* @author Matthew Usnick
*/
public class Util 
{
    //*****************************callPhone()**********************************
    /** Task: This method calls a given phone number.
    *        
    * @param a is the Activity that called this method
    * @param phoneNumber is the number to call
    */
    public void callPhone(Activity a, String phoneNumber)
    {
        //Start the CALL activity with the given phone number
        a.startActivity(new Intent(Intent.ACTION_CALL, 
                                   Uri.parse("tel:" + phoneNumber)));
        
    }//end callPhone()
    
    //*****************************searchWeb()**********************************
    /** Task: This method opens Google, in a web browser, and initiates a search
    *         for the given term(s).
    *        
    * @param a is the Activity that called this method
    * @param searchTerm is the terms that user have inputed to search   
    */
    public void searchWeb(Activity a, String searchTerm)
    {
        //Start the WEB_SEARCH activity with the given search term(s)
        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
        search.putExtra(SearchManager.QUERY, searchTerm);
        a.startActivity(search);
        
    }//end searchWeb()
    
    //*****************************viewMap()************************************
    /** Task: This method opens Google Maps and displays a location, defined
    *         by the users input coordinates.    
    *         
    * @param a is the activity that called this method
    * @param lat is the Latitude coordinate
    * @param lon is the Longitude coordinate           
    */
    public void viewMap(Activity a, Double lat, Double lon)
    {
        //Start the VIEW activiy (map) with the given coordinates
        a.startActivity(new Intent(Intent.ACTION_VIEW, 
                                   Uri.parse("geo:" + lat + "," + lon)));
        
    }//end viewMap()
    
}//end Util.java
