//Matthew Usnick
//CS211D HW4: Information Center
//InformationCenter.java

package cs211d.hw4;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@ InformationCenter @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
/** Task: This app allows the user to do one of the following: enter coordinates
*         and view them in Google maps, enter a search term(s) and search it
*         in Google, or enter a phone number and call it. Only one action can
*         be done at a time, and the GUI updates to reflect the currently 
*         selected mode. Input validation makes sure the user has entered data,
*         and if they have not, a toast message is displayed. Actions are 
*         started by either pressing the main "go" button, or by pressing 
*         "Return".
*
* @author Matthew Usnick
*/
public class InformationCenterActivity extends Activity 
{
    //GUI Elements--------------------------------------------------------------
    //Buttons-----------------------------
    Button mapBtn;
    Button searchBtn;
    Button phoneBtn;
    Button goBtn;
	
    //TextView----------------------------
    TextView textTitle;
	
    //EditText----------------------------
    EditText textField;
    EditText latText;
    EditText lonText;
	
    //Layouts-----------------------------
    LinearLayout coordinateLayout;
	
    //Class Variables-----------------------------------------------------------
    //Utility class for calling Google Activities
    Util u;
	
    //variable to set EditText max length
    int textMaxLength = 20;
    
    //variable for setting EditText filter options (max length)
    InputFilter[] filterOptions;
	
    //variables to set action mode
    boolean mapMode = false;
    boolean searchMode = false;
    boolean callMode = false;
		
    ////////////////////////////////onCreate()//////////////////////////////////
    /** Task: onCreate is called when this activity is first created.
    *         This method calls it's super version of itself, as well as setting
    *         the content view to the main layout. The GUI elements are 
    *         initialized and the Util class is loaded.
    *
    * @param b is a Bundle object
    */
    @Override //annotation
    public void onCreate(Bundle savedInstanceState) 
    {
        //required actions >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //pass Bundle to super version of this method
        super.onCreate(savedInstanceState);
        
        //Set content view to main.xml
        setContentView(R.layout.main);

        //set up GUI elements >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //Setup TextView -------------------------------------------------------
        //Load "Enter:" TextView
        textTitle = (TextView)findViewById(R.id.textView);
        
        //Set default state to Invisible
        textTitle.setVisibility(View.INVISIBLE);
       
        //Setup EditText -------------------------------------------------------
        //Load EditText 
        textField = (EditText)findViewById(R.id.editText);
        
        //Set default state to disabled
        textField.setEnabled(false);
        
        //create listener to allow "Return" key to execute an action
        textField.setOnKeyListener(createTextListener());
       
        //Setup coordinateLayout elements --------------------------------------        
        //Load layout for coordinates EditTexts (default state is hidden)
        coordinateLayout = (LinearLayout) findViewById(R.id.coordinateLayout);
        
        //Set default state to hidden
        coordinateLayout.setVisibility(View.GONE);
        
        //Load Lat and Lon EditText
        latText = (EditText)findViewById(R.id.latText);
        lonText = (EditText)findViewById(R.id.lonText);
        
        //create listeners to allow "Return" key to execute an action
        latText.setOnKeyListener(createTextListener());
        lonText.setOnKeyListener(createTextListener());
        
        //Setup Go Button ------------------------------------------------------
        //Load Go Button 
        goBtn = (Button)findViewById(R.id.goButton);
        
        //Set default state to hidden
        goBtn.setVisibility(View.GONE);
        
        //Load Menu Buttons ----------------------------------------------------
        mapBtn = (Button)findViewById(R.id.mapButton);
        searchBtn = (Button)findViewById(R.id.searchButton);
        phoneBtn= (Button)findViewById(R.id.phoneButton);
        
        //create Util class >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        u = new Util();

    }//end onCreate()
    
