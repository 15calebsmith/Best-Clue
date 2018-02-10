package com.games.csmith.bestclue;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ArrayList<String> tabsArrayList = new ArrayList<>();
    private static final String TABS_ARRAY_LIST_KEY = "TABS_ARRAY_LIST_KEY";
    ViewPager viewPager;

    private Game game;
    private static final String GAME_KEY = "GAME_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new BestCluePagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(7);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        newGame();

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
        viewPager.getAdapter().notifyDataSetChanged();
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

    private void newGame() {
        game = new Game();
        tabsArrayList.clear();
        tabsArrayList.add(getString(R.string.main_tab_title));
        viewPager.getAdapter().notifyDataSetChanged();
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
            viewPager.getAdapter().notifyDataSetChanged();
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
        Button startGameButton = findViewById(R.id.start_game_button);
        if (startGameButton != null) {
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
        Button addPlayerButton = findViewById(R.id.add_player_button);
        addPlayerButton.setVisibility(View.GONE);
        Button startGameButton = findViewById(R.id.start_game_button);
        startGameButton.setVisibility(View.GONE);
        Button endGameButton = findViewById(R.id.end_game_button);
        endGameButton.setVisibility(View.VISIBLE);
        Button newTurnButton = findViewById(R.id.new_turn_button);
        newTurnButton.setVisibility(View.VISIBLE);
    }

    public void onEndGameButtonOnClick(View view) {
        Log.d(TAG, "onEndGameButtonOnClick: ");
        newGame();
        Button addPlayerButton = findViewById(R.id.add_player_button);
        addPlayerButton.setVisibility(View.VISIBLE);
        Button startGameButton = findViewById(R.id.start_game_button);
        startGameButton.setVisibility(View.VISIBLE);
        startGameButton.setEnabled(false);
        Button engGameButton = findViewById(R.id.end_game_button);
        engGameButton.setVisibility(View.GONE);
        Button newTurnButton = findViewById(R.id.new_turn_button);
        newTurnButton.setVisibility(View.GONE);
    }

    public void onNewTurnButtonOnClick(View view) {
        Log.d(TAG, "onNewTurnButtonOnClick: ");
    }

    private class BestCluePagerAdapter extends FragmentPagerAdapter {

        BestCluePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return MainFragment.newInstance(position + 1);
            } else {
                return PlayerFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            return tabsArrayList.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabsArrayList.get(position);
        }
  }
}
