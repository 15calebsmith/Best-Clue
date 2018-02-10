package com.games.csmith.bestclue;

import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ArrayList<String> tabsArrayList = new ArrayList<>();
    private SparseArray<Fragment> fragmentArrayList = new SparseArray<>();
    private static final String TABS_ARRAY_LIST_KEY = "TABS_ARRAY_LIST_KEY";
    private Spinner spinner;
    
    private Game game;
    private static final String GAME_KEY = "GAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tabsArrayList.add(this.getResources().getString(R.string.app_name));

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new TabsAdapter(toolbar.getContext(), tabsArrayList));
        spinner.setOnItemSelectedListener(new OnTabSelectedListener());

        game = new Game();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onSaveInstanceState: ");
        savedInstanceState.putParcelable(GAME_KEY, game);
        savedInstanceState.putStringArrayList(TABS_ARRAY_LIST_KEY, tabsArrayList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedInstanceState);
        tabsArrayList = savedInstanceState.getStringArrayList(TABS_ARRAY_LIST_KEY);
        game = savedInstanceState.getParcelable(GAME_KEY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onAddPlayerButtonOnClick(View view) {
        Log.d(TAG, "onAddPlayerButtonOnClick: ");
        showAddPlayerDialog();
    }

    private void showAddPlayerDialog() {
        Log.d(TAG, "showAddPlayerDialog: ");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_player_dialog_title)
                .setView(R.layout.dialog_enter_player_name)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText playerNameEditText = ((AlertDialog) dialog).findViewById(R.id.player_name_edit_text);
                        if (playerNameEditText != null) {
                            String playerName = playerNameEditText.getText().toString().trim();
                            addPlayer(playerName);
                        }
                    }
                });
        builder.create().show();
    }

    private void addPlayer(String playerName) {
        if (!game.containsPlayer(playerName)) {
            Player player = new Player(playerName);
            game.addPlayer(player);
            tabsArrayList.add(playerName);
            if (game.hasEnoughPlayersToStart()) {
                enableStartGameButton();
            }
            Toast.makeText(getApplicationContext(), playerName + " is now playing.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), playerName + " is already playing!", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableStartGameButton() {
        View fragmentView = fragmentArrayList.get(0).getView();
        if (fragmentView != null) {
            Button startGameButton = fragmentView.findViewById(R.id.start_game_button);
            startGameButton.setEnabled(true);
        }
    }

    public void onStartGameButtonOnClick(View view) {
        Log.d(TAG, "onStartGameButtonOnClick: ");
        showSelectMainPlayerDialog();
    }

    private void showSelectMainPlayerDialog() {
        final ArrayList<Player> players = game.getPlayers();
        CharSequence[] playersCharSequenceArray = new CharSequence[players.size()];
        for (int i = 0; i < players.size(); i++) {
            playersCharSequenceArray[i] = players.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_main_player_dialog_title)
                .setItems(playersCharSequenceArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Player mainPlayer = players.get(which);
                        ArrayList<Player> otherPlayers = new ArrayList<>();
                        otherPlayers.addAll(players);
                        otherPlayers.remove(mainPlayer);
                        showSelectMainPlayersCardsDialog(mainPlayer, otherPlayers);
                    }
                });
        builder.create().show();
    }

    private void showSelectMainPlayersCardsDialog(final Player mainPlayer, final ArrayList<Player> otherPlayers) {
        final boolean[] checkedCards = new boolean[Card.getCardCount()];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_main_players_cards_dialog_title, mainPlayer.getName()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mainPlayer.setCards(checkedCards);
                        Player otherPlayer = otherPlayers.get(0);
                        otherPlayers.remove(otherPlayer);
                        showSetOtherPlayersNumberOfCards(otherPlayer, otherPlayers);
                    }
                })
                .setMultiChoiceItems(Card.getCards(), checkedCards, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedCards[which] = isChecked;
                    }
                });
        builder.create().show();
    }

    private void showSetOtherPlayersNumberOfCards(final Player otherPlayer, final ArrayList<Player> otherPlayers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.set_other_players_number_of_cards_dialog_title, otherPlayer.getName()))
                .setView(R.layout.dialog_enter_player_number_of_cards)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText playerNameEditText = (EditText) ((AlertDialog) dialog).findViewById(R.id.number_of_cards_edit_text);
                        if (playerNameEditText != null) {
                            String numberOfCardsText = playerNameEditText.getText().toString().trim();
                            int numberOfCards = Integer.parseInt(numberOfCardsText);
                            otherPlayer.setNumberOfCards(numberOfCards);
                            otherPlayers.remove(otherPlayer);
                            if (otherPlayers.size() > 0) {
                                Player otherPlayer = otherPlayers.get(0);
                                showSetOtherPlayersNumberOfCards(otherPlayer, otherPlayers);
                            } else {
                                startGame();
                            }
                        }
                    }
                });
        builder.create().show();
    }

    private void startGame() {
        Log.d(TAG, "startGame: ");
        View fragmentView = fragmentArrayList.get(0).getView();
        if (fragmentView != null) {
            Button addPlayerButton = fragmentView.findViewById(R.id.add_player_button);
            addPlayerButton.setVisibility(View.GONE);
            Button startGameButton = fragmentView.findViewById(R.id.start_game_button);
            startGameButton.setVisibility(View.GONE);
            Button endGameButton = fragmentView.findViewById(R.id.end_game_button);
            endGameButton.setVisibility(View.VISIBLE);
            Button newTurnButton = fragmentView.findViewById(R.id.new_turn_button);
            newTurnButton.setVisibility(View.VISIBLE);
        }
    }

    public void onEndGameButtonOnClick(View view) {
        Log.d(TAG, "onEndGameButtonOnClick: ");
    }

    public void onNewTurnButtonOnClick(View view) {
        Log.d(TAG, "onNewTurnButtonOnClick: ");
    }

    private static class TabsAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        TabsAdapter(Context context, List<String> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }

    private class OnTabSelectedListener implements  OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // When the given dropdown item is selected, show its contents in the
            // container view.
            if (position == 0) {
                MainFragment mainFragment = MainFragment.newInstance(position + 1);
                fragmentArrayList.put(position, mainFragment);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mainFragment)
                        .commit();
            }
            else {
                PlayerFragment playerFragment = PlayerFragment.newInstance(position + 1);
                fragmentArrayList.put(position, playerFragment);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, playerFragment)
                        .commit();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
