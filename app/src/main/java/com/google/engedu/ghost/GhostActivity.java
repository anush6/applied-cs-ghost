package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private TextView wordFragment,gameStatus;
    private Button challengeButton;
    private Button resetButton;
    private static final String STATE_FRAGMENT ="currentFragment";
    private static final String STATE_STATUS ="currentStatus";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        wordFragment = (TextView) findViewById(R.id.ghostText);
        gameStatus = (TextView) findViewById(R.id.gameStatus);

        if(savedInstanceState!=null)
        {
            wordFragment.setText(savedInstanceState.getString(STATE_FRAGMENT));
            gameStatus.setText(savedInstanceState.getString(STATE_STATUS));
        }
        else {
            wordFragment.setText("");
            gameStatus.setText("Start Playing...");
        }

        try {
            InputStream is = this.getAssets().open("words.txt");
            dictionary = new FastDictionary(is);

        }
        catch (Exception e){
            Log.d("FileError","Couldn't Open file!");
        }

        challengeButton = (Button) findViewById(R.id.challengeBtn);
        challengeButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   String current = "";
                                                   if (wordFragment.getText() != "")
                                                       current = wordFragment.getText().toString();
                                                   if (current.equals("")) return;

                                                   String comp = dictionary.getAnyWordStartingWith(current);


                                                   if (dictionary.isWord(current) && current.length() >= 4) {
                                                       gameStatus.setText("You Win! Congratulations!");
                                                       challengeButton.setEnabled(false);
                                                   } else if (comp != null) {
                                                       wordFragment.setText(comp);
                                                       challengeButton.setEnabled(false);
                                                       gameStatus.setText("The Computer Wins! Restart to play again!");
                                                   } else {
                                                       challengeButton.setEnabled(false);
                                                       gameStatus.setText("You Win! Congratulations!");
                                                   }
                                               }

                                           }

        );

        resetButton = (Button) findViewById(R.id.reset_Btn);
        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                wordFragment.setText("");
                gameStatus.setText("");
               userTurn = true;
              onStart(null);
            }
        });

        onStart(null);

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(userTurn){

            challengeButton.setEnabled(false);
            int keyunicode = event.getUnicodeChar(event.getMetaState() );
            char currentchar = (char) keyunicode;
            if(Character.isLetter(currentchar)) {
                String current = wordFragment.getText().toString() + Character.toString(currentchar);
                wordFragment.setText(current);
                userTurn = false;
                gameStatus.setText(COMPUTER_TURN);
                computerTurn();
                return true;
            }

            return super.onKeyUp(keyCode, event);
        }
        return super.onKeyUp(keyCode, event);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void computerTurn() {
        String current="";

        if(wordFragment.getText() != "")
            current = wordFragment.getText().toString();
        String temp = null;
        String comp = dictionary.getAnyWordStartingWith(current);
        if(comp!=null){
            if(current.equals(""))
            {
                temp = comp.substring(0,1);
            }
            else if(comp.length()>current.length())
                temp = current + comp.substring(current.length(), current.length() + 1);
            else
                temp = current;
        }

        if(comp == null)
        {
            gameStatus.setText("The Computer Wins, as no word can be formed from these letters. Please Restart!");
            return;
        }

        if(current.length() >= GhostDictionary.MIN_WORD_LENGTH && dictionary.isWord(current)){
            gameStatus.setText("Computer Wins! Restart to play again!");
        }
        else{

            wordFragment.setText(temp);
            challengeButton.setEnabled(true);
            userTurn = true;
            gameStatus.setText(USER_TURN);
        }
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        challengeButton.setEnabled(true);
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(STATE_FRAGMENT, wordFragment.getText().toString());
        savedInstanceState.putString(STATE_STATUS, gameStatus.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        wordFragment.setText(savedInstanceState.getString(STATE_FRAGMENT));
        gameStatus.setText(savedInstanceState.getString(STATE_STATUS));
    }
}