    //**************************createTextListener()****************************
    /** Task: this method creates and returns an anonymous class. This class is
    *         the OnKeyListener for the EditTexts. Whenever a user presses
    *         "Return" in any EditText, executeAction() will be called. 
    *        
    * @return OnKeyListener       
    */
    public OnKeyListener createTextListener()
    {
        //return the defined anonymous class
        return (new View.OnKeyListener()
        {
            //Overridden method
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent ke)
            {
                //if the user presses Return
                if((ke.getAction() == KeyEvent.ACTION_DOWN)
                && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    //execute the currently selected action/mode
                    executeAction();
                    
                    //always return true
                    return true;
                }
                
                //always return false if the Return key was not pressed
                return false;
			    
            }//end onKey()
        });//end onKeyListener()
        
    }//end createTextListener()
    
    //********************************doClick()*********************************
    /** Task: this method handles all button click events. The corresponding
    *         actions are called.
    *         
    * @param v is a View object. It is used to find the button ID that called
    *        this method.       
    */
    public void doClick(View v)
    {
        //Switch structure to find Button ID >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        switch(v.getId())
        {
            //if Map menu button is clicked ------------------------------------
            case (R.id.mapButton):
            	
                //set up the app to be in "Map" mode
                setupMapMode(); 	  
                break;
					
            //if Search menu button is clicked ---------------------------------   
            case (R.id.searchButton):

                //set up the app to be in "Search" mode
                setupSearchMode();
                break;
                
            //if phone menu button is clicked ----------------------------------         
            case (R.id.phoneButton):

                //set up the app to be in "Phone" mode
                setupPhoneMode();
                break;
                
            //if logo area is clicked ------------------------------------------		
            case (R.id.logoArea):
                
                //set up the app to be in default (initialized) state
                setupDefaultMode();
                break;    	
                    
            //if "Go" button is clicked ----------------------------------------		
            case (R.id.goButton):
            	        
                //execute the currently selected action/mode
                executeAction();
                break;
                    	
            //error: unknown id ------------------------------------------------		      
            default:
            	
                //log an error with the unknown ID to LogCat
                Log.e(getString(R.string.unknownButton), "" + v.getId());
                break;

        }//end switch
        
    }//end doClick()
   
    //*****************************executeAction()******************************
    /** Task: this method executes either the map, search, or phone actions. The
    *         user's input is retrieved and passed to the corresponding Util 
    *         action. If no input is entered when this method is called, a toast
    *         message reminds the user to enter data.              
    */
    public void executeAction()
    {
        //if in call mode ------------------------------------------------------
        if(callMode)
        {
            //get user input and call the number given
            String phoneNumber = textField.getText().toString();
        	
            //if the user did not enter a number
            if(phoneNumber.length() == 0)
            {
                //display toast message asking user to enter a number                		
                Toast msg = Toast.makeText(getApplicationContext(), 
                                           R.string.toast_Phone, 
                                           Toast.LENGTH_SHORT);
                msg.show();
            }
            //valid input
            else
            {
                //call the phone number
                u.callPhone(this, phoneNumber);   
            }
        }
        //if in map mode -------------------------------------------------------
        else if (mapMode)
        {         
            //get user input for lat and lon
            String latInput = latText.getText().toString();
            String lonInput = lonText.getText().toString();
        	
            //if the user did not enter a lat or a lon
            if(latInput.length() == 0 || lonInput.length() == 0)
            {
                //display toast message asking user to enter lat and lon
                Toast msg = Toast.makeText(getApplicationContext(), 
                                           R.string.toast_Map, 
                                           Toast.LENGTH_SHORT);
                msg.show();
            }
            //valid input
            else
            {
                //parse input strings to doubles
                Double lat = Double.parseDouble(latInput);
                Double lon = Double.parseDouble(lonInput);
                
                //view the coordinates in Google Maps
                u.viewMap(this,lat, lon);
            }
        }
        //if in search mode ----------------------------------------------------
        else if(searchMode)
        {   
            //get user input
            String searchTerm = textField.getText().toString();
        	
            //if the user did not enter a search term
            if(searchTerm.length() == 0)
            {
                //display toast message asking user to enter search term
                Toast msg = Toast.makeText(getApplicationContext(), 
                                           R.string.toast_Search, 
                                           Toast.LENGTH_SHORT);
                msg.show();
            }
            //user entered search term
            else
            {
                //Google the search term in a web browser
                u.searchWeb(this, searchTerm);
            }
        }
        //error ----------------------------------------------------------------
        else 
        {
            //error
            Log.e(getString(R.string.unknownMode), "unknown mode.");
        }
    }//end executeAction();
    
    //***************************setupDefaultMode()*****************************
    /** Task: This method puts the app in "default" mode: The EditTexts are 
    *        disabled and a message is displayed, telling the user to choose
    *        a function. In this mode, the user can only select a mode to 
    *        enter. The menu items are updated to indicate that no mode is 
    *        selected.
    */
    public void setupDefaultMode()
    {
        //make "Enter:" text invisible -----------------------------------------
        textTitle.setVisibility(View.INVISIBLE);

        //make cooridinateLayout hidden ----------------------------------------
        coordinateLayout.setVisibility(View.GONE);
    	
        //update EditText ------------------------------------------------------
        //make EditText visible
        textField.setVisibility(View.VISIBLE);
                
        //make EditText disabled
        textField.setEnabled(false);
    
        //clear any text from EditText
        textField.setText(R.string.empty);
        
        //set EditText hint to "choose a function"
        textField.setHint(R.string.edittext_default);

        //update Go button -----------------------------------------------------
        //make button invisible
        goBtn.setVisibility(View.INVISIBLE);
           		
        //update menu buttons text colors --------------------------------------
        //Set all text to white.
        mapBtn.setTextColor(getResources().getColor(R.color.white));
        searchBtn.setTextColor(getResources().getColor(R.color.white));
        phoneBtn.setTextColor(getResources().getColor(R.color.white));
             	 
        //update modes ---------------------------------------------------------
        //enable phone mode and disable other modes    	
        mapMode = false;
  	    searchMode = false;
        callMode = false;
        
    }//end setupDefaultMode
    
    //*****************************setupMapMode()*******************************
    /** Task: this method puts the app in "Map" mode. The GUI is updated to 
    *         display 2 EditTexts (one for lat, one for lon) and the goBtn.
    *         The goBtn text is set to "View". The menu buttons are updated to 
    *         indicate that the app is in Map mode. Any inputed data is removed
    *         from the EditText fields, and the EditTexts input length are set 
    *         to 20. The EditTexts are updated to display input hints for 
    *         Lat/Lon.
    */
    public void setupMapMode()
    {
        //make "Enter:" text visible -------------------------------------------
        textTitle.setVisibility(View.VISIBLE);
    
        //make EditText hidden -------------------------------------------------
        textField.setVisibility(View.GONE);
		
        //update EditTexts -----------------------------------------------------
        //set EditText max length to 20
        textMaxLength = 20;  
        filterOptions = new InputFilter[1];  
        filterOptions[0] = new InputFilter.LengthFilter(textMaxLength);  
        latText.setFilters(filterOptions); 
        lonText.setFilters(filterOptions); 
		
        //update coordinateLayout ----------------------------------------------
        //make coordinateLayout visible
        coordinateLayout.setVisibility(View.VISIBLE);

        //clear any data from lat and lon EditText boxes
        latText.setText(R.string.empty);
        lonText.setText(R.string.empty);
    	
        //update Go button -----------------------------------------------------
        //set text to View
        goBtn.setText(R.string.goButton_View);
        
        //make button visible
        goBtn.setVisibility(View.VISIBLE);

        //update menu buttons text colors --------------------------------------
        //Set Map to be highlighted in blue and set others to white.
        mapBtn.setTextColor(getResources().getColor(R.color.androidBlue));
        searchBtn.setTextColor(getResources().getColor(R.color.white));
        phoneBtn.setTextColor(getResources().getColor(R.color.white));
        
        //update modes ---------------------------------------------------------
        //enable map mode and disable other modes
        mapMode = true;
        searchMode = false;
        callMode = false;
        
    }//end setupMapMode()
    
    //***************************setupPhoneMode()*******************************
    /** Task: this method puts the app in "Phone" mode. The GUI is updated to 
    *         display 1 EditText (which only accepts phone input) and the goBtn.
    *         The goBtn text is set to "Dial". The menu buttons are updated to 
    *         indicate that the app is in Phone mode. Any inputed data is 
    *         removed from the EditText field, and the EditText input length is
    *         set to 20. The EditText is updated to display an input hint for a 
    *         phone number entry. 
    */
    public void setupPhoneMode()
    {
        //make "Enter:" text visible -------------------------------------------
        textTitle.setVisibility(View.VISIBLE);
    
        //make coordinateLayout hidden -----------------------------------------
        coordinateLayout.setVisibility(View.GONE);
    
        //update EditText ------------------------------------------------------
    	
        //make EditText visible
        textField.setVisibility(View.VISIBLE);
    	
        //make EditText enabled
        textField.setEnabled(true);

        //make EditText accept phone number input    	
        textField.setInputType(InputType.TYPE_CLASS_PHONE);

        //clear any text from EditText
        textField.setText(R.string.empty);

        //set EditText hint to "(xxx) xxx-xxxx"
        textField.setHint(R.string.hint_phone);

        //set EditText max length to 20
        textMaxLength = 20;  
        filterOptions = new InputFilter[1];  
        filterOptions[0] = new InputFilter.LengthFilter(textMaxLength);  
    	textField.setFilters(filterOptions); 
    	
        //update Go button -----------------------------------------------------
        //set text to Dial
    	goBtn.setText(R.string.goButton_Dial);
    	
        //make button visible
    	goBtn.setVisibility(View.VISIBLE);
    	
        //update menu buttons text colors --------------------------------------
        //Set phone to be highlighted in blue and set others to white.
     	mapBtn.setTextColor(getResources().getColor(R.color.white));
        searchBtn.setTextColor(getResources().getColor(R.color.white));
        phoneBtn.setTextColor(getResources().getColor(R.color.androidBlue));

        //update modes ---------------------------------------------------------
        //enable phone mode and disable other modes
  	    mapMode = false;	
        searchMode = false;
        callMode = true;
        
    }//end setupPhoneMode()
    
    //**************************setupSearchMode()*******************************
    /** Task: this method puts the app in "Search" mode. The GUI is updated to 
    *         display 1 EditText (which accepts text input) and the goBtn.
    *         The goBtn text is set to "Search". The menu buttons are updated to 
    *         indicate that the app is in Search mode. Any inputed data is 
    *         removed from the EditText field, and the EditText input length is 
    *         set to 40. The EditText is updated to display an input hint for a 
    *         search term.
    */
    public void setupSearchMode()
    {
    	//make "Enter:" text visible -------------------------------------------
        textTitle.setVisibility(View.VISIBLE);
    
    	//make coordinateLayout hidden
    	coordinateLayout.setVisibility(View.GONE);
    
        //update EditText ------------------------------------------------------
    	
    	//make EditText visible
    	textField.setVisibility(View.VISIBLE);
        
    	//make EditText enabled
	    textField.setEnabled(true);

	    //make EditText accept text input    	
    	textField.setInputType(InputType.TYPE_CLASS_TEXT);

    	//clear any text from EditText
     	textField.setText(R.string.empty);

    	//set EditText hint to "search term(s)"
     	textField.setHint(R.string.hint_search);

        //set EditText max length to 40
        textMaxLength = 40;  
        filterOptions = new InputFilter[1];  
        filterOptions[0] = new InputFilter.LengthFilter(textMaxLength);  
        textField.setFilters(filterOptions); 
    	
        //update Go button -----------------------------------------------------
        //set text to Search
        goBtn.setText(R.string.goButton_Search);

        //make button visible
        goBtn.setVisibility(View.VISIBLE);

        //update menu buttons text colors --------------------------------------
        //Set Search to be highlighted in blue and set others to white.
        mapBtn.setTextColor(getResources().getColor(R.color.white));
        searchBtn.setTextColor(getResources().getColor(R.color.androidBlue));
        phoneBtn.setTextColor(getResources().getColor(R.color.white));
               	  
        //update modes ---------------------------------------------------------
        //enable search mode and disable other modes
        searchMode = true;
  	    mapMode = false;
  	    callMode = false;     
               	  
    }//end setupSearchMode()

}//end InformationCenter.java